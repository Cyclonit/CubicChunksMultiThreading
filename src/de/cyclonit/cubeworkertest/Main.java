package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.worldgen.MultiThreadedWorldGenerator;
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
	    GeneratorStage gamma = new GeneratorStage("gamma");
	    stageRegistry.addStage(gamma);

	    SingleThreadedWorldGenerator singleThreadedWorldGenerator = new SingleThreadedWorldGenerator(stageRegistry);
	    ServerCubeProvider singleThreadedCubeProvider = new ServerCubeProvider(singleThreadedWorldGenerator);

	    MultiThreadedWorldGenerator multiThreadedWorldGenerator = new MultiThreadedWorldGenerator(stageRegistry);
	    ServerCubeProvider multiThreadedCubeProvider = new ServerCubeProvider(multiThreadedWorldGenerator);

	    int radius = 5;
		for (int x = -radius; x <= radius; ++x) {
			for (int y = -radius; y <= radius; ++y) {
				for (int z = -radius; z <= radius; ++z) {
					singleThreadedCubeProvider.provideCube(new CubeCoords(x, y, z));
					multiThreadedCubeProvider.provideCube(new CubeCoords(x, y, z));
				}
			}
		}

	    long timeStart, timeDiff = 0;

		// Single threaded
	    timeStart = System.currentTimeMillis();
	    singleThreadedWorldGenerator.processAll();
	    timeDiff = System.currentTimeMillis() - timeStart;

	    System.out.println("Single Threaded: " + timeDiff + "ms");
	    singleThreadedWorldGenerator.getReport().reportTotal();


	    // Multi threaded
	    multiThreadedWorldGenerator.setWorkerCount(2);
	    timeStart = System.currentTimeMillis();
	    multiThreadedWorldGenerator.processAll();
	    timeDiff = System.currentTimeMillis() - timeStart;

	    System.out.println("Multi Threaded: " + timeDiff + "ms");
	    multiThreadedWorldGenerator.report();

	    for (int x = -radius; x <= radius; ++x) {
		    for (int y = -radius; y <= radius; ++y) {
			    for (int z = -radius; z <= radius; ++z) {
				    if (singleThreadedCubeProvider.getCube(new CubeCoords(x, y, z)).getCurrentStage() .getName() != "gamma") {
					    System.out.println("WRONG!");
				    }
				    if (multiThreadedCubeProvider.getCube(new CubeCoords(x, y, z)).getCurrentStage() .getName() != "gamma") {
					    System.out.println("WRONG! 2");
				    }
			    }
		    }
	    }

	    return;
    }
}
