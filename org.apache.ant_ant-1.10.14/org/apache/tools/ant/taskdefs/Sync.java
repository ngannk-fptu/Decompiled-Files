/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.util.FileUtils;

public class Sync
extends Task {
    private MyCopy myCopy;
    private SyncTarget syncTarget;
    private Resources resources = null;

    @Override
    public void init() throws BuildException {
        this.myCopy = new MyCopy();
        this.configureTask(this.myCopy);
        this.myCopy.setFiltering(false);
        this.myCopy.setIncludeEmptyDirs(false);
        this.myCopy.setPreserveLastModified(true);
    }

    private void configureTask(Task helper) {
        helper.setProject(this.getProject());
        helper.setTaskName(this.getTaskName());
        helper.setOwningTarget(this.getOwningTarget());
        helper.init();
    }

    @Override
    public void execute() throws BuildException {
        File toDir = this.myCopy.getToDir();
        Set allFiles = this.myCopy.nonOrphans;
        String[] toDirChildren = toDir.list();
        boolean noRemovalNecessary = !toDir.exists() || toDirChildren == null || toDirChildren.length < 1;
        this.log("PASS#1: Copying files to " + toDir, 4);
        this.myCopy.execute();
        if (noRemovalNecessary) {
            this.log("NO removing necessary in " + toDir, 4);
            return;
        }
        LinkedHashSet<File> preservedDirectories = new LinkedHashSet<File>();
        this.log("PASS#2: Removing orphan files from " + toDir, 4);
        int[] removedFileCount = this.removeOrphanFiles(allFiles, toDir, preservedDirectories);
        this.logRemovedCount(removedFileCount[0], "dangling director", "y", "ies");
        this.logRemovedCount(removedFileCount[1], "dangling file", "", "s");
        if (!this.myCopy.getIncludeEmptyDirs() || this.getExplicitPreserveEmptyDirs() == Boolean.FALSE) {
            this.log("PASS#3: Removing empty directories from " + toDir, 4);
            int removedDirCount = 0;
            removedDirCount = !this.myCopy.getIncludeEmptyDirs() ? this.removeEmptyDirectories(toDir, false, preservedDirectories) : this.removeEmptyDirectories(preservedDirectories);
            this.logRemovedCount(removedDirCount, "empty director", "y", "ies");
        }
    }

    private void logRemovedCount(int count, String prefix, String singularSuffix, String pluralSuffix) {
        File toDir = this.myCopy.getToDir();
        String what = prefix == null ? "" : prefix;
        what = what + (count < 2 ? singularSuffix : pluralSuffix);
        if (count > 0) {
            this.log("Removed " + count + " " + what + " from " + toDir, 2);
        } else {
            this.log("NO " + what + " to remove from " + toDir, 3);
        }
    }

    private int[] removeOrphanFiles(Set<String> nonOrphans, File toDir, Set<File> preservedDirectories) {
        DirectoryScanner ds;
        int[] removedCount = new int[]{0, 0};
        String[] excls = nonOrphans.toArray(new String[nonOrphans.size() + 1]);
        excls[nonOrphans.size()] = "";
        if (this.syncTarget != null) {
            String[] fs = this.syncTarget.toFileSet(false);
            fs.setDir(toDir);
            PatternSet ps = this.syncTarget.mergePatterns(this.getProject());
            fs.appendExcludes(ps.getIncludePatterns(this.getProject()));
            fs.appendIncludes(ps.getExcludePatterns(this.getProject()));
            fs.setDefaultexcludes(!this.syncTarget.getDefaultexcludes());
            FileSelector[] s = this.syncTarget.getSelectors(this.getProject());
            if (s.length > 0) {
                NoneSelector ns = new NoneSelector();
                for (FileSelector element : s) {
                    ns.appendSelector(element);
                }
                fs.appendSelector(ns);
            }
            ds = fs.getDirectoryScanner(this.getProject());
        } else {
            ds = new DirectoryScanner();
            ds.setBasedir(toDir);
            FileUtils.isCaseSensitiveFileSystem(toDir.toPath()).ifPresent(ds::setCaseSensitive);
        }
        ds.addExcludes(excls);
        ds.scan();
        for (String file : ds.getIncludedFiles()) {
            File f = new File(toDir, file);
            this.log("Removing orphan file: " + f, 4);
            f.delete();
            removedCount[1] = removedCount[1] + 1;
        }
        String[] dirs = ds.getIncludedDirectories();
        for (int i = dirs.length - 1; i >= 0; --i) {
            File f = new File(toDir, dirs[i]);
            String[] children = f.list();
            if (children != null && children.length >= 1) continue;
            this.log("Removing orphan directory: " + f, 4);
            f.delete();
            removedCount[0] = removedCount[0] + 1;
        }
        Boolean ped = this.getExplicitPreserveEmptyDirs();
        if (ped != null && ped.booleanValue() != this.myCopy.getIncludeEmptyDirs()) {
            FileSet fs = this.syncTarget.toFileSet(true);
            fs.setDir(toDir);
            String[] preservedDirs = fs.getDirectoryScanner(this.getProject()).getIncludedDirectories();
            for (int i = preservedDirs.length - 1; i >= 0; --i) {
                preservedDirectories.add(new File(toDir, preservedDirs[i]));
            }
        }
        return removedCount;
    }

    private int removeEmptyDirectories(File dir, boolean removeIfEmpty, Set<File> preservedEmptyDirectories) {
        int removedCount = 0;
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File file : children) {
                    if (!file.isDirectory()) continue;
                    removedCount += this.removeEmptyDirectories(file, true, preservedEmptyDirectories);
                }
            }
            if (children == null || children.length > 0) {
                children = dir.listFiles();
            }
            if ((children == null || children.length < 1) && removeIfEmpty && !preservedEmptyDirectories.contains(dir)) {
                this.log("Removing empty directory: " + dir, 4);
                dir.delete();
                ++removedCount;
            }
        }
        return removedCount;
    }

    private int removeEmptyDirectories(Set<File> preservedEmptyDirectories) {
        int removedCount = 0;
        for (File f : preservedEmptyDirectories) {
            String[] s = f.list();
            if (s != null && s.length != 0) continue;
            this.log("Removing empty directory: " + f, 4);
            f.delete();
            ++removedCount;
        }
        return removedCount;
    }

    public void setTodir(File destDir) {
        this.myCopy.setTodir(destDir);
    }

    public void setVerbose(boolean verbose) {
        this.myCopy.setVerbose(verbose);
    }

    public void setOverwrite(boolean overwrite) {
        this.myCopy.setOverwrite(overwrite);
    }

    public void setIncludeEmptyDirs(boolean includeEmpty) {
        this.myCopy.setIncludeEmptyDirs(includeEmpty);
    }

    public void setFailOnError(boolean failonerror) {
        this.myCopy.setFailOnError(failonerror);
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void add(ResourceCollection rc) {
        if (rc instanceof FileSet && rc.isFilesystemOnly()) {
            this.myCopy.add(rc);
        } else {
            if (this.resources == null) {
                Restrict r = new Restrict();
                r.add(new Exists());
                this.resources = new Resources();
                r.add(this.resources);
                this.myCopy.add(r);
            }
            this.resources.add(rc);
        }
    }

    public void setGranularity(long granularity) {
        this.myCopy.setGranularity(granularity);
    }

    public void addPreserveInTarget(SyncTarget s) {
        if (this.syncTarget != null) {
            throw new BuildException("you must not specify multiple preserveintarget elements.");
        }
        this.syncTarget = s;
    }

    private Boolean getExplicitPreserveEmptyDirs() {
        return this.syncTarget == null ? null : this.syncTarget.getPreserveEmptyDirs();
    }

    private static void assertTrue(String message, boolean condition) {
        if (!condition) {
            throw new BuildException("Assertion Error: " + message);
        }
    }

    public static class MyCopy
    extends Copy {
        private Set<String> nonOrphans = new HashSet<String>();

        @Override
        protected void scan(File fromDir, File toDir, String[] files, String[] dirs) {
            Sync.assertTrue("No mapper", this.mapperElement == null);
            super.scan(fromDir, toDir, files, dirs);
            Collections.addAll(this.nonOrphans, files);
            Collections.addAll(this.nonOrphans, dirs);
        }

        @Override
        protected Map<Resource, String[]> scan(Resource[] resources, File toDir) {
            Sync.assertTrue("No mapper", this.mapperElement == null);
            Stream.of(resources).map(Resource::getName).forEach(this.nonOrphans::add);
            return super.scan(resources, toDir);
        }

        public File getToDir() {
            return this.destDir;
        }

        public boolean getIncludeEmptyDirs() {
            return this.includeEmpty;
        }

        @Override
        protected boolean supportsNonFileResources() {
            return true;
        }
    }

    public static class SyncTarget
    extends AbstractFileSet {
        private Boolean preserveEmptyDirs;

        @Override
        public void setDir(File dir) throws BuildException {
            throw new BuildException("preserveintarget doesn't support the dir attribute");
        }

        public void setPreserveEmptyDirs(boolean b) {
            this.preserveEmptyDirs = b;
        }

        public Boolean getPreserveEmptyDirs() {
            return this.preserveEmptyDirs;
        }

        private FileSet toFileSet(boolean withPatterns) {
            FileSet fs = new FileSet();
            fs.setCaseSensitive(this.isCaseSensitive());
            fs.setFollowSymlinks(this.isFollowSymlinks());
            fs.setMaxLevelsOfSymlinks(this.getMaxLevelsOfSymlinks());
            fs.setProject(this.getProject());
            if (withPatterns) {
                PatternSet ps = this.mergePatterns(this.getProject());
                fs.appendIncludes(ps.getIncludePatterns(this.getProject()));
                fs.appendExcludes(ps.getExcludePatterns(this.getProject()));
                for (FileSelector sel : this.getSelectors(this.getProject())) {
                    fs.appendSelector(sel);
                }
                fs.setDefaultexcludes(this.getDefaultexcludes());
            }
            return fs;
        }
    }
}

