/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.TransformListHandler;

public class DefaultTransformListHandler
implements TransformListHandler {
    public static final TransformListHandler INSTANCE = new DefaultTransformListHandler();

    protected DefaultTransformListHandler() {
    }

    @Override
    public void startTransformList() throws ParseException {
    }

    @Override
    public void matrix(float a, float b, float c, float d, float e, float f) throws ParseException {
    }

    @Override
    public void rotate(float theta) throws ParseException {
    }

    @Override
    public void rotate(float theta, float cx, float cy) throws ParseException {
    }

    @Override
    public void translate(float tx) throws ParseException {
    }

    @Override
    public void translate(float tx, float ty) throws ParseException {
    }

    @Override
    public void scale(float sx) throws ParseException {
    }

    @Override
    public void scale(float sx, float sy) throws ParseException {
    }

    @Override
    public void skewX(float skx) throws ParseException {
    }

    @Override
    public void skewY(float sky) throws ParseException {
    }

    @Override
    public void endTransformList() throws ParseException {
    }
}

