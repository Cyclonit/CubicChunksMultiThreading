package de.cyclonit.cubeworkertest.worldgen.staging;

import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.dependency.ICubeDependency;

public class DependentGeneratorStage extends GeneratorStage {

	private final ICubeDependency cubeDependency;


	public DependentGeneratorStage(String name, ICubeDependency cubeDependency) {
		super(name);
		this.cubeDependency = cubeDependency;
	}


	@Override
	public ICubeDependency getCubeDependency(Cube cube) {
		return this.cubeDependency;
	}

}
