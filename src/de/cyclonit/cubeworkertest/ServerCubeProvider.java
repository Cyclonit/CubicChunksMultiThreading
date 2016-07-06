package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.ColumnCoords;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.CubeCache;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.ICubeGenerator;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public class ServerCubeProvider implements IPartialCubeProvider {

	private final ICubeCache cubeCache;

	private final ICubeGenerator cubeGenerator;


	public ServerCubeProvider(ICubeGenerator cubeGenerator) {
		this.cubeCache = new CubeCache();
		this.cubeGenerator = cubeGenerator;
		this.cubeGenerator.initialize(this);
	}


	// -------------------------------------------- Interface: ICubeProvider -------------------------------------------

	@Override
	public ICubeCache getCubeCache() {
		return this.cubeCache;
	}

	@Override
	public Cube getCube(CubeCoords coords) {
		Cube cube = this.cubeCache.getCube(coords);
		if (cube != null && cube.isLive()) {
			return cube;
		} else {
			return null;
		}
	}

	@Override
	public Column getColumn(ColumnCoords coords) {
		return this.cubeCache.getColumn(coords);
	}

	@Override
	public Cube provideCube(CubeCoords coords) {

		// Get the cube's column.
		ColumnCoords columnCoords = ColumnCoords.fromCubeCoords(coords);
		Column column = this.cubeCache.getColumn(columnCoords);

		// If the column does not yet exist, create it.
		if (column == null) {
			column = this.createColumn(columnCoords);
		}

		// Get the cube from the column.
		Cube cube = column.getCube(coords.getCubeY());

		// If the cube does not yet exist, create it.
		if (cube == null) {
			cube = this.createCube(coords);
		}

		// If the cube is live, return it.
		 else if (cube.isLive()) {
			return cube;
		}

		// Since the cube was requested and has not yet reached the last stage, generate it.
		this.cubeGenerator.generateCube(cube);

		// This method must not return cubes that have not reached the final stage, thus return null.
		return null;
	}

	@Override
	public Column provideColumn(ColumnCoords coords) {

		// Get the column from the cache.
		Column column = this.cubeCache.getColumn(coords);

		// If the column does not yet exist, create it.
		if (column == null) {
			column = this.createColumn(coords);
		}

		return column;
	}


	// ---------------------------------------- Interface: IPartialCubeProvider ----------------------------------------

	@Override
	public Cube getCube(CubeCoords coords, GeneratorStage generatorStage) {

		// Get the cube's column.
		ColumnCoords columnCoords = ColumnCoords.fromCubeCoords(coords);
		Column column = this.cubeCache.getColumn(columnCoords);

		// If the column does not yet exist, create it.
		if (column == null) {
			column = this.createColumn(columnCoords);
		}

		// Get the cube from the column.
		Cube cube = column.getCube(coords.getCubeY());

		// If the cube does not yet exist, create it.
		if (cube == null) {
			cube = this.createCube(coords);
		}

		// If the cube has reached the requested stage, return it, otherwise return null.
		if (cube.hasReachedStage(generatorStage)) {
			return cube;
		} else {
			return null;
		}
	}

	@Override
	public Cube provideCube(CubeCoords coords, GeneratorStage generatorStage) {

		// Get the cube's column.
		ColumnCoords columnCoords = ColumnCoords.fromCubeCoords(coords);
		Column column = this.cubeCache.getColumn(columnCoords);

		// If the column does not yet exist, create it.
		if (column == null) {
			column = this.createColumn(columnCoords);
		}

		// Get the cube from the column.
		Cube cube = column.getCube(coords.getCubeY());

		// If the cube does not yet exist, create it.
		if (cube == null) {
			cube = this.createCube(coords);
		}

		// If the cube has reached the requested stage, return it.
		if (cube.hasReachedStage(generatorStage)) {
			return cube;
		}

		// Generate the cube up to the requested stage.
		this.cubeGenerator.generateCube(cube, generatorStage);

		// Because the cube has not yet reached the requested stage, return null.
		return null;
	}


	// ---------------------------------------------------- Helpers ----------------------------------------------------

	private Column createColumn(ColumnCoords coords) {
		Column column = new Column(coords);
		this.cubeCache.addColumn(column);
		return column;
	}

	private Cube createCube(CubeCoords coords) {
		Cube cube = new Cube(coords);
		this.cubeCache.addCube(cube);
		return cube;
	}

}