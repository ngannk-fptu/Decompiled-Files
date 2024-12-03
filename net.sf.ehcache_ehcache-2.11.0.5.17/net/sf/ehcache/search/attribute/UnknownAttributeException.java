/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import net.sf.ehcache.search.SearchException;

public class UnknownAttributeException
extends SearchException {
    public UnknownAttributeException(String message) {
        super(message);
    }

    public UnknownAttributeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownAttributeException(Throwable cause) {
        super(cause);
    }
}

