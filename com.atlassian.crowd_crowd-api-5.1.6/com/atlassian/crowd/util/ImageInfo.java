/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.util;

import java.util.Objects;
import java.util.Optional;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageInfo {
    private static final Logger log = LoggerFactory.getLogger(ImageInfo.class);
    private static final String IMAGE_FILE_PREFIX = "image-";
    private static final String SEPARATOR = ",";
    private static final String DATA_IMAGE_PREFIX = "data:image/";
    private static final String BASE_64_IMAGE_PREFIX = ";base64";
    private final String name;
    private final String imageBase64;
    private final String extension;

    @JsonCreator
    public ImageInfo(@JsonProperty(value="name") String name, @JsonProperty(value="imageBase64") String imageBase64, @JsonProperty(value="extension") String extension) {
        this.name = name;
        this.imageBase64 = imageBase64;
        this.extension = extension;
    }

    public static Optional<ImageInfo> fromImageData(String imageData) {
        try {
            String clearedImage = imageData.trim().replace(DATA_IMAGE_PREFIX, "").replace(BASE_64_IMAGE_PREFIX, "");
            int commaIndex = clearedImage.indexOf(SEPARATOR);
            String extension = clearedImage.substring(0, commaIndex);
            String imageBase64 = clearedImage.substring(commaIndex + 1);
            String imageName = IMAGE_FILE_PREFIX + Math.abs(Objects.hash(imageBase64));
            return Optional.of(new ImageInfo(imageName, imageBase64, extension));
        }
        catch (Exception e) {
            log.debug("Error while extracting imageData {}", (Object)imageData, (Object)e);
            return Optional.empty();
        }
    }

    public String getName() {
        return this.name;
    }

    public String getImageBase64() {
        return this.imageBase64;
    }

    public String getExtension() {
        return this.extension;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ImageInfo imageInfo = (ImageInfo)o;
        return Objects.equals(this.name, imageInfo.name) && Objects.equals(this.imageBase64, imageInfo.imageBase64) && Objects.equals(this.extension, imageInfo.extension);
    }

    public int hashCode() {
        return Objects.hash(this.name, this.imageBase64, this.extension);
    }
}

