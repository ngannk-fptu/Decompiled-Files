/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.multipart;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.InputStreamSource;
import org.springframework.lang.Nullable;

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

    public void transferTo(File var1) throws IOException, IllegalStateException;
}

