/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.i18n.LocalizableSupport
 */
package org.apache.batik.ext.awt.image.spi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Hashtable;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.apache.batik.i18n.LocalizableSupport;

public class DefaultBrokenLinkProvider
extends BrokenLinkProvider {
    static Filter brokenLinkImg = null;
    static final String MESSAGE_RSRC = "resources.Messages";
    static final Color BROKEN_LINK_COLOR = new Color(255, 255, 255, 190);

    public static String formatMessage(Object base, String code, Object[] params) {
        ClassLoader cl = null;
        try {
            cl = DefaultBrokenLinkProvider.class.getClassLoader();
            cl = base.getClass().getClassLoader();
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        LocalizableSupport ls = new LocalizableSupport(MESSAGE_RSRC, base.getClass(), cl);
        return ls.formatMessage(code, params);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Filter getBrokenLinkImage(Object base, String code, Object[] params) {
        Class<DefaultBrokenLinkProvider> clazz = DefaultBrokenLinkProvider.class;
        synchronized (DefaultBrokenLinkProvider.class) {
            if (brokenLinkImg != null) {
                // ** MonitorExit[var4_4] (shouldn't be in output)
                return brokenLinkImg;
            }
            BufferedImage bi = new BufferedImage(100, 100, 2);
            Hashtable<String, String> ht = new Hashtable<String, String>();
            ht.put("org.apache.batik.BrokenLinkImage", DefaultBrokenLinkProvider.formatMessage(base, code, params));
            bi = new BufferedImage(bi.getColorModel(), bi.getRaster(), bi.isAlphaPremultiplied(), ht);
            Graphics2D g2d = bi.createGraphics();
            g2d.setColor(BROKEN_LINK_COLOR);
            g2d.fillRect(0, 0, 100, 100);
            g2d.setColor(Color.black);
            g2d.drawRect(2, 2, 96, 96);
            g2d.drawString("Broken Image", 6, 50);
            g2d.dispose();
            brokenLinkImg = new RedRable(GraphicsUtil.wrap(bi));
            // ** MonitorExit[var4_4] (shouldn't be in output)
            return brokenLinkImg;
        }
    }
}

