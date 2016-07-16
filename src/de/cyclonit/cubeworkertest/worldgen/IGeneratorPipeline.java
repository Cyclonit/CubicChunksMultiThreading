package de.cyclonit.cubeworkertest.worldgen;

import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;
import de.cyclonit.cubeworkertest.worldgen.dependency.DependencyManager;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

public interface IGeneratorPipeline {

	/**
	 * Provides the IGeneratorPipeline with the ICubeCache containing the cubes it will operate on. This method must be
	 * called prior to any other method of this interface.
	 *
	 * @param cubeCache the ICubeCache to be used by this IGeneratorPipeline
	 */
	// TODO: Comment
    void initialize(ICubeCache cubeCache, DependencyManager dependencyManager);

	/**
	 * Initializes the given cube with regards to world generation. This method must be called directly after the given
	 * cube's object has been created, such that all required properties are set.
	 *
	 * @param cube the cube to be initialized
	 */
	void initializeCube(Cube cube);

	/**
	 * Schedules the given cube to be fully generated. No processing will occur in this method. Instead, either
	 * {@link #tick(long)} or {@link #processAll()} must be called to process cubes asynchronously to this method.
	 * If the given cube has already been generated fully, no further actions will be taken.
	 *
	 * @param cube the cube to be generated
	 */
    void generateCube(Cube cube);

	/**
	 * Schedules the given cube to be partially generated up to the given GeneratorStage. No processing will occur in
	 * this method. Instead, either {@link #tick(long)} or {@link #processAll()} must be called to process cubes
	 * asynchronously to this method.
	 * If the given cube has already been generated up to the given stage, no further actions will be taken.
	 *
	 * @param cube the cube to be generated partially
	 * @param targetStage the GeneratorStage up to which the cube will be generated
	 */
    void generateCube(Cube cube, GeneratorStage targetStage);

	void resumeCube(Cube cube);

	/**
	 * Initializes the given column with regards to world generation. This method must be called directly after the
	 * given column's object has been created, such that all required properties are set.
	 *
	 * @param column the column to be initialized
	 */
    void initializeColumn(Column column);

	/**
	 * Processes the cubes that have been given to this IGeneratorPipeline through calls to {@link #generateCube(Cube)} and
	 * {@link #generateCube(Cube, GeneratorStage)}. Implementations of IGeneratorPipeline are meant to limit the total
	 * runtime of each call to this method to the given duration on a best-effort basis.
	 *
	 * @param duration the intended maximum runtime of the current call to this method
	 */
    void tick(long duration);

	/**
	 * Processes all cubes that were scheduled through {@link #generateCube(Cube)} and
	 * {@link #generateCube(Cube, GeneratorStage)}, in advance to the call to this method. On return, all scheduled
	 * cubes will have been generated either fully or up to the latest requested GeneratorStage.
	 */
    void processAll();


	void reportTotal();

	void reportRecent();

	void resetRecentReport();

}
