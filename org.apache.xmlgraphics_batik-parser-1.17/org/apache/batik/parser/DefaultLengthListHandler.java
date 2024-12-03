/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.DefaultLengthHandler;
import org.apache.batik.parser.LengthListHandler;
import org.apache.batik.parser.ParseException;

public class DefaultLengthListHandler
extends DefaultLengthHandler
implements LengthListHandler {
    public static final LengthListHandler INSTANCE = new DefaultLengthListHandler();

    protected DefaultLengthListHandler() {
    }

    @Override
    public void startLengthList() throws ParseException {
    }

    @Override
    public void endLengthList() throws ParseException {
    }
}

