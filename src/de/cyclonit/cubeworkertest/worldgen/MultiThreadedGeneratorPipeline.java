package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.concurrency.ColumnTaskManager;
import de.cyclonit.cubeworkertest.worldgen.concurrency.GeneratorTask;
import de.cyclonit.cubeworkertest.worldgen.concurrency.GeneratorWorker;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependencyManager;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedGeneratorPipeline extends AbstractGeneratorPipeline {

	private static final long DEFAULT_BATCH_DURATION = 40;

	private static final int DEFAULT_WORKER_COUNT = 2;


	private final ColumnTaskManager<GeneratorTask> taskManager;

	private int workerCount;

	private final List<GeneratorWorker> workers;

	private long batchDuration;


	public MultiThreadedGeneratorPipeline(GeneratorStageRegistry generatorStageRegistry) {
		super(generatorStageRegistry);
		this.taskManager = new ColumnTaskManager<>();
		this.taskManager.pause();
		this.workerCount = DEFAULT_WORKER_COUNT;
		this.workers = new ArrayList<>();
		this.batchDuration = DEFAULT_BATCH_DURATION;
	}


	// ----------------------------------------------- Worker Management -----------------------------------------------

	/**
	 * Sets the number of workers used by this MultiThreadedGeneratorPipeline. The pipeline will add or remove workers
	 * as needed to meet the given value.
	 * If workers are to be removed, their threads will remain alive as long as the pipeline's ColumnTaskManager is
	 * paused. They will not process any additional cubes.
	 *
	 * @param workerCount The number of workers to be used by this MultiThreadedGeneratorPipeline.
	 */
	public void setWorkerCount(int workerCount) {

		this.workerCount = workerCount;

		// If this pipeline has not yet been initialize, postpone adding the workers. See: initialize(...)
		if (this.cubeCache == null) {
			return;
		}

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
				// TODO: Clean up
				GeneratorWorker worker = new GeneratorWorker(this, this.cubeCache, this.taskManager, this.generatorStageRegistry);
				Thread workerThread = new Thread(worker);
				workerThread.start();

				this.workers.add(worker);
			}
		}
	}

	/**
	 * Shuts down the MultiThreadedGeneratorPipeline and terminates all threads involved in an orderly manner.
	 * After this call has been made, no further asynchronous processing will occur during calls of #tick(long) or
	 * processAll().
	 * @see #tick(long)
	 * @see #processAll()
	 */
	public void shutdown() {
		this.taskManager.shutdown();
	}


	// ----------------------------------------- Interface: IGeneratorPipeline -----------------------------------------

	@Override
	public void initialize(ICubeCache cubeCache, DependencyManager dependencyManager) {
		super.initialize(cubeCache, dependencyManager);
		this.setWorkerCount(this.workerCount);
	}

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

		// Queue the required task.
		GeneratorTask task = new GeneratorTask(cube.getCoords(), targetStage);
		this.taskManager.add(task);
	}

	@Override
	public void resumeCube(Cube cube) {

		// If the cube has reached the target stage, don't do anything.
		if (cube.hasReachedTargetStage()) {
			return;
		}

		// Queue the required task.
		GeneratorTask task = new GeneratorTask(cube.getCoords(), cube.getTargetStage());
		this.taskManager.add(task);
	}

	@Override
	public void initializeColumn(Column column) {
		// TODO: Implement.
	}

	/**
	 * @see #shutdown()
	 */
	@Override
	public void tick(long duration) {
		this.taskManager.unpause();

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			// TODO
		}

		this.taskManager.pause();
	}

	/**
	 * @see #shutdown()
	 */
	@Override
	public void processAll() {

		// Unpause the task manager such that all workers can resume processing.
		this.taskManager.unpause();

		// Wait until the queue is empty and stop all workers afterwards.
		this.taskManager.waitUntilPolling();
		this.taskManager.pause();

	}

	@Override
	public void reportTotal() {
		for (GeneratorWorker worker : this.workers) {
			worker.getReport().reportTotal();
		}
	}

	@Override
	public void reportRecent() {
		for (GeneratorWorker worker : this.workers) {
			worker.getReport().reportRecent();
		}
	}

	@Override
	public void resetRecentReport() {
		for (GeneratorWorker worker : this.workers) {
			worker.getReport().resetRecentReport();
		}
	}

}
