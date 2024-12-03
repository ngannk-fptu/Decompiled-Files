/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.ImageEffect;
import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public abstract class BaseEffect
implements ImageEffect {
    protected String effectName;

    public BaseEffect(String effectName) {
        this.effectName = effectName;
    }

    @Override
    public abstract BufferedImage processEffect(BufferedImage var1, String var2) throws IOException, FontFormatException;

    @Override
    public boolean handles(String effectName) {
        return this.effectName.contains(effectName);
    }

    @Override
    public String getEffectName() {
        return this.effectName;
    }

    protected static void bgFill(BufferedImage out, Graphics graphics) {
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, out.getWidth(), out.getHeight());
    }

    protected void closeQuietly(InputStream istr) {
        try {
            istr.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

