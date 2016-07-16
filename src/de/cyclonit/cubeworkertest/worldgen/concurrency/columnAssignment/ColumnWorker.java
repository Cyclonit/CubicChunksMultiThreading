package de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment;

public abstract class ColumnWorker {

	protected final ColumnLockManager lockManager;

	IColumnTask assignment;

	private boolean alive;


	public ColumnWorker(ColumnLockManager lockManager) {
		this.lockManager = lockManager;
		this.lockManager.register(this);
		this.alive = true;
	}


	public void clearAssignment() {
		this.lockManager.clearAssignment(this);
	}

	public boolean assign(IColumnTask task) {
		return this.lockManager.assign(task, this);
	}

	public IColumnTask getAssignment() {
		return this.lockManager.getAssignment(this);
	}

	void setAssignment(IColumnTask assignment) {
		this.lockManager.setAssignment(this, assignment);
	}

	public void destroy() {
		this.clearAssignment();
		this.alive = false;
	}

}
