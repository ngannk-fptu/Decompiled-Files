/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.util.LinkedList;
import java.util.logging.Level;
import org.xhtmlrenderer.swing.ImageLoadItem;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.MutableFSImage;
import org.xhtmlrenderer.util.XRLog;

class ImageLoadQueue {
    private static final ImageLoadItem KILL_SWITCH = new ImageLoadItem(null, null, null, -1, -1);
    private final LinkedList _loadQueue = new LinkedList();

    public synchronized void addToQueue(ImageResourceLoader imageResourceLoader, String uri, MutableFSImage mfsi, int width, int height) {
        XRLog.general(Level.FINE, "Queueing load for image uri " + uri);
        this._loadQueue.addLast(new ImageLoadItem(imageResourceLoader, uri, mfsi, width, height));
        this.notifyAll();
    }

    public synchronized ImageLoadItem getTask() throws InterruptedException {
        while (this._loadQueue.isEmpty()) {
            this.wait();
        }
        if (this._loadQueue.getLast() == KILL_SWITCH) {
            XRLog.general(Level.FINE, "Thread " + Thread.currentThread().getName() + " requested item, but queue is shutting down; returning kill switch.");
            return KILL_SWITCH;
        }
        ImageLoadItem item = (ImageLoadItem)this._loadQueue.removeLast();
        XRLog.general(Level.FINE, "Thread " + Thread.currentThread().getName() + " pulled item " + item._uri + " from queue, " + (this._loadQueue.size() - 1) + " remaining");
        return item;
    }

    public synchronized void reset() {
        this._loadQueue.clear();
    }

    public synchronized void kill() {
        this._loadQueue.addLast(KILL_SWITCH);
        this.notifyAll();
    }

    public static boolean isKillSwitch(Object queueItem) {
        return queueItem == KILL_SWITCH;
    }

    public int size() {
        return this._loadQueue.size();
    }
}

