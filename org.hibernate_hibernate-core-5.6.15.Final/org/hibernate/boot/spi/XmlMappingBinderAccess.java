/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.spi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.hibernate.boot.MappingNotFoundException;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.internal.FileXmlSource;
import org.hibernate.boot.jaxb.internal.InputStreamXmlSource;
import org.hibernate.boot.jaxb.internal.MappingBinder;
import org.hibernate.boot.jaxb.internal.UrlXmlSource;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.service.ServiceRegistry;
import org.jboss.logging.Logger;

public class XmlMappingBinderAccess {
    private static final Logger LOG = Logger.getLogger(XmlMappingBinderAccess.class);
    private final ClassLoaderService classLoaderService;
    private final MappingBinder mappingBinder;

    public XmlMappingBinderAccess(ServiceRegistry serviceRegistry) {
        this.classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        this.mappingBinder = new MappingBinder(this.classLoaderService, true);
    }

    public MappingBinder getMappingBinder() {
        return this.mappingBinder;
    }

    public Binding bind(String resource) {
        LOG.tracef("reading mappings from resource : %s", (Object)resource);
        Origin origin = new Origin(SourceType.RESOURCE, resource);
        URL url = this.classLoaderService.locateResource(resource);
        if (url == null) {
            throw new MappingNotFoundException(origin);
        }
        return new UrlXmlSource(origin, url).doBind(this.getMappingBinder());
    }

    public Binding bind(File file) {
        Origin origin = new Origin(SourceType.FILE, file.getPath());
        LOG.tracef("reading mappings from file : %s", (Object)origin.getName());
        if (!file.exists()) {
            throw new MappingNotFoundException(origin);
        }
        return new FileXmlSource(origin, file).doBind(this.getMappingBinder());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Binding bind(InputStreamAccess xmlInputStreamAccess) {
        LOG.tracef("reading mappings from InputStreamAccess : %s", (Object)xmlInputStreamAccess.getStreamName());
        Origin origin = new Origin(SourceType.INPUT_STREAM, xmlInputStreamAccess.getStreamName());
        InputStream xmlInputStream = xmlInputStreamAccess.accessInputStream();
        try {
            Binding binding = new InputStreamXmlSource(origin, xmlInputStream, false).doBind(this.mappingBinder);
            return binding;
        }
        finally {
            try {
                xmlInputStream.close();
            }
            catch (IOException e) {
                LOG.debugf("Unable to close InputStream obtained from InputStreamAccess : %s", (Object)xmlInputStreamAccess.getStreamName());
            }
        }
    }

    public Binding bind(InputStream xmlInputStream) {
        LOG.trace((Object)"reading mappings from InputStream");
        Origin origin = new Origin(SourceType.INPUT_STREAM, null);
        return new InputStreamXmlSource(origin, xmlInputStream, false).doBind(this.getMappingBinder());
    }

    public Binding bind(URL url) {
        String urlExternalForm = url.toExternalForm();
        LOG.debugf("Reading mapping document from URL : %s", (Object)urlExternalForm);
        Origin origin = new Origin(SourceType.URL, urlExternalForm);
        return new UrlXmlSource(origin, url).doBind(this.getMappingBinder());
    }
}

