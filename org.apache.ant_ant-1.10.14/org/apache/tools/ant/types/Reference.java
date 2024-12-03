/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class Reference {
    private String refid;
    private Project project;

    @Deprecated
    public Reference() {
    }

    @Deprecated
    public Reference(String id) {
        this.setRefId(id);
    }

    public Reference(Project p, String id) {
        this.setRefId(id);
        this.setProject(p);
    }

    public void setRefId(String id) {
        this.refid = id;
    }

    public String getRefId() {
        return this.refid;
    }

    public void setProject(Project p) {
        this.project = p;
    }

    public Project getProject() {
        return this.project;
    }

    public <T> T getReferencedObject(Project fallback) throws BuildException {
        Object o;
        if (this.refid == null) {
            throw new BuildException("No reference specified");
        }
        Object t = o = this.project == null ? fallback.getReference(this.refid) : this.project.getReference(this.refid);
        if (o == null) {
            throw new BuildException("Reference " + this.refid + " not found.");
        }
        return o;
    }

    public <T> T getReferencedObject() throws BuildException {
        if (this.project == null) {
            throw new BuildException("No project set on reference to " + this.refid);
        }
        return this.getReferencedObject(this.project);
    }
}

