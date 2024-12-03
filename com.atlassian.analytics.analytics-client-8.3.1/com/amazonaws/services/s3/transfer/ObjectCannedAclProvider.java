/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import java.io.File;

public interface ObjectCannedAclProvider {
    public CannedAccessControlList provideObjectCannedAcl(File var1);
}

