/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata;

import org.apache.tika.metadata.Property;

public interface TikaPagedText {
    public static final String TIKA_PAGED_TEXT_PREFIX = "tika_pg:";
    public static final Property PAGE_NUMBER = Property.internalInteger("tika_pg:page_number");
    public static final Property PAGE_ROTATION = Property.internalRational("tika_pg:page_rotation");
}

