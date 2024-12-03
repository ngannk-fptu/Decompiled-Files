/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection;

import org.hibernate.annotations.common.reflection.MetadataProvider;

public interface MetadataProviderInjector {
    public MetadataProvider getMetadataProvider();

    public void setMetadataProvider(MetadataProvider var1);
}

