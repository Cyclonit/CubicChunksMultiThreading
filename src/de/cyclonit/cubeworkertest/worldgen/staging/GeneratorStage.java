package de.cyclonit.cubeworkertest.worldgen.staging;

public class GeneratorStage {

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

    public boolean precedes(GeneratorStage targetStage) {
        return this.ordinal < targetStage.ordinal;
    }

    public boolean isLast() {
        return this.nextStage == null;
    }

}
