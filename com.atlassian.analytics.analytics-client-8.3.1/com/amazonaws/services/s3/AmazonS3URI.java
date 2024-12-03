/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AmazonS3URI {
    private static final Pattern ENDPOINT_PATTERN = Pattern.compile("^(.+\\.)?s3[.-]([a-z0-9-]+)\\.");
    private static final Pattern VERSION_ID_PATTERN = Pattern.compile("[&;]");
    private final URI uri;
    private final boolean isPathStyle;
    private final String bucket;
    private final String key;
    private final String versionId;
    private final String region;

    public AmazonS3URI(String str) {
        this(str, true);
    }

    public AmazonS3URI(String str, boolean urlEncode) {
        this(URI.create(AmazonS3URI.preprocessUrlStr(str, urlEncode)), urlEncode);
    }

    public AmazonS3URI(URI uri) {
        this(uri, false);
    }

    private AmazonS3URI(URI uri, boolean urlEncode) {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        this.uri = uri;
        if ("s3".equalsIgnoreCase(uri.getScheme())) {
            this.region = null;
            this.versionId = null;
            this.isPathStyle = false;
            this.bucket = uri.getAuthority();
            if (this.bucket == null) {
                throw new IllegalArgumentException("Invalid S3 URI: no bucket: " + uri);
            }
            String path = uri.getPath();
            this.key = path.length() <= 1 ? null : uri.getPath().substring(1);
            return;
        }
        String host = uri.getHost();
        if (host == null) {
            throw new IllegalArgumentException("Invalid S3 URI: no hostname: " + uri);
        }
        Matcher matcher = ENDPOINT_PATTERN.matcher(host);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid S3 URI: hostname does not appear to be a valid S3 endpoint: " + uri);
        }
        String prefix = matcher.group(1);
        if (prefix == null || prefix.isEmpty()) {
            String path;
            this.isPathStyle = true;
            String string = path = urlEncode ? uri.getPath() : uri.getRawPath();
            if ("".equals(path) || "/".equals(path)) {
                this.bucket = null;
                this.key = null;
            } else {
                int index = path.indexOf(47, 1);
                if (index == -1) {
                    this.bucket = AmazonS3URI.decode(path.substring(1));
                    this.key = null;
                } else if (index == path.length() - 1) {
                    this.bucket = AmazonS3URI.decode(path.substring(1, index));
                    this.key = null;
                } else {
                    this.bucket = AmazonS3URI.decode(path.substring(1, index));
                    this.key = AmazonS3URI.decode(path.substring(index + 1));
                }
            }
        } else {
            this.isPathStyle = false;
            this.bucket = prefix.substring(0, prefix.length() - 1);
            String path = uri.getPath();
            this.key = path == null || path.isEmpty() || "/".equals(uri.getPath()) ? null : uri.getPath().substring(1);
        }
        this.versionId = AmazonS3URI.parseVersionId(uri.getRawQuery());
        this.region = "amazonaws".equals(matcher.group(2)) ? null : matcher.group(2);
    }

    private static String parseVersionId(String query) {
        if (query != null) {
            String[] params;
            for (String param : params = VERSION_ID_PATTERN.split(query)) {
                if (!param.startsWith("versionId=")) continue;
                return AmazonS3URI.decode(param.substring(10));
            }
        }
        return null;
    }

    public URI getURI() {
        return this.uri;
    }

    public boolean isPathStyle() {
        return this.isPathStyle;
    }

    public String getBucket() {
        return this.bucket;
    }

    public String getKey() {
        return this.key;
    }

    public String getVersionId() {
        return this.versionId;
    }

    public String getRegion() {
        return this.region;
    }

    public String toString() {
        return this.uri.toString();
    }

    private static String preprocessUrlStr(String str, boolean encode) {
        if (encode) {
            try {
                return URLEncoder.encode(str, "UTF-8").replace("%3A", ":").replace("%2F", "/").replace("+", "%20");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return str;
    }

    private static String decode(String str) {
        if (str == null) {
            return null;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) != '%') continue;
            return AmazonS3URI.decode(str, i);
        }
        return str;
    }

    private static String decode(String str, int firstPercent) {
        StringBuilder builder = new StringBuilder();
        builder.append(str.substring(0, firstPercent));
        AmazonS3URI.appendDecoded(builder, str, firstPercent);
        for (int i = firstPercent + 3; i < str.length(); ++i) {
            if (str.charAt(i) == '%') {
                AmazonS3URI.appendDecoded(builder, str, i);
                i += 2;
                continue;
            }
            builder.append(str.charAt(i));
        }
        return builder.toString();
    }

    private static void appendDecoded(StringBuilder builder, String str, int index) {
        if (index > str.length() - 3) {
            throw new IllegalStateException("Invalid percent-encoded string:\"" + str + "\".");
        }
        char first = str.charAt(index + 1);
        char second = str.charAt(index + 2);
        char decoded = (char)(AmazonS3URI.fromHex(first) << 4 | AmazonS3URI.fromHex(second));
        builder.append(decoded);
    }

    private static int fromHex(char c) {
        if (c < '0') {
            throw new IllegalStateException("Invalid percent-encoded string: bad character '" + c + "' in escape sequence.");
        }
        if (c <= '9') {
            return c - 48;
        }
        if (c < 'A') {
            throw new IllegalStateException("Invalid percent-encoded string: bad character '" + c + "' in escape sequence.");
        }
        if (c <= 'F') {
            return c - 65 + 10;
        }
        if (c < 'a') {
            throw new IllegalStateException("Invalid percent-encoded string: bad character '" + c + "' in escape sequence.");
        }
        if (c <= 'f') {
            return c - 97 + 10;
        }
        throw new IllegalStateException("Invalid percent-encoded string: bad character '" + c + "' in escape sequence.");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AmazonS3URI that = (AmazonS3URI)o;
        if (this.isPathStyle != that.isPathStyle) {
            return false;
        }
        if (!this.uri.equals(that.uri)) {
            return false;
        }
        if (this.bucket != null ? !this.bucket.equals(that.bucket) : that.bucket != null) {
            return false;
        }
        if (this.key != null ? !this.key.equals(that.key) : that.key != null) {
            return false;
        }
        if (this.versionId != null ? !this.versionId.equals(that.versionId) : that.versionId != null) {
            return false;
        }
        return this.region != null ? this.region.equals(that.region) : that.region == null;
    }

    public int hashCode() {
        int result = this.uri.hashCode();
        result = 31 * result + (this.isPathStyle ? 1 : 0);
        result = 31 * result + (this.bucket != null ? this.bucket.hashCode() : 0);
        result = 31 * result + (this.key != null ? this.key.hashCode() : 0);
        result = 31 * result + (this.versionId != null ? this.versionId.hashCode() : 0);
        result = 31 * result + (this.region != null ? this.region.hashCode() : 0);
        return result;
    }
}

