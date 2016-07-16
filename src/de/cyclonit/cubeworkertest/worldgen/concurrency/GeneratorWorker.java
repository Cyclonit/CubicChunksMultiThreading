package de.cyclonit.cubeworkertest.worldgen.concurrency;

import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.GeneratorReport;
import de.cyclonit.cubeworkertest.worldgen.IGeneratorPipeline;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

public class GeneratorWorker extends ConcurrentColumnWorker {

	private final IGeneratorPipeline generatorPipeline;

	private final ICubeCache cubeCache;

	private final ColumnTaskManager<GeneratorTask> taskManager;

	private GeneratorReport report;


	public GeneratorWorker(IGeneratorPipeline generatorPipeline, ICubeCache cubeCache, ColumnTaskManager<GeneratorTask> taskManager, GeneratorStageRegistry generatorStageRegistry) {
		super(taskManager.getColumnLockManager());
		this.generatorPipeline = generatorPipeline;
		this.cubeCache = cubeCache;
		this.taskManager = taskManager;
		this.taskManager.register(this);

		this.report = new GeneratorReport(generatorStageRegistry);
	}


	// -------------------------------------- Superclass: ConcurrentColumnWorker ---------------------------------------

	@Override
	public void tick() {

		GeneratorTask task = this.taskManager.poll(this);

		if (task != null) {
			this.process(task);
		} else {
			this.report.addSkipped();
		}
	}

	@Override
	public void onShutdown() {
		this.taskManager.unregister(this);
	}


	// ---------------------------------------------------- Helper -----------------------------------------------------

	private boolean process(GeneratorTask task) {

		// Get the cube or return if it does not exist.
		Cube cube = this.cubeCache.getCube(task.coords);

		// If the cube does not exist or has reached its target stage, return.
		if (cube == null || cube.hasReachedStage(task.targetStage)) {
			return false;
		}

		// Process the cube.
		GeneratorStage previousStage = cube.getCurrentStage();
		processCube(cube);

		// Advance the cube's stage and requeue if necessary.
		advanceStage(cube);
		if (!cube.hasReachedStage(task.targetStage)) {
			this.generatorPipeline.generateCube(cube);
		}
		this.clearAssignment();

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

	public GeneratorReport getReport() {
		return this.report;
	}

}
