/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.spi.manifest;

import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import java.net.URI;

public interface ManifestProducer {
    public Manifest getManifest(URI var1) throws ManifestNotFoundException;

    public ApplicationStatus getStatus(URI var1);
}

