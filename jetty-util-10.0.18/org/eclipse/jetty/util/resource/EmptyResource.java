/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import org.eclipse.jetty.util.resource.Resource;

public class EmptyResource
extends Resource {
    public static final Resource INSTANCE = new EmptyResource();

    private EmptyResource() {
    }

    @Override
    public boolean isContainedIn(Resource r) throws MalformedURLException {
        return false;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean exists() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    @Override
    public long length() {
        return 0L;
    }

    @Override
    public URI getURI() {
        return null;
    }

    @Override
    public File getFile() throws IOException {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
    }

    @Override
    public boolean delete() throws SecurityException {
        return false;
    }

    @Override
    public boolean renameTo(Resource dest) throws SecurityException {
        return false;
    }

    @Override
    public String[] list() {
        return null;
    }

    @Override
    public Resource addPath(String path) throws IOException, MalformedURLException {
        return this;
    }
}

