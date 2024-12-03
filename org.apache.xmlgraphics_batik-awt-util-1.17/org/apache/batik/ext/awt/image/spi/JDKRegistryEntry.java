/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 */
package org.apache.batik.ext.awt.image.spi;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.renderable.DeferRable;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.RedRable;
import org.apache.batik.ext.awt.image.spi.AbstractRegistryEntry;
import org.apache.batik.ext.awt.image.spi.ImageTagRegistry;
import org.apache.batik.ext.awt.image.spi.URLRegistryEntry;
import org.apache.batik.util.ParsedURL;

public class JDKRegistryEntry
extends AbstractRegistryEntry
implements URLRegistryEntry {
    public static final float PRIORITY = 1000000.0f;

    public JDKRegistryEntry() {
        super("JDK", 1000000.0f, new String[0], new String[]{"image/gif"});
    }

    @Override
    public boolean isCompatibleURL(ParsedURL purl) {
        try {
            new URL(purl.toString());
        }
        catch (MalformedURLException mue) {
            return false;
        }
        return true;
    }

    @Override
    public Filter handleURL(ParsedURL purl, boolean needRawData) {
        Object[] errParam;
        String errCode;
        URL url;
        try {
            url = new URL(purl.toString());
        }
        catch (MalformedURLException mue) {
            return null;
        }
        final DeferRable dr = new DeferRable();
        if (purl != null) {
            errCode = "url.format.unreadable";
            errParam = new Object[]{"JDK", url};
        } else {
            errCode = "stream.format.unreadable";
            errParam = new Object[]{"JDK"};
        }
        Thread t = new Thread(){

            @Override
            public void run() {
                Filter filt = null;
                try {
                    RenderedImage ri;
                    Toolkit tk = Toolkit.getDefaultToolkit();
                    Image img = tk.createImage(url);
                    if (img != null && (ri = JDKRegistryEntry.this.loadImage(img, dr)) != null) {
                        filt = new RedRable(GraphicsUtil.wrap(ri));
                    }
                }
                catch (ThreadDeath td) {
                    filt = ImageTagRegistry.getBrokenLinkImage(JDKRegistryEntry.this, errCode, errParam);
                    dr.setSource(filt);
                    throw td;
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                if (filt == null) {
                    filt = ImageTagRegistry.getBrokenLinkImage(JDKRegistryEntry.this, errCode, errParam);
                }
                dr.setSource(filt);
            }
        };
        t.start();
        return dr;
    }

    public RenderedImage loadImage(Image img, DeferRable dr) {
        if (img instanceof RenderedImage) {
            return (RenderedImage)((Object)img);
        }
        MyImgObs observer = new MyImgObs();
        Toolkit.getDefaultToolkit().prepareImage(img, -1, -1, observer);
        observer.waitTilWidthHeightDone();
        if (observer.imageError) {
            return null;
        }
        int width = observer.width;
        int height = observer.height;
        dr.setBounds(new Rectangle2D.Double(0.0, 0.0, width, height));
        BufferedImage bi = new BufferedImage(width, height, 2);
        Graphics2D g2d = bi.createGraphics();
        observer.waitTilImageDone();
        if (observer.imageError) {
            return null;
        }
        dr.setProperties(new HashMap());
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();
        return bi;
    }

    public static class MyImgObs
    implements ImageObserver {
        boolean widthDone = false;
        boolean heightDone = false;
        boolean imageDone = false;
        int width = -1;
        int height = -1;
        boolean imageError = false;
        int IMG_BITS = 224;

        public void clear() {
            this.width = -1;
            this.height = -1;
            this.widthDone = false;
            this.heightDone = false;
            this.imageDone = false;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            MyImgObs myImgObs = this;
            synchronized (myImgObs) {
                boolean notify = false;
                if ((infoflags & 1) != 0) {
                    this.width = width;
                }
                if ((infoflags & 2) != 0) {
                    this.height = height;
                }
                if ((infoflags & 0x20) != 0) {
                    this.width = width;
                    this.height = height;
                }
                if ((infoflags & this.IMG_BITS) != 0) {
                    if (!(this.widthDone && this.heightDone && this.imageDone)) {
                        this.widthDone = true;
                        this.heightDone = true;
                        this.imageDone = true;
                        notify = true;
                    }
                    if ((infoflags & 0x40) != 0) {
                        this.imageError = true;
                    }
                }
                if (!this.widthDone && this.width != -1) {
                    notify = true;
                    this.widthDone = true;
                }
                if (!this.heightDone && this.height != -1) {
                    notify = true;
                    this.heightDone = true;
                }
                if (notify) {
                    this.notifyAll();
                }
            }
            return true;
        }

        public synchronized void waitTilWidthHeightDone() {
            while (!this.widthDone || !this.heightDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }

        public synchronized void waitTilWidthDone() {
            while (!this.widthDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }

        public synchronized void waitTilHeightDone() {
            while (!this.heightDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }

        public synchronized void waitTilImageDone() {
            while (!this.imageDone) {
                try {
                    this.wait();
                }
                catch (InterruptedException interruptedException) {}
            }
        }
    }
}

