/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.render;

import java.awt.RenderingHints;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.MarkerData;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.render.StrutMetrics;

public class ListItemPainter {
    public static void paint(RenderingContext c, BlockBox box) {
        if (box.getMarkerData() == null) {
            return;
        }
        MarkerData markerData = box.getMarkerData();
        if (markerData.getImageMarker() != null) {
            ListItemPainter.drawImage(c, box, markerData);
        } else {
            CalculatedStyle style = box.getStyle();
            IdentValue listStyle = style.getIdent(CSSName.LIST_STYLE_TYPE);
            c.getOutputDevice().setColor(style.getColor());
            if (markerData.getGlyphMarker() != null) {
                ListItemPainter.drawGlyph(c, box, style, listStyle);
            } else if (markerData.getTextMarker() != null) {
                ListItemPainter.drawText(c, box, listStyle);
            }
        }
    }

    private static void drawImage(RenderingContext c, BlockBox box, MarkerData markerData) {
        FSImage img = null;
        MarkerData.ImageMarker marker = markerData.getImageMarker();
        img = marker.getImage();
        if (img != null) {
            StrutMetrics strutMetrics = box.getMarkerData().getStructMetrics();
            int x = ListItemPainter.getReferenceX(c, box);
            c.getOutputDevice().drawImage(img, x += -marker.getLayoutWidth() + (marker.getLayoutWidth() / 2 - img.getWidth() / 2), (int)((float)ListItemPainter.getReferenceBaseline(c, box) - strutMetrics.getAscent() / 2.0f - (float)(img.getHeight() / 2)));
        }
    }

    private static int getReferenceX(RenderingContext c, BlockBox box) {
        MarkerData markerData = box.getMarkerData();
        if (markerData.getReferenceLine() != null) {
            return markerData.getReferenceLine().getAbsX();
        }
        return box.getAbsX() + (int)box.getMargin(c).left();
    }

    private static int getReferenceBaseline(RenderingContext c, BlockBox box) {
        MarkerData markerData = box.getMarkerData();
        StrutMetrics strutMetrics = box.getMarkerData().getStructMetrics();
        if (markerData.getReferenceLine() != null) {
            return markerData.getReferenceLine().getAbsY() + strutMetrics.getBaseline();
        }
        return box.getAbsY() + box.getTy() + strutMetrics.getBaseline();
    }

    private static void drawGlyph(RenderingContext c, BlockBox box, CalculatedStyle style, IdentValue listStyle) {
        Object aa_key = c.getOutputDevice().getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        c.getOutputDevice().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        StrutMetrics strutMetrics = box.getMarkerData().getStructMetrics();
        MarkerData.GlyphMarker marker = box.getMarkerData().getGlyphMarker();
        int x = ListItemPainter.getReferenceX(c, box);
        x += -marker.getLayoutWidth();
        int y = ListItemPainter.getReferenceBaseline(c, box) - (int)strutMetrics.getAscent() / 2 - marker.getDiameter() / 2;
        if (listStyle == IdentValue.DISC) {
            c.getOutputDevice().fillOval(x, y, marker.getDiameter(), marker.getDiameter());
        } else if (listStyle == IdentValue.SQUARE) {
            c.getOutputDevice().fillRect(x, y, marker.getDiameter(), marker.getDiameter());
        } else if (listStyle == IdentValue.CIRCLE) {
            c.getOutputDevice().drawOval(x, y, marker.getDiameter(), marker.getDiameter());
        }
        c.getOutputDevice().setRenderingHint(RenderingHints.KEY_ANTIALIASING, aa_key == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : aa_key);
    }

    private static void drawText(RenderingContext c, BlockBox box, IdentValue listStyle) {
        MarkerData.TextMarker text = box.getMarkerData().getTextMarker();
        int x = ListItemPainter.getReferenceX(c, box);
        int y = ListItemPainter.getReferenceBaseline(c, box);
        c.getOutputDevice().setColor(box.getStyle().getColor());
        c.getOutputDevice().setFont(box.getStyle().getFSFont(c));
        c.getTextRenderer().drawString(c.getOutputDevice(), text.getText(), x += -text.getLayoutWidth(), y);
    }
}

