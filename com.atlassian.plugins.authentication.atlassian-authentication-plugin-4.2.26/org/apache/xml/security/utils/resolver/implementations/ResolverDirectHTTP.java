/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.utils.resolver.implementations;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResolverDirectHTTP
extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger(ResolverDirectHTTP.class);
    private static final String[] properties = new String[]{"http.proxy.host", "http.proxy.port", "http.proxy.username", "http.proxy.password", "http.basic.username", "http.basic.password"};
    private static final int HttpProxyHost = 0;
    private static final int HttpProxyPort = 1;
    private static final int HttpProxyUser = 2;
    private static final int HttpProxyPass = 3;
    private static final int HttpBasicUser = 4;
    private static final int HttpBasicPass = 5;
    private final Map<String, String> resolverProperties;

    public ResolverDirectHTTP() {
        this.resolverProperties = Collections.emptyMap();
    }

    public ResolverDirectHTTP(Map<String, String> resolverProperties) {
        this.resolverProperties = Collections.unmodifiableMap(resolverProperties != null ? resolverProperties : Collections.emptyMap());
    }

    /*
     * Exception decompiling
     */
    @Override
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
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

    private URLConnection openConnection(URL url, ResourceResolverContext context) throws IOException {
        URLConnection urlConnection;
        String proxyHostProp = this.getProperty(context, properties[0]);
        String proxyPortProp = this.getProperty(context, properties[1]);
        String proxyUser = this.getProperty(context, properties[2]);
        String proxyPass = this.getProperty(context, properties[3]);
        Proxy proxy = null;
        if (proxyHostProp != null && proxyPortProp != null) {
            int port = Integer.parseInt(proxyPortProp);
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHostProp, port));
        }
        if (proxy != null) {
            urlConnection = url.openConnection(proxy);
            if (proxyUser != null && proxyPass != null) {
                String password = proxyUser + ":" + proxyPass;
                String authString = "Basic " + XMLUtils.encodeToString(password.getBytes(StandardCharsets.ISO_8859_1));
                urlConnection.setRequestProperty("Proxy-Authorization", authString);
            }
        } else {
            urlConnection = url.openConnection();
        }
        return urlConnection;
    }

    @Override
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null) {
            LOG.debug("quick fail, uri == null");
            return false;
        }
        if (context.uriToResolve.isEmpty() || context.uriToResolve.charAt(0) == '#') {
            LOG.debug("quick fail for empty URIs and local ones");
            return false;
        }
        LOG.debug("I was asked whether I can resolve {}", (Object)context.uriToResolve);
        if (context.uriToResolve.startsWith("http:") || context.baseUri != null && context.baseUri.startsWith("http:")) {
            LOG.debug("I state that I can resolve {}", (Object)context.uriToResolve);
            return true;
        }
        LOG.debug("I state that I can't resolve {}", (Object)context.uriToResolve);
        return false;
    }

    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri = null;
        newUri = baseURI == null || baseURI.length() == 0 ? new URI(uri) : new URI(baseURI).resolve(uri);
        if (newUri.getFragment() != null) {
            return new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
        }
        return newUri;
    }

    private String getProperty(ResourceResolverContext context, String propertyName) {
        if (this.resolverProperties.containsKey(propertyName)) {
            return this.resolverProperties.get(propertyName);
        }
        return context.getProperties().get(propertyName);
    }

    private static /* synthetic */ /* end resource */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            }
            catch (Throwable throwable) {
                x0.addSuppressed(throwable);
            }
        } else {
            x1.close();
        }
    }
}

