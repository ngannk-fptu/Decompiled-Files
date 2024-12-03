/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.transform.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageManager;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.cache.DefaultExpirationPolicy;
import org.apache.xmlgraphics.image.loader.cache.ExpirationPolicy;
import org.apache.xmlgraphics.image.loader.cache.ImageCacheListener;
import org.apache.xmlgraphics.image.loader.cache.ImageKey;
import org.apache.xmlgraphics.image.loader.cache.TimeStampProvider;
import org.apache.xmlgraphics.image.loader.util.SoftMapCache;

public class ImageCache {
    protected static final Log log = LogFactory.getLog(ImageCache.class);
    private Map invalidURIs = Collections.synchronizedMap(new HashMap());
    private ExpirationPolicy invalidURIExpirationPolicy;
    private SoftMapCache imageInfos = new SoftMapCache(true);
    private SoftMapCache images = new SoftMapCache(true);
    private ImageCacheListener cacheListener;
    private TimeStampProvider timeStampProvider;
    private long lastHouseKeeping;
    private static final long ONE_HOUR = 3600000L;

    public ImageCache() {
        this(new TimeStampProvider(), new DefaultExpirationPolicy());
    }

    public ImageCache(TimeStampProvider timeStampProvider, ExpirationPolicy invalidURIExpirationPolicy) {
        this.timeStampProvider = timeStampProvider;
        this.invalidURIExpirationPolicy = invalidURIExpirationPolicy;
        this.lastHouseKeeping = this.timeStampProvider.getTimeStamp();
    }

    public void setCacheListener(ImageCacheListener listener) {
        this.cacheListener = listener;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ImageInfo needImageInfo(String uri, ImageSessionContext session, ImageManager manager) throws ImageException, IOException {
        String lockURI;
        if (this.isInvalidURI(uri)) {
            throw new FileNotFoundException("Image not found: " + uri);
        }
        String string = lockURI = uri.intern();
        synchronized (string) {
            ImageInfo info = this.getImageInfo(uri);
            if (info == null) {
                try {
                    Source src = session.needSource(uri);
                    if (src == null) {
                        this.registerInvalidURI(uri);
                        throw new FileNotFoundException("Image not found: " + uri);
                    }
                    info = manager.preloadImage(uri, src);
                    session.returnSource(uri, src);
                }
                catch (IOException ioe) {
                    this.registerInvalidURI(uri);
                    throw ioe;
                }
                catch (ImageException e) {
                    this.registerInvalidURI(uri);
                    throw e;
                }
                if (info.getOriginalImage() == null || info.getOriginalImage().isCacheable()) {
                    this.putImageInfo(info);
                }
            }
            return info;
        }
    }

    public boolean isInvalidURI(String uri) {
        boolean expired = this.removeInvalidURIIfExpired(uri);
        if (expired) {
            return false;
        }
        if (this.cacheListener != null) {
            this.cacheListener.invalidHit(uri);
        }
        return true;
    }

    private boolean removeInvalidURIIfExpired(String uri) {
        boolean expired;
        Long timestamp = (Long)this.invalidURIs.get(uri);
        boolean bl = expired = timestamp == null || this.invalidURIExpirationPolicy.isExpired(this.timeStampProvider, timestamp);
        if (expired) {
            this.invalidURIs.remove(uri);
        }
        return expired;
    }

    protected ImageInfo getImageInfo(String uri) {
        ImageInfo info = (ImageInfo)this.imageInfos.get(uri);
        if (this.cacheListener != null) {
            if (info != null) {
                this.cacheListener.cacheHitImageInfo(uri);
            } else if (!this.isInvalidURI(uri)) {
                this.cacheListener.cacheMissImageInfo(uri);
            }
        }
        return info;
    }

    protected void putImageInfo(ImageInfo info) {
        this.imageInfos.put(info.getOriginalURI(), info);
    }

    void registerInvalidURI(String uri) {
        this.invalidURIs.put(uri, this.timeStampProvider.getTimeStamp());
        this.considerHouseKeeping();
    }

    public Image getImage(ImageInfo info, ImageFlavor flavor) {
        return this.getImage(info.getOriginalURI(), flavor);
    }

    public Image getImage(String uri, ImageFlavor flavor) {
        if (uri == null || "".equals(uri)) {
            return null;
        }
        ImageKey key = new ImageKey(uri, flavor);
        Image img = (Image)this.images.get(key);
        if (this.cacheListener != null) {
            if (img != null) {
                this.cacheListener.cacheHitImage(key);
            } else {
                this.cacheListener.cacheMissImage(key);
            }
        }
        return img;
    }

    public void putImage(Image img) {
        String originalURI = img.getInfo().getOriginalURI();
        if (originalURI == null || "".equals(originalURI)) {
            return;
        }
        if (!img.isCacheable()) {
            throw new IllegalArgumentException("Image is not cacheable! (Flavor: " + img.getFlavor() + ")");
        }
        ImageKey key = new ImageKey(originalURI, img.getFlavor());
        this.images.put(key, img);
    }

    public void clearCache() {
        this.invalidURIs.clear();
        this.imageInfos.clear();
        this.images.clear();
        this.doHouseKeeping();
    }

    private void considerHouseKeeping() {
        long ts = this.timeStampProvider.getTimeStamp();
        if (this.lastHouseKeeping + 3600000L > ts) {
            this.lastHouseKeeping = ts;
            this.doHouseKeeping();
        }
    }

    public void doHouseKeeping() {
        this.imageInfos.doHouseKeeping();
        this.images.doHouseKeeping();
        this.doInvalidURIHouseKeeping();
    }

    private void doInvalidURIHouseKeeping() {
        HashSet currentEntries = new HashSet(this.invalidURIs.keySet());
        for (Object currentEntry : currentEntries) {
            String key = (String)currentEntry;
            this.removeInvalidURIIfExpired(key);
        }
    }
}

