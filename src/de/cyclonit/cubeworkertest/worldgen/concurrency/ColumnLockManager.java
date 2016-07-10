package de.cyclonit.cubeworkertest.worldgen.concurrency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.util.GridCoords;

import java.util.HashSet;
import java.util.Set;

public class ColumnLockManager {

	private final Set<ColumnWorker> workers;

	private final Object mutex;


	public ColumnLockManager() {
		this.workers = new HashSet<>();
		this.mutex = new Object();
	}


	public boolean register(ColumnWorker worker) {
		return this.workers.add(worker);
	}


	public boolean isAvailable(GridCoords coords, int lockRadius) {
		synchronized (this.mutex) {
			for (ColumnWorker worker : this.workers) {
				ColumnTask otherAssignment = worker.getAssignment();
				if (otherAssignment != null) {
					GridCoords otherCoords = otherAssignment.getGridCoords();
					if (otherCoords.getCubeX() - coords.getCubeX() < worker.getLockRadius() + lockRadius &&
						otherCoords.getCubeZ() - coords.getCubeZ() < worker.getLockRadius() + lockRadius) {
						return false;
					}
				}
			}
			return true;
		}
	}

	public boolean assign(ColumnWorker worker, ColumnTask task) {
		synchronized (this.mutex) {
			if (this.isAvailable(task.getGridCoords(), worker.getLockRadius())) {
				worker.setAssignment(task);
				return true;
			} else {
				return false;
			}
		}
	}

	public void clearAssignment(ColumnWorker worker) {
		synchronized (this.mutex) {
			worker.setAssignment(null);
		}
	}
}
