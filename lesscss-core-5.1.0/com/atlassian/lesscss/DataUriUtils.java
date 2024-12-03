/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.codec.binary.Base64
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.lesscss;

import com.google.common.collect.ImmutableMap;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataUriUtils {
    private static final int MAX_BYTES = 32768;
    private static final Logger log = LoggerFactory.getLogger(DataUriUtils.class);
    private static final Map<String, String> FILE_EXTENSION_TO_MIME_TYPE = ImmutableMap.builder().put((Object)"gif", (Object)"image/gif").put((Object)"jpeg", (Object)"image/jpeg").put((Object)"jpg", (Object)"image/jpeg").put((Object)"png", (Object)"image/png").put((Object)"svg", (Object)"image/svg+xml").build();

    private DataUriUtils() {
        throw new UnsupportedOperationException();
    }

    public static String dataUri(String mimeType, byte[] bytes) {
        if (DataUriUtils.canOptimize(mimeType)) {
            bytes = DataUriUtils.optimize(bytes);
        }
        if (bytes.length >= 32768) {
            log.info("Image exceeded limit and cannot be data-uri encoded. limit = {}, size = {}", (Object)32768, (Object)bytes.length);
            return null;
        }
        return "\"data:" + mimeType + ";base64," + DataUriUtils.base64Encode(bytes) + "\"";
    }

    public static String guessMimeType(String path) {
        String extension;
        String matchingMimeType;
        String mimeType = "application/octet-stream";
        int dotIndex = path.lastIndexOf(46);
        if (dotIndex != -1 && (matchingMimeType = FILE_EXTENSION_TO_MIME_TYPE.get(extension = path.substring(dotIndex + 1).toLowerCase(Locale.US))) != null) {
            mimeType = matchingMimeType;
        }
        return mimeType;
    }

    private static String base64Encode(byte[] bytes) {
        return new String(Base64.encodeBase64((byte[])bytes));
    }

    private static boolean canOptimize(String mimeType) {
        return "image/png".equals(mimeType);
    }

    private static byte[] optimize(byte[] bytes) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write((RenderedImage)image, "png", out);
            return out.toByteArray();
        }
        catch (IOException e) {
            log.info("Failed to optimise image. Falling back to unoptimised version", (Throwable)e);
            return bytes;
        }
    }
}

