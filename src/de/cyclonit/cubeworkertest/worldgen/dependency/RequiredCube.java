package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

import java.util.HashSet;
import java.util.Set;

class RequiredCube {

	private GeneratorStage targetStage;

	private final Set<ICubeDependent> dependents;


	public RequiredCube(GeneratorStage targetStage) {
		this.targetStage = targetStage;
		this.dependents = new HashSet<>();
	}


	public void setTargetStage(GeneratorStage targetStage) {
		this.targetStage = targetStage;
	}

	public GeneratorStage getTargetStage() {
		return targetStage;
	}


	public void addDependent(ICubeDependent dependent) {
		this.dependents.add(dependent);
	}

	public void removeDependent(ICubeDependent dependent) {
		this.dependents.remove(dependent);
	}

	public void updateDependents(Cube requiredCube) {
		for (ICubeDependent dependent : this.dependents) {
			dependent.onCubeUpdate(requiredCube);
		}
	}

	public boolean isRequired() {
		return this.dependents.size() > 0;
	}
}
