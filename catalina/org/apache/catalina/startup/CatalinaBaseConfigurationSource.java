/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UriUtil
 *  org.apache.tomcat.util.file.ConfigurationSource
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import org.apache.tomcat.util.buf.UriUtil;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

public class CatalinaBaseConfigurationSource
implements ConfigurationSource {
    protected static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.startup");
    private final String serverXmlPath;
    private final File catalinaBaseFile;
    private final URI catalinaBaseUri;

    public CatalinaBaseConfigurationSource(File catalinaBaseFile, String serverXmlPath) {
        this.catalinaBaseFile = catalinaBaseFile;
        this.catalinaBaseUri = catalinaBaseFile.toURI();
        this.serverXmlPath = serverXmlPath;
    }

    public ConfigurationSource.Resource getServerXml() throws IOException {
        InputStream stream;
        IOException ioe = null;
        ConfigurationSource.Resource result = null;
        try {
            result = this.serverXmlPath == null || this.serverXmlPath.equals("conf/server.xml") ? super.getServerXml() : this.getResource(this.serverXmlPath);
        }
        catch (IOException e) {
            ioe = e;
        }
        if (result == null && (stream = this.getClass().getClassLoader().getResourceAsStream("server-embed.xml")) != null) {
            try {
                result = new ConfigurationSource.Resource(stream, this.getClass().getClassLoader().getResource("server-embed.xml").toURI());
            }
            catch (URISyntaxException e) {
                stream.close();
            }
        }
        if (result == null && ioe != null) {
            throw ioe;
        }
        return result;
    }

    public ConfigurationSource.Resource getResource(String name) throws IOException {
        if (!UriUtil.isAbsoluteURI((String)name)) {
            File f = new File(name);
            if (!f.isAbsolute()) {
                f = new File(this.catalinaBaseFile, name);
            }
            if (f.isFile()) {
                FileInputStream fis = new FileInputStream(f);
                return new ConfigurationSource.Resource((InputStream)fis, f.toURI());
            }
            InputStream stream = null;
            try {
                stream = this.getClass().getClassLoader().getResourceAsStream(name);
                if (stream != null) {
                    return new ConfigurationSource.Resource(stream, this.getClass().getClassLoader().getResource(name).toURI());
                }
            }
            catch (URISyntaxException e) {
                stream.close();
                throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", new Object[]{name}), e);
            }
        }
        URI uri = null;
        try {
            uri = this.getURIInternal(name);
        }
        catch (IllegalArgumentException e) {
            throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", new Object[]{name}));
        }
        try {
            URL url = uri.toURL();
            return new ConfigurationSource.Resource(url.openConnection().getInputStream(), uri);
        }
        catch (MalformedURLException e) {
            throw new IOException(sm.getString("catalinaConfigurationSource.cannotObtainURL", new Object[]{name}), e);
        }
    }

    public URI getURI(String name) {
        if (!UriUtil.isAbsoluteURI((String)name)) {
            File f = new File(name);
            if (!f.isAbsolute()) {
                f = new File(this.catalinaBaseFile, name);
            }
            if (f.isFile()) {
                return f.toURI();
            }
            try {
                URL resource = this.getClass().getClassLoader().getResource(name);
                if (resource != null) {
                    return resource.toURI();
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.getURIInternal(name);
    }

    private URI getURIInternal(String name) {
        URI uri = this.catalinaBaseUri != null ? this.catalinaBaseUri.resolve(name) : URI.create(name);
        return uri;
    }
}

