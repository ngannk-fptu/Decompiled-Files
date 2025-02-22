/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.spi;

import java.awt.Image;
import org.apache.batik.ext.awt.image.renderable.Filter;

public abstract class BrokenLinkProvider {
    public static final String BROKEN_LINK_PROPERTY = "org.apache.batik.BrokenLinkImage";

    public abstract Filter getBrokenLinkImage(Object var1, String var2, Object[] var3);

    public static boolean hasBrokenLinkProperty(Filter f) {
        Object o = f.getProperty(BROKEN_LINK_PROPERTY);
        if (o == null) {
            return false;
        }
        return o != Image.UndefinedProperty;
    }
}

