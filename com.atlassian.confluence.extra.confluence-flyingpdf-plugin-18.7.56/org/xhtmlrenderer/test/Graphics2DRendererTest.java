/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.xhtmlrenderer.simple.Graphics2DRenderer;

public class Graphics2DRendererTest {
    public static void main(String[] args) throws Exception {
        BufferedImage img = Graphics2DRenderer.renderToImageAutoSize(new File("demos/splash/splash.html").toURL().toExternalForm(), 700, 2);
        ImageIO.write((RenderedImage)img, "png", new File("test.png"));
    }
}

