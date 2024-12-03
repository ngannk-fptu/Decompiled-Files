/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.marketplace.client.api;

import com.atlassian.marketplace.client.MpacException;
import com.atlassian.marketplace.client.api.ArtifactId;
import com.atlassian.marketplace.client.api.ImageId;
import com.atlassian.marketplace.client.api.ImagePurpose;
import java.io.File;

public interface Assets {
    public ArtifactId uploadAddonArtifact(File var1) throws MpacException;

    public ArtifactId uploadAddonArtifact(File var1, String var2) throws MpacException;

    public ImageId uploadImage(File var1, ImagePurpose var2) throws MpacException;
}

