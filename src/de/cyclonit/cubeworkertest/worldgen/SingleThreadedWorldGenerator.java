package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.IPartialCubeProvider;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SingleThreadedWorldGenerator implements ICubeGenerator {

	private ICubeCache cubeCache;

	private final Queue<CubeCoords> queue;

	private final Set<CubeCoords> queuedCubes;

	private final GeneratorStageRegistry generatorStageRegistry;


	public SingleThreadedWorldGenerator(GeneratorStageRegistry generatorStageRegistry) {
		this.queue = new LinkedList<>();
		this.queuedCubes = new HashSet<>();
		this.generatorStageRegistry = generatorStageRegistry;
	}


	// ------------------------------------------- Interface: ICubeGenerator -------------------------------------------

	@Override
	public void initialize(IPartialCubeProvider cubeProvider) {
		this.cubeCache = cubeProvider.getCubeCache();
	}

	@Override
	public void generateCube(Cube cube) {
		this.generateCube(cube, this.generatorStageRegistry.getLastStage());
	}

	@Override
	public void generateCube(Cube cube, GeneratorStage targetStage) {

		// Ensure a current stage is set.
		if (cube.getCurrentStage() == null) {
			cube.setCurrentStage(this.generatorStageRegistry.getFirstStage());
		}

		// Ensure the proper target stage is set.
		if (cube.getTargetStage() == null || cube.getTargetStage().precedes(targetStage)) {
			cube.setTargetStage(targetStage);
		}

		// Queue the cube for processing in tick().
		this.queueCube(cube);
	}

	@Override
	public void generateColumn(Column column) {
		// TODO: Implement.
	}

	@Override
	public void tick() {
		// TODO: Implement.
	}

	@Override
	public void processAll() {

		while (!this.queue.isEmpty()) {

			// Get the next cube. If it does not exist, continue.
			Cube cube = this.cubeCache.getCube(this.queue.poll());
			if (cube == null) {
				continue;
			}

			// Process the cube.
			processCube(cube);

			// Advance its stage and requeue if necessary.
			advanceStage(cube);
			if (!cube.hasReachedTargetStage()) {
				this.queueCube(cube);
			}
		}
	}


	// ---------------------------------------------------- Helpers ----------------------------------------------------

	private void queueCube(Cube cube) {
		CubeCoords coords = cube.getCoords();
		if (!this.queuedCubes.contains(coords)) {
			this.queuedCubes.add(coords);
			this.queue.add(coords);
		}
	}

	private static void processCube(Cube cube) {
		try {
			Thread.sleep((long) (Math.random() * 10L));
		} catch (InterruptedException e) {
		}
	}

	private static void advanceStage(Cube cube) {
		cube.setCurrentStage(cube.getCurrentStage().getNextStage());
	}

}
