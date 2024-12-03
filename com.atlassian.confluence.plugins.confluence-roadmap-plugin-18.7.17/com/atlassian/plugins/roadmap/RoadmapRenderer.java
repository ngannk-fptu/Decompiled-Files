/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.gson.Gson
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.plugins.roadmap.beans.Roadmap;
import com.atlassian.plugins.roadmap.beans.RoadmapColumn;
import com.atlassian.plugins.roadmap.beans.RoadmapMarker;
import com.atlassian.plugins.roadmap.beans.RoadmapTask;
import com.atlassian.plugins.roadmap.beans.RoadmapTheme;
import com.atlassian.plugins.roadmap.placeholder.PlaceholderImageFactory;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.gson.Gson;
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
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RoadmapRenderer {
    private static final Logger logger = LoggerFactory.getLogger(RoadmapRenderer.class);
    private static final Font loadedFont = RoadmapRenderer.loadFont();
    private static final Color COLOR_TEXT = new Color(0x707070);
    private static final Color COLOR_BORDER = new Color(0xD1D1D1);
    private static final Color COLOR_TEXT_TASK_LIGHT = new Color(0xF5F5F5);
    private static final Color COLOR_TEXT_TASK_DARK = new Color(0x333333);
    private static final Color COLOR_BACK_COLUMN_ODD = new Color(0xF5F5F5);
    private static final Color COLOR_BACK_COLUMN_EVEN = new Color(0xE8E8E8);
    private static final Font FONT_TITLE = loadedFont.deriveFont(0, 20.0f);
    private static final Font FONT_COLUMNS = loadedFont.deriveFont(0, 16.0f);
    private static final Font FONT_THEMES = loadedFont.deriveFont(1, 13.0f);
    private static final Font FONT_TASKS = loadedFont.deriveFont(1, 13.0f);
    private static final Font FONT_MARKERS = loadedFont.deriveFont(1, 13.0f);
    private static final Stroke STROKE_MARKER = new BasicStroke(2.0f);
    private static final Stroke STROKE_COLUMN_LINE = new BasicStroke(1.0f, 1, 1, 1.0f, new float[]{1.0f, 4.0f}, 0.0f);
    private static final int MARGIN_TITLE = 15;
    private static final int MARGIN_THEME = 5;
    private static final int MARGIN_THEME_VERTICAL = 15;
    private static final int MARGIN_TOP_COLUMNS = 10;
    private static final int MARGIN_TASK = 10;
    private static final int MARGIN_TASK_HORIZONTAL = 5;
    private static final int MARGIN_MARKER_LINE = 20;
    private static final int MARGIN_MARKER = 5;
    private static final int CORNER_SIZE_THEME = 0;
    private static final int CORNER_SIZE_TASK = 10;
    private static final int SIZE_HEIGHT_COLUMN = 40;
    private static final int SIZE_HEIGHT_TASK = 40;
    private static final int PADDING_TASK = 5;
    private static final String ELLIPSIS = "...";
    protected static final int PLACEHOLDER_HEIGHT = 30;

    RoadmapRenderer() {
    }

    private static final Font loadFont() {
        return new Font("SansSerif", 0, 8);
    }

    public static BufferedImage drawImage(String roadmap, Optional<Integer> widthOption, Optional<Integer> heightOption, boolean isPlaceholder, I18nResolver i18nResolver) {
        Gson gson = new Gson();
        return RoadmapRenderer.drawImage((Roadmap)gson.fromJson(roadmap, Roadmap.class), widthOption, heightOption, isPlaceholder, i18nResolver);
    }

    public static BufferedImage drawImage(Roadmap r, Optional<Integer> widthOption, Optional<Integer> heightOption, boolean isPlaceholder, I18nResolver i18nResolver) {
        HashMap<String, Integer> columnWidthsInc = new HashMap<String, Integer>();
        int prevWidth = 0;
        for (RoadmapColumn column : r.columns) {
            int newWidth = column.width + prevWidth;
            columnWidthsInc.put(column.id, newWidth);
            prevWidth = newWidth;
        }
        BufferedImage result = new BufferedImage(1, 1, 2);
        Graphics2D g2 = result.createGraphics();
        FontMetrics fmTitle = g2.getFontMetrics(FONT_TITLE);
        FontMetrics fmCols = g2.getFontMetrics(FONT_COLUMNS);
        FontMetrics fmTheme = g2.getFontMetrics(FONT_THEMES);
        FontMetrics fmTask = g2.getFontMetrics(FONT_TASKS);
        FontMetrics fmMarker = g2.getFontMetrics(FONT_MARKERS);
        Rectangle2D boundsTitle = fmTitle.getStringBounds(r.title, g2);
        int wTheme = fmTheme.getHeight() + 10;
        int wTitle = (int)boundsTitle.getHeight() + 30;
        int wTitleTheme = wTitle + wTheme;
        int wColumns = (Integer)columnWidthsInc.get(r.columns.get((int)(r.columns.size() - 1)).id);
        int hRoadmap = 0;
        int[] themeHeights = new int[r.themes.size()];
        for (int i = 0; i < r.themes.size(); ++i) {
            int hThemeTitle;
            RoadmapTheme theme = r.themes.get(i);
            int taskSize = theme.tasks.size();
            int hTheme = taskSize * 40 + (taskSize + 2) * 10;
            if (hTheme < (hThemeTitle = fmTheme.stringWidth(theme.title) + 30)) {
                hTheme = hThemeTitle;
            }
            hRoadmap += hTheme;
            themeHeights[i] = hTheme;
        }
        int wRoadmap = wColumns + wTitleTheme;
        int rightMargin = 0;
        for (RoadmapMarker marker : r.markers) {
            Rectangle2D boundsMarker;
            int halfWidth;
            int xPos = wTitleTheme + RoadmapRenderer.getXFromColPos(r, columnWidthsInc, marker.columnid, marker.columnpos);
            if (xPos + (halfWidth = (int)((boundsMarker = fmMarker.getStringBounds(marker.title, g2)).getWidth() / 2.0)) <= wRoadmap) continue;
            rightMargin = Math.max(rightMargin, halfWidth);
        }
        int realWidth = wRoadmap + rightMargin;
        int realHeight = hRoadmap + 40 + 20 + 10 + fmMarker.getHeight();
        g2.dispose();
        int finalWidth = (widthOption.isPresent() ? Math.min(realWidth, widthOption.get()) : realWidth) + 1;
        int finalHeight = (heightOption.isPresent() ? Math.min(realHeight, heightOption.get()) : realHeight) + 1;
        Rectangle roadmapClip = g2.getClipBounds();
        int taskEllipsisWidth = fmTask.stringWidth(ELLIPSIS);
        if (isPlaceholder) {
            result = new BufferedImage(finalWidth, finalHeight + 30, 2);
            g2 = result.createGraphics();
            PlaceholderImageFactory.drawPlaceholderImage(g2, loadedFont, i18nResolver);
            g2.translate(0, 30);
        } else {
            result = new BufferedImage(finalWidth, finalHeight, 2);
            g2 = result.createGraphics();
        }
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        Stroke origStroke = g2.getStroke();
        AffineTransform origTransform = g2.getTransform();
        g2.setFont(FONT_TITLE);
        g2.setColor(COLOR_TEXT);
        g2.rotate(-1.5707963267948966);
        g2.drawString(r.title, (int)(-((double)hRoadmap + boundsTitle.getWidth())) / 2 - 40, (int)boundsTitle.getHeight() + 15 - fmTitle.getDescent());
        g2.setTransform(origTransform);
        g2.setFont(FONT_COLUMNS);
        int colXPos = wTitleTheme;
        g2.setColor(COLOR_BORDER);
        g2.drawLine(colXPos, 40, colXPos + wColumns, 40);
        g2.drawLine(colXPos, 40 + hRoadmap, colXPos + wColumns, 40 + hRoadmap);
        int j = 0;
        for (RoadmapColumn col : r.columns) {
            g2.setColor((j++ & 1) == 0 ? COLOR_BACK_COLUMN_ODD : COLOR_BACK_COLUMN_EVEN);
            g2.fillRect(colXPos, 41, col.width, hRoadmap - 1);
            g2.setStroke(STROKE_COLUMN_LINE);
            g2.drawLine(colXPos + col.width, 40, colXPos + col.width, hRoadmap + 40);
            g2.setStroke(origStroke);
            g2.setColor(COLOR_TEXT);
            Rectangle2D boundsCol = fmCols.getStringBounds(col.title, g2);
            g2.drawString(col.title, colXPos + (int)((double)col.width.intValue() - boundsCol.getWidth()) / 2, (int)boundsCol.getHeight() + 10);
            colXPos += col.width.intValue();
        }
        g2.setFont(FONT_MARKERS);
        g2.setStroke(STROKE_MARKER);
        for (RoadmapMarker marker : r.markers) {
            int xPos = wTitleTheme + RoadmapRenderer.getXFromColPos(r, columnWidthsInc, marker.columnid, marker.columnpos);
            g2.setColor(RoadmapRenderer.decodeColor(marker.colour));
            int hMarkerLine = hRoadmap + 20 + 40;
            g2.drawLine(xPos, 40, xPos, hMarkerLine);
            Rectangle2D boundsMarker = fmMarker.getStringBounds(marker.title, g2);
            int yPos = hMarkerLine + 5 + (int)boundsMarker.getHeight();
            g2.drawString(marker.title, xPos - (int)(boundsMarker.getWidth() / 2.0), yPos);
        }
        g2.setStroke(origStroke);
        int themeYPos = 40;
        for (int i = 0; i < r.themes.size(); ++i) {
            RoadmapTheme theme = r.themes.get(i);
            Color colorTheme = RoadmapRenderer.decodeColor(theme.colour);
            int nRows = 0;
            for (RoadmapTask task : theme.tasks) {
                int xPos = wTitleTheme + RoadmapRenderer.getXFromColPos(r, columnWidthsInc, task.startid, task.startpos);
                int wTask = wTitleTheme + RoadmapRenderer.getXFromColPos(r, columnWidthsInc, task.endid, task.endpos) - xPos;
                int yPos = themeYPos + task.row * 40 + (task.row + 1) * 10;
                g2.setColor(colorTheme);
                g2.fillRoundRect(xPos + 5, yPos, wTask - 10, 40, 10, 10);
                g2.setColor(RoadmapRenderer.isContrasted(COLOR_TEXT_TASK_LIGHT, colorTheme) ? COLOR_TEXT_TASK_LIGHT : COLOR_TEXT_TASK_DARK);
                g2.setFont(FONT_TASKS);
                Rectangle2D boundsTask = fmTask.getStringBounds(task.title, g2);
                int yTitlePos = (int)((double)yPos + (boundsTask.getHeight() + 40.0) / 2.0 - (double)fmTask.getDescent() + 1.0);
                int taskTitleWidth = fmTask.stringWidth(task.title) + 10;
                if (taskTitleWidth <= wTask) {
                    g2.drawString(task.title, xPos + (int)((double)wTask - boundsTask.getWidth()) / 2, yTitlePos);
                } else {
                    int xTitlePos = xPos + 5 + 5;
                    int taskTitleDrawingWidth = wTask - 10 - 10 - taskEllipsisWidth;
                    g2.clipRect(xTitlePos, yPos, taskTitleDrawingWidth, 40);
                    g2.drawString(task.title, xTitlePos, yTitlePos);
                    g2.setClip(roadmapClip);
                    g2.drawString(ELLIPSIS, xTitlePos + taskTitleDrawingWidth, yTitlePos);
                }
                nRows = Math.max(task.row, nRows);
            }
            int hTheme = themeHeights[i];
            g2.setFont(FONT_THEMES);
            g2.setColor(colorTheme);
            g2.fillRoundRect(wTitle, themeYPos, wTheme, hTheme, 0, 0);
            g2.setColor(COLOR_BORDER);
            g2.drawRoundRect(wTitle, themeYPos, wTheme, hTheme, 0, 0);
            Rectangle2D boundsTheme = fmTheme.getStringBounds(theme.title, g2);
            g2.setFont(FONT_THEMES);
            g2.setColor(RoadmapRenderer.isContrasted(COLOR_TEXT_TASK_LIGHT, colorTheme) ? COLOR_TEXT_TASK_LIGHT : COLOR_TEXT_TASK_DARK);
            g2.rotate(-1.5707963267948966);
            int yPos = (int)(-((double)hTheme + boundsTheme.getWidth())) / 2 - themeYPos;
            int xPos = wTitle + (int)boundsTheme.getHeight() + 5 - 1;
            g2.drawString(theme.title, yPos, xPos);
            g2.setTransform(origTransform);
            themeYPos += hTheme;
        }
        if (isPlaceholder) {
            g2.translate(0, 0);
        }
        g2.dispose();
        return result;
    }

    private static Color decodeColor(String colour) {
        return new Color(Integer.decode("0x" + colour));
    }

    private static int getXFromColPos(Roadmap r, Map<String, Integer> columnWidthsInc, String columnid, double columnpos) {
        RoadmapColumn column = RoadmapRenderer.getColumn(r, columnid);
        return columnWidthsInc.get(columnid) + (int)((double)column.width.intValue() * columnpos) - column.width;
    }

    private static RoadmapColumn getColumn(Roadmap r, String id) {
        for (RoadmapColumn column : r.columns) {
            if (!column.id.equals(id)) continue;
            return column;
        }
        return null;
    }

    private static boolean isContrasted(Color c1, Color c2) {
        double L2;
        double L1 = RoadmapRenderer.getLuminosity(c1);
        double contrast = (L1 + 0.05) / ((L2 = RoadmapRenderer.getLuminosity(c2)) + 0.05);
        return contrast > 0.5;
    }

    private static double getLuminosity(Color color) {
        return 0.2126 * RoadmapRenderer.getLinearisedColor(color.getRed()) + 0.7152 * RoadmapRenderer.getLinearisedColor(color.getGreen()) + 0.0722 * RoadmapRenderer.getLinearisedColor(color.getBlue());
    }

    private static double getLinearisedColor(int component) {
        return Math.pow(component / 255, 2.2);
    }
}

