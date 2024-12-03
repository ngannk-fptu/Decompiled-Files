/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import com.jhlabs.image.BorderFilter;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class PolaroidBorderEffect
extends BaseEffect
implements ImageEffect {
    public PolaroidBorderEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException, FontFormatException {
        InputStream in = this.getClass().getResourceAsStream("/images/paper_texture.png");
        BufferedImage texture = ImageIO.read(in);
        TexturePaint paint = new TexturePaint(texture, new Rectangle2D.Float(0.0f, 0.0f, texture.getWidth(), texture.getHeight()));
        BorderFilter filter = new BorderFilter(20, 20, 20, 75, paint);
        BufferedImage out = filter.filter(img, null);
        Graphics2D g = (Graphics2D)out.getGraphics();
        try {
            if (label != null && label.length() > 0) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                Font f = Font.createFont(0, this.getClass().getResourceAsStream("/fonts/MOANHAND.ttf"));
                f = f.deriveFont(26.0f).deriveFont(1);
                g.setPaint(new Color(44, 32, 24));
                Rectangle2D stringBounds = f.getStringBounds(label, g.getFontRenderContext());
                double width = stringBounds.getWidth();
                int left = 30;
                if (width < (double)out.getWidth()) {
                    left = (out.getWidth() - (int)width) / 2;
                }
                g.translate(left, out.getHeight() - 30);
                g.setFont(f);
                g.drawString(label, 0, 0);
            }
        }
        finally {
            g.dispose();
            this.closeQuietly(in);
        }
        return out;
    }
}

