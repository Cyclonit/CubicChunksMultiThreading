package de.cyclonit.cubeworkertest.worldgen.dependency;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.IGeneratorPipeline;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DependentCube implements ICubeDependent
{
	private final Cube cube;

	private final ICubeDependency cubeDependency;

	private final IGeneratorPipeline generatorPipeline;

	private final Map<CubeCoords, CubeRequirement> requirements;

	private int remaining;


	public DependentCube(Cube cube, ICubeDependency cubeDependency, IGeneratorPipeline generatorPipeline) {
		this.cube = cube;
		this.cubeDependency = cubeDependency;
		this.generatorPipeline = generatorPipeline;

		this.requirements = new HashMap<>();
		for (CubeRequirement requirement : this.cubeDependency.getRequirements(this.cube)) {
			this.requirements.put(requirement.getCoords(), requirement);
		}
		this.remaining = this.requirements.size();
	}


	public Cube getCube() {
		return this.cube;
	}


	// ------------------------------------------- Interface: ICubeDependent -------------------------------------------

	@Override
	public Collection<CubeRequirement> getRequirements() {
		return this.requirements.values();
	}

	@Override
	public void onCubeUpdate(Cube updatedCube) {
		if (this.cubeDependency.isSatisfied(this) && !this.requirements.get(updatedCube.getCoords()).isSatisfied()) {

			this.requirements.get(updatedCube.getCoords()).setSatisfied(true);
			--this.remaining;

			if (this.isSatisfied()) {
				this.generatorPipeline.resumeCube(this.cube);
			}
		}
	}

	@Override
	public boolean isSatisfied() {
		return this.remaining == 0;
	}

}
