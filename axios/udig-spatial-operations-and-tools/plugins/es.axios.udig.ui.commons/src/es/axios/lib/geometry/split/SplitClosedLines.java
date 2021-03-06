/* Spatial Operations & Editing Tools for uDig
 * 
 * Axios Engineering under a funding contract with: 
 *      Wien Government 
 *
 *      http://wien.gov.at
 *      http://www.axios.es 
 *
 * (C) 2010, Vienna City - Municipal Department of Automated Data Processing, 
 * Information and Communications Technologies.
 * Vienna City agrees to license under Lesser General Public License (LGPL).
 * 
 * You can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software 
 * Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 */
package es.axios.lib.geometry.split;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import es.axios.lib.geometry.split.RingExtractor.ResultRingExtractor;

/**
 * Responsible of doing the split when the split line is a closed line.
 * 
 * @author Mauricio Pazos (www.axios.es)
 * @author Aritz Davila (www.axios.es)
 * @since 1.3.0
 */
final class SplitClosedLines {

	/** The remaining line (Original line less the extracted rings). */
	Geometry		remainingLine	= null;

	/** The extracted rings from the original line. */
	List<Geometry>	rings			= null;

	/** Stores the resultant geometries from this operation. */
	List<Geometry>	piecesFrom		= null;

	/**
	 * Constructor that requires the output of the {@link RingExtractor}.
	 * 
	 * @param inputData
	 */
	public SplitClosedLines(ResultRingExtractor inputData) {

		this.remainingLine = inputData.getRemainingLine();
		this.rings = inputData.getRings();
	}

	/**
	 * Executes the closed line operation.
	 * 
	 * @param polygon
	 * @return The resultant geometries.
	 */
	public List<Geometry> runClosedLineSplit(Geometry polygon) {

		piecesFrom = new ArrayList<Geometry>();

		Geometry result = processClosedLines(polygon);

		piecesFrom.addAll(processRemainingLine(result));

		return piecesFrom;
	}

	/**
	 * Realize the closed line operation for the given polygon.
	 * 
	 * It stores the resultant geometries in the piecesFrom list, and return the
	 * remaining of the input polygon.
	 * 
	 * @param polygon
	 *            Polygon which will suffer the closed line operation.
	 * @return The remaining of the input polygon. The resultant geometries are
	 *         stored on piecesFrom list.
	 */
	private Geometry processClosedLines(Geometry polygon) {

		Geometry result = polygon;
		GeometryFactory gf = polygon.getFactory();
		for (Geometry ring : rings) {

			// the original feature + apply all the differences with a ring =
			// result feature

			// each ring, if it lays entirely inside the polygon = one new piece
			Polygon polygonArea = gf.createPolygon((LinearRing) ring, null);
			result = result.difference(polygonArea);
			Geometry areaIntersection = polygonArea.intersection(polygon);
			for (int i = 0; i < areaIntersection.getNumGeometries(); i++) {
				piecesFrom.add(areaIntersection.getGeometryN(i));
			}
		}
		return result;
	}

	/**
	 * Pick the remaining line and the resultant polygon from the previous
	 * operations and perform an split operation.
	 * 
	 * @param geomToSplit
	 *            The resultant geometry from the previous step.
	 * @return List with the split geometries.
	 */
	private List<Geometry> processRemainingLine(Geometry geomToSplit) {

		List<Geometry> splitResult = new ArrayList<Geometry>();
		List<Geometry> inputGeometries = new ArrayList<Geometry>();
		Geometry line = remainingLine;

		for (int j = 0; j < geomToSplit.getNumGeometries(); j++) {

			inputGeometries.add(geomToSplit.getGeometryN(j));
		}
		for (int i = 0; i < line.getNumGeometries(); i++) {

			splitResult.clear();
			SplitStrategy strategy = new SplitStrategy((LineString) line.getGeometryN(i));

			for (Geometry inputGeom : inputGeometries) {

				if (strategy.canSplit(inputGeom)) {

					splitResult.addAll(strategy.split(inputGeom));
				} else {
					splitResult.add(inputGeom);
				}
			}

			inputGeometries.clear();
			inputGeometries.addAll(splitResult);

		}

		return inputGeometries;
	}

}
