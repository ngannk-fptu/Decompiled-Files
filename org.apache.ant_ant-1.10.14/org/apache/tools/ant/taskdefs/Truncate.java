/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;

public class Truncate
extends Task {
    private static final int BUFFER_SIZE = 1024;
    private static final Long ZERO = 0L;
    private static final String NO_CHILD = "No files specified.";
    private static final String INVALID_LENGTH = "Cannot truncate to length ";
    private static final String READ_WRITE = "rw";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final byte[] FILL_BUFFER = new byte[1024];
    private Path path;
    private boolean create = true;
    private boolean mkdirs = false;
    private Long length;
    private Long adjust;

    public void setFile(File f) {
        this.add(new FileResource(f));
    }

    public void add(ResourceCollection rc) {
        this.getPath().add(rc);
    }

    public void setAdjust(Long adjust) {
        this.adjust = adjust;
    }

    public void setLength(Long length) {
        this.length = length;
        if (length != null && length < 0L) {
            throw new BuildException(INVALID_LENGTH + length);
        }
    }

    public void setCreate(boolean create) {
        this.create = create;
    }

    public void setMkdirs(boolean mkdirs) {
        this.mkdirs = mkdirs;
    }

    @Override
    public void execute() {
        if (this.length != null && this.adjust != null) {
            throw new BuildException("length and adjust are mutually exclusive options");
        }
        if (this.length == null && this.adjust == null) {
            this.length = ZERO;
        }
        if (this.path == null) {
            throw new BuildException(NO_CHILD);
        }
        for (Resource r : this.path) {
            File f = r.as(FileProvider.class).getFile();
            if (!this.shouldProcess(f)) continue;
            this.process(f);
        }
    }

    private boolean shouldProcess(File f) {
        if (f.isFile()) {
            return true;
        }
        if (!this.create) {
            return false;
        }
        IOException exception = null;
        try {
            if (FILE_UTILS.createNewFile(f, this.mkdirs)) {
                return true;
            }
        }
        catch (IOException e) {
            exception = e;
        }
        String msg = "Unable to create " + f;
        if (exception == null) {
            this.log(msg, 1);
            return false;
        }
        throw new BuildException(msg, exception);
    }

    private void process(File f) {
        long newLength;
        long len = f.length();
        long l = newLength = this.length == null ? len + this.adjust : this.length;
        if (len == newLength) {
            return;
        }
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(f, READ_WRITE);
        }
        catch (Exception e) {
            throw new BuildException("Could not open " + f + " for writing", e);
        }
        try {
            if (newLength > len) {
                long writeCount;
                long pos;
                raf.seek(pos);
                for (pos = len; pos < newLength; pos += writeCount) {
                    writeCount = Math.min((long)FILL_BUFFER.length, newLength - pos);
                    raf.write(FILL_BUFFER, 0, (int)writeCount);
                }
            } else {
                raf.setLength(newLength);
            }
        }
        catch (IOException e) {
            throw new BuildException("Exception working with " + raf, e);
        }
        finally {
            try {
                raf.close();
            }
            catch (IOException e) {
                this.log("Caught " + e + " closing " + raf, 1);
            }
        }
    }

    private synchronized Path getPath() {
        if (this.path == null) {
            this.path = new Path(this.getProject());
        }
        return this.path;
    }
}

