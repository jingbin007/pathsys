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

package qupath.lib.gui.commands;

import java.awt.image.BufferedImage;

import javafx.stage.Stage;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.tma.TMASummaryViewer;
import qupath.lib.images.ImageData;

/**
 * Launch GUI for viewing exported TMA summary data.
 * 
 * @author Pete Bankhead
 *
 */
public class TMAViewerCommand implements PathCommand {
	
	@Override
	public void run() {
		QuPathGUI qupath = QuPathGUI.getInstance();
		Stage stage = new Stage();
		if (qupath != null)
			stage.initOwner(qupath.getStage());
		TMASummaryViewer tmaViewer = new TMASummaryViewer(stage);
		
		ImageData<BufferedImage> imageData = qupath.getImageData();
		if (imageData != null && imageData.getHierarchy().getTMAGrid() != null)
			tmaViewer.setTMAEntriesFromImageData(imageData);
		
		tmaViewer.getStage().show();
		
	}

}
