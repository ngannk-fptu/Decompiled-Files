/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.AngleHandler;
import org.apache.batik.parser.ParseException;

public class DefaultAngleHandler
implements AngleHandler {
    public static final AngleHandler INSTANCE = new DefaultAngleHandler();

    protected DefaultAngleHandler() {
    }

    @Override
    public void startAngle() throws ParseException {
    }

    @Override
    public void angleValue(float v) throws ParseException {
    }

    @Override
    public void deg() throws ParseException {
    }

    @Override
    public void grad() throws ParseException {
    }

    @Override
    public void rad() throws ParseException {
    }

    @Override
    public void endAngle() throws ParseException {
    }
}

