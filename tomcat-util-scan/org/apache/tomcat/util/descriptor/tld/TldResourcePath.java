/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 */
package org.apache.tomcat.util.descriptor.tld;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.scan.JarFactory;
import org.apache.tomcat.util.scan.ReferenceCountedJar;

public class TldResourcePath {
    private final URL url;
    private final String webappPath;
    private final String entryName;

    public TldResourcePath(URL url, String webappPath) {
        this(url, webappPath, null);
    }

    public TldResourcePath(URL url, String webappPath, String entryName) {
        this.url = url;
        this.webappPath = webappPath;
        this.entryName = entryName;
    }

    public URL getUrl() {
        return this.url;
    }

    public String getWebappPath() {
        return this.webappPath;
    }

    public String getEntryName() {
        return this.entryName;
    }

    public String toExternalForm() {
        if (this.entryName == null) {
            return this.url.toExternalForm();
        }
        return "jar:" + this.url.toExternalForm() + "!/" + this.entryName;
    }

    public InputStream openStream() throws IOException {
        if (this.entryName == null) {
            return this.url.openStream();
        }
        URL entryUrl = JarFactory.getJarEntryURL(this.url, this.entryName);
        return entryUrl.openStream();
    }

    public Jar openJar() throws IOException {
        if (this.entryName == null) {
            return null;
        }
        return new ReferenceCountedJar(this.url);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        TldResourcePath other = (TldResourcePath)o;
        return this.url.equals(other.url) && Objects.equals(this.webappPath, other.webappPath) && Objects.equals(this.entryName, other.entryName);
    }

    public int hashCode() {
        int result = this.url.hashCode();
        result = result * 31 + Objects.hashCode(this.webappPath);
        result = result * 31 + Objects.hashCode(this.entryName);
        return result;
    }
}

