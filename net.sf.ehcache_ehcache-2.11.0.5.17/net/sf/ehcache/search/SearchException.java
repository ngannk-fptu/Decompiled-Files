/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search;

import net.sf.ehcache.CacheException;

public class SearchException
extends CacheException {
    private static final long serialVersionUID = 6942653724476318512L;

    public SearchException(String message) {
        super(message);
    }

    public SearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SearchException(Throwable cause) {
        super(cause);
    }
}

