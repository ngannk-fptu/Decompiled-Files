/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.troubleshooting.api.supportzip;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.troubleshooting.api.supportzip.BundleCategory;
import java.io.Closeable;
import java.io.File;
import java.util.Collection;

@PublicSpi
public interface SupportZipBundle {
    public String getTitle();

    default public BundleCategory getCategory() {
        return BundleCategory.OTHER;
    }

    public String getDescription();

    public Collection<Artifact> getArtifacts();

    public String getKey();

    public boolean isSelected();

    public boolean isRequired();

    default public boolean isApplicable() {
        return true;
    }

    default public String getApplicabilityReason() {
        return "";
    }

    public static interface Artifact
    extends Closeable {
        public File getFile();

        public String getTargetPath();

        @Override
        default public void close() {
        }
    }
}

