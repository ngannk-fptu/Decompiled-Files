/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.imaging.common.ImageMetadata;

public class GenericImageMetadata
implements ImageMetadata {
    private static final String NEWLINE = System.getProperty("line.separator");
    private final List<ImageMetadata.ImageMetadataItem> items = new ArrayList<ImageMetadata.ImageMetadataItem>();

    public void add(String keyword, String text) {
        this.add(new GenericImageMetadataItem(keyword, text));
    }

    public void add(ImageMetadata.ImageMetadataItem item) {
        this.items.add(item);
    }

    @Override
    public List<? extends ImageMetadata.ImageMetadataItem> getItems() {
        return new ArrayList<ImageMetadata.ImageMetadataItem>(this.items);
    }

    public String toString() {
        return this.toString(null);
    }

    @Override
    public String toString(String prefix) {
        if (null == prefix) {
            prefix = "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < this.items.size(); ++i) {
            if (i > 0) {
                result.append(NEWLINE);
            }
            ImageMetadata.ImageMetadataItem item = this.items.get(i);
            result.append(item.toString(prefix + "\t"));
        }
        return result.toString();
    }

    public static class GenericImageMetadataItem
    implements ImageMetadata.ImageMetadataItem {
        private final String keyword;
        private final String text;

        public GenericImageMetadataItem(String keyword, String text) {
            this.keyword = keyword;
            this.text = text;
        }

        public String getKeyword() {
            return this.keyword;
        }

        public String getText() {
            return this.text;
        }

        @Override
        public String toString() {
            return this.toString(null);
        }

        @Override
        public String toString(String prefix) {
            String result = this.keyword + ": " + this.text;
            if (null != prefix) {
                return prefix + result;
            }
            return result;
        }
    }
}

