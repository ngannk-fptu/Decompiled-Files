/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d.color;

import java.awt.Color;
import org.apache.xmlgraphics.java2d.color.ColorConverter;

public final class DefaultColorConverter
implements ColorConverter {
    private static final DefaultColorConverter SINGLETON = new DefaultColorConverter();

    private DefaultColorConverter() {
    }

    public static DefaultColorConverter getInstance() {
        return SINGLETON;
    }

    @Override
    public Color convert(Color color) {
        return color;
    }
}

