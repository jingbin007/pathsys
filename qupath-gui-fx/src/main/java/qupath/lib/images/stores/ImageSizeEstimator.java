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

package qupath.lib.images.stores;

import javafx.scene.image.Image;

/**
 * Estimate the size, in bytes of a JavaFX image.
 * 
 * This does nothing particularly clever... currently, it only multiplies width * height * 4,
 * assuming RGBA (or equivalent) storage.
 * 
 * @author Pete Bankhead
 *
 */
public class ImageSizeEstimator implements SizeEstimator<Image> {

	@Override
	public long getApproxImageSize(Image img) {
		if (img == null)
			return 0;
		return (long)(Math.ceil(img.getWidth()) * Math.ceil(img.getHeight()) * 4);
	}

}
