/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.scanning;

import com.sun.jersey.api.uri.UriComponent;
import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.scanning.Scanner;
import com.sun.jersey.core.spi.scanning.ScannerException;
import com.sun.jersey.core.spi.scanning.ScannerListener;
import com.sun.jersey.core.spi.scanning.uri.BundleSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.FileSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.JarZipSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.UriSchemeScanner;
import com.sun.jersey.core.spi.scanning.uri.VfsSchemeScanner;
import com.sun.jersey.spi.service.ServiceFinder;
import java.io.IOException;
import java.lang.reflect.ReflectPermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessController;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class PackageNamesScanner
implements Scanner {
    private final String[] packages;
    private final ClassLoader classloader;
    private final Map<String, UriSchemeScanner> scanners;

    public PackageNamesScanner(String[] packages) {
        this(AccessController.doPrivileged(ReflectionHelper.getContextClassLoaderPA()), packages);
    }

    public PackageNamesScanner(ClassLoader classloader, String[] packages) {
        this.packages = packages;
        this.classloader = classloader;
        this.scanners = new HashMap<String, UriSchemeScanner>();
        this.add(new JarZipSchemeScanner());
        this.add(new FileSchemeScanner());
        this.add(new VfsSchemeScanner());
        this.add(new BundleSchemeScanner());
        for (UriSchemeScanner s : ServiceFinder.find(UriSchemeScanner.class)) {
            this.add(s);
        }
    }

    private void add(UriSchemeScanner ss) {
        for (String s : ss.getSchemes()) {
            this.scanners.put(s.toLowerCase(), ss);
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public void scan(ScannerListener cfl) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public static void setResourcesProvider(ResourcesProvider provider) throws SecurityException {
        ResourcesProvider.setInstance(provider);
    }

    private void scan(URI u, ScannerListener cfl) {
        UriSchemeScanner ss = this.scanners.get(u.getScheme().toLowerCase());
        if (ss == null) {
            throw new ScannerException("The URI scheme " + u.getScheme() + " of the URI " + u + " is not supported. Package scanning deployment is not supported for such URIs.\nTry using a different deployment mechanism such as explicitly declaring root resource and provider classes using an extension of javax.ws.rs.core.Application");
        }
        ss.scan(u, cfl);
    }

    private URI toURI(URL url) throws URISyntaxException {
        try {
            return url.toURI();
        }
        catch (URISyntaxException e) {
            return URI.create(this.toExternalForm(url));
        }
    }

    private String toExternalForm(URL u) {
        int len = u.getProtocol().length() + 1;
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            len += 2 + u.getAuthority().length();
        }
        if (u.getPath() != null) {
            len += u.getPath().length();
        }
        if (u.getQuery() != null) {
            len += 1 + u.getQuery().length();
        }
        if (u.getRef() != null) {
            len += 1 + u.getRef().length();
        }
        StringBuilder result = new StringBuilder(len);
        result.append(u.getProtocol());
        result.append(":");
        if (u.getAuthority() != null && u.getAuthority().length() > 0) {
            result.append("//");
            result.append(u.getAuthority());
        }
        if (u.getPath() != null) {
            result.append(UriComponent.contextualEncode(u.getPath(), UriComponent.Type.PATH));
        }
        if (u.getQuery() != null) {
            result.append('?');
            result.append(UriComponent.contextualEncode(u.getQuery(), UriComponent.Type.QUERY));
        }
        if (u.getRef() != null) {
            result.append("#");
            result.append(u.getRef());
        }
        return result.toString();
    }

    public static abstract class ResourcesProvider {
        private static volatile ResourcesProvider provider;

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        private static ResourcesProvider getInstance() {
            ResourcesProvider result = provider;
            if (result != null) return result;
            Class<ResourcesProvider> clazz = ResourcesProvider.class;
            synchronized (ResourcesProvider.class) {
                result = provider;
                if (result != null) return result;
                provider = result = new ResourcesProvider(){

                    @Override
                    public Enumeration<URL> getResources(String name, ClassLoader cl) throws IOException {
                        return cl.getResources(name);
                    }
                };
                // ** MonitorExit[var1_1] (shouldn't be in output)
                return result;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private static void setInstance(ResourcesProvider provider) throws SecurityException {
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                ReflectPermission rp = new ReflectPermission("suppressAccessChecks");
                security.checkPermission(rp);
            }
            Class<ResourcesProvider> clazz = ResourcesProvider.class;
            synchronized (ResourcesProvider.class) {
                ResourcesProvider.provider = provider;
                // ** MonitorExit[var2_2] (shouldn't be in output)
                return;
            }
        }

        public abstract Enumeration<URL> getResources(String var1, ClassLoader var2) throws IOException;

        static /* synthetic */ ResourcesProvider access$000() {
            return ResourcesProvider.getInstance();
        }
    }
}

