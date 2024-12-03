/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.dailysummary.content.popular;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import javax.imageio.ImageIO;

class SafeImageReader {
    private final InputStream stream;

    SafeImageReader(InputStream stream) {
        this.stream = stream;
    }

    BufferedImage read() throws IOException {
        BufferedImage image = ImageIO.read(this.stream);
        return Optional.ofNullable(image).orElseGet(this::fallbackImage);
    }

    private BufferedImage fallbackImage() {
        try {
            return ImageIO.read(this.fallbackImageStream());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream fallbackImageStream() {
        return this.getClass().getResourceAsStream("fallback.png");
    }
}

