package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;

import java.util.HashMap;
import java.util.Map;

public class DependentCubeManager {

	private final DependencyManager dependencyManager;

	private final Map<CubeCoords, DependentCube> dependentCubeMap;


	public DependentCubeManager(DependencyManager dependencyManager) {
		this.dependencyManager = dependencyManager;
		this.dependentCubeMap = new HashMap<>();
	}


	public void registerDependentCube(DependentCube dependentCube) {

		// Prevent duplicate registrations.
		if (!this.dependentCubeMap.containsKey(dependentCube.getCube().getCoords())) {

			// Remember the dependent.
			this.dependentCubeMap.put(dependentCube.getCube().getCoords(), dependentCube);

			// Register the dependent at the DependencyManager.
			this.dependencyManager.registerDependent(dependentCube);
		}
	}

	public void unregisterDependentCube(DependentCube dependentCube) {
		this.dependencyManager.unregister(dependentCube);
		this.dependentCubeMap.remove(dependentCube.getCube().getCoords());
	}

	public void updateDependents(Cube cube) {
		this.dependencyManager.updateDependents(cube);
	}
}
