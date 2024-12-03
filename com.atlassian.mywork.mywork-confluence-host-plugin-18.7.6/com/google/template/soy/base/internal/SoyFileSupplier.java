/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.io.CharSource
 *  com.google.common.io.Files
 *  com.google.common.io.Resources
 */
package com.google.template.soy.base.internal;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.template.soy.base.SoySyntaxException;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.base.internal.StableSoyFileSupplier;
import com.google.template.soy.base.internal.VolatileSoyFileSupplier;
import com.google.template.soy.internal.base.Pair;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

public interface SoyFileSupplier {
    public Pair<Reader, Version> open() throws IOException;

    public boolean hasChangedSince(Version var1);

    public SoyFileKind getSoyFileKind();

    public String getFilePath();

    public static final class Factory {
        public static SoyFileSupplier create(CharSource contentSource, SoyFileKind soyFileKind, String filePath) {
            return new StableSoyFileSupplier(contentSource, soyFileKind, filePath);
        }

        public static SoyFileSupplier create(File inputFile, SoyFileKind soyFileKind) {
            return Factory.create(Files.asCharSource((File)inputFile, (Charset)Charsets.UTF_8), soyFileKind, inputFile.getPath());
        }

        public static SoyFileSupplier create(URL inputFileUrl, SoyFileKind soyFileKind, String filePath) {
            if (inputFileUrl.getProtocol().equals("file")) {
                try {
                    return new VolatileSoyFileSupplier(new File(inputFileUrl.toURI()), soyFileKind);
                }
                catch (URISyntaxException e) {
                    throw SoySyntaxException.createWithoutMetaInfo("Error in URL Syntax " + inputFileUrl + ": " + e);
                }
            }
            return Factory.create(Resources.asCharSource((URL)inputFileUrl, (Charset)Charsets.UTF_8), soyFileKind, filePath);
        }

        public static SoyFileSupplier create(URL inputFileUrl, SoyFileKind soyFileKind) {
            return Factory.create(inputFileUrl, soyFileKind, inputFileUrl.toString());
        }

        public static SoyFileSupplier create(CharSequence content, SoyFileKind soyFileKind, String filePath) {
            return Factory.create(CharSource.wrap((CharSequence)content), soyFileKind, filePath);
        }

        private Factory() {
        }
    }

    public static interface Version {
        public static final Version STABLE_VERSION = new Version(){};

        public boolean equals(Object var1);
    }
}

