/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.io.File;
import java.util.Date;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.util.Uu;

public class HamletSpeedTest {
    public static void main(String[] args) throws Exception {
        long total = 0L;
        for (int i = 0; i < 10; ++i) {
            Date start = new Date();
            Graphics2DRenderer.renderToImage(new File("demos/browser/xhtml/old/hamlet.xhtml").toURL().toExternalForm(), 700, 700);
            Date end = new Date();
            long diff = end.getTime() - start.getTime();
            Uu.p("ms = " + diff);
            total += diff;
        }
        long avg = total / 10L;
        Uu.p("average : " + avg);
    }
}

