package de.cyclonit.cubeworkertest.worldgen.staging;

import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.dependency.ICubeDependency;

public class GeneratorStage implements ICubeDependencyProvider {

    private final String name;

    private int ordinal;

    private GeneratorStage nextStage;


    public GeneratorStage(String name) {
        this.name = name;
    }


    public String getName() {
        return this.name;
    }

    void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    void setNextStage(GeneratorStage nextStage) {
        if (this.nextStage == null) {
            this.nextStage = nextStage;
        } else {
            throw new Error("TODO");
        }
    }

    public GeneratorStage getNextStage() {
        return this.nextStage;
    }


    // -------------------------------------- Interface: ICubeDependencyProvider ---------------------------------------

    public ICubeDependency getCubeDependency(Cube cube) {
        return null;
    }


    // ---------------------------------------------------- Helper -----------------------------------------------------

    public boolean precedes(GeneratorStage targetStage) {
        return this.ordinal < targetStage.ordinal;
    }

    public boolean isLast() {
        return this.nextStage == null;
    }

}
