package de.cyclonit.cubeworkertest.world;

import de.cyclonit.cubeworkertest.util.ColumnCoords;
import de.cyclonit.cubeworkertest.util.CubeCoords;

public interface ICubeCache {

    void addCube(Cube cube);

    Cube getCube(CubeCoords coords);

    void addColumn(Column column);

    Column getColumn(ColumnCoords coords);

}
