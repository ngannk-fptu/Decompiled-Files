/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.tika.config.Initializable;
import org.apache.tika.config.InitializableProblemHandler;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public abstract class ConfigBase {
    protected static <T> T buildSingle(String itemName, Class<T> itemClass, InputStream is) throws TikaConfigException, IOException {
        Element properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (SAXException e) {
            throw new IOException(e);
        }
        catch (TikaException e) {
            throw new TikaConfigException("problem loading xml to dom", e);
        }
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        return ConfigBase.buildSingle(itemName, itemClass, properties, null);
    }

    protected static <T> T buildSingle(String itemName, Class<T> itemClass, Element properties, T defaultValue) throws TikaConfigException, IOException {
        NodeList children = properties.getChildNodes();
        T toConfigure = null;
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() != 1 || !itemName.equals(child.getLocalName())) continue;
            if (toConfigure != null) {
                throw new TikaConfigException("There can only be one " + itemName + " in a config");
            }
            T item = ConfigBase.buildClass(child, itemName, itemClass);
            ConfigBase.setParams(item, child, new HashSet<String>());
            toConfigure = item;
        }
        if (toConfigure == null) {
            if (defaultValue == null) {
                throw new TikaConfigException("could not find " + itemName);
            }
            return defaultValue;
        }
        return toConfigure;
    }

    protected static <P, T> P buildComposite(String compositeElementName, Class<P> compositeClass, String itemName, Class<T> itemClass, InputStream is) throws TikaConfigException, IOException {
        Element properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (SAXException e) {
            throw new IOException(e);
        }
        catch (TikaException e) {
            throw new TikaConfigException("problem loading xml to dom", e);
        }
        return ConfigBase.buildComposite(compositeElementName, compositeClass, itemName, itemClass, properties);
    }

    protected static <P, T> P buildComposite(String compositeElementName, Class<P> compositeClass, String itemName, Class<T> itemClass, Element properties) throws TikaConfigException, IOException {
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        NodeList children = properties.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() != 1 || !compositeElementName.equals(child.getLocalName())) continue;
            List<T> components = ConfigBase.loadComposite(child, itemName, itemClass);
            Constructor<P> constructor = null;
            try {
                constructor = compositeClass.getConstructor(List.class);
                P composite = constructor.newInstance(components);
                ConfigBase.setParams(composite, child, new HashSet<String>(), itemName);
                return composite;
            }
            catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                throw new TikaConfigException("can't build composite class", e);
            }
        }
        throw new TikaConfigException("could not find " + compositeElementName);
    }

    private static <T> List<T> loadComposite(Node composite, String itemName, Class<? extends T> itemClass) throws TikaConfigException {
        NodeList children = composite.getChildNodes();
        ArrayList<T> items = new ArrayList<T>();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (child.getNodeType() != 1 || !itemName.equals(child.getLocalName())) continue;
            T item = ConfigBase.buildClass(child, itemName, itemClass);
            ConfigBase.setParams(item, child, new HashSet<String>());
            items.add(item);
        }
        return items;
    }

    private static <T> T buildClass(Node node, String elementName, Class itemClass) throws TikaConfigException {
        String className = itemClass.getName();
        Node classNameNode = node.getAttributes().getNamedItem("class");
        if (classNameNode != null) {
            className = classNameNode.getTextContent();
        }
        try {
            Class<?> clazz = Class.forName(className);
            if (!itemClass.isAssignableFrom(clazz)) {
                throw new TikaConfigException(elementName + " with class name " + className + " must be of type '" + itemClass.getName() + "'");
            }
            return (T)clazz.newInstance();
        }
        catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new TikaConfigException("problem loading " + elementName, e);
        }
    }

    private static void setParams(Object object, Node targetNode, Set<String> settings) throws TikaConfigException {
        ConfigBase.setParams(object, targetNode, settings, null);
    }

    private static void setParams(Object object, Node targetNode, Set<String> settings, String exceptNodeName) throws TikaConfigException {
        int i;
        NodeList children = targetNode.getChildNodes();
        NodeList params = null;
        for (i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if ("params".equals(child.getLocalName())) {
                params = child.getChildNodes();
                continue;
            }
            if (child.getNodeType() != 1 || child.getLocalName().equals(exceptNodeName)) continue;
            String itemName = child.getLocalName();
            String setter = "set" + itemName.substring(0, 1).toUpperCase(Locale.US) + itemName.substring(1);
            Class<?> itemClass = null;
            Method setterMethod = null;
            for (Method method : object.getClass().getMethods()) {
                Class<?>[] classes;
                if (!setter.equals(method.getName()) || (classes = method.getParameterTypes()).length != 1) continue;
                itemClass = classes[0];
                setterMethod = method;
                break;
            }
            if (itemClass == null) {
                throw new TikaConfigException("Couldn't find setter '" + setter + "' for " + itemName);
            }
            Object item = ConfigBase.buildClass(child, itemName, itemClass);
            ConfigBase.setParams(itemClass.cast(item), child, new HashSet<String>());
            try {
                setterMethod.invoke(object, item);
                continue;
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                throw new TikaConfigException("problem creating " + itemName, e);
            }
        }
        if (params != null) {
            for (i = 0; i < params.getLength(); ++i) {
                String localName;
                Node param = params.item(i);
                if (param.getNodeType() != 1 || (localName = param.getLocalName()) == null || localName.equals(exceptNodeName)) continue;
                String txt = param.getTextContent();
                if (ConfigBase.hasChildNodes(param)) {
                    if (ConfigBase.isMap(param)) {
                        ConfigBase.tryToSetMap(object, param);
                    } else {
                        ConfigBase.tryToSetList(object, param);
                    }
                } else {
                    ConfigBase.tryToSet(object, localName, txt);
                }
                if (txt == null) continue;
                settings.add(localName);
            }
        }
        if (object instanceof Initializable) {
            ((Initializable)object).initialize(Collections.EMPTY_MAP);
            ((Initializable)object).checkInitialization(InitializableProblemHandler.THROW);
        }
    }

    private static boolean hasChildNodes(Node param) {
        if (!param.hasChildNodes()) {
            return false;
        }
        NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node item = nodeList.item(i);
            if (item.getNodeType() != 1) continue;
            return true;
        }
        return false;
    }

    private static void tryToSetList(Object object, Node param) throws TikaConfigException {
        String name = param.getLocalName();
        ArrayList<String> strings = new ArrayList<String>();
        NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            String txt;
            Node n = nodeList.item(i);
            if (n.getNodeType() != 1 || (txt = n.getTextContent()) == null) continue;
            strings.add(txt);
        }
        String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        try {
            Method m = object.getClass().getMethod(setter, List.class);
            m.invoke(object, strings);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new TikaConfigException("can't set " + name, e);
        }
    }

    private static void tryToSetMap(Object object, Node param) throws TikaConfigException {
        String name = param.getLocalName();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node n = nodeList.item(i);
            if (n.getNodeType() != 1) continue;
            NamedNodeMap m = n.getAttributes();
            String key = null;
            String value = null;
            if (m.getNamedItem("from") != null) {
                key = m.getNamedItem("from").getTextContent();
            } else if (m.getNamedItem("key") != null) {
                key = m.getNamedItem("key").getTextContent();
            }
            if (m.getNamedItem("to") != null) {
                value = m.getNamedItem("to").getTextContent();
            } else if (m.getNamedItem("value") != null) {
                value = m.getNamedItem("value").getTextContent();
            }
            if (key == null) {
                throw new TikaConfigException("must specify a 'key' or 'from' value in a map object : " + param);
            }
            if (value == null) {
                throw new TikaConfigException("must specify a 'value' or 'to' value in a map object : " + param);
            }
            map.put(key, value);
        }
        String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        try {
            Method m = object.getClass().getMethod(setter, Map.class);
            m.invoke(object, map);
        }
        catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new TikaConfigException("can't set " + name, e);
        }
    }

    private static boolean isMap(Node param) {
        NodeList nodeList = param.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node n = nodeList.item(i);
            if (n.getNodeType() != 1 || !n.hasAttributes() || n.getAttributes().getNamedItem("from") == null || n.getAttributes().getNamedItem("to") == null) continue;
            return true;
        }
        return false;
    }

    private static void tryToSet(Object object, String name, String value) throws TikaConfigException {
        Class[] types;
        String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
        for (Class t : types = new Class[]{String.class, Boolean.TYPE, Long.TYPE, Integer.TYPE, Double.TYPE, Float.TYPE}) {
            try {
                Method m = object.getClass().getMethod(setter, t);
                if (t == Integer.TYPE) {
                    try {
                        m.invoke(object, Integer.parseInt(value));
                        return;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Long.TYPE) {
                    try {
                        m.invoke(object, Long.parseLong(value));
                        return;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Boolean.TYPE) {
                    try {
                        m.invoke(object, Boolean.parseBoolean(value));
                        return;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Float.TYPE) {
                    try {
                        m.invoke(object, Float.valueOf(Float.parseFloat(value)));
                        return;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                if (t == Double.TYPE) {
                    try {
                        m.invoke(object, Double.parseDouble(value));
                        return;
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("bad parameter " + setter, e);
                    }
                }
                try {
                    m.invoke(object, value);
                    return;
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    throw new TikaConfigException("bad parameter " + setter, e);
                }
            }
            catch (NoSuchMethodException noSuchMethodException) {
            }
        }
        throw new TikaConfigException("Couldn't find setter: " + setter + " for object " + object.getClass());
    }

    protected void handleSettings(Set<String> settings) {
    }

    protected Set<String> configure(String nodeName, InputStream is) throws TikaConfigException, IOException {
        HashSet<String> settings = new HashSet<String>();
        Element properties = null;
        try {
            properties = XMLReaderUtils.buildDOM(is).getDocumentElement();
        }
        catch (SAXException e) {
            throw new IOException(e);
        }
        catch (TikaException e) {
            throw new TikaConfigException("problem loading xml to dom", e);
        }
        if (!properties.getLocalName().equals("properties")) {
            throw new TikaConfigException("expect properties as root node");
        }
        NodeList children = properties.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if (!nodeName.equals(child.getLocalName())) continue;
            ConfigBase.setParams(this, child, settings);
        }
        return settings;
    }
}

