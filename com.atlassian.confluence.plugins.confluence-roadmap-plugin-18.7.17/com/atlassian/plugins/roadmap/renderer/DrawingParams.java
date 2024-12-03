/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.roadmap.renderer;

import com.atlassian.plugins.roadmap.renderer.RenderedImageInfoEnricher;
import java.awt.FontMetrics;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

class DrawingParams {
    RenderedImageInfoEnricher enricher;
    FontMetrics fmCols;
    FontMetrics fmMarker;
    FontMetrics fmTask;
    FontMetrics fmTheme;
    FontMetrics fmTitle;
    int hRoadmap;
    int wRoadmap;
    Stroke origStroke;
    AffineTransform origTransform;
    int realHeight;
    int themeYPos;
    int wColumns;
    int wTheme;
    int wTitle;
    int wTitleTheme;

    DrawingParams() {
    }
}

