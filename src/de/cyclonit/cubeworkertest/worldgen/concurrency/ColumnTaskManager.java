package de.cyclonit.cubeworkertest.worldgen.concurrency;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ColumnTaskManager<T extends ColumnTask> {

	private final ColumnLockManager lockManager;

	private final List<T> taskQueue;

	private final Set<ColumnWorker> workers;

	// Polling

	private final Object mutex;

	private final Object pollingMutex;

	private volatile int pollingProcessorCount;

	// Pausing

	private volatile boolean paused;

	private final Object pauseMutex;

	private volatile int pausedProcessorCount;

	// Shutting down

	private final Object shutdownMutex;

	private volatile boolean shutdown;


	// ------------------------------------------------- Constructors --------------------------------------------------

	public ColumnTaskManager() {
		this(new ColumnLockManager());
	}

	public ColumnTaskManager(ColumnLockManager lockManager) {
		this.lockManager = lockManager;
		this.taskQueue = new LinkedList<>();

		this.mutex = new Object();
		this.workers = new HashSet<>();

		this.paused = false;
		this.pauseMutex = new Object();
		this.pausedProcessorCount = 0;

		this.pollingMutex = new Object();
		this.pollingProcessorCount = 0;

		this.shutdownMutex = new Object();
		this.shutdown = false;
	}


	// ----------------------------------------------- Worker Management -----------------------------------------------

	public boolean register(ColumnWorker worker) {
		synchronized (this.shutdownMutex) {
			if (!this.shutdown) {
				synchronized (this.pauseMutex) {
					synchronized (this.pollingMutex) {
						return this.workers.add(worker);
					}
				}
			} else {
				return false;
			}
		}
	}

	public void unregister(ColumnWorker worker) {
		synchronized (this.shutdownMutex) {
			worker.clearAssignment();
			if (this.workers.remove(worker)) {
				this.shutdownMutex.notifyAll();
			}
		}
	}


	// ---------------------------------------------------- Pausing ----------------------------------------------------

	public void pause() {
		this.paused = true;

		// Notify all polling processors.
		synchronized (this.mutex) {
			this.mutex.notifyAll();
		}
	}

	public void unpause() {
		this.paused = false;

		// Notify all waiting processors.
		synchronized (this.mutex) {
			this.mutex.notifyAll();
		}
	}

	public void waitUntilPaused() {
		synchronized (this.pauseMutex) {
			if (this.paused) {
				try {
					while (this.pausedProcessorCount < this.workers.size() && !this.shutdown) {
						this.pauseMutex.wait();
					}
				} catch (InterruptedException e) {
					// TODO: Handle
				}
			}
		}
	}

	private void waitWhilePaused() {
		synchronized (this.mutex) {
			if (this.paused) {
				synchronized (this.pauseMutex) {
					++this.pausedProcessorCount;
					this.pauseMutex.notifyAll();
				}

				try {
					while (this.paused && !this.shutdown) {
						this.mutex.wait();
					}
				} catch (InterruptedException e) {
					// TODO: Handle
				}

				synchronized (this.pauseMutex) {
					--this.pausedProcessorCount;
				}
			}
		}
	}


	// ----------------------------------------------- Adding / Polling ------------------------------------------------

	public void add(T task) {
		synchronized (this.mutex) {
			this.taskQueue.add(task);

			// If the queue is not paused, notify waiting processor.
			if (!this.paused) {
				this.mutex.notifyAll();
			}
		}
	}

	private void waitForNew() {
		synchronized (this.mutex) {
			synchronized (this.pollingMutex) {
				++this.pollingProcessorCount;
				this.pollingMutex.notifyAll();
			}

			try {
				this.mutex.wait();
			} catch (InterruptedException e) {
				// TODO: Handle
			}

			synchronized (this.pollingMutex) {
				--this.pollingProcessorCount;
			}
		}
	}

	public boolean waitUntilPolling() {
		synchronized (this.pollingMutex) {

			synchronized (this.pauseMutex) {
				if (this.paused) {
					return false;
				}
			}

			try {
				while (this.pollingProcessorCount < this.workers.size()) {
					this.pollingMutex.wait();
				}
			} catch (InterruptedException e) {
				// TODO: Handle
			}

			return true;
		}
	}

	public T poll(ColumnWorker worker) {

		synchronized (this.mutex) {

			// Remove the processor's previous assignment.
			worker.clearAssignment();

			if (this.shutdown) {
				return null;
			}

			// If this queue is paused, wait for it to be unpaused.
			this.waitWhilePaused();

			// Notify all currently processors workers about potentially unlocked cubes.
			this.mutex.notifyAll();

			while (!this.shutdown && !worker.isShutdown()) {

				Iterator<T> iter = this.taskQueue.iterator();
				T task = iter.hasNext() ? iter.next() : null;

				while (task != null) {

					if (worker.assign(task)) {
						iter.remove();
						return task;
					}

					task = iter.hasNext() ? iter.next() : null;
				}

				// If this queue is paused, wait for it to be unpaused.
				if (this.paused) {
					this.waitWhilePaused();
				}
				// Otherwise, just wait for new cubes to be added.
				else {
					this.waitForNew();
				}
			}

			return null;
		}
	}


	// ------------------------------------------------- Shutting down -------------------------------------------------

	public void shutdown() {
		this.shutdown = true;

		// Unpause all workers.
		synchronized (this.mutex) {
			this.mutex.notifyAll();
		}
		synchronized (this.pauseMutex) {
			this.pauseMutex.notifyAll();
		}
	}

	public boolean isShutdown() {
		return this.shutdown;
	}

	public void waitUntilShutdown() {
		synchronized (this.shutdownMutex) {
			try {
				while (this.workers.size() > 0) {
					this.shutdownMutex.wait();
				}
			} catch (InterruptedException e) {
				// TODO: Handle
			}
		}
	}


	// ---------------------------------------------------- Helper -----------------------------------------------------

	public void notifyWorkers() {
		synchronized (this.mutex) {
			this.mutex.notifyAll();
		}
	}

	public int size() {
		synchronized (this.mutex) {
			return this.taskQueue.size();
		}
	}

	public boolean isEmpty() {
		synchronized (this.mutex) {
			return this.taskQueue.isEmpty();
		}
	}

	public int getRunningProcessorCount() {
		return this.workers.size() - this.pausedProcessorCount - this.pollingProcessorCount;
	}

	public ColumnLockManager getColumnLockManager() {
		return this.lockManager;
	}

}
