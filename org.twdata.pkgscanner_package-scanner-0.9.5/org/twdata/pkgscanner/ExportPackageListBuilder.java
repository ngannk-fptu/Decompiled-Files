/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.twdata.pkgscanner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.twdata.pkgscanner.ExportPackage;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExportPackageListBuilder {
    private static final Logger log = LoggerFactory.getLogger(ExportPackageListBuilder.class);
    private Map<String, ExportPackage> packageMap = new HashMap<String, ExportPackage>();

    public void add(ExportPackage exportPackage) {
        String packageName = exportPackage.getPackageName();
        ExportPackage currentExportPackage = this.packageMap.get(packageName);
        if (currentExportPackage == null) {
            this.packageMap.put(packageName, exportPackage);
        } else if (exportPackage.getVersion() == null) {
            if (currentExportPackage.getVersion() != null) {
                this.logDuplicateOneVersion(exportPackage, currentExportPackage, currentExportPackage.getVersion());
            }
        } else if (currentExportPackage.getVersion() == null) {
            this.packageMap.put(packageName, exportPackage);
            this.logDuplicateOneVersion(exportPackage, currentExportPackage, exportPackage.getVersion());
        } else {
            if (!currentExportPackage.getVersion().equals(exportPackage.getVersion())) {
                this.logDuplicateWarning(exportPackage, currentExportPackage);
            }
            this.packageMap.put(packageName, exportPackage);
        }
    }

    private void logDuplicateOneVersion(ExportPackage exportPackage1, ExportPackage exportPackage2, String acceptedVersion) {
        log.info("Package Scanner found duplicates for package '" + exportPackage1.getPackageName() + "' - accepting version '" + acceptedVersion + "'. Files: " + exportPackage1.getLocation().getName() + " and " + exportPackage2.getLocation().getName() + "\n  '" + exportPackage1.getLocation().getAbsolutePath() + "'" + "\n  '" + exportPackage2.getLocation().getAbsolutePath() + "'");
    }

    private void logDuplicateWarning(ExportPackage exportPackage1, ExportPackage exportPackage2) {
        log.warn("Package Scanner found duplicates for package '" + exportPackage1.getPackageName() + "' with different versions. Files: " + exportPackage1.getLocation().getName() + " and " + exportPackage2.getLocation().getName() + "\n  '" + exportPackage1.getLocation().getAbsolutePath() + "'" + "\n  '" + exportPackage2.getLocation().getAbsolutePath() + "'");
    }

    public List<ExportPackage> getPackageList() {
        ArrayList<ExportPackage> packageList = new ArrayList<ExportPackage>(this.packageMap.values());
        Collections.sort(packageList);
        return packageList;
    }
}

