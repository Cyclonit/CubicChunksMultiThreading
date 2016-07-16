package de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment;

import de.cyclonit.cubeworkertest.util.GridCoords;

public interface IColumnTask {

	GridCoords getGridCoords();

	int getLockRadius();

}
