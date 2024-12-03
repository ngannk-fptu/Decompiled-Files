/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.boot.model.process.internal;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.archive.internal.StandardArchiveDescriptorFactory;
import org.hibernate.boot.archive.internal.UrlInputStreamAccess;
import org.hibernate.boot.archive.scan.internal.StandardScanParameters;
import org.hibernate.boot.archive.scan.internal.StandardScanner;
import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.hibernate.boot.archive.scan.spi.MappingFileDescriptor;
import org.hibernate.boot.archive.scan.spi.PackageDescriptor;
import org.hibernate.boot.archive.scan.spi.ScanEnvironment;
import org.hibernate.boot.archive.scan.spi.ScanResult;
import org.hibernate.boot.archive.scan.spi.Scanner;
import org.hibernate.boot.archive.spi.ArchiveDescriptorFactory;
import org.hibernate.boot.internal.ClassLoaderAccessImpl;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.SourceType;
import org.hibernate.boot.model.process.internal.ManagedResourcesImpl;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.ClassLoaderAccess;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.jboss.logging.Logger;

public class ScanningCoordinator {
    private static final Logger log = Logger.getLogger(ScanningCoordinator.class);
    public static final ScanningCoordinator INSTANCE = new ScanningCoordinator();
    private static final Class[] SINGLE_ARG = new Class[]{ArchiveDescriptorFactory.class};

    private ScanningCoordinator() {
    }

    public void coordinateScan(ManagedResourcesImpl managedResources, BootstrapContext bootstrapContext, XmlMappingBinderAccess xmlMappingBinderAccess) {
        if (bootstrapContext.getScanEnvironment() == null) {
            return;
        }
        ClassLoaderService classLoaderService = bootstrapContext.getServiceRegistry().getService(ClassLoaderService.class);
        ClassLoaderAccessImpl classLoaderAccess = new ClassLoaderAccessImpl(bootstrapContext.getJpaTempClassLoader(), classLoaderService);
        Scanner scanner = ScanningCoordinator.buildScanner(bootstrapContext, classLoaderAccess);
        ScanResult scanResult = scanner.scan(bootstrapContext.getScanEnvironment(), bootstrapContext.getScanOptions(), StandardScanParameters.INSTANCE);
        this.applyScanResultsToManagedResources(managedResources, scanResult, bootstrapContext, xmlMappingBinderAccess);
    }

    private static Scanner buildScanner(BootstrapContext bootstrapContext, ClassLoaderAccess classLoaderAccess) {
        Object scannerSetting = bootstrapContext.getScanner();
        ArchiveDescriptorFactory archiveDescriptorFactory = bootstrapContext.getArchiveDescriptorFactory();
        if (scannerSetting == null) {
            if (archiveDescriptorFactory == null) {
                return new StandardScanner();
            }
            return new StandardScanner(archiveDescriptorFactory);
        }
        if (Scanner.class.isInstance(scannerSetting)) {
            if (archiveDescriptorFactory != null) {
                throw new IllegalStateException("A Scanner instance and an ArchiveDescriptorFactory were both specified; please specify one or the other, or if you need to supply both, Scanner class to use (assuming it has a constructor accepting a ArchiveDescriptorFactory).  Alternatively, just pass the ArchiveDescriptorFactory during your own Scanner constructor assuming it is statically known.");
            }
            return (Scanner)scannerSetting;
        }
        Class scannerImplClass = Class.class.isInstance(scannerSetting) ? (Class)scannerSetting : classLoaderAccess.classForName(scannerSetting.toString());
        if (archiveDescriptorFactory != null) {
            try {
                Constructor constructor = scannerImplClass.getConstructor(SINGLE_ARG);
                try {
                    return (Scanner)constructor.newInstance(archiveDescriptorFactory);
                }
                catch (Exception e) {
                    throw new IllegalStateException("Error trying to instantiate custom specified Scanner [" + scannerImplClass.getName() + "]", e);
                }
            }
            catch (NoSuchMethodException e) {
                throw new IllegalArgumentException("Configuration named a custom Scanner and a custom ArchiveDescriptorFactory, but Scanner impl did not define a constructor accepting ArchiveDescriptorFactory");
            }
        }
        try {
            Constructor constructor = scannerImplClass.getConstructor(SINGLE_ARG);
            try {
                return (Scanner)constructor.newInstance(StandardArchiveDescriptorFactory.INSTANCE);
            }
            catch (Exception e) {
                throw new IllegalStateException("Error trying to instantiate custom specified Scanner [" + scannerImplClass.getName() + "]", e);
            }
        }
        catch (NoSuchMethodException e) {
            try {
                Constructor constructor = scannerImplClass.getConstructor(new Class[0]);
                try {
                    return (Scanner)constructor.newInstance(new Object[0]);
                }
                catch (Exception e2) {
                    throw new IllegalStateException("Error trying to instantiate custom specified Scanner [" + scannerImplClass.getName() + "]", e2);
                }
            }
            catch (NoSuchMethodException ignore) {
                throw new IllegalArgumentException("Configuration named a custom Scanner, but we were unable to locate an appropriate constructor");
            }
        }
    }

    public void applyScanResultsToManagedResources(ManagedResourcesImpl managedResources, ScanResult scanResult, BootstrapContext bootstrapContext, XmlMappingBinderAccess xmlMappingBinderAccess) {
        ScanEnvironment scanEnvironment = bootstrapContext.getScanEnvironment();
        StandardServiceRegistry serviceRegistry = bootstrapContext.getServiceRegistry();
        ClassLoaderService classLoaderService = serviceRegistry.getService(ClassLoaderService.class);
        HashSet<String> nonLocatedMappingFileNames = new HashSet<String>();
        List<String> explicitMappingFileNames = scanEnvironment.getExplicitlyListedMappingFiles();
        if (explicitMappingFileNames != null) {
            nonLocatedMappingFileNames.addAll(explicitMappingFileNames);
        }
        if (xmlMappingBinderAccess != null) {
            for (MappingFileDescriptor mappingFileDescriptor : scanResult.getLocatedMappingFiles()) {
                managedResources.addXmlBinding(xmlMappingBinderAccess.bind(mappingFileDescriptor.getStreamAccess()));
                nonLocatedMappingFileNames.remove(mappingFileDescriptor.getName());
            }
            for (String name : nonLocatedMappingFileNames) {
                URL url = classLoaderService.locateResource(name);
                if (url == null) {
                    throw new MappingException("Unable to resolve explicitly named mapping-file : " + name, new Origin(SourceType.RESOURCE, name));
                }
                UrlInputStreamAccess inputStreamAccess = new UrlInputStreamAccess(url);
                managedResources.addXmlBinding(xmlMappingBinderAccess.bind(inputStreamAccess));
            }
        }
        ArrayList unresolvedListedClassNames = scanEnvironment.getExplicitlyListedClassNames() == null ? new ArrayList() : new ArrayList<String>(scanEnvironment.getExplicitlyListedClassNames());
        for (ClassDescriptor classDescriptor : scanResult.getLocatedClasses()) {
            if (classDescriptor.getCategorization() == ClassDescriptor.Categorization.CONVERTER) {
                managedResources.addAttributeConverterDefinition(AttributeConverterDefinition.from(classLoaderService.classForName(classDescriptor.getName())));
            } else if (classDescriptor.getCategorization() == ClassDescriptor.Categorization.MODEL) {
                managedResources.addAnnotatedClassName(classDescriptor.getName());
            }
            unresolvedListedClassNames.remove(classDescriptor.getName());
        }
        for (PackageDescriptor packageDescriptor : scanResult.getLocatedPackages()) {
            managedResources.addAnnotatedPackageName(packageDescriptor.getName());
            unresolvedListedClassNames.remove(packageDescriptor.getName());
        }
        for (String unresolvedListedClassName : unresolvedListedClassNames) {
            String classFileName = unresolvedListedClassName.replace('.', '/') + ".class";
            URL classFileUrl = classLoaderService.locateResource(classFileName);
            if (classFileUrl != null) {
                managedResources.addAnnotatedClassName(unresolvedListedClassName);
                continue;
            }
            String packageInfoFileName = unresolvedListedClassName.replace('.', '/') + "/package-info.class";
            URL packageInfoFileUrl = classLoaderService.locateResource(packageInfoFileName);
            if (packageInfoFileUrl != null) {
                managedResources.addAnnotatedPackageName(unresolvedListedClassName);
                continue;
            }
            try {
                Class clazz = classLoaderService.classForName(unresolvedListedClassName);
                managedResources.addAnnotatedClassReference(clazz);
            }
            catch (ClassLoadingException classLoadingException) {
                log.debugf("Unable to resolve class [%s] named in persistence unit [%s]", (Object)unresolvedListedClassName, (Object)scanEnvironment.getRootUrl());
            }
        }
    }
}

