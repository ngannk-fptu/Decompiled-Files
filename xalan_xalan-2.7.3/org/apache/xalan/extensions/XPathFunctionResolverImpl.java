/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import org.apache.xalan.extensions.ExtensionHandler;
import org.apache.xalan.extensions.ExtensionHandlerJavaClass;
import org.apache.xalan.extensions.XPathFunctionImpl;
import org.apache.xalan.res.XSLMessages;

public class XPathFunctionResolverImpl
implements XPathFunctionResolver {
    @Override
    public XPathFunction resolveFunction(QName qname, int arity) {
        int lastSlash;
        if (qname == null) {
            throw new NullPointerException(XSLMessages.createMessage("ER_XPATH_RESOLVER_NULL_QNAME", null));
        }
        if (arity < 0) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_XPATH_RESOLVER_NEGATIVE_ARITY", null));
        }
        String uri = qname.getNamespaceURI();
        if (uri == null || uri.length() == 0) {
            return null;
        }
        String className = null;
        String methodName = null;
        if (uri.startsWith("http://exslt.org")) {
            className = this.getEXSLTClassName(uri);
            methodName = qname.getLocalPart();
        } else if (!uri.equals("http://xml.apache.org/xalan/java") && -1 != (lastSlash = className.lastIndexOf(47))) {
            className = className.substring(lastSlash + 1);
        }
        String localPart = qname.getLocalPart();
        int lastDotIndex = localPart.lastIndexOf(46);
        if (lastDotIndex > 0) {
            className = className != null ? className + "." + localPart.substring(0, lastDotIndex) : localPart.substring(0, lastDotIndex);
            methodName = localPart.substring(lastDotIndex + 1);
        } else {
            methodName = localPart;
        }
        if (null == className || className.trim().length() == 0 || null == methodName || methodName.trim().length() == 0) {
            return null;
        }
        ExtensionHandlerJavaClass handler = null;
        try {
            ExtensionHandler.getClassForName(className);
            handler = new ExtensionHandlerJavaClass(uri, "javaclass", className);
        }
        catch (ClassNotFoundException e) {
            return null;
        }
        return new XPathFunctionImpl(handler, methodName);
    }

    private String getEXSLTClassName(String uri) {
        if (uri.equals("http://exslt.org/math")) {
            return "org.apache.xalan.lib.ExsltMath";
        }
        if (uri.equals("http://exslt.org/sets")) {
            return "org.apache.xalan.lib.ExsltSets";
        }
        if (uri.equals("http://exslt.org/strings")) {
            return "org.apache.xalan.lib.ExsltStrings";
        }
        if (uri.equals("http://exslt.org/dates-and-times")) {
            return "org.apache.xalan.lib.ExsltDatetime";
        }
        if (uri.equals("http://exslt.org/dynamic")) {
            return "org.apache.xalan.lib.ExsltDynamic";
        }
        if (uri.equals("http://exslt.org/common")) {
            return "org.apache.xalan.lib.ExsltCommon";
        }
        return null;
    }
}

