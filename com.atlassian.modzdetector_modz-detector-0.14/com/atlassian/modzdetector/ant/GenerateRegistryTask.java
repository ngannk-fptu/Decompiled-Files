/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.FileSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.modzdetector.ant;

import com.atlassian.modzdetector.HashRegistry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GenerateRegistryTask
extends Task {
    private static final Logger log = LoggerFactory.getLogger(GenerateRegistryTask.class);
    private List files = new ArrayList();
    private List classes = new ArrayList();
    private String name;

    public void addFilesystem(FileSet f) {
        if (f != null) {
            this.files.add(f);
        }
    }

    public void addClasspath(FileSet f) {
        if (f != null) {
            this.classes.add(f);
        }
    }

    public void setName(String fileName) {
        this.name = fileName;
    }

    public void execute() throws BuildException {
        try {
            HashRegistry hr = this.name != null && this.name.length() > 0 ? new HashRegistry(this.name) : new HashRegistry();
            hr.setClasspathMode();
            this.register(hr, this.classes);
            hr.setFilesystemMode();
            this.register(hr, this.files);
            hr.store();
        }
        catch (IOException e) {
            throw new BuildException((Throwable)e);
        }
    }

    private void register(HashRegistry hr, List<FileSet> filesets) {
        for (FileSet set : filesets) {
            String[] fileNames;
            DirectoryScanner ds = set.getDirectoryScanner(this.getProject());
            for (String filename : fileNames = ds.getIncludedFiles()) {
                log.debug("registering " + filename);
                try {
                    String filenameFixed = filename.replace('\\', '/');
                    hr.register(filenameFixed, new FileInputStream(new File(set.getDir(this.getProject()), filename)));
                }
                catch (FileNotFoundException e) {
                    log.error("cannot find file " + filename);
                }
            }
        }
    }
}

