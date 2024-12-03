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
    private static Class[] SUPPORTED_PRIMITIVES = new Class[]{String.class, Boolean.TYPE, Long.TYPE, Integer.TYPE, Double.TYPE, Float.TYPE};

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
            throw new TikaConfigException("problem loading " + elementName + " with class " + itemClass.getName(), e);
        }
    }

    private static void setParams(Object object, Node targetNode, Set<String> settings) throws TikaConfigException {
        ConfigBase.setParams(object, targetNode, settings, null);
    }

    private static void setParams(Object object, Node targetNode, Set<String> settings, String exceptNodeName) throws TikaConfigException {
        int i;
        NodeList children = targetNode.getChildNodes();
        ArrayList<Node> params = new ArrayList<Node>();
        for (i = 0; i < children.getLength(); ++i) {
            Node child = children.item(i);
            if ("params".equals(child.getLocalName())) {
                NodeList paramsList = child.getChildNodes();
                for (int j = 0; j < paramsList.getLength(); ++j) {
                    params.add(paramsList.item(j));
                }
                continue;
            }
            params.add(child);
        }
        for (i = 0; i < params.size(); ++i) {
            String localName;
            Node param = (Node)params.get(i);
            if (param.getNodeType() != 1 || (localName = param.getLocalName()) == null || localName.equals(exceptNodeName)) continue;
            String txt = param.getTextContent();
            String itemName = param.getLocalName();
            SetterClassPair setterClassPair = ConfigBase.findSetterClassPair(object, itemName);
            boolean processed = false;
            if (!ConfigBase.hasClass(param)) {
                if (setterClassPair.itemClass.isAssignableFrom(Map.class) && ConfigBase.isMap(param)) {
                    ConfigBase.tryToSetMap(object, param);
                    processed = true;
                } else if (setterClassPair.itemClass.isAssignableFrom(List.class)) {
                    ConfigBase.tryToSetList(object, param);
                    processed = true;
                }
            }
            if (!processed) {
                if (ConfigBase.isPrimitive(setterClassPair.itemClass)) {
                    ConfigBase.tryToSetPrimitive(object, setterClassPair, param.getTextContent());
                } else {
                    Object item = ConfigBase.buildClass(param, itemName, setterClassPair.itemClass);
                    ConfigBase.setParams(setterClassPair.itemClass.cast(item), param, new HashSet<String>());
                    try {
                        setterClassPair.setterMethod.invoke(object, item);
                    }
                    catch (IllegalAccessException | InvocationTargetException e) {
                        throw new TikaConfigException("problem creating " + itemName, e);
                    }
                }
            }
            if (txt == null) continue;
            settings.add(localName);
        }
        if (object instanceof Initializable) {
            ((Initializable)object).initialize(Collections.EMPTY_MAP);
            ((Initializable)object).checkInitialization(InitializableProblemHandler.THROW);
        }
    }

    private static boolean isPrimitive(Class itemClass) {
        for (int i = 0; i < SUPPORTED_PRIMITIVES.length; ++i) {
            if (!SUPPORTED_PRIMITIVES[i].equals(itemClass)) continue;
            return true;
        }
        return false;
    }

    private static boolean hasClass(Node param) {
        return param.hasAttributes() && param.getAttributes().getNamedItem("class") != null;
    }

    private static SetterClassPair findSetterClassPair(Object object, String itemName) throws TikaConfigException {
        String setter = "set" + itemName.substring(0, 1).toUpperCase(Locale.US) + itemName.substring(1);
        Class<?> itemClass = null;
        Method setterMethod = null;
        for (Method method : object.getClass().getMethods()) {
            Class<?>[] classes;
            if (!setter.equals(method.getName()) || (classes = method.getParameterTypes()).length != 1 || itemClass != null && !classes[0].equals(String.class)) continue;
            itemClass = classes[0];
            setterMethod = method;
        }
        if (setterMethod != null && itemClass != null) {
            return new SetterClassPair(setterMethod, itemClass);
        }
        String adder = "add" + itemName.substring(0, 1).toUpperCase(Locale.US) + itemName.substring(1);
        for (Method method : object.getClass().getMethods()) {
            Class<?>[] classes;
            if (!adder.equals(method.getName()) || (classes = method.getParameterTypes()).length != 1 || itemClass != null && !classes[0].equals(String.class)) continue;
            itemClass = classes[0];
            setterMethod = method;
        }
        if (setterMethod == null && itemClass == null) {
            throw new TikaConfigException("Couldn't find setter '" + setter + "' or adder '" + adder + "' for " + itemName + " of class: " + object.getClass());
        }
        return new SetterClassPair(setterMethod, itemClass);
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
        if (ConfigBase.hasClass(param)) {
            ConfigBase.tryToSetClassList(object, param);
        } else {
            ConfigBase.tryToSetStringList(object, param);
        }
    }

    private static void tryToSetClassList(Object object, Node node) throws TikaConfigException {
        String name = node.getLocalName();
        try {
            Class<?> interfaze = Class.forName(node.getAttributes().getNamedItem("class").getTextContent());
            ArrayList items = new ArrayList();
            NodeList nodeList = node.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Node child = nodeList.item(i);
                if (child.getNodeType() != 1) continue;
                Object item = ConfigBase.buildClass(child, child.getLocalName(), interfaze);
                ConfigBase.setParams(item, child, new HashSet<String>());
                items.add(item);
            }
            String setter = "set" + name.substring(0, 1).toUpperCase(Locale.US) + name.substring(1);
            Method m = object.getClass().getMethod(setter, List.class);
            m.invoke(object, items);
        }
        catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new TikaConfigException("couldn't build class for " + name, e);
        }
    }

    private static void tryToSetStringList(Object object, Node param) throws TikaConfigException {
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
            } else if (m.getNamedItem("k") != null) {
                key = m.getNamedItem("k").getTextContent();
            }
            if (m.getNamedItem("to") != null) {
                value = m.getNamedItem("to").getTextContent();
            } else if (m.getNamedItem("value") != null) {
                value = m.getNamedItem("value").getTextContent();
            } else if (m.getNamedItem("v") != null) {
                value = m.getNamedItem("v").getTextContent();
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
            if (n.getNodeType() != 1 || !n.hasAttributes()) continue;
            if (n.getAttributes().getNamedItem("from") != null && n.getAttributes().getNamedItem("to") != null) {
                return true;
            }
            if (n.getAttributes().getNamedItem("k") == null || n.getAttributes().getNamedItem("v") == null) continue;
            return true;
        }
        return false;
    }

    private static void tryToSetPrimitive(Object object, SetterClassPair setterClassPair, String value) throws TikaConfigException {
        try {
            if (setterClassPair.itemClass == Integer.TYPE) {
                setterClassPair.setterMethod.invoke(object, Integer.parseInt(value));
            } else if (setterClassPair.itemClass == Long.TYPE) {
                setterClassPair.setterMethod.invoke(object, Long.parseLong(value));
            } else if (setterClassPair.itemClass == Float.TYPE) {
                setterClassPair.setterMethod.invoke(object, Float.valueOf(Float.parseFloat(value)));
            } else if (setterClassPair.itemClass == Double.TYPE) {
                setterClassPair.setterMethod.invoke(object, Double.parseDouble(value));
            } else if (setterClassPair.itemClass == Boolean.TYPE) {
                setterClassPair.setterMethod.invoke(object, Boolean.parseBoolean(value));
            } else {
                setterClassPair.setterMethod.invoke(object, value);
            }
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new TikaConfigException("bad parameter " + setterClassPair + " " + value, e);
        }
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

    private static class SetterClassPair {
        private final Method setterMethod;
        private final Class itemClass;

        public SetterClassPair(Method setterMethod, Class itemClass) {
            this.setterMethod = setterMethod;
            this.itemClass = itemClass;
        }

        public String toString() {
            return "SetterClassPair{setterMethod=" + this.setterMethod + ", itemClass=" + this.itemClass + '}';
        }
    }
}

