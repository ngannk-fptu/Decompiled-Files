/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.base.Preconditions
 */
package com.google.template.soy.base.internal;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.template.soy.base.internal.SoyFileKind;
import com.google.template.soy.base.internal.SoyFileSupplier;

abstract class AbstractSoyFileSupplier
implements SoyFileSupplier {
    protected final SoyFileKind soyFileKind;
    protected final String filePath;

    public AbstractSoyFileSupplier(SoyFileKind soyFileKind, String filePath) {
        this.soyFileKind = soyFileKind;
        Preconditions.checkState((filePath != null && !filePath.equals("") ? 1 : 0) != 0, (Object)"Soy file path must be non-null and non-empty.");
        this.filePath = filePath;
    }

    @Override
    public SoyFileKind getSoyFileKind() {
        return this.soyFileKind;
    }

    @Override
    public String getFilePath() {
        return this.filePath;
    }

    public boolean equals(Object other) {
        if (other instanceof AbstractSoyFileSupplier && other.getClass() == this.getClass()) {
            AbstractSoyFileSupplier otherSupplier = (AbstractSoyFileSupplier)other;
            return this.filePath.equals(otherSupplier.filePath) && this.soyFileKind == otherSupplier.soyFileKind;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.filePath, this.soyFileKind, this.getClass()});
    }
}

