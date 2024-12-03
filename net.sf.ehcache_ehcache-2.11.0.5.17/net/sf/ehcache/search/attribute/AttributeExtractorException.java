/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.attribute;

import java.io.Serializable;
import net.sf.ehcache.search.SearchException;

public class AttributeExtractorException
extends SearchException
implements Serializable {
    private static final long serialVersionUID = 5066522240394222152L;

    public AttributeExtractorException(String message) {
        super(message);
    }

    public AttributeExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

    public AttributeExtractorException(Throwable cause) {
        super(cause);
    }
}

