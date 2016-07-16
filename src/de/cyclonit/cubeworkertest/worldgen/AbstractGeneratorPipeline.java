package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependencyManager;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependentCube;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependentCubeManager;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStageRegistry;

public abstract class AbstractGeneratorPipeline implements IGeneratorPipeline {

	protected final GeneratorStageRegistry generatorStageRegistry;

	protected ICubeCache cubeCache;

	protected DependentCubeManager dependentCubeManager;


	public AbstractGeneratorPipeline(GeneratorStageRegistry generatorStageRegistry) {
		this.generatorStageRegistry = generatorStageRegistry;
	}


	// ------------------------------------------- Interface: IGeneratorPipeline -------------------------------------------

	@Override
	public void initialize(ICubeCache cubeCache, DependencyManager dependencyManager) {
		this.cubeCache = cubeCache;
		this.dependentCubeManager = new DependentCubeManager(dependencyManager);
	}

	@Override
	public void initializeCube(Cube cube) {
		cube.setCurrentStage(this.generatorStageRegistry.getFirstStage());
		cube.setTargetStage(this.generatorStageRegistry.getFirstStage());
	}

	@Override
	public void generateCube(Cube cube) {
		this.generateCube(cube, this.generatorStageRegistry.getLastStage());
	}

	@Override
	public void initializeColumn(Column column) {
		// TODO
	}

}
