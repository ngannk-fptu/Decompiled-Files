/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.extensions;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.apache.xalan.res.XSLMessages;

public class ExtensionNamespaceContext
implements NamespaceContext {
    public static final String EXSLT_PREFIX = "exslt";
    public static final String EXSLT_URI = "http://exslt.org/common";
    public static final String EXSLT_MATH_PREFIX = "math";
    public static final String EXSLT_MATH_URI = "http://exslt.org/math";
    public static final String EXSLT_SET_PREFIX = "set";
    public static final String EXSLT_SET_URI = "http://exslt.org/sets";
    public static final String EXSLT_STRING_PREFIX = "str";
    public static final String EXSLT_STRING_URI = "http://exslt.org/strings";
    public static final String EXSLT_DATETIME_PREFIX = "datetime";
    public static final String EXSLT_DATETIME_URI = "http://exslt.org/dates-and-times";
    public static final String EXSLT_DYNAMIC_PREFIX = "dyn";
    public static final String EXSLT_DYNAMIC_URI = "http://exslt.org/dynamic";
    public static final String JAVA_EXT_PREFIX = "java";
    public static final String JAVA_EXT_URI = "http://xml.apache.org/xalan/java";

    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_NAMESPACE_CONTEXT_NULL_PREFIX", null));
        }
        if (prefix.equals("")) {
            return "";
        }
        if (prefix.equals("xml")) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        if (prefix.equals("xmlns")) {
            return "http://www.w3.org/2000/xmlns/";
        }
        if (prefix.equals(EXSLT_PREFIX)) {
            return EXSLT_URI;
        }
        if (prefix.equals(EXSLT_MATH_PREFIX)) {
            return EXSLT_MATH_URI;
        }
        if (prefix.equals(EXSLT_SET_PREFIX)) {
            return EXSLT_SET_URI;
        }
        if (prefix.equals(EXSLT_STRING_PREFIX)) {
            return EXSLT_STRING_URI;
        }
        if (prefix.equals(EXSLT_DATETIME_PREFIX)) {
            return EXSLT_DATETIME_URI;
        }
        if (prefix.equals(EXSLT_DYNAMIC_PREFIX)) {
            return EXSLT_DYNAMIC_URI;
        }
        if (prefix.equals(JAVA_EXT_PREFIX)) {
            return JAVA_EXT_URI;
        }
        return "";
    }

    @Override
    public String getPrefix(String namespace) {
        if (namespace == null) {
            throw new IllegalArgumentException(XSLMessages.createMessage("ER_NAMESPACE_CONTEXT_NULL_NAMESPACE", null));
        }
        if (namespace.equals("http://www.w3.org/XML/1998/namespace")) {
            return "xml";
        }
        if (namespace.equals("http://www.w3.org/2000/xmlns/")) {
            return "xmlns";
        }
        if (namespace.equals(EXSLT_URI)) {
            return EXSLT_PREFIX;
        }
        if (namespace.equals(EXSLT_MATH_URI)) {
            return EXSLT_MATH_PREFIX;
        }
        if (namespace.equals(EXSLT_SET_URI)) {
            return EXSLT_SET_PREFIX;
        }
        if (namespace.equals(EXSLT_STRING_URI)) {
            return EXSLT_STRING_PREFIX;
        }
        if (namespace.equals(EXSLT_DATETIME_URI)) {
            return EXSLT_DATETIME_PREFIX;
        }
        if (namespace.equals(EXSLT_DYNAMIC_URI)) {
            return EXSLT_DYNAMIC_PREFIX;
        }
        if (namespace.equals(JAVA_EXT_URI)) {
            return JAVA_EXT_PREFIX;
        }
        return null;
    }

    public Iterator getPrefixes(String namespace) {
        final String result = this.getPrefix(namespace);
        return new Iterator(){
            private boolean isFirstIteration;
            {
                this.isFirstIteration = result != null;
            }

            @Override
            public boolean hasNext() {
                return this.isFirstIteration;
            }

            public Object next() {
                if (this.isFirstIteration) {
                    this.isFirstIteration = false;
                    return result;
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}

