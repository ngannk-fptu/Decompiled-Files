/*
 * Decompiled with CFR 0.152.
 */
package org.tuckey.web.filters.urlrewrite.json;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.StringCharacterIterator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.tuckey.web.filters.urlrewrite.json.JsonRpcBean;
import org.tuckey.web.filters.urlrewrite.json.JsonRpcErrorBean;

public class JsonWriter {
    private StringBuffer buf = new StringBuffer();
    private Stack calls = new Stack();
    boolean emitClassName = true;
    static char[] hex = "0123456789ABCDEF".toCharArray();

    public JsonWriter(boolean emitClassName) {
        this.emitClassName = emitClassName;
    }

    public JsonWriter() {
        this(true);
    }

    public String write(Object object) {
        this.buf.setLength(0);
        this.value(object);
        return this.buf.toString();
    }

    public String write(long n) {
        return String.valueOf(n);
    }

    public String write(double d) {
        return String.valueOf(d);
    }

    public String write(char c) {
        return "\"" + c + "\"";
    }

    public String write(boolean b) {
        return String.valueOf(b);
    }

    private void value(Object object) {
        if (object == null || this.cyclic(object)) {
            this.add("null");
        } else {
            this.calls.push(object);
            if (object instanceof Class) {
                this.string(object);
            } else if (object instanceof Boolean) {
                this.bool((Boolean)object);
            } else if (object instanceof Number) {
                this.add(object);
            } else if (object instanceof String) {
                this.string(object);
            } else if (object instanceof Character) {
                this.string(object);
            } else if (object instanceof Map) {
                this.map((Map)object);
            } else if (object.getClass().isArray()) {
                this.array(object);
            } else if (object instanceof Iterator) {
                this.array((Iterator)object);
            } else if (object instanceof Collection) {
                this.array(((Collection)object).iterator());
            } else {
                this.bean(object);
            }
            this.calls.pop();
        }
    }

    private boolean cyclic(Object object) {
        for (Object called : this.calls) {
            if (object != called) continue;
            return true;
        }
        return false;
    }

    private void bean(Object object) {
        this.add("{");
        boolean addedSomething = false;
        try {
            BeanInfo info = Introspector.getBeanInfo(object.getClass());
            PropertyDescriptor[] props = info.getPropertyDescriptors();
            for (int i = 0; i < props.length; ++i) {
                JsonRpcBean rpcBean;
                PropertyDescriptor prop = props[i];
                String name = prop.getName();
                if (object instanceof Throwable && "stackTrace".equals(name) || object instanceof JsonRpcErrorBean && "class".equals(name) || object instanceof JsonRpcBean && ("class".equals(name) || (rpcBean = (JsonRpcBean)object).getError() == null && "error".equals(name) || rpcBean.getError() != null && "result".equals(name))) continue;
                Method accessor = prop.getReadMethod();
                if (!this.emitClassName && "class".equals(name) || accessor == null) continue;
                if (!accessor.isAccessible()) {
                    accessor.setAccessible(true);
                }
                Object value = accessor.invoke(object, (Object[])null);
                if (addedSomething) {
                    this.add(',');
                }
                this.add(name, value);
                addedSomething = true;
            }
            Field[] ff = object.getClass().getFields();
            for (int i = 0; i < ff.length; ++i) {
                Field field = ff[i];
                if (addedSomething) {
                    this.add(',');
                }
                this.add(field.getName(), field.get(object));
                addedSomething = true;
            }
        }
        catch (IllegalAccessException iae) {
            iae.printStackTrace();
        }
        catch (InvocationTargetException ite) {
            ite.getCause().printStackTrace();
            ite.printStackTrace();
        }
        catch (IntrospectionException ie) {
            ie.printStackTrace();
        }
        this.add("}");
    }

    private void add(String name, Object value) {
        this.add('\"');
        this.add(name);
        this.add("\":");
        this.value(value);
    }

    private void map(Map map) {
        this.add("{");
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = it.next();
            this.value(e.getKey());
            this.add(":");
            this.value(e.getValue());
            if (!it.hasNext()) continue;
            this.add(',');
        }
        this.add("}");
    }

    private void array(Iterator it) {
        this.add("[");
        while (it.hasNext()) {
            this.value(it.next());
            if (!it.hasNext()) continue;
            this.add(",");
        }
        this.add("]");
    }

    private void array(Object object) {
        this.add("[");
        int length = Array.getLength(object);
        for (int i = 0; i < length; ++i) {
            this.value(Array.get(object, i));
            if (i >= length - 1) continue;
            this.add(',');
        }
        this.add("]");
    }

    private void bool(boolean b) {
        this.add(b ? "true" : "false");
    }

    private void string(Object obj) {
        this.add('\"');
        StringCharacterIterator it = new StringCharacterIterator(obj.toString());
        char c = it.first();
        while (c != '\uffff') {
            if (c == '\"') {
                this.add("\\\"");
            } else if (c == '\\') {
                this.add("\\\\");
            } else if (c == '/') {
                this.add("\\/");
            } else if (c == '\b') {
                this.add("\\b");
            } else if (c == '\f') {
                this.add("\\f");
            } else if (c == '\n') {
                this.add("\\n");
            } else if (c == '\r') {
                this.add("\\r");
            } else if (c == '\t') {
                this.add("\\t");
            } else if (Character.isISOControl(c)) {
                this.unicode(c);
            } else {
                this.add(c);
            }
            c = it.next();
        }
        this.add('\"');
    }

    private void add(Object obj) {
        this.buf.append(obj);
    }

    private void add(char c) {
        this.buf.append(c);
    }

    private void unicode(char c) {
        this.add("\\u");
        int n = c;
        for (int i = 0; i < 4; ++i) {
            int digit = (n & 0xF000) >> 12;
            this.add(hex[digit]);
            n <<= 4;
        }
    }
}

