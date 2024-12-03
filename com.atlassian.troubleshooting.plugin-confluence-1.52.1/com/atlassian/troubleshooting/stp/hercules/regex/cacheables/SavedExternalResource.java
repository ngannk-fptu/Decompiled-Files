/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.troubleshooting.stp.hercules.regex.cacheables;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;

public interface SavedExternalResource {
    @Nonnull
    public URL getCachedUrl();

    public String getLocalFilename();

    default public String parseResponse(Response response) throws ResponseException {
        return response.getResponseBodyAsString();
    }

    public static String hashFilename(String url, String filename) {
        int maxFilenameLength = 130;
        try {
            return StringUtils.left((String)Base64.getUrlEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(url.getBytes())), (int)(130 - filename.length())) + filename;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("This should never happen, apparently MD5 isn't an algorithm in this java release", e);
        }
    }
}

