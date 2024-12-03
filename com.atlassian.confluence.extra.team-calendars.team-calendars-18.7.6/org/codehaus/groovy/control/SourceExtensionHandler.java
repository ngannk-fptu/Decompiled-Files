/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import groovy.lang.GroovyRuntimeException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;

public class SourceExtensionHandler {
    public static Set<String> getRegisteredExtensions(ClassLoader loader) {
        LinkedHashSet<String> extensions = new LinkedHashSet<String>();
        extensions.add("groovy");
        try {
            Enumeration<URL> globalServices = loader.getResources("META-INF/services/org.codehaus.groovy.source.Extensions");
            while (globalServices.hasMoreElements()) {
                BufferedReader svcIn = null;
                URL service = globalServices.nextElement();
                try {
                    svcIn = new BufferedReader(new InputStreamReader(service.openStream()));
                    String extension = svcIn.readLine();
                    while (extension != null) {
                        if (!(extension = extension.trim()).startsWith("#") && extension.length() > 0) {
                            extensions.add(extension);
                        }
                        extension = svcIn.readLine();
                    }
                }
                catch (IOException ex) {
                    throw new GroovyRuntimeException("IO Exception attempting to load registered source extension " + service.toExternalForm() + ". Exception: " + ex.toString());
                }
                finally {
                    if (svcIn == null) continue;
                    svcIn.close();
                }
            }
        }
        catch (IOException ex) {
            throw new GroovyRuntimeException("IO Exception getting registered source extensions. Exception: " + ex.toString());
        }
        return extensions;
    }
}

