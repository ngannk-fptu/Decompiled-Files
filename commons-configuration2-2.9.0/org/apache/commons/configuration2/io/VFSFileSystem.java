/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.commons.vfs2.FileContent
 *  org.apache.commons.vfs2.FileName
 *  org.apache.commons.vfs2.FileObject
 *  org.apache.commons.vfs2.FileSystemConfigBuilder
 *  org.apache.commons.vfs2.FileSystemException
 *  org.apache.commons.vfs2.FileSystemManager
 *  org.apache.commons.vfs2.FileSystemOptions
 *  org.apache.commons.vfs2.VFS
 *  org.apache.commons.vfs2.provider.UriParser
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.io.DefaultFileSystem;
import org.apache.commons.configuration2.io.FileOptionsProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.UriParser;

public class VFSFileSystem
extends DefaultFileSystem {
    private final Log log = LogFactory.getLog(this.getClass());

    @Override
    public String getBasePath(String path) {
        if (UriParser.extractScheme((String)path) == null) {
            return super.getBasePath(path);
        }
        try {
            FileName parent = this.resolveURI(path).getParent();
            return parent != null ? parent.getURI() : null;
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }

    @Override
    public String getFileName(String path) {
        if (UriParser.extractScheme((String)path) == null) {
            return super.getFileName(path);
        }
        try {
            return this.resolveURI(path).getBaseName();
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }

    @Override
    public InputStream getInputStream(URL url) throws ConfigurationException {
        try {
            FileSystemOptions opts = this.getOptions(url.getProtocol());
            FileObject file = this.getManager().resolveFile(url.toString(), opts);
            if (!file.exists()) {
                throw new ConfigurationException("File not found");
            }
            if (!file.isFile()) {
                throw new ConfigurationException("Cannot load a configuration from a directory");
            }
            FileContent content = file.getContent();
            if (content == null) {
                String msg = "Cannot access content of " + file.getName().getFriendlyURI();
                throw new ConfigurationException(msg);
            }
            return content.getInputStream();
        }
        catch (FileSystemException fse) {
            String msg = "Unable to access " + url.toString();
            throw new ConfigurationException(msg, fse);
        }
    }

    private FileSystemManager getManager() throws FileSystemException {
        return VFS.getManager();
    }

    private FileSystemOptions getOptions(String scheme) {
        FileSystemConfigBuilder builder;
        if (scheme == null) {
            return null;
        }
        FileSystemOptions opts = new FileSystemOptions();
        try {
            builder = this.getManager().getFileSystemConfigBuilder(scheme);
        }
        catch (Exception ex) {
            return null;
        }
        FileOptionsProvider provider = this.getFileOptionsProvider();
        if (provider != null) {
            Map<String, Object> map = provider.getOptions();
            if (map == null) {
                return null;
            }
            int count = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                try {
                    String key = entry.getKey();
                    if ("currentUser".equals(key)) {
                        key = "creatorName";
                    }
                    this.setProperty(builder, opts, key, entry.getValue());
                    ++count;
                }
                catch (Exception ex) {}
            }
            if (count > 0) {
                return opts;
            }
        }
        return null;
    }

    @Override
    public OutputStream getOutputStream(URL url) throws ConfigurationException {
        try {
            FileSystemOptions opts = this.getOptions(url.getProtocol());
            FileObject file = this.getManager().resolveFile(url.toString(), opts);
            if (file == null || file.isFolder()) {
                throw new ConfigurationException("Cannot save a configuration to a directory");
            }
            FileContent content = file.getContent();
            if (content == null) {
                throw new ConfigurationException("Cannot access content of " + url);
            }
            return content.getOutputStream();
        }
        catch (FileSystemException fse) {
            throw new ConfigurationException("Unable to access " + url, fse);
        }
    }

    @Override
    public String getPath(File file, URL url, String basePath, String fileName) {
        if (file != null) {
            return super.getPath(file, url, basePath, fileName);
        }
        try {
            FileName name;
            if (url != null && (name = this.resolveURI(url.toString())) != null) {
                return name.toString();
            }
            if (UriParser.extractScheme((String)fileName) != null) {
                return fileName;
            }
            if (basePath != null) {
                FileName base = this.resolveURI(basePath);
                return this.getManager().resolveName(base, fileName).getURI();
            }
            name = this.resolveURI(fileName);
            FileName base = name.getParent();
            return this.getManager().resolveName(base, name.getBaseName()).getURI();
        }
        catch (FileSystemException fse) {
            fse.printStackTrace();
            return null;
        }
    }

    @Override
    public URL getURL(String basePath, String file) throws MalformedURLException {
        if (basePath != null && UriParser.extractScheme((String)basePath) == null || basePath == null && UriParser.extractScheme((String)file) == null) {
            return super.getURL(basePath, file);
        }
        try {
            FileName path;
            if (basePath != null && UriParser.extractScheme((String)file) == null) {
                FileName base = this.resolveURI(basePath);
                path = this.getManager().resolveName(base, file);
            } else {
                path = this.resolveURI(file);
            }
            VFSURLStreamHandler handler = new VFSURLStreamHandler(path);
            return new URL(null, path.getURI(), handler);
        }
        catch (FileSystemException fse) {
            throw new ConfigurationRuntimeException("Could not parse basePath: " + basePath + " and fileName: " + file, fse);
        }
    }

    @Override
    public URL locateFromURL(String basePath, String fileName) {
        String fileScheme = UriParser.extractScheme((String)fileName);
        if ((basePath == null || UriParser.extractScheme((String)basePath) == null) && fileScheme == null) {
            return super.locateFromURL(basePath, fileName);
        }
        try {
            FileObject file;
            if (basePath != null && fileScheme == null) {
                String scheme = UriParser.extractScheme((String)basePath);
                FileSystemOptions opts = this.getOptions(scheme);
                FileObject base = this.getManager().resolveFile(basePath, opts);
                if (base.isFile()) {
                    base = base.getParent();
                }
                file = this.getManager().resolveFile(base, fileName);
            } else {
                FileSystemOptions opts = this.getOptions(fileScheme);
                file = this.getManager().resolveFile(fileName, opts);
            }
            if (!file.exists()) {
                return null;
            }
            FileName path = file.getName();
            VFSURLStreamHandler handler = new VFSURLStreamHandler(path);
            return new URL(null, path.getURI(), handler);
        }
        catch (MalformedURLException | FileSystemException fse) {
            return null;
        }
    }

    private FileName resolveURI(String path) throws FileSystemException {
        return this.getManager().resolveURI(path);
    }

    private void setProperty(FileSystemConfigBuilder builder, FileSystemOptions options, String key, Object value) {
        String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
        Class[] paramTypes = new Class[]{FileSystemOptions.class, value.getClass()};
        try {
            Method method = builder.getClass().getMethod(methodName, paramTypes);
            Object[] params = new Object[]{options, value};
            method.invoke((Object)builder, params);
        }
        catch (Exception ex) {
            this.log.warn((Object)("Cannot access property '" + key + "'! Ignoring."), (Throwable)ex);
        }
    }

    private static class VFSURLStreamHandler
    extends URLStreamHandler {
        private final String protocol;

        public VFSURLStreamHandler(FileName file) {
            this.protocol = file.getScheme();
        }

        @Override
        protected URLConnection openConnection(URL url) throws IOException {
            throw new IOException("VFS URLs can only be used with VFS APIs");
        }
    }
}

