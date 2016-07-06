package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.IPartialCubeProvider;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public interface ICubeGenerator {

    void initialize(IPartialCubeProvider cubeProvider);

    void generateCube(Cube cube);

    void generateCube(Cube cube, GeneratorStage targetStage);

    void generateColumn(Column column);

    void tick();

    void processAll();

}
