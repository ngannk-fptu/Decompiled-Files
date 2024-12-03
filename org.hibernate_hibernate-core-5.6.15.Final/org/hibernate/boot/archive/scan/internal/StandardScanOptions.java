/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.internal;

import org.hibernate.boot.archive.scan.spi.ScanOptions;

public class StandardScanOptions
implements ScanOptions {
    private final boolean detectClassesInRoot;
    private final boolean detectClassesInNonRoot;
    private final boolean detectHibernateMappingFiles;

    public StandardScanOptions() {
        this("hbm,class", false);
    }

    public StandardScanOptions(String explicitDetectionSetting, boolean persistenceUnitExcludeUnlistedClassesValue) {
        if (explicitDetectionSetting == null) {
            this.detectHibernateMappingFiles = true;
            this.detectClassesInRoot = !persistenceUnitExcludeUnlistedClassesValue;
            this.detectClassesInNonRoot = true;
        } else {
            this.detectHibernateMappingFiles = explicitDetectionSetting.contains("hbm");
            this.detectClassesInNonRoot = this.detectClassesInRoot = explicitDetectionSetting.contains("class");
        }
    }

    @Override
    public boolean canDetectUnlistedClassesInRoot() {
        return this.detectClassesInRoot;
    }

    @Override
    public boolean canDetectUnlistedClassesInNonRoot() {
        return this.detectClassesInNonRoot;
    }

    @Override
    public boolean canDetectHibernateMappingFiles() {
        return this.detectHibernateMappingFiles;
    }
}

