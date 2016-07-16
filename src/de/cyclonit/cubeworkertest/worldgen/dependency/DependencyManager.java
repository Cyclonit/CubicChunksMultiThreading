package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.IPartialCubeProvider;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;

import java.util.HashMap;
import java.util.Map;

public class DependencyManager {

	public final IPartialCubeProvider cubeProvider;

	public final Map<CubeCoords, RequiredCube> requiredCubeMap;


	public DependencyManager(IPartialCubeProvider cubeProvider) {
		this.cubeProvider = cubeProvider;
		this.requiredCubeMap = new HashMap<CubeCoords, RequiredCube>();
	}


	public void registerDependent(ICubeDependent dependent) {

		for (CubeRequirement requirement : dependent.getRequirements()) {

			// Get the required cube's entry from the map.
			RequiredCube requiredCube = this.requiredCubeMap.get(requirement.getCoords());
			if (requiredCube == null) {
				requiredCube = new RequiredCube(requirement.getTargetStage());
				this.requiredCubeMap.put(requirement.getCoords(), requiredCube);
			}

			// Make sure the proper target stage is set.
			else if (requiredCube.getTargetStage().precedes(requirement.getTargetStage())) {
				requiredCube.setTargetStage(requirement.getTargetStage());
			}

			// Add the dependent to the required cube.
			requiredCube.addDependent(dependent);

			// Retrieve the cube from the cube provider or instruct it to generate it.
			Cube cube = this.cubeProvider.provideCube(requirement.getCoords(), requirement.getTargetStage());
			if (cube != null) {
				dependent.onCubeUpdate(cube);
			}
		}
	}

	public void unregister(ICubeDependent dependent) {

		for (CubeRequirement requirement : dependent.getRequirements()) {

			// Get the required cube's entry from the map.
			RequiredCube requiredCube = this.requiredCubeMap.get(requirement.getCoords());

			// Remove the dependent from the required cube's list of dependents.
			requiredCube.removeDependent(dependent);

			// If the cube is no longer required, remove it from the map.
			if (!requiredCube.isRequired()) {
				this.requiredCubeMap.remove(requirement.getCoords());
			}
		}

	}

	public void updateDependents(Cube cube) {
		RequiredCube requiredCube = this.requiredCubeMap.get(cube.getCoords());
		if (requiredCube != null) {
			requiredCube.updateDependents(cube);
		}
	}

	public boolean isRequired(Cube cube) {
		return this.requiredCubeMap.get(cube.getCoords()) != null;
	}

}
