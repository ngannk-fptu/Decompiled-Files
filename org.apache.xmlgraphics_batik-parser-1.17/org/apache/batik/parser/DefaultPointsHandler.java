/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PointsHandler;

public class DefaultPointsHandler
implements PointsHandler {
    public static final DefaultPointsHandler INSTANCE = new DefaultPointsHandler();

    protected DefaultPointsHandler() {
    }

    @Override
    public void startPoints() throws ParseException {
    }

    @Override
    public void point(float x, float y) throws ParseException {
    }

    @Override
    public void endPoints() throws ParseException {
    }
}

