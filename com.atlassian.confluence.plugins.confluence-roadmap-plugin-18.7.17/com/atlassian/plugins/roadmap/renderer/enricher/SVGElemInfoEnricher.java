/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package com.atlassian.plugins.roadmap.renderer.enricher;

import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.LaneColor;
import com.atlassian.plugins.roadmap.models.Marker;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.renderer.ConfluenceSVGGraphics2D;
import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePosition;
import com.google.gson.Gson;
import java.util.UUID;
import org.w3c.dom.Element;

public class SVGElemInfoEnricher
implements RenderedImageInfoEnricher {
    private final ConfluenceSVGGraphics2D graphics2D;
    private static final Gson GSON = new Gson();
    private Element svggOverlayDiv;
    private Element barOverlayDiv;

    public SVGElemInfoEnricher(ConfluenceSVGGraphics2D graphics2D) {
        this.graphics2D = graphics2D;
        this.svggOverlayDiv = graphics2D.getDOMFactory().createElement("div");
        this.barOverlayDiv = graphics2D.getDOMFactory().createElement("div");
    }

    private void putData(String key, Object value) {
        Element elem = this.graphics2D.getLastAddedElement();
        elem.setAttributeNS(null, key, GSON.toJson(value).replace("\"", ""));
    }

    @Override
    public void enrichBar(int x, int y, int width, int height, Bar bar) {
        this.putData("data-roadmap-bar", bar);
    }

    @Override
    public void enrichBarTitle(int x, int y, int width, int height, Bar bar, LaneColor laneColor) {
        Element title = this.graphics2D.getDOMFactory().createElement("p");
        title.setAttribute("class", "ellipsis");
        title.setTextContent(bar.getTitle());
        Element barOverlay = this.graphics2D.getDOMFactory().createElement("div");
        barOverlay.setAttribute("class", "overlay-element ellipsis bar-title");
        barOverlay.setAttribute("style", String.format("left:%spx; top:%spx; width:%spx; color:%s", x, y, width, laneColor.getText()));
        barOverlay.setAttribute("data-roadmap-bar", GSON.toJson((Object)bar));
        barOverlay.setAttribute("title", bar.getTitle());
        barOverlay.setAttribute("role", "button");
        barOverlay.setAttribute("tabindex", "0");
        barOverlay.appendChild(title);
        this.barOverlayDiv.appendChild(barOverlay);
        Element barTitleElem = this.graphics2D.getLastAddedElement();
        barTitleElem.getParentNode().removeChild(barTitleElem);
    }

    @Override
    public void enrichLane(int x, int y, int width, int height, Lane theme) {
        Element laneOverlay = this.graphics2D.getDOMFactory().createElement("div");
        laneOverlay.setAttribute("class", "overlay-element ellipsis rotate-text lane-title");
        laneOverlay.setAttribute("style", String.format("left:%spx; top:%spx; width:%spx; height:%spx; color:%s; margin-top:%spx", x, y, height, width, theme.getColor().getText(), height - width));
        laneOverlay.setAttribute("title", theme.getTitle());
        laneOverlay.setAttribute("role", "button");
        laneOverlay.setAttribute("tabindex", "0");
        laneOverlay.setTextContent(theme.getTitle());
        this.svggOverlayDiv.appendChild(laneOverlay);
        Element laneTitleElem = this.graphics2D.getLastAddedElement();
        laneTitleElem.getParentNode().removeChild(laneTitleElem);
    }

    @Override
    public void enrichColumn(int x, int y, int width, int height, TimelinePosition column) {
    }

    @Override
    public void enrichMarker(int x1, int y1, int x2, int y2, Marker marker) {
        this.putData("data-roadmap-marker", marker);
    }

    @Override
    public void enrichContainer(TimelinePlanner timelinePlanner, int width) {
        Element elem = this.graphics2D.getRoot();
        elem.setAttributeNS(null, "data-roadmap-id", UUID.randomUUID().toString());
        this.svggOverlayDiv.setAttribute("style", String.format("width:%spx", width));
        this.svggOverlayDiv.setAttribute("class", "svg-overlay");
        if (this.barOverlayDiv.hasChildNodes()) {
            this.barOverlayDiv.setAttribute("class", "svg-bar-overlay");
            this.svggOverlayDiv.appendChild(this.barOverlayDiv);
        }
        this.graphics2D.getRoot().appendChild(this.svggOverlayDiv);
    }

    @Override
    public void enrichColumnText() {
        this.putData("roadmap-column-text", "true");
    }
}

