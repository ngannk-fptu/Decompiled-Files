/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginException
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.PluginException;
import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectoryScanner
implements Scanner {
    private static Logger log = LoggerFactory.getLogger(DirectoryScanner.class);
    private final File pluginsDirectory;
    private final Map<String, DeploymentUnit> scannedDeploymentUnits = new TreeMap<String, DeploymentUnit>();

    public DirectoryScanner(File pluginsDirectory) {
        this.pluginsDirectory = (File)Preconditions.checkNotNull((Object)pluginsDirectory);
    }

    private DeploymentUnit createAndStoreDeploymentUnit(File file) {
        if (this.isScanned(file)) {
            return null;
        }
        DeploymentUnit unit = new DeploymentUnit(file);
        this.scannedDeploymentUnits.put(file.getAbsolutePath(), unit);
        return unit;
    }

    public DeploymentUnit locateDeploymentUnit(File file) {
        return this.scannedDeploymentUnits.get(file.getAbsolutePath());
    }

    private boolean isScanned(File file) {
        return this.locateDeploymentUnit(file) != null;
    }

    public void clear(File file) {
        this.scannedDeploymentUnits.remove(file.getAbsolutePath());
    }

    @Override
    public Collection<DeploymentUnit> scan() {
        ArrayList<File> removedFiles = new ArrayList<File>();
        for (DeploymentUnit unit : this.scannedDeploymentUnits.values()) {
            if (unit.getPath().exists() && unit.getPath().canRead()) continue;
            removedFiles.add(unit.getPath());
        }
        this.clear(removedFiles);
        ArrayList<DeploymentUnit> result = new ArrayList<DeploymentUnit>();
        Object[] files = this.pluginsDirectory.listFiles((dir, name) -> !name.startsWith("."));
        if (files == null) {
            log.error("listFiles returned null for directory {}", (Object)this.pluginsDirectory.getAbsolutePath());
            return result;
        }
        Arrays.sort(files);
        for (Object file : files) {
            DeploymentUnit unit;
            if (this.isScanned((File)file) && this.isModified((File)file)) {
                this.clear((File)file);
                unit = this.createAndStoreDeploymentUnit((File)file);
                if (unit == null) continue;
                result.add(unit);
                continue;
            }
            if (this.isScanned((File)file) || (unit = this.createAndStoreDeploymentUnit((File)file)) == null) continue;
            result.add(unit);
        }
        return result;
    }

    private boolean isModified(File file) {
        DeploymentUnit unit = this.locateDeploymentUnit(file);
        return file.lastModified() > unit.lastModified();
    }

    private void clear(List<File> toUndeploy) {
        for (File aToUndeploy : toUndeploy) {
            this.clear(aToUndeploy);
        }
    }

    @Override
    public Collection<DeploymentUnit> getDeploymentUnits() {
        return Collections.unmodifiableCollection(this.scannedDeploymentUnits.values());
    }

    @Override
    public void reset() {
        this.scannedDeploymentUnits.clear();
    }

    @Override
    public void remove(DeploymentUnit unit) {
        File file = unit.getPath();
        try {
            Files.delete(file.toPath());
        }
        catch (AccessDeniedException e) {
            log.info("Plugin file <{}> exists but we do not have permission to remove it. Ignoring.", (Object)file.getAbsolutePath());
        }
        catch (FileNotFoundException | NoSuchFileException e) {
            log.debug("Plugin file <{}> doesn't exist to delete. Ignoring.", (Object)file.getAbsolutePath());
        }
        catch (IOException e) {
            throw new PluginException("Unable to delete plugin file: " + file.getAbsolutePath());
        }
        this.clear(file);
    }
}

