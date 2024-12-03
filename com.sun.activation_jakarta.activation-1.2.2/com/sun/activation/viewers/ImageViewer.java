/*
 * Decompiled with CFR 0.152.
 */
package com.sun.activation.viewers;

import com.sun.activation.viewers.ImageViewerCanvas;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.CommandObject;
import javax.activation.DataHandler;

public class ImageViewer
extends Panel
implements CommandObject {
    private ImageViewerCanvas canvas = new ImageViewerCanvas();
    private Image image = null;
    private DataHandler _dh = null;
    private boolean DEBUG = false;

    public ImageViewer() {
        this.add(this.canvas);
    }

    @Override
    public void setCommandContext(String verb, DataHandler dh) throws IOException {
        this._dh = dh;
        this.setInputStream(this._dh.getInputStream());
    }

    private void setInputStream(InputStream ins) throws IOException {
        MediaTracker mt = new MediaTracker(this);
        int bytes_read = 0;
        byte[] data = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((bytes_read = ins.read(data)) > 0) {
            baos.write(data, 0, bytes_read);
        }
        ins.close();
        this.image = this.getToolkit().createImage(baos.toByteArray());
        mt.addImage(this.image, 0);
        try {
            mt.waitForID(0);
            mt.waitForAll();
            if (mt.statusID(0, true) != 8) {
                System.out.println("Error occured in image loading = " + mt.getErrorsID(0));
            }
        }
        catch (InterruptedException e) {
            throw new IOException("Error reading image data");
        }
        this.canvas.setImage(this.image);
        if (this.DEBUG) {
            System.out.println("calling invalidate");
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        this.invalidate();
        this.validate();
        this.doLayout();
    }

    @Override
    public Dimension getPreferredSize() {
        return this.canvas.getPreferredSize();
    }
}

