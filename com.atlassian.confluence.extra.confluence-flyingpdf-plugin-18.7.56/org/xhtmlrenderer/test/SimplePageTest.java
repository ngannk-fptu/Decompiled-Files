/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.io.File;
import java.util.Date;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.util.Uu;

public class SimplePageTest {
    public static void main(String[] args) throws Exception {
        long total = 0L;
        int cnt = 1;
        String demosDir = "d:/java/javanet/xhtmlrenderer/demos/browser/xhtml/new";
        String page = demosDir + "/dead-simple-page.xhtml";
        if (args.length == 1) {
            page = demosDir + "/" + args[0];
        }
        System.out.println("Testing with page " + page);
        for (int i = 0; i < cnt; ++i) {
            Date start = new Date();
            Graphics2DRenderer.renderToImage(new File(page).toURL().toExternalForm(), 700, 700);
            Date end = new Date();
            long diff = end.getTime() - start.getTime();
            Uu.p("ms = " + diff);
            if (i <= 4) continue;
            total += diff;
        }
        long avg = total / (long)cnt;
        System.out.println("average : " + avg);
    }
}

