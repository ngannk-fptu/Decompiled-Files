/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.util.List;

public interface ImageMetadata {
    public String toString(String var1);

    public List<? extends ImageMetadataItem> getItems();

    public static interface ImageMetadataItem {
        public String toString(String var1);

        public String toString();
    }
}

