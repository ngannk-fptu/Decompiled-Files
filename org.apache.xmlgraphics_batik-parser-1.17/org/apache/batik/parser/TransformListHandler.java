/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;

public interface TransformListHandler {
    public void startTransformList() throws ParseException;

    public void matrix(float var1, float var2, float var3, float var4, float var5, float var6) throws ParseException;

    public void rotate(float var1) throws ParseException;

    public void rotate(float var1, float var2, float var3) throws ParseException;

    public void translate(float var1) throws ParseException;

    public void translate(float var1, float var2) throws ParseException;

    public void scale(float var1) throws ParseException;

    public void scale(float var1, float var2) throws ParseException;

    public void skewX(float var1) throws ParseException;

    public void skewY(float var1) throws ParseException;

    public void endTransformList() throws ParseException;
}

