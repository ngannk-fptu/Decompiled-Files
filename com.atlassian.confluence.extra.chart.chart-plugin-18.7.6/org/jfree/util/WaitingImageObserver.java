/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import org.jfree.util.Log;

public class WaitingImageObserver
implements ImageObserver,
Serializable,
Cloneable {
    static final long serialVersionUID = -807204410581383550L;
    private boolean lock;
    private Image image;
    private boolean error;

    public WaitingImageObserver(Image image) {
        if (image == null) {
            throw new NullPointerException();
        }
        this.image = image;
        this.lock = true;
    }

    public synchronized boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & 0x20) == 32) {
            this.lock = false;
            this.error = false;
            this.notifyAll();
            return false;
        }
        if ((infoflags & 0x80) == 128 || (infoflags & 0x40) == 64) {
            this.lock = false;
            this.error = true;
            this.notifyAll();
            return false;
        }
        return true;
    }

    public synchronized void waitImageLoaded() {
        if (!this.lock) {
            return;
        }
        BufferedImage img = new BufferedImage(1, 1, 1);
        Graphics g = img.getGraphics();
        while (this.lock) {
            if (g.drawImage(this.image, 0, 0, img.getWidth(this), img.getHeight(this), this)) {
                return;
            }
            try {
                this.wait(500L);
            }
            catch (InterruptedException e) {
                Log.info("WaitingImageObserver.waitImageLoaded(): InterruptedException thrown", e);
            }
        }
    }

    public Object clone() throws CloneNotSupportedException {
        return (WaitingImageObserver)super.clone();
    }

    public boolean isLoadingComplete() {
        return !this.lock;
    }

    public boolean isError() {
        return this.error;
    }
}

