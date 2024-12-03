/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.catalina.Context;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.Extension;
import org.apache.catalina.util.ManifestResource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public final class ExtensionValidator {
    private static final Log log = LogFactory.getLog(ExtensionValidator.class);
    private static final StringManager sm = StringManager.getManager((String)"org.apache.catalina.util");
    private static volatile List<Extension> containerAvailableExtensions = null;
    private static final List<ManifestResource> containerManifestResources = new ArrayList<ManifestResource>();

    public static synchronized boolean validateApplication(WebResourceRoot resources, Context context) throws IOException {
        WebResource[] manifestResources;
        String appName = context.getName();
        ArrayList<ManifestResource> appManifestResources = new ArrayList<ManifestResource>();
        WebResource resource = resources.getResource("/META-INF/MANIFEST.MF");
        if (resource.isFile()) {
            try (InputStream inputStream = resource.getInputStream();){
                Manifest manifest = new Manifest(inputStream);
                ManifestResource mre = new ManifestResource(sm.getString("extensionValidator.web-application-manifest"), manifest, 2);
                appManifestResources.add(mre);
            }
        }
        for (WebResource manifestResource : manifestResources = resources.getClassLoaderResources("/META-INF/MANIFEST.MF")) {
            if (!manifestResource.isFile()) continue;
            String jarName = manifestResource.getURL().toExternalForm();
            Manifest jmanifest = manifestResource.getManifest();
            if (jmanifest == null) continue;
            ManifestResource mre = new ManifestResource(jarName, jmanifest, 3);
            appManifestResources.add(mre);
        }
        return ExtensionValidator.validateManifestResources(appName, appManifestResources);
    }

    public static void addSystemResource(File jarFile) throws IOException {
        try (FileInputStream is = new FileInputStream(jarFile);){
            Manifest manifest = ExtensionValidator.getManifest(is);
            if (manifest != null) {
                ManifestResource mre = new ManifestResource(jarFile.getAbsolutePath(), manifest, 1);
                containerManifestResources.add(mre);
            }
        }
    }

    private static boolean validateManifestResources(String appName, List<ManifestResource> resources) {
        boolean passes = true;
        int failureCount = 0;
        List<Extension> availableExtensions = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> requiredList = mre.getRequiredExtensions();
            if (requiredList == null) continue;
            if (availableExtensions == null) {
                availableExtensions = ExtensionValidator.buildAvailableExtensionsList(resources);
            }
            if (containerAvailableExtensions == null) {
                containerAvailableExtensions = ExtensionValidator.buildAvailableExtensionsList(containerManifestResources);
            }
            for (Extension requiredExt : requiredList) {
                boolean found = false;
                if (availableExtensions != null) {
                    for (Extension targetExt : availableExtensions) {
                        if (!targetExt.isCompatibleWith(requiredExt)) continue;
                        requiredExt.setFulfilled(true);
                        found = true;
                        break;
                    }
                }
                if (!found && containerAvailableExtensions != null) {
                    for (Extension targetExt : containerAvailableExtensions) {
                        if (!targetExt.isCompatibleWith(requiredExt)) continue;
                        requiredExt.setFulfilled(true);
                        found = true;
                        break;
                    }
                }
                if (found) continue;
                log.info((Object)sm.getString("extensionValidator.extension-not-found-error", new Object[]{appName, mre.getResourceName(), requiredExt.getExtensionName()}));
                passes = false;
                ++failureCount;
            }
        }
        if (!passes) {
            log.info((Object)sm.getString("extensionValidator.extension-validation-error", new Object[]{appName, failureCount + ""}));
        }
        return passes;
    }

    private static List<Extension> buildAvailableExtensionsList(List<ManifestResource> resources) {
        ArrayList<Extension> availableList = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> list = mre.getAvailableExtensions();
            if (list == null) continue;
            for (Extension ext : list) {
                if (availableList == null) {
                    availableList = new ArrayList<Extension>();
                    availableList.add(ext);
                    continue;
                }
                availableList.add(ext);
            }
        }
        return availableList;
    }

    private static Manifest getManifest(InputStream inStream) throws IOException {
        Manifest manifest = null;
        try (JarInputStream jin = new JarInputStream(inStream);){
            manifest = jin.getManifest();
        }
        return manifest;
    }

    private static void addFolderList(String property) {
        String extensionsDir = System.getProperty(property);
        if (extensionsDir != null) {
            StringTokenizer extensionsTok = new StringTokenizer(extensionsDir, File.pathSeparator);
            while (extensionsTok.hasMoreTokens()) {
                File[] files;
                File targetDir = new File(extensionsTok.nextToken());
                if (!targetDir.isDirectory() || (files = targetDir.listFiles()) == null) continue;
                for (File file : files) {
                    if (!file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar") || !file.isFile()) continue;
                    try {
                        ExtensionValidator.addSystemResource(file);
                    }
                    catch (IOException e) {
                        log.error((Object)sm.getString("extensionValidator.failload", new Object[]{file}), (Throwable)e);
                    }
                }
            }
        }
    }

    static {
        String systemClasspath = System.getProperty("java.class.path");
        StringTokenizer strTok = new StringTokenizer(systemClasspath, File.pathSeparator);
        while (strTok.hasMoreTokens()) {
            File item;
            String classpathItem = strTok.nextToken();
            if (!classpathItem.toLowerCase(Locale.ENGLISH).endsWith(".jar") || !(item = new File(classpathItem)).isFile()) continue;
            try {
                ExtensionValidator.addSystemResource(item);
            }
            catch (IOException e) {
                log.error((Object)sm.getString("extensionValidator.failload", new Object[]{item}), (Throwable)e);
            }
        }
        ExtensionValidator.addFolderList("java.ext.dirs");
    }
}

