/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.xml;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bedework.util.misc.Logged;
import org.bedework.util.xml.FromXmlCallback;
import org.bedework.util.xml.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class FromXml
extends Logged {
    private FromXmlCallback cb;

    public <T> T fromXml(InputStream is, Class<T> cl, FromXmlCallback cb) throws SAXException {
        try {
            Document doc = this.parseXml(is);
            return this.fromXml(doc.getDocumentElement(), cl, cb);
        }
        catch (SAXException se) {
            if (this.debug) {
                this.error(se);
            }
            throw se;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new SAXException(t.getMessage());
        }
    }

    public <T> T fromXml(Element rootEl, Class<T> cl, FromXmlCallback cb) throws SAXException {
        try {
            this.cb = cb == null ? new FromXmlCallback() : cb;
            Object o = this.fromClass(cl);
            if (o == null) {
                return null;
            }
            for (Element el : XmlUtil.getElementsArray(rootEl)) {
                this.populate(el, o, null, null);
            }
            return (T)o;
        }
        catch (SAXException se) {
            if (this.debug) {
                this.error(se);
            }
            throw se;
        }
        catch (Throwable t) {
            if (this.debug) {
                this.error(t);
            }
            throw new SAXException(t.getMessage());
        }
    }

    public Document parseXml(InputStream is) throws Throwable {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(is));
        if (doc == null) {
            return null;
        }
        return doc;
    }

    private Object fromClass(Class cl) throws Throwable {
        if (cl == null) {
            this.error("Must supply a class or have type attribute");
            return null;
        }
        return cl.newInstance();
    }

    private void populate(Element subroot, Object o, Collection<Object> col, Class cl) throws Throwable {
        Object val;
        if (this.cb.skipElement(subroot)) {
            return;
        }
        Method meth = null;
        Class<?> elClass = this.cb.forElement(subroot);
        if (col == null) {
            meth = this.findSetter(o, subroot);
            if (meth == null) {
                this.error("No setter for " + subroot);
                return;
            }
            Class<?>[] parClasses = meth.getParameterTypes();
            if (parClasses.length != 1) {
                this.error("Invalid setter method " + subroot);
                throw new SAXException("Invalid setter method " + subroot);
            }
            elClass = parClasses[0];
        } else if (cl != null) {
            elClass = cl;
        }
        if (elClass == null) {
            this.error("No class for element " + subroot);
            return;
        }
        if (!XmlUtil.hasChildren(subroot)) {
            val = this.simpleValue(elClass, subroot);
            if (val == null) {
                this.error("Unsupported par class " + elClass + " for field " + subroot);
                throw new SAXException("Unsupported par class " + elClass + " for field " + subroot);
            }
            this.assign(val, subroot, col, o, meth);
            return;
        }
        if (Collection.class.isAssignableFrom(elClass)) {
            TreeSet<Object> colVal;
            if (elClass.getName().equals("java.util.Set")) {
                colVal = new TreeSet();
            } else if (elClass.getName().equals("java.util.List")) {
                colVal = new ArrayList();
            } else if (elClass.getName().equals("java.util.Collection")) {
                colVal = new ArrayList();
            } else {
                this.error("Unsupported element class " + elClass + " for field " + subroot);
                return;
            }
            this.assign(colVal, subroot, col, o, meth);
            Type[] gpts = meth.getGenericParameterTypes();
            if (gpts.length != 1) {
                this.error("Unsupported type " + elClass + " with name " + subroot);
                return;
            }
            Type gpt = gpts[0];
            if (!(gpt instanceof ParameterizedType)) {
                this.error("Unsupported type " + elClass + " with name " + subroot);
                return;
            }
            ParameterizedType aType = (ParameterizedType)gpt;
            Type[] parameterArgTypes = aType.getActualTypeArguments();
            if (parameterArgTypes.length != 1) {
                this.error("Unsupported type " + elClass + " with name " + subroot);
                return;
            }
            Type parameterArgType = parameterArgTypes[0];
            Class colElType = (Class)parameterArgType;
            for (Element el : XmlUtil.getElementsArray(subroot)) {
                this.populate(el, o, colVal, colElType);
            }
            return;
        }
        val = this.fromClass(elClass);
        this.assign(val, subroot, col, o, meth);
        for (Element el : XmlUtil.getElementsArray(subroot)) {
            this.populate(el, val, null, null);
        }
    }

    private Method findSetter(Object val, Element el) throws Throwable {
        String name = this.cb.getFieldlName(el);
        if (name == null) {
            name = el.getNodeName();
        }
        String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
        Method[] meths = val.getClass().getMethods();
        Method meth = null;
        for (Method m : meths) {
            if (!m.getName().equals(methodName)) continue;
            if (meth != null) {
                throw new SAXException("Multiple setters for field " + el);
            }
            meth = m;
        }
        if (meth == null) {
            this.error("No setter method for property " + el + " for class " + val.getClass().getName());
            return null;
        }
        return meth;
    }

    private void assign(Object val, Element el, Collection<Object> col, Object o, Method meth) throws Throwable {
        if (col != null) {
            col.add(val);
        } else if (!this.cb.save(el, o, val)) {
            Object[] pars = new Object[]{val};
            meth.invoke(o, pars);
        }
    }

    private Object simpleValue(Class cl, Element el) throws Throwable {
        if (!XmlUtil.hasChildren(el)) {
            String ndval = XmlUtil.getElementContent(el);
            if (cl.getName().equals("java.lang.String")) {
                return ndval;
            }
            if (cl.getName().equals("int") || cl.getName().equals("java.lang.Integer")) {
                return Integer.valueOf(ndval);
            }
            if (cl.getName().equals("long") || cl.getName().equals("java.lang.Long")) {
                return Long.valueOf(ndval);
            }
            if (cl.getName().equals("boolean") || cl.getName().equals("java.lang.Boolean")) {
                return Boolean.valueOf(ndval);
            }
            return this.cb.simpleValue(cl, ndval);
        }
        return null;
    }
}

