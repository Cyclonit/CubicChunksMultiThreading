package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.IPartialCubeProvider;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.concurrency.ColumnTaskManager;
import de.cyclonit.cubeworkertest.worldgen.concurrency.GeneratorTask;
import de.cyclonit.cubeworkertest.worldgen.concurrency.GeneratorWorker;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedWorldGenerator implements ICubeGenerator {

	private static final long DEFAULT_BATCH_DURATION = 40;

	private static final int DEFAULT_WORKER_COUNT = 2;


	private final GeneratorStageRegistry generatorStageRegistry;

	private final ColumnTaskManager<GeneratorTask> taskManager;

	private final List<GeneratorWorker> workers;

	private ICubeCache cubeCache;

	private long batchDuration;


	public MultiThreadedWorldGenerator(GeneratorStageRegistry generatorStageRegistry) {
		this.generatorStageRegistry = generatorStageRegistry;
		this.taskManager = new ColumnTaskManager<>();
		this.taskManager.pause();
		this.workers = new ArrayList<>();
		this.batchDuration = DEFAULT_BATCH_DURATION;
	}


	// ----------------------------------------------- Worker Management -----------------------------------------------

	/**
	 * Sets the number of workers used by this MultiThreadedWorldGenerator. The generator will add or remove workers as
	 * needed to meet the given value.
	 * If workers are to be removed, they will remain alive as long as the generator's ColumnTaskManager is paused. They
	 * will not process any additional cubes.
	 *
	 * @param workerCount The number of workers to be used by this MultiThreadedWorldGenerator.
	 */
	public void setWorkerCount(int workerCount) {

		// If the new worker count is smaller, remove workers.
		if (workerCount < this.workers.size()) {

			// Remove the required number of workers.
			int diff = this.workers.size() - workerCount;
			for (int i = this.workers.size() - 1; i >= diff; --i) {
				GeneratorWorker worker = this.workers.remove(i);
				// The worker will unregister itself.
				worker.shutdown();
			}
		}

		// If the new worker count is larger, add workers.
		else if (workerCount > this.workers.size()) {

			// Add the required number of workers.
			int diff = workerCount - this.workers.size();
			for (int i = 0; i < diff; ++i) {
				GeneratorWorker worker = new GeneratorWorker(this.cubeCache, this.taskManager, this.generatorStageRegistry);
				Thread workerThread = new Thread(worker);
				workerThread.start();

				this.workers.add(worker);
			}
		}
	}


	// ------------------------------------------- Interface: ICubeGenerator -------------------------------------------

	@Override
	public void initialize(IPartialCubeProvider cubeProvider) {
		this.cubeCache = cubeProvider.getCubeCache();
		this.setWorkerCount(DEFAULT_WORKER_COUNT);
	}

	@Override
	public void generateCube(Cube cube) {
		this.generateCube(cube, this.generatorStageRegistry.getLastStage());
	}

	@Override
	public void generateCube(Cube cube, GeneratorStage targetStage) {

		// Ensure the current and target stage are set.
		if (cube.getCurrentStage() == null) {
			cube.setCurrentStage(this.generatorStageRegistry.getFirstStage());
			cube.setTargetStage(targetStage);
		}

		// Queue the required task.
		GeneratorTask task = new GeneratorTask(cube.getCoords(), targetStage);
		this.taskManager.add(task);
	}

	@Override
	public void generateColumn(Column column) {
		// TODO: Implement.
	}

	@Override
	public void tick() {
		this.taskManager.unpause();

		try {
			Thread.sleep(this.batchDuration);
		} catch (InterruptedException e) {
			// TODO
		}

		this.taskManager.pause();
	}

	@Override
	public void processAll() {

		this.taskManager.unpause();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO
		}

		// Wait until the queue is empty and stop all workers afterwards.
		this.taskManager.waitUntilPolling();
		this.taskManager.pause();

	}


	// ---------------------------------------------------- Helper -----------------------------------------------------

	public void report() {
		for (GeneratorWorker worker : this.workers) {
			worker.getReport().reportTotal();
		}
	}

}
