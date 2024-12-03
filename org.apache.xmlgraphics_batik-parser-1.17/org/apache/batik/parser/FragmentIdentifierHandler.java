/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.PreserveAspectRatioHandler;
import org.apache.batik.parser.TransformListHandler;

public interface FragmentIdentifierHandler
extends PreserveAspectRatioHandler,
TransformListHandler {
    public void startFragmentIdentifier() throws ParseException;

    public void idReference(String var1) throws ParseException;

    public void viewBox(float var1, float var2, float var3, float var4) throws ParseException;

    public void startViewTarget() throws ParseException;

    public void viewTarget(String var1) throws ParseException;

    public void endViewTarget() throws ParseException;

    public void zoomAndPan(boolean var1);

    public void endFragmentIdentifier() throws ParseException;
}

