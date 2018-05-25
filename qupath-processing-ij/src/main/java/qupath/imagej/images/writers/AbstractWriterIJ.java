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

package qupath.imagej.images.writers;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import ij.process.ShortProcessor;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.IOException;

import qupath.imagej.helpers.IJTools;
import qupath.lib.images.PathImage;
import qupath.lib.images.servers.ImageServer;
import qupath.lib.images.writers.AbstractImageWriter;
import qupath.lib.regions.RegionRequest;
import qupath.lib.roi.interfaces.ROI;

/**
 * Abstract ImageWriter using ImageJ.
 * 
 * @author Pete Bankhead
 *
 */
abstract class AbstractWriterIJ extends AbstractImageWriter<BufferedImage> {

	@Override
	public boolean supportsT() {
		return true;
	}

	@Override
	public boolean supportsZ() {
		return true;
	}

	@Override
	public boolean suportsImageType(ImageServer<BufferedImage> server) {
		return true;
	}

	@Override
	public boolean supportsPyramidal() {
		return false;
	}

	@Override
	public boolean supportsPixelSize() {
		return true;
	}

	@Override
	public BufferedImage writeImage(ImageServer<BufferedImage> server, RegionRequest request, String pathOutput) throws IOException {
		PathImage<ImagePlus> pathImage = IJTools.extractROI(server, (ROI)null, request, false, null);
		if (pathImage == null)
			throw new IOException("Unable to extract region from from " + server.getPath());
		writeImage(pathImage.getImage(), pathOutput);
		return pathImage.getImage().getBufferedImage();
	}

	@Override
	public void writeImage(BufferedImage img, String pathOutput) throws IOException {
		ImageProcessor ip;
		if (img.getSampleModel().getNumBands() == 1) {
			int type = img.getSampleModel().getDataType();
			if (type == DataBuffer.TYPE_BYTE)
				ip = new ByteProcessor(img);
			else if (type == DataBuffer.TYPE_SHORT)
				ip = new ShortProcessor(img);
			else
				ip = new FloatProcessor(img.getWidth(), img.getHeight(), img.getRaster().getPixel(0, 0, (float[])null));
		} else
			ip = new ColorProcessor(img);
		writeImage(new ImagePlus("", ip), pathOutput);
	}

	public void writeImage(ImagePlus imp, String pathOutput) throws IOException {
		IJ.save(imp, pathOutput);
	}

}
