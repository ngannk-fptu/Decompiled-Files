/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core;

import com.atlassian.imageeffects.core.BaseEffect;
import com.atlassian.imageeffects.core.ImageEffect;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class TapeForThumbEffect
extends BaseEffect
implements ImageEffect {
    public TapeForThumbEffect(String effectName) {
        super(effectName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BufferedImage processEffect(BufferedImage img, String label) throws IOException {
        InputStream inTopLeft = this.getClass().getResourceAsStream("/images/tape_top_left_opaque.png");
        InputStream inTopRight = this.getClass().getResourceAsStream("/images/tape_top_right_opaque.png");
        InputStream inBottomLeft = this.getClass().getResourceAsStream("/images/tape_bottom_left_opaque.png");
        InputStream inBottomRight = this.getClass().getResourceAsStream("/images/tape_bottom_right_opaque.png");
        int padding = 60;
        BufferedImage out = new BufferedImage(img.getWidth() + padding, img.getHeight() + padding, 1);
        Graphics graphics = out.getGraphics();
        TapeForThumbEffect.bgFill(out, graphics);
        try {
            graphics.drawImage(img, padding / 2, padding / 2, img.getWidth(), img.getHeight(), null);
            BufferedImage topLeft = ImageIO.read(inTopLeft);
            BufferedImage topRight = ImageIO.read(inTopRight);
            BufferedImage bottomLeft = ImageIO.read(inBottomLeft);
            BufferedImage bottomRight = ImageIO.read(inBottomRight);
            graphics.drawImage(topLeft, 0, 0, null);
            graphics.drawImage(topRight, img.getWidth() + padding - topRight.getWidth(), 0, null);
            graphics.drawImage(bottomLeft, 0, img.getHeight() + padding - bottomLeft.getHeight(), null);
            graphics.drawImage(bottomRight, img.getWidth() + padding - bottomRight.getWidth(), img.getHeight() + padding - bottomRight.getHeight(), null);
        }
        finally {
            graphics.dispose();
            this.closeQuietly(inTopLeft);
            this.closeQuietly(inTopRight);
            this.closeQuietly(inBottomLeft);
            this.closeQuietly(inBottomRight);
        }
        return out;
    }
}

