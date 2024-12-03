/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationType
 */
package com.atlassian.applinks.spi.manifest;

import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.spi.Manifest;
import com.atlassian.applinks.spi.manifest.ApplicationStatus;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import java.net.URI;

public interface ManifestRetriever {
    public Manifest getManifest(URI var1) throws ManifestNotFoundException;

    public Manifest getManifest(URI var1, ApplicationType var2) throws ManifestNotFoundException;

    public ApplicationStatus getApplicationStatus(URI var1, ApplicationType var2);
}

