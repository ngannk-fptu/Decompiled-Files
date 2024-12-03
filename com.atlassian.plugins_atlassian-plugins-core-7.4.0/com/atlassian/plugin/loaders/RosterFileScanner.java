/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.loaders.classloading.DeploymentUnit
 *  com.google.common.base.Preconditions
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.loaders;

import com.atlassian.plugin.loaders.classloading.DeploymentUnit;
import com.atlassian.plugin.loaders.classloading.Scanner;
import com.google.common.base.Preconditions;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RosterFileScanner
implements Scanner {
    private static final Logger log = LoggerFactory.getLogger(RosterFileScanner.class);
    private final File rosterFile;
    private Map<String, DeploymentUnit> deploymentUnits;
    private long lastModified;

    public RosterFileScanner(File rosterFile) {
        Preconditions.checkNotNull((Object)rosterFile);
        Preconditions.checkArgument((boolean)RosterFileScanner.isKnownRosterFileFormat(rosterFile), (String)"Roster file '%s' does not end with '%s'", (Object)rosterFile, (Object)RosterFileScanner.getListSuffix());
        this.rosterFile = rosterFile;
        this.deploymentUnits = Collections.emptyMap();
    }

    @Override
    public Collection<DeploymentUnit> scan() {
        try {
            ArrayList<DeploymentUnit> scanned = new ArrayList<DeploymentUnit>();
            long updatedLastModified = this.rosterFile.lastModified();
            if (updatedLastModified != 0L && updatedLastModified != this.lastModified) {
                List filePaths = FileUtils.readLines((File)this.rosterFile);
                HashMap<String, DeploymentUnit> updatedDeploymentUnits = new HashMap<String, DeploymentUnit>(filePaths.size());
                for (String filePath : filePaths) {
                    DeploymentUnit priorUnit = this.deploymentUnits.get(filePath);
                    if (null == priorUnit) {
                        File file = new File(filePath);
                        File absoluteFile = file.isAbsolute() ? file : new File(this.rosterFile.getParentFile(), filePath);
                        DeploymentUnit deploymentUnit = new DeploymentUnit(absoluteFile);
                        updatedDeploymentUnits.put(filePath, deploymentUnit);
                        scanned.add(deploymentUnit);
                        continue;
                    }
                    updatedDeploymentUnits.put(filePath, priorUnit);
                }
                this.deploymentUnits = updatedDeploymentUnits;
                this.lastModified = updatedLastModified;
                return scanned;
            }
        }
        catch (IOException eio) {
            log.warn("Cannot read roster file '{}': {}", (Object)this.rosterFile.getAbsolutePath(), (Object)eio.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<DeploymentUnit> getDeploymentUnits() {
        return Collections.unmodifiableCollection(this.deploymentUnits.values());
    }

    @Override
    public void reset() {
        this.deploymentUnits = Collections.emptyMap();
        this.lastModified = 0L;
    }

    @Override
    public void remove(DeploymentUnit deploymentUnit) {
    }

    public static String getListSuffix() {
        return ".list";
    }

    public static boolean isKnownRosterFileFormat(File rosterFile) {
        return rosterFile.getName().endsWith(RosterFileScanner.getListSuffix());
    }
}

