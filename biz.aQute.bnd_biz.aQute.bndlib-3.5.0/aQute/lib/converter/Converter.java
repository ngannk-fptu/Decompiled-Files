/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.converter;

import aQute.lib.base64.Base64;
import aQute.lib.converter.TypeReference;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class Converter {
    boolean fatal = true;
    Map<Type, Hook> hooks;
    List<Hook> allHooks;

    public <T> T convert(Class<T> type, Object o) throws Exception {
        if (o != null && type.isAssignableFrom(o.getClass())) {
            return (T)o;
        }
        return (T)this.convert((Type)type, o);
    }

    public <T> T convert(TypeReference<T> type, Object o) throws Exception {
        return (T)this.convert(type.getType(), o);
    }

    public Object convert(Type type, Object o) throws Exception {
        Collection col;
        Number n;
        Class<?> actualType;
        Class<Object> resultType;
        block77: {
            Object value;
            Hook hook;
            resultType = this.getRawClass(type);
            if (o == null) {
                if (resultType.isPrimitive() || Number.class.isAssignableFrom(resultType)) {
                    return this.convert(type, (Object)0);
                }
                return null;
            }
            if (this.allHooks != null) {
                for (Hook hook2 : this.allHooks) {
                    Object r = hook2.convert(type, o);
                    if (r == null) continue;
                    return r;
                }
            }
            if (this.hooks != null && (hook = this.hooks.get(type)) != null && (value = hook.convert(type, o)) != null) {
                return value;
            }
            actualType = o.getClass();
            if (resultType == String.class) {
                if (actualType.isArray()) {
                    if (actualType == char[].class) {
                        return new String((char[])o);
                    }
                    if (actualType == byte[].class) {
                        return Base64.encodeBase64((byte[])o);
                    }
                    int l = Array.getLength(o);
                    StringBuilder sb = new StringBuilder("[");
                    String del = "";
                    for (int i = 0; i < l; ++i) {
                        sb.append(del);
                        del = ",";
                        sb.append(this.convert(String.class, Array.get(o, i)));
                    }
                    sb.append("]");
                    return sb.toString();
                }
                return ((Object)o).toString();
            }
            if (resultType == UUID.class) {
                return UUID.fromString(((Object)o).toString());
            }
            if (o instanceof Dictionary && !(o instanceof Map)) {
                Dictionary dict = (Dictionary)((Object)o);
                HashMap map = new HashMap();
                Enumeration e = dict.keys();
                while (e.hasMoreElements()) {
                    Object k = e.nextElement();
                    Object v = dict.get(k);
                    map.put(k, v);
                }
                o = map;
            }
            if (Collection.class.isAssignableFrom(resultType)) {
                return this.collection(type, resultType, o);
            }
            if (Map.class.isAssignableFrom(resultType)) {
                return this.map(type, resultType, o);
            }
            if (type instanceof GenericArrayType) {
                GenericArrayType gType = (GenericArrayType)type;
                return this.array(gType.getGenericComponentType(), o);
            }
            if (resultType.isArray()) {
                if (actualType == String.class) {
                    String s = (String)((Object)o);
                    if (byte[].class == resultType) {
                        return Base64.decodeBase64(s);
                    }
                    if (char[].class == resultType) {
                        return s.toCharArray();
                    }
                }
                if (byte[].class == resultType) {
                    try {
                        Method m = actualType.getMethod("toByteArray", new Class[0]);
                        if (m.getReturnType() == byte[].class) {
                            return m.invoke(o, new Object[0]);
                        }
                    }
                    catch (Exception e) {
                        // empty catch block
                    }
                }
                return this.array(resultType.getComponentType(), o);
            }
            if (resultType.isAssignableFrom(o.getClass())) {
                return o;
            }
            if (Map.class.isAssignableFrom(actualType) && resultType.isInterface()) {
                return this.proxy(resultType, o);
            }
            if (resultType == Boolean.TYPE || resultType == Boolean.class) {
                if (actualType == Boolean.TYPE || actualType == Boolean.class) {
                    return o;
                }
                n = this.number(o);
                if (n != null) {
                    return n.longValue() != 0L;
                }
                resultType = Boolean.class;
            } else if (resultType == Byte.TYPE || resultType == Byte.class) {
                n = this.number(o);
                if (n != null) {
                    return n.byteValue();
                }
                resultType = Byte.class;
            } else if (resultType == Character.TYPE || resultType == Character.class) {
                n = this.number(o);
                if (n != null) {
                    return Character.valueOf((char)n.shortValue());
                }
                resultType = Character.class;
            } else if (resultType == Short.TYPE || resultType == Short.class) {
                n = this.number(o);
                if (n != null) {
                    return n.shortValue();
                }
                resultType = Short.class;
            } else if (resultType == Integer.TYPE || resultType == Integer.class) {
                n = this.number(o);
                if (n != null) {
                    return n.intValue();
                }
                resultType = Integer.class;
            } else if (resultType == Long.TYPE || resultType == Long.class) {
                n = this.number(o);
                if (n != null) {
                    return n.longValue();
                }
                resultType = Long.class;
            } else if (resultType == Float.TYPE || resultType == Float.class) {
                n = this.number(o);
                if (n != null) {
                    return Float.valueOf(n.floatValue());
                }
                resultType = Float.class;
            } else if (resultType == Double.TYPE || resultType == Double.class) {
                n = this.number(o);
                if (n != null) {
                    return n.doubleValue();
                }
                resultType = Double.class;
            }
            assert (!resultType.isPrimitive());
            if (actualType == String.class) {
                String input = (String)((Object)o);
                if (resultType == char[].class) {
                    return input.toCharArray();
                }
                if (resultType == byte[].class) {
                    return Base64.decodeBase64(input);
                }
                if (Enum.class.isAssignableFrom(resultType)) {
                    try {
                        return Enum.valueOf(resultType, input);
                    }
                    catch (Exception e) {
                        input = input.toUpperCase();
                        return Enum.valueOf(resultType, input);
                    }
                }
                if (resultType == Pattern.class) {
                    return Pattern.compile(input);
                }
                if (resultType == URI.class) {
                    return new URI(this.sanitizeInputForURI(input));
                }
                try {
                    Constructor<Object> c = resultType.getConstructor(String.class);
                    return c.newInstance(((Object)o).toString());
                }
                catch (Throwable t) {
                    try {
                        Method m = resultType.getMethod("valueOf", String.class);
                        if (Modifier.isStatic(m.getModifiers())) {
                            return m.invoke(null, ((Object)o).toString());
                        }
                    }
                    catch (Throwable t2) {
                        // empty catch block
                    }
                    if (resultType != Character.class || input.length() != 1) break block77;
                    return Character.valueOf(input.charAt(0));
                }
            }
        }
        if ((n = this.number(o)) != null && Enum.class.isAssignableFrom(resultType)) {
            try {
                Method values = resultType.getMethod("values", new Class[0]);
                Enum[] vs = (Enum[])values.invoke(null, new Object[0]);
                int nn = n.intValue();
                if (nn > 0 && nn < vs.length) {
                    return vs[nn];
                }
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if (actualType.isArray() && Array.getLength(o) == 1) {
            return this.convert(type, Array.get(o, 0));
        }
        if (o instanceof Collection && (col = (Collection)((Object)o)).size() == 1) {
            return this.convert(type, col.iterator().next());
        }
        if (o instanceof Map) {
            String key = null;
            try {
                Map map = o;
                Object instance = resultType.getConstructor(new Class[0]).newInstance(new Object[0]);
                for (Map.Entry e : map.entrySet()) {
                    key = (String)e.getKey();
                    try {
                        Field f = resultType.getField(key);
                        Object value = this.convert(f.getGenericType(), e.getValue());
                        f.set(instance, value);
                    }
                    catch (Exception ee) {
                        Field f = resultType.getField("__extra");
                        HashMap<String, Object> extra = (HashMap<String, Object>)f.get(instance);
                        if (extra == null) {
                            extra = new HashMap<String, Object>();
                            f.set(instance, extra);
                        }
                        extra.put(key, this.convert(Object.class, e.getValue()));
                    }
                }
                return instance;
            }
            catch (Exception e) {
                return this.error("No conversion found for " + o.getClass() + " to " + type + ", error " + e + " on key " + key);
            }
        }
        return this.error("No conversion found for " + o.getClass() + " to " + type);
    }

    private String sanitizeInputForURI(String input) {
        int newline = input.indexOf("\n");
        if (newline > -1) {
            return input.substring(0, newline).trim();
        }
        return input;
    }

    private Number number(Object o) {
        if (o instanceof Number) {
            return (Number)o;
        }
        if (o instanceof Boolean) {
            return (Boolean)o != false ? 1 : 0;
        }
        if (o instanceof Character) {
            return (int)((Character)o).charValue();
        }
        if (o instanceof String) {
            String s = (String)o;
            try {
                return Double.parseDouble(s);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    /*
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Collection collection(Type collectionType, Class<? extends Collection> rawClass, Object o) throws Exception {
        void var4_12;
        if (rawClass.isInterface() || Modifier.isAbstract(rawClass.getModifiers())) {
            if (rawClass.isAssignableFrom(ArrayList.class)) {
                ArrayList arrayList = new ArrayList();
            } else if (rawClass.isAssignableFrom(HashSet.class)) {
                HashSet hashSet = new HashSet();
            } else if (rawClass.isAssignableFrom(TreeSet.class)) {
                TreeSet treeSet = new TreeSet();
            } else if (rawClass.isAssignableFrom(LinkedList.class)) {
                LinkedList linkedList = new LinkedList();
            } else if (rawClass.isAssignableFrom(Vector.class)) {
                Vector vector = new Vector();
            } else if (rawClass.isAssignableFrom(Stack.class)) {
                Stack stack = new Stack();
            } else {
                if (!rawClass.isAssignableFrom(ConcurrentLinkedQueue.class)) return (Collection)this.error("Cannot find a suitable collection for the collection interface " + rawClass);
                ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
            }
        } else {
            Collection collection = rawClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        Object subType = Object.class;
        if (collectionType instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)collectionType;
            subType = ptype.getActualTypeArguments()[0];
        }
        Collection<?> input = this.toCollection(o);
        for (Object i : input) {
            var4_12.add(this.convert((Type)subType, i));
        }
        return var4_12;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Map map(Type mapType, Class<? extends Map<?, ?>> rawClass, Object o) throws Exception {
        Map<Object, Object> result;
        if (rawClass.isInterface() || Modifier.isAbstract(rawClass.getModifiers())) {
            if (rawClass.isAssignableFrom(HashMap.class)) {
                result = new HashMap();
            } else if (rawClass.isAssignableFrom(TreeMap.class)) {
                result = new TreeMap();
            } else {
                if (!rawClass.isAssignableFrom(ConcurrentHashMap.class)) return (Map)this.error("Cannot find suitable map for map interface " + rawClass);
                result = new ConcurrentHashMap();
            }
        } else {
            result = rawClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        Map<?, ?> input = this.toMap(o);
        Object keyType = Object.class;
        Object valueType = Object.class;
        if (mapType instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)mapType;
            keyType = ptype.getActualTypeArguments()[0];
            valueType = ptype.getActualTypeArguments()[1];
        }
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            Object key = this.convert((Type)keyType, entry.getKey());
            Object value = this.convert((Type)valueType, entry.getValue());
            if (key == null) {
                this.error("Key for map must not be null: " + input);
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    public Object array(Type type, Object o) throws Exception {
        Collection<?> input = this.toCollection(o);
        Class<?> componentClass = this.getRawClass(type);
        Object array = Array.newInstance(componentClass, input.size());
        int i = 0;
        for (Object next : input) {
            Array.set(array, i++, this.convert(type, next));
        }
        return array;
    }

    private Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class)type;
        }
        if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType)type).getGenericComponentType();
            return Array.newInstance(this.getRawClass(componentType), 0).getClass();
        }
        if (type instanceof TypeVariable) {
            Type componentType = ((TypeVariable)type).getBounds()[0];
            return Array.newInstance(this.getRawClass(componentType), 0).getClass();
        }
        if (type instanceof WildcardType) {
            Type componentType = ((WildcardType)type).getUpperBounds()[0];
            return Array.newInstance(this.getRawClass(componentType), 0).getClass();
        }
        return Object.class;
    }

    public Collection<?> toCollection(Object o) {
        if (o instanceof Collection) {
            return (Collection)o;
        }
        if (o.getClass().isArray()) {
            if (o.getClass().getComponentType().isPrimitive()) {
                int length = Array.getLength(o);
                ArrayList<Object> result = new ArrayList<Object>(length);
                for (int i = 0; i < length; ++i) {
                    result.add(Array.get(o, i));
                }
                return result;
            }
            return Arrays.asList((Object[])o);
        }
        return Arrays.asList(o);
    }

    public Map<?, ?> toMap(Object o) throws Exception {
        Field[] fields;
        if (o instanceof Map) {
            return (Map)o;
        }
        HashMap<String, Object> result = new HashMap<String, Object>();
        for (Field f : fields = o.getClass().getFields()) {
            result.put(f.getName(), f.get(o));
        }
        if (result.isEmpty()) {
            return null;
        }
        return result;
    }

    private Object error(String string) {
        if (this.fatal) {
            throw new IllegalArgumentException(string);
        }
        return null;
    }

    public void setFatalIsException(boolean b) {
        this.fatal = b;
    }

    public Converter hook(Type type, Hook hook) {
        if (type != null) {
            if (this.hooks == null) {
                this.hooks = new HashMap<Type, Hook>();
            }
            this.hooks.put(type, hook);
        } else {
            if (this.allHooks == null) {
                this.allHooks = new ArrayList<Hook>();
            }
            this.allHooks.add(hook);
        }
        return this;
    }

    public <T> T proxy(Class<T> interfc, final Map<?, ?> properties) {
        return (T)Proxy.newProxyInstance(interfc.getClassLoader(), new Class[]{interfc}, new InvocationHandler(){

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object o = properties.get(method.getName());
                if (o == null) {
                    o = properties.get(Converter.mangleMethodName(method.getName()));
                }
                if (o == null) {
                    o = args != null && args.length == 1 ? args[0] : method.getDefaultValue();
                }
                return Converter.this.convert(method.getGenericReturnType(), o);
            }
        });
    }

    public static String mangleMethodName(String id) {
        StringBuilder sb = new StringBuilder(id);
        for (int i = 0; i < sb.length(); ++i) {
            boolean twice;
            char c = sb.charAt(i);
            boolean bl = twice = i < sb.length() - 1 && sb.charAt(i + 1) == c;
            if (c != '$' && c != '_') continue;
            if (twice) {
                sb.deleteCharAt(i + 1);
                continue;
            }
            if (c == '$') {
                sb.deleteCharAt(i--);
                continue;
            }
            sb.setCharAt(i, '.');
        }
        return sb.toString();
    }

    public static <T> T cnv(TypeReference<T> tr, Object source) throws Exception {
        return new Converter().convert(tr, source);
    }

    public static <T> T cnv(Class<T> tr, Object source) throws Exception {
        return new Converter().convert(tr, source);
    }

    public static Object cnv(Type tr, Object source) throws Exception {
        return new Converter().convert(tr, source);
    }

    public static interface Hook {
        public Object convert(Type var1, Object var2) throws Exception;
    }
}

