/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.EventQueue;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.ImageLoadItem;
import org.xhtmlrenderer.swing.ImageLoadQueue;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.XRLog;

class ImageLoadWorker
extends Thread {
    private static volatile int counter = 0;
    private final ImageLoadQueue queue;

    public ImageLoadWorker(ImageLoadQueue queue) {
        this.queue = queue;
        this.setDaemon(true);
        this.setPriority(1);
        this.setName("ImageLoadWorker(" + counter++ + ")");
    }

    @Override
    public void run() {
        try {
            ImageLoadItem loadItem;
            while (!ImageLoadQueue.isKillSwitch(loadItem = this.queue.getTask())) {
                boolean wasScaled;
                ImageResource ir = ImageResourceLoader.loadImageResourceFromUri(loadItem._uri);
                FSImage awtfsImage = ir.getImage();
                BufferedImage newImg = ((AWTFSImage)awtfsImage).getImage();
                XRLog.load(Level.FINE, this + ", loaded " + loadItem._uri);
                loadItem._imageResourceLoader.loaded(ir, newImg.getWidth(), newImg.getHeight());
                if (loadItem.haveTargetDimensions() && !ir.hasDimensions(loadItem._targetWidth, loadItem._targetHeight)) {
                    XRLog.load(Level.FINE, this + ", scaling " + loadItem._uri + " to " + loadItem._targetWidth + ", " + loadItem._targetHeight);
                    newImg = ImageUtil.getScaledInstance(newImg, loadItem._targetWidth, loadItem._targetHeight);
                    ImageResource sir = new ImageResource(ir.getImageUri(), AWTFSImage.createImage(newImg));
                    loadItem._imageResourceLoader.loaded(sir, newImg.getWidth(), newImg.getHeight());
                    wasScaled = true;
                } else {
                    wasScaled = false;
                }
                final BufferedImage newImg1 = newImg;
                EventQueue.invokeLater(new Runnable(){

                    @Override
                    public void run() {
                        loadItem._mfsImage.setImage(loadItem._uri, newImg1, wasScaled);
                    }
                });
            }
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }
}

