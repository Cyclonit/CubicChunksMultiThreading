package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.world.Cube;

import java.util.Collection;

public interface ICubeDependency {

	Collection<CubeRequirement> getRequirements(Cube cube);

	boolean isSatisfied(DependentCube dependentCube);

}
