/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import java.awt.Shape;

public interface ShapeProducer {
    public Shape getShape();

    public void setWindingRule(int var1);

    public int getWindingRule();
}

