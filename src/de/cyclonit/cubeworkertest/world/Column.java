package de.cyclonit.cubeworkertest.world;

import de.cyclonit.cubeworkertest.util.ColumnCoords;

import java.util.Map;
import java.util.TreeMap;

public class Column {

    private final ColumnCoords coords;

    private final Map<Integer, Cube> cubes;


    public Column(ColumnCoords coords) {
        this.coords = coords;
        this.cubes = new TreeMap<>();
    }


    public ColumnCoords getCoords() {
        return this.coords;
    }

    public void addCube(Cube cube) {
        if (this.cubes.put(cube.getCoords().getCubeY(), cube) != null) {
            throw new Error("TODO");
        }
    }

    public Cube getCube(int cubeY) {
        return this.cubes.get(cubeY);
    }

}
