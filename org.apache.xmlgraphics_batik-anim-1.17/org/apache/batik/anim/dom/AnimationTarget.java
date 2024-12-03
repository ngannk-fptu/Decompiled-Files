/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.AnimationTargetListener;
import org.apache.batik.anim.values.AnimatableValue;
import org.w3c.dom.Element;

public interface AnimationTarget {
    public static final short PERCENTAGE_FONT_SIZE = 0;
    public static final short PERCENTAGE_VIEWPORT_WIDTH = 1;
    public static final short PERCENTAGE_VIEWPORT_HEIGHT = 2;
    public static final short PERCENTAGE_VIEWPORT_SIZE = 3;

    public Element getElement();

    public void updatePropertyValue(String var1, AnimatableValue var2);

    public void updateAttributeValue(String var1, String var2, AnimatableValue var3);

    public void updateOtherValue(String var1, AnimatableValue var2);

    public AnimatableValue getUnderlyingValue(String var1, String var2);

    public short getPercentageInterpretation(String var1, String var2, boolean var3);

    public boolean useLinearRGBColorInterpolation();

    public float svgToUserSpace(float var1, short var2, short var3);

    public void addTargetListener(String var1, String var2, boolean var3, AnimationTargetListener var4);

    public void removeTargetListener(String var1, String var2, boolean var3, AnimationTargetListener var4);
}

