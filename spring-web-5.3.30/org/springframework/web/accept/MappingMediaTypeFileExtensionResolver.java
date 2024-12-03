/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.web.accept;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.accept.MediaTypeFileExtensionResolver;

public class MappingMediaTypeFileExtensionResolver
implements MediaTypeFileExtensionResolver {
    private final ConcurrentMap<String, MediaType> mediaTypes = new ConcurrentHashMap<String, MediaType>(64);
    private final ConcurrentMap<MediaType, List<String>> fileExtensions = new ConcurrentHashMap<MediaType, List<String>>(64);
    private final List<String> allFileExtensions = new CopyOnWriteArrayList<String>();

    public MappingMediaTypeFileExtensionResolver(@Nullable Map<String, MediaType> mediaTypes) {
        if (mediaTypes != null) {
            HashSet allFileExtensions = new HashSet(mediaTypes.size());
            mediaTypes.forEach((extension, mediaType) -> {
                String lowerCaseExtension = extension.toLowerCase(Locale.ENGLISH);
                this.mediaTypes.put(lowerCaseExtension, (MediaType)mediaType);
                this.addFileExtension((MediaType)mediaType, lowerCaseExtension);
                allFileExtensions.add(lowerCaseExtension);
            });
            this.allFileExtensions.addAll(allFileExtensions);
        }
    }

    public Map<String, MediaType> getMediaTypes() {
        return this.mediaTypes;
    }

    protected List<MediaType> getAllMediaTypes() {
        return new ArrayList<MediaType>(this.mediaTypes.values());
    }

    protected void addMapping(String extension, MediaType mediaType) {
        MediaType previous = this.mediaTypes.putIfAbsent(extension, mediaType);
        if (previous == null) {
            this.addFileExtension(mediaType, extension);
            this.allFileExtensions.add(extension);
        }
    }

    private void addFileExtension(MediaType mediaType, String extension) {
        this.fileExtensions.computeIfAbsent(mediaType, key -> new CopyOnWriteArrayList()).add(extension);
    }

    @Override
    public List<String> resolveFileExtensions(MediaType mediaType) {
        List<String> fileExtensions = (List<String>)this.fileExtensions.get(mediaType);
        return fileExtensions != null ? fileExtensions : Collections.emptyList();
    }

    @Override
    public List<String> getAllFileExtensions() {
        return Collections.unmodifiableList(this.allFileExtensions);
    }

    @Nullable
    protected MediaType lookupMediaType(String extension) {
        return (MediaType)this.mediaTypes.get(extension.toLowerCase(Locale.ENGLISH));
    }
}

