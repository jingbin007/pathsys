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

package qupath.lib.gui.panels;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import qupath.lib.gui.ImageDataChangeListener;
import qupath.lib.gui.ImageDataWrapper;
import qupath.lib.gui.QuPathGUI;
import qupath.lib.gui.prefs.PathPrefs;
import qupath.lib.images.ImageData;
import qupath.lib.images.servers.ImageServer;

/**
 * A simple viewer for a slide label, tied to the current viewer.
 * 
 * @author Pete Bankhead
 *
 */
public class SlideLabelView implements ImageDataChangeListener<BufferedImage> {
	
	private static Logger logger = LoggerFactory.getLogger(SlideLabelView.class);
	
	private QuPathGUI qupath;
	private Stage dialog;
	private BooleanProperty showing = PathPrefs.createPersistentPreference("showSlideLabel", false);
	private BorderPane pane = new BorderPane();
	
	public SlideLabelView(final QuPathGUI qupath) {
		this.qupath = qupath;
		createDialog();
		qupath.addImageDataChangeListener(this);
	}
	
	private void createDialog() {
		dialog = new Stage();
		dialog.initOwner(qupath.getStage());
		dialog.setTitle("Label");
		dialog.setScene(new Scene(pane, 400, 400));
		
		showing.addListener((v, o, n) -> {
			if (n) {
				if (!dialog.isShowing())
					dialog.show();
			} else {
				if (dialog.isShowing())
					dialog.hide();
			}
		});
		
		
		dialog.showingProperty().addListener((v, o, n) -> {
			if (n)
				updateLabel(qupath.getImageData());
			showing.set(n);
		});
		
		
		if (showing.get()) {
			Platform.runLater(() -> dialog.show());
		}
	}
	
	public BooleanProperty showingProperty() {
		return showing;
	}

	@Override
	public void imageDataChanged(ImageDataWrapper<BufferedImage> source, ImageData<BufferedImage> imageDataOld, ImageData<BufferedImage> imageDataNew) {
		updateLabel(imageDataNew);
	}
	
	private void updateLabel(final ImageData<BufferedImage> imageData) {
		if (dialog == null || !dialog.isShowing())
			return;
		
		// Try to get a label image
		Image imgLabel = null;
		if (imageData != null) {
			ImageServer<BufferedImage> server = imageData.getServer();
			if (server != null) {
				for (String name : server.getAssociatedImageList()) {
					if ("label".equals(name.toLowerCase().trim())) {
						try {
							imgLabel = SwingFXUtils.toFXImage(server.getAssociatedImage(name), null);
							break;
						} catch (Exception e) {
							logger.error("Unable to read label {} from {}", name, server.getPath());
						}
					}
				}
			}
		}

		// Update the component
		if (imgLabel == null)
			pane.setCenter(new Label("No label available"));
		else {
			ImageView view = new ImageView(imgLabel);
			view.setPreserveRatio(true);
			view.fitWidthProperty().bind(pane.widthProperty());
			view.fitHeightProperty().bind(pane.heightProperty());
			pane.setCenter(view);
		}
	}

}
