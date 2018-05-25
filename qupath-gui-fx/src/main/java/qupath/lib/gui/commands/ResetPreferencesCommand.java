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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import qupath.lib.gui.commands.interfaces.PathCommand;
import qupath.lib.gui.helpers.DisplayHelpers;
import qupath.lib.gui.prefs.PathPrefs;

/**
 * Command to reset all preferences in PathPrefs to their default states.
 * 
 * @author Pete Bankhead
 *
 */
public class ResetPreferencesCommand implements PathCommand {
	
	private static Logger logger = LoggerFactory.getLogger(ResetPreferencesCommand.class);

	@Override
	public void run() {
		if (DisplayHelpers.showConfirmDialog("重置设置", "是否确认全部设置?\n\n您可以重启系统后启用设置.")) {
			PathPrefs.resetPreferences();
		}
		else
			logger.info("忽略设置命令!");
	}

}
