/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.multipart;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

public interface MultipartRequest {
    public Iterator<String> getFileNames();

    @Nullable
    public MultipartFile getFile(String var1);

    public List<MultipartFile> getFiles(String var1);

    public Map<String, MultipartFile> getFileMap();

    public MultiValueMap<String, MultipartFile> getMultiFileMap();

    @Nullable
    public String getMultipartContentType(String var1);
}

