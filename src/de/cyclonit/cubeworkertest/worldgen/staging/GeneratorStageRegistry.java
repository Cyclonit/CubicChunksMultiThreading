package de.cyclonit.cubeworkertest.worldgen.staging;

import java.util.ArrayList;
import java.util.List;

public class GeneratorStageRegistry {

    private final List<GeneratorStage> stages;


    public GeneratorStageRegistry() {
        this.stages = new ArrayList<>();
    }


    public void addStage(GeneratorStage generatorStage) {

        if (this.stages.contains(generatorStage)) {
            throw new Error("TODO");
        }

        generatorStage.setOrdinal(this.stages.size());
        this.stages.add(generatorStage);

        if (this.stages.size() > 1) {
            this.stages.get(this.stages.size() - 2).setNextStage(generatorStage);
        }
    }

    public GeneratorStage getStage(int ordinal) {
        return this.stages.get(ordinal);
    }

    public GeneratorStage getFirstStage() {
        return this.stages.get(0);
    }

    public GeneratorStage getLastStage() {
        return this.stages.get(this.stages.size() - 1);
    }

    public int size() {
        return this.stages.size();
    }

}
