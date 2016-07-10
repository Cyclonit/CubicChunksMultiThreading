package de.cyclonit.cubeworkertest.util;

public class CubeCoords implements GridCoords {

    private final int x;

    private final int y;

    private final int z;


    public CubeCoords(int cubeX, int cubeY, int cubeZ) {
        this.x = cubeX;
        this.y = cubeY;
        this.z = cubeZ;
    }


    public int getCubeX() {
        return this.x;
    }

    public int getCubeY() {
        return this.y;
    }

    public int getCubeZ() {
        return this.z;
    }


    // ---------------------------------------------- Superclass: Object -----------------------------------------------

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CubeCoords) {
            CubeCoords other = (CubeCoords) obj;
            return this.x == other.x && this.y == other.y && this.z == other.z;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Long.hashCode(AddressTools.getAddress(this.x, this.y, this.z));
    }
}
