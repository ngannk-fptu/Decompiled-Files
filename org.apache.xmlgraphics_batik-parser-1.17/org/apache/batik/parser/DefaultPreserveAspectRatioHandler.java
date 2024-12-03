/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;

public class DefaultPreserveAspectRatioHandler
implements PreserveAspectRatioHandler {
    public static final PreserveAspectRatioHandler INSTANCE = new DefaultPreserveAspectRatioHandler();

    protected DefaultPreserveAspectRatioHandler() {
    }

    @Override
    public void startPreserveAspectRatio() throws ParseException {
    }

    @Override
    public void none() throws ParseException {
    }

    @Override
    public void xMaxYMax() throws ParseException {
    }

    @Override
    public void xMaxYMid() throws ParseException {
    }

    @Override
    public void xMaxYMin() throws ParseException {
    }

    @Override
    public void xMidYMax() throws ParseException {
    }

    @Override
    public void xMidYMid() throws ParseException {
    }

    @Override
    public void xMidYMin() throws ParseException {
    }

    @Override
    public void xMinYMax() throws ParseException {
    }

    @Override
    public void xMinYMid() throws ParseException {
    }

    @Override
    public void xMinYMin() throws ParseException {
    }

    @Override
    public void meet() throws ParseException {
    }

    @Override
    public void slice() throws ParseException {
    }

    @Override
    public void endPreserveAspectRatio() throws ParseException {
    }
}

