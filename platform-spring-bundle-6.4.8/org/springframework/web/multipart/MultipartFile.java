/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFileResource;

public interface MultipartFile
extends InputStreamSource {
    public String getName();

    @Nullable
    public String getOriginalFilename();

    @Nullable
    public String getContentType();

    public boolean isEmpty();

    public long getSize();

    public byte[] getBytes() throws IOException;

    @Override
    public InputStream getInputStream() throws IOException;

    default public Resource getResource() {
        return new MultipartFileResource(this);
    }

    public void transferTo(File var1) throws IOException, IllegalStateException;

    default public void transferTo(Path dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.getInputStream(), Files.newOutputStream(dest, new OpenOption[0]));
    }
}

