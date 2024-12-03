/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.util.Assert
 */
package org.springframework.security.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import org.springframework.core.io.AbstractResource;
import org.springframework.util.Assert;

public class InMemoryResource
extends AbstractResource {
    private final byte[] source;
    private final String description;

    public InMemoryResource(String source) {
        this(source.getBytes());
    }

    public InMemoryResource(byte[] source) {
        this(source, null);
    }

    public InMemoryResource(byte[] source, String description) {
        Assert.notNull((Object)source, (String)"source cannot be null");
        this.source = source;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.source);
    }

    public boolean equals(Object res) {
        if (!(res instanceof InMemoryResource)) {
            return false;
        }
        return Arrays.equals(this.source, ((InMemoryResource)((Object)res)).source);
    }

    public int hashCode() {
        return 1;
    }
}

