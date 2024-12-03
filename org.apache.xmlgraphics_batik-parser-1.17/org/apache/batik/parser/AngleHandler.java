/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;

public interface AngleHandler {
    public void startAngle() throws ParseException;

    public void angleValue(float var1) throws ParseException;

    public void deg() throws ParseException;

    public void grad() throws ParseException;

    public void rad() throws ParseException;

    public void endAngle() throws ParseException;
}

