/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.gif;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.gif.GifImageMetadataItem;

public class GifImageMetadata
implements ImageMetadata {
    private static final String NEWLINE = System.getProperty("line.separator");
    private final int width;
    private final int height;
    private final List<GifImageMetadataItem> items;

    GifImageMetadata(int width, int height, List<GifImageMetadataItem> items) {
        this.width = width;
        this.height = height;
        this.items = Collections.unmodifiableList(new ArrayList<GifImageMetadataItem>(items));
    }

    @Override
    public String toString(String prefix) {
        prefix = prefix == null ? "" : prefix;
        StringBuilder result = new StringBuilder();
        result.append(String.format("%sGIF metadata:", prefix));
        result.append(String.format("%sWidth: %d%s", prefix, this.width, NEWLINE));
        result.append(String.format("%sHeight: %d%s", prefix, this.height, NEWLINE));
        result.append(NEWLINE);
        result.append(String.format("%sImages:", prefix));
        for (GifImageMetadataItem item : this.items) {
            result.append(NEWLINE);
            result.append(item.toString(prefix));
        }
        return result.toString();
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public List<GifImageMetadataItem> getItems() {
        return this.items;
    }
}

