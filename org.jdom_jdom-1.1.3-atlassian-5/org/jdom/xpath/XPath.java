/*
 * Decompiled with CFR 0.152.
 */
package org.jdom.xpath;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.jdom.JDOMException;
import org.jdom.Namespace;

public abstract class XPath
implements Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: XPath.java,v $ $Revision: 1.17 $ $Date: 2007/11/10 05:29:02 $ $Name:  $";
    private static final String XPATH_CLASS_PROPERTY = "org.jdom.xpath.class";
    private static final String DEFAULT_XPATH_CLASS = "org.jdom.xpath.JaxenXPath";
    public static final String JDOM_OBJECT_MODEL_URI = "http://jdom.org/jaxp/xpath/jdom";
    private static Constructor constructor = null;

    public static XPath newInstance(String path) throws JDOMException {
        try {
            if (constructor == null) {
                String className;
                try {
                    className = System.getProperty(XPATH_CLASS_PROPERTY, DEFAULT_XPATH_CLASS);
                }
                catch (SecurityException ex1) {
                    className = DEFAULT_XPATH_CLASS;
                }
                XPath.setXPathClass(Class.forName(className));
            }
            return (XPath)constructor.newInstance(path);
        }
        catch (JDOMException ex1) {
            throw ex1;
        }
        catch (InvocationTargetException ex2) {
            Throwable t = ex2.getTargetException();
            throw t instanceof JDOMException ? (JDOMException)t : new JDOMException(t.toString(), t);
        }
        catch (Exception ex3) {
            throw new JDOMException(ex3.toString(), ex3);
        }
    }

    public static void setXPathClass(Class aClass) throws JDOMException {
        if (aClass == null) {
            throw new IllegalArgumentException("aClass");
        }
        try {
            if (!XPath.class.isAssignableFrom(aClass) || Modifier.isAbstract(aClass.getModifiers())) {
                throw new JDOMException(aClass.getName() + " is not a concrete JDOM XPath implementation");
            }
            constructor = aClass.getConstructor(String.class);
        }
        catch (JDOMException ex1) {
            throw ex1;
        }
        catch (Exception ex2) {
            throw new JDOMException(ex2.toString(), ex2);
        }
    }

    public abstract List selectNodes(Object var1) throws JDOMException;

    public abstract Object selectSingleNode(Object var1) throws JDOMException;

    public abstract String valueOf(Object var1) throws JDOMException;

    public abstract Number numberValueOf(Object var1) throws JDOMException;

    public abstract void setVariable(String var1, Object var2);

    public abstract void addNamespace(Namespace var1);

    public void addNamespace(String prefix, String uri) {
        this.addNamespace(Namespace.getNamespace(prefix, uri));
    }

    public abstract String getXPath();

    public static List selectNodes(Object context, String path) throws JDOMException {
        return XPath.newInstance(path).selectNodes(context);
    }

    public static Object selectSingleNode(Object context, String path) throws JDOMException {
        return XPath.newInstance(path).selectSingleNode(context);
    }

    protected final Object writeReplace() throws ObjectStreamException {
        return new XPathString(this.getXPath());
    }

    private static final class XPathString
    implements Serializable {
        private String xPath = null;

        public XPathString(String xpath) {
            this.xPath = xpath;
        }

        private Object readResolve() throws ObjectStreamException {
            try {
                return XPath.newInstance(this.xPath);
            }
            catch (JDOMException ex1) {
                throw new InvalidObjectException("Can't create XPath object for expression \"" + this.xPath + "\": " + ex1.toString());
            }
        }
    }
}

