/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.ByteArrayResource
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

public class TransformedResource
extends ByteArrayResource {
    @Nullable
    private final String filename;
    private final long lastModified;

    public TransformedResource(Resource original, byte[] transformedContent) {
        super(transformedContent);
        this.filename = original.getFilename();
        try {
            this.lastModified = original.lastModified();
        }
        catch (IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @Nullable
    public String getFilename() {
        return this.filename;
    }

    public long lastModified() throws IOException {
        return this.lastModified;
    }
}

