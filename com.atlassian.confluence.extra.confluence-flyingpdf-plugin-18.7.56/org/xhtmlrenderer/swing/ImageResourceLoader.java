/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.swing.AWTFSImage;
import org.xhtmlrenderer.swing.ImageLoadQueue;
import org.xhtmlrenderer.swing.ImageLoadWorker;
import org.xhtmlrenderer.swing.MutableFSImage;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.ImageUtil;
import org.xhtmlrenderer.util.StreamResource;
import org.xhtmlrenderer.util.XRLog;

public class ImageResourceLoader {
    public static final RepaintListener NO_OP_REPAINT_LISTENER = new RepaintListener(){

        @Override
        public void repaintRequested(boolean doLayout) {
            XRLog.general(Level.FINE, "No-op repaint requested");
        }
    };
    private final Map _imageCache;
    private final ImageLoadQueue _loadQueue;
    private final int _imageCacheCapacity;
    private RepaintListener _repaintListener = NO_OP_REPAINT_LISTENER;
    private final boolean _useBackgroundImageLoading;

    public ImageResourceLoader() {
        this(16);
    }

    public ImageResourceLoader(int cacheSize) {
        this._imageCacheCapacity = cacheSize;
        this._useBackgroundImageLoading = Configuration.isTrue("xr.image.background.loading.enable", false);
        if (this._useBackgroundImageLoading) {
            this._loadQueue = new ImageLoadQueue();
            int workerCount = Configuration.valueAsInt("xr.image.background.workers", 5);
            for (int i = 0; i < workerCount; ++i) {
                new ImageLoadWorker(this._loadQueue).start();
            }
        } else {
            this._loadQueue = null;
        }
        this._repaintListener = NO_OP_REPAINT_LISTENER;
        this._imageCache = new LinkedHashMap(cacheSize, 0.75f, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static ImageResource loadImageResourceFromUri(String uri) {
        if (ImageUtil.isEmbeddedBase64Image(uri)) {
            return ImageResourceLoader.loadEmbeddedBase64ImageResource(uri);
        }
        StreamResource sr = new StreamResource(uri);
        ImageResource ir = null;
        try {
            sr.connect();
            BufferedInputStream is = sr.bufferedStream();
            try {
                BufferedImage img = ImageIO.read(is);
                if (img == null) {
                    throw new IOException("ImageIO.read() returned null");
                }
                ir = ImageResourceLoader.createImageResource(uri, img);
            }
            catch (FileNotFoundException e) {
                XRLog.exception("Can't read image file; image at URI '" + uri + "' not found");
            }
            catch (IOException e) {
                XRLog.exception("Can't read image file; unexpected problem for URI '" + uri + "'", e);
            }
            finally {
                sr.close();
            }
        }
        catch (IOException e) {
            XRLog.exception("Can't open stream for URI '" + uri + "': " + e.getMessage());
        }
        if (ir == null) {
            ir = ImageResourceLoader.createImageResource(uri, null);
        }
        return ir;
    }

    public static ImageResource loadEmbeddedBase64ImageResource(String uri) {
        BufferedImage bufferedImage = ImageUtil.loadEmbeddedBase64Image(uri);
        if (bufferedImage != null) {
            FSImage image = AWTFSImage.createImage(bufferedImage);
            return new ImageResource(null, image);
        }
        return new ImageResource(null, null);
    }

    public synchronized void shrink() {
        int ovr = this._imageCache.size() - this._imageCacheCapacity;
        Iterator it = this._imageCache.keySet().iterator();
        while (it.hasNext() && ovr-- > 0) {
            it.next();
            it.remove();
        }
    }

    public synchronized void clear() {
        this._imageCache.clear();
    }

    public ImageResource get(String uri) {
        return this.get(uri, -1, -1);
    }

    public synchronized ImageResource get(String uri, int width, int height) {
        if (ImageUtil.isEmbeddedBase64Image(uri)) {
            ImageResource resource = ImageResourceLoader.loadEmbeddedBase64ImageResource(uri);
            resource.getImage().scale(width, height);
            return resource;
        }
        CacheKey key = new CacheKey(uri, width, height);
        ImageResource ir = (ImageResource)this._imageCache.get(key);
        if (ir == null) {
            ir = (ImageResource)this._imageCache.get(new CacheKey(uri, -1, -1));
            if (ir == null) {
                if (this.isImmediateLoadUri(uri)) {
                    XRLog.load(Level.FINE, "Load immediate: " + uri);
                    ir = ImageResourceLoader.loadImageResourceFromUri(uri);
                    FSImage awtfsImage = ir.getImage();
                    BufferedImage newImg = ((AWTFSImage)awtfsImage).getImage();
                    this.loaded(ir, -1, -1);
                    if (width > -1 && height > -1) {
                        XRLog.load(Level.FINE, this + ", scaling " + uri + " to " + width + ", " + height);
                        newImg = ImageUtil.getScaledInstance(newImg, width, height);
                        ir = new ImageResource(ir.getImageUri(), AWTFSImage.createImage(newImg));
                        this.loaded(ir, width, height);
                    }
                } else {
                    XRLog.load(Level.FINE, "Image cache miss, URI not yet loaded, queueing: " + uri);
                    MutableFSImage mfsi = new MutableFSImage(this._repaintListener);
                    ir = new ImageResource(uri, mfsi);
                    this._loadQueue.addToQueue(this, uri, mfsi, width, height);
                }
                this._imageCache.put(key, ir);
            } else {
                XRLog.load(Level.FINE, this + ", scaling " + uri + " to " + width + ", " + height);
                FSImage awtfsImage = ir.getImage();
                BufferedImage newImg = ((AWTFSImage)awtfsImage).getImage();
                newImg = ImageUtil.getScaledInstance(newImg, width, height);
                ir = new ImageResource(ir.getImageUri(), AWTFSImage.createImage(newImg));
                this.loaded(ir, width, height);
            }
        }
        return ir;
    }

    public boolean isImmediateLoadUri(String uri) {
        return !this._useBackgroundImageLoading || uri.startsWith("jar:file:") || uri.startsWith("file:");
    }

    public synchronized void loaded(ImageResource ir, int width, int height) {
        String imageUri = ir.getImageUri();
        if (imageUri != null) {
            this._imageCache.put(new CacheKey(imageUri, width, height), ir);
        }
    }

    public static ImageResource createImageResource(String uri, BufferedImage img) {
        if (img == null) {
            return new ImageResource(uri, AWTFSImage.createImage(ImageUtil.createTransparentImage(10, 10)));
        }
        return new ImageResource(uri, AWTFSImage.createImage(ImageUtil.makeCompatible(img)));
    }

    public void setRepaintListener(RepaintListener repaintListener) {
        this._repaintListener = repaintListener;
    }

    public void stopLoading() {
        if (this._loadQueue != null) {
            XRLog.load("By request, clearing pending items from load queue: " + this._loadQueue.size());
            this._loadQueue.reset();
        }
    }

    private static class CacheKey {
        final String uri;
        final int width;
        final int height;

        public CacheKey(String uri, int width, int height) {
            this.uri = uri;
            this.width = width;
            this.height = height;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            if (this.height != cacheKey.height) {
                return false;
            }
            if (this.width != cacheKey.width) {
                return false;
            }
            return this.uri.equals(cacheKey.uri);
        }

        public int hashCode() {
            int result = this.uri.hashCode();
            result = 31 * result + this.width;
            result = 31 * result + this.height;
            return result;
        }
    }
}

