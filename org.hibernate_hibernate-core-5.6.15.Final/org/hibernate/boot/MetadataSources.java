/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import javax.xml.transform.dom.DOMSource;
import org.hibernate.HibernateException;
import org.hibernate.boot.MappingNotFoundException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.archive.spi.InputStreamAccess;
import org.hibernate.boot.internal.MetadataBuilderImpl;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.jaxb.internal.CacheableFileXmlSource;
import org.hibernate.boot.jaxb.internal.JarFileEntryXmlSource;
import org.hibernate.boot.jaxb.internal.JaxpSourceXmlSource;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataBuilderFactory;
import org.hibernate.boot.spi.MetadataBuilderImplementor;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.SerializationException;
import org.w3c.dom.Document;

public class MetadataSources
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(MetadataSources.class);
    private final ServiceRegistry serviceRegistry;
    private final boolean disableXmlMappingBinders;
    private XmlMappingBinderAccess xmlMappingBinderAccess;
    private List<Binding> xmlBindings;
    private LinkedHashSet<Class<?>> annotatedClasses;
    private LinkedHashSet<String> annotatedClassNames;
    private LinkedHashSet<String> annotatedPackages;

    public MetadataSources() {
        this(new BootstrapServiceRegistryBuilder().build());
    }

    public MetadataSources(ServiceRegistry serviceRegistry) {
        if (!MetadataSources.isExpectedServiceRegistryType(serviceRegistry) && LOG.isDebugEnabled()) {
            LOG.debugf("Unexpected ServiceRegistry type [%s] encountered during building of MetadataSources; may cause problems later attempting to construct MetadataBuilder", serviceRegistry.getClass().getName());
        }
        this.serviceRegistry = serviceRegistry;
        this.disableXmlMappingBinders = false;
    }

    public MetadataSources(ServiceRegistry serviceRegistry, boolean disableXmlMappingBinders) {
        Objects.requireNonNull(serviceRegistry);
        this.serviceRegistry = serviceRegistry;
        this.disableXmlMappingBinders = disableXmlMappingBinders;
    }

    protected static boolean isExpectedServiceRegistryType(ServiceRegistry serviceRegistry) {
        return BootstrapServiceRegistry.class.isInstance(serviceRegistry) || StandardServiceRegistry.class.isInstance(serviceRegistry);
    }

    public XmlMappingBinderAccess getXmlMappingBinderAccess() {
        if (this.disableXmlMappingBinders) {
            return null;
        }
        if (this.xmlMappingBinderAccess == null) {
            this.xmlMappingBinderAccess = new XmlMappingBinderAccess(this.serviceRegistry);
        }
        return this.xmlMappingBinderAccess;
    }

    public List<Binding> getXmlBindings() {
        return this.xmlBindings == null ? Collections.emptyList() : this.xmlBindings;
    }

    public Collection<String> getAnnotatedPackages() {
        return this.annotatedPackages == null ? Collections.emptySet() : this.annotatedPackages;
    }

    public Collection<Class<?>> getAnnotatedClasses() {
        return this.annotatedClasses == null ? Collections.emptySet() : this.annotatedClasses;
    }

    public Collection<String> getAnnotatedClassNames() {
        return this.annotatedClassNames == null ? Collections.emptySet() : this.annotatedClassNames;
    }

    public ServiceRegistry getServiceRegistry() {
        return this.serviceRegistry;
    }

    public MetadataBuilder getMetadataBuilder() {
        MetadataBuilderImpl defaultBuilder = new MetadataBuilderImpl(this);
        return this.getCustomBuilderOrDefault(defaultBuilder);
    }

    @Deprecated
    public MetadataBuilder getMetadataBuilder(StandardServiceRegistry serviceRegistry) {
        MetadataBuilderImpl defaultBuilder = new MetadataBuilderImpl(this, serviceRegistry);
        return this.getCustomBuilderOrDefault(defaultBuilder);
    }

    private MetadataBuilder getCustomBuilderOrDefault(MetadataBuilderImpl defaultBuilder) {
        ClassLoaderService cls = this.serviceRegistry.getService(ClassLoaderService.class);
        Collection<MetadataBuilderFactory> discoveredBuilderFactories = cls.loadJavaServices(MetadataBuilderFactory.class);
        MetadataBuilderImplementor builder = null;
        ArrayList<String> activeFactoryNames = null;
        for (MetadataBuilderFactory discoveredBuilderFactory : discoveredBuilderFactories) {
            MetadataBuilderImplementor returnedBuilder = discoveredBuilderFactory.getMetadataBuilder(this, defaultBuilder);
            if (returnedBuilder == null) continue;
            if (activeFactoryNames == null) {
                activeFactoryNames = new ArrayList<String>();
            }
            activeFactoryNames.add(discoveredBuilderFactory.getClass().getName());
            builder = returnedBuilder;
        }
        if (activeFactoryNames != null && activeFactoryNames.size() > 1) {
            throw new HibernateException("Multiple active MetadataBuilder definitions were discovered : " + String.join((CharSequence)", ", (Iterable<? extends CharSequence>)activeFactoryNames));
        }
        return builder != null ? builder : defaultBuilder;
    }

    public Metadata buildMetadata() {
        return this.getMetadataBuilder().build();
    }

    public Metadata buildMetadata(StandardServiceRegistry serviceRegistry) {
        return this.getMetadataBuilder(serviceRegistry).build();
    }

    public MetadataSources addAnnotatedClass(Class annotatedClass) {
        if (this.annotatedClasses == null) {
            this.annotatedClasses = new LinkedHashSet();
        }
        this.annotatedClasses.add(annotatedClass);
        return this;
    }

    public MetadataSources addAnnotatedClassName(String annotatedClassName) {
        if (this.annotatedClassNames == null) {
            this.annotatedClassNames = new LinkedHashSet();
        }
        this.annotatedClassNames.add(annotatedClassName);
        return this;
    }

    public MetadataSources addPackage(String packageName) {
        if (packageName == null) {
            throw new IllegalArgumentException("The specified package name cannot be null");
        }
        if (packageName.endsWith(".")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        this.addPackageInternal(packageName);
        return this;
    }

    private void addPackageInternal(String packageName) {
        if (this.annotatedPackages == null) {
            this.annotatedPackages = new LinkedHashSet();
        }
        this.annotatedPackages.add(packageName);
    }

    public MetadataSources addPackage(Package packageRef) {
        this.addPackageInternal(packageRef.getName());
        return this;
    }

    @Deprecated
    public MetadataSources addClass(Class entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("The specified class cannot be null");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("adding resource mappings from class convention : %s", entityClass.getName());
        }
        String mappingResourceName = entityClass.getName().replace('.', '/') + ".hbm.xml";
        this.addResource(mappingResourceName);
        return this;
    }

    public MetadataSources addResource(String name) {
        this.getXmlBindingsForWrite().add(this.getXmlMappingBinderAccess().bind(name));
        return this;
    }

    public MetadataSources addFile(String path) {
        this.addFile(new File(path));
        return this;
    }

    public MetadataSources addFile(File file) {
        this.getXmlBindingsForWrite().add(this.getXmlMappingBinderAccess().bind(file));
        return this;
    }

    public MetadataSources addXmlBinding(Binding<?> binding) {
        this.getXmlBindingsForWrite().add(binding);
        return this;
    }

    public MetadataSources addCacheableFile(String path) {
        Origin origin = new Origin(SourceType.FILE, path);
        this.addCacheableFile(origin, new File(path));
        return this;
    }

    private void addCacheableFile(Origin origin, File file) {
        this.getXmlBindingsForWrite().add(new CacheableFileXmlSource(origin, file, false).doBind(this.getXmlMappingBinderAccess().getMappingBinder()));
    }

    public MetadataSources addCacheableFile(File file) {
        Origin origin = new Origin(SourceType.FILE, file.getName());
        this.addCacheableFile(origin, file);
        return this;
    }

    public MetadataSources addCacheableFileStrictly(File file) throws SerializationException, FileNotFoundException {
        Origin origin = new Origin(SourceType.FILE, file.getAbsolutePath());
        this.getXmlBindingsForWrite().add(new CacheableFileXmlSource(origin, file, true).doBind(this.getXmlMappingBinderAccess().getMappingBinder()));
        return this;
    }

    public MetadataSources addInputStream(InputStreamAccess xmlInputStreamAccess) {
        this.getXmlBindingsForWrite().add(this.getXmlMappingBinderAccess().bind(xmlInputStreamAccess));
        return this;
    }

    public MetadataSources addInputStream(InputStream xmlInputStream) {
        this.getXmlBindingsForWrite().add(this.getXmlMappingBinderAccess().bind(xmlInputStream));
        return this;
    }

    public MetadataSources addURL(URL url) {
        this.getXmlBindingsForWrite().add(this.getXmlMappingBinderAccess().bind(url));
        return this;
    }

    @Deprecated
    public MetadataSources addDocument(Document document) {
        Origin origin = new Origin(SourceType.DOM, "<unknown>");
        this.getXmlBindingsForWrite().add(new JaxpSourceXmlSource(origin, new DOMSource(document)).doBind(this.getXmlMappingBinderAccess().getMappingBinder()));
        return this;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public MetadataSources addJar(File jar) {
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Seeking mapping documents in jar file : %s", jar.getName());
        }
        Origin origin = new Origin(SourceType.JAR, jar.getAbsolutePath());
        try {
            JarFile jarFile = new JarFile(jar);
            boolean TRACE = LOG.isTraceEnabled();
            try {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    ZipEntry zipEntry = jarEntries.nextElement();
                    if (!zipEntry.getName().endsWith(".hbm.xml")) continue;
                    if (TRACE) {
                        LOG.tracef("found mapping document : %s", zipEntry.getName());
                    }
                    this.getXmlBindingsForWrite().add(new JarFileEntryXmlSource(origin, jarFile, zipEntry).doBind(this.getXmlMappingBinderAccess().getMappingBinder()));
                }
            }
            finally {
                try {
                    jarFile.close();
                }
                catch (Exception exception) {}
            }
        }
        catch (IOException e) {
            throw new MappingNotFoundException(e, origin);
        }
        return this;
    }

    private <Binding> List getXmlBindingsForWrite() {
        if (this.xmlBindings == null) {
            this.xmlBindings = new ArrayList<Binding>();
        }
        return this.xmlBindings;
    }

    public MetadataSources addDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isDirectory()) {
                    this.addDirectory(file);
                    continue;
                }
                if (!file.getName().endsWith(".hbm.xml")) continue;
                this.addFile(file);
            }
        }
        return this;
    }
}

