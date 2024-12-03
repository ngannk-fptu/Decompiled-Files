/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;

public interface LengthHandler {
    public void startLength() throws ParseException;

    public void lengthValue(float var1) throws ParseException;

    public void em() throws ParseException;

    public void ex() throws ParseException;

    public void in() throws ParseException;

    public void cm() throws ParseException;

    public void mm() throws ParseException;

    public void pc() throws ParseException;

    public void pt() throws ParseException;

    public void px() throws ParseException;

    public void percentage() throws ParseException;

    public void endLength() throws ParseException;
}

