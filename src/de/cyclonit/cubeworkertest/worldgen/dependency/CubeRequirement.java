package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public class CubeRequirement {

	private final CubeCoords coords;

	private final GeneratorStage targetStage;

	private boolean satisfied;


	public CubeRequirement(CubeCoords coords, GeneratorStage targetStage) {
		this.coords = coords;
		this.targetStage = targetStage;
		this.satisfied = false;
	}


	public CubeCoords getCoords() {
		return coords;
	}

	public GeneratorStage getTargetStage() {
		return targetStage;
	}

	public boolean isSatisfied() {
		return this.satisfied;
	}

	public void setSatisfied(boolean satisfied) {
		this.satisfied = satisfied;
	}
}
