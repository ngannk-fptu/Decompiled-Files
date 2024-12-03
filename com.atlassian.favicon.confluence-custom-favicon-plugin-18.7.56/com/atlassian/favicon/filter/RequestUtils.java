/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.ThumbnailDimension
 *  com.atlassian.favicon.core.FaviconSize
 *  com.atlassian.favicon.core.ImageType
 */
package com.atlassian.favicon.filter;

import com.atlassian.core.util.thumbnail.ThumbnailDimension;
import com.atlassian.favicon.core.FaviconSize;
import com.atlassian.favicon.core.ImageType;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestUtils {
    private static final Pattern WIDTH_HEIGHT_REGEX = Pattern.compile("(\\d+)-(\\d+)");
    private static final Pattern ONE_NUMBER_REGEX = Pattern.compile("(\\d+)");
    private static final Pattern FILE_PATH_WITHOUT_RESOURCE_PREFIX = Pattern.compile("/_/(.+)$");

    public static Optional<ImageType> getImageTypeFromRequestURL(String aRequestURL) {
        int lastDot = aRequestURL.lastIndexOf(46);
        if (lastDot > 0 && lastDot < aRequestURL.length() - 1) {
            return ImageType.parseFromExtension((String)aRequestURL.substring(lastDot + 1));
        }
        return Optional.empty();
    }

    public static Optional<ThumbnailDimension> getDesiredSizeFromRequestURL(String aRequestURL) {
        String[] urlBits = aRequestURL.split("/");
        String filename = urlBits[urlBits.length - 1];
        Matcher widthHeightMatcher = WIDTH_HEIGHT_REGEX.matcher(filename);
        if (widthHeightMatcher.find()) {
            return Optional.of(FaviconSize.fromWidthAndHeight((int)Integer.parseInt(widthHeightMatcher.group(1)), (int)Integer.parseInt(widthHeightMatcher.group(2))));
        }
        Matcher oneNumberMatcher = ONE_NUMBER_REGEX.matcher(filename);
        if (oneNumberMatcher.find()) {
            int dimension = Integer.parseInt(oneNumberMatcher.group(1));
            return Optional.of(FaviconSize.fromWidthAndHeight((int)dimension, (int)dimension));
        }
        return Optional.empty();
    }

    public static String getFilePathWithoutStaticResourcePrefix(String aPath) {
        Matcher filePathMatcher = FILE_PATH_WITHOUT_RESOURCE_PREFIX.matcher(aPath);
        if (filePathMatcher.find()) {
            return "/" + filePathMatcher.group(1);
        }
        return aPath;
    }

    private RequestUtils() {
    }
}

