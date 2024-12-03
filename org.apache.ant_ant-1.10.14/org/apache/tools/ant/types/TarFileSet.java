/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.AbstractFileSet;
import org.apache.tools.ant.types.ArchiveFileSet;
import org.apache.tools.ant.types.ArchiveScanner;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.TarScanner;

public class TarFileSet
extends ArchiveFileSet {
    private boolean userNameSet;
    private boolean groupNameSet;
    private boolean userIdSet;
    private boolean groupIdSet;
    private String userName = "";
    private String groupName = "";
    private int uid;
    private int gid;

    public TarFileSet() {
    }

    protected TarFileSet(FileSet fileset) {
        super(fileset);
    }

    protected TarFileSet(TarFileSet fileset) {
        super(fileset);
    }

    public void setUserName(String userName) {
        this.checkTarFileSetAttributesAllowed();
        this.userNameSet = true;
        this.userName = userName;
    }

    public String getUserName() {
        if (this.isReference()) {
            return ((TarFileSet)this.getRef()).getUserName();
        }
        return this.userName;
    }

    public boolean hasUserNameBeenSet() {
        return this.userNameSet;
    }

    public void setUid(int uid) {
        this.checkTarFileSetAttributesAllowed();
        this.userIdSet = true;
        this.uid = uid;
    }

    public int getUid() {
        if (this.isReference()) {
            return ((TarFileSet)this.getRef()).getUid();
        }
        return this.uid;
    }

    public boolean hasUserIdBeenSet() {
        return this.userIdSet;
    }

    public void setGroup(String groupName) {
        this.checkTarFileSetAttributesAllowed();
        this.groupNameSet = true;
        this.groupName = groupName;
    }

    public String getGroup() {
        if (this.isReference()) {
            return ((TarFileSet)this.getRef()).getGroup();
        }
        return this.groupName;
    }

    public boolean hasGroupBeenSet() {
        return this.groupNameSet;
    }

    public void setGid(int gid) {
        this.checkTarFileSetAttributesAllowed();
        this.groupIdSet = true;
        this.gid = gid;
    }

    public int getGid() {
        if (this.isReference()) {
            return ((TarFileSet)this.getRef()).getGid();
        }
        return this.gid;
    }

    public boolean hasGroupIdBeenSet() {
        return this.groupIdSet;
    }

    @Override
    protected ArchiveScanner newArchiveScanner() {
        TarScanner zs = new TarScanner();
        zs.setEncoding(this.getEncoding());
        return zs;
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.userNameSet || this.userIdSet || this.groupNameSet || this.groupIdSet) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    @Override
    protected AbstractFileSet getRef(Project p) {
        this.dieOnCircularReference(p);
        Object o = this.getRefid().getReferencedObject(p);
        if (o instanceof TarFileSet) {
            return (AbstractFileSet)o;
        }
        if (o instanceof FileSet) {
            TarFileSet zfs = new TarFileSet((FileSet)o);
            this.configureFileSet(zfs);
            return zfs;
        }
        String msg = this.getRefid().getRefId() + " doesn't denote a tarfileset or a fileset";
        throw new BuildException(msg);
    }

    @Override
    protected AbstractFileSet getRef() {
        return this.getRef(this.getProject());
    }

    @Override
    protected void configureFileSet(ArchiveFileSet zfs) {
        super.configureFileSet(zfs);
        if (zfs instanceof TarFileSet) {
            TarFileSet tfs = (TarFileSet)zfs;
            tfs.setUserName(this.userName);
            tfs.setGroup(this.groupName);
            tfs.setUid(this.uid);
            tfs.setGid(this.gid);
        }
    }

    @Override
    public Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        return super.clone();
    }

    private void checkTarFileSetAttributesAllowed() {
        if (this.getProject() == null || this.isReference() && this.getRefid().getReferencedObject(this.getProject()) instanceof TarFileSet) {
            this.checkAttributesAllowed();
        }
    }
}

