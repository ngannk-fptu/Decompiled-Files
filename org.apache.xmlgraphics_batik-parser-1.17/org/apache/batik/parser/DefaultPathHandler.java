/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PathHandler;

public class DefaultPathHandler
implements PathHandler {
    public static final PathHandler INSTANCE = new DefaultPathHandler();

    protected DefaultPathHandler() {
    }

    @Override
    public void startPath() throws ParseException {
    }

    @Override
    public void endPath() throws ParseException {
    }

    @Override
    public void movetoRel(float x, float y) throws ParseException {
    }

    @Override
    public void movetoAbs(float x, float y) throws ParseException {
    }

    @Override
    public void closePath() throws ParseException {
    }

    @Override
    public void linetoRel(float x, float y) throws ParseException {
    }

    @Override
    public void linetoAbs(float x, float y) throws ParseException {
    }

    @Override
    public void linetoHorizontalRel(float x) throws ParseException {
    }

    @Override
    public void linetoHorizontalAbs(float x) throws ParseException {
    }

    @Override
    public void linetoVerticalRel(float y) throws ParseException {
    }

    @Override
    public void linetoVerticalAbs(float y) throws ParseException {
    }

    @Override
    public void curvetoCubicRel(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoCubicAbs(float x1, float y1, float x2, float y2, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoCubicSmoothRel(float x2, float y2, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoCubicSmoothAbs(float x2, float y2, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoQuadraticRel(float x1, float y1, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoQuadraticAbs(float x1, float y1, float x, float y) throws ParseException {
    }

    @Override
    public void curvetoQuadraticSmoothRel(float x, float y) throws ParseException {
    }

    @Override
    public void curvetoQuadraticSmoothAbs(float x, float y) throws ParseException {
    }

    @Override
    public void arcRel(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
    }

    @Override
    public void arcAbs(float rx, float ry, float xAxisRotation, boolean largeArcFlag, boolean sweepFlag, float x, float y) throws ParseException {
    }
}

