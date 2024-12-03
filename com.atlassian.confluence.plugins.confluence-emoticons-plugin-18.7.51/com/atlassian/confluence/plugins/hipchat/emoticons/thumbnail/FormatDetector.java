/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.conversion.convert.FileFormat
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail;

import com.atlassian.plugins.conversion.convert.FileFormat;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class FormatDetector {
    private static byte[] gif = new byte[]{71, 73, 70, 56};
    private static byte[] png = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static byte[] jpeg1 = new byte[]{-1, -40, -1, -18};
    private static byte[] jpeg2 = new byte[]{-1, -40, -1};

    public static Optional<FileFormat> detect(InputStream in) throws IOException {
        byte[] bytes = new byte[12];
        in.read(bytes, 0, 12);
        return FormatDetector.detect(bytes);
    }

    public static Optional<FileFormat> detect(byte[] bytes) {
        if (Objects.deepEquals(Arrays.copyOf(bytes, gif.length), gif)) {
            return Optional.of(FileFormat.GIF);
        }
        if (Objects.deepEquals(Arrays.copyOf(bytes, png.length), png)) {
            return Optional.of(FileFormat.PNG);
        }
        if (Objects.deepEquals(Arrays.copyOf(bytes, jpeg1.length), jpeg1)) {
            return Optional.of(FileFormat.JPG);
        }
        if (Objects.deepEquals(Arrays.copyOf(bytes, jpeg2.length), jpeg2)) {
            return Optional.of(FileFormat.JPG);
        }
        return Optional.empty();
    }
}

