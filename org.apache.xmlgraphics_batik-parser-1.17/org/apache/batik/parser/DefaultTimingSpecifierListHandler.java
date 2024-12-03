/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.DefaultTimingSpecifierHandler;
import org.apache.batik.parser.TimingSpecifierListHandler;

public class DefaultTimingSpecifierListHandler
extends DefaultTimingSpecifierHandler
implements TimingSpecifierListHandler {
    public static final TimingSpecifierListHandler INSTANCE = new DefaultTimingSpecifierListHandler();

    protected DefaultTimingSpecifierListHandler() {
    }

    @Override
    public void startTimingSpecifierList() {
    }

    @Override
    public void endTimingSpecifierList() {
    }
}

