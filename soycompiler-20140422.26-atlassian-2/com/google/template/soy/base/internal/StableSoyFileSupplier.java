/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.CharSource
 */
package com.google.template.soy.base.internal;

import com.google.common.io.CharSource;
import com.google.template.soy.base.internal.AbstractSoyFileSupplier;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.internal.base.Pair;
import java.io.IOException;
import java.io.Reader;

public class StableSoyFileSupplier
extends AbstractSoyFileSupplier {
    private final CharSource contentSource;

    public StableSoyFileSupplier(CharSource contentSource, SoyFileKind soyFileKind, String filePath) {
        super(soyFileKind, filePath);
        this.contentSource = contentSource;
    }

    @Override
    public boolean hasChangedSince(SoyFileSupplier.Version version) {
        return !SoyFileSupplier.Version.STABLE_VERSION.equals(version);
    }

    @Override
    public Pair<Reader, SoyFileSupplier.Version> open() throws IOException {
        return Pair.of(this.contentSource.openStream(), SoyFileSupplier.Version.STABLE_VERSION);
    }
}

