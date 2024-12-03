/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.annotation.metatype;

import aQute.bnd.annotation.metatype.Meta;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

public class Configurable<T> {
    public static Pattern SPLITTER_P = Pattern.compile("(?<!\\\\)\\|");
    private static final String BND_ANNOTATION_CLASS_NAME = "aQute.bnd.osgi.Annotation";
    private static final String BND_ANNOTATION_METHOD_NAME = "getAnnotation";

    public static <T> T createConfigurable(Class<T> c, Map<?, ?> properties) {
        Object o = Proxy.newProxyInstance(c.getClassLoader(), new Class[]{c}, (InvocationHandler)new ConfigurableHandler(properties, c.getClassLoader()));
        return c.cast(o);
    }

    public static <T> T createConfigurable(Class<T> c, Dictionary<?, ?> properties) {
        HashMap alt = new HashMap();
        Enumeration<?> e = properties.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            alt.put(key, properties.get(key));
        }
        return Configurable.createConfigurable(c, alt);
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

    public static List<String> unescape(String s) {
        String[] parts;
        ArrayList<String> tokens = new ArrayList<String>();
        for (String p : parts = s.split("(?<!\\\\),")) {
            p = p.replaceAll("^\\s*", "");
            p = p.replaceAll("(?!<\\\\)\\s*$", "");
            p = p.replaceAll("\\\\([\\s,\\\\|])", "$1");
            tokens.add(p);
        }
        return tokens;
    }

    static class ConfigurableHandler
    implements InvocationHandler {
        final Map<?, ?> properties;
        final ClassLoader loader;

        ConfigurableHandler(Map<?, ?> properties, ClassLoader loader) {
            this.properties = properties;
            this.loader = loader;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object o;
            Meta.AD ad = method.getAnnotation(Meta.AD.class);
            String id = Configurable.mangleMethodName(method.getName());
            if (ad != null && !ad.id().equals("\u00a7NULL\u00a7")) {
                id = ad.id();
            }
            if ((o = this.properties.get(id)) == null && ad != null) {
                if (ad.required()) {
                    throw new IllegalStateException("Attribute is required but not set " + method.getName());
                }
                o = ad.deflt();
                if (o.equals("\u00a7NULL\u00a7")) {
                    o = null;
                }
            }
            if (o == null) {
                Class<?> rt = method.getReturnType();
                if (rt == Boolean.TYPE) {
                    return false;
                }
                if (method.getReturnType().isPrimitive()) {
                    o = "0";
                } else {
                    return null;
                }
            }
            return this.convert(method.getGenericReturnType(), o);
        }

        public Object convert(Type type, Object o) throws Exception {
            if (type instanceof ParameterizedType) {
                ParameterizedType pType = (ParameterizedType)type;
                return this.convert(pType, o);
            }
            if (type instanceof GenericArrayType) {
                GenericArrayType gType = (GenericArrayType)type;
                return this.convertArray(gType.getGenericComponentType(), o);
            }
            Class resultType = (Class<Byte>)((Object)type);
            if (resultType.isArray()) {
                return this.convertArray(resultType.getComponentType(), o);
            }
            Class<Byte> actualType = o.getClass();
            if (actualType.isAssignableFrom(resultType)) {
                return o;
            }
            if (resultType == Boolean.TYPE || resultType == Boolean.class) {
                if (actualType == Boolean.TYPE || actualType == Boolean.class) {
                    return o;
                }
                if (Number.class.isAssignableFrom(actualType)) {
                    double b = ((Number)o).doubleValue();
                    if (b == 0.0) {
                        return false;
                    }
                    return true;
                }
                if (o instanceof String) {
                    return Boolean.parseBoolean((String)o);
                }
                return true;
            }
            if (resultType == Byte.TYPE || resultType == Byte.class) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return ((Number)o).byteValue();
                }
                resultType = Byte.class;
            } else if (resultType == Character.TYPE) {
                resultType = Character.class;
            } else if (resultType == Short.TYPE) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return ((Number)o).shortValue();
                }
                resultType = Short.class;
            } else if (resultType == Integer.TYPE) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return ((Number)o).intValue();
                }
                resultType = Integer.class;
            } else if (resultType == Long.TYPE) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return ((Number)o).longValue();
                }
                resultType = Long.class;
            } else if (resultType == Float.TYPE) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return Float.valueOf(((Number)o).floatValue());
                }
                resultType = Float.class;
            } else if (resultType == Double.TYPE) {
                if (Number.class.isAssignableFrom(actualType)) {
                    return ((Number)o).doubleValue();
                }
                resultType = Double.class;
            }
            if (resultType.isPrimitive()) {
                throw new IllegalArgumentException("Unknown primitive: " + resultType);
            }
            if (Number.class.isAssignableFrom(resultType) && actualType == Boolean.class) {
                Boolean b = (Boolean)o;
                o = b != false ? "1" : "0";
            } else if (actualType == String.class) {
                String input = (String)o;
                if (Enum.class.isAssignableFrom(resultType)) {
                    return Enum.valueOf(resultType, input);
                }
                if (resultType == Class.class && this.loader != null) {
                    return this.loader.loadClass(input);
                }
                if (resultType == Pattern.class) {
                    return Pattern.compile(input);
                }
            } else if (resultType.isAnnotation() && actualType.getName().equals(Configurable.BND_ANNOTATION_CLASS_NAME)) {
                Method m = actualType.getMethod(Configurable.BND_ANNOTATION_METHOD_NAME, new Class[0]);
                Annotation a = (Annotation)m.invoke(o, new Object[0]);
                if (resultType.isAssignableFrom(a.getClass())) {
                    return a;
                }
                throw new IllegalArgumentException("Annotation " + o + " is not of expected type " + resultType);
            }
            try {
                Constructor c = resultType.getConstructor(String.class);
                return c.newInstance(o.toString());
            }
            catch (Throwable t) {
                throw new IllegalArgumentException("No conversion to " + resultType + " from " + actualType + " value " + o);
            }
        }

        private Object convert(ParameterizedType pType, Object o) throws InstantiationException, IllegalAccessException, Exception {
            Class resultType = (Class<ArrayList>)((Object)pType.getRawType());
            if (Collection.class.isAssignableFrom(resultType)) {
                Collection<?> input = this.toCollection(o);
                if (resultType.isInterface()) {
                    if (resultType == Collection.class || resultType == List.class) {
                        resultType = ArrayList.class;
                    } else if (resultType == Set.class || resultType == SortedSet.class) {
                        resultType = TreeSet.class;
                    } else if (resultType == Queue.class) {
                        resultType = LinkedList.class;
                    } else if (resultType == Queue.class) {
                        resultType = LinkedList.class;
                    } else {
                        throw new IllegalArgumentException("Unknown interface for a collection, no concrete class found: " + resultType);
                    }
                }
                Collection result = (Collection)resultType.getConstructor(new Class[0]).newInstance(new Object[0]);
                Type componentType = pType.getActualTypeArguments()[0];
                for (Object i : input) {
                    result.add(this.convert(componentType, i));
                }
                return result;
            }
            if (pType.getRawType() == Class.class) {
                return this.loader.loadClass(o.toString());
            }
            if (Map.class.isAssignableFrom(resultType)) {
                Map<?, ?> input = this.toMap(o);
                if (resultType.isInterface()) {
                    if (resultType == SortedMap.class) {
                        resultType = TreeMap.class;
                    } else if (resultType == Map.class) {
                        resultType = LinkedHashMap.class;
                    } else {
                        throw new IllegalArgumentException("Unknown interface for a collection, no concrete class found: " + resultType);
                    }
                }
                Map result = (Map)resultType.getConstructor(new Class[0]).newInstance(new Object[0]);
                Type keyType = pType.getActualTypeArguments()[0];
                Type valueType = pType.getActualTypeArguments()[1];
                for (Map.Entry<?, ?> entry : input.entrySet()) {
                    result.put(this.convert(keyType, entry.getKey()), this.convert(valueType, entry.getValue()));
                }
                return result;
            }
            throw new IllegalArgumentException("cannot convert to " + pType + " because it uses generics and is not a Collection or a map");
        }

        Object convertArray(Type componentType, Object o) throws Exception {
            if (o instanceof String) {
                String s = (String)o;
                if (componentType == Byte.class || componentType == Byte.TYPE) {
                    return s.getBytes(StandardCharsets.UTF_8);
                }
                if (componentType == Character.class || componentType == Character.TYPE) {
                    return s.toCharArray();
                }
            }
            Collection<?> input = this.toCollection(o);
            Class<?> componentClass = this.getRawClass(componentType);
            Object array = Array.newInstance(componentClass, input.size());
            int i = 0;
            for (Object next : input) {
                Array.set(array, i++, this.convert(componentType, next));
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
            throw new IllegalArgumentException("For the raw type, type must be ParamaterizedType or Class but is " + type);
        }

        private Collection<?> toCollection(Object o) {
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
            if (o instanceof String) {
                String s = (String)o;
                if (SPLITTER_P.matcher(s).find()) {
                    return Arrays.asList(s.split("\\|"));
                }
                return Configurable.unescape(s);
            }
            return Arrays.asList(o);
        }

        private Map<?, ?> toMap(Object o) {
            if (o instanceof Map) {
                return (Map)o;
            }
            throw new IllegalArgumentException("Cannot convert " + o + " to a map as requested");
        }
    }
}

