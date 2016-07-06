package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.ColumnCoords;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;

public interface ICubeProvider {

    ICubeCache getCubeCache();

    Cube getCube(CubeCoords coords);

    Column getColumn(ColumnCoords coords);

    Cube provideCube(CubeCoords coords);

    Column provideColumn(ColumnCoords coords);

}
