/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.accept;

import java.util.List;
import org.springframework.http.MediaType;

public interface MediaTypeFileExtensionResolver {
    public List<String> resolveFileExtensions(MediaType var1);

    public List<String> getAllFileExtensions();
}

