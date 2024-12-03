/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import org.hibernate.boot.model.source.spi.AbstractAttributeKey;

public class AttributePath
extends AbstractAttributeKey {
    public static final char DELIMITER = '.';

    public AttributePath() {
    }

    @Override
    protected char getDelimiter() {
        return '.';
    }

    @Override
    public AttributePath append(String property) {
        return new AttributePath(this, property);
    }

    @Override
    public AttributePath getParent() {
        return (AttributePath)super.getParent();
    }

    public AttributePath(AttributePath parent, String property) {
        super(parent, property);
    }

    public static AttributePath parse(String path) {
        if (path == null) {
            return null;
        }
        AttributePath attributePath = new AttributePath();
        for (String part : path.split("\\.")) {
            attributePath = attributePath.append(part);
        }
        return attributePath;
    }
}

