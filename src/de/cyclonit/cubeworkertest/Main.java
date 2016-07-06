package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.worldgen.SingleThreadedWorldGenerator;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

public class Main {

    public static void main(String[] args) {

	    GeneratorStageRegistry stageRegistry = new GeneratorStageRegistry();

	    GeneratorStage alpha = new GeneratorStage("alpha");
	    stageRegistry.addStage(alpha);

	    GeneratorStage beta = new GeneratorStage("beta");
	    stageRegistry.addStage(beta);

	    SingleThreadedWorldGenerator worldGenerator = new SingleThreadedWorldGenerator(stageRegistry);

	    ServerCubeProvider cubeProvider = new ServerCubeProvider(worldGenerator);

	    long timeStart = System.currentTimeMillis();

	    int radius = 10;
		for (int x = -radius; x <= radius; ++x) {
			for (int y = -radius; y <= radius; ++y) {
				for (int z = -radius; z <= radius; ++z) {
					cubeProvider.provideCube(new CubeCoords(x, y, z));
				}
			}
		}

	    long timeDiff = System.currentTimeMillis() - timeStart;
	    System.out.println("Providing: " + timeDiff);


	    timeStart = System.currentTimeMillis();

	    worldGenerator.processAll();

	    timeDiff = System.currentTimeMillis() - timeStart;
	    System.out.println("Generating: " + timeDiff);

    }
}
