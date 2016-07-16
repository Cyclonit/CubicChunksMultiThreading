package de.cyclonit.cubeworkertest.worldgen.staging;

import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.dependency.ICubeDependency;

public interface ICubeDependencyProvider {

	ICubeDependency getCubeDependency(Cube cube);

}
