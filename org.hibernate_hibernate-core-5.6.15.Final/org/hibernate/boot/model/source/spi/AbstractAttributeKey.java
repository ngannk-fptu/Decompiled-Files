/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hibernate.internal.util.StringHelper;

public abstract class AbstractAttributeKey {
    private static final String COLLECTION_ELEMENT = "collection&&element";
    private static final String DOT_COLLECTION_ELEMENT = ".collection&&element";
    private static final Pattern DOT_COLLECTION_ELEMENT_PATTERN = Pattern.compile(".collection&&element", 16);
    private final AbstractAttributeKey parent;
    private final String property;
    private final String fullPath;
    private final int depth;

    protected AbstractAttributeKey() {
        this(null, "");
    }

    protected AbstractAttributeKey(String base) {
        this(null, base);
    }

    protected AbstractAttributeKey(AbstractAttributeKey parent, String property) {
        String prefix;
        this.parent = parent;
        this.property = property;
        if (parent != null) {
            String resolvedParent = parent.getFullPath();
            prefix = StringHelper.isEmpty(resolvedParent) ? "" : resolvedParent + this.getDelimiter();
            this.depth = parent.getDepth() + 1;
        } else {
            prefix = "";
            this.depth = 0;
        }
        this.fullPath = prefix + property;
    }

    public int getDepth() {
        return this.depth;
    }

    protected abstract char getDelimiter();

    public abstract AbstractAttributeKey append(String var1);

    public AbstractAttributeKey getParent() {
        return this.parent;
    }

    public String getProperty() {
        return this.property;
    }

    public String getFullPath() {
        return this.fullPath;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public boolean isCollectionElement() {
        return COLLECTION_ELEMENT.equals(this.property);
    }

    public boolean isPartOfCollectionElement() {
        return this.fullPath.contains(DOT_COLLECTION_ELEMENT);
    }

    public String stripCollectionElementMarker() {
        return DOT_COLLECTION_ELEMENT_PATTERN.matcher(this.fullPath).replaceAll(Matcher.quoteReplacement(""));
    }

    public String toString() {
        return this.getFullPath();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractAttributeKey that = (AbstractAttributeKey)o;
        return this.fullPath.equals(that.fullPath);
    }

    public int hashCode() {
        return this.fullPath.hashCode();
    }
}

