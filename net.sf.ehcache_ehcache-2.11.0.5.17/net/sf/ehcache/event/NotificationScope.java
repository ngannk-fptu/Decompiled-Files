/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

public enum NotificationScope {
    LOCAL(true, false),
    REMOTE(false, true),
    ALL(true, true);

    private final boolean deliverLocal;
    private final boolean deliverRemote;

    private NotificationScope(boolean deliverLocal, boolean deliverRemote) {
        this.deliverLocal = deliverLocal;
        this.deliverRemote = deliverRemote;
    }

    public boolean shouldDeliver(boolean isRemote) {
        return isRemote && this.deliverRemote || !isRemote && this.deliverLocal;
    }
}

