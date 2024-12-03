/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.HashAlgorithm;
import com.atlassian.modzdetector.IOUtils;
import com.atlassian.modzdetector.MD5HashAlgorithm;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HashRegistry {
    private static final Logger log = LoggerFactory.getLogger(HashRegistry.class);
    static final String FILE_NAME_HASH_REGISTRY_PROPERTIES = "hash-registry.properties";
    public static final String PREFIX_CLASSPATH = "cp.";
    public static final String PREFIX_FILESYSTEM = "fs.";
    private Properties properties;
    private HashAlgorithm algorithm;
    private String registryFilename;
    private String currentPrefix;

    public HashRegistry() {
        this(new MD5HashAlgorithm(), FILE_NAME_HASH_REGISTRY_PROPERTIES);
    }

    public HashRegistry(String fileName) {
        this(new MD5HashAlgorithm(), fileName);
    }

    public HashRegistry(HashAlgorithm algorithm, String fileName) {
        this.algorithm = algorithm;
        this.registryFilename = fileName;
        this.properties = new Properties();
        this.setFilesystemMode();
    }

    public void setClasspathMode() {
        this.currentPrefix = PREFIX_CLASSPATH;
    }

    public void setFilesystemMode() {
        this.currentPrefix = PREFIX_FILESYSTEM;
    }

    public void register(String name, InputStream contents) {
        this.properties.setProperty(this.currentPrefix + name, this.algorithm.getHash(contents));
        IOUtils.closeQuietly(contents);
    }

    public void registerFilesystm(File root) {
        this.registerFilesystem(root, new FileFilter(){

            public boolean accept(File file) {
                return true;
            }
        });
    }

    public void registerFilesystem(File root, FileFilter filter) {
        this.setFilesystemMode();
        if (!filter.accept(root)) {
            return;
        }
        int stripTo = root.getAbsolutePath().length() + "/".length();
        this.registerFilesystem(stripTo, root, filter);
    }

    private void registerFilesystem(int stripTo, File file, FileFilter filter) {
        if (file.isDirectory()) {
            for (File f : file.listFiles(filter)) {
                this.registerFilesystem(stripTo, f, filter);
            }
        } else {
            String relativePath = file.getAbsolutePath().substring(stripTo);
            try {
                this.register(relativePath, new FileInputStream(file));
            }
            catch (FileNotFoundException e) {
                log.warn("Weird, file not found: '{}'", (Object)file.getAbsolutePath());
            }
        }
    }

    public File store() throws IOException {
        File registryFile = new File(this.registryFilename);
        FileOutputStream propertiesOut = new FileOutputStream(registryFile);
        this.properties.store(propertiesOut, " THIS FILE IS GENERATED - DO NOT MODIFY. algorithm: " + this.algorithm);
        propertiesOut.close();
        log.info("wrote " + this.properties.keySet().size() + " hashes to " + registryFile.getAbsolutePath());
        return registryFile;
    }
}

