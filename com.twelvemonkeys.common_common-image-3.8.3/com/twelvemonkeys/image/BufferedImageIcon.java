/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.image;

import com.twelvemonkeys.lang.Validate;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.Icon;

public class BufferedImageIcon
implements Icon {
    private final BufferedImage image;
    private final int width;
    private final int height;
    private final boolean fast;

    public BufferedImageIcon(BufferedImage bufferedImage) {
        this(bufferedImage, bufferedImage != null ? bufferedImage.getWidth() : 0, bufferedImage != null ? bufferedImage.getHeight() : 0);
    }

    public BufferedImageIcon(BufferedImage bufferedImage, int n, int n2) {
        this(bufferedImage, n, n2, bufferedImage.getWidth() == n && bufferedImage.getHeight() == n2);
    }

    public BufferedImageIcon(BufferedImage bufferedImage, int n, int n2, boolean bl) {
        this.image = (BufferedImage)Validate.notNull((Object)bufferedImage, (String)"image");
        this.width = (Integer)Validate.isTrue((n > 0 ? 1 : 0) != 0, (Object)n, (String)"width must be positive: %d");
        this.height = (Integer)Validate.isTrue((n2 > 0 ? 1 : 0) != 0, (Object)n2, (String)"height must be positive: %d");
        this.fast = bl;
    }

    @Override
    public int getIconHeight() {
        return this.height;
    }

    @Override
    public int getIconWidth() {
        return this.width;
    }

    @Override
    public void paintIcon(Component component, Graphics graphics, int n, int n2) {
        if (this.fast || !(graphics instanceof Graphics2D)) {
            graphics.drawImage(this.image, n, n2, this.width, this.height, null);
        } else {
            Graphics2D graphics2D = (Graphics2D)graphics;
            AffineTransform affineTransform = AffineTransform.getTranslateInstance(n, n2);
            affineTransform.scale((double)this.width / (double)this.image.getWidth(), (double)this.height / (double)this.image.getHeight());
            graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics2D.drawImage(this.image, affineTransform, null);
        }
    }
}

