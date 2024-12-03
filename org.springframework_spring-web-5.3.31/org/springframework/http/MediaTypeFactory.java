/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

public final class MediaTypeFactory {
    private static final String MIME_TYPES_FILE_NAME = "/org/springframework/http/mime.types";
    private static final MultiValueMap<String, MediaType> fileExtensionToMediaTypes = MediaTypeFactory.parseMimeTypes();

    private MediaTypeFactory() {
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static MultiValueMap<String, MediaType> parseMimeTypes() {
        InputStream is = MediaTypeFactory.class.getResourceAsStream(MIME_TYPES_FILE_NAME);
        Assert.state((is != null ? 1 : 0) != 0, (String)"/org/springframework/http/mime.types not found in classpath");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));){
            String line;
            LinkedMultiValueMap result = new LinkedMultiValueMap();
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.charAt(0) == '#') continue;
                String[] tokens = StringUtils.tokenizeToStringArray((String)line, (String)" \t\n\r\f");
                MediaType mediaType = MediaType.parseMediaType(tokens[0]);
                for (int i = 1; i < tokens.length; ++i) {
                    String fileExtension = tokens[i].toLowerCase(Locale.ENGLISH);
                    result.add((Object)fileExtension, (Object)mediaType);
                }
            }
            LinkedMultiValueMap linkedMultiValueMap = result;
            return linkedMultiValueMap;
        }
        catch (IOException ex) {
            throw new IllegalStateException("Could not read /org/springframework/http/mime.types", ex);
        }
    }

    public static Optional<MediaType> getMediaType(@Nullable Resource resource) {
        return Optional.ofNullable(resource).map(Resource::getFilename).flatMap(MediaTypeFactory::getMediaType);
    }

    public static Optional<MediaType> getMediaType(@Nullable String filename) {
        return MediaTypeFactory.getMediaTypes(filename).stream().findFirst();
    }

    public static List<MediaType> getMediaTypes(@Nullable String filename) {
        List mediaTypes = null;
        String ext = StringUtils.getFilenameExtension((String)filename);
        if (ext != null) {
            mediaTypes = (List)fileExtensionToMediaTypes.get((Object)ext.toLowerCase(Locale.ENGLISH));
        }
        return mediaTypes != null ? mediaTypes : Collections.emptyList();
    }
}

