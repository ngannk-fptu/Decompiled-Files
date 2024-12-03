/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MetaClassRegistry;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Range;
import groovy.lang.Script;
import groovy.lang.SpreadMap;
import groovy.lang.SpreadMapEvaluatingException;
import groovy.lang.Tuple;
import groovy.lang.Writable;
import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.RegexSupport;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.metaclass.MetaClassRegistryImpl;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExecutionFailed;
import org.codehaus.groovy.runtime.powerassert.PowerAssertionError;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.wrappers.PojoWrapper;
import org.w3c.dom.Element;

public class InvokerHelper {
    private static final Object[] EMPTY_MAIN_ARGS = new Object[]{new String[0]};
    public static final Object[] EMPTY_ARGS = new Object[0];
    protected static final Object[] EMPTY_ARGUMENTS = EMPTY_ARGS;
    protected static final Class[] EMPTY_TYPES = new Class[0];
    public static final MetaClassRegistry metaRegistry = GroovySystem.getMetaClassRegistry();

    public static void removeClass(Class clazz) {
        metaRegistry.removeMetaClass(clazz);
        ClassInfo.remove(clazz);
        Introspector.flushFromCaches(clazz);
    }

    public static Object invokeMethodSafe(Object object, String methodName, Object arguments) {
        if (object != null) {
            return InvokerHelper.invokeMethod(object, methodName, arguments);
        }
        return null;
    }

    public static Object invokeStaticMethod(String klass, String methodName, Object arguments) throws ClassNotFoundException {
        Class<?> type = Class.forName(klass);
        return InvokerHelper.invokeStaticMethod(type, methodName, arguments);
    }

    public static Object invokeStaticNoArgumentsMethod(Class type, String methodName) {
        return InvokerHelper.invokeStaticMethod(type, methodName, (Object)EMPTY_ARGS);
    }

    public static Object invokeConstructorOf(String klass, Object arguments) throws ClassNotFoundException {
        Class<?> type = Class.forName(klass);
        return InvokerHelper.invokeConstructorOf(type, arguments);
    }

    public static Object invokeNoArgumentsConstructorOf(Class type) {
        return InvokerHelper.invokeConstructorOf(type, (Object)EMPTY_ARGS);
    }

    public static Object invokeClosure(Object closure, Object arguments) {
        return InvokerHelper.invokeMethod(closure, "doCall", arguments);
    }

    public static List asList(Object value) {
        if (value == null) {
            return Collections.EMPTY_LIST;
        }
        if (value instanceof List) {
            return (List)value;
        }
        if (value.getClass().isArray()) {
            return Arrays.asList((Object[])value);
        }
        if (value instanceof Enumeration) {
            ArrayList answer = new ArrayList();
            Enumeration e = (Enumeration)value;
            while (e.hasMoreElements()) {
                answer.add(e.nextElement());
            }
            return answer;
        }
        return Collections.singletonList(value);
    }

    public static String toString(Object arguments) {
        if (arguments instanceof Object[]) {
            return InvokerHelper.toArrayString((Object[])arguments);
        }
        if (arguments instanceof Collection) {
            return InvokerHelper.toListString((Collection)arguments);
        }
        if (arguments instanceof Map) {
            return InvokerHelper.toMapString((Map)arguments);
        }
        return InvokerHelper.format(arguments, false);
    }

    public static String inspect(Object self) {
        return InvokerHelper.format(self, true);
    }

    public static Object getAttribute(Object object, String attribute) {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        if (object instanceof Class) {
            return metaRegistry.getMetaClass((Class)object).getAttribute(object, attribute);
        }
        if (object instanceof GroovyObject) {
            return ((GroovyObject)object).getMetaClass().getAttribute(object, attribute);
        }
        return metaRegistry.getMetaClass(object.getClass()).getAttribute(object, attribute);
    }

    public static void setAttribute(Object object, String attribute, Object newValue) {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        if (object instanceof Class) {
            metaRegistry.getMetaClass((Class)object).setAttribute(object, attribute, newValue);
        } else if (object instanceof GroovyObject) {
            ((GroovyObject)object).getMetaClass().setAttribute(object, attribute, newValue);
        } else {
            metaRegistry.getMetaClass(object.getClass()).setAttribute(object, attribute, newValue);
        }
    }

    public static Object getProperty(Object object, String property) {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        if (object instanceof GroovyObject) {
            GroovyObject pogo = (GroovyObject)object;
            return pogo.getProperty(property);
        }
        if (object instanceof Class) {
            Class c = (Class)object;
            return metaRegistry.getMetaClass(c).getProperty(object, property);
        }
        return ((MetaClassRegistryImpl)metaRegistry).getMetaClass(object).getProperty(object, property);
    }

    public static Object getPropertySafe(Object object, String property) {
        if (object != null) {
            return InvokerHelper.getProperty(object, property);
        }
        return null;
    }

    public static void setProperty(Object object, String property, Object newValue) {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        if (object instanceof GroovyObject) {
            GroovyObject pogo = (GroovyObject)object;
            pogo.setProperty(property, newValue);
        } else if (object instanceof Class) {
            metaRegistry.getMetaClass((Class)object).setProperty((Class)object, property, newValue);
        } else {
            ((MetaClassRegistryImpl)GroovySystem.getMetaClassRegistry()).getMetaClass(object).setProperty(object, property, newValue);
        }
    }

    public static void setProperty2(Object newValue, Object object, String property) {
        InvokerHelper.setProperty(object, property, newValue);
    }

    public static void setGroovyObjectProperty(Object newValue, GroovyObject object, String property) {
        object.setProperty(property, newValue);
    }

    public static Object getGroovyObjectProperty(GroovyObject object, String property) {
        return object.getProperty(property);
    }

    public static void setPropertySafe2(Object newValue, Object object, String property) {
        if (object != null) {
            InvokerHelper.setProperty2(newValue, object, property);
        }
    }

    public static Closure getMethodPointer(Object object, String methodName) {
        if (object == null) {
            throw new NullPointerException("Cannot access method pointer for '" + methodName + "' on null object");
        }
        return new MethodClosure(object, methodName);
    }

    public static Object unaryMinus(Object value) {
        if (value instanceof Integer) {
            Integer number = (Integer)value;
            return -number.intValue();
        }
        if (value instanceof Long) {
            Long number = (Long)value;
            return -number.longValue();
        }
        if (value instanceof BigInteger) {
            return ((BigInteger)value).negate();
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal)value).negate();
        }
        if (value instanceof Double) {
            Double number = (Double)value;
            return -number.doubleValue();
        }
        if (value instanceof Float) {
            Float number = (Float)value;
            return Float.valueOf(-number.floatValue());
        }
        if (value instanceof Short) {
            Short number = (Short)value;
            return -number.shortValue();
        }
        if (value instanceof Byte) {
            Byte number = (Byte)value;
            return -number.byteValue();
        }
        if (value instanceof ArrayList) {
            ArrayList<Object> newlist = new ArrayList<Object>();
            Iterator it = ((ArrayList)value).iterator();
            while (it.hasNext()) {
                newlist.add(InvokerHelper.unaryMinus(it.next()));
            }
            return newlist;
        }
        return InvokerHelper.invokeMethod(value, "negative", EMPTY_ARGS);
    }

    public static Object unaryPlus(Object value) {
        if (value instanceof Integer || value instanceof Long || value instanceof BigInteger || value instanceof BigDecimal || value instanceof Double || value instanceof Float || value instanceof Short || value instanceof Byte) {
            return value;
        }
        if (value instanceof ArrayList) {
            ArrayList<Object> newlist = new ArrayList<Object>();
            Iterator it = ((ArrayList)value).iterator();
            while (it.hasNext()) {
                newlist.add(InvokerHelper.unaryPlus(it.next()));
            }
            return newlist;
        }
        return InvokerHelper.invokeMethod(value, "positive", EMPTY_ARGS);
    }

    public static Matcher findRegex(Object left, Object right) {
        String regexToCompareTo;
        String stringToCompare = left instanceof String ? (String)left : InvokerHelper.toString(left);
        if (right instanceof String) {
            regexToCompareTo = (String)right;
        } else {
            if (right instanceof Pattern) {
                Pattern pattern = (Pattern)right;
                return pattern.matcher(stringToCompare);
            }
            regexToCompareTo = InvokerHelper.toString(right);
        }
        return Pattern.compile(regexToCompareTo).matcher(stringToCompare);
    }

    public static boolean matchRegex(Object left, Object right) {
        if (left == null || right == null) {
            return false;
        }
        Pattern pattern = right instanceof Pattern ? (Pattern)right : Pattern.compile(InvokerHelper.toString(right));
        String stringToCompare = InvokerHelper.toString(left);
        Matcher matcher = pattern.matcher(stringToCompare);
        RegexSupport.setLastMatcher(matcher);
        return matcher.matches();
    }

    public static Tuple createTuple(Object[] array) {
        return new Tuple(array);
    }

    public static SpreadMap spreadMap(Object value) {
        if (value instanceof Map) {
            Object[] values = new Object[((Map)value).keySet().size() * 2];
            int index = 0;
            for (Object key : ((Map)value).keySet()) {
                values[index++] = key;
                values[index++] = ((Map)value).get(key);
            }
            return new SpreadMap(values);
        }
        throw new SpreadMapEvaluatingException("Cannot spread the map " + value.getClass().getName() + ", value " + value);
    }

    public static List createList(Object[] values) {
        ArrayList<Object> answer = new ArrayList<Object>(values.length);
        answer.addAll(Arrays.asList(values));
        return answer;
    }

    public static Map createMap(Object[] values) {
        LinkedHashMap<Object, Object> answer = new LinkedHashMap<Object, Object>(values.length / 2);
        int i = 0;
        while (i < values.length - 1) {
            if (values[i] instanceof SpreadMap && values[i + 1] instanceof Map) {
                Map smap = (Map)values[i + 1];
                for (Object key : smap.keySet()) {
                    answer.put(key, smap.get(key));
                }
                i += 2;
                continue;
            }
            answer.put(values[i++], values[i++]);
        }
        return answer;
    }

    public static void assertFailed(Object expression, Object message) {
        if (message == null || "".equals(message)) {
            throw new PowerAssertionError(expression.toString());
        }
        throw new AssertionError((Object)(String.valueOf(message) + ". Expression: " + expression));
    }

    public static Object runScript(Class scriptClass, String[] args) {
        Binding context = new Binding(args);
        Script script = InvokerHelper.createScript(scriptClass, context);
        return InvokerHelper.invokeMethod(script, "run", EMPTY_ARGS);
    }

    public static Script createScript(Class scriptClass, Binding context) {
        Script script;
        if (scriptClass == null) {
            script = new NullScript(context);
        } else {
            try {
                if (Script.class.isAssignableFrom(scriptClass)) {
                    script = InvokerHelper.newScript(scriptClass, context);
                } else {
                    final GroovyObject object = (GroovyObject)scriptClass.newInstance();
                    script = new Script(context){

                        @Override
                        public Object run() {
                            Object argsToPass = EMPTY_MAIN_ARGS;
                            try {
                                Object args = this.getProperty("args");
                                if (args instanceof String[]) {
                                    argsToPass = args;
                                }
                            }
                            catch (MissingPropertyException missingPropertyException) {
                                // empty catch block
                            }
                            object.invokeMethod("main", argsToPass);
                            return null;
                        }
                    };
                    Map variables = context.getVariables();
                    MetaClass mc = InvokerHelper.getMetaClass(object);
                    for (Map.Entry o : variables.entrySet()) {
                        Map.Entry entry = o;
                        String key = entry.getKey().toString();
                        InvokerHelper.setPropertySafe(key.startsWith("_") ? script : object, mc, key, entry.getValue());
                    }
                }
            }
            catch (Exception e) {
                throw new GroovyRuntimeException("Failed to create Script instance for class: " + scriptClass + ". Reason: " + e, e);
            }
        }
        return script;
    }

    public static Script newScript(Class scriptClass, Binding context) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Script script;
        try {
            Constructor constructor = scriptClass.getConstructor(Binding.class);
            script = (Script)constructor.newInstance(context);
        }
        catch (NoSuchMethodException e) {
            script = (Script)scriptClass.newInstance();
            script.setBinding(context);
        }
        return script;
    }

    public static void setProperties(Object object, Map map) {
        MetaClass mc = InvokerHelper.getMetaClass(object);
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry entry = o = iterator.next();
            String key = entry.getKey().toString();
            Object value = entry.getValue();
            InvokerHelper.setPropertySafe(object, mc, key, value);
        }
    }

    private static void setPropertySafe(Object object, MetaClass mc, String key, Object value) {
        block3: {
            try {
                mc.setProperty(object, key, value);
            }
            catch (MissingPropertyException missingPropertyException) {
            }
            catch (InvokerInvocationException iie) {
                Throwable cause = iie.getCause();
                if (cause != null && cause instanceof IllegalArgumentException) break block3;
                throw iie;
            }
        }
    }

    public static void write(Writer out, Object object) throws IOException {
        if (object instanceof String) {
            out.write((String)object);
        } else if (object instanceof Object[]) {
            out.write(InvokerHelper.toArrayString((Object[])object));
        } else if (object instanceof Map) {
            out.write(InvokerHelper.toMapString((Map)object));
        } else if (object instanceof Collection) {
            out.write(InvokerHelper.toListString((Collection)object));
        } else if (object instanceof Writable) {
            Writable writable = (Writable)object;
            writable.writeTo(out);
        } else if (object instanceof InputStream || object instanceof Reader) {
            int i;
            Reader reader = object instanceof InputStream ? new InputStreamReader((InputStream)object) : (Reader)object;
            char[] chars = new char[8192];
            while ((i = reader.read(chars)) != -1) {
                out.write(chars, 0, i);
            }
            reader.close();
        } else {
            out.write(InvokerHelper.toString(object));
        }
    }

    public static void append(Appendable out, Object object) throws IOException {
        if (object instanceof String) {
            out.append((String)object);
        } else if (object instanceof Object[]) {
            out.append(InvokerHelper.toArrayString((Object[])object));
        } else if (object instanceof Map) {
            out.append(InvokerHelper.toMapString((Map)object));
        } else if (object instanceof Collection) {
            out.append(InvokerHelper.toListString((Collection)object));
        } else if (object instanceof Writable) {
            Writable writable = (Writable)object;
            StringWriter stringWriter = new StringWriter();
            writable.writeTo(stringWriter);
            out.append(stringWriter.toString());
        } else if (object instanceof InputStream || object instanceof Reader) {
            int i;
            Reader reader = object instanceof InputStream ? new InputStreamReader((InputStream)object) : (Reader)object;
            char[] chars = new char[8192];
            while ((i = reader.read(chars)) != -1) {
                for (int j = 0; j < i; ++j) {
                    out.append(chars[j]);
                }
            }
            reader.close();
        } else {
            out.append(InvokerHelper.toString(object));
        }
    }

    public static Iterator<Object> asIterator(Object o) {
        return (Iterator)InvokerHelper.invokeMethod(o, "iterator", EMPTY_ARGS);
    }

    protected static String format(Object arguments, boolean verbose) {
        return InvokerHelper.format(arguments, verbose, -1);
    }

    public static String format(Object arguments, boolean verbose, int maxSize) {
        if (arguments == null) {
            NullObject nullObject = NullObject.getNullObject();
            return (String)nullObject.getMetaClass().invokeMethod((Object)nullObject, "toString", EMPTY_ARGS);
        }
        if (arguments.getClass().isArray()) {
            if (arguments instanceof char[]) {
                return new String((char[])arguments);
            }
            return InvokerHelper.format(DefaultTypeTransformation.asCollection(arguments), verbose, maxSize);
        }
        if (arguments instanceof Range) {
            Range range = (Range)arguments;
            if (verbose) {
                return range.inspect();
            }
            return range.toString();
        }
        if (arguments instanceof Collection) {
            return InvokerHelper.formatList((Collection)arguments, verbose, maxSize);
        }
        if (arguments instanceof Map) {
            return InvokerHelper.formatMap((Map)arguments, verbose, maxSize);
        }
        if (arguments instanceof Element) {
            try {
                Method serialize = Class.forName("groovy.xml.XmlUtil").getMethod("serialize", Element.class);
                return (String)serialize.invoke(null, arguments);
            }
            catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (arguments instanceof String) {
            if (verbose) {
                String arg = InvokerHelper.escapeBackslashes((String)arguments).replaceAll("'", "\\\\'");
                return "'" + arg + "'";
            }
            return (String)arguments;
        }
        return arguments.toString();
    }

    public static String escapeBackslashes(String orig) {
        return orig.replace("\\", "\\\\").replace("\n", "\\n").replaceAll("\\r", "\\\\r").replaceAll("\\t", "\\\\t").replaceAll("\\f", "\\\\f");
    }

    private static String formatMap(Map map, boolean verbose, int maxSize) {
        if (map.isEmpty()) {
            return "[:]";
        }
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry o : map.entrySet()) {
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            if (maxSize != -1 && buffer.length() > maxSize) {
                buffer.append("...");
                break;
            }
            Map.Entry entry = o;
            buffer.append(InvokerHelper.format(entry.getKey(), verbose));
            buffer.append(":");
            if (entry.getValue() == map) {
                buffer.append("(this Map)");
                continue;
            }
            buffer.append(InvokerHelper.format(entry.getValue(), verbose, InvokerHelper.sizeLeft(maxSize, buffer)));
        }
        buffer.append("]");
        return buffer.toString();
    }

    private static int sizeLeft(int maxSize, StringBuilder buffer) {
        return maxSize == -1 ? maxSize : Math.max(0, maxSize - buffer.length());
    }

    private static String formatList(Collection collection, boolean verbose, int maxSize) {
        return InvokerHelper.formatList(collection, verbose, maxSize, false);
    }

    private static String formatList(Collection collection, boolean verbose, int maxSize, boolean safe) {
        StringBuilder buffer = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            String str;
            if (first) {
                first = false;
            } else {
                buffer.append(", ");
            }
            if (maxSize != -1 && buffer.length() > maxSize) {
                buffer.append("...");
                break;
            }
            if (item == collection) {
                buffer.append("(this Collection)");
                continue;
            }
            try {
                str = InvokerHelper.format(item, verbose, InvokerHelper.sizeLeft(maxSize, buffer));
            }
            catch (Exception ex) {
                String hash;
                if (!safe) {
                    throw new GroovyRuntimeException(ex);
                }
                try {
                    hash = Integer.toHexString(item.hashCode());
                }
                catch (Exception ignored) {
                    hash = "????";
                }
                str = "<" + item.getClass().getName() + "@" + hash + ">";
            }
            buffer.append(str);
        }
        buffer.append("]");
        return buffer.toString();
    }

    public static String toTypeString(Object[] arguments) {
        if (arguments == null) {
            return "null";
        }
        StringBuilder argBuf = new StringBuilder();
        for (int i = 0; i < arguments.length; ++i) {
            if (i > 0) {
                argBuf.append(", ");
            }
            argBuf.append(arguments[i] != null ? arguments[i].getClass().getName() : "null");
        }
        return argBuf.toString();
    }

    public static String toMapString(Map arg) {
        return InvokerHelper.toMapString(arg, -1);
    }

    public static String toMapString(Map arg, int maxSize) {
        return InvokerHelper.formatMap(arg, false, maxSize);
    }

    public static String toListString(Collection arg) {
        return InvokerHelper.toListString(arg, -1);
    }

    public static String toListString(Collection arg, int maxSize) {
        return InvokerHelper.toListString(arg, maxSize, false);
    }

    public static String toListString(Collection arg, int maxSize, boolean safe) {
        return InvokerHelper.formatList(arg, false, maxSize, safe);
    }

    public static String toArrayString(Object[] arguments) {
        if (arguments == null) {
            return "null";
        }
        String sbdry = "[";
        String ebdry = "]";
        StringBuilder argBuf = new StringBuilder(sbdry);
        for (int i = 0; i < arguments.length; ++i) {
            if (i > 0) {
                argBuf.append(", ");
            }
            argBuf.append(InvokerHelper.format(arguments[i], false));
        }
        argBuf.append(ebdry);
        return argBuf.toString();
    }

    public static String toArrayString(Object[] arguments, int maxSize, boolean safe) {
        return InvokerHelper.toListString(DefaultTypeTransformation.asCollection(arguments), maxSize, safe);
    }

    public static List createRange(Object from, Object to, boolean inclusive) {
        try {
            return ScriptBytecodeAdapter.createRange(from, to, inclusive);
        }
        catch (RuntimeException re) {
            throw re;
        }
        catch (Error e) {
            throw e;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static Object bitwiseNegate(Object value) {
        if (value instanceof Integer) {
            Integer number = (Integer)value;
            return ~number.intValue();
        }
        if (value instanceof Long) {
            Long number = (Long)value;
            return number ^ 0xFFFFFFFFFFFFFFFFL;
        }
        if (value instanceof BigInteger) {
            return ((BigInteger)value).not();
        }
        if (value instanceof String) {
            return StringGroovyMethods.bitwiseNegate(value.toString());
        }
        if (value instanceof GString) {
            return StringGroovyMethods.bitwiseNegate(value.toString());
        }
        if (value instanceof ArrayList) {
            ArrayList<Object> newlist = new ArrayList<Object>();
            Iterator it = ((ArrayList)value).iterator();
            while (it.hasNext()) {
                newlist.add(InvokerHelper.bitwiseNegate(it.next()));
            }
            return newlist;
        }
        return InvokerHelper.invokeMethod(value, "bitwiseNegate", EMPTY_ARGS);
    }

    public static MetaClassRegistry getMetaRegistry() {
        return metaRegistry;
    }

    public static MetaClass getMetaClass(Object object) {
        if (object instanceof GroovyObject) {
            return ((GroovyObject)object).getMetaClass();
        }
        return ((MetaClassRegistryImpl)GroovySystem.getMetaClassRegistry()).getMetaClass(object);
    }

    public static MetaClass getMetaClass(Class cls) {
        return metaRegistry.getMetaClass(cls);
    }

    public static Object invokeMethod(Object object, String methodName, Object arguments) {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        if (object instanceof Class) {
            Class theClass = (Class)object;
            MetaClass metaClass = metaRegistry.getMetaClass(theClass);
            return metaClass.invokeStaticMethod(object, methodName, InvokerHelper.asArray(arguments));
        }
        if (!(object instanceof GroovyObject)) {
            return InvokerHelper.invokePojoMethod(object, methodName, arguments);
        }
        return InvokerHelper.invokePogoMethod(object, methodName, arguments);
    }

    static Object invokePojoMethod(Object object, String methodName, Object arguments) {
        MetaClass metaClass = InvokerHelper.getMetaClass(object);
        return metaClass.invokeMethod(object, methodName, InvokerHelper.asArray(arguments));
    }

    static Object invokePogoMethod(Object object, String methodName, Object arguments) {
        GroovyObject groovy = (GroovyObject)object;
        boolean intercepting = groovy instanceof GroovyInterceptable;
        try {
            if (intercepting) {
                return groovy.invokeMethod(methodName, InvokerHelper.asUnwrappedArray(arguments));
            }
            return groovy.getMetaClass().invokeMethod(object, methodName, InvokerHelper.asArray(arguments));
        }
        catch (MissingMethodException e) {
            if (e instanceof MissingMethodExecutionFailed) {
                throw (MissingMethodException)e.getCause();
            }
            if (!intercepting && e.getMethod().equals(methodName) && object.getClass() == e.getType()) {
                return groovy.invokeMethod(methodName, InvokerHelper.asUnwrappedArray(arguments));
            }
            throw e;
        }
    }

    public static Object invokeSuperMethod(Object object, String methodName, Object arguments) {
        if (object == null) {
            throw new NullPointerException("Cannot invoke method " + methodName + "() on null object");
        }
        Class<?> theClass = object.getClass();
        MetaClass metaClass = metaRegistry.getMetaClass(theClass.getSuperclass());
        return metaClass.invokeMethod(object, methodName, InvokerHelper.asArray(arguments));
    }

    public static Object invokeStaticMethod(Class type, String method, Object arguments) {
        MetaClass metaClass = metaRegistry.getMetaClass(type);
        return metaClass.invokeStaticMethod(type, method, InvokerHelper.asArray(arguments));
    }

    public static Object invokeConstructorOf(Class type, Object arguments) {
        MetaClass metaClass = metaRegistry.getMetaClass(type);
        return metaClass.invokeConstructor(InvokerHelper.asArray(arguments));
    }

    public static Object[] asArray(Object arguments) {
        if (arguments == null) {
            return EMPTY_ARGUMENTS;
        }
        if (arguments instanceof Object[]) {
            return (Object[])arguments;
        }
        return new Object[]{arguments};
    }

    public static Object[] asUnwrappedArray(Object arguments) {
        Object[] args = InvokerHelper.asArray(arguments);
        for (int i = 0; i < args.length; ++i) {
            if (!(args[i] instanceof PojoWrapper)) continue;
            args[i] = ((PojoWrapper)args[i]).unwrap();
        }
        return args;
    }

    static class NullScript
    extends Script {
        public NullScript() {
            this(new Binding());
        }

        public NullScript(Binding context) {
            super(context);
        }

        @Override
        public Object run() {
            return null;
        }
    }
}

