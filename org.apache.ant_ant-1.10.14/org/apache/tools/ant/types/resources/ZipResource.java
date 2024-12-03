/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipExtraField;
import org.apache.tools.zip.ZipFile;

public class ZipResource
extends ArchiveResource {
    private String encoding;
    private ZipExtraField[] extras;
    private int method;

    public ZipResource() {
    }

    public ZipResource(File z, String enc, ZipEntry e) {
        super(z, true);
        this.setEncoding(enc);
        this.setEntry(e);
    }

    public void setZipfile(File z) {
        this.setArchive(z);
    }

    public File getZipfile() {
        FileProvider fp = this.getArchive().as(FileProvider.class);
        return fp.getFile();
    }

    @Override
    public void addConfigured(ResourceCollection a) {
        super.addConfigured(a);
        if (!a.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
    }

    public void setEncoding(String enc) {
        this.checkAttributesAllowed();
        this.encoding = enc;
    }

    public String getEncoding() {
        return this.isReference() ? this.getRef().getEncoding() : this.encoding;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.encoding != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        return ZipResource.getZipEntryStream(new ZipFile(this.getZipfile(), this.getEncoding()), this.getName());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        throw new UnsupportedOperationException("Use the zip task for zip output.");
    }

    public ZipExtraField[] getExtraFields() {
        if (this.isReference()) {
            return this.getRef().getExtraFields();
        }
        this.checkEntry();
        if (this.extras == null) {
            return new ZipExtraField[0];
        }
        return this.extras;
    }

    public int getMethod() {
        return this.method;
    }

    public static InputStream getZipEntryStream(final ZipFile zipFile, String zipEntry) throws IOException {
        ZipEntry ze = zipFile.getEntry(zipEntry);
        if (ze == null) {
            zipFile.close();
            throw new BuildException("no entry " + zipEntry + " in " + zipFile.getName());
        }
        return new FilterInputStream(zipFile.getInputStream(ze)){

            @Override
            public void close() throws IOException {
                FileUtils.close(this.in);
                zipFile.close();
            }

            protected void finalize() throws Throwable {
                try {
                    this.close();
                }
                finally {
                    super.finalize();
                }
            }
        };
    }

    @Override
    protected void fetchEntry() {
        ZipFile z = null;
        try {
            z = new ZipFile(this.getZipfile(), this.getEncoding());
            this.setEntry(z.getEntry(this.getName()));
        }
        catch (IOException e) {
            try {
                this.log(e.getMessage(), 4);
                throw new BuildException(e);
            }
            catch (Throwable throwable) {
                ZipFile.closeQuietly(z);
                throw throwable;
            }
        }
        ZipFile.closeQuietly(z);
    }

    @Override
    protected ZipResource getRef() {
        return this.getCheckedRef(ZipResource.class);
    }

    private void setEntry(ZipEntry e) {
        if (e == null) {
            this.setExists(false);
            return;
        }
        this.setName(e.getName());
        this.setExists(true);
        this.setLastModified(e.getTime());
        this.setDirectory(e.isDirectory());
        this.setSize(e.getSize());
        this.setMode(e.getUnixMode());
        this.extras = e.getExtraFields(true);
        this.method = e.getMethod();
    }
}

