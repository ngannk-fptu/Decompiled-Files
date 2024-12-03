/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Stack;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;

public abstract class ResourceDecorator
extends Resource {
    private Resource resource;

    protected ResourceDecorator() {
    }

    protected ResourceDecorator(ResourceCollection other) {
        this.addConfigured(other);
    }

    public final void addConfigured(ResourceCollection a) {
        this.checkChildrenAllowed();
        if (this.resource != null) {
            throw new BuildException("you must not specify more than one resource");
        }
        if (a.size() != 1) {
            throw new BuildException("only single argument resource collections are supported");
        }
        this.setChecked(false);
        this.resource = (Resource)a.iterator().next();
    }

    @Override
    public String getName() {
        return this.getResource().getName();
    }

    @Override
    public boolean isExists() {
        return this.getResource().isExists();
    }

    @Override
    public long getLastModified() {
        return this.getResource().getLastModified();
    }

    @Override
    public boolean isDirectory() {
        return this.getResource().isDirectory();
    }

    @Override
    public long getSize() {
        return this.getResource().getSize();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return this.getResource().getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return this.getResource().getOutputStream();
    }

    @Override
    public boolean isFilesystemOnly() {
        return this.as(FileProvider.class) != null;
    }

    @Override
    public void setRefid(Reference r) {
        if (this.resource != null) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }

    @Override
    public <T> T as(Class<T> clazz) {
        return this.getResource().as(clazz);
    }

    @Override
    public int compareTo(Resource other) {
        if (other == this) {
            return 0;
        }
        if (other instanceof ResourceDecorator) {
            return this.getResource().compareTo(((ResourceDecorator)other).getResource());
        }
        return this.getResource().compareTo(other);
    }

    @Override
    public int hashCode() {
        return this.getClass().hashCode() << 4 | this.getResource().hashCode();
    }

    protected final Resource getResource() {
        if (this.isReference()) {
            return this.getCheckedRef(Resource.class);
        }
        if (this.resource == null) {
            throw new BuildException("no resource specified");
        }
        this.dieOnCircularReference();
        return this.resource;
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stack, Project project) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stack, project);
        } else {
            ResourceDecorator.pushAndInvokeCircularReferenceCheck(this.resource, stack, project);
            this.setChecked(true);
        }
    }

    @Override
    public void setName(String name) throws BuildException {
        throw new BuildException("you can't change the name of a " + this.getDataTypeName());
    }

    @Override
    public void setExists(boolean exists) {
        throw new BuildException("you can't change the exists state of a " + this.getDataTypeName());
    }

    @Override
    public void setLastModified(long lastmodified) throws BuildException {
        throw new BuildException("you can't change the timestamp of a " + this.getDataTypeName());
    }

    @Override
    public void setDirectory(boolean directory) throws BuildException {
        throw new BuildException("you can't change the directory state of a " + this.getDataTypeName());
    }

    @Override
    public void setSize(long size) throws BuildException {
        throw new BuildException("you can't change the size of a " + this.getDataTypeName());
    }
}

