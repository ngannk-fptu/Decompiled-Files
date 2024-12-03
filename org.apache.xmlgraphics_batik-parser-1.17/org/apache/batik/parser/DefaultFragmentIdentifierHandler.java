/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.DefaultPreserveAspectRatioHandler;
import org.apache.batik.parser.FragmentIdentifierHandler;
import org.apache.batik.parser.ParseException;

public class DefaultFragmentIdentifierHandler
extends DefaultPreserveAspectRatioHandler
implements FragmentIdentifierHandler {
    public static final FragmentIdentifierHandler INSTANCE = new DefaultFragmentIdentifierHandler();

    protected DefaultFragmentIdentifierHandler() {
    }

    @Override
    public void startFragmentIdentifier() throws ParseException {
    }

    @Override
    public void idReference(String s) throws ParseException {
    }

    @Override
    public void viewBox(float x, float y, float width, float height) throws ParseException {
    }

    @Override
    public void startViewTarget() throws ParseException {
    }

    @Override
    public void viewTarget(String name) throws ParseException {
    }

    @Override
    public void endViewTarget() throws ParseException {
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

    @Override
    public void zoomAndPan(boolean magnify) {
    }

    @Override
    public void endFragmentIdentifier() throws ParseException {
    }
}

