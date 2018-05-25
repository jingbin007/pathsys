/*-
 * #%L
 * This file is part of QuPath.
 * %%
 * Copyright (C) 2014 - 2018 The Queen's University of Belfast, Northern Ireland
 * Contact: IP Management (ipmanagement@qub.ac.uk)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

package qupath.lib.gui.commands.scriptable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qupath.lib.gui.ImageDataWrapper;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.images.ImageData;
import qupath.lib.objects.PathAnnotationObject;
import qupath.lib.objects.PathObject;
import qupath.lib.objects.hierarchy.PathObjectHierarchy;
import qupath.lib.roi.PolygonROI;
import qupath.lib.roi.ShapeSimplifierAwt;
import qupath.lib.roi.experimental.ShapeSimplifier;
import qupath.lib.roi.interfaces.PathShape;


/**
 * Simplify a shape by reducing the number of vertices it contains.
 * 
 * @author Pete Bankhead
 *
 */
public class ShapeSimplifierCommand implements PathCommand {
	
	final private static Logger logger = LoggerFactory.getLogger(ShapeSimplifierCommand.class);
	
	private ImageDataWrapper<?> manager;
	private double altitudeThreshold = 1;
	
	public ShapeSimplifierCommand(final ImageDataWrapper<?> manager) {
		this.manager = manager;
	}
	

	@Override
	public void run() {
		ImageData<?> imageData = manager.getImageData();
		if (imageData == null)
			return;
		PathObjectHierarchy hierarchy = imageData.getHierarchy();
		PathObject pathObject = hierarchy.getSelectionModel().getSelectedObject();
		if (!(pathObject instanceof PathAnnotationObject) || pathObject.hasChildren() || !(pathObject.getROI() instanceof PathShape)) {
			logger.error("Only annotations without child objects can be simplified");
			return;
		}

		String input = DisplayHelpers.showInputDialog("Simplify shape", 
				"Set altitude threshold in pixels (> 0; higher values give simpler shapes)", 
				Double.toString(altitudeThreshold));
		if (input == null || !(input instanceof String) || ((String)input).trim().length() == 0)
			return;
		try {
			altitudeThreshold = Double.parseDouble(((String)input).trim());
		} catch (NumberFormatException e) {
			logger.error("Could not parse altitude threshold from {}", input);
			return;
		}
		
		long startTime = System.currentTimeMillis();
		PathShape pathROI = (PathShape)pathObject.getROI();
		PathObject pathObjectNew = null;
		if (pathROI instanceof PolygonROI) {
			PolygonROI polygonROI = (PolygonROI)pathROI;
			polygonROI = ShapeSimplifier.simplifyPolygon(polygonROI, altitudeThreshold);
			pathObjectNew = new PathAnnotationObject(polygonROI, pathObject.getPathClass(), pathObject.getMeasurementList());
		} else {
			pathROI = ShapeSimplifierAwt.simplifyShape(pathROI, altitudeThreshold);
			pathObjectNew = new PathAnnotationObject(pathROI, pathObject.getPathClass(), pathObject.getMeasurementList());			
		}
		long endTime = System.currentTimeMillis();
//		logger.debug("Polygon simplified in " + (endTime - startTime)/1000. + " seconds");
		logger.debug("Shape simplified in " + (endTime - startTime) + " ms");
		hierarchy.removeObject(pathObject, true);
		hierarchy.addPathObject(pathObjectNew, false);
		hierarchy.getSelectionModel().setSelectedObject(pathObjectNew);
//		viewer.setSelectedObject(pathObjectNew);
	}
	

}
