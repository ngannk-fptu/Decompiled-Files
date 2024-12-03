/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  org.apache.tomcat.util.scan.JarFactory
 */
package org.apache.catalina.startup;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.apache.catalina.Context;
import org.apache.tomcat.util.scan.JarFactory;

public class WebappServiceLoader<T> {
    private static final String CLASSES = "/WEB-INF/classes/";
    private static final String LIB = "/WEB-INF/lib/";
    private static final String SERVICES = "META-INF/services/";
    private final Context context;
    private final ServletContext servletContext;
    private final Pattern containerSciFilterPattern;

    public WebappServiceLoader(Context context) {
        this.context = context;
        this.servletContext = context.getServletContext();
        String containerSciFilter = context.getContainerSciFilter();
        this.containerSciFilterPattern = containerSciFilter != null && containerSciFilter.length() > 0 ? Pattern.compile(containerSciFilter) : null;
    }

    public List<T> load(Class<T> serviceType) throws IOException {
        String configFile = SERVICES + serviceType.getName();
        ClassLoader loader = this.context.getParentClassLoader();
        Enumeration<URL> containerResources = loader == null ? ClassLoader.getSystemResources(configFile) : loader.getResources(configFile);
        LinkedHashSet<String> containerServiceClassNames = new LinkedHashSet<String>();
        HashSet<URL> containerServiceConfigFiles = new HashSet<URL>();
        while (containerResources.hasMoreElements()) {
            URL containerServiceConfigFile = containerResources.nextElement();
            containerServiceConfigFiles.add(containerServiceConfigFile);
            this.parseConfigFile(containerServiceClassNames, containerServiceConfigFile);
        }
        if (this.containerSciFilterPattern != null) {
            containerServiceClassNames.removeIf(s -> this.containerSciFilterPattern.matcher((CharSequence)s).find());
        }
        LinkedHashSet<String> applicationServiceClassNames = new LinkedHashSet<String>();
        List orderedLibs = (List)this.servletContext.getAttribute("javax.servlet.context.orderedLibs");
        if (orderedLibs == null) {
            Enumeration<URL> allResources = this.servletContext.getClassLoader().getResources(configFile);
            while (allResources.hasMoreElements()) {
                URL serviceConfigFile = allResources.nextElement();
                if (containerServiceConfigFiles.contains(serviceConfigFile)) continue;
                this.parseConfigFile(applicationServiceClassNames, serviceConfigFile);
            }
        } else {
            URL unpacked = this.servletContext.getResource(CLASSES + configFile);
            if (unpacked != null) {
                this.parseConfigFile(applicationServiceClassNames, unpacked);
            }
            for (String lib : orderedLibs) {
                URL url;
                URL jarUrl = this.servletContext.getResource(LIB + lib);
                if (jarUrl == null) continue;
                String base = jarUrl.toExternalForm();
                if (base.endsWith("/")) {
                    URI uri;
                    try {
                        uri = new URI(base + configFile);
                    }
                    catch (URISyntaxException e) {
                        throw new IOException(e);
                    }
                    url = uri.toURL();
                } else {
                    url = JarFactory.getJarEntryURL((URL)jarUrl, (String)configFile);
                }
                try {
                    this.parseConfigFile(applicationServiceClassNames, url);
                }
                catch (FileNotFoundException fileNotFoundException) {}
            }
        }
        containerServiceClassNames.addAll(applicationServiceClassNames);
        if (containerServiceClassNames.isEmpty()) {
            return Collections.emptyList();
        }
        return this.loadServices(serviceType, containerServiceClassNames);
    }

    void parseConfigFile(LinkedHashSet<String> servicesFound, URL url) throws IOException {
        try (InputStream is = url.openStream();
             InputStreamReader in = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(in);){
            String line;
            while ((line = reader.readLine()) != null) {
                int i = line.indexOf(35);
                if (i >= 0) {
                    line = line.substring(0, i);
                }
                if ((line = line.trim()).length() == 0) continue;
                servicesFound.add(line);
            }
        }
    }

    List<T> loadServices(Class<T> serviceType, LinkedHashSet<String> servicesFound) throws IOException {
        ClassLoader loader = this.servletContext.getClassLoader();
        ArrayList<T> services = new ArrayList<T>(servicesFound.size());
        for (String serviceClass : servicesFound) {
            try {
                Class<?> clazz = Class.forName(serviceClass, true, loader);
                services.add(serviceType.cast(clazz.getConstructor(new Class[0]).newInstance(new Object[0])));
            }
            catch (ClassCastException | ReflectiveOperationException e) {
                throw new IOException(e);
            }
        }
        return Collections.unmodifiableList(services);
    }
}

