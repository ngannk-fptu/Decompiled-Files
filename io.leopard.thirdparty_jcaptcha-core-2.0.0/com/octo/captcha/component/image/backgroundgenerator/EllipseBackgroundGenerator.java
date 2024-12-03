/*
 * Decompiled with CFR 0.152.
 */
package com.octo.captcha.component.image.backgroundgenerator;

import com.octo.captcha.component.image.backgroundgenerator.AbstractBackgroundGenerator;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;

public class EllipseBackgroundGenerator
extends AbstractBackgroundGenerator {
    public EllipseBackgroundGenerator(Integer width, Integer height) {
        super(width, height);
    }

    @Override
    public BufferedImage getBackground() {
        BufferedImage bimgTP = new BufferedImage(this.getImageWidth(), this.getImageHeight(), 1);
        Graphics2D g2d = bimgTP.createGraphics();
        BasicStroke bs = new BasicStroke(2.0f, 0, 0, 2.0f, new float[]{2.0f, 2.0f}, 0.0f);
        g2d.setStroke(bs);
        AlphaComposite ac = AlphaComposite.getInstance(3, 0.75f);
        g2d.setComposite(ac);
        g2d.translate((double)this.getImageWidth() * -1.0, 0.0);
        double delta = 5.0;
        double ts = 0.0;
        for (double xt = 0.0; xt < 2.0 * (double)this.getImageWidth(); xt += delta) {
            Arc2D.Double arc = new Arc2D.Double(0.0, 0.0, this.getImageWidth(), this.getImageHeight(), 0.0, 360.0, 0);
            g2d.draw(arc);
            g2d.translate(delta, 0.0);
            ts += delta;
        }
        return bimgTP;
    }
}

