package de.cyclonit.cubeworkertest.world;

import de.cyclonit.cubeworkertest.util.ColumnCoords;
import de.cyclonit.cubeworkertest.util.CubeCoords;

import java.util.HashMap;
import java.util.Map;

public class CubeCache implements ICubeCache {

	private final Map<ColumnCoords, Column> columns;


	public CubeCache() {
		this.columns = new HashMap<>();
	}


	// --------------------------------------------- Interface: ICubeCache ---------------------------------------------

	@Override
	public void addCube(Cube cube) {
		Column column = this.columns.get(ColumnCoords.fromCubeCoords(cube.getCoords()));
		if (column != null) {
			column.addCube(cube);
		} else {
			throw new Error("TODO");
		}
	}

	@Override
	public Cube getCube(CubeCoords coords) {
		Column column = this.columns.get(ColumnCoords.fromCubeCoords(coords));
		if (column != null) {
			return column.getCube(coords.getCubeY());
		} else {
			return null;
		}
	}

	@Override
	public void addColumn(Column column) {
		if (this.columns.put(column.getCoords(), column) != null) {
			throw new Error("TODO");
		}
	}

	@Override
	public Column getColumn(ColumnCoords coords) {
		return this.columns.get(coords);
	}
}
