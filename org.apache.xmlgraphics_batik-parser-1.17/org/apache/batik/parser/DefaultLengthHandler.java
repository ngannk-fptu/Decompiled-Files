/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.ParseException;

public class DefaultLengthHandler
implements LengthHandler {
    public static final LengthHandler INSTANCE = new DefaultLengthHandler();

    protected DefaultLengthHandler() {
    }

    @Override
    public void startLength() throws ParseException {
    }

    @Override
    public void lengthValue(float v) throws ParseException {
    }

    @Override
    public void em() throws ParseException {
    }

    @Override
    public void ex() throws ParseException {
    }

    @Override
    public void in() throws ParseException {
    }

    @Override
    public void cm() throws ParseException {
    }

    @Override
    public void mm() throws ParseException {
    }

    @Override
    public void pc() throws ParseException {
    }

    @Override
    public void pt() throws ParseException {
    }

    @Override
    public void px() throws ParseException {
    }

    @Override
    public void percentage() throws ParseException {
    }

    @Override
    public void endLength() throws ParseException {
    }
}

