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

package qupath.lib.objects;

import qupath.lib.measurements.MeasurementList;
import qupath.lib.roi.interfaces.ROI;

/**
 * A subclass of PathDetectionObject, which is generally used to represent an image region that doesn't 
 * (in itself) correspond to any particularly interesting structure, e.g. a square tile or irregularly-shaped 'superpixel'.
 * 
 * @author Pete Bankhead
 *
 * @see PathDetectionObject
 */
public class PathTileObject extends PathDetectionObject {

	private static final long serialVersionUID = 1L;
	
	public PathTileObject() {
		super();
	}

	public PathTileObject(ROI pathROI) {
		super(pathROI, null);
	}
	
	public PathTileObject(ROI pathROI, MeasurementList measurements) {
		super(pathROI, null, measurements);
	}
		
		
}
