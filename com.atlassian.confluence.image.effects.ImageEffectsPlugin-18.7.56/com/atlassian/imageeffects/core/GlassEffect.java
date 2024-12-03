/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class GlassEffect
extends BaseEffect
implements ImageEffect {
    public GlassEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        Graphics2D graphics = (Graphics2D)img.getGraphics();
        InputStream in = this.getClass().getResourceAsStream("/images/glass.png");
        try {
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            BufferedImage glass = ImageIO.read(in);
            graphics.drawImage(glass, 0, 0, img.getWidth(), img.getHeight(), null);
        }
        finally {
            graphics.dispose();
            this.closeQuietly(in);
        }
        return img;
    }
}

