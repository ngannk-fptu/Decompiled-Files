/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.xml.security.keys.keyresolver;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.DEREncodedKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.DSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.ECKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.KeyInfoReferenceResolver;
import org.apache.xml.security.keys.keyresolver.implementations.RSAKeyValueResolver;
import org.apache.xml.security.keys.keyresolver.implementations.RetrievalMethodResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509CertificateResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509DigestResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509IssuerSerialResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509SKIResolver;
import org.apache.xml.security.keys.keyresolver.implementations.X509SubjectNameResolver;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.utils.ClassLoaderUtils;
import org.apache.xml.security.utils.JavaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

public class KeyResolver {
    private static final Logger LOG = LoggerFactory.getLogger(KeyResolver.class);
    private static List<KeyResolverSpi> resolverList = new CopyOnWriteArrayList<KeyResolverSpi>();
    private static final AtomicBoolean defaultResolversAdded = new AtomicBoolean();

    public static int length() {
        return resolverList.size();
    }

    public static final X509Certificate getX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        for (KeyResolverSpi resolver : resolverList) {
            if (resolver == null) {
                Object[] exArgs = new Object[]{element != null && element.getNodeType() == 1 ? element.getTagName() : "null"};
                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            LOG.debug("check resolvability by class {}", resolver.getClass());
            X509Certificate cert = resolver.engineLookupResolveX509Certificate(element, baseURI, storage, secureValidation);
            if (cert == null) continue;
            return cert;
        }
        Object[] exArgs = new Object[]{element != null && element.getNodeType() == 1 ? element.getTagName() : "null"};
        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }

    public static final PublicKey getPublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        for (KeyResolverSpi resolver : resolverList) {
            if (resolver == null) {
                Object[] exArgs = new Object[]{element != null && element.getNodeType() == 1 ? element.getTagName() : "null"};
                throw new KeyResolverException("utils.resolver.noClass", exArgs);
            }
            LOG.debug("check resolvability by class {}", resolver.getClass());
            PublicKey cert = resolver.engineLookupAndResolvePublicKey(element, baseURI, storage, secureValidation);
            if (cert == null) continue;
            return cert;
        }
        Object[] exArgs = new Object[]{element != null && element.getNodeType() == 1 ? element.getTagName() : "null"};
        throw new KeyResolverException("utils.resolver.noClass", exArgs);
    }

    public static void register(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        KeyResolverSpi keyResolverSpi = (KeyResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, KeyResolver.class));
        KeyResolver.register(keyResolverSpi, false);
    }

    public static void registerAtStart(String className) {
        JavaUtils.checkRegisterPermission();
        KeyResolverSpi keyResolverSpi = null;
        ReflectiveOperationException ex = null;
        try {
            keyResolverSpi = (KeyResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, KeyResolver.class));
            KeyResolver.register(keyResolverSpi, true);
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            ex = e;
        }
        if (ex != null) {
            throw (IllegalArgumentException)new IllegalArgumentException("Invalid KeyResolver class name").initCause(ex);
        }
    }

    public static void register(KeyResolverSpi keyResolverSpi, boolean start) {
        JavaUtils.checkRegisterPermission();
        if (start) {
            resolverList.add(0, keyResolverSpi);
        } else {
            resolverList.add(keyResolverSpi);
        }
    }

    public static void registerClassNames(List<String> classNames) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        JavaUtils.checkRegisterPermission();
        ArrayList<KeyResolverSpi> keyResolverList = new ArrayList<KeyResolverSpi>(classNames.size());
        for (String className : classNames) {
            KeyResolverSpi keyResolverSpi = (KeyResolverSpi)JavaUtils.newInstanceWithEmptyConstructor(ClassLoaderUtils.loadClass(className, KeyResolver.class));
            keyResolverList.add(keyResolverSpi);
        }
        resolverList.addAll(keyResolverList);
    }

    public static void registerDefaultResolvers() {
        if (defaultResolversAdded.compareAndSet(false, true)) {
            ArrayList<KeyResolverSpi> keyResolverList = new ArrayList<KeyResolverSpi>();
            keyResolverList.add(new RSAKeyValueResolver());
            keyResolverList.add(new DSAKeyValueResolver());
            keyResolverList.add(new X509CertificateResolver());
            keyResolverList.add(new X509SKIResolver());
            keyResolverList.add(new RetrievalMethodResolver());
            keyResolverList.add(new X509SubjectNameResolver());
            keyResolverList.add(new X509IssuerSerialResolver());
            keyResolverList.add(new DEREncodedKeyValueResolver());
            keyResolverList.add(new KeyInfoReferenceResolver());
            keyResolverList.add(new X509DigestResolver());
            keyResolverList.add(new ECKeyValueResolver());
            resolverList.addAll(keyResolverList);
        }
    }

    public static Iterator<KeyResolverSpi> iterator() {
        return new ResolverIterator(resolverList);
    }

    static class ResolverIterator
    implements Iterator<KeyResolverSpi> {
        private List<KeyResolverSpi> res;
        private Iterator<KeyResolverSpi> it;

        public ResolverIterator(List<KeyResolverSpi> list) {
            this.res = list;
            this.it = this.res.iterator();
        }

        @Override
        public boolean hasNext() {
            return this.it.hasNext();
        }

        @Override
        public KeyResolverSpi next() {
            KeyResolverSpi resolver = this.it.next();
            if (resolver == null) {
                throw new RuntimeException("utils.resolver.noClass");
            }
            return resolver;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove resolvers using the iterator");
        }
    }
}

