/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.EmptyRange;
import groovy.lang.GroovyInterceptable;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.GroovySystem;
import groovy.lang.IntRange;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.ObjectRange;
import groovy.lang.Tuple;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.NullObject;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExecutionFailed;
import org.codehaus.groovy.runtime.metaclass.MissingPropertyExceptionNoStack;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.wrappers.GroovyObjectWrapper;
import org.codehaus.groovy.runtime.wrappers.PojoWrapper;
import org.codehaus.groovy.runtime.wrappers.Wrapper;

public class ScriptBytecodeAdapter {
    public static final Object[] EMPTY_ARGS = new Object[0];
    private static final Integer ZERO = 0;
    private static final Integer MINUS_ONE = -1;
    private static final Integer ONE = 1;

    public static Throwable unwrap(GroovyRuntimeException gre) {
        Throwable th;
        if (gre.getCause() == null) {
            if (gre instanceof MissingPropertyExceptionNoStack) {
                MissingPropertyExceptionNoStack noStack = (MissingPropertyExceptionNoStack)gre;
                return new MissingPropertyException(noStack.getProperty(), noStack.getType());
            }
            if (gre instanceof MissingMethodExceptionNoStack) {
                MissingMethodExceptionNoStack noStack = (MissingMethodExceptionNoStack)gre;
                return new MissingMethodException(noStack.getMethod(), noStack.getType(), noStack.getArguments(), noStack.isStatic());
            }
        }
        if ((th = gre).getCause() != null && th.getCause() != gre) {
            th = th.getCause();
        }
        if (th != gre && th instanceof GroovyRuntimeException) {
            return ScriptBytecodeAdapter.unwrap(th);
        }
        return th;
    }

    public static Object invokeMethodOnCurrentN(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        Object result;
        block6: {
            result = null;
            boolean intercepting = receiver instanceof GroovyInterceptable;
            try {
                try {
                    result = intercepting ? receiver.invokeMethod(messageName, messageArguments) : receiver.getMetaClass().invokeMethod(senderClass, receiver, messageName, messageArguments, false, true);
                }
                catch (MissingMethodException e) {
                    if (e instanceof MissingMethodExecutionFailed) {
                        throw (MissingMethodException)e.getCause();
                    }
                    if (!intercepting && receiver.getClass() == e.getType() && e.getMethod().equals(messageName)) {
                        result = receiver.invokeMethod(messageName, messageArguments);
                        break block6;
                    }
                    throw e;
                }
            }
            catch (GroovyRuntimeException gre) {
                throw ScriptBytecodeAdapter.unwrap(gre);
            }
        }
        return result;
    }

    public static Object invokeMethodOnCurrentNSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(senderClass, receiver, messageName, messageArguments);
    }

    public static Object invokeMethodOnCurrentNSpreadSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.invokeMethodNSafe(senderClass, it.next(), messageName, messageArguments));
        }
        return answer;
    }

    public static Object invokeMethodOnCurrent0(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodOnCurrent0Safe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnCurrentNSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodOnCurrent0SpreadSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnCurrentNSpreadSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodOnSuperN(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        MetaClass metaClass = receiver.getMetaClass();
        Object result = null;
        try {
            result = metaClass.invokeMethod(senderClass, receiver, messageName, messageArguments, true, true);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
        return result;
    }

    public static Object invokeMethodOnSuperNSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnSuperN(senderClass, receiver, messageName, messageArguments);
    }

    public static Object invokeMethodOnSuperNSpreadSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.invokeMethodNSafe(senderClass, it.next(), messageName, messageArguments));
        }
        return answer;
    }

    public static Object invokeMethodOnSuper0(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnSuperN(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodOnSuper0Safe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnSuperNSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodOnSuper0SpreadSafe(Class senderClass, GroovyObject receiver, String messageName, Object[] messageArguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnSuperNSpreadSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethodN(Class senderClass, Object receiver, String messageName, Object[] messageArguments) throws Throwable {
        try {
            return InvokerHelper.invokeMethod(receiver, messageName, messageArguments);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object invokeMethodNSafe(Class senderClass, Object receiver, String messageName, Object[] messageArguments) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return ScriptBytecodeAdapter.invokeMethodN(senderClass, receiver, messageName, messageArguments);
    }

    public static Object invokeMethodNSpreadSafe(Class senderClass, Object receiver, String messageName, Object[] messageArguments) throws Throwable {
        if (receiver == null) {
            return null;
        }
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.invokeMethodNSafe(senderClass, it.next(), messageName, messageArguments));
        }
        return answer;
    }

    public static Object invokeMethod0(Class senderClass, Object receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodN(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethod0Safe(Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return ScriptBytecodeAdapter.invokeMethodNSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeMethod0SpreadSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodNSpreadSafe(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeStaticMethodN(Class senderClass, Class receiver, String messageName, Object[] messageArguments) throws Throwable {
        try {
            return InvokerHelper.invokeStaticMethod(receiver, messageName, (Object)messageArguments);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object invokeStaticMethod0(Class senderClass, Class receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeStaticMethodN(senderClass, receiver, messageName, EMPTY_ARGS);
    }

    public static Object invokeNewN(Class senderClass, Class receiver, Object arguments) throws Throwable {
        try {
            return InvokerHelper.invokeConstructorOf(receiver, arguments);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object invokeNew0(Class senderClass, Class receiver) throws Throwable {
        return ScriptBytecodeAdapter.invokeNewN(senderClass, receiver, EMPTY_ARGS);
    }

    public static int selectConstructorAndTransformArguments(Object[] arguments, int numberOfConstructors, Class which) throws Throwable {
        MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(which);
        try {
            return metaClass.selectConstructorAndTransformArguments(numberOfConstructors, arguments);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getFieldOnSuper(Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            if (receiver instanceof Class) {
                return InvokerHelper.getAttribute(receiver, messageName);
            }
            MetaClass mc = ((GroovyObject)receiver).getMetaClass();
            return mc.getAttribute(senderClass, receiver, messageName, true);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getFieldOnSuperSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.getFieldOnSuper(senderClass, receiver, messageName);
    }

    public static Object getFieldOnSuperSpreadSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getFieldOnSuper(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setFieldOnSuper(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            if (receiver instanceof Class) {
                InvokerHelper.setAttribute(receiver, messageName, messageArgument);
            } else {
                MetaClass mc = ((GroovyObject)receiver).getMetaClass();
                mc.setAttribute(senderClass, receiver, messageName, messageArgument, true, true);
            }
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setFieldOnSuperSafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        ScriptBytecodeAdapter.setFieldOnSuper(messageArgument, senderClass, receiver, messageName);
    }

    public static void setFieldOnSuperSpreadSafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setFieldOnSuper(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Object getField(Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            return InvokerHelper.getAttribute(receiver, messageName);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getFieldSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return ScriptBytecodeAdapter.getField(senderClass, receiver, messageName);
    }

    public static Object getFieldSpreadSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getFieldSafe(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setField(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            InvokerHelper.setAttribute(receiver, messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setFieldSafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        ScriptBytecodeAdapter.setField(messageArgument, senderClass, receiver, messageName);
    }

    public static void setFieldSpreadSafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setFieldSafe(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Object getGroovyObjectField(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        try {
            return receiver.getMetaClass().getAttribute(receiver, messageName);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getGroovyObjectFieldSafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        try {
            return receiver.getMetaClass().getAttribute(receiver, messageName);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getGroovyObjectFieldSpreadSafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getFieldSafe(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setGroovyObjectField(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        try {
            receiver.getMetaClass().setAttribute(receiver, messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setGroovyObjectFieldSafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        try {
            receiver.getMetaClass().setAttribute(receiver, messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setGroovyObjectFieldSpreadSafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setFieldSafe(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Object getPropertyOnSuper(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodOnSuperN(senderClass, receiver, "getProperty", new Object[]{messageName});
    }

    public static Object getPropertyOnSuperSafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        return ScriptBytecodeAdapter.getPropertyOnSuper(senderClass, receiver, messageName);
    }

    public static Object getPropertyOnSuperSpreadSafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getPropertySafe(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setPropertyOnSuper(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        try {
            InvokerHelper.setAttribute(receiver, messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setPropertyOnSuperSafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        ScriptBytecodeAdapter.setPropertyOnSuper(messageArgument, senderClass, receiver, messageName);
    }

    public static void setPropertyOnSuperSpreadSafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setPropertySafe(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Object getProperty(Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            return InvokerHelper.getProperty(receiver, messageName);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object getPropertySafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return ScriptBytecodeAdapter.getProperty(senderClass, receiver, messageName);
    }

    public static Object getPropertySpreadSafe(Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getPropertySafe(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setProperty(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        try {
            if (receiver == null) {
                receiver = NullObject.getNullObject();
            }
            InvokerHelper.setProperty(receiver, messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setPropertySafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        ScriptBytecodeAdapter.setProperty(messageArgument, senderClass, receiver, messageName);
    }

    public static void setPropertySpreadSafe(Object messageArgument, Class senderClass, Object receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setPropertySafe(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Object getGroovyObjectProperty(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        return receiver.getProperty(messageName);
    }

    public static Object getGroovyObjectPropertySafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        return ScriptBytecodeAdapter.getGroovyObjectProperty(senderClass, receiver, messageName);
    }

    public static Object getGroovyObjectPropertySpreadSafe(Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return null;
        }
        ArrayList<Object> answer = new ArrayList<Object>();
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            answer.add(ScriptBytecodeAdapter.getPropertySafe(senderClass, it.next(), messageName));
        }
        return answer;
    }

    public static void setGroovyObjectProperty(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        try {
            receiver.setProperty(messageName, messageArgument);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static void setGroovyObjectPropertySafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        receiver.setProperty(messageName, messageArgument);
    }

    public static void setGroovyObjectPropertySpreadSafe(Object messageArgument, Class senderClass, GroovyObject receiver, String messageName) throws Throwable {
        if (receiver == null) {
            return;
        }
        Iterator<Object> it = InvokerHelper.asIterator(receiver);
        while (it.hasNext()) {
            ScriptBytecodeAdapter.setPropertySafe(messageArgument, senderClass, it.next(), messageName);
        }
    }

    public static Closure getMethodPointer(Object object, String methodName) {
        return InvokerHelper.getMethodPointer(object, methodName);
    }

    public static Object invokeClosure(Object closure, Object[] arguments) throws Throwable {
        return ScriptBytecodeAdapter.invokeMethodN(closure.getClass(), closure, "call", arguments);
    }

    public static Object asType(Object object, Class type) throws Throwable {
        if (object == null) {
            object = NullObject.getNullObject();
        }
        return ScriptBytecodeAdapter.invokeMethodN(object.getClass(), object, "asType", new Object[]{type});
    }

    public static Object castToType(Object object, Class type) throws Throwable {
        return DefaultTypeTransformation.castToType(object, type);
    }

    public static Tuple createTuple(Object[] array) {
        return new Tuple(array);
    }

    public static List createList(Object[] values) {
        return InvokerHelper.createList(values);
    }

    public static Wrapper createPojoWrapper(Object val, Class clazz) {
        return new PojoWrapper(val, clazz);
    }

    public static Wrapper createGroovyObjectWrapper(GroovyObject val, Class clazz) {
        return new GroovyObjectWrapper(val, clazz);
    }

    public static Map createMap(Object[] values) {
        return InvokerHelper.createMap(values);
    }

    public static List createRange(Object from, Object to, boolean inclusive) throws Throwable {
        if (from instanceof Integer && to instanceof Integer) {
            int ifrom = (Integer)from;
            int ito = (Integer)to;
            if (inclusive || ifrom != ito) {
                return new IntRange(inclusive, ifrom, ito);
            }
        }
        if (!inclusive) {
            if (ScriptBytecodeAdapter.compareEqual(from, to)) {
                return new EmptyRange((Comparable)from);
            }
            to = ScriptBytecodeAdapter.compareGreaterThan(from, to) ? ScriptBytecodeAdapter.invokeMethod0(ScriptBytecodeAdapter.class, to, "next") : ScriptBytecodeAdapter.invokeMethod0(ScriptBytecodeAdapter.class, to, "previous");
        }
        return new ObjectRange((Comparable)from, (Comparable)to);
    }

    public static void assertFailed(Object expression, Object message) {
        InvokerHelper.assertFailed(expression, message);
    }

    public static boolean isCase(Object switchValue, Object caseExpression) throws Throwable {
        if (caseExpression == null) {
            return switchValue == null;
        }
        return DefaultTypeTransformation.castToBoolean(ScriptBytecodeAdapter.invokeMethodN(caseExpression.getClass(), caseExpression, "isCase", new Object[]{switchValue}));
    }

    public static boolean compareIdentical(Object left, Object right) {
        return left == right;
    }

    public static boolean compareNotIdentical(Object left, Object right) {
        return left != right;
    }

    public static boolean compareEqual(Object left, Object right) {
        Class<?> rightClass;
        if (left == right) {
            return true;
        }
        Class<?> leftClass = left == null ? null : left.getClass();
        Class<?> clazz = rightClass = right == null ? null : right.getClass();
        if (leftClass == Integer.class && rightClass == Integer.class) {
            return left.equals(right);
        }
        if (leftClass == Double.class && rightClass == Double.class) {
            return left.equals(right);
        }
        if (leftClass == Long.class && rightClass == Long.class) {
            return left.equals(right);
        }
        return DefaultTypeTransformation.compareEqual(left, right);
    }

    public static boolean compareNotEqual(Object left, Object right) {
        return !ScriptBytecodeAdapter.compareEqual(left, right);
    }

    public static Integer compareTo(Object left, Object right) {
        int answer = DefaultTypeTransformation.compareTo(left, right);
        if (answer == 0) {
            return ZERO;
        }
        return answer > 0 ? ONE : MINUS_ONE;
    }

    public static boolean compareLessThan(Object left, Object right) {
        Class<?> rightClass;
        Class<?> leftClass = left == null ? null : left.getClass();
        Class<?> clazz = rightClass = right == null ? null : right.getClass();
        if (leftClass == Integer.class && rightClass == Integer.class) {
            return (Integer)left < (Integer)right;
        }
        if (leftClass == Double.class && rightClass == Double.class) {
            return (Double)left < (Double)right;
        }
        if (leftClass == Long.class && rightClass == Long.class) {
            return (Long)left < (Long)right;
        }
        return ScriptBytecodeAdapter.compareTo(left, right) < 0;
    }

    public static boolean compareLessThanEqual(Object left, Object right) {
        Class<?> rightClass;
        Class<?> leftClass = left == null ? null : left.getClass();
        Class<?> clazz = rightClass = right == null ? null : right.getClass();
        if (leftClass == Integer.class && rightClass == Integer.class) {
            return (Integer)left <= (Integer)right;
        }
        if (leftClass == Double.class && rightClass == Double.class) {
            return (Double)left <= (Double)right;
        }
        if (leftClass == Long.class && rightClass == Long.class) {
            return (Long)left <= (Long)right;
        }
        return ScriptBytecodeAdapter.compareTo(left, right) <= 0;
    }

    public static boolean compareGreaterThan(Object left, Object right) {
        Class<?> rightClass;
        Class<?> leftClass = left == null ? null : left.getClass();
        Class<?> clazz = rightClass = right == null ? null : right.getClass();
        if (leftClass == Integer.class && rightClass == Integer.class) {
            return (Integer)left > (Integer)right;
        }
        if (leftClass == Double.class && rightClass == Double.class) {
            return (Double)left > (Double)right;
        }
        if (leftClass == Long.class && rightClass == Long.class) {
            return (Long)left > (Long)right;
        }
        return ScriptBytecodeAdapter.compareTo(left, right) > 0;
    }

    public static boolean compareGreaterThanEqual(Object left, Object right) {
        Class<?> rightClass;
        Class<?> leftClass = left == null ? null : left.getClass();
        Class<?> clazz = rightClass = right == null ? null : right.getClass();
        if (leftClass == Integer.class && rightClass == Integer.class) {
            return (Integer)left >= (Integer)right;
        }
        if (leftClass == Double.class && rightClass == Double.class) {
            return (Double)left >= (Double)right;
        }
        if (leftClass == Long.class && rightClass == Long.class) {
            return (Long)left >= (Long)right;
        }
        return ScriptBytecodeAdapter.compareTo(left, right) >= 0;
    }

    public static Pattern regexPattern(Object regex) {
        return StringGroovyMethods.bitwiseNegate(regex.toString());
    }

    public static Matcher findRegex(Object left, Object right) throws Throwable {
        return InvokerHelper.findRegex(left, right);
    }

    public static boolean matchRegex(Object left, Object right) {
        return InvokerHelper.matchRegex(left, right);
    }

    public static Object[] despreadList(Object[] args, Object[] spreads, int[] positions) {
        ArrayList<Object> ret = new ArrayList<Object>();
        int argsPos = 0;
        int spreadPos = 0;
        for (int pos = 0; pos < positions.length; ++pos) {
            while (argsPos < positions[pos]) {
                ret.add(args[argsPos]);
                ++argsPos;
            }
            Object value = spreads[spreadPos];
            if (value == null) {
                ret.add(null);
            } else if (value instanceof List) {
                ret.addAll((List)value);
            } else if (value.getClass().isArray()) {
                ret.addAll(DefaultTypeTransformation.primitiveArrayToList(value));
            } else {
                throw new IllegalArgumentException("cannot spread the type " + value.getClass().getName() + " with value " + value);
            }
            ++spreadPos;
        }
        while (argsPos < args.length) {
            ret.add(args[argsPos]);
            ++argsPos;
        }
        return ret.toArray();
    }

    public static Object spreadMap(Object value) {
        return InvokerHelper.spreadMap(value);
    }

    public static Object unaryMinus(Object value) throws Throwable {
        return InvokerHelper.unaryMinus(value);
    }

    public static Object unaryPlus(Object value) throws Throwable {
        try {
            return InvokerHelper.unaryPlus(value);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static Object bitwiseNegate(Object value) throws Throwable {
        try {
            return InvokerHelper.bitwiseNegate(value);
        }
        catch (GroovyRuntimeException gre) {
            throw ScriptBytecodeAdapter.unwrap(gre);
        }
    }

    public static MetaClass initMetaClass(Object object) {
        return InvokerHelper.getMetaClass(object.getClass());
    }
}

