package de.cyclonit.cubeworkertest.worldgen.concurrency;

public abstract class ColumnWorker {

	private final int DEFAULT_LOCK_RADIUS = 2;

	private final ColumnLockManager lockManager;

	private ColumnTask assignment;

	// Shutting down

	private final Object shutdownMutex;

	private volatile boolean shouldShutdown;

	protected volatile boolean isShutdown;


	public ColumnWorker(ColumnLockManager lockManager) {
		this.lockManager = lockManager;
		this.lockManager.register(this);

		this.shutdownMutex = new Object();
		this.shouldShutdown = false;
		this.isShutdown = false;
	}




	public void clearAssignment() {
		this.lockManager.clearAssignment(this);
	}

	public boolean assign(ColumnTask task) {
		return this.lockManager.assign(this, task);
	}

	public int getLockRadius() {
		return DEFAULT_LOCK_RADIUS;
	}

	public ColumnTask getAssignment() {
		return this.assignment;
	}

	void setAssignment(ColumnTask assignment) {
		this.assignment = assignment;
	}


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
