package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public interface IPartialCubeProvider extends ICubeProvider {

    Cube getCube(CubeCoords coords, GeneratorStage generatorStage);

    Cube provideCube(CubeCoords coords, GeneratorStage generatorStage);

}
