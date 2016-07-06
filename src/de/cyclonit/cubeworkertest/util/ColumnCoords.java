package de.cyclonit.cubeworkertest.util;

public class ColumnCoords {

    private final int x;

    private final int z;


    public ColumnCoords(int cubeX, int cubeZ) {
        this.x = cubeX;
        this.z = cubeZ;
    }

    public static ColumnCoords fromCubeCoords(CubeCoords coords) {
        return new ColumnCoords(coords.getCubeX(), coords.getCubeZ());
    }


    public int getCubeX() {
        return this.x;
    }

    public int getCubeZ() {
        return this.z;
    }


    // ---------------------------------------------- Superclass: Object -----------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColumnCoords) {
            ColumnCoords other = (ColumnCoords) obj;
            return this.x == other.x && this.z == other.z;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Long.hashCode(AddressTools.getAddress(this.x, this.z));
    }
}
