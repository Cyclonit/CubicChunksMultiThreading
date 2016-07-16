package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.world.Cube;

import java.util.Collection;

public interface ICubeDependent {

	Collection<CubeRequirement> getRequirements();

	// TODO: Better name
	void onCubeUpdate(Cube cube);

	boolean isSatisfied();

}
