/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;

public class Resource
extends DataType
implements Comparable<Resource>,
ResourceCollection {
    public static final long UNKNOWN_SIZE = -1L;
    public static final long UNKNOWN_DATETIME = 0L;
    protected static final int MAGIC = Resource.getMagicNumber("Resource".getBytes());
    private static final int NULL_NAME = Resource.getMagicNumber("null name".getBytes());
    private String name = null;
    private Boolean exists = null;
    private Long lastmodified = null;
    private Boolean directory = null;
    private Long size = null;

    protected static int getMagicNumber(byte[] seed) {
        return new BigInteger(seed).intValue();
    }

    public Resource() {
    }

    public Resource(String name) {
        this(name, false, 0L, false);
    }

    public Resource(String name, boolean exists, long lastmodified) {
        this(name, exists, lastmodified, false);
    }

    public Resource(String name, boolean exists, long lastmodified, boolean directory) {
        this(name, exists, lastmodified, directory, -1L);
    }

    public Resource(String name, boolean exists, long lastmodified, boolean directory, long size) {
        this.name = name;
        this.setName(name);
        this.setExists(exists);
        this.setLastModified(lastmodified);
        this.setDirectory(directory);
        this.setSize(size);
    }

    public String getName() {
        return this.isReference() ? this.getRef().getName() : this.name;
    }

    public void setName(String name) {
        this.checkAttributesAllowed();
        this.name = name;
    }

    public boolean isExists() {
        if (this.isReference()) {
            return this.getRef().isExists();
        }
        return this.exists == null || this.exists != false;
    }

    public void setExists(boolean exists) {
        this.checkAttributesAllowed();
        this.exists = exists ? Boolean.TRUE : Boolean.FALSE;
    }

    public long getLastModified() {
        if (this.isReference()) {
            return this.getRef().getLastModified();
        }
        if (!this.isExists() || this.lastmodified == null) {
            return 0L;
        }
        long result = this.lastmodified;
        return result < 0L ? 0L : result;
    }

    public void setLastModified(long lastmodified) {
        this.checkAttributesAllowed();
        this.lastmodified = lastmodified;
    }

    public boolean isDirectory() {
        if (this.isReference()) {
            return this.getRef().isDirectory();
        }
        return this.directory != null && this.directory != false;
    }

    public void setDirectory(boolean directory) {
        this.checkAttributesAllowed();
        this.directory = directory ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setSize(long size) {
        this.checkAttributesAllowed();
        this.size = size > -1L ? size : -1L;
    }

    public long getSize() {
        if (this.isReference()) {
            return this.getRef().getSize();
        }
        return this.isExists() ? (this.size != null ? this.size : -1L) : 0L;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException("CloneNotSupportedException for a Resource caught. Derived classes must support cloning.");
        }
    }

    @Override
    public int compareTo(Resource other) {
        if (this.isReference()) {
            return this.getRef().compareTo(other);
        }
        return this.getName().compareTo(other.getName());
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (this.isReference()) {
            return this.getRef().equals(other);
        }
        return other != null && other.getClass().equals(this.getClass()) && this.compareTo((Resource)other) == 0;
    }

    public int hashCode() {
        if (this.isReference()) {
            return this.getRef().hashCode();
        }
        String name = this.getName();
        return MAGIC * (name == null ? NULL_NAME : name.hashCode());
    }

    public InputStream getInputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getInputStream();
        }
        throw new UnsupportedOperationException();
    }

    public OutputStream getOutputStream() throws IOException {
        if (this.isReference()) {
            return this.getRef().getOutputStream();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Resource> iterator() {
        return this.isReference() ? this.getRef().iterator() : Collections.singleton(this).iterator();
    }

    @Override
    public int size() {
        return this.isReference() ? this.getRef().size() : 1;
    }

    @Override
    public boolean isFilesystemOnly() {
        return this.isReference() && this.getRef().isFilesystemOnly() || this.as(FileProvider.class) != null;
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        String n = this.getName();
        return n == null ? "(anonymous)" : n;
    }

    public final String toLongString() {
        return this.isReference() ? this.getRef().toLongString() : this.getDataTypeName() + " \"" + this.toString() + '\"';
    }

    @Override
    public void setRefid(Reference r) {
        if (this.name != null || this.exists != null || this.lastmodified != null || this.directory != null || this.size != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public <T> T as(Class<T> clazz) {
        return clazz.isAssignableFrom(this.getClass()) ? (T)clazz.cast(this) : null;
    }

    public <T> Optional<T> asOptional(Class<T> clazz) {
        return Optional.ofNullable(this.as(clazz));
    }

    protected Resource getRef() {
        return this.getCheckedRef(Resource.class);
    }
}

