/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.CannotCheckResource;
import com.atlassian.modzdetector.DefaultStreamMapper;
import com.atlassian.modzdetector.HashAlgorithm;
import com.atlassian.modzdetector.IOUtils;
import com.atlassian.modzdetector.MD5HashAlgorithm;
import com.atlassian.modzdetector.Modifications;
import com.atlassian.modzdetector.ModzRegistryException;
import com.atlassian.modzdetector.ResourceAccessor;
import com.atlassian.modzdetector.StreamMapper;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ModzDetector {
    private static final Logger log = LoggerFactory.getLogger(ModzDetector.class);
    private ResourceAccessor resourceAccessor;
    private HashAlgorithm hashAlgorithm;
    private StreamMapper streamMapper;
    public static final FileFilter OPEN_FILTER = new FileFilter(){

        public boolean accept(File file) {
            return true;
        }
    };

    public ModzDetector(ResourceAccessor resourceAccessor) {
        this(resourceAccessor, new MD5HashAlgorithm(), new DefaultStreamMapper(resourceAccessor));
    }

    public ModzDetector(ResourceAccessor resourceAccessor, HashAlgorithm hashAlgorithm) {
        this(resourceAccessor, hashAlgorithm, new DefaultStreamMapper(resourceAccessor));
    }

    public ModzDetector(ResourceAccessor resourceAccessor, HashAlgorithm hashAlgorithm, StreamMapper streamMapper) {
        this.resourceAccessor = resourceAccessor;
        this.hashAlgorithm = hashAlgorithm;
        this.streamMapper = streamMapper;
    }

    public Modifications getModifiedFiles() throws ModzRegistryException {
        InputStream registryStream = this.getDefaultRegistryStream();
        return this.getModifiedFiles(registryStream);
    }

    public Modifications getModifiedFiles(InputStream registryStream) throws ModzRegistryException {
        if (registryStream == null) {
            throw new ModzRegistryException("No registry provided.");
        }
        try {
            Properties registry = new Properties();
            registry.load(registryStream);
            Modifications mods = new Modifications();
            long start = System.currentTimeMillis();
            this.checkRegistry(mods, registry);
            log.info("Time taken (ms) to check registry: " + (System.currentTimeMillis() - start));
            Modifications modifications = mods;
            return modifications;
        }
        catch (IOException e) {
            throw new ModzRegistryException("Unable to load hash registry: ", e);
        }
        finally {
            IOUtils.closeQuietly(registryStream);
        }
    }

    public List<String> getAddedFiles(File rootDirectory) throws ModzRegistryException {
        InputStream registryStream = this.getDefaultRegistryStream();
        return this.getAddedFiles(registryStream, rootDirectory, OPEN_FILTER);
    }

    private InputStream getDefaultRegistryStream() {
        if (log.isDebugEnabled()) {
            log.debug("registry loading from resource hash-registry.properties using provider " + this.resourceAccessor.getClass().getName());
        }
        return this.resourceAccessor.getResourceFromClasspath("hash-registry.properties");
    }

    public List<String> getAddedFiles(InputStream registryStream, File root, FileFilter filter) throws ModzRegistryException {
        if (!root.canRead() || !root.isDirectory()) {
            throw new IllegalArgumentException("root is not a readable directory: " + root.getPath());
        }
        try {
            Properties registry = new Properties();
            registry.load(registryStream);
            List<String> list = this.getAddedFiles(registry, root, filter);
            return list;
        }
        catch (IOException e) {
            throw new ModzRegistryException("Unable to load hash registry: ", e);
        }
        finally {
            IOUtils.closeQuietly(registryStream);
        }
    }

    private List<String> getRelativePaths(File root, List<String> absolutePaths) {
        String rootPath = root.getAbsolutePath() + "/";
        int rootPathLength = rootPath.length();
        ArrayList<String> relativePaths = new ArrayList<String>(absolutePaths.size());
        for (String absolutePath : absolutePaths) {
            relativePaths.add(absolutePath.substring(rootPathLength));
        }
        return relativePaths;
    }

    List<String> getAddedFiles(Properties registry, File root, FileFilter filter) {
        File[] files;
        ArrayList<String> addedFiles = new ArrayList<String>();
        for (File file : files = root.listFiles(filter)) {
            if (file.isDirectory()) {
                addedFiles.addAll(this.getAddedFiles(registry, file, filter));
                continue;
            }
            String resourceKey = this.streamMapper.getResourceKey(file);
            if (registry.containsKey(resourceKey)) continue;
            addedFiles.add(this.streamMapper.getResourcePath(resourceKey));
        }
        return addedFiles;
    }

    void checkRegistry(Modifications mods, Properties registry) {
        int failureCount = 0;
        for (Map.Entry<Object, Object> prop : registry.entrySet()) {
            String propertyKey = (String)prop.getKey();
            try {
                ResourceType checkResult = this.checkResource(propertyKey, (String)prop.getValue());
                checkResult.handle(mods);
            }
            catch (CannotCheckResource cannotCheckResource) {
                ++failureCount;
            }
        }
        if (failureCount > 0) {
            log.warn("Failed to check " + failureCount + " files.");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    ResourceType checkResource(String propertyKey, String resourceName, String hash, InputStream resource) throws CannotCheckResource {
        try {
            if (resourceName == null) {
                throw new IllegalArgumentException("resourceName cannot be null");
            }
            if (hash == null) {
                throw new CannotCheckResource("Expected hash is null");
            }
            if (resource == null) {
                ResourceType resourceType = ResourceType.createRemoved(this.streamMapper.getResourcePath(propertyKey));
                return resourceType;
            }
            String actualHash = this.hashAlgorithm.getHash(resource);
            if (hash.equals(actualHash)) {
                ResourceType resourceType = ResourceType.createUnchanged(this.streamMapper.getResourcePath(propertyKey));
                return resourceType;
            }
            if (log.isDebugEnabled()) {
                log.debug("detected modified resource '" + resourceName + "' expected hash " + hash + " got " + actualHash);
            }
            ResourceType resourceType = ResourceType.createModified(this.streamMapper.getResourcePath(propertyKey));
            return resourceType;
        }
        finally {
            IOUtils.closeQuietly(resource);
        }
    }

    ResourceType checkResource(String propertyKey, String hash) throws CannotCheckResource {
        String prefix = propertyKey.substring(0, "cp.".length());
        String resourceName = propertyKey.substring(prefix.length(), propertyKey.length());
        if (hash == null) {
            throw new CannotCheckResource("unable to interpret registered file with key: " + propertyKey);
        }
        InputStream resource = this.streamMapper.mapStream(prefix, resourceName);
        if (resource == null) {
            return ResourceType.createRemoved(this.streamMapper.getResourcePath(propertyKey));
        }
        return this.checkResource(propertyKey, resourceName, hash, resource);
    }

    static abstract class ResourceType {
        private String resourceName;

        protected ResourceType(String resourceName) {
            this.resourceName = resourceName;
        }

        static ResourceType createModified(final String resourcePath) {
            return new ResourceType(resourcePath){

                void handle(Modifications mods) {
                    mods.modifiedFiles.add(resourcePath);
                }
            };
        }

        static ResourceType createRemoved(final String resourcePath) {
            return new ResourceType(resourcePath){

                void handle(Modifications mods) {
                    mods.removedFiles.add(resourcePath);
                }
            };
        }

        static ResourceType createUnchanged(String resourcePath) {
            return new ResourceType(resourcePath){

                void handle(Modifications mods) {
                }
            };
        }

        abstract void handle(Modifications var1);
    }
}

