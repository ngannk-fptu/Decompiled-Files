/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Signatures {
    public boolean hasGenerics() throws Exception {
        try {
            this.call(Signatures.class, "getGenericSuperClass");
            return true;
        }
        catch (NoSuchMethodException mnfe) {
            return false;
        }
    }

    public String getSignature(Object c) throws Exception {
        if (c instanceof Class) {
            return this.getSignature((Class)c);
        }
        if (c instanceof Constructor) {
            return this.getSignature((Constructor)c);
        }
        if (c instanceof Method) {
            return this.getSignature((Method)c);
        }
        if (c instanceof Field) {
            return this.getSignature((Field)c);
        }
        throw new IllegalArgumentException(c.toString());
    }

    public String getSignature(Class<?> c) throws Exception {
        StringBuilder sb = new StringBuilder();
        this.declaration(sb, c);
        this.reference(sb, this.call(c, "getGenericSuperclass"));
        for (Object type : (Object[])this.call(c, "getGenericInterfaces")) {
            this.reference(sb, type);
        }
        return sb.toString();
    }

    public String getSignature(Method m) throws Exception {
        StringBuilder sb = new StringBuilder();
        this.declaration(sb, m);
        sb.append('(');
        for (Object type : (Object[])this.call(m, "getGenericParameterTypes")) {
            this.reference(sb, type);
        }
        sb.append(')');
        this.reference(sb, this.call(m, "getGenericReturnType"));
        return sb.toString();
    }

    public String getSignature(Constructor<?> c) throws Exception {
        StringBuilder sb = new StringBuilder();
        this.declaration(sb, c);
        sb.append('(');
        for (Object type : (Object[])this.call(c, "getGenericParameterTypes")) {
            this.reference(sb, type);
        }
        sb.append(')');
        this.reference(sb, Void.TYPE);
        return sb.toString();
    }

    public String getSignature(Field f) throws Exception {
        StringBuilder sb = new StringBuilder();
        Object t = this.call(f, "getGenericType");
        this.reference(sb, t);
        return sb.toString();
    }

    private void declaration(StringBuilder sb, Object gd) throws Exception {
        Object[] typeParameters = (Object[])this.call(gd, "getTypeParameters");
        if (typeParameters.length > 0) {
            sb.append('<');
            for (Object tv : typeParameters) {
                sb.append(this.call(tv, "getName"));
                Object[] bounds = (Object[])this.call(tv, "getBounds");
                if (bounds.length > 0 && this.isInterface(bounds[0])) {
                    sb.append(':');
                }
                for (int i = 0; i < bounds.length; ++i) {
                    sb.append(':');
                    this.reference(sb, bounds[i]);
                }
            }
            sb.append('>');
        }
    }

    private boolean isInterface(Object type) throws Exception {
        if (type instanceof Class) {
            return ((Class)type).isInterface();
        }
        if (this.isInstance(type.getClass(), "java.lang.reflect.ParameterizedType")) {
            return this.isInterface(this.call(type, "getRawType"));
        }
        return false;
    }

    private void reference(StringBuilder sb, Object t) throws Exception {
        if (this.isInstance(t.getClass(), "java.lang.reflect.ParameterizedType")) {
            sb.append('L');
            this.parameterizedType(sb, t);
            sb.append(';');
            return;
        }
        if (this.isInstance(t.getClass(), "java.lang.reflect.GenericArrayType")) {
            sb.append('[');
            this.reference(sb, this.call(t, "getGenericComponentType"));
        } else if (this.isInstance(t.getClass(), "java.lang.reflect.WildcardType")) {
            Object[] lowerBounds = (Object[])this.call(t, "getLowerBounds");
            Object[] upperBounds = (Object[])this.call(t, "getUpperBounds");
            if (upperBounds.length == 1 && upperBounds[0] == Object.class) {
                upperBounds = new Object[]{};
            }
            if (upperBounds.length != 0) {
                for (Object upper : upperBounds) {
                    sb.append('+');
                    this.reference(sb, upper);
                }
            } else if (lowerBounds.length != 0) {
                for (Object lower : lowerBounds) {
                    sb.append('-');
                    this.reference(sb, lower);
                }
            } else {
                sb.append('*');
            }
        } else if (this.isInstance(t.getClass(), "java.lang.reflect.TypeVariable")) {
            sb.append('T');
            sb.append(this.call(t, "getName"));
            sb.append(';');
        } else if (t instanceof Class) {
            Class c = (Class)t;
            if (c.isPrimitive()) {
                sb.append(this.primitive(c));
            } else {
                sb.append('L');
                String name = c.getName().replace('.', '/');
                sb.append(name);
                sb.append(';');
            }
        }
    }

    private void parameterizedType(StringBuilder sb, Object pt) throws Exception {
        Object owner = this.call(pt, "getOwnerType");
        String name = ((Class)this.call(pt, "getRawType")).getName().replace('.', '/');
        if (owner != null) {
            if (this.isInstance(owner.getClass(), "java.lang.reflect.ParameterizedType")) {
                this.parameterizedType(sb, owner);
            } else {
                sb.append(((Class)owner).getName().replace('.', '/'));
            }
            sb.append('.');
            int n = name.lastIndexOf(36);
            name = name.substring(n + 1);
        }
        sb.append(name);
        sb.append('<');
        for (Object parameterType : (Object[])this.call(pt, "getActualTypeArguments")) {
            this.reference(sb, parameterType);
        }
        sb.append('>');
    }

    private char primitive(Class<?> type) {
        if (type == Byte.TYPE) {
            return 'B';
        }
        if (type == Character.TYPE) {
            return 'C';
        }
        if (type == Double.TYPE) {
            return 'D';
        }
        if (type == Float.TYPE) {
            return 'F';
        }
        if (type == Integer.TYPE) {
            return 'I';
        }
        if (type == Long.TYPE) {
            return 'J';
        }
        if (type == Short.TYPE) {
            return 'S';
        }
        if (type == Boolean.TYPE) {
            return 'Z';
        }
        if (type == Void.TYPE) {
            return 'V';
        }
        throw new IllegalArgumentException("Unknown primitive type " + type);
    }

    public String normalize(String signature) {
        StringBuilder sb = new StringBuilder();
        HashMap<String, String> map = new HashMap<String, String>();
        Rover rover = new Rover(signature);
        this.declare(sb, map, rover);
        if (rover.peek() == '(') {
            sb.append(rover.take('('));
            while (rover.peek() != ')') {
                this.reference(sb, map, rover, true);
            }
            sb.append(rover.take(')'));
            this.reference(sb, map, rover, true);
        } else {
            this.reference(sb, map, rover, true);
            while (!rover.isEOF()) {
                this.reference(sb, map, rover, true);
            }
        }
        return sb.toString();
    }

    private void reference(StringBuilder sb, Map<String, String> map, Rover rover, boolean primitivesAllowed) {
        char type = rover.take();
        sb.append(type);
        if (type == '[') {
            this.reference(sb, map, rover, true);
        } else if (type == 'L') {
            String fqnb = rover.upTo("<;.");
            sb.append(fqnb);
            this.body(sb, map, rover);
            while (rover.peek() == '.') {
                sb.append(rover.take('.'));
                sb.append(rover.upTo("<;."));
                this.body(sb, map, rover);
            }
            sb.append(rover.take(';'));
        } else if (type == 'T') {
            String name = rover.upTo(";");
            name = this.assign(map, name);
            sb.append(name);
            sb.append(rover.take(';'));
        } else if (!primitivesAllowed) {
            throw new IllegalStateException("Primitives are not allowed without an array");
        }
    }

    private void body(StringBuilder sb, Map<String, String> map, Rover rover) {
        if (rover.peek() == '<') {
            sb.append(rover.take('<'));
            while (rover.peek() != '>') {
                switch (rover.peek()) {
                    case 'L': 
                    case '[': {
                        this.reference(sb, map, rover, false);
                        break;
                    }
                    case 'T': {
                        sb.append(rover.take('T'));
                        String name = rover.upTo(";");
                        sb.append(this.assign(map, name));
                        sb.append(rover.take(';'));
                        break;
                    }
                    case '+': 
                    case '-': {
                        sb.append(rover.take());
                        this.reference(sb, map, rover, false);
                        break;
                    }
                    case '*': {
                        sb.append(rover.take());
                    }
                }
            }
            sb.append(rover.take('>'));
        }
    }

    private void declare(StringBuilder sb, Map<String, String> map, Rover rover) {
        char c = rover.peek();
        if (c == '<') {
            sb.append(rover.take('<'));
            while (rover.peek() != '>') {
                String name = rover.upTo(":");
                name = this.assign(map, name);
                sb.append(name);
                block4: while (rover.peek() == ':') {
                    sb.append(rover.take(':'));
                    switch (rover.peek()) {
                        case ':': {
                            continue block4;
                        }
                    }
                    this.reference(sb, map, rover, false);
                }
            }
            sb.append(rover.take('>'));
        }
    }

    private String assign(Map<String, String> map, String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        int n = map.size();
        map.put(name, "_" + n);
        return "_" + n;
    }

    private boolean isInstance(Class<?> type, String string) {
        if (type == null) {
            return false;
        }
        if (type.getName().equals(string)) {
            return true;
        }
        if (this.isInstance(type.getSuperclass(), string)) {
            return true;
        }
        for (Class<?> intf : type.getInterfaces()) {
            if (!this.isInstance(intf, string)) continue;
            return true;
        }
        return false;
    }

    private Object call(Object gd, String string) throws Exception {
        Method m = gd.getClass().getMethod(string, new Class[0]);
        return m.invoke(gd, new Object[0]);
    }

    static class Rover {
        final String s;
        int i;

        public Rover(String s) {
            this.s = s;
            this.i = 0;
        }

        char peek() {
            return this.s.charAt(this.i);
        }

        char take() {
            return this.s.charAt(this.i++);
        }

        char take(char c) {
            char x;
            if (c != (x = this.s.charAt(this.i++))) {
                throw new IllegalStateException("get() expected " + c + " but got + " + x);
            }
            return x;
        }

        public String upTo(String except) {
            int start = this.i;
            while (except.indexOf(this.peek()) < 0) {
                this.take();
            }
            return this.s.substring(start, this.i);
        }

        public boolean isEOF() {
            return this.i >= this.s.length();
        }
    }
}

