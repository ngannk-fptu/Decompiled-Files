/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.resources.ImmutableResourceException;

public class StringResource
extends Resource {
    private static final int STRING_MAGIC = Resource.getMagicNumber("StringResource".getBytes());
    private static final String DEFAULT_ENCODING = "UTF-8";
    private String encoding = "UTF-8";

    public StringResource() {
    }

    public StringResource(String value) {
        this(null, value);
    }

    public StringResource(Project project, String value) {
        this.setProject(project);
        this.setValue(project == null ? value : project.replaceProperties(value));
    }

    @Override
    public synchronized void setName(String s) {
        if (this.getName() != null) {
            throw new BuildException(new ImmutableResourceException());
        }
        super.setName(s);
    }

    public synchronized void setValue(String s) {
        this.setName(s);
    }

    @Override
    public synchronized String getName() {
        return super.getName();
    }

    public synchronized String getValue() {
        return this.getName();
    }

    @Override
    public boolean isExists() {
        return this.getValue() != null;
    }

    public void addText(String text) {
        this.checkChildrenAllowed();
        this.setValue(this.getProject().replaceProperties(text));
    }

    public synchronized void setEncoding(String s) {
        this.checkAttributesAllowed();
        this.encoding = s;
    }

    public synchronized String getEncoding() {
        return this.encoding;
    }

    @Override
    public synchronized long getSize() {
        return this.isReference() ? this.getRef().getSize() : (long)this.getContent().length();
    }

    @Override
    public synchronized int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        return super.hashCode() * STRING_MAGIC;
    }

    @Override
    public String toString() {
        return String.valueOf(this.getContent());
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        String content = this.getContent();
        if (content == null) {
            throw new IllegalStateException("unset string value");
        }
        return new ByteArrayInputStream(this.encoding == null ? content.getBytes() : content.getBytes(this.encoding));
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        if (this.getValue() != null) {
            throw new ImmutableResourceException();
        }
        return new StringResourceFilterOutputStream();
    }

    @Override
    public void setRefid(Reference r) {
        if (this.encoding != DEFAULT_ENCODING) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    protected synchronized String getContent() {
        return this.getValue();
    }

    @Override
    protected StringResource getRef() {
        return this.getCheckedRef(StringResource.class);
    }

    private class StringResourceFilterOutputStream
    extends FilterOutputStream {
        private final ByteArrayOutputStream baos;

        public StringResourceFilterOutputStream() {
            super(new ByteArrayOutputStream());
            this.baos = (ByteArrayOutputStream)this.out;
        }

        @Override
        public void close() throws IOException {
            super.close();
            String result = StringResource.this.encoding == null ? this.baos.toString() : this.baos.toString(StringResource.this.encoding);
            this.setValueFromOutputStream(result);
        }

        private void setValueFromOutputStream(String output) {
            String value = StringResource.this.getProject() != null ? StringResource.this.getProject().replaceProperties(output) : output;
            StringResource.this.setValue(value);
        }
    }
}

