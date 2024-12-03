/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.Service
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 */
package org.apache.batik.ext.awt.image.spi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StreamCorruptedException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.apache.batik.ext.awt.image.URLImageCache;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.ProfileRable;
import org.apache.batik.ext.awt.image.spi.BrokenLinkProvider;
import org.apache.batik.ext.awt.image.spi.DefaultBrokenLinkProvider;
import org.apache.batik.ext.awt.image.spi.ErrorConstants;
import org.apache.batik.ext.awt.image.spi.JDKRegistryEntry;
import org.apache.batik.ext.awt.image.spi.RegistryEntry;
import org.apache.batik.ext.awt.image.spi.StreamRegistryEntry;
import org.apache.batik.ext.awt.image.spi.URLRegistryEntry;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.Service;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;

public class ImageTagRegistry
implements ErrorConstants {
    List entries = new LinkedList();
    List extensions = null;
    List mimeTypes = null;
    URLImageCache rawCache;
    URLImageCache imgCache;
    static ImageTagRegistry registry = null;
    static BrokenLinkProvider defaultProvider = new DefaultBrokenLinkProvider();
    static BrokenLinkProvider brokenLinkProvider = null;

    public ImageTagRegistry() {
        this(null, null);
    }

    public ImageTagRegistry(URLImageCache rawCache, URLImageCache imgCache) {
        if (rawCache == null) {
            rawCache = new URLImageCache();
        }
        if (imgCache == null) {
            imgCache = new URLImageCache();
        }
        this.rawCache = rawCache;
        this.imgCache = imgCache;
    }

    public void flushCache() {
        this.rawCache.flush();
        this.imgCache.flush();
    }

    public void flushImage(ParsedURL purl) {
        this.rawCache.clear(purl);
        this.imgCache.clear(purl);
    }

    public Filter checkCache(ParsedURL purl, ICCColorSpaceWithIntent colorSpace) {
        boolean needRawData = colorSpace != null;
        Filter ret = null;
        URLImageCache cache = needRawData ? this.rawCache : this.imgCache;
        ret = cache.request(purl);
        if (ret == null) {
            cache.clear(purl);
            return null;
        }
        if (colorSpace != null) {
            ret = new ProfileRable(ret, colorSpace);
        }
        return ret;
    }

    public Filter readURL(ParsedURL purl) {
        return this.readURL(null, purl, null, true, true);
    }

    public Filter readURL(ParsedURL purl, ICCColorSpaceWithIntent colorSpace) {
        return this.readURL(null, purl, colorSpace, true, true);
    }

    public Filter readURL(InputStream is, ParsedURL purl, ICCColorSpaceWithIntent colorSpace, boolean allowOpenStream, boolean returnBrokenLink) {
        if (is != null && !is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        boolean needRawData = colorSpace != null;
        Filter ret = null;
        URLImageCache cache = null;
        if (purl != null && (ret = (cache = needRawData ? this.rawCache : this.imgCache).request(purl)) != null) {
            if (colorSpace != null) {
                ret = new ProfileRable(ret, colorSpace);
            }
            return ret;
        }
        boolean openFailed = false;
        List mimeTypes = this.getRegisteredMimeTypes();
        for (RegistryEntry re : this.entries) {
            if (re instanceof URLRegistryEntry) {
                URLRegistryEntry ure;
                if (purl == null || !allowOpenStream || !(ure = (URLRegistryEntry)re).isCompatibleURL(purl) || (ret = ure.handleURL(purl, needRawData)) == null) continue;
                break;
            }
            if (!(re instanceof StreamRegistryEntry)) continue;
            StreamRegistryEntry sre = (StreamRegistryEntry)re;
            if (openFailed) continue;
            try {
                if (is == null) {
                    if (purl == null || !allowOpenStream) break;
                    try {
                        is = purl.openStream(mimeTypes.iterator());
                    }
                    catch (IOException ioe) {
                        openFailed = true;
                        continue;
                    }
                    if (!is.markSupported()) {
                        is = new BufferedInputStream(is);
                    }
                }
                if (!sre.isCompatibleStream(is) || (ret = sre.handleStream(is, purl, needRawData)) == null) continue;
                break;
            }
            catch (StreamCorruptedException sce) {
                is = null;
            }
        }
        if (cache != null) {
            cache.put(purl, ret);
        }
        if (ret == null) {
            if (!returnBrokenLink) {
                return null;
            }
            if (openFailed) {
                return ImageTagRegistry.getBrokenLinkImage(this, "url.unreachable", null);
            }
            return ImageTagRegistry.getBrokenLinkImage(this, "url.uninterpretable", null);
        }
        if (BrokenLinkProvider.hasBrokenLinkProperty(ret)) {
            return returnBrokenLink ? ret : null;
        }
        if (colorSpace != null) {
            ret = new ProfileRable(ret, colorSpace);
        }
        return ret;
    }

    public Filter readStream(InputStream is) {
        return this.readStream(is, null);
    }

    public Filter readStream(InputStream is, ICCColorSpaceWithIntent colorSpace) {
        if (!is.markSupported()) {
            is = new BufferedInputStream(is);
        }
        boolean needRawData = colorSpace != null;
        Filter ret = null;
        for (Object entry : this.entries) {
            RegistryEntry re = (RegistryEntry)entry;
            if (!(re instanceof StreamRegistryEntry)) continue;
            StreamRegistryEntry sre = (StreamRegistryEntry)re;
            try {
                if (!sre.isCompatibleStream(is) || (ret = sre.handleStream(is, null, needRawData)) == null) continue;
            }
            catch (StreamCorruptedException sce) {}
            break;
        }
        if (ret == null) {
            return ImageTagRegistry.getBrokenLinkImage(this, "stream.unreadable", null);
        }
        if (colorSpace != null && !BrokenLinkProvider.hasBrokenLinkProperty(ret)) {
            ret = new ProfileRable(ret, colorSpace);
        }
        return ret;
    }

    public synchronized void register(RegistryEntry newRE) {
        float priority = newRE.getPriority();
        ListIterator<RegistryEntry> li = this.entries.listIterator();
        while (li.hasNext()) {
            RegistryEntry re = (RegistryEntry)li.next();
            if (!(re.getPriority() > priority)) continue;
            li.previous();
            break;
        }
        li.add(newRE);
        this.extensions = null;
        this.mimeTypes = null;
    }

    public synchronized List getRegisteredExtensions() {
        if (this.extensions != null) {
            return this.extensions;
        }
        this.extensions = new LinkedList();
        for (Object entry : this.entries) {
            RegistryEntry re = (RegistryEntry)entry;
            this.extensions.addAll(re.getStandardExtensions());
        }
        this.extensions = Collections.unmodifiableList(this.extensions);
        return this.extensions;
    }

    public synchronized List getRegisteredMimeTypes() {
        if (this.mimeTypes != null) {
            return this.mimeTypes;
        }
        this.mimeTypes = new LinkedList();
        for (Object entry : this.entries) {
            RegistryEntry re = (RegistryEntry)entry;
            this.mimeTypes.addAll(re.getMimeTypes());
        }
        this.mimeTypes = Collections.unmodifiableList(this.mimeTypes);
        return this.mimeTypes;
    }

    public static synchronized ImageTagRegistry getRegistry() {
        if (registry != null) {
            return registry;
        }
        registry = new ImageTagRegistry();
        registry.register(new JDKRegistryEntry());
        Iterator iter = Service.providers(RegistryEntry.class);
        while (iter.hasNext()) {
            RegistryEntry re = (RegistryEntry)iter.next();
            registry.register(re);
        }
        return registry;
    }

    public static synchronized Filter getBrokenLinkImage(Object base, String code, Object[] params) {
        Filter ret = null;
        if (brokenLinkProvider != null) {
            ret = brokenLinkProvider.getBrokenLinkImage(base, code, params);
        }
        if (ret == null) {
            ret = defaultProvider.getBrokenLinkImage(base, code, params);
        }
        return ret;
    }

    public static synchronized void setBrokenLinkProvider(BrokenLinkProvider provider) {
        brokenLinkProvider = provider;
    }
}

