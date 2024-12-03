/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.ParsedURL
 *  org.apache.batik.util.SoftReferenceCache
 */
package org.apache.batik.ext.awt.image;

import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SoftReferenceCache;

public class URLImageCache
extends SoftReferenceCache {
    static URLImageCache theCache = new URLImageCache();

    public static URLImageCache getDefaultCache() {
        return theCache;
    }

    public synchronized boolean isPresent(ParsedURL purl) {
        return super.isPresentImpl((Object)purl);
    }

    public synchronized boolean isDone(ParsedURL purl) {
        return super.isDoneImpl((Object)purl);
    }

    public synchronized Filter request(ParsedURL purl) {
        return (Filter)super.requestImpl((Object)purl);
    }

    public synchronized void clear(ParsedURL purl) {
        super.clearImpl((Object)purl);
    }

    public synchronized void put(ParsedURL purl, Filter filt) {
        super.putImpl((Object)purl, (Object)filt);
    }
}

