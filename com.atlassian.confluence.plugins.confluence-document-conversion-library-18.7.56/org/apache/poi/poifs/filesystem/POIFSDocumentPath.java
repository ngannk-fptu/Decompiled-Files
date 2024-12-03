/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.filesystem;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class POIFSDocumentPath {
    private static final Logger LOGGER = LogManager.getLogger(POIFSDocumentPath.class);
    private final String[] components;
    private int hashcode;

    public POIFSDocumentPath() {
        this.components = new String[0];
    }

    public POIFSDocumentPath(String[] components) throws IllegalArgumentException {
        this(null, components);
    }

    public POIFSDocumentPath(POIFSDocumentPath path, String[] components) throws IllegalArgumentException {
        Predicate<String> p;
        String[] s1 = path == null ? new String[]{} : path.components;
        String[] s2 = components == null ? new String[]{} : components;
        Predicate<String> predicate = p = path != null ? Objects::isNull : s -> s == null || s.isEmpty();
        if (Stream.of(s2).anyMatch(p)) {
            throw new IllegalArgumentException("components cannot contain null or empty strings");
        }
        this.components = (String[])Stream.concat(Stream.of(s1), Stream.of(s2)).toArray(String[]::new);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null && o.getClass() == this.getClass()) {
            POIFSDocumentPath path = (POIFSDocumentPath)o;
            return Arrays.equals(this.components, path.components);
        }
        return false;
    }

    public int hashCode() {
        return this.hashcode == 0 ? (this.hashcode = Arrays.hashCode(this.components)) : this.hashcode;
    }

    public int length() {
        return this.components.length;
    }

    public String getComponent(int n) throws ArrayIndexOutOfBoundsException {
        return this.components[n];
    }

    public POIFSDocumentPath getParent() {
        return this.components.length == 0 ? null : new POIFSDocumentPath(Arrays.copyOf(this.components, this.components.length - 1));
    }

    public String getName() {
        return this.components.length == 0 ? "" : this.components[this.components.length - 1];
    }

    public String toString() {
        return File.separatorChar + String.join((CharSequence)String.valueOf(File.separatorChar), this.components);
    }
}

