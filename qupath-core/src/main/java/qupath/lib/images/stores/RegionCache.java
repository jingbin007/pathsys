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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import qupath.lib.images.servers.ImageServer;
import qupath.lib.regions.RegionRequest;

/**
 * Map for storing image tiles, which automatically removes tiles that have not been accessed
 * in a while after it reaches a maximum number of tiles, or maximum memory consumption.
 * 
 * The memory consumption estimate is based on the bit-depth of the image & number of pixels it contains
 * (other overhead is ignored).
 * 
 * @author Pete Bankhead
 *
 */
public class RegionCache<T> {

	private LinkedHashMap<RegionRequest, T> map;
	private final SizeEstimator<T> sizeEstimator;
	private int maxCapacity;
	private int nonNullSize = 0;
	private long maxMemoryBytes;
	private long memoryBytes = 0; // Rely on synchronization to control access to map anyway, so no need for atomic...?

	RegionCache(final SizeEstimator<T> sizeEstimator, final int maxCapacity, final long maxSizeBytes) {
		this.maxMemoryBytes = maxSizeBytes;
		this.sizeEstimator = sizeEstimator;
		this.maxCapacity = maxCapacity;
		map = new LinkedHashMap<RegionRequest, T>(maxCapacity+1, 2f, true) {

			private static final long serialVersionUID = 1L;

			@Override
			protected synchronized boolean removeEldestEntry(Map.Entry<RegionRequest, T> eldest) {
				// Remove if the map is full (in terms of numbers), or occupying too much memory
				boolean doRemove = nonNullSize >= maxCapacity || memoryBytes > maxMemoryBytes;
				if (doRemove) {
					memoryBytes = memoryBytes - sizeEstimator.getApproxImageSize(eldest.getValue());
					if (eldest.getValue() != null)
						nonNullSize--;
//					if (getApproxImageSize(eldest.getValue()) > 10784000)
//											logger.info(String.format("REMOVED! %.2f MB remaining, %d images", memoryBytes/(1024. * 1024.), size()));
				}
				return doRemove;
			}

		}; // Should never have to resize the cache, so loadfactor is > 1
		//			logger.info("Max capacity: " + maxCapacity);
		//			logger.info("Max size: " + maxSizeBytes);
	}

	RegionCache(final SizeEstimator<T> sizeEstimator, long maxSizeBytes) {
		this(sizeEstimator, Math.max(200, (int)(maxSizeBytes / (256 * 256 * 4) + 10)), maxSizeBytes);
	}

	synchronized void clearCacheForServer(ImageServer<?> server) {
		Iterator<Entry<RegionRequest, T>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<RegionRequest, T> entry = iter.next();
			if (entry.getKey().getPath().equals(server.getPath())) {
				memoryBytes -= sizeEstimator.getApproxImageSize(entry.getValue());
				if (entry.getValue() != null)
					nonNullSize--;
				iter.remove();
			}
		}
	}
	
	
	synchronized void clearCacheForRequestOverlap(RegionRequest request) {
		Iterator<Entry<RegionRequest, T>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<RegionRequest, T> entry = iter.next();
			if (request.overlapsRequest(entry.getKey())) {
				memoryBytes -= sizeEstimator.getApproxImageSize(entry.getValue());
				if (entry.getValue() != null)
					nonNullSize--;
				iter.remove();
			}
		}
	}

	synchronized T put(RegionRequest request, T img) {
		// Update the memory requirements
		T imgPrevious = map.put(request, img);
		if (img != null) {
			memoryBytes += sizeEstimator.getApproxImageSize(img);
			nonNullSize++;
		}
		if (imgPrevious != null) {
			memoryBytes -= sizeEstimator.getApproxImageSize(imgPrevious);
			nonNullSize--;
		}
//		else
//			System.out.println("PUTTING NEW: " + nonNullSize + ", " + request + ", " + Thread.currentThread());
		return imgPrevious;
	}
	
	public synchronized boolean containsKey(RegionRequest request) {
		return map.containsKey(request);
	}

	synchronized T get(RegionRequest request) {
		return map.get(request);
	}

	synchronized void clear() {
		memoryBytes = 0;
		nonNullSize = 0;
		map.clear();
	}
	
	
	@Override
	public String toString() {
		return String.format("Cache: %d (%d/%d non-null), %s", map.size(), nonNullSize, maxCapacity, map.toString());
	}
	

}
