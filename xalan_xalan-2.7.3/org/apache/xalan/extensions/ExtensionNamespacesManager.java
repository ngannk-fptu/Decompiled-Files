/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.util.Vector;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionNamespaceSupport;

public class ExtensionNamespacesManager {
    private Vector m_extensions = new Vector();
    private Vector m_predefExtensions = new Vector(7);
    private Vector m_unregisteredExtensions = new Vector();

    public ExtensionNamespacesManager() {
        this.setPredefinedNamespaces();
    }

    public void registerExtension(String namespace) {
        if (this.namespaceIndex(namespace, this.m_extensions) == -1) {
            int predef = this.namespaceIndex(namespace, this.m_predefExtensions);
            if (predef != -1) {
                this.m_extensions.add(this.m_predefExtensions.get(predef));
            } else if (!this.m_unregisteredExtensions.contains(namespace)) {
                this.m_unregisteredExtensions.add(namespace);
            }
        }
    }

    public void registerExtension(ExtensionNamespaceSupport extNsSpt) {
        String namespace = extNsSpt.getNamespace();
        if (this.namespaceIndex(namespace, this.m_extensions) == -1) {
            this.m_extensions.add(extNsSpt);
            if (this.m_unregisteredExtensions.contains(namespace)) {
                this.m_unregisteredExtensions.remove(namespace);
            }
        }
    }

    public int namespaceIndex(String namespace, Vector extensions) {
        for (int i = 0; i < extensions.size(); ++i) {
            if (!((ExtensionNamespaceSupport)extensions.get(i)).getNamespace().equals(namespace)) continue;
            return i;
        }
        return -1;
    }

    public Vector getExtensions() {
        return this.m_extensions;
    }

    public void registerUnregisteredNamespaces() {
        for (int i = 0; i < this.m_unregisteredExtensions.size(); ++i) {
            String ns = (String)this.m_unregisteredExtensions.get(i);
            ExtensionNamespaceSupport extNsSpt = this.defineJavaNamespace(ns);
            if (extNsSpt == null) continue;
            this.m_extensions.add(extNsSpt);
        }
    }

    public ExtensionNamespaceSupport defineJavaNamespace(String ns) {
        return this.defineJavaNamespace(ns, ns);
    }

    public ExtensionNamespaceSupport defineJavaNamespace(String ns, String classOrPackage) {
        int lastSlash;
        if (null == ns || ns.trim().length() == 0) {
            return null;
        }
        String className = classOrPackage;
        if (className.startsWith("class:")) {
            className = className.substring(6);
        }
        if (-1 != (lastSlash = className.lastIndexOf(47))) {
            className = className.substring(lastSlash + 1);
        }
        if (null == className || className.trim().length() == 0) {
            return null;
        }
        try {
            ExtensionHandler.getClassForName(className);
            return new ExtensionNamespaceSupport(ns, "org.apache.xalan.extensions.ExtensionHandlerJavaClass", new Object[]{ns, "javaclass", className});
        }
        catch (ClassNotFoundException e) {
            return new ExtensionNamespaceSupport(ns, "org.apache.xalan.extensions.ExtensionHandlerJavaPackage", new Object[]{ns, "javapackage", className + "."});
        }
    }

    private void setPredefinedNamespaces() {
        String uri = "http://xml.apache.org/xalan/java";
        String handlerClassName = "org.apache.xalan.extensions.ExtensionHandlerJavaPackage";
        String lang = "javapackage";
        String lib = "";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xslt/java";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xsl.lotus.com/java";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xalan";
        handlerClassName = "org.apache.xalan.extensions.ExtensionHandlerJavaClass";
        lang = "javaclass";
        lib = "org.apache.xalan.lib.Extensions";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xslt";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xalan/redirect";
        lib = "org.apache.xalan.lib.Redirect";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xalan/PipeDocument";
        lib = "org.apache.xalan.lib.PipeDocument";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://xml.apache.org/xalan/sql";
        lib = "org.apache.xalan.lib.sql.XConnection";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/common";
        lib = "org.apache.xalan.lib.ExsltCommon";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/math";
        lib = "org.apache.xalan.lib.ExsltMath";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/sets";
        lib = "org.apache.xalan.lib.ExsltSets";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/dates-and-times";
        lib = "org.apache.xalan.lib.ExsltDatetime";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/dynamic";
        lib = "org.apache.xalan.lib.ExsltDynamic";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
        uri = "http://exslt.org/strings";
        lib = "org.apache.xalan.lib.ExsltStrings";
        this.m_predefExtensions.add(new ExtensionNamespaceSupport(uri, handlerClassName, new Object[]{uri, lang, lib}));
    }
}

