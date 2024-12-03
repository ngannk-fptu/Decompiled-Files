/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.geom;

import java.awt.Shape;
import org.apache.batik.ext.awt.geom.ExtendedPathIterator;

public interface ExtendedShape
extends Shape {
    public ExtendedPathIterator getExtendedPathIterator();
}

