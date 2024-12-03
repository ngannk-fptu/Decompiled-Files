/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.universalchardet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Objects;
import org.mozilla.universalchardet.UnicodeBOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

public final class ReaderFactory {
    private ReaderFactory() {
        throw new AssertionError((Object)"No instances allowed");
    }

    public static BufferedReader createBufferedReader(File file, Charset defaultCharset) throws IOException {
        Charset cs = Objects.requireNonNull(defaultCharset, "defaultCharset must be not null");
        String detectedEncoding = UniversalDetector.detectCharset(file);
        if (detectedEncoding != null) {
            cs = Charset.forName(detectedEncoding);
        }
        if (!cs.toString().contains("UTF")) {
            return Files.newBufferedReader(file.toPath(), cs);
        }
        Path path = file.toPath();
        return new BufferedReader(new InputStreamReader((InputStream)new UnicodeBOMInputStream(new BufferedInputStream(Files.newInputStream(path, new OpenOption[0]))), cs));
    }

    public static BufferedReader createBufferedReader(File file) throws IOException {
        return ReaderFactory.createBufferedReader(file, Charset.defaultCharset());
    }

    @Deprecated
    public static Reader createReaderFromFile(File file, Charset defaultCharset) throws IOException {
        return ReaderFactory.createBufferedReader(file, defaultCharset);
    }

    @Deprecated
    public static Reader createReaderFromFile(File file) throws IOException {
        return ReaderFactory.createReaderFromFile(file, Charset.defaultCharset());
    }
}

