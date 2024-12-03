/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.NumberListHandler;
import org.apache.batik.parser.ParseException;

public class DefaultNumberListHandler
implements NumberListHandler {
    public static final NumberListHandler INSTANCE = new DefaultNumberListHandler();

    protected DefaultNumberListHandler() {
    }

    @Override
    public void startNumberList() throws ParseException {
    }

    @Override
    public void endNumberList() throws ParseException {
    }

    @Override
    public void startNumber() throws ParseException {
    }

    @Override
    public void numberValue(float v) throws ParseException {
    }

    @Override
    public void endNumber() throws ParseException {
    }
}

