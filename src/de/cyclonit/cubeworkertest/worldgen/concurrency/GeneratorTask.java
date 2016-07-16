package de.cyclonit.cubeworkertest.worldgen.concurrency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.util.GridCoords;
import de.cyclonit.cubeworkertest.worldgen.concurrency.columnAssignment.IColumnTask;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public class GeneratorTask implements IColumnTask {

	public static final int DEFAULT_LOCK_RADIUS = 2;


	public final CubeCoords coords;

	public final GeneratorStage targetStage;


	public GeneratorTask(CubeCoords coords, GeneratorStage targetStage) {
		this.coords = coords;
		this.targetStage = targetStage;
	}


	// -------------------------------------------- Interface: IColumnTask ---------------------------------------------

	@Override
	public GridCoords getGridCoords() {
		return this.coords;
	}

	@Override
	public int getLockRadius() {
		return DEFAULT_LOCK_RADIUS;
	}


	// ---------------------------------------------- Superclass: Object -----------------------------------------------

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeneratorTask) {
			GeneratorTask other = (GeneratorTask) obj;
			return this.coords == other.coords && this.targetStage == other.targetStage;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.coords.hashCode();
	}
}
