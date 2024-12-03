/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.util.SoftReferenceCache
 *  org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent
 */
package org.apache.batik.ext.awt.color;

import org.apache.batik.util.SoftReferenceCache;
import org.apache.xmlgraphics.java2d.color.ICCColorSpaceWithIntent;

public class NamedProfileCache
extends SoftReferenceCache {
    static NamedProfileCache theCache = new NamedProfileCache();

    public static NamedProfileCache getDefaultCache() {
        return theCache;
    }

    public NamedProfileCache() {
        super(true);
    }

    public synchronized boolean isPresent(String profileName) {
        return super.isPresentImpl((Object)profileName);
    }

    public synchronized boolean isDone(String profileName) {
        return super.isDoneImpl((Object)profileName);
    }

    public synchronized ICCColorSpaceWithIntent request(String profileName) {
        return (ICCColorSpaceWithIntent)super.requestImpl((Object)profileName);
    }

    public synchronized void clear(String profileName) {
        super.clearImpl((Object)profileName);
    }

    public synchronized void put(String profileName, ICCColorSpaceWithIntent bi) {
        super.putImpl((Object)profileName, (Object)bi);
    }
}

