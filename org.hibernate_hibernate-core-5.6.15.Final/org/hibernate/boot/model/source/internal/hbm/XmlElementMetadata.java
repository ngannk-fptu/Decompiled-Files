/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Locale;

public enum XmlElementMetadata {
    ID(true, true),
    COMPOSITE_ID(false, true),
    DISCRIMINATOR(true, false),
    MULTI_TENANCY(true, false),
    VERSION(true, true),
    TIMESTAMP(true, true),
    NATURAL_ID(false, false),
    PROPERTIES(false, true),
    PROPERTY(false, true),
    KEY_PROPERTY(false, true),
    MANY_TO_ONE(false, true),
    KEY_MANY_TO_ONE(false, true),
    ONE_TO_ONE(false, true),
    ANY(false, true),
    COMPONENT(false, true),
    KEY(false, false),
    SET(false, true),
    LIST(false, true),
    BAG(false, true),
    ID_BAG(false, true),
    MAP(false, true),
    ARRAY(false, true),
    PRIMITIVE_ARRAY(false, true),
    COLLECTION_ID(true, false),
    ELEMENT(false, false),
    MANY_TO_MANY(false, false),
    MANY_TO_ANY(false, false),
    MAP_KEY(false, false),
    MAP_KEY_MANY_TO_MANY(false, false),
    INDEX(false, false),
    INDEX_MANY_TO_MANY(false, false),
    LIST_INDEX(true, false);

    private final boolean inherentlySingleColumn;
    private final boolean canBeNamed;

    private XmlElementMetadata(boolean inherentlySingleColumn, boolean canBeNamed) {
        this.inherentlySingleColumn = inherentlySingleColumn;
        this.canBeNamed = canBeNamed;
    }

    public String getElementName() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public boolean isInherentlySingleColumn() {
        return this.inherentlySingleColumn;
    }

    public boolean canBeNamed() {
        return this.canBeNamed;
    }
}

