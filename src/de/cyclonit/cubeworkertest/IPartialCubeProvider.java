package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.worldgen.staging.GeneratorStage;

/**
 * Instances of classes implementing this interface provide the means for retrieving partially generated cubes. Whenever
 * a partial cube is requested through methods of this interface, it must not be generated past the requested stage,
 * unless an explicit request to do so has been issued. This restriction is in place to prevent infinite chains of cubes
 * being generated through use of the dependency system.
 */
public interface IPartialCubeProvider extends ICubeProvider {

	Cube getRawCube(CubeCoords coords);

	/**
     * Retrieves the requested cube, if it is loaded and has reached the given GeneratorStage. Otherwise, this method
	 * returns null. There will be no attempt made to load or generate the requested cube.
     *
     * @param coords the coordinates of the cube to be retrieved
     * @param generatorStage the GeneratorStage the requested cube must have reached
     * @return the requested cube if it is available and has reached the given GeneratorStage or null otherwise.
     */
    Cube getCube(CubeCoords coords, GeneratorStage generatorStage);

	/**
	 * Retrieves the requested cube, if it is loaded and has reached the given GeneratorStage. Otherwise, this method
	 * returns null immediately and the cube will be loaded and/or generated asynchronously. There is no guarantee as to
	 * if or when the requested cube will be available.
	 * @see #tick(long)
     *
     * @param coords the coordinates of the cube to be retrieved
     * @param generatorStage the GeneratorStage the requested cube must have reached
     * @return the requested cube if it is available and has reached the given GeneratorStage or null otherwise
     */
    Cube provideCube(CubeCoords coords, GeneratorStage generatorStage);

}
