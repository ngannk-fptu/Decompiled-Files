/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.Sort;
import org.apache.tools.ant.types.resources.comparators.FileSystem;
import org.apache.tools.ant.types.resources.comparators.ResourceComparator;
import org.apache.tools.ant.types.resources.comparators.Reverse;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;
import org.apache.tools.ant.util.FileUtils;

public class Delete
extends MatchingTask {
    private static final ResourceComparator REVERSE_FILESYSTEM = new Reverse(new FileSystem());
    private static final ResourceSelector EXISTS = new Exists();
    private static FileUtils FILE_UTILS = FileUtils.getFileUtils();
    protected File file = null;
    protected File dir = null;
    protected Vector<FileSet> filesets = new Vector();
    protected boolean usedMatchingTask = false;
    protected boolean includeEmpty = false;
    private int verbosity = 3;
    private boolean quiet = false;
    private boolean failonerror = true;
    private boolean deleteOnExit = false;
    private boolean removeNotFollowedSymlinks = false;
    private Resources rcs = null;
    private boolean performGc = Os.isFamily("windows");

    public void setFile(File file) {
        this.file = file;
    }

    public void setDir(File dir) {
        this.dir = dir;
        this.getImplicitFileSet().setDir(dir);
    }

    public void setVerbose(boolean verbose) {
        this.verbosity = verbose ? 2 : 3;
    }

    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
        if (quiet) {
            this.failonerror = false;
        }
    }

    public void setFailOnError(boolean failonerror) {
        this.failonerror = failonerror;
    }

    public void setDeleteOnExit(boolean deleteOnExit) {
        this.deleteOnExit = deleteOnExit;
    }

    public void setIncludeEmptyDirs(boolean includeEmpty) {
        this.includeEmpty = includeEmpty;
    }

    public void setPerformGcOnFailedDelete(boolean b) {
        this.performGc = b;
    }

    public void addFileset(FileSet set) {
        this.filesets.addElement(set);
    }

    public void add(ResourceCollection rc) {
        if (rc == null) {
            return;
        }
        if (this.rcs == null) {
            this.rcs = new Resources();
            this.rcs.setCache(true);
        }
        this.rcs.add(rc);
    }

    @Override
    public PatternSet.NameEntry createInclude() {
        this.usedMatchingTask = true;
        return super.createInclude();
    }

    @Override
    public PatternSet.NameEntry createIncludesFile() {
        this.usedMatchingTask = true;
        return super.createIncludesFile();
    }

    @Override
    public PatternSet.NameEntry createExclude() {
        this.usedMatchingTask = true;
        return super.createExclude();
    }

    @Override
    public PatternSet.NameEntry createExcludesFile() {
        this.usedMatchingTask = true;
        return super.createExcludesFile();
    }

    @Override
    public PatternSet createPatternSet() {
        this.usedMatchingTask = true;
        return super.createPatternSet();
    }

    @Override
    public void setIncludes(String includes) {
        this.usedMatchingTask = true;
        super.setIncludes(includes);
    }

    @Override
    public void setExcludes(String excludes) {
        this.usedMatchingTask = true;
        super.setExcludes(excludes);
    }

    @Override
    public void setDefaultexcludes(boolean useDefaultExcludes) {
        this.usedMatchingTask = true;
        super.setDefaultexcludes(useDefaultExcludes);
    }

    @Override
    public void setIncludesfile(File includesfile) {
        this.usedMatchingTask = true;
        super.setIncludesfile(includesfile);
    }

    @Override
    public void setExcludesfile(File excludesfile) {
        this.usedMatchingTask = true;
        super.setExcludesfile(excludesfile);
    }

    @Override
    public void setCaseSensitive(boolean isCaseSensitive) {
        this.usedMatchingTask = true;
        super.setCaseSensitive(isCaseSensitive);
    }

    @Override
    public void setFollowSymlinks(boolean followSymlinks) {
        this.usedMatchingTask = true;
        super.setFollowSymlinks(followSymlinks);
    }

    public void setRemoveNotFollowedSymlinks(boolean b) {
        this.removeNotFollowedSymlinks = b;
    }

    @Override
    public void addSelector(SelectSelector selector) {
        this.usedMatchingTask = true;
        super.addSelector(selector);
    }

    @Override
    public void addAnd(AndSelector selector) {
        this.usedMatchingTask = true;
        super.addAnd(selector);
    }

    @Override
    public void addOr(OrSelector selector) {
        this.usedMatchingTask = true;
        super.addOr(selector);
    }

    @Override
    public void addNot(NotSelector selector) {
        this.usedMatchingTask = true;
        super.addNot(selector);
    }

    @Override
    public void addNone(NoneSelector selector) {
        this.usedMatchingTask = true;
        super.addNone(selector);
    }

    @Override
    public void addMajority(MajoritySelector selector) {
        this.usedMatchingTask = true;
        super.addMajority(selector);
    }

    @Override
    public void addDate(DateSelector selector) {
        this.usedMatchingTask = true;
        super.addDate(selector);
    }

    @Override
    public void addSize(SizeSelector selector) {
        this.usedMatchingTask = true;
        super.addSize(selector);
    }

    @Override
    public void addFilename(FilenameSelector selector) {
        this.usedMatchingTask = true;
        super.addFilename(selector);
    }

    @Override
    public void addCustom(ExtendSelector selector) {
        this.usedMatchingTask = true;
        super.addCustom(selector);
    }

    @Override
    public void addContains(ContainsSelector selector) {
        this.usedMatchingTask = true;
        super.addContains(selector);
    }

    @Override
    public void addPresent(PresentSelector selector) {
        this.usedMatchingTask = true;
        super.addPresent(selector);
    }

    @Override
    public void addDepth(DepthSelector selector) {
        this.usedMatchingTask = true;
        super.addDepth(selector);
    }

    @Override
    public void addDepend(DependSelector selector) {
        this.usedMatchingTask = true;
        super.addDepend(selector);
    }

    @Override
    public void addContainsRegexp(ContainsRegexpSelector selector) {
        this.usedMatchingTask = true;
        super.addContainsRegexp(selector);
    }

    @Override
    public void addModified(ModifiedSelector selector) {
        this.usedMatchingTask = true;
        super.addModified(selector);
    }

    @Override
    public void add(FileSelector selector) {
        this.usedMatchingTask = true;
        super.add(selector);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        if (this.usedMatchingTask) {
            this.log("DEPRECATED - Use of the implicit FileSet is deprecated.  Use a nested fileset element instead.", this.quiet ? 3 : this.verbosity);
        }
        if (this.file == null && this.dir == null && this.filesets.isEmpty() && this.rcs == null) {
            throw new BuildException("At least one of the file or dir attributes, or a nested resource collection, must be set.");
        }
        if (this.quiet && this.failonerror) {
            throw new BuildException("quiet and failonerror cannot both be set to true", this.getLocation());
        }
        if (this.file != null) {
            if (this.file.exists()) {
                if (this.file.isDirectory()) {
                    this.log("Directory " + this.file.getAbsolutePath() + " cannot be removed using the file attribute.  Use dir instead.", this.quiet ? 3 : this.verbosity);
                } else {
                    this.log("Deleting: " + this.file.getAbsolutePath());
                    if (!this.delete(this.file)) {
                        this.handle("Unable to delete file " + this.file.getAbsolutePath());
                    }
                }
            } else if (this.isDanglingSymlink(this.file)) {
                this.log("Trying to delete file " + this.file.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!this.delete(this.file)) {
                    this.handle("Unable to delete file " + this.file.getAbsolutePath());
                }
            } else {
                this.log("Could not find file " + this.file.getAbsolutePath() + " to delete.", this.quiet ? 3 : this.verbosity);
            }
        }
        if (this.dir != null && !this.usedMatchingTask) {
            if (this.dir.exists() && this.dir.isDirectory()) {
                if (this.verbosity == 3) {
                    this.log("Deleting directory " + this.dir.getAbsolutePath());
                }
                this.removeDir(this.dir);
            } else if (this.isDanglingSymlink(this.dir)) {
                this.log("Trying to delete directory " + this.dir.getAbsolutePath() + " which looks like a broken symlink.", this.quiet ? 3 : this.verbosity);
                if (!this.delete(this.dir)) {
                    this.handle("Unable to delete directory " + this.dir.getAbsolutePath());
                }
            }
        }
        Resources resourcesToDelete = new Resources();
        resourcesToDelete.setProject(this.getProject());
        resourcesToDelete.setCache(true);
        Resources filesetDirs = new Resources();
        filesetDirs.setProject(this.getProject());
        filesetDirs.setCache(true);
        FileSet implicit = null;
        if (this.usedMatchingTask && this.dir != null && this.dir.isDirectory()) {
            implicit = this.getImplicitFileSet();
            implicit.setProject(this.getProject());
            this.filesets.add(implicit);
        }
        for (FileSet fs : this.filesets) {
            String[] n;
            if (fs.getProject() == null) {
                this.log("Deleting fileset with no project specified; assuming executing project", 3);
                fs = (FileSet)fs.clone();
                fs.setProject(this.getProject());
            }
            final File fsDir = fs.getDir();
            if (!fs.getErrorOnMissingDir() && (fsDir == null || !fsDir.exists())) continue;
            if (fsDir == null) {
                throw new BuildException("File or Resource without directory or file specified");
            }
            if (!fsDir.isDirectory()) {
                this.handle("Directory does not exist: " + fsDir);
                continue;
            }
            DirectoryScanner ds = fs.getDirectoryScanner();
            final String[] files = ds.getIncludedFiles();
            resourcesToDelete.add(new ResourceCollection(){

                @Override
                public boolean isFilesystemOnly() {
                    return true;
                }

                @Override
                public int size() {
                    return files.length;
                }

                @Override
                public Iterator<Resource> iterator() {
                    return new FileResourceIterator(Delete.this.getProject(), fsDir, files);
                }
            });
            if (this.includeEmpty) {
                filesetDirs.add(new ReverseDirs(this.getProject(), fsDir, ds.getIncludedDirectories()));
            }
            if (!this.removeNotFollowedSymlinks || (n = ds.getNotFollowedSymlinks()).length <= 0) continue;
            String[] links = new String[n.length];
            System.arraycopy(n, 0, links, 0, n.length);
            Arrays.sort(links, Comparator.reverseOrder());
            for (String link : links) {
                boolean deleted;
                Path filePath = Paths.get(link, new String[0]);
                if (!Files.isSymbolicLink(filePath) || (deleted = filePath.toFile().delete())) continue;
                this.handle("Could not delete symbolic link at " + filePath);
            }
        }
        resourcesToDelete.add(filesetDirs);
        if (this.rcs != null) {
            Restrict exists = new Restrict();
            exists.add(EXISTS);
            exists.add(this.rcs);
            Sort s = new Sort();
            s.add(REVERSE_FILESYSTEM);
            s.add(exists);
            resourcesToDelete.add(s);
        }
        try {
            if (resourcesToDelete.isFilesystemOnly()) {
                for (Resource r : resourcesToDelete) {
                    File f = r.as(FileProvider.class).getFile();
                    if (!f.exists() || f.isDirectory() && f.list() != null && f.list().length != 0) continue;
                    this.log("Deleting " + f, this.verbosity);
                    if (this.delete(f) || !this.failonerror) continue;
                    this.handle("Unable to delete " + (f.isDirectory() ? "directory " : "file ") + f);
                }
            } else {
                this.handle(this.getTaskName() + " handles only filesystem resources");
            }
        }
        catch (Exception e) {
            this.handle(e);
        }
        finally {
            if (implicit != null) {
                this.filesets.remove(implicit);
            }
        }
    }

    private void handle(String msg) {
        this.handle(new BuildException(msg));
    }

    private void handle(Exception e) {
        if (this.failonerror) {
            throw e instanceof BuildException ? (BuildException)e : new BuildException(e);
        }
        this.log(e, this.quiet ? 3 : this.verbosity);
    }

    private boolean delete(File f) {
        if (!FILE_UTILS.tryHardToDelete(f, this.performGc)) {
            if (this.deleteOnExit) {
                int level = this.quiet ? 3 : 2;
                this.log("Failed to delete " + f + ", calling deleteOnExit. This attempts to delete the file when the Ant jvm has exited and might not succeed.", level);
                f.deleteOnExit();
                return true;
            }
            return false;
        }
        return true;
    }

    protected void removeDir(File d) {
        String[] list = d.list();
        if (list == null) {
            list = new String[]{};
        }
        for (String s : list) {
            File f = new File(d, s);
            if (f.isDirectory()) {
                this.removeDir(f);
                continue;
            }
            this.log("Deleting " + f.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            if (this.delete(f)) continue;
            this.handle("Unable to delete file " + f.getAbsolutePath());
        }
        this.log("Deleting directory " + d.getAbsolutePath(), this.verbosity);
        if (!this.delete(d)) {
            this.handle("Unable to delete directory " + d.getAbsolutePath());
        }
    }

    protected void removeFiles(File d, String[] files, String[] dirs) {
        if (files.length > 0) {
            this.log("Deleting " + files.length + " files from " + d.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            for (String filename : files) {
                File f = new File(d, filename);
                this.log("Deleting " + f.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (this.delete(f)) continue;
                this.handle("Unable to delete file " + f.getAbsolutePath());
            }
        }
        if (dirs.length > 0 && this.includeEmpty) {
            int dirCount = 0;
            for (int j = dirs.length - 1; j >= 0; --j) {
                File currDir = new File(d, dirs[j]);
                String[] dirFiles = currDir.list();
                if (dirFiles != null && dirFiles.length != 0) continue;
                this.log("Deleting " + currDir.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
                if (!this.delete(currDir)) {
                    this.handle("Unable to delete directory " + currDir.getAbsolutePath());
                    continue;
                }
                ++dirCount;
            }
            if (dirCount > 0) {
                this.log("Deleted " + dirCount + " director" + (dirCount == 1 ? "y" : "ies") + " form " + d.getAbsolutePath(), this.quiet ? 3 : this.verbosity);
            }
        }
    }

    private boolean isDanglingSymlink(File f) {
        if (!Files.isSymbolicLink(f.toPath())) {
            return false;
        }
        boolean targetFileExists = Files.exists(f.toPath(), new LinkOption[0]);
        return !targetFileExists;
    }

    private static class ReverseDirs
    implements ResourceCollection {
        private Project project;
        private File basedir;
        private String[] dirs;

        ReverseDirs(Project project, File basedir, String[] dirs) {
            this.project = project;
            this.basedir = basedir;
            this.dirs = dirs;
            Arrays.sort(this.dirs, Comparator.reverseOrder());
        }

        @Override
        public Iterator<Resource> iterator() {
            return new FileResourceIterator(this.project, this.basedir, this.dirs);
        }

        @Override
        public boolean isFilesystemOnly() {
            return true;
        }

        @Override
        public int size() {
            return this.dirs.length;
        }
    }
}

