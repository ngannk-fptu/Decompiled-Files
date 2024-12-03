/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 */
package com.google.template.soy.base.internal;

import com.google.common.base.Charsets;
import com.google.template.soy.base.internal.AbstractSoyFileSupplier;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.internal.base.Pair;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class VolatileSoyFileSupplier
extends AbstractSoyFileSupplier {
    private final File file;

    public VolatileSoyFileSupplier(File file, SoyFileKind soyFileKind) {
        super(soyFileKind, file.getPath());
        this.file = file;
    }

    @Override
    public boolean hasChangedSince(SoyFileSupplier.Version version) {
        if (!(version instanceof VolatileFileVersion)) {
            return true;
        }
        return this.file.lastModified() != ((VolatileFileVersion)version).lastModified;
    }

    @Override
    public Pair<Reader, SoyFileSupplier.Version> open() throws IOException {
        long lastModified = this.file.lastModified();
        return Pair.of(new BufferedReader(new InputStreamReader((InputStream)new FileInputStream(this.file), Charsets.UTF_8)), new VolatileFileVersion(lastModified));
    }

    private static final class VolatileFileVersion
    implements SoyFileSupplier.Version {
        final long lastModified;

        VolatileFileVersion(long lastModified) {
            this.lastModified = lastModified;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof VolatileFileVersion && this.lastModified == ((VolatileFileVersion)other).lastModified;
        }

        public int hashCode() {
            return (int)this.lastModified;
        }

        public String toString() {
            return String.valueOf(this.lastModified);
        }
    }
}

