/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api.binary;

import java.net.URI;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import org.apache.jackrabbit.api.binary.BinaryDownloadOptions;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface BinaryDownload
extends Binary {
    @Nullable
    public URI getURI(BinaryDownloadOptions var1) throws RepositoryException;
}

