/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.osgi.annotation.versioning.ProviderType
 */
package org.apache.jackrabbit.api;

import javax.jcr.AccessDeniedException;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import org.apache.jackrabbit.api.binary.BinaryUpload;
import org.apache.jackrabbit.api.binary.BinaryUploadOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.annotation.versioning.ProviderType;

@ProviderType
public interface JackrabbitValueFactory
extends ValueFactory {
    @Nullable
    public BinaryUpload initiateBinaryUpload(long var1, int var3) throws IllegalArgumentException, AccessDeniedException;

    @Nullable
    public BinaryUpload initiateBinaryUpload(long var1, int var3, BinaryUploadOptions var4) throws IllegalArgumentException, AccessDeniedException;

    @Nullable
    public Binary completeBinaryUpload(@NotNull String var1) throws IllegalArgumentException, RepositoryException;
}

