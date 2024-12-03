/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.roadmap;

import javax.servlet.http.HttpServletRequest;

public class RoadmapRequest {
    private static final String REQUEST_PATH_PLACEHOLDER = "placeholder";
    private long id;
    private int version;
    private String hash;
    private int width;
    private int height;
    private boolean timeline;
    private boolean isPlaceholder;

    public RoadmapRequest(HttpServletRequest req) {
        String requestPath = req.getPathInfo();
        String[] requestParams = requestPath.split("/");
        if (requestParams.length == 4) {
            this.id = Long.parseLong(requestParams[1]);
            this.version = Integer.parseInt(requestParams[2]);
            this.hash = requestParams[3].substring(0, requestParams[3].lastIndexOf(".png"));
        } else if (REQUEST_PATH_PLACEHOLDER.equalsIgnoreCase(requestParams[1])) {
            this.hash = req.getParameter("hash");
            this.width = Integer.parseInt(req.getParameter("width"));
            this.height = Integer.parseInt(req.getParameter("height"));
            this.timeline = Boolean.parseBoolean(req.getParameter("timeline"));
            this.isPlaceholder = true;
        }
    }

    public long getId() {
        return this.id;
    }

    public int getVersion() {
        return this.version;
    }

    public String getHash() {
        return this.hash;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isTimeline() {
        return this.timeline;
    }

    public boolean isPlaceholder() {
        return this.isPlaceholder;
    }
}

