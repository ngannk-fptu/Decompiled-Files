/*
 * Decompiled with CFR 0.152.
 */
package org.twdata.pkgscanner;

import java.io.File;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExportPackage
implements Comparable<ExportPackage> {
    private final String packageName;
    private final String version;
    private final File location;

    public ExportPackage(String packageName, String version, File location) {
        if (packageName == null) {
            throw new IllegalArgumentException("packageName must not be null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location must not be null");
        }
        this.version = version;
        this.location = location;
        if (packageName.startsWith(".")) {
            packageName = packageName.substring(1);
        }
        this.packageName = packageName;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getVersion() {
        return this.version;
    }

    public File getLocation() {
        return this.location;
    }

    @Override
    public int compareTo(ExportPackage exportPackage) {
        return this.packageName.compareTo(exportPackage.getPackageName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ExportPackage that = (ExportPackage)o;
        if (!this.packageName.equals(that.packageName)) {
            return false;
        }
        return !(this.version != null ? !this.version.equals(that.version) : that.version != null);
    }

    public int hashCode() {
        int result = this.packageName.hashCode();
        result = 31 * result + (this.version != null ? this.version.hashCode() : 0);
        return result;
    }
}

