/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;

public interface NumberListHandler {
    public void startNumberList() throws ParseException;

    public void endNumberList() throws ParseException;

    public void startNumber() throws ParseException;

    public void endNumber() throws ParseException;

    public void numberValue(float var1) throws ParseException;
}

