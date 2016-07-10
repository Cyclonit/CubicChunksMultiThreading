package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.IPartialCubeProvider;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SingleThreadedWorldGenerator implements ICubeGenerator {

	private static final int DEFAULT_BATCH_SIZE = 50;

	private static final long DEFAULT_BATCH_DURATION = 40;


	private ICubeCache cubeCache;

	private final Queue<CubeCoords> queue;

	private final Set<CubeCoords> queuedCubes;

	private final GeneratorStageRegistry generatorStageRegistry;


	private int batchSize;

	private long batchDuration;

	private final GeneratorReport report;


	public SingleThreadedWorldGenerator(GeneratorStageRegistry generatorStageRegistry) {
		this.queue = new LinkedList<>();
		this.queuedCubes = new HashSet<>();
		this.generatorStageRegistry = generatorStageRegistry;
		this.batchSize = DEFAULT_BATCH_SIZE;
		this.batchDuration = DEFAULT_BATCH_DURATION;

		this.report = new GeneratorReport(generatorStageRegistry);
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

		report.startTimer();

		long timeEnd = System.currentTimeMillis() + this.batchDuration;
		int processed = 0;

		while (processed < this.batchSize && System.currentTimeMillis() < timeEnd) {
			if (processNext()) {
				++processed;
			}
		}

		report.stopTimer();
	}

	@Override
	public void processAll() {

		report.startTimer();

		while (!this.queue.isEmpty()) {
			processNext();
		}

		report.stopTimer();
	}


	// ---------------------------------------------------- Helpers ----------------------------------------------------

	private void queueCube(Cube cube) {
		CubeCoords coords = cube.getCoords();
		if (!this.queuedCubes.contains(coords)) {
			this.queuedCubes.add(coords);
			this.queue.add(coords);
		}
	}

	private boolean processNext() {

		// Get the next cube.
		CubeCoords coords = this.queue.poll();
		this.queuedCubes.remove(coords);
		Cube cube = this.cubeCache.getCube(coords);

		// If it does not exist, continue.
		if (cube == null) {
			this.report.addSkipped();
			return false;
		}

		// Process the cube.
		GeneratorStage previousStage = cube.getCurrentStage();
		processCube(cube);

		// Advance its stage and requeue if necessary.
		advanceStage(cube);
		if (!cube.hasReachedTargetStage()) {
			this.queueCube(cube);
		}

		this.report.addProcessed(previousStage);
		return true;
	}

	private static void processCube(Cube cube) {
		try {
			Thread.sleep((long) (Math.random() * 5L));
		} catch (InterruptedException e) {
		}
	}

	public GeneratorReport getReport() {
		return this.report;
	}

	private static void advanceStage(Cube cube) {
		cube.setCurrentStage(cube.getCurrentStage().getNextStage());
	}

}
