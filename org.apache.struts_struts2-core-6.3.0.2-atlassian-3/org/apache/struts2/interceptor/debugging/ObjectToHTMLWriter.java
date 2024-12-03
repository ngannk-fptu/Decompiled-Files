/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor.debugging;

import com.opensymphony.xwork2.util.reflection.ReflectionException;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import java.beans.IntrospectionException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.interceptor.debugging.PrettyPrintWriter;

class ObjectToHTMLWriter {
    private static final Logger LOG = LogManager.getLogger(ObjectToHTMLWriter.class);
    private PrettyPrintWriter prettyWriter;

    ObjectToHTMLWriter(Writer writer) {
        this.prettyWriter = new PrettyPrintWriter(writer);
        this.prettyWriter.setEscape(false);
    }

    public void write(ReflectionProvider reflectionProvider, Object root, String expr) throws IntrospectionException, ReflectionException {
        this.prettyWriter.startNode("table");
        this.prettyWriter.addAttribute("class", "debugTable");
        if (root == null) {
            LOG.info("Root is null");
            this.writeProperty("root", null, expr);
        } else if (root instanceof Map) {
            LOG.info("Root is a Map");
            Iterator iterator = ((Map)root).entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry next;
                Map.Entry property = next = iterator.next();
                String key = property.getKey().toString();
                Object value = property.getValue();
                this.writeProperty(key, value, expr);
            }
        } else if (root instanceof List) {
            LOG.info("Root is a List");
            List list = (List)root;
            for (int i = 0; i < list.size(); ++i) {
                Object element = list.get(i);
                this.writeProperty(String.valueOf(i), element, expr);
            }
        } else if (root instanceof Set) {
            LOG.info("Root is a Set");
            Set set = (Set)root;
            for (Object next : set) {
                this.writeProperty("", next, expr);
            }
        } else if (root.getClass().isArray()) {
            LOG.info("Root is an Array");
            Object[] objects = (Object[])root;
            for (int i = 0; i < objects.length; ++i) {
                this.writeProperty(String.valueOf(i), objects[i], expr);
            }
        } else {
            LOG.info("Root is {}", root.getClass());
            Map<String, Object> properties = reflectionProvider.getBeanMap(root);
            for (Map.Entry<String, Object> property : properties.entrySet()) {
                String name = property.getKey();
                Object value = property.getValue();
                if ("class".equals(name)) continue;
                this.writeProperty(name, value, expr);
            }
        }
        this.prettyWriter.endNode();
    }

    private void writeProperty(String name, Object value, String expr) {
        this.prettyWriter.startNode("tr");
        this.prettyWriter.startNode("td");
        this.prettyWriter.addAttribute("class", "nameColumn");
        this.prettyWriter.setValue(name);
        this.prettyWriter.endNode();
        this.prettyWriter.startNode("td");
        if (value != null) {
            LOG.info("Writing property [{}] as [{}]", (Object)name, value);
            if (this.isEmptyCollection(value) || this.isEmptyMap(value) || value.getClass().isArray() && ((Object[])value).length == 0) {
                this.prettyWriter.addAttribute("class", "emptyCollection");
                this.prettyWriter.setValue("empty");
            } else {
                this.prettyWriter.addAttribute("class", "valueColumn");
                this.writeValue(name, value, expr);
            }
        } else {
            this.prettyWriter.addAttribute("class", "nullValue");
            this.prettyWriter.setValue("null");
        }
        this.prettyWriter.endNode();
        this.prettyWriter.startNode("td");
        if (value != null) {
            this.prettyWriter.addAttribute("class", "typeColumn");
            Class<?> clazz = value.getClass();
            this.prettyWriter.setValue(clazz.getName());
        } else {
            this.prettyWriter.addAttribute("class", "nullValue");
            this.prettyWriter.setValue("unknown");
        }
        this.prettyWriter.endNode();
        this.prettyWriter.endNode();
    }

    private boolean isEmptyMap(Object value) {
        try {
            return value instanceof Map && ((Map)value).isEmpty();
        }
        catch (Exception e) {
            return true;
        }
    }

    private boolean isEmptyCollection(Object value) {
        try {
            return value instanceof Collection && ((Collection)value).isEmpty();
        }
        catch (Exception e) {
            return true;
        }
    }

    private void writeValue(String name, Object value, String expr) {
        Class<?> clazz = value.getClass();
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz) || clazz.equals(String.class) || Boolean.class.equals(clazz)) {
            this.prettyWriter.setValue(String.valueOf(value));
        } else {
            this.prettyWriter.startNode("a");
            String path = expr.replaceAll("#", "%23") + "[\"" + name.replaceAll("#", "%23") + "\"]";
            this.prettyWriter.addAttribute("onclick", "expand(this, '" + path + "')");
            this.prettyWriter.addAttribute("href", "javascript://nop/");
            this.prettyWriter.setValue("Expand");
            this.prettyWriter.endNode();
        }
    }
}

