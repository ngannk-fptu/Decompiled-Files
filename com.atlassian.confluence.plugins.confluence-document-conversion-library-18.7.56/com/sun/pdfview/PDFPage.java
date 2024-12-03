/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview;

import com.sun.pdfview.BaseWatchable;
import com.sun.pdfview.Cache;
import com.sun.pdfview.ImageInfo;
import com.sun.pdfview.PDFChangeStrokeCmd;
import com.sun.pdfview.PDFCmd;
import com.sun.pdfview.PDFFillAlphaCmd;
import com.sun.pdfview.PDFFillPaintCmd;
import com.sun.pdfview.PDFImage;
import com.sun.pdfview.PDFImageCmd;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFPopCmd;
import com.sun.pdfview.PDFPushCmd;
import com.sun.pdfview.PDFRenderer;
import com.sun.pdfview.PDFShapeCmd;
import com.sun.pdfview.PDFStrokeAlphaCmd;
import com.sun.pdfview.PDFStrokePaintCmd;
import com.sun.pdfview.PDFXformCmd;
import com.sun.pdfview.RefImage;
import com.sun.pdfview.annotation.PDFAnnotation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PDFPage {
    private List<PDFCmd> commands;
    private boolean finished = false;
    private int pageNumber;
    private Rectangle2D bbox;
    private int rotation;
    private Cache cache;
    private Map<ImageInfo, WeakReference> renderers;
    private List<PDFAnnotation> annots;

    public PDFPage(Rectangle2D bbox, int rotation) {
        this(-1, bbox, rotation, null);
    }

    public PDFPage(int pageNumber, Rectangle2D bbox, int rotation, Cache cache) {
        this.pageNumber = pageNumber;
        this.cache = cache;
        if (bbox == null) {
            bbox = new Rectangle2D.Float(0.0f, 0.0f, 1.0f, 1.0f);
        }
        if (rotation < 0) {
            rotation += 360;
        }
        this.rotation = rotation;
        if (rotation == 90 || rotation == 270) {
            bbox = new Rectangle2D.Double(bbox.getX(), bbox.getY(), bbox.getHeight(), bbox.getWidth());
        }
        this.bbox = bbox;
        this.renderers = Collections.synchronizedMap(new HashMap());
        this.commands = Collections.synchronizedList(new ArrayList(250));
    }

    public Dimension getUnstretchedSize(int width, int height, Rectangle2D clip) {
        if (clip == null) {
            clip = this.bbox;
        } else if (this.getRotation() == 90 || this.getRotation() == 270) {
            clip = new Rectangle2D.Double(clip.getX(), clip.getY(), clip.getHeight(), clip.getWidth());
        }
        double ratio = clip.getHeight() / clip.getWidth();
        double askratio = (double)height / (double)width;
        if (askratio > ratio) {
            height = (int)((double)width * ratio + 0.5);
        } else {
            width = (int)((double)height / ratio + 0.5);
        }
        return new Dimension(width, height);
    }

    public Image getImage(int width, int height, Rectangle2D clip, ImageObserver observer) {
        return this.getImage(width, height, clip, observer, true, false);
    }

    public Image getImage(int width, int height, Rectangle2D clip, ImageObserver observer, boolean drawbg, boolean wait) {
        BufferedImage image = null;
        BaseWatchable renderer = null;
        ImageInfo info = new ImageInfo(width, height, clip, null);
        if (this.cache != null) {
            image = this.cache.getImage(this, info);
            renderer = this.cache.getImageRenderer(this, info);
        }
        if (image == null) {
            if (drawbg) {
                info.bgColor = Color.WHITE;
            }
            image = new RefImage(info.width, info.height, 2);
            renderer = new PDFRenderer(this, info, image);
            if (this.cache != null) {
                this.cache.addImage(this, info, image, (PDFRenderer)renderer);
            }
            this.renderers.put(info, new WeakReference<BaseWatchable>(renderer));
        }
        if (renderer != null) {
            if (observer != null) {
                ((PDFRenderer)renderer).addObserver(observer);
            }
            if (!renderer.isFinished()) {
                renderer.go(wait);
            }
        }
        return image;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public float getAspectRatio() {
        return this.getWidth() / this.getHeight();
    }

    public Rectangle2D getBBox() {
        return this.bbox;
    }

    public float getWidth() {
        return (float)this.bbox.getWidth();
    }

    public float getHeight() {
        return (float)this.bbox.getHeight();
    }

    public int getRotation() {
        return this.rotation;
    }

    public AffineTransform getInitialTransform(int width, int height, Rectangle2D clip) {
        AffineTransform at = new AffineTransform();
        switch (this.getRotation()) {
            case 0: {
                at = new AffineTransform(1.0f, 0.0f, 0.0f, -1.0f, 0.0f, height);
                break;
            }
            case 90: {
                at = new AffineTransform(0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f);
                break;
            }
            case 180: {
                at = new AffineTransform(-1.0f, 0.0f, 0.0f, 1.0f, width, 0.0f);
                break;
            }
            case 270: {
                at = new AffineTransform(0.0f, -1.0f, -1.0f, 0.0f, width, height);
            }
        }
        if (clip == null) {
            clip = this.getBBox();
        } else if (this.getRotation() == 90 || this.getRotation() == 270) {
            int tmp = width;
            width = height;
            height = tmp;
        }
        double scaleX = (double)width / clip.getWidth();
        double scaleY = (double)height / clip.getHeight();
        at.scale(scaleX, scaleY);
        at.translate(-clip.getMinX(), -clip.getMinY());
        return at;
    }

    public int getCommandCount() {
        return this.commands.size();
    }

    public PDFCmd getCommand(int index) {
        return this.commands.get(index);
    }

    public List<PDFCmd> getCommands() {
        return this.commands;
    }

    public List getCommands(int startIndex) {
        return this.getCommands(startIndex, this.getCommandCount());
    }

    public List getCommands(int startIndex, int endIndex) {
        return this.commands.subList(startIndex, endIndex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addCommand(PDFCmd cmd) {
        List<PDFCmd> list = this.commands;
        synchronized (list) {
            this.commands.add(cmd);
        }
        this.updateImages();
    }

    public void addCommands(PDFPage page) {
        this.addCommands(page, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addCommands(PDFPage page, AffineTransform extra) {
        List<PDFCmd> list = this.commands;
        synchronized (list) {
            this.addPush();
            if (extra != null) {
                this.addXform(extra);
            }
            this.commands.addAll(page.getCommands());
            this.addPop();
        }
        this.updateImages();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void clearCommands() {
        List<PDFCmd> list = this.commands;
        synchronized (list) {
            this.commands.clear();
        }
        this.updateImages();
    }

    public boolean isFinished() {
        return this.finished;
    }

    public synchronized void waitForFinish() throws InterruptedException {
        if (!this.finished) {
            this.wait();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void stop(int width, int height, Rectangle2D clip) {
        ImageInfo info = new ImageInfo(width, height, clip);
        Map<ImageInfo, WeakReference> map = this.renderers;
        synchronized (map) {
            PDFRenderer renderer;
            WeakReference rendererRef = this.renderers.get(info);
            if (rendererRef != null && (renderer = (PDFRenderer)rendererRef.get()) != null) {
                renderer.stop();
            }
        }
    }

    public synchronized void finish() {
        this.finished = true;
        this.notifyAll();
        this.updateImages();
    }

    public void addPush() {
        this.addCommand(new PDFPushCmd());
    }

    public void addPop() {
        this.addCommand(new PDFPopCmd());
    }

    public void addXform(AffineTransform at) {
        this.addCommand(new PDFXformCmd(new AffineTransform(at)));
    }

    public void addStrokeWidth(float w) {
        PDFChangeStrokeCmd sc = new PDFChangeStrokeCmd();
        sc.setWidth(w);
        this.addCommand(sc);
    }

    public void addEndCap(int capstyle) {
        PDFChangeStrokeCmd sc = new PDFChangeStrokeCmd();
        int cap = 0;
        switch (capstyle) {
            case 0: {
                cap = 0;
                break;
            }
            case 1: {
                cap = 1;
                break;
            }
            case 2: {
                cap = 2;
            }
        }
        sc.setEndCap(cap);
        this.addCommand(sc);
    }

    public void addLineJoin(int joinstyle) {
        PDFChangeStrokeCmd sc = new PDFChangeStrokeCmd();
        int join = 0;
        switch (joinstyle) {
            case 0: {
                join = 0;
                break;
            }
            case 1: {
                join = 1;
                break;
            }
            case 2: {
                join = 2;
            }
        }
        sc.setLineJoin(join);
        this.addCommand(sc);
    }

    public void addMiterLimit(float limit) {
        PDFChangeStrokeCmd sc = new PDFChangeStrokeCmd();
        sc.setMiterLimit(limit);
        this.addCommand(sc);
    }

    public void addDash(float[] dashary, float phase) {
        PDFChangeStrokeCmd sc = new PDFChangeStrokeCmd();
        sc.setDash(dashary, phase);
        this.addCommand(sc);
    }

    public void addPath(GeneralPath path, int style) {
        this.addCommand(new PDFShapeCmd(path, style));
    }

    public void addFillPaint(PDFPaint p) {
        this.addCommand(new PDFFillPaintCmd(p));
    }

    public void addStrokePaint(PDFPaint p) {
        this.addCommand(new PDFStrokePaintCmd(p));
    }

    public void addFillAlpha(float a) {
        this.addCommand(new PDFFillAlphaCmd(a));
    }

    public void addStrokeAlpha(float a) {
        this.addCommand(new PDFStrokeAlphaCmd(a));
    }

    public void addImage(PDFImage image) {
        this.addCommand(new PDFImageCmd(image));
    }

    public void updateImages() {
        for (WeakReference ref : this.renderers.values()) {
            PDFRenderer renderer = (PDFRenderer)ref.get();
            if (renderer == null || renderer.getStatus() != 3) continue;
            renderer.setStatus(2);
        }
    }

    public List<PDFAnnotation> getAnnots() {
        return this.annots;
    }

    public List<PDFAnnotation> getAnnots(PDFAnnotation.ANNOTATION_TYPE type) {
        ArrayList<PDFAnnotation> list = new ArrayList<PDFAnnotation>();
        if (this.annots != null) {
            for (PDFAnnotation annot : this.annots) {
                if (annot.getType() != type) continue;
                list.add(annot);
            }
        }
        return list;
    }

    public void setAnnots(List<PDFAnnotation> annots) {
        this.annots = annots;
        for (PDFAnnotation pdfAnnotation : annots) {
            List<PDFCmd> pcmd = pdfAnnotation.getPageCommandsForAnnotation();
            this.commands.addAll(0, pcmd);
        }
    }

    public static PDFImageCmd createImageCmd(PDFImage image) {
        return new PDFImageCmd(image);
    }

    public static PDFPushCmd createPushCmd() {
        return new PDFPushCmd();
    }

    public static PDFPopCmd createPopCmd() {
        return new PDFPopCmd();
    }

    public static PDFXformCmd createXFormCmd(AffineTransform at) {
        return new PDFXformCmd(new AffineTransform(at));
    }
}

