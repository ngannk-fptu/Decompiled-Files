/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.ui;

import java.awt.Dimension;
import org.jfree.ui.Drawable;

public interface ExtendedDrawable
extends Drawable {
    public Dimension getPreferredSize();

    public boolean isPreserveAspectRatio();
}

