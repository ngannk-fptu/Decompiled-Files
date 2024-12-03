/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.AbsoluteNameLocationStrategy;
import org.apache.commons.configuration2.io.BasePathLocationStrategy;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.apache.commons.configuration2.io.CombinedLocationStrategy;
import org.apache.commons.configuration2.io.DefaultFileSystem;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.FileSystemLocationStrategy;
import org.apache.commons.configuration2.io.FileUtils;
import org.apache.commons.configuration2.io.HomeDirectoryLocationStrategy;
import org.apache.commons.configuration2.io.ProvidedURLLocationStrategy;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class FileLocatorUtils {
    public static final FileSystem DEFAULT_FILE_SYSTEM = new DefaultFileSystem();
    public static final FileLocationStrategy DEFAULT_LOCATION_STRATEGY = FileLocatorUtils.initDefaultLocationStrategy();
    private static final String FILE_SCHEME = "file:";
    private static final Log LOG = LogFactory.getLog(FileLocatorUtils.class);
    private static final String PROP_BASE_PATH = "basePath";
    private static final String PROP_ENCODING = "encoding";
    private static final String PROP_FILE_NAME = "fileName";
    private static final String PROP_FILE_SYSTEM = "fileSystem";
    private static final String PROP_STRATEGY = "locationStrategy";
    private static final String PROP_SOURCE_URL = "sourceURL";

    private FileLocatorUtils() {
    }

    static String appendPath(String path, String ext) {
        StringBuilder fName = new StringBuilder();
        fName.append(path);
        if (!path.endsWith(File.separator)) {
            fName.append(File.separator);
        }
        if (ext.startsWith("." + File.separator)) {
            fName.append(ext.substring(2));
        } else {
            fName.append(ext);
        }
        return fName.toString();
    }

    static File constructFile(String basePath, String fileName) {
        File absolute = new File(fileName);
        File file = StringUtils.isEmpty((CharSequence)basePath) || absolute.isAbsolute() ? absolute : new File(FileLocatorUtils.appendPath(basePath, fileName));
        return file;
    }

    static URL convertFileToURL(File file) {
        return FileLocatorUtils.convertURIToURL(file.toURI());
    }

    static URL convertURIToURL(URI uri) {
        try {
            return uri.toURL();
        }
        catch (MalformedURLException e) {
            return null;
        }
    }

    private static FileLocator createFullyInitializedLocatorFromURL(FileLocator src, URL url) {
        FileLocator.FileLocatorBuilder fileLocatorBuilder = FileLocatorUtils.fileLocator(src);
        if (src.getSourceURL() == null) {
            fileLocatorBuilder.sourceURL(url);
        }
        if (StringUtils.isBlank((CharSequence)src.getFileName())) {
            fileLocatorBuilder.fileName(FileLocatorUtils.getFileName(url));
        }
        if (StringUtils.isBlank((CharSequence)src.getBasePath())) {
            fileLocatorBuilder.basePath(FileLocatorUtils.getBasePath(url));
        }
        return fileLocatorBuilder.create();
    }

    public static File fileFromURL(URL url) {
        return FileUtils.toFile(url);
    }

    public static FileLocator.FileLocatorBuilder fileLocator() {
        return FileLocatorUtils.fileLocator(null);
    }

    public static FileLocator.FileLocatorBuilder fileLocator(FileLocator src) {
        return new FileLocator.FileLocatorBuilder(src);
    }

    public static FileLocator fromMap(Map<String, ?> map) {
        FileLocator.FileLocatorBuilder builder = FileLocatorUtils.fileLocator();
        if (map != null) {
            builder.basePath((String)map.get(PROP_BASE_PATH)).encoding((String)map.get(PROP_ENCODING)).fileName((String)map.get(PROP_FILE_NAME)).fileSystem((FileSystem)map.get(PROP_FILE_SYSTEM)).locationStrategy((FileLocationStrategy)map.get(PROP_STRATEGY)).sourceURL((URL)map.get(PROP_SOURCE_URL));
        }
        return builder.create();
    }

    public static FileLocator fullyInitializedLocator(FileLocator locator) {
        if (FileLocatorUtils.isFullyInitialized(locator)) {
            return locator;
        }
        URL url = FileLocatorUtils.locate(locator);
        return url != null ? FileLocatorUtils.createFullyInitializedLocatorFromURL(locator, url) : null;
    }

    static String getBasePath(URL url) {
        if (url == null) {
            return null;
        }
        String s = url.toString();
        if (s.startsWith(FILE_SCHEME) && !s.startsWith("file://")) {
            s = "file://" + s.substring(FILE_SCHEME.length());
        }
        if (s.endsWith("/") || StringUtils.isEmpty((CharSequence)url.getPath())) {
            return s;
        }
        return s.substring(0, s.lastIndexOf("/") + 1);
    }

    static URL getClasspathResource(String resourceName) {
        URL url = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null && (url = loader.getResource(resourceName)) != null) {
            LOG.debug((Object)("Loading configuration from the context classpath (" + resourceName + ")"));
        }
        if (url == null && (url = ClassLoader.getSystemResource(resourceName)) != null) {
            LOG.debug((Object)("Loading configuration from the system classpath (" + resourceName + ")"));
        }
        return url;
    }

    static File getFile(String basePath, String fileName) {
        URL url;
        File f = new File(fileName);
        if (f.isAbsolute()) {
            return f;
        }
        try {
            url = new URL(new URL(basePath), fileName);
        }
        catch (MalformedURLException mex1) {
            try {
                url = new URL(fileName);
            }
            catch (MalformedURLException mex2) {
                url = null;
            }
        }
        if (url != null) {
            return FileLocatorUtils.fileFromURL(url);
        }
        return FileLocatorUtils.constructFile(basePath, fileName);
    }

    static String getFileName(URL url) {
        if (url == null) {
            return null;
        }
        String path = url.getPath();
        if (path.endsWith("/") || StringUtils.isEmpty((CharSequence)path)) {
            return null;
        }
        return path.substring(path.lastIndexOf("/") + 1);
    }

    static FileSystem getFileSystem(FileLocator locator) {
        return locator != null ? (FileSystem)ObjectUtils.defaultIfNull((Object)locator.getFileSystem(), (Object)DEFAULT_FILE_SYSTEM) : DEFAULT_FILE_SYSTEM;
    }

    static FileLocationStrategy getLocationStrategy(FileLocator locator) {
        return locator != null ? (FileLocationStrategy)ObjectUtils.defaultIfNull((Object)locator.getLocationStrategy(), (Object)DEFAULT_LOCATION_STRATEGY) : DEFAULT_LOCATION_STRATEGY;
    }

    private static FileLocationStrategy initDefaultLocationStrategy() {
        FileLocationStrategy[] subStrategies = new FileLocationStrategy[]{new ProvidedURLLocationStrategy(), new FileSystemLocationStrategy(), new AbsoluteNameLocationStrategy(), new BasePathLocationStrategy(), new HomeDirectoryLocationStrategy(true), new HomeDirectoryLocationStrategy(false), new ClasspathLocationStrategy()};
        return new CombinedLocationStrategy(Arrays.asList(subStrategies));
    }

    public static boolean isFullyInitialized(FileLocator locator) {
        if (locator == null) {
            return false;
        }
        return locator.getBasePath() != null && locator.getFileName() != null && locator.getSourceURL() != null;
    }

    public static boolean isLocationDefined(FileLocator locator) {
        return locator != null && (locator.getFileName() != null || locator.getSourceURL() != null);
    }

    public static URL locate(FileLocator locator) {
        if (locator == null) {
            return null;
        }
        return FileLocatorUtils.getLocationStrategy(locator).locate(FileLocatorUtils.getFileSystem(locator), locator);
    }

    public static URL locateOrThrow(FileLocator locator) throws ConfigurationException {
        URL url = FileLocatorUtils.locate(locator);
        if (url == null) {
            throw new ConfigurationException("Could not locate: " + locator);
        }
        return url;
    }

    public static void put(FileLocator locator, Map<String, Object> map) {
        if (map == null) {
            throw new IllegalArgumentException("Map must not be null!");
        }
        if (locator != null) {
            map.put(PROP_BASE_PATH, locator.getBasePath());
            map.put(PROP_ENCODING, locator.getEncoding());
            map.put(PROP_FILE_NAME, locator.getFileName());
            map.put(PROP_FILE_SYSTEM, locator.getFileSystem());
            map.put(PROP_SOURCE_URL, locator.getSourceURL());
            map.put(PROP_STRATEGY, locator.getLocationStrategy());
        }
    }

    static URL toURL(File file) throws MalformedURLException {
        return file.toURI().toURL();
    }
}

