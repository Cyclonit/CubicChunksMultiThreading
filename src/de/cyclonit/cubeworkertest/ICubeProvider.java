package de.cyclonit.cubeworkertest;

import de.cyclonit.cubeworkertest.util.ColumnCoords;
import de.cyclonit.cubeworkertest.util.CubeCoords;
import de.cyclonit.cubeworkertest.world.Column;
import de.cyclonit.cubeworkertest.world.Cube;
import de.cyclonit.cubeworkertest.world.ICubeCache;

/**
 * Instances of classes implementing this interface provide the means for retrieving live cubes and columns. There are
 * methods for both retrieving cubes that are currently loaded and to cause the provider to load or generate cubes that
 * are not loaded.
 * TODO
 */
public interface ICubeProvider {

	/**
	 * Retrieves the ICubeCache used by this ICubeProvider. This ICubeCache contains all cubes currently being provided
	 * by this provider. The cache may contain additional cubes, which may not yet be live.
	 *
	 * @return this provider's ICubeCache
	 */
    ICubeCache getCubeCache();

	/**
	 * Retrieves the cube at the given coordinates, if it is both loaded and live. Otherwise, this method returns null.
	 * There will be no attempt made to load or generate the requested cube.
	 *
	 * @param coords the coordinates of the cube to be retrieved
	 * @return the requested cube if it is available or null otherwise
	 */
    Cube getCube(CubeCoords coords);

	/**
	 * Retrieves the column at the given coordinates, if it is both loaded and live. Otherwise, this method returns
	 * null. There will be no attempt made to load or generate the requested column.
	 *
	 * @param coords the coordinates of the column to be retrieved
	 * @return the requested column if it is available or null otherwise
	 */
    Column getColumn(ColumnCoords coords);

	/**
	 * Retrieves the cube at the given coordinates, if it is loaded and live. Otherwise, this method returns null
	 * immediately and the cube will be loaded and/or generated asynchronously. There is no
	 * guarantee as to if or when the requested cube will be available.
	 * @see #tick(long)
	 *
	 * @param coords the coordinates of the cube to be retrieved
	 * @return the requested cube if it is available or null otherwise
	 */
    Cube provideCube(CubeCoords coords);

	/**
	 * Retrieves the column at the given coordinates, if it is loaded and live. Otherwise, this method returns null
	 * immediately and the column will be loaded and/or generated asynchronously. There is no guarantee as to if or when
	 * the requested column will be available.
	 * @see #tick(long)
	 *
	 * @param coords the coordinates of the column to be retrieved
	 * @return the requested cube if it is available or null otherwise
	 */
    Column provideColumn(ColumnCoords coords);

	/**
	 * This method processes the asynchronous loading and generating of cubes requested through this provider' methods.
	 * It will make an effort to limit its runtime to the given duration on a best effort basis.
	 *
	 * @param duration the intended maximum duration for this method to run, in ms
	 */
	void tick(long duration);

}
