/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.IntrospectionUtils
 *  org.apache.tomcat.util.descriptor.web.ResourceBase
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.catalina.storeconfig;

import java.beans.IndexedPropertyDescriptor;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.Iterator;
import org.apache.catalina.storeconfig.StoreDescription;
import org.apache.tomcat.util.IntrospectionUtils;
import org.apache.tomcat.util.descriptor.web.ResourceBase;
import org.apache.tomcat.util.security.Escape;

public class StoreAppender {
    private static Class<?>[] persistables = new Class[]{String.class, Integer.class, Integer.TYPE, Boolean.class, Boolean.TYPE, Byte.class, Byte.TYPE, Character.class, Character.TYPE, Double.class, Double.TYPE, Float.class, Float.TYPE, Long.class, Long.TYPE, Short.class, Short.TYPE, InetAddress.class};
    private int pos = 0;

    public void printCloseTag(PrintWriter aWriter, StoreDescription aDesc) throws Exception {
        aWriter.print("</");
        aWriter.print(aDesc.getTag());
        aWriter.println(">");
    }

    public void printOpenTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        if (aDesc.isAttributes() && bean != null) {
            this.printAttributes(aWriter, indent, bean, aDesc);
        }
        aWriter.println(">");
    }

    public void printTag(PrintWriter aWriter, int indent, Object bean, StoreDescription aDesc) throws Exception {
        aWriter.print("<");
        aWriter.print(aDesc.getTag());
        if (aDesc.isAttributes() && bean != null) {
            this.printAttributes(aWriter, indent, bean, aDesc);
        }
        aWriter.println("/>");
    }

    public void printTagContent(PrintWriter aWriter, String tag, String content) throws Exception {
        aWriter.print("<");
        aWriter.print(tag);
        aWriter.print(">");
        aWriter.print(Escape.xml((String)content));
        aWriter.print("</");
        aWriter.print(tag);
        aWriter.println(">");
    }

    public void printTagValueArray(PrintWriter aWriter, String tag, int indent, String[] elements) {
        if (elements != null && elements.length > 0) {
            this.printIndent(aWriter, indent + 2);
            aWriter.print("<");
            aWriter.print(tag);
            aWriter.print(">");
            for (int i = 0; i < elements.length; ++i) {
                this.printIndent(aWriter, indent + 4);
                aWriter.print(elements[i]);
                if (i + 1 >= elements.length) continue;
                aWriter.println(",");
            }
            this.printIndent(aWriter, indent + 2);
            aWriter.print("</");
            aWriter.print(tag);
            aWriter.println(">");
        }
    }

    public void printTagArray(PrintWriter aWriter, String tag, int indent, String[] elements) throws Exception {
        if (elements != null) {
            for (String element : elements) {
                this.printIndent(aWriter, indent);
                this.printTagContent(aWriter, tag, element);
            }
        }
    }

    public void printIndent(PrintWriter aWriter, int indent) {
        for (int i = 0; i < indent; ++i) {
            aWriter.print(' ');
        }
        this.pos = indent;
    }

    public void printAttributes(PrintWriter writer, int indent, Object bean, StoreDescription desc) throws Exception {
        this.printAttributes(writer, indent, true, bean, desc);
    }

    public void printAttributes(PrintWriter writer, int indent, boolean include, Object bean, StoreDescription desc) throws Exception {
        PropertyDescriptor[] descriptors;
        if (include && !desc.isStandard()) {
            writer.print(" className=\"");
            writer.print(bean.getClass().getName());
            writer.print("\"");
        }
        if ((descriptors = Introspector.getBeanInfo(bean.getClass()).getPropertyDescriptors()) == null) {
            descriptors = new PropertyDescriptor[]{};
        }
        Object bean2 = this.defaultInstance(bean);
        for (PropertyDescriptor descriptor : descriptors) {
            Object value = this.checkAttribute(desc, descriptor, descriptor.getName(), bean, bean2);
            if (value == null) continue;
            this.printAttribute(writer, indent, bean, desc, descriptor.getName(), bean2, value);
        }
        if (bean instanceof ResourceBase) {
            ResourceBase resource = (ResourceBase)bean;
            Iterator iter = resource.listProperties();
            while (iter.hasNext()) {
                String name = (String)iter.next();
                Object value = resource.getProperty(name);
                if (!this.isPersistable(value.getClass()) || desc.isTransientAttribute(name)) continue;
                this.printValue(writer, indent, name, value);
            }
        }
    }

    protected Object checkAttribute(StoreDescription desc, PropertyDescriptor descriptor, String attributeName, Object bean, Object bean2) {
        if (descriptor instanceof IndexedPropertyDescriptor) {
            return null;
        }
        if (!this.isPersistable(descriptor.getPropertyType()) || descriptor.getReadMethod() == null || descriptor.getWriteMethod() == null) {
            return null;
        }
        if (desc.isTransientAttribute(descriptor.getName())) {
            return null;
        }
        Object value = IntrospectionUtils.getProperty((Object)bean, (String)descriptor.getName());
        if (value == null) {
            return null;
        }
        Object value2 = IntrospectionUtils.getProperty((Object)bean2, (String)descriptor.getName());
        if (value.equals(value2)) {
            return null;
        }
        return value;
    }

    protected void printAttribute(PrintWriter writer, int indent, Object bean, StoreDescription desc, String attributeName, Object bean2, Object value) {
        if (this.isPrintValue(bean, bean2, attributeName, desc)) {
            this.printValue(writer, indent, attributeName, value);
        }
    }

    public boolean isPrintValue(Object bean, Object bean2, String attrName, StoreDescription desc) {
        return true;
    }

    public Object defaultInstance(Object bean) throws ReflectiveOperationException {
        return bean.getClass().getConstructor(new Class[0]).newInstance(new Object[0]);
    }

    public void printValue(PrintWriter writer, int indent, String name, Object value) {
        if (value instanceof InetAddress) {
            value = ((InetAddress)value).getHostAddress();
        }
        if (!(value instanceof String)) {
            value = value.toString();
        }
        String strValue = Escape.xml((String)((String)value));
        this.pos = this.pos + name.length() + strValue.length();
        if (this.pos > 60) {
            writer.println();
            this.printIndent(writer, indent + 4);
        } else {
            writer.print(' ');
        }
        writer.print(name);
        writer.print("=\"");
        writer.print(strValue);
        writer.print("\"");
    }

    protected boolean isPersistable(Class<?> clazz) {
        for (Class<?> persistable : persistables) {
            if (persistable != clazz && !persistable.isAssignableFrom(clazz)) continue;
            return true;
        }
        return false;
    }
}

