/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.xpath;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.xpath.Path;
import org.apache.xmlbeans.impl.xpath.XPath;
import org.apache.xmlbeans.impl.xpath.XQuery;
import org.apache.xmlbeans.impl.xpath.saxon.SaxonXPath;
import org.apache.xmlbeans.impl.xpath.saxon.SaxonXQuery;
import org.apache.xmlbeans.impl.xpath.xmlbeans.XmlbeansXPath;

public class XPathFactory {
    private static final int USE_XMLBEANS = 1;
    private static final int USE_SAXON = 4;
    private static final Map<String, WeakReference<Path>> _xmlbeansPathCache = new WeakHashMap<String, WeakReference<Path>>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    static String getCurrentNodeVar(XmlOptions options) {
        String currentNodeVar = "this";
        String cnv = (options = XmlOptions.maskNull(options)).getXqueryCurrentNodeVar();
        if (cnv != null && (currentNodeVar = cnv).startsWith("$")) {
            throw new IllegalArgumentException("Omit the '$' prefix for the current node variable");
        }
        return currentNodeVar;
    }

    public static Path getCompiledPath(String pathExpr, XmlOptions options) {
        options = XmlOptions.maskNull(options);
        return XPathFactory.getCompiledPath(pathExpr, options, XPathFactory.getCurrentNodeVar(options));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Path getCompiledPath(String pathExpr, XmlOptions options, String currentVar) {
        int force = options.isXPathUseSaxon() ? 4 : (options.isXPathUseXmlBeans() ? 1 : 5);
        Path path = null;
        WeakReference<Path> pathWeakRef = null;
        HashMap<String, String> namespaces = (force & 4) != 0 ? new HashMap<String, String>() : null;
        lock.readLock().lock();
        try {
            if ((force & 1) != 0) {
                pathWeakRef = _xmlbeansPathCache.get(pathExpr);
            }
            if (pathWeakRef != null) {
                path = (Path)pathWeakRef.get();
            }
            if (path != null) {
                Path path2 = path;
                return path2;
            }
        }
        finally {
            lock.readLock().unlock();
        }
        lock.writeLock().lock();
        try {
            if ((force & 1) != 0) {
                pathWeakRef = _xmlbeansPathCache.get(pathExpr);
                if (pathWeakRef != null) {
                    path = (Path)pathWeakRef.get();
                }
                if (path == null) {
                    path = XPathFactory.getCompiledPathXmlBeans(pathExpr, currentVar, namespaces);
                }
            }
            if (path == null && (force & 4) != 0) {
                path = XPathFactory.getCompiledPathSaxon(pathExpr, currentVar, namespaces);
            }
            if (path == null) {
                StringBuilder errMessage = new StringBuilder();
                if ((force & 1) != 0) {
                    errMessage.append(" Trying XmlBeans path engine...");
                }
                if ((force & 4) != 0) {
                    errMessage.append(" Trying Saxon path engine...");
                }
                throw new RuntimeException(errMessage.toString() + " FAILED on " + pathExpr);
            }
        }
        finally {
            lock.writeLock().unlock();
        }
        return path;
    }

    private static Path getCompiledPathXmlBeans(String pathExpr, String currentVar, Map<String, String> namespaces) {
        try {
            XmlbeansXPath path = new XmlbeansXPath(pathExpr, currentVar, XPath.compileXPath(pathExpr, currentVar, namespaces));
            _xmlbeansPathCache.put(pathExpr, new WeakReference<XmlbeansXPath>(path));
            return path;
        }
        catch (XPath.XPathCompileException ignored) {
            return null;
        }
    }

    public static Path getCompiledPathSaxon(String pathExpr, String currentVar, Map<String, String> namespaces) {
        if (namespaces == null) {
            namespaces = new HashMap<String, String>();
        }
        try {
            XPath.compileXPath(pathExpr, currentVar, namespaces);
        }
        catch (XPath.XPathCompileException xPathCompileException) {
            // empty catch block
        }
        int offset = Integer.parseInt(namespaces.getOrDefault("$xmlbeans!ns_boundary", "0"));
        namespaces.remove("$xmlbeans!ns_boundary");
        return new SaxonXPath(pathExpr.substring(offset), currentVar, namespaces);
    }

    public static String compilePath(String pathExpr, XmlOptions options) {
        XPathFactory.getCompiledPath(pathExpr, options);
        return pathExpr;
    }

    public static XmlObject[] objectExecQuery(Cur c, String queryExpr, XmlOptions options) {
        return XPathFactory.getCompiledQuery(queryExpr, options).objectExecute(c, options);
    }

    public static XmlCursor cursorExecQuery(Cur c, String queryExpr, XmlOptions options) {
        return XPathFactory.getCompiledQuery(queryExpr, options).cursorExecute(c, options);
    }

    public static synchronized XQuery getCompiledQuery(String queryExpr, XmlOptions options) {
        return XPathFactory.getCompiledQuery(queryExpr, XPathFactory.getCurrentNodeVar(options), options);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static synchronized XQuery getCompiledQuery(String queryExpr, String currentVar, XmlOptions options) {
        int boundaryVal;
        assert (queryExpr != null);
        options = XmlOptions.maskNull(options);
        HashMap<String, String> boundary = new HashMap<String, String>();
        try {
            XPath.compileXPath(queryExpr, currentVar, boundary);
        }
        catch (XPath.XPathCompileException xPathCompileException) {
        }
        finally {
            boundaryVal = Integer.parseInt(boundary.getOrDefault("$xmlbeans!ns_boundary", "0"));
        }
        return new SaxonXQuery(queryExpr, currentVar, boundaryVal, options);
    }

    public static synchronized String compileQuery(String queryExpr, XmlOptions options) {
        XPathFactory.getCompiledQuery(queryExpr, options);
        return queryExpr;
    }
}

