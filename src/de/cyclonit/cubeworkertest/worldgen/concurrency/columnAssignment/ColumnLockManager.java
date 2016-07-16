package de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment;

import de.cyclonit.cubeworkertest.util.GridCoords;

import java.util.HashSet;
import java.util.Set;

/**
 * A ColumnLockManager provides the means to synchronize access to columns and their cubes. All instances of
 * {@link ColumnWorker} sharing one ColumnLockManager can be synchronized using their member methods and instances of
 * {@link IColumnTask}.
 *
 * Prior to accessing a given column, each worker must first call its {@link ColumnWorker#assign(IColumnTask)} method.
 * The given task object contains the coordinates of the column which it wants to access and an additional so called
 * 'lock radius'. These two values describe an area, which, if the assignment succeeds, is to be considered locked to
 * the worker. Assigning an area to a worker fails, if any column in the area is covered by another worker's current
 * assignment.
 *
 * Warning: A worker must never access columns outside of its current assignment, be it through the world's cube
 * provider or any other reference. Otherwise, it risks causing synchronization errors. There is no mechanism in place
 * to enforce this behaviour, instead it is entirely based on goodwill.
 **/
public class ColumnLockManager {

	/**
	 * The set of all ColumnWorkers
	 */
	private final Set<ColumnWorker> workers;

	private final Object mutex;


	/**
	 * Constructs a new ColumnLockManager.
	 */
	public ColumnLockManager() {
		this.workers = new HashSet<>();
		this.mutex = new Object();
	}


	// ----------------------------------------------- Worker Management -----------------------------------------------

	/**
	 * Registers the given {@link ColumnWorker} with this ColumnLockManager.
	 *
	 * @param worker the {@link ColumnWorker} to be registered
	 * @return true if the given {@link ColumnWorker} was not yet registered, false otherwise
	 */
	boolean register(ColumnWorker worker) {
		synchronized (this.mutex) {
			return this.workers.add(worker);
		}
	}

	/**
	 * Unregisters the given {@link ColumnWorker} from this ColumnLockManager.
	 *
	 * @param worker the {@link ColumnWorker} to be unregistered
	 * @return true if the given {@link ColumnWorker} was registered, false otherwise
	 */
	boolean unregister(ColumnWorker worker) {
		synchronized (this.mutex) {
			return this.workers.remove(worker);
		}
	}


	// Getters and Setters for ColumnWorker.assignment

	/**
	 * Clears the given {@link ColumnWorker}'s current assignment.
	 *
	 * @param worker the {@link ColumnWorker} whose assignment is to be cleared
	 */
	void clearAssignment(ColumnWorker worker) {
		synchronized (this.mutex) {
			worker.assignment = null;
		}
	}

	/**
	 * Sets the given {@link ColumnWorker}'s current assignment.
	 *
	 * @param worker the {@link ColumnWorker} whose assignment is to be set
	 * @param task the {@link IColumnTask} to be assigned
	 */
	void setAssignment(ColumnWorker worker, IColumnTask task) {
		synchronized (this.mutex) {
			worker.assignment = task;
		}
	}

	/**
	 * Retrieves the given {@link ColumnWorker}'s current assignment.
	 *
	 * @param worker the {@link ColumnWorker} whose assignment is to be retrieved
	 * @return the given worker's current assignment
	 */
	public IColumnTask getAssignment(ColumnWorker worker) {
		synchronized (this.mutex) {
			return worker.assignment;
		}
	}


	// ----------------------------------------------- Column Assignment -----------------------------------------------

	/**
	 * Determines if the given task is free to be assigned with respect to all tasks currently being assigned to other
	 * workers. For the task to be available, the region defined by its center point {@link IColumnTask#getGridCoords()}
	 * and the radius {@link IColumnTask#getLockRadius()} must not overlap with any other worker's current assignment.
	 * Calls to this method are synchronized with all other methods of this instance of ColumnLockManager. However, as
	 * soon as this method returns, the given column may be assigned to a worker through a parallel call to
	 * {@link #assign(IColumnTask, ColumnWorker)} or freed through {@link #clearAssignment(ColumnWorker)}.
	 *
	 * @param task the coordinates of the column to be checked
	 * @return true if currently there is no column assignment clashing with the given parameters
	 */
	private boolean isAvailable(IColumnTask task) {
		synchronized (this.mutex) {
			GridCoords taskCoords = task.getGridCoords();
			int lockRadius = task.getLockRadius();

			for (ColumnWorker worker : this.workers) {
				IColumnTask otherAssignment = worker.getAssignment();
				if (otherAssignment != null) {
					GridCoords otherCoords = otherAssignment.getGridCoords();
					if (Math.abs(otherCoords.getCubeX() - taskCoords.getCubeX()) < task.getLockRadius() + lockRadius &&
						Math.abs(otherCoords.getCubeZ() - taskCoords.getCubeZ()) < task.getLockRadius() + lockRadius) {
						return false;
					}
				}
			}
			return true;
		}
	}

	/**
	 * Attempts to assign the given {@link IColumnTask} to the given {@link ColumnWorker}. Assigning the task fails if
	 * the area defined by its {@link IColumnTask#getGridCoords()} and {@link IColumnTask#getLockRadius()} overlaps with
	 * the area of the current assignment of another worker.
	 * Calls to this method are synchronized with all other methods of this instance of ColumnLockManager.
	 *
	 * @param task the {@link IColumnTask} to be assigned
	 * @param worker the {@link ColumnWorker} to which the task will be assigned
	 * @return true if the task was assigned successfully, false otherwise
	 */
	public boolean assign(IColumnTask task, ColumnWorker worker) {
		synchronized (this.mutex) {
			if (this.isAvailable(task)) {
				worker.setAssignment(task);
				return true;
			} else {
				return false;
			}
		}
	}
}
