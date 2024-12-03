/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import net.sf.ehcache.config.InvalidConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

final class BeanHandler
extends DefaultHandler {
    private static final Logger LOG = LoggerFactory.getLogger((String)BeanHandler.class.getName());
    private final Object bean;
    private ElementInfo element;
    private Locator locator;
    private String subtreeMatchingQname;
    private StringBuilder subtreeText;
    private Method subtreeMethod;

    public BeanHandler(Object bean) {
        this.bean = bean;
    }

    @Override
    public final void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    private String getTagPart(String qName) {
        String[] parts = qName.split(":");
        return parts[parts.length - 1];
    }

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        boolean subtreeAppend = this.extractingSubtree();
        if (this.extractingSubtree() || this.startExtractingSubtree(this.getTagPart(qName))) {
            if (subtreeAppend) {
                this.appendToSubtree("<" + qName);
            }
            for (int i = 0; i < attributes.getLength(); ++i) {
                String attrName = attributes.getQName(i);
                String attrValue = attributes.getValue(i);
                if (!subtreeAppend) continue;
                this.appendToSubtree(" " + attrName + "=\"" + attrValue + "\"");
            }
            if (subtreeAppend) {
                this.appendToSubtree(">");
            }
            this.element = new ElementInfo(this.element, qName, this.bean);
        } else {
            if (this.element == null) {
                this.element = new ElementInfo(qName, this.bean);
            } else {
                Object child = this.createChild(this.element, qName);
                this.element = new ElementInfo(this.element, qName, child);
            }
            for (int i = 0; i < attributes.getLength(); ++i) {
                String attrName = attributes.getQName(i);
                String attrValue = attributes.getValue(i);
                this.setAttribute(this.element, attrName, attrValue);
            }
        }
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        if (this.element.parent != null) {
            if (this.extractingSubtree()) {
                if (this.endsSubtree(this.getTagPart(qName))) {
                    this.endSubtree();
                } else {
                    this.appendToSubtree("</" + qName + ">");
                }
            } else {
                this.addChild(this.element.parent.bean, this.element.bean, qName);
            }
        }
        this.element = this.element.parent;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.extractingSubtree()) {
            this.appendToSubtree(ch, start, length);
        }
    }

    private Object createChild(ElementInfo parent, String name) throws SAXException {
        try {
            Class<?> parentClass = parent.bean.getClass();
            Method method = BeanHandler.findCreateMethod(parentClass, name);
            if (method != null) {
                return method.invoke(parent.bean, new Object[0]);
            }
            method = this.findSetMethod(parentClass, "add", name);
            if (method != null) {
                return BeanHandler.createInstance(parent.bean, method.getParameterTypes()[0]);
            }
        }
        catch (Exception e) {
            throw new SAXException(this.getLocation() + ": Could not create nested element <" + name + ">.", e);
        }
        throw new SAXException(this.getLocation() + ": Element <" + parent.elementName + "> does not allow nested <" + name + "> elements.");
    }

    private static Object createInstance(Object parent, Class childClass) throws Exception {
        Constructor<?>[] constructors = childClass.getDeclaredConstructors();
        ArrayList candidates = new ArrayList();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] params = constructor.getParameterTypes();
            if (params.length == 0) {
                candidates.add(constructor);
                continue;
            }
            if (params.length != 1 || !params[0].isInstance(parent)) continue;
            candidates.add(constructor);
        }
        switch (candidates.size()) {
            case 0: {
                throw new Exception("No constructor for class " + childClass.getName());
            }
            case 1: {
                break;
            }
            default: {
                throw new Exception("Multiple constructors for class " + childClass.getName());
            }
        }
        Constructor constructor = (Constructor)candidates.remove(0);
        constructor.setAccessible(true);
        if (constructor.getParameterTypes().length == 0) {
            return constructor.newInstance(new Object[0]);
        }
        return constructor.newInstance(parent);
    }

    private static Method findCreateMethod(Class objClass, String name) {
        Method[] methods;
        String methodName = BeanHandler.makeMethodName("create", name);
        for (Method method : methods = objClass.getMethods()) {
            if (!method.getName().equals(methodName) || Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 0 || method.getReturnType().isPrimitive() || method.getReturnType().isArray()) continue;
            return method;
        }
        return null;
    }

    private static String makeMethodName(String prefix, String name) {
        String rawName = prefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return rawName.replace("-", "");
    }

    private void setAttribute(ElementInfo element, String attrName, String attrValue) throws SAXException {
        try {
            Class<?> objClass = element.bean.getClass();
            Method method = this.chooseSetMethod(objClass, "set", attrName, String.class);
            if (method != null) {
                Object realValue = BeanHandler.convert(attrName, method.getParameterTypes()[0], attrValue);
                method.invoke(element.bean, realValue);
                return;
            }
            if (element.elementName.equals("ehcache")) {
                LOG.debug("Ignoring ehcache attribute {}", (Object)attrName);
                return;
            }
        }
        catch (InvocationTargetException e) {
            throw new SAXException(this.getLocation() + ": Could not set attribute \"" + attrName + "\".. Message was: " + e.getTargetException());
        }
        catch (Exception e) {
            throw new SAXException(this.getLocation() + ": Could not set attribute \"" + attrName + "\" - " + e.getMessage());
        }
        throw new SAXException(this.getLocation() + ": Element <" + element.elementName + "> does not allow attribute \"" + attrName + "\".");
    }

    private static Object convert(String attributeName, Class toClass, String value) throws Exception {
        if (value == null) {
            return null;
        }
        if (toClass.isInstance(value)) {
            return value;
        }
        if (toClass == Long.class || toClass == Long.TYPE) {
            return Long.decode(value);
        }
        if (toClass == Integer.class || toClass == Integer.TYPE) {
            return Integer.decode(value);
        }
        if (toClass == Boolean.class || toClass == Boolean.TYPE) {
            if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                return Boolean.valueOf(value);
            }
            throw new InvalidConfigurationException("Invalid value specified for attribute '" + attributeName + "', please use 'true' or 'false' instead of '" + value + "'");
        }
        throw new Exception("Cannot convert attribute value to class " + toClass.getName());
    }

    private Method chooseSetMethod(Class objClass, String prefix, String name, Class preferredParameterType) throws Exception {
        String methodName = BeanHandler.makeMethodName(prefix, name);
        Method[] methods = objClass.getMethods();
        HashSet<Method> candidates = new HashSet<Method>();
        for (Method method : methods) {
            if (!method.getName().equals(methodName) || Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 1 || !method.getReturnType().equals(Void.TYPE)) continue;
            candidates.add(method);
        }
        if (candidates.size() == 0) {
            return null;
        }
        if (candidates.size() == 1) {
            return (Method)candidates.iterator().next();
        }
        for (Method m : candidates) {
            if (!m.getParameterTypes()[0].equals(preferredParameterType)) continue;
            return m;
        }
        throw new Exception("Multiple " + methodName + "() methods found in class " + objClass.getName() + ", but not one with preferred parameter type - " + preferredParameterType.getName());
    }

    private Method findSetMethod(Class objClass, String prefix, String name) throws Exception {
        String methodName = BeanHandler.makeMethodName(prefix, name);
        Method[] methods = objClass.getMethods();
        Method candidate = null;
        for (Method method : methods) {
            if (!method.getName().equals(methodName) || Modifier.isStatic(method.getModifiers()) || method.getParameterTypes().length != 1 || !method.getReturnType().equals(Void.TYPE)) continue;
            if (candidate != null) {
                throw new Exception("Multiple " + methodName + "() methods in class " + objClass.getName() + ".");
            }
            candidate = method;
        }
        return candidate;
    }

    private void addChild(Object parent, Object child, String name) throws SAXException {
        try {
            Method method = this.findSetMethod(parent.getClass(), "add", name);
            if (method != null) {
                method.invoke(parent, child);
            }
        }
        catch (InvocationTargetException e) {
            SAXException exc = new SAXException(this.getLocation() + ": Could not finish element <" + name + ">. Message was: " + e.getTargetException());
            throw exc;
        }
        catch (Exception e) {
            throw new SAXException(this.getLocation() + ": Could not finish element <" + name + ">.");
        }
    }

    private String getLocation() {
        return this.locator.getSystemId() + ":" + this.locator.getLineNumber();
    }

    private boolean startExtractingSubtree(String name) throws SAXException {
        if (this.element == null || this.element.bean == null) {
            return false;
        }
        try {
            Method method = this.findSetMethod(this.element.bean.getClass(), "extract", name);
            if (method != null) {
                this.subtreeMatchingQname = name;
                this.subtreeText = new StringBuilder();
                this.subtreeMethod = method;
                return true;
            }
            return false;
        }
        catch (Exception e) {
            throw new SAXException(this.getLocation() + ": Error checking for extract method on <" + name + ">.");
        }
    }

    private boolean extractingSubtree() {
        return this.subtreeMatchingQname != null;
    }

    private void appendToSubtree(String text) {
        this.subtreeText.append(text);
    }

    private void appendToSubtree(char[] text, int start, int length) {
        this.subtreeText.append(text, start, length);
    }

    private boolean endsSubtree(String endName) {
        return this.subtreeMatchingQname != null && this.subtreeMatchingQname.equals(endName);
    }

    private void endSubtree() throws SAXException {
        try {
            this.subtreeMethod.invoke(this.element.parent.bean, this.subtreeText.toString());
        }
        catch (InvocationTargetException e) {
            throw new SAXException(this.getLocation() + ": Could not set extracted subtree \"" + this.subtreeMatchingQname + "\". Message was: " + e.getTargetException());
        }
        catch (Exception e) {
            throw new SAXException(this.getLocation() + ": Could not set extracted subtree \"" + this.subtreeMatchingQname + "\". Message was: " + e.getMessage());
        }
        this.subtreeMatchingQname = null;
        this.subtreeMethod = null;
        this.subtreeText = null;
    }

    private static final class ElementInfo {
        private final ElementInfo parent;
        private final String elementName;
        private final Object bean;

        public ElementInfo(String elementName, Object bean) {
            this.parent = null;
            this.elementName = elementName;
            this.bean = bean;
        }

        public ElementInfo(ElementInfo parent, String elementName, Object bean) {
            this.parent = parent;
            this.elementName = elementName;
            this.bean = bean;
        }
    }
}

