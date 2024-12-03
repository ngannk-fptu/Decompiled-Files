/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.annotation.handlers;

public interface PDAppearanceHandler {
    public void generateAppearanceStreams();

    public void generateNormalAppearance();

    public void generateRolloverAppearance();

    public void generateDownAppearance();
}

