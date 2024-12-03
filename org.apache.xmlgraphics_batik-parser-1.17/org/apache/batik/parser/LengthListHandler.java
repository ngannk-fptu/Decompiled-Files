/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.ParseException;

public interface LengthListHandler
extends LengthHandler {
    public void startLengthList() throws ParseException;

    public void endLengthList() throws ParseException;
}

