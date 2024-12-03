/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.loaders.classloading;

import java.io.File;

public final class DeploymentUnit
implements Comparable<DeploymentUnit> {
    private final File path;
    private final long lastModifiedAtTimeOfDeployment;

    public DeploymentUnit(File path) {
        if (path == null) {
            throw new IllegalArgumentException("File should not be null!");
        }
        this.path = path;
        this.lastModifiedAtTimeOfDeployment = path.lastModified();
    }

    public long lastModified() {
        return this.lastModifiedAtTimeOfDeployment;
    }

    public File getPath() {
        return this.path;
    }

    @Override
    public int compareTo(DeploymentUnit target) {
        int result = this.path.compareTo(target.getPath());
        if (result == 0) {
            result = Long.compare(this.lastModifiedAtTimeOfDeployment, target.lastModified());
        }
        return result;
    }

    public boolean equals(Object deploymentUnit) {
        if (deploymentUnit instanceof DeploymentUnit) {
            return this.equals((DeploymentUnit)deploymentUnit);
        }
        return false;
    }

    public boolean equals(DeploymentUnit deploymentUnit) {
        if (!this.path.equals(deploymentUnit.path)) {
            return false;
        }
        return this.lastModifiedAtTimeOfDeployment == deploymentUnit.lastModifiedAtTimeOfDeployment;
    }

    public int hashCode() {
        int result = this.path.hashCode();
        result = 31 * result + (int)(this.lastModifiedAtTimeOfDeployment ^ this.lastModifiedAtTimeOfDeployment >>> 32);
        return result;
    }

    public String toString() {
        return "Unit: " + this.path.toString() + " (" + this.lastModifiedAtTimeOfDeployment + ")";
    }
}

