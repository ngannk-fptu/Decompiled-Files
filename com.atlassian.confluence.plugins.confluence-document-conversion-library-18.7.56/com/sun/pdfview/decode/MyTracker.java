/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decode;

import java.awt.Image;
import java.awt.image.ImageObserver;

class MyTracker
implements ImageObserver {
    boolean done = false;

    public MyTracker(Image img) {
        img.getWidth(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & 0xE0) != 0) {
            MyTracker myTracker = this;
            synchronized (myTracker) {
                this.done = true;
                this.notifyAll();
            }
            return false;
        }
        return true;
    }

    public synchronized void waitForAll() {
        if (!this.done) {
            try {
                this.wait();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }
}

