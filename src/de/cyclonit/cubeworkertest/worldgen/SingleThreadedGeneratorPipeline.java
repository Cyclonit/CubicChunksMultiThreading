package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependentCube;
import de.cyclonit.cubeworkertest.worldgen.dependency.ICubeDependency;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class SingleThreadedGeneratorPipeline extends AbstractGeneratorPipeline {

	private static final int DEFAULT_BATCH_SIZE = 50;

	private static final long DEFAULT_BATCH_DURATION = 40;


	private final Queue<CubeCoords> queue;

	private final Set<CubeCoords> queuedCubes;


	private int batchSize;

	private long batchDuration;

	private final GeneratorReport report;


	public SingleThreadedGeneratorPipeline(GeneratorStageRegistry generatorStageRegistry) {
		super(generatorStageRegistry);
		this.queue = new LinkedList<>();
		this.queuedCubes = new HashSet<>();
		this.batchSize = DEFAULT_BATCH_SIZE;
		this.batchDuration = DEFAULT_BATCH_DURATION;

		this.report = new GeneratorReport(generatorStageRegistry);
	}


	// ------------------------------------------- Interface: IGeneratorPipeline -------------------------------------------

	@Override
	public void generateCube(Cube cube, GeneratorStage targetStage) {

		// Ensure the proper target stage is set.
		if (cube.getTargetStage().precedes(targetStage)) {
			cube.setTargetStage(targetStage);
		}

		// If the cube has reached the target stage, don't do anything.
		if (cube.hasReachedTargetStage()) {
			return;
		}

		// If the cube's current stage has requirements, hand it to the DependencyManager.
		ICubeDependency cubeDependency = cube.getCurrentStage().getCubeDependency(cube);
		if (cubeDependency != null) {
			DependentCube dependentCube = new DependentCube(cube, cubeDependency, this);
			this.dependentCubeManager.registerDependentCube(dependentCube);
		}

		// Otherwise, queue the cube for processing in tick().
		else {
			this.queueCube(cube);
		}
	}

	@Override
	public void resumeCube(Cube cube) {

		// If the cube has reached the target stage, don't do anything.
		if (cube.hasReachedTargetStage()) {
			return;
		}

		// Queue the cube for processing in tick().
		this.queueCube(cube);
	}

	@Override
	public void tick(long duration) {

		report.startTimer();

		long timeEnd = System.currentTimeMillis() + duration;
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
			this.processNext();
		}

		report.stopTimer();
	}

	@Override
	public void reportTotal() {
		this.report.reportTotal();
	}

	@Override
	public void reportRecent() {
		this.report.reportRecent();
	}

	@Override
	public void resetRecentReport() {
		this.report.resetRecentReport();
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

	private static void advanceStage(Cube cube) {
		cube.setCurrentStage(cube.getCurrentStage().getNextStage());
	}

}
