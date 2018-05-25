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

package qupath.lib.roi;

import qupath.lib.rois.vertices.MutableVertices;
import qupath.lib.rois.vertices.Vertices;

/**
 * Factory used to help create vertices objects.
 * 
 * @author Pete Bankhead
 *
 */
public class VerticesFactory {

	public static DefaultVertices createVertices(final int capacity) {
		return new DefaultVertices(capacity);
	}

	public static DefaultMutableVertices createMutableVertices() {
		return new DefaultMutableVertices(createVertices(DefaultVertices.DEFAULT_CAPACITY));
	}

	public static Vertices createVertices(final float[] x, final float[] y, final boolean copyArrays) {
		return new DefaultVertices(x, y, copyArrays);
	}

	public static Vertices createVertices() {
		return createVertices(DefaultVertices.DEFAULT_CAPACITY);
	}

	public static DefaultMutableVertices createMutableVertices(final int capacity) {
		return new DefaultMutableVertices(new DefaultVertices(capacity));
	}

	public static MutableVertices createMutableVertices(final float[] x, final float[] y, final boolean copyArrays) {
		return new DefaultMutableVertices(new DefaultVertices(x, y, copyArrays));
	}

}
