/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.Sets
 */
package com.atlassian.plugins.roadmap.renderer;

import com.atlassian.plugins.roadmap.FontUtils;
import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.LaneColor;
import com.atlassian.plugins.roadmap.models.Marker;
import com.atlassian.plugins.roadmap.models.Timeline;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.plugins.roadmap.placeholder.PlaceholderImageFactory;
import com.atlassian.plugins.roadmap.renderer.DrawingParams;
import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePosition;
import com.atlassian.plugins.roadmap.renderer.beans.TimelinePositionTitle;
import com.atlassian.plugins.roadmap.renderer.helper.TimeLineColorHelper;
import com.atlassian.plugins.roadmap.renderer.helper.TimeLineHelper;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Sets;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

abstract class AbstractTimelinePlannerRenderer {
    private I18nResolver i18n;
    private static final Font loadedFont = AbstractTimelinePlannerRenderer.loadFont();
    private static final Color COLOR_TEXT = new Color(0x707070);
    private static final Color COLOR_BORDER = new Color(0xD1D1D1);
    private static final Color COLOR_BACK_COLUMN = new Color(0xF5F5F5);
    private static final Color COLOR_ROADMAP_BASE = new Color(0xFFFFFF);
    private static final Color COLOR_MARKER = new Color(13648951);
    private static final Font FONT_TITLE = loadedFont.deriveFont(0, 20.0f);
    private static final Font FONT_COLUMNS = loadedFont.deriveFont(0, 13.0f);
    private static final Font FONT_COLUMN_YEAR = loadedFont.deriveFont(1, 13.0f);
    private static final Font FONT_LANES = loadedFont.deriveFont(1, 13.0f);
    private static final Font FONT_BARS = loadedFont.deriveFont(1, 12.0f);
    private static final Font FONT_MARKERS = loadedFont.deriveFont(0, 14.0f);
    private static final Stroke STROKE_MARKER = new BasicStroke(1.5f);
    private static final Stroke STROKE_COLUMN_LINE = new BasicStroke(0.3f, 1, 1, 1.0f, new float[]{3.5f, 6.5f}, 0.0f);
    protected static final int PLACEHOLDER_HEIGHT = 30;
    protected static final int ROADMAP_MONTH_COLUMN_WIDTH = 100;
    protected static final int ROADMAP_WEEK_COLUMN_WIDTH = 100;
    protected static final int MIN_LANE_HEIGHT = 97;
    private static final int MARGIN_TITLE = 20;
    private static final int MARGIN_LANE = 10;
    private static final int PADDING_LANE_TITLE = 10;
    private static final int MARGIN_TOP_COLUMNS = 5;
    private static final int MARGIN_BAR = 8;
    private static final int MARGIN_BAR_HORIZONTAL = 1;
    private static final int MARGIN_MARKER_LINE = 10;
    private static final int MARGIN_MARKER = 15;
    private static final int MARKER_TITLE_WIDTH = 100;
    private static final int MARKER_TITLE_LINE = 2;
    private static final int MARGIN_TOP = 30;
    private static final int CORNER_SIZE_LANE = 0;
    private static final int CORNER_SIZE_BAR = 4;
    private static final int SIZE_HEIGHT_COLUMN = 40;
    private static final int SIZE_HEIGHT_BAR = 37;
    private final FontMetrics fmTitle;
    private final FontMetrics fmCols;
    private final FontMetrics fmTheme;
    private final FontMetrics fmTask;
    private final FontMetrics fmMarker;

    private static Font loadFont() {
        return new Font("SansSerif", 0, 20);
    }

    protected abstract Graphics2D createDummyGraphics2D();

    protected abstract Graphics2D createGraphics2D(int var1, int var2);

    protected abstract RenderedImageInfoEnricher createEnricher();

    public void setI18n(I18nResolver i18n) {
        this.i18n = i18n;
    }

    protected AbstractTimelinePlannerRenderer() {
        Graphics2D dummyG2 = this.createDummyGraphics2D();
        this.fmTitle = dummyG2.getFontMetrics(FONT_TITLE);
        this.fmCols = dummyG2.getFontMetrics(FONT_COLUMNS);
        this.fmTheme = dummyG2.getFontMetrics(FONT_LANES);
        this.fmTask = dummyG2.getFontMetrics(FONT_BARS);
        this.fmMarker = dummyG2.getFontMetrics(FONT_MARKERS);
        dummyG2.dispose();
    }

    protected void drawImage(TimelinePlanner timelinePlanner, Optional<Integer> widthOption, Optional<Integer> heightOption, boolean isPlaceholder) throws IOException {
        Graphics2D g2;
        int finalHeight;
        int wTheme = this.fmTheme.getHeight() + 20;
        int wTitle = 20;
        int wTitleTheme = wTitle + wTheme;
        if (timelinePlanner.getTimeline().getDisplayOption() == Timeline.DisplayOption.MONTH) {
            this.correctTimelineBoundary(timelinePlanner.getTimeline());
        }
        int wColumns = this.getTimelineWidth(timelinePlanner.getTimeline());
        int hRoadmap = this.getRoadmapHeight(timelinePlanner.getLanes());
        int wRoadmap = wColumns + wTitleTheme;
        int realHeight = hRoadmap + 40 + 10 + 30 + this.fmMarker.getHeight() + 30 + this.fmMarker.getHeight() * 2;
        int realWidth = wRoadmap + 50;
        int finalWidth = widthOption.isPresent() ? Math.min(realWidth, widthOption.get()) : realWidth;
        int n = finalHeight = heightOption.isPresent() ? Math.min(realHeight, heightOption.get()) : realHeight;
        if (isPlaceholder) {
            g2 = this.createGraphics2D(finalWidth, finalHeight + 30);
            PlaceholderImageFactory.drawPlaceholderImage(g2, loadedFont, this.i18n);
            g2.translate(0, 30);
        } else {
            g2 = this.createGraphics2D(finalWidth, finalHeight);
        }
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, finalWidth, finalHeight);
        RenderedImageInfoEnricher enricher = this.createEnricher();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Stroke origStroke = g2.getStroke();
        AffineTransform origTransform = g2.getTransform();
        DrawingParams params = new DrawingParams();
        params.enricher = enricher;
        params.fmCols = this.fmCols;
        params.fmMarker = this.fmMarker;
        params.fmTask = this.fmTask;
        params.fmTheme = this.fmTheme;
        params.fmTitle = this.fmTitle;
        params.hRoadmap = hRoadmap;
        params.wRoadmap = wRoadmap;
        params.origTransform = origTransform;
        params.origStroke = origStroke;
        params.realHeight = realHeight;
        params.wColumns = wColumns;
        params.wTheme = wTheme;
        params.wTitle = wTitle;
        params.wTitleTheme = wTitleTheme;
        this.drawTimeline(g2, timelinePlanner.getTimeline(), params);
        this.drawMarkers(g2, timelinePlanner, params);
        this.drawLanes(g2, timelinePlanner, params);
        this.drawTimelineBorder(g2, timelinePlanner.getTimeline(), params);
        enricher.enrichContainer(timelinePlanner, params.wRoadmap);
        if (isPlaceholder) {
            g2.translate(0, 0);
        }
    }

    private void correctTimelineBoundary(Timeline timeline) {
        Calendar calendarStartDate = Calendar.getInstance();
        calendarStartDate.setTime(timeline.getStartDate());
        calendarStartDate.set(5, 1);
        calendarStartDate.set(11, 0);
        calendarStartDate.set(12, 0);
        calendarStartDate.set(13, 0);
        calendarStartDate.set(14, 0);
        Calendar calendarEndDate = Calendar.getInstance();
        calendarEndDate.setTime(timeline.getEndDate());
        calendarEndDate.set(5, calendarEndDate.getActualMaximum(5));
        calendarEndDate.set(11, 23);
        calendarEndDate.set(12, 59);
        calendarEndDate.set(13, 59);
        calendarEndDate.set(14, 999);
        timeline.setStartDate(calendarStartDate.getTime());
        timeline.setEndDate(calendarEndDate.getTime());
    }

    private void drawTitle(Graphics2D g2, TimelinePlanner timelinePlanner, DrawingParams params) {
        g2.setColor(COLOR_ROADMAP_BASE);
        g2.fillRect(0, 0, params.wTitleTheme, params.realHeight - (45 + params.fmMarker.getHeight() * 2 + 1));
        g2.fillRect(params.wRoadmap, 0, 50, params.realHeight - (45 + params.fmMarker.getHeight() * 2 + 1));
    }

    private void drawTimeline(Graphics2D g2, Timeline timeline, DrawingParams params) {
        HashSet yearStacks = Sets.newHashSet();
        List<TimelinePosition> columns = TimeLineHelper.getColumnPosition(timeline);
        g2.setColor(COLOR_BACK_COLUMN);
        g2.setStroke(STROKE_COLUMN_LINE);
        int colXPos = params.wTitleTheme;
        int columnWidth = this.getColumnWidth(timeline);
        for (TimelinePosition timelinePosition : columns) {
            g2.setFont(FONT_COLUMNS);
            g2.setColor(COLOR_TEXT);
            g2.drawLine(colXPos, 70, colXPos, 40 + params.hRoadmap + 30);
            params.enricher.enrichColumn(colXPos, 41, columnWidth, params.hRoadmap - 1, timelinePosition);
            TimelinePositionTitle columnTitle = TimeLineHelper.getPositionTitle(timeline, timelinePosition, this.i18n);
            g2.setColor(COLOR_TEXT);
            Rectangle2D boundsCol = params.fmCols.getStringBounds(columnTitle.getMonth(), g2);
            g2.drawString(columnTitle.getMonth(), colXPos + (int)((double)columnWidth - boundsCol.getWidth()) / 2, (int)boundsCol.getHeight() + 5 + 30);
            String year = columnTitle.getYear();
            if (!yearStacks.contains(year)) {
                g2.setFont(FONT_COLUMN_YEAR);
                yearStacks.add(year);
                Rectangle2D yearBoundsCol = params.fmCols.getStringBounds(year, g2);
                g2.drawString(year, colXPos + (int)((double)columnWidth - yearBoundsCol.getWidth()) / 2, 35);
            }
            params.enricher.enrichColumnText();
            colXPos += columnWidth;
        }
        g2.setTransform(params.origTransform);
    }

    private void drawTimelineBorder(Graphics2D g2, Timeline timeline, DrawingParams params) {
        g2.setStroke(new BasicStroke());
        g2.setColor(COLOR_BORDER);
        g2.drawLine(params.wRoadmap, 70, params.wRoadmap, params.hRoadmap + 40 + 30);
    }

    private void drawMarkers(Graphics2D g2, TimelinePlanner timelinePlanner, DrawingParams params) {
        g2.setFont(FONT_MARKERS);
        g2.setStroke(STROKE_MARKER);
        for (Marker marker : timelinePlanner.getMarkers()) {
            TimelinePosition markerPosition = TimeLineHelper.calculateTimelinePosition(timelinePlanner.getTimeline(), marker.getMarkerDate());
            int xPos = params.wTitleTheme + this.getXFromColumnPosition(timelinePlanner.getTimeline(), markerPosition.getColumn(), markerPosition.getOffset());
            if (xPos < params.wTitleTheme || xPos > params.wRoadmap) continue;
            g2.setColor(COLOR_MARKER);
            int hMarkerLine = params.hRoadmap + 10 + 40;
            g2.drawLine(xPos, 70, xPos, hMarkerLine + 30);
            params.enricher.enrichMarker(xPos, 40, xPos, hMarkerLine, marker);
            Rectangle2D boundsMarker = params.fmMarker.getStringBounds(marker.getTitle(), g2);
            int yPos = hMarkerLine + 15 + (int)boundsMarker.getHeight();
            String[] markerTexts = FontUtils.wrap(marker.getTitle(), 100, g2.getFontMetrics(), 2);
            int markerTextHeight = g2.getFontMetrics().getHeight();
            for (String markerText : markerTexts) {
                Rectangle2D boundsMarkerText = params.fmMarker.getStringBounds(markerText.trim(), g2);
                g2.drawString(markerText, Math.max(0, xPos - (int)(boundsMarkerText.getWidth() / 2.0)), yPos += markerTextHeight);
            }
        }
        g2.setStroke(params.origStroke);
    }

    private void drawLanes(Graphics2D g2, TimelinePlanner r, DrawingParams params) {
        this.drawLaneAndBar(g2, r, params);
        this.drawTitle(g2, r, params);
        this.drawLaneTitle(g2, r, params);
        this.drawLaneBorder(g2, r, params);
    }

    private void drawLaneAndBar(Graphics2D g2, TimelinePlanner r, DrawingParams params) {
        g2.setFont(FONT_BARS);
        int laneYPos = 40;
        for (Lane lane : r.getLanes()) {
            for (Bar bar : lane.getBars()) {
                params.themeYPos = laneYPos;
                this.drawBar(g2, r, bar, lane.getColor(), params);
            }
            laneYPos += this.getLaneHeight(lane);
        }
    }

    private void drawLaneTitle(Graphics2D g2, TimelinePlanner r, DrawingParams params) {
        int laneYPos = 70;
        for (Lane lane : r.getLanes()) {
            Color colorLane = TimeLineColorHelper.decodeColor(lane.getColor().getLane());
            int hTheme = this.getLaneHeight(lane);
            int laneTitleWidth = params.wTitle - 10;
            g2.setFont(FONT_LANES);
            g2.setColor(colorLane);
            g2.fillRoundRect(laneTitleWidth, laneYPos, params.wTheme, hTheme, 0, 0);
            g2.setColor(COLOR_BORDER);
            g2.drawRoundRect(laneTitleWidth, laneYPos, params.wTheme, hTheme, 0, 0);
            Rectangle2D boundsTheme = params.fmTheme.getStringBounds(lane.getTitle(), g2);
            g2.setFont(FONT_LANES);
            g2.setColor(TimeLineColorHelper.decodeColor(lane.getColor().getText()));
            g2.rotate(-1.5707963267948966);
            int yPos = (int)(-((double)hTheme + Math.min(boundsTheme.getWidth(), (double)(hTheme - 20)))) / 2 - laneYPos;
            int xPos = laneTitleWidth + (int)boundsTheme.getHeight() + 10 - 1;
            Rectangle rectangle = new Rectangle(params.wTitle, laneYPos, params.wTheme, hTheme - 5);
            String drawTitle = FontUtils.cutTextInBox(lane.getTitle(), rectangle, FONT_LANES, g2, 10, true);
            g2.drawString(drawTitle, yPos, xPos);
            params.enricher.enrichLane(laneTitleWidth, laneYPos, params.wTheme, hTheme, lane);
            g2.setTransform(params.origTransform);
            laneYPos += hTheme;
        }
    }

    private void drawLaneBorder(Graphics2D g2, TimelinePlanner r, DrawingParams params) {
        int laneYPos = 70;
        g2.setColor(COLOR_BORDER);
        for (Lane lane : r.getLanes()) {
            int hTheme = this.getLaneHeight(lane);
            g2.drawLine(params.wTitle, laneYPos, params.wTitle + this.getTimelineWidth(r.getTimeline()) + params.wTheme, laneYPos);
            laneYPos += hTheme;
        }
        g2.drawLine(params.wTitle, laneYPos, params.wTitle + this.getTimelineWidth(r.getTimeline()) + params.wTheme, laneYPos);
    }

    private void drawBar(Graphics2D g2, TimelinePlanner r, Bar bar, LaneColor laneColor, DrawingParams params) {
        TimelinePosition barStartPosition = TimeLineHelper.calculateTimelinePosition(r.getTimeline(), bar.getStartDate());
        int xPos = params.wTitleTheme + this.getXFromColumnPosition(r.getTimeline(), barStartPosition.getColumn(), barStartPosition.getOffset()) + 1;
        int yPos = params.themeYPos + bar.getRowIndex() * 37 + (bar.getRowIndex() + 1) * 8 + 30;
        int wTask = params.wTitleTheme + this.getXFromColumnPosition(r.getTimeline(), barStartPosition.getColumn(), barStartPosition.getOffset() + bar.getDuration()) - xPos - 1;
        Color colorText = TimeLineColorHelper.decodeColor(laneColor.getText());
        Color colorBar = TimeLineColorHelper.decodeColor(laneColor.getBar());
        Color colorLane = TimeLineColorHelper.decodeColor(laneColor.getLane());
        g2.setColor(colorBar);
        g2.fillRoundRect(xPos, yPos, wTask, 37, 4, 4);
        g2.setColor(colorLane);
        g2.drawRoundRect(xPos, yPos, wTask, 37, 4, 4);
        params.enricher.enrichBar(xPos, yPos, wTask, 37, bar);
        g2.setColor(colorText);
        g2.setFont(FONT_BARS);
        Rectangle taskBoundRectangle = new Rectangle(xPos, yPos, wTask, 37);
        String drawTitle = FontUtils.cutTextInBox(bar.getTitle(), taskBoundRectangle, FONT_BARS, g2, 20, false);
        Rectangle2D boundsTask = params.fmTask.getStringBounds(drawTitle, g2);
        g2.drawString(drawTitle, xPos + (int)((double)wTask - boundsTask.getWidth()) / 2, (int)((double)yPos + (boundsTask.getHeight() + 37.0) / 2.0 - (double)params.fmTask.getDescent() + 1.0));
        params.enricher.enrichBarTitle(xPos, yPos, wTask, 37, bar, laneColor);
        g2.setTransform(params.origTransform);
    }

    private int getTimelineWidth(Timeline timeline) {
        int columnWidth = this.getColumnWidth(timeline);
        return columnWidth * TimeLineHelper.getNumberOfColumnInTimeline(timeline);
    }

    private int getXFromColumnPosition(Timeline timeline, Integer columnIndex, double columnOffset) {
        int columnWidth = this.getColumnWidth(timeline);
        return columnWidth * columnIndex + (int)((double)columnWidth * columnOffset);
    }

    private int getRoadmapHeight(List<Lane> lanes) {
        int height = 0;
        for (Lane lane : lanes) {
            height += this.getLaneHeight(lane);
        }
        return height;
    }

    private int getLaneHeight(Lane lane) {
        int nRows = 0;
        for (Bar bar : lane.getBars()) {
            nRows = Math.max(bar.getRowIndex(), nRows);
        }
        int hTheme = (nRows + 1) * 37 + (nRows + 2) * 8;
        return Math.max(hTheme, 97);
    }

    private int getColumnWidth(Timeline timeline) {
        return timeline.getDisplayOption() == Timeline.DisplayOption.MONTH ? 100 : 100;
    }
}

