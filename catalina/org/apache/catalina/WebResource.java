/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina;

import java.io.InputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.jar.Manifest;
import org.apache.catalina.WebResourceRoot;

public interface WebResource {
    public long getLastModified();

    public String getLastModifiedHttp();

    public boolean exists();

    public boolean isVirtual();

    public boolean isDirectory();

    public boolean isFile();

    public boolean delete();

    public String getName();

    public long getContentLength();

    public String getCanonicalPath();

    public boolean canRead();

    public String getWebappPath();

    public String getETag();

    public void setMimeType(String var1);

    public String getMimeType();

    public InputStream getInputStream();

    public byte[] getContent();

    public long getCreation();

    public URL getURL();

    public URL getCodeBase();

    public WebResourceRoot getWebResourceRoot();

    public Certificate[] getCertificates();

    public Manifest getManifest();
}

