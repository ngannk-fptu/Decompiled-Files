/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws;

import com.amazonaws.annotation.NotThreadSafe;
import java.util.EnumMap;

@NotThreadSafe
public final class RequestClientOptions {
    public static final int DEFAULT_STREAM_BUFFER_SIZE = 131073;
    private final EnumMap<Marker, String> markers = new EnumMap(Marker.class);
    private int readLimit = 131073;
    private boolean skipAppendUriPath = false;

    public String getClientMarker(Marker marker) {
        return this.markers.get((Object)marker);
    }

    public void putClientMarker(Marker marker, String value) {
        this.markers.put(marker, value);
    }

    public void appendUserAgent(String userAgent) {
        String marker = this.markers.get((Object)Marker.USER_AGENT);
        if (marker == null) {
            marker = "";
        }
        marker = this.createUserAgentMarkerString(marker, userAgent);
        this.putClientMarker(Marker.USER_AGENT, marker);
    }

    private String createUserAgentMarkerString(String marker, String userAgent) {
        return marker.contains(userAgent) ? marker : marker + " " + userAgent;
    }

    public final int getReadLimit() {
        return this.readLimit;
    }

    public final void setReadLimit(int readLimit) {
        this.readLimit = readLimit;
    }

    public boolean isSkipAppendUriPath() {
        return this.skipAppendUriPath;
    }

    public void setSkipAppendUriPath(boolean skipAppendUriPath) {
        this.skipAppendUriPath = skipAppendUriPath;
    }

    void copyTo(RequestClientOptions target) {
        target.setReadLimit(this.getReadLimit());
        target.setSkipAppendUriPath(this.isSkipAppendUriPath());
        for (Marker marker : Marker.values()) {
            target.putClientMarker(marker, this.getClientMarker(marker));
        }
    }

    public static enum Marker {
        USER_AGENT;

    }
}

