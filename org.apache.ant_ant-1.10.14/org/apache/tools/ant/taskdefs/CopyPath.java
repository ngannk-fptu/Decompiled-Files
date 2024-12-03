/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.FileUtils;

@Deprecated
public class CopyPath
extends Task {
    public static final String ERROR_NO_DESTDIR = "No destDir specified";
    public static final String ERROR_NO_PATH = "No path specified";
    public static final String ERROR_NO_MAPPER = "No mapper specified";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private FileNameMapper mapper;
    private Path path;
    private File destDir;
    private long granularity = FILE_UTILS.getFileTimestampGranularity();
    private boolean preserveLastModified = false;

    public void setDestDir(File destDir) {
        this.destDir = destDir;
    }

    public void add(FileNameMapper newmapper) {
        if (this.mapper != null) {
            throw new BuildException("Only one mapper allowed");
        }
        this.mapper = newmapper;
    }

    public void setPath(Path s) {
        this.createPath().append(s);
    }

    public void setPathRef(Reference r) {
        this.createPath().setRefid(r);
    }

    public Path createPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path;
    }

    public void setGranularity(long granularity) {
        this.granularity = granularity;
    }

    public void setPreserveLastModified(boolean preserveLastModified) {
        this.preserveLastModified = preserveLastModified;
    }

    protected void validateAttributes() throws BuildException {
        if (this.destDir == null) {
            throw new BuildException(ERROR_NO_DESTDIR);
        }
        if (this.mapper == null) {
            throw new BuildException(ERROR_NO_MAPPER);
        }
        if (this.path == null) {
            throw new BuildException(ERROR_NO_PATH);
        }
    }

    @Override
    public void execute() throws BuildException {
        this.log("This task should have never been released and was obsoleted by ResourceCollection support in <copy> available since Ant 1.7.0.  Don't use it.", 0);
        this.validateAttributes();
        String[] sourceFiles = this.path.list();
        if (sourceFiles.length == 0) {
            this.log("Path is empty", 3);
            return;
        }
        for (String sourceFileName : sourceFiles) {
            File sourceFile = new File(sourceFileName);
            String[] toFiles = this.mapper.mapFileName(sourceFileName);
            if (toFiles == null) continue;
            for (String destFileName : toFiles) {
                File destFile = new File(this.destDir, destFileName);
                if (sourceFile.equals(destFile)) {
                    this.log("Skipping self-copy of " + sourceFileName, 3);
                    continue;
                }
                if (sourceFile.isDirectory()) {
                    this.log("Skipping directory " + sourceFileName);
                    continue;
                }
                try {
                    this.log("Copying " + sourceFile + " to " + destFile, 3);
                    FILE_UTILS.copyFile(sourceFile, destFile, null, null, false, this.preserveLastModified, null, null, this.getProject());
                }
                catch (IOException ioe) {
                    String msg = "Failed to copy " + sourceFile + " to " + destFile + " due to " + ioe.getMessage();
                    if (destFile.exists() && !destFile.delete()) {
                        msg = msg + " and I couldn't delete the corrupt " + destFile;
                    }
                    throw new BuildException(msg, ioe, this.getLocation());
                }
            }
        }
    }
}

