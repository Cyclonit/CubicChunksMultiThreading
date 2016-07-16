package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.worldgen.MultiThreadedGeneratorPipeline;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

public class Main {

    public static void main(String[] args) {

	    GeneratorStageRegistry stageRegistry = new GeneratorStageRegistry();

	    GeneratorStage alpha = new GeneratorStage("alpha");
	    stageRegistry.addStage(alpha);

	    GeneratorStage beta = new GeneratorStage("beta");
	    stageRegistry.addStage(beta);
	    GeneratorStage gamma = new GeneratorStage("gamma");
	    stageRegistry.addStage(gamma);

	    int runs = 20;
	    long total = 0L;

	    for (int i = 0; i < runs; ++i) {
		    System.out.println(i + "...");

		    MultiThreadedGeneratorPipeline generatorPipeline = new MultiThreadedGeneratorPipeline(stageRegistry);
		    ServerCubeProvider cubeProvider = new ServerCubeProvider(generatorPipeline);

		    int radius = 5;
		    for (int x = -radius; x <= radius; ++x) {
			    for (int y = -radius; y <= radius; ++y) {
				    for (int z = -radius; z <= radius; ++z) {
					    cubeProvider.provideCube(new CubeCoords(x, y, z));
				    }
			    }
		    }

		    long timeStart = System.currentTimeMillis();
		    generatorPipeline.processAll();
		    total += System.currentTimeMillis() - timeStart;

		    generatorPipeline.shutdown();

	    }

	    double average = (double) total / runs;
	    System.out.println(average);

	    return;
    }
}
