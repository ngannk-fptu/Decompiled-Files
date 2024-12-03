/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.ImmutableResourceException;
import org.apache.tools.ant.util.PropertyOutputStream;

public class PropertyResource
extends Resource {
    private static final int PROPERTY_MAGIC = Resource.getMagicNumber("PropertyResource".getBytes());
    private static final InputStream UNSET = new InputStream(){

        @Override
        public int read() {
            return -1;
        }
    };

    public PropertyResource() {
    }

    public PropertyResource(Project p, String n) {
        super(n);
        this.setProject(p);
    }

    public String getValue() {
        if (this.isReference()) {
            return this.getRef().getValue();
        }
        Project p = this.getProject();
        return p == null ? null : p.getProperty(this.getName());
    }

    public Object getObjectValue() {
        if (this.isReference()) {
            return this.getRef().getObjectValue();
        }
        Project p = this.getProject();
        return p == null ? null : PropertyHelper.getProperty(p, this.getName());
    }

    @Override
    public boolean isExists() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().isExists();
        }
        return this.getObjectValue() != null;
    }

    @Override
    public long getSize() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getSize();
        }
        Object o = this.getObjectValue();
        return o == null ? 0L : (long)String.valueOf(o).length();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || this.isReferenceOrProxy() && this.getReferencedOrProxied().equals(o);
    }

    @Override
    public int hashCode() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().hashCode();
        }
        return super.hashCode() * PROPERTY_MAGIC;
    }

    @Override
    public String toString() {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().toString();
        }
        return this.getValue();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getInputStream();
        }
        Object o = this.getObjectValue();
        return o == null ? UNSET : new ByteArrayInputStream(String.valueOf(o).getBytes());
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (this.isReferenceOrProxy()) {
            return this.getReferencedOrProxied().getOutputStream();
        }
        if (this.isExists()) {
            throw new ImmutableResourceException();
        }
        return new PropertyOutputStream(this.getProject(), this.getName());
    }

    protected boolean isReferenceOrProxy() {
        return this.isReference() || this.getObjectValue() instanceof Resource;
    }

    protected Resource getReferencedOrProxied() {
        if (this.isReference()) {
            return super.getRef();
        }
        Object o = this.getObjectValue();
        if (o instanceof Resource) {
            return (Resource)o;
        }
        throw new IllegalStateException("This PropertyResource does not reference or proxy another Resource");
    }

    @Override
    protected PropertyResource getRef() {
        return this.getCheckedRef(PropertyResource.class);
    }
}

