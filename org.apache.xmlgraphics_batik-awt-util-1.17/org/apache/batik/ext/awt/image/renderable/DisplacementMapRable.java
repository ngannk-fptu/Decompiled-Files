/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.util.List;
import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface DisplacementMapRable
extends FilterColorInterpolation {
    public static final int CHANNEL_R = 1;
    public static final int CHANNEL_G = 2;
    public static final int CHANNEL_B = 3;
    public static final int CHANNEL_A = 4;

    public void setSources(List var1);

    public void setScale(double var1);

    public double getScale();

    public void setXChannelSelector(ARGBChannel var1);

    public ARGBChannel getXChannelSelector();

    public void setYChannelSelector(ARGBChannel var1);

    public ARGBChannel getYChannelSelector();
}

