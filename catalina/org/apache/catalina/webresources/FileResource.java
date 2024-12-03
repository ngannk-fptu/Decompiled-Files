/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.webresources;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.AbstractResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class FileResource
extends AbstractResource {
    private static final Log log = LogFactory.getLog(FileResource.class);
    private static final boolean PROPERTIES_NEED_CONVERT;
    private final File resource;
    private final String name;
    private final boolean readOnly;
    private final Manifest manifest;
    private final boolean needConvert;

    public FileResource(WebResourceRoot root, String webAppPath, File resource, boolean readOnly, Manifest manifest) {
        super(root, webAppPath);
        this.resource = resource;
        if (webAppPath.charAt(webAppPath.length() - 1) == '/') {
            String realName = resource.getName() + '/';
            if (webAppPath.endsWith(realName)) {
                this.name = resource.getName();
            } else {
                int endOfName = webAppPath.length() - 1;
                this.name = webAppPath.substring(webAppPath.lastIndexOf(47, endOfName - 1) + 1, endOfName);
            }
        } else {
            this.name = resource.getName();
        }
        this.readOnly = readOnly;
        this.manifest = manifest;
        this.needConvert = PROPERTIES_NEED_CONVERT && this.name.endsWith(".properties");
    }

    @Override
    public long getLastModified() {
        return this.resource.lastModified();
    }

    @Override
    public boolean exists() {
        return this.resource.exists();
    }

    @Override
    public boolean isVirtual() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return this.resource.isDirectory();
    }

    @Override
    public boolean isFile() {
        return this.resource.isFile();
    }

    @Override
    public boolean delete() {
        if (this.readOnly) {
            return false;
        }
        return this.resource.delete();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getContentLength() {
        return this.getContentLengthInternal(this.needConvert);
    }

    private long getContentLengthInternal(boolean convert) {
        if (convert) {
            byte[] content = this.getContent();
            if (content == null) {
                return -1L;
            }
            return content.length;
        }
        if (this.isDirectory()) {
            return -1L;
        }
        return this.resource.length();
    }

    @Override
    public String getCanonicalPath() {
        try {
            return this.resource.getCanonicalPath();
        }
        catch (IOException ioe) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("fileResource.getCanonicalPathFail", new Object[]{this.resource.getPath()}), (Throwable)ioe);
            }
            return null;
        }
    }

    @Override
    public boolean canRead() {
        return this.resource.canRead();
    }

    @Override
    protected InputStream doGetInputStream() {
        if (this.needConvert) {
            byte[] content = this.getContent();
            if (content == null) {
                return null;
            }
            return new ByteArrayInputStream(content);
        }
        try {
            return new FileInputStream(this.resource);
        }
        catch (FileNotFoundException fnfe) {
            return null;
        }
    }

    @Override
    public final byte[] getContent() {
        long len = this.getContentLengthInternal(false);
        if (len > Integer.MAX_VALUE) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("abstractResource.getContentTooLarge", new Object[]{this.getWebappPath(), len}));
        }
        if (len < 0L) {
            return null;
        }
        int size = (int)len;
        byte[] result = new byte[size];
        try (FileInputStream is = new FileInputStream(this.resource);){
            int n;
            for (int pos = 0; pos < size; pos += n) {
                n = ((InputStream)is).read(result, pos, size - pos);
                if (n >= 0) continue;
                break;
            }
        }
        catch (IOException ioe) {
            if (this.getLog().isDebugEnabled()) {
                this.getLog().debug((Object)sm.getString("abstractResource.getContentFail", new Object[]{this.getWebappPath()}), (Throwable)ioe);
            }
            return null;
        }
        if (this.needConvert) {
            String str = new String(result);
            try {
                result = str.getBytes(StandardCharsets.UTF_8);
            }
            catch (Exception e) {
                result = null;
            }
        }
        return result;
    }

    @Override
    public long getCreation() {
        try {
            BasicFileAttributes attrs = Files.readAttributes(this.resource.toPath(), BasicFileAttributes.class, new LinkOption[0]);
            return attrs.creationTime().toMillis();
        }
        catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("fileResource.getCreationFail", new Object[]{this.resource.getPath()}), (Throwable)e);
            }
            return 0L;
        }
    }

    @Override
    public URL getURL() {
        if (this.resource.exists()) {
            try {
                return this.resource.toURI().toURL();
            }
            catch (MalformedURLException e) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)sm.getString("fileResource.getUrlFail", new Object[]{this.resource.getPath()}), (Throwable)e);
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public URL getCodeBase() {
        if (this.getWebappPath().startsWith("/WEB-INF/classes/") && this.name.endsWith(".class")) {
            return this.getWebResourceRoot().getResource("/WEB-INF/classes/").getURL();
        }
        return this.getURL();
    }

    @Override
    public Certificate[] getCertificates() {
        return null;
    }

    @Override
    public Manifest getManifest() {
        return this.manifest;
    }

    @Override
    protected Log getLog() {
        return log;
    }

    static {
        boolean isEBCDIC = false;
        try {
            String encoding = Charset.defaultCharset().displayName();
            if (encoding.contains("EBCDIC")) {
                isEBCDIC = true;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        PROPERTIES_NEED_CONVERT = isEBCDIC;
    }
}

