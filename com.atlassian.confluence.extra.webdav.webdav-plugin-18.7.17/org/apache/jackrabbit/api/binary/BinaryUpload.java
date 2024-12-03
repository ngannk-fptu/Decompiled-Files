/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.binary;

import java.net.URI;
import org.jetbrains.annotations.NotNull;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface BinaryUpload {
    @NotNull
    public Iterable<URI> getUploadURIs();

    public long getMinPartSize();

    public long getMaxPartSize();

    @NotNull
    public String getUploadToken();
}

