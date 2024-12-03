/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;

public interface PointsHandler {
    public void startPoints() throws ParseException;

    public void point(float var1, float var2) throws ParseException;

    public void endPoints() throws ParseException;
}

