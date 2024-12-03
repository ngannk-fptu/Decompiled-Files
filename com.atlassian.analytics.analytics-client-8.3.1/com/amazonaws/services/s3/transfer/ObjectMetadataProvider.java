/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.File;

public interface ObjectMetadataProvider {
    public void provideObjectMetadata(File var1, ObjectMetadata var2);
}

