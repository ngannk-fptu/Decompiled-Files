/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterSetCollection;

public class Move
extends Copy {
    private boolean performGc = Os.isFamily("windows");

    public Move() {
        this.setOverwrite(true);
    }

    public void setPerformGcOnFailedDelete(boolean b) {
        this.performGc = b;
    }

    @Override
    protected void validateAttributes() throws BuildException {
        if (this.file != null && this.file.isDirectory()) {
            if (this.destFile != null && this.destDir != null || this.destFile == null && this.destDir == null) {
                throw new BuildException("One and only one of tofile and todir must be set.");
            }
            this.destFile = this.destFile == null ? new File(this.destDir, this.file.getName()) : this.destFile;
            this.destDir = this.destDir == null ? this.destFile.getParentFile() : this.destDir;
            this.completeDirMap.put(this.file, this.destFile);
            this.file = null;
        } else {
            super.validateAttributes();
        }
    }

    /*
     * WARNING - void declaration
     */
    @Override
    protected void doFileOperations() {
        int moveCount;
        if (this.completeDirMap.size() > 0) {
            for (Map.Entry entry : this.completeDirMap.entrySet()) {
                File file = (File)entry.getKey();
                File toDir = (File)entry.getValue();
                boolean renamed = false;
                try {
                    this.log("Attempting to rename dir: " + file + " to " + toDir, this.verbosity);
                    renamed = this.renameFile(file, toDir, this.filtering, this.forceOverwrite);
                }
                catch (IOException ioe) {
                    String msg = "Failed to rename dir " + file + " to " + toDir + " due to " + ioe.getMessage();
                    throw new BuildException(msg, ioe, this.getLocation());
                }
                if (renamed) continue;
                FileSet fs = new FileSet();
                fs.setProject(this.getProject());
                fs.setDir(file);
                this.addFileset(fs);
                DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
                this.scan(file, toDir, ds.getIncludedFiles(), ds.getIncludedDirectories());
            }
        }
        if ((moveCount = this.fileCopyMap.size()) > 0) {
            this.log("Moving " + moveCount + " file" + (moveCount == 1 ? "" : "s") + " to " + this.destDir.getAbsolutePath());
            for (Map.Entry entry : this.fileCopyMap.entrySet()) {
                String fromFile = (String)entry.getKey();
                File f = new File(fromFile);
                boolean selfMove = false;
                if (!f.exists()) continue;
                String[] toFiles = (String[])entry.getValue();
                for (int i = 0; i < toFiles.length; ++i) {
                    String toFile = toFiles[i];
                    if (fromFile.equals(toFile)) {
                        this.log("Skipping self-move of " + fromFile, this.verbosity);
                        selfMove = true;
                        continue;
                    }
                    File d = new File(toFile);
                    if (i + 1 == toFiles.length && !selfMove) {
                        this.moveFile(f, d, this.filtering, this.forceOverwrite);
                        continue;
                    }
                    this.copyFile(f, d, this.filtering, this.forceOverwrite);
                }
            }
        }
        if (this.includeEmpty) {
            void var2_7;
            boolean bl = false;
            for (Map.Entry entry : this.dirCopyMap.entrySet()) {
                String fromDirName = (String)entry.getKey();
                boolean selfMove = false;
                for (String toDirName : (String[])entry.getValue()) {
                    if (fromDirName.equals(toDirName)) {
                        this.log("Skipping self-move of " + fromDirName, this.verbosity);
                        selfMove = true;
                        continue;
                    }
                    File d = new File(toDirName);
                    if (d.exists()) continue;
                    if (!d.mkdirs() && !d.exists()) {
                        this.log("Unable to create directory " + d.getAbsolutePath(), 0);
                        continue;
                    }
                    ++var2_7;
                }
                File fromDir = new File(fromDirName);
                if (selfMove || !this.okToDelete(fromDir)) continue;
                this.deleteDir(fromDir);
            }
            if (var2_7 > 0) {
                this.log("Moved " + this.dirCopyMap.size() + " empty director" + (this.dirCopyMap.size() == 1 ? "y" : "ies") + " to " + (int)var2_7 + " empty director" + (var2_7 == true ? "y" : "ies") + " under " + this.destDir.getAbsolutePath());
            }
        }
    }

    private void moveFile(File fromFile, File toFile, boolean filtering, boolean overwrite) {
        boolean moved = false;
        try {
            this.log("Attempting to rename: " + fromFile + " to " + toFile, this.verbosity);
            moved = this.renameFile(fromFile, toFile, filtering, this.forceOverwrite);
        }
        catch (IOException ioe) {
            throw new BuildException("Failed to rename " + fromFile + " to " + toFile + " due to " + ioe.getMessage(), ioe, this.getLocation());
        }
        if (!moved) {
            this.copyFile(fromFile, toFile, filtering, overwrite);
            if (!this.getFileUtils().tryHardToDelete(fromFile, this.performGc)) {
                throw new BuildException("Unable to delete file %s", fromFile.getAbsolutePath());
            }
        }
    }

    private void copyFile(File fromFile, File toFile, boolean filtering, boolean overwrite) {
        try {
            this.log("Copying " + fromFile + " to " + toFile, this.verbosity);
            FilterSetCollection executionFilters = new FilterSetCollection();
            if (filtering) {
                executionFilters.addFilterSet(this.getProject().getGlobalFilterSet());
            }
            this.getFilterSets().forEach(executionFilters::addFilterSet);
            this.getFileUtils().copyFile(fromFile, toFile, executionFilters, this.getFilterChains(), this.forceOverwrite, this.getPreserveLastModified(), false, this.getEncoding(), this.getOutputEncoding(), this.getProject(), this.getForce());
        }
        catch (IOException ioe) {
            throw new BuildException("Failed to copy " + fromFile + " to " + toFile + " due to " + ioe.getMessage(), ioe, this.getLocation());
        }
    }

    protected boolean okToDelete(File d) {
        String[] list = d.list();
        if (list == null) {
            return false;
        }
        for (String s : list) {
            File f = new File(d, s);
            if (f.isDirectory()) {
                if (this.okToDelete(f)) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    protected void deleteDir(File d) {
        this.deleteDir(d, false);
    }

    protected void deleteDir(File d, boolean deleteFiles) {
        String[] list = d.list();
        if (list == null) {
            return;
        }
        for (String s : list) {
            File f = new File(d, s);
            if (!f.isDirectory()) {
                if (deleteFiles && !this.getFileUtils().tryHardToDelete(f, this.performGc)) {
                    throw new BuildException("Unable to delete file %s", f.getAbsolutePath());
                }
                throw new BuildException("UNEXPECTED ERROR - The file %s should not exist!", f.getAbsolutePath());
            }
            this.deleteDir(f);
        }
        this.log("Deleting directory " + d.getAbsolutePath(), this.verbosity);
        if (!this.getFileUtils().tryHardToDelete(d, this.performGc)) {
            throw new BuildException("Unable to delete directory %s", d.getAbsolutePath());
        }
    }

    protected boolean renameFile(File sourceFile, File destFile, boolean filtering, boolean overwrite) throws IOException, BuildException {
        File parent;
        if (destFile.isDirectory() || filtering || !this.getFilterSets().isEmpty() || !this.getFilterChains().isEmpty()) {
            return false;
        }
        if (destFile.isFile() && !destFile.canWrite()) {
            if (!this.getForce()) {
                throw new IOException(String.format("can't replace read-only destination file %s", destFile));
            }
            if (!this.getFileUtils().tryHardToDelete(destFile)) {
                throw new IOException(String.format("failed to delete read-only destination file %s", destFile));
            }
        }
        if ((parent = destFile.getParentFile()) != null && !parent.exists()) {
            parent.mkdirs();
        } else if (destFile.isFile()) {
            sourceFile = this.getFileUtils().normalize(sourceFile.getAbsolutePath()).getCanonicalFile();
            destFile = this.getFileUtils().normalize(destFile.getAbsolutePath());
            if (destFile.getAbsolutePath().equals(sourceFile.getAbsolutePath())) {
                this.log("Rename of " + sourceFile + " to " + destFile + " is a no-op.", 3);
                return true;
            }
            if (!this.getFileUtils().areSame(sourceFile, destFile) && !this.getFileUtils().tryHardToDelete(destFile, this.performGc)) {
                throw new BuildException("Unable to remove existing file %s", destFile);
            }
        }
        return sourceFile.renameTo(destFile);
    }
}

