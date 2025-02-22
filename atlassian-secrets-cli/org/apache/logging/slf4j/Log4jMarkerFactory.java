/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.slf4j.Log4jMarker;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class Log4jMarkerFactory
implements IMarkerFactory {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<String, Marker>();

    @Override
    public Marker getMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Marker name must not be null");
        }
        Marker marker = (Marker)this.markerMap.get(name);
        if (marker != null) {
            return marker;
        }
        org.apache.logging.log4j.Marker log4jMarker = MarkerManager.getMarker(name);
        return this.addMarkerIfAbsent(name, log4jMarker);
    }

    private Marker addMarkerIfAbsent(String name, org.apache.logging.log4j.Marker log4jMarker) {
        Log4jMarker marker = new Log4jMarker(log4jMarker);
        Marker existing = this.markerMap.putIfAbsent(name, marker);
        return existing == null ? marker : existing;
    }

    public Marker getMarker(Marker marker) {
        if (marker == null) {
            throw new IllegalArgumentException("Marker must not be null");
        }
        Marker m = (Marker)this.markerMap.get(marker.getName());
        if (m != null) {
            return m;
        }
        return this.addMarkerIfAbsent(marker.getName(), Log4jMarkerFactory.convertMarker(marker));
    }

    private static org.apache.logging.log4j.Marker convertMarker(Marker original) {
        if (original == null) {
            throw new IllegalArgumentException("Marker must not be null");
        }
        return Log4jMarkerFactory.convertMarker(original, new ArrayList<Marker>());
    }

    private static org.apache.logging.log4j.Marker convertMarker(Marker original, Collection<Marker> visited) {
        org.apache.logging.log4j.Marker marker = MarkerManager.getMarker(original.getName());
        if (original.hasReferences()) {
            Iterator<Marker> it = original.iterator();
            while (it.hasNext()) {
                Marker next = it.next();
                if (visited.contains(next)) {
                    LOGGER.warn("Found a cycle in Marker [{}]. Cycle will be broken.", (Object)next.getName());
                    continue;
                }
                visited.add(next);
                marker.addParents(Log4jMarkerFactory.convertMarker(next, visited));
            }
        }
        return marker;
    }

    @Override
    public boolean exists(String name) {
        return this.markerMap.containsKey(name);
    }

    @Override
    public boolean detachMarker(String name) {
        return false;
    }

    @Override
    public Marker getDetachedMarker(String name) {
        LOGGER.warn("Log4j does not support detached Markers. Returned Marker [{}] will be unchanged.", (Object)name);
        return this.getMarker(name);
    }
}

