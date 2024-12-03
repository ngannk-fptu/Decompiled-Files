/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

public class UrlResource
extends AbstractFileResolvingResource {
    @Nullable
    private final URI uri;
    private final URL url;
    private final URL cleanedUrl;

    public UrlResource(URI uri) throws MalformedURLException {
        Assert.notNull((Object)uri, "URI must not be null");
        this.uri = uri;
        this.url = uri.toURL();
        this.cleanedUrl = this.getCleanedUrl(this.url, uri.toString());
    }

    public UrlResource(URL url) {
        Assert.notNull((Object)url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = this.getCleanedUrl(this.url, url.toString());
        this.uri = null;
    }

    public UrlResource(String path) throws MalformedURLException {
        Assert.notNull((Object)path, "Path must not be null");
        this.uri = null;
        this.url = new URL(path);
        this.cleanedUrl = this.getCleanedUrl(this.url, path);
    }

    public UrlResource(String protocol, String location) throws MalformedURLException {
        this(protocol, location, null);
    }

    public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
        try {
            this.uri = new URI(protocol, location, fragment);
            this.url = this.uri.toURL();
            this.cleanedUrl = this.getCleanedUrl(this.url, this.uri.toString());
        }
        catch (URISyntaxException ex) {
            MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
            exToThrow.initCause(ex);
            throw exToThrow;
        }
    }

    private URL getCleanedUrl(URL originalUrl, String originalPath) {
        try {
            return new URL(StringUtils.cleanPath(originalPath));
        }
        catch (MalformedURLException ex) {
            return originalUrl;
        }
    }

    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        ResourceUtils.useCachesIfNecessary(con);
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection)con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public URL getURL() {
        return this.url;
    }

    @Override
    public URI getURI() throws IOException {
        if (this.uri != null) {
            return this.uri;
        }
        return super.getURI();
    }

    @Override
    public boolean isFile() {
        if (this.uri != null) {
            return super.isFile(this.uri);
        }
        return super.isFile();
    }

    @Override
    public File getFile() throws IOException {
        if (this.uri != null) {
            return super.getFile(this.uri);
        }
        return super.getFile();
    }

    @Override
    public Resource createRelative(String relativePath) throws MalformedURLException {
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return new UrlResource(new URL(this.url, relativePath));
    }

    @Override
    public String getFilename() {
        return StringUtils.getFilename(this.cleanedUrl.getPath());
    }

    @Override
    public String getDescription() {
        return "URL [" + this.url + "]";
    }

    @Override
    public boolean equals(Object other) {
        return this == other || other instanceof UrlResource && this.cleanedUrl.equals(((UrlResource)other).cleanedUrl);
    }

    @Override
    public int hashCode() {
        return this.cleanedUrl.hashCode();
    }
}

