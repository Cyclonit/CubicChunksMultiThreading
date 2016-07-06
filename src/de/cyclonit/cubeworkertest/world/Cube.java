package de.cyclonit.cubeworkertest.world;

import de.cyclonit.cubeworkertest.util.AddressTools;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public class Cube {

    private final CubeCoords coords;

    private GeneratorStage currentStage;

    private GeneratorStage targetStage;


    public Cube(CubeCoords coords) {
        this.coords = coords;
        this.currentStage = null;
        this.targetStage = null;
    }


    public CubeCoords getCoords() {
        return this.coords;
    }

    public void setCurrentStage(GeneratorStage currentStage) {
        this.currentStage = currentStage;
    }

    public GeneratorStage getCurrentStage() {
        return currentStage;
    }

    public void setTargetStage(GeneratorStage targetStage) {
        this.targetStage = targetStage;
    }

    public GeneratorStage getTargetStage() {
        return targetStage;
    }

    public boolean hasReachedTargetStage() {
        return this.hasReachedStage(this.targetStage);
    }

    public boolean hasReachedStage(GeneratorStage generatorStage) {
        return !this.currentStage.precedes(generatorStage);
    }

    public boolean isLive() {
        return this.currentStage.isLast();
    }


	// ---------------------------------------------- Superclass: Object -----------------------------------------------

	@Override
	public String toString() {
		return "Cube(" + this.coords.getCubeX() + "," + this.coords.getCubeY() + "," + this.coords.getCubeZ() + ")";
	}

}
