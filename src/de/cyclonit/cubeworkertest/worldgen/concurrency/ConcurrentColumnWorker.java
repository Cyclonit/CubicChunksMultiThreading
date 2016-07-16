package de.cyclonit.cubeworkertest.worldgen.concurrency;

import de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment.ColumnLockManager;
import de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment.ColumnWorker;

public abstract class ConcurrentColumnWorker extends ColumnWorker implements Runnable {

	private final Object shutdownMutex;

	private volatile boolean shouldShutdown;

	protected volatile boolean isShutdown;


	public ConcurrentColumnWorker(ColumnLockManager lockManager) {
		super(lockManager);
		this.shutdownMutex = new Object();
		this.shouldShutdown = false;
		this.isShutdown = false;
	}


	// ---------------------------------------------- Interface: Runnable ----------------------------------------------

	@Override
	public void run() {

		while (!this.shouldShutdown()) {
			this.tick();
		}

		this.isShutdown = true;
	}


	// -------------------------------------------------- Abstraction --------------------------------------------------

	public abstract void tick();

	public abstract void onShutdown();


	// ------------------------------------------------- Shutting down -------------------------------------------------

	public void shutdown() {
		synchronized (this.shutdownMutex) {
			this.shouldShutdown = true;
		}
	}

	public boolean shouldShutdown() {
		synchronized (this.shutdownMutex) {
			return this.shouldShutdown;
		}
	}

	public boolean isShutdown() {
		synchronized (this.shutdownMutex) {
			return this.isShutdown;
		}
	}
}
