/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.ArchiveResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

public class TarResource
extends ArchiveResource {
    private String userName = "";
    private String groupName = "";
    private long uid;
    private long gid;
    private byte linkFlag = (byte)48;
    private String linkName = "";

    public TarResource() {
    }

    public TarResource(File a, TarEntry e) {
        super(a, true);
        this.setEntry(e);
    }

    public TarResource(Resource a, TarEntry e) {
        super(a, true);
        this.setEntry(e);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        TarEntry te;
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        Resource archive = this.getArchive();
        TarInputStream i = new TarInputStream(archive.getInputStream());
        while ((te = i.getNextEntry()) != null) {
            if (!te.getName().equals(this.getName())) continue;
            return i;
        }
        FileUtils.close(i);
        throw new BuildException("no entry " + this.getName() + " in " + this.getArchive());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        throw new UnsupportedOperationException("Use the tar task for tar output.");
    }

    public String getUserName() {
        if (this.isReference()) {
            return this.getRef().getUserName();
        }
        this.checkEntry();
        return this.userName;
    }

    public String getGroup() {
        if (this.isReference()) {
            return this.getRef().getGroup();
        }
        this.checkEntry();
        return this.groupName;
    }

    public long getLongUid() {
        if (this.isReference()) {
            return this.getRef().getLongUid();
        }
        this.checkEntry();
        return this.uid;
    }

    @Deprecated
    public int getUid() {
        return (int)this.getLongUid();
    }

    public long getLongGid() {
        if (this.isReference()) {
            return this.getRef().getLongGid();
        }
        this.checkEntry();
        return this.gid;
    }

    @Deprecated
    public int getGid() {
        return (int)this.getLongGid();
    }

    public String getLinkName() {
        return this.linkName;
    }

    public byte getLinkFlag() {
        return this.linkFlag;
    }

    @Override
    protected void fetchEntry() {
        Resource archive = this.getArchive();
        try (TarInputStream i = new TarInputStream(archive.getInputStream());){
            TarEntry te = null;
            while ((te = i.getNextEntry()) != null) {
                if (!te.getName().equals(this.getName())) continue;
                this.setEntry(te);
                return;
            }
        }
        catch (IOException e) {
            this.log(e.getMessage(), 4);
            throw new BuildException(e);
        }
        this.setEntry(null);
    }

    @Override
    protected TarResource getRef() {
        return this.getCheckedRef(TarResource.class);
    }

    private void setEntry(TarEntry e) {
        if (e == null) {
            this.setExists(false);
            return;
        }
        this.setName(e.getName());
        this.setExists(true);
        this.setLastModified(e.getModTime().getTime());
        this.setDirectory(e.isDirectory());
        this.setSize(e.getSize());
        this.setMode(e.getMode());
        this.userName = e.getUserName();
        this.groupName = e.getGroupName();
        this.uid = e.getLongUserId();
        this.gid = e.getLongGroupId();
        this.linkName = e.getLinkName();
        this.linkFlag = e.getLinkFlag();
    }
}

