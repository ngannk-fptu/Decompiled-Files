/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.parser;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;
import java.util.concurrent.ArrayBlockingQueue;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.xml.security.parser.XMLParser;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XMLParserImpl
implements XMLParser {
    private static int parserPoolSize = AccessController.doPrivileged(() -> Integer.getInteger("org.apache.xml.security.parser.pool-size", 20));
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS = Collections.synchronizedMap(new WeakHashMap());
    private static final Map<ClassLoader, Queue<DocumentBuilder>> DOCUMENT_BUILDERS_DISALLOW_DOCTYPE = Collections.synchronizedMap(new WeakHashMap());

    @Override
    public Document parse(InputStream inputStream, boolean disallowDocTypeDeclarations) throws XMLParserException {
        try {
            ClassLoader loader = XMLParserImpl.getContextClassLoader();
            if (loader == null) {
                loader = XMLParserImpl.getClassLoader(XMLUtils.class);
            }
            if (loader == null) {
                DocumentBuilder documentBuilder = XMLParserImpl.createDocumentBuilder(disallowDocTypeDeclarations);
                return documentBuilder.parse(inputStream);
            }
            Queue<DocumentBuilder> queue = XMLParserImpl.getDocumentBuilderQueue(disallowDocTypeDeclarations, loader);
            DocumentBuilder documentBuilder = XMLParserImpl.getDocumentBuilder(disallowDocTypeDeclarations, queue);
            Document doc = documentBuilder.parse(inputStream);
            XMLParserImpl.repoolDocumentBuilder(documentBuilder, queue);
            return doc;
        }
        catch (IOException | ParserConfigurationException | SAXException ex) {
            throw new XMLParserException(ex, "empty", new Object[]{"Error parsing the inputstream"});
        }
    }

    private static Queue<DocumentBuilder> getDocumentBuilderQueue(boolean disallowDocTypeDeclarations, ClassLoader loader) throws ParserConfigurationException {
        Map<ClassLoader, Queue<DocumentBuilder>> docBuilderCache = disallowDocTypeDeclarations ? DOCUMENT_BUILDERS_DISALLOW_DOCTYPE : DOCUMENT_BUILDERS;
        Queue<DocumentBuilder> queue = docBuilderCache.get(loader);
        if (queue == null) {
            queue = new ArrayBlockingQueue<DocumentBuilder>(parserPoolSize);
            docBuilderCache.put(loader, queue);
        }
        return queue;
    }

    private static DocumentBuilder getDocumentBuilder(boolean disallowDocTypeDeclarations, Queue<DocumentBuilder> queue) throws ParserConfigurationException {
        DocumentBuilder db = queue.poll();
        if (db == null) {
            db = XMLParserImpl.createDocumentBuilder(disallowDocTypeDeclarations);
        }
        return db;
    }

    private static DocumentBuilder createDocumentBuilder(boolean disallowDocTypeDeclarations) throws ParserConfigurationException {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disallowDocTypeDeclarations);
        return f.newDocumentBuilder();
    }

    private static void repoolDocumentBuilder(DocumentBuilder db, Queue<DocumentBuilder> queue) {
        if (queue != null) {
            db.reset();
            queue.offer(db);
        }
    }

    private static ClassLoader getContextClassLoader() {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
        return Thread.currentThread().getContextClassLoader();
    }

    private static ClassLoader getClassLoader(final Class<?> clazz) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){

                @Override
                public ClassLoader run() {
                    return clazz.getClassLoader();
                }
            });
        }
        return clazz.getClassLoader();
    }
}

