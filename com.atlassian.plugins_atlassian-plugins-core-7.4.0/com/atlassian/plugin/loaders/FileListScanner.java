/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class FileListScanner
implements Scanner {
    private final Collection<File> files;
    private transient Collection<DeploymentUnit> units;

    public FileListScanner(Collection<File> files) {
        this.files = new ArrayList<File>(files);
    }

    @Override
    public Collection<DeploymentUnit> scan() {
        if (this.units != null) {
            return Collections.emptyList();
        }
        this.units = new ArrayList<DeploymentUnit>();
        for (File file : this.files) {
            this.units.add(new DeploymentUnit(file));
        }
        return this.units;
    }

    @Override
    public Collection<DeploymentUnit> getDeploymentUnits() {
        return Collections.unmodifiableCollection(this.units);
    }

    @Override
    public void reset() {
        this.units = null;
    }

    @Override
    public void remove(DeploymentUnit unit) {
        throw new PluginException("Cannot remove files in a file-list scanner: " + unit.getPath());
    }
}

