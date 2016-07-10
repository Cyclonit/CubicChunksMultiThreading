package de.cyclonit.cubeworkertest.worldgen.concurrency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.util.GridCoords;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public class GeneratorTask extends ColumnTask {

	public final CubeCoords coords;

	public final GeneratorStage targetStage;


	public GeneratorTask(CubeCoords coords, GeneratorStage targetStage) {
		this.coords = coords;
		this.targetStage = targetStage;
	}


	// -------------------------------------------- Superclass: ColumnTask ---------------------------------------------

	@Override
	public GridCoords getGridCoords() {
		return this.coords;
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
