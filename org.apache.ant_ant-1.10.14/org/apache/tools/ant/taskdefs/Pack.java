/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;

public abstract class Pack
extends Task {
    private static final int BUFFER_SIZE = 8192;
    protected File zipFile;
    protected File source;
    private Resource src;

    public void setZipfile(File zipFile) {
        this.zipFile = zipFile;
    }

    public void setDestfile(File zipFile) {
        this.setZipfile(zipFile);
    }

    public void setSrc(File src) {
        this.setSrcResource(new FileResource(src));
    }

    public void setSrcResource(Resource src) {
        if (src.isDirectory()) {
            throw new BuildException("the source can't be a directory");
        }
        FileProvider fp = src.as(FileProvider.class);
        if (fp != null) {
            this.source = fp.getFile();
        } else if (!this.supportsNonFileResources()) {
            throw new BuildException("Only FileSystem resources are supported.");
        }
        this.src = src;
    }

    public void addConfigured(ResourceCollection a) {
        if (a.size() == 0) {
            throw new BuildException("No resource selected, %s needs exactly one resource.", this.getTaskName());
        }
        if (a.size() != 1) {
            throw new BuildException("%s cannot handle multiple resources at once. (%d resources were selected.)", this.getTaskName(), a.size());
        }
        this.setSrcResource((Resource)a.iterator().next());
    }

    private void validate() throws BuildException {
        if (this.zipFile == null) {
            throw new BuildException("zipfile attribute is required", this.getLocation());
        }
        if (this.zipFile.isDirectory()) {
            throw new BuildException("zipfile attribute must not represent a directory!", this.getLocation());
        }
        if (this.getSrcResource() == null) {
            throw new BuildException("src attribute or nested resource is required", this.getLocation());
        }
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        Resource s = this.getSrcResource();
        if (!s.isExists()) {
            this.log("Nothing to do: " + s.toString() + " doesn't exist.");
        } else if (this.zipFile.lastModified() < s.getLastModified()) {
            this.log("Building: " + this.zipFile.getAbsolutePath());
            this.pack();
        } else {
            this.log("Nothing to do: " + this.zipFile.getAbsolutePath() + " is up to date.");
        }
    }

    private void zipFile(InputStream in, OutputStream zOut) throws IOException {
        byte[] buffer = new byte[8192];
        int count = 0;
        do {
            zOut.write(buffer, 0, count);
        } while ((count = in.read(buffer, 0, buffer.length)) != -1);
    }

    protected void zipFile(File file, OutputStream zOut) throws IOException {
        this.zipResource(new FileResource(file), zOut);
    }

    protected void zipResource(Resource resource, OutputStream zOut) throws IOException {
        try (InputStream rIn = resource.getInputStream();){
            this.zipFile(rIn, zOut);
        }
    }

    protected abstract void pack();

    public Resource getSrcResource() {
        return this.src;
    }

    protected boolean supportsNonFileResources() {
        return false;
    }
}

