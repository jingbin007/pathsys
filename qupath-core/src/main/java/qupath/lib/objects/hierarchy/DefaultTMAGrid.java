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

package qupath.lib.objects.hierarchy;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qupath.lib.objects.TMACoreObject;
import qupath.lib.roi.ROIHelpers;

/**
 * Default implementation of a TMAGrid.
 * 
 * @author Pete Bankhead
 *
 */
public class DefaultTMAGrid implements TMAGrid {
	
	private static final long serialVersionUID = 1L;
	
	private final static Logger logger = LoggerFactory.getLogger(DefaultTMAGrid.class);
	
	private List<TMACoreObject> cores = new ArrayList<>();
	private int gridWidth = -1;
	private int gridHeight = -1;
	
	public DefaultTMAGrid(List<TMACoreObject> cores, int gridWidth) {
		this.cores.addAll(cores);
		this.gridWidth = gridWidth;
		this.gridHeight = cores.size() / gridWidth;
	}

	@Override
	public int getCoreIndex(String coreName) {
		int ind = 0;
		for (TMACoreObject core : cores) {
			String name = core.getName();
			if (coreName == null) {
				if (name == null)
					return ind;
			} else if (coreName.equals(name))
				return ind;
			ind++;
		}
		return -1;
	}

	@Override
	public int nCores() {
		return cores.size();
	}
	
	public int getNMissingCores() {
		int missing = 0;
		for (TMACoreObject core : cores)
			if (core.isMissing())
				missing++;
		return missing;
	}

	@Override
	public int getGridWidth() {
		return gridWidth;
	}

	@Override
	public int getGridHeight() {
		return gridHeight;
	}

	@Override
	public TMACoreObject getTMACore(int ind) {
		return cores.get(ind);
	}

	@Override
	public TMACoreObject getTMACore(int row, int col) {
		return cores.get(row * gridWidth + col);
	}

	@Override
	public List<TMACoreObject> getTMACoreList() {
		ArrayList<TMACoreObject> list = new ArrayList<>();
		list.addAll(cores);
		return list;
	}

	@Override
	public TMACoreObject getTMACoreForPixel(double x, double y) {
		// TODO: Consider overlapping cores - would be slightly nicer to return core with closest centroid
		for (TMACoreObject core : cores) {
			if (ROIHelpers.areaContains(core.getROI(), x, y))
				return core;
		}
		return null;
	}

	@Override
	public TMACoreObject getTMACore(String coreName) {
		// We can't match a null coreName
		if (coreName == null) {
			logger.warn("Cannot find match to unnammed TMA core!");
			return null;
		}
		for (TMACoreObject core : cores) {
			if (coreName.equals(core.getName()))
				return core;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "TMA Grid: " + nCores() + " cores ("+ getGridWidth() + " x " + getGridHeight() + "), " + getNMissingCores() + " missing";
	}

	@Override
	public TMACoreObject getTMACoreByUniqueID(String uniqueID) {
		for (TMACoreObject core : cores) {
			if (uniqueID.equals(core.getUniqueID()))
				return core;
		}
		return null;
	}

}
