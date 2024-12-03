/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

class MultipartFileResource
extends AbstractResource {
    private final MultipartFile multipartFile;

    public MultipartFileResource(MultipartFile multipartFile) {
        Assert.notNull((Object)multipartFile, (String)"MultipartFile must not be null");
        this.multipartFile = multipartFile;
    }

    public boolean exists() {
        return true;
    }

    public boolean isOpen() {
        return true;
    }

    public long contentLength() {
        return this.multipartFile.getSize();
    }

    public String getFilename() {
        return this.multipartFile.getOriginalFilename();
    }

    public InputStream getInputStream() throws IOException, IllegalStateException {
        return this.multipartFile.getInputStream();
    }

    public String getDescription() {
        return "MultipartFile resource [" + this.multipartFile.getName() + "]";
    }

    public boolean equals(@Nullable Object other) {
        return this == other || other instanceof MultipartFileResource && ((MultipartFileResource)((Object)other)).multipartFile.equals(this.multipartFile);
    }

    public int hashCode() {
        return this.multipartFile.hashCode();
    }
}

