/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.xpath;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.internal.SystemProperty;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public abstract class XPath
implements Serializable {
    private static final long serialVersionUID = 200L;
    private static final String XPATH_CLASS_PROPERTY = "org.jdom2.xpath.class";
    private static final String DEFAULT_XPATH_CLASS = "org.jdom2.xpath.jaxen.JDOMXPath";
    public static final String JDOM_OBJECT_MODEL_URI = "http://jdom.org/jaxp/xpath/jdom";
    private static Constructor<? extends XPath> constructor = null;

    public static XPath newInstance(String path) throws JDOMException {
        try {
            if (constructor == null) {
                String className;
                try {
                    className = SystemProperty.get(XPATH_CLASS_PROPERTY, DEFAULT_XPATH_CLASS);
                }
                catch (SecurityException ex1) {
                    className = DEFAULT_XPATH_CLASS;
                }
                Class<?> useclass = Class.forName(className);
                if (!XPath.class.isAssignableFrom(useclass)) {
                    throw new JDOMException("Unable to create a JDOMXPath from class '" + className + "'.");
                }
                XPath.setXPathClass(useclass);
            }
            return constructor.newInstance(path);
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

    public static void setXPathClass(Class<? extends XPath> aClass) throws JDOMException {
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

    public abstract List<?> selectNodes(Object var1) throws JDOMException;

    public abstract Object selectSingleNode(Object var1) throws JDOMException;

    public abstract String valueOf(Object var1) throws JDOMException;

    public abstract Number numberValueOf(Object var1) throws JDOMException;

    public abstract void setVariable(String var1, Object var2);

    public abstract void addNamespace(Namespace var1);

    public void addNamespace(String prefix, String uri) {
        this.addNamespace(Namespace.getNamespace(prefix, uri));
    }

    public abstract String getXPath();

    public static List<?> selectNodes(Object context, String path) throws JDOMException {
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
        private static final long serialVersionUID = 200L;
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

