/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.Nestable;

public class ExceptionUtils {
    static final String WRAPPED_MARKER = " [wrapped] ";
    private static String[] CAUSE_METHOD_NAMES;
    private static final Method THROWABLE_CAUSE_METHOD;
    private static final Method THROWABLE_INITCAUSE_METHOD;
    static /* synthetic */ Class class$java$lang$Throwable;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void addCauseMethodName(String methodName) {
        ArrayList list;
        if (!StringUtils.isNotEmpty(methodName) || ExceptionUtils.isCauseMethodName(methodName) || !(list = ExceptionUtils.getCauseMethodNameList()).add(methodName)) return;
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            CAUSE_METHOD_NAMES = ExceptionUtils.toArray(list);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void removeCauseMethodName(String methodName) {
        ArrayList list;
        if (!StringUtils.isNotEmpty(methodName) || !(list = ExceptionUtils.getCauseMethodNameList()).remove(methodName)) return;
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            CAUSE_METHOD_NAMES = ExceptionUtils.toArray(list);
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    public static boolean setCause(Throwable target, Throwable cause) {
        if (target == null) {
            throw new NullArgumentException("target");
        }
        Object[] causeArgs = new Object[]{cause};
        boolean modifiedTarget = false;
        if (THROWABLE_INITCAUSE_METHOD != null) {
            try {
                THROWABLE_INITCAUSE_METHOD.invoke((Object)target, causeArgs);
                modifiedTarget = true;
            }
            catch (IllegalAccessException ignored) {
            }
            catch (InvocationTargetException ignored) {
                // empty catch block
            }
        }
        try {
            Method setCauseMethod = target.getClass().getMethod("setCause", class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable);
            setCauseMethod.invoke((Object)target, causeArgs);
            modifiedTarget = true;
        }
        catch (NoSuchMethodException ignored) {
        }
        catch (IllegalAccessException ignored) {
        }
        catch (InvocationTargetException ignored) {
            // empty catch block
        }
        return modifiedTarget;
    }

    private static String[] toArray(List list) {
        return list.toArray(new String[list.size()]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static ArrayList getCauseMethodNameList() {
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            // ** MonitorExit[var0] (shouldn't be in output)
            return new ArrayList<String>(Arrays.asList(CAUSE_METHOD_NAMES));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isCauseMethodName(String methodName) {
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ArrayUtils.indexOf(CAUSE_METHOD_NAMES, methodName) >= 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Throwable getCause(Throwable throwable) {
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return ExceptionUtils.getCause(throwable, CAUSE_METHOD_NAMES);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Converted monitor instructions to comments
     * Lifted jumps to return sites
     */
    public static Throwable getCause(Throwable throwable, String[] methodNames) {
        String methodName;
        if (throwable == null) {
            return null;
        }
        Throwable cause = ExceptionUtils.getCauseUsingWellKnownTypes(throwable);
        if (cause != null) return cause;
        if (methodNames == null) {
            String[] stringArray = CAUSE_METHOD_NAMES;
            // MONITORENTER : CAUSE_METHOD_NAMES
            methodNames = CAUSE_METHOD_NAMES;
            // MONITOREXIT : stringArray
        }
        for (int i = 0; i < methodNames.length && ((methodName = methodNames[i]) == null || (cause = ExceptionUtils.getCauseUsingMethodName(throwable, methodName)) == null); ++i) {
        }
        if (cause != null) return cause;
        return ExceptionUtils.getCauseUsingFieldName(throwable, "detail");
    }

    public static Throwable getRootCause(Throwable throwable) {
        List list = ExceptionUtils.getThrowableList(throwable);
        return list.size() < 2 ? null : (Throwable)list.get(list.size() - 1);
    }

    private static Throwable getCauseUsingWellKnownTypes(Throwable throwable) {
        if (throwable instanceof Nestable) {
            return ((Nestable)((Object)throwable)).getCause();
        }
        if (throwable instanceof SQLException) {
            return ((SQLException)throwable).getNextException();
        }
        if (throwable instanceof InvocationTargetException) {
            return ((InvocationTargetException)throwable).getTargetException();
        }
        return null;
    }

    private static Throwable getCauseUsingMethodName(Throwable throwable, String methodName) {
        Method method = null;
        try {
            method = throwable.getClass().getMethod(methodName, null);
        }
        catch (NoSuchMethodException ignored) {
        }
        catch (SecurityException ignored) {
            // empty catch block
        }
        if (method != null && (class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(method.getReturnType())) {
            try {
                return (Throwable)method.invoke((Object)throwable, ArrayUtils.EMPTY_OBJECT_ARRAY);
            }
            catch (IllegalAccessException ignored) {
            }
            catch (IllegalArgumentException ignored) {
            }
            catch (InvocationTargetException invocationTargetException) {
                // empty catch block
            }
        }
        return null;
    }

    private static Throwable getCauseUsingFieldName(Throwable throwable, String fieldName) {
        Field field = null;
        try {
            field = throwable.getClass().getField(fieldName);
        }
        catch (NoSuchFieldException ignored) {
        }
        catch (SecurityException ignored) {
            // empty catch block
        }
        if (field != null && (class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).isAssignableFrom(field.getType())) {
            try {
                return (Throwable)field.get(throwable);
            }
            catch (IllegalAccessException ignored) {
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return null;
    }

    public static boolean isThrowableNested() {
        return THROWABLE_CAUSE_METHOD != null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static boolean isNestedThrowable(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof Nestable) {
            return true;
        }
        if (throwable instanceof SQLException) {
            return true;
        }
        if (throwable instanceof InvocationTargetException) {
            return true;
        }
        if (ExceptionUtils.isThrowableNested()) {
            return true;
        }
        Class<?> cls = throwable.getClass();
        String[] stringArray = CAUSE_METHOD_NAMES;
        synchronized (CAUSE_METHOD_NAMES) {
            int isize = CAUSE_METHOD_NAMES.length;
            for (int i = 0; i < isize; ++i) {
                try {
                    Method method = cls.getMethod(CAUSE_METHOD_NAMES[i], null);
                    if (method == null || !(class$java$lang$Throwable == null ? ExceptionUtils.class$("java.lang.Throwable") : class$java$lang$Throwable).isAssignableFrom(method.getReturnType())) continue;
                    // ** MonitorExit[var2_2] (shouldn't be in output)
                    return true;
                }
                catch (NoSuchMethodException ignored) {
                    continue;
                }
                catch (SecurityException ignored) {
                    // empty catch block
                }
            }
            // ** MonitorExit[var2_2] (shouldn't be in output)
            try {
                Field field = cls.getField("detail");
                if (field != null) {
                    return true;
                }
            }
            catch (NoSuchFieldException ignored) {
            }
            catch (SecurityException securityException) {
                // empty catch block
            }
            return false;
        }
    }

    public static int getThrowableCount(Throwable throwable) {
        return ExceptionUtils.getThrowableList(throwable).size();
    }

    public static Throwable[] getThrowables(Throwable throwable) {
        List list = ExceptionUtils.getThrowableList(throwable);
        return list.toArray(new Throwable[list.size()]);
    }

    public static List getThrowableList(Throwable throwable) {
        ArrayList<Throwable> list = new ArrayList<Throwable>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = ExceptionUtils.getCause(throwable);
        }
        return list;
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz) {
        return ExceptionUtils.indexOf(throwable, clazz, 0, false);
    }

    public static int indexOfThrowable(Throwable throwable, Class clazz, int fromIndex) {
        return ExceptionUtils.indexOf(throwable, clazz, fromIndex, false);
    }

    public static int indexOfType(Throwable throwable, Class type) {
        return ExceptionUtils.indexOf(throwable, type, 0, true);
    }

    public static int indexOfType(Throwable throwable, Class type, int fromIndex) {
        return ExceptionUtils.indexOf(throwable, type, fromIndex, true);
    }

    private static int indexOf(Throwable throwable, Class type, int fromIndex, boolean subclass) {
        Throwable[] throwables;
        if (throwable == null || type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (fromIndex >= (throwables = ExceptionUtils.getThrowables(throwable)).length) {
            return -1;
        }
        if (subclass) {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (!type.isAssignableFrom(throwables[i].getClass())) continue;
                return i;
            }
        } else {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (!type.equals(throwables[i].getClass())) continue;
                return i;
            }
        }
        return -1;
    }

    public static void printRootCauseStackTrace(Throwable throwable) {
        ExceptionUtils.printRootCauseStackTrace(throwable, System.err);
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintStream stream) {
        if (throwable == null) {
            return;
        }
        if (stream == null) {
            throw new IllegalArgumentException("The PrintStream must not be null");
        }
        String[] trace = ExceptionUtils.getRootCauseStackTrace(throwable);
        for (int i = 0; i < trace.length; ++i) {
            stream.println(trace[i]);
        }
        stream.flush();
    }

    public static void printRootCauseStackTrace(Throwable throwable, PrintWriter writer) {
        if (throwable == null) {
            return;
        }
        if (writer == null) {
            throw new IllegalArgumentException("The PrintWriter must not be null");
        }
        String[] trace = ExceptionUtils.getRootCauseStackTrace(throwable);
        for (int i = 0; i < trace.length; ++i) {
            writer.println(trace[i]);
        }
        writer.flush();
    }

    public static String[] getRootCauseStackTrace(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        Throwable[] throwables = ExceptionUtils.getThrowables(throwable);
        int count = throwables.length;
        ArrayList<String> frames = new ArrayList<String>();
        List nextTrace = ExceptionUtils.getStackFrameList(throwables[count - 1]);
        int i = count;
        while (--i >= 0) {
            List trace = nextTrace;
            if (i != 0) {
                nextTrace = ExceptionUtils.getStackFrameList(throwables[i - 1]);
                ExceptionUtils.removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            } else {
                frames.add(WRAPPED_MARKER + throwables[i].toString());
            }
            for (int j = 0; j < trace.size(); ++j) {
                frames.add((String)trace.get(j));
            }
        }
        return frames.toArray(new String[0]);
    }

    public static void removeCommonFrames(List causeFrames, List wrapperFrames) {
        if (causeFrames == null || wrapperFrames == null) {
            throw new IllegalArgumentException("The List must not be null");
        }
        int causeFrameIndex = causeFrames.size() - 1;
        for (int wrapperFrameIndex = wrapperFrames.size() - 1; causeFrameIndex >= 0 && wrapperFrameIndex >= 0; --causeFrameIndex, --wrapperFrameIndex) {
            String wrapperFrame;
            String causeFrame = (String)causeFrames.get(causeFrameIndex);
            if (!causeFrame.equals(wrapperFrame = (String)wrapperFrames.get(wrapperFrameIndex))) continue;
            causeFrames.remove(causeFrameIndex);
        }
    }

    public static String getFullStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        Throwable[] ts = ExceptionUtils.getThrowables(throwable);
        for (int i = 0; i < ts.length; ++i) {
            ts[i].printStackTrace(pw);
            if (ExceptionUtils.isNestedThrowable(ts[i])) break;
        }
        return sw.getBuffer().toString();
    }

    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static String[] getStackFrames(Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return ExceptionUtils.getStackFrames(ExceptionUtils.getStackTrace(throwable));
    }

    static String[] getStackFrames(String stackTrace) {
        String linebreak = SystemUtils.LINE_SEPARATOR;
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        ArrayList<String> list = new ArrayList<String>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return ExceptionUtils.toArray(list);
    }

    static List getStackFrameList(Throwable t) {
        String stackTrace = ExceptionUtils.getStackTrace(t);
        String linebreak = SystemUtils.LINE_SEPARATOR;
        StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        ArrayList<String> list = new ArrayList<String>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            String token = frames.nextToken();
            int at = token.indexOf("at");
            if (at != -1 && token.substring(0, at).trim().length() == 0) {
                traceStarted = true;
                list.add(token);
                continue;
            }
            if (!traceStarted) continue;
            break;
        }
        return list;
    }

    public static String getMessage(Throwable th) {
        if (th == null) {
            return "";
        }
        String clsName = ClassUtils.getShortClassName(th, null);
        String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }

    public static String getRootCauseMessage(Throwable th) {
        Throwable root = ExceptionUtils.getRootCause(th);
        root = root == null ? th : root;
        return ExceptionUtils.getMessage(root);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Method causeMethod;
        CAUSE_METHOD_NAMES = new String[]{"getCause", "getNextException", "getTargetException", "getException", "getSourceException", "getRootCause", "getCausedByException", "getNested", "getLinkedException", "getNestedException", "getLinkedCause", "getThrowable"};
        try {
            causeMethod = (class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).getMethod("getCause", null);
        }
        catch (Exception e) {
            causeMethod = null;
        }
        THROWABLE_CAUSE_METHOD = causeMethod;
        try {
            causeMethod = (class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable).getMethod("initCause", class$java$lang$Throwable == null ? (class$java$lang$Throwable = ExceptionUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable);
        }
        catch (Exception e) {
            causeMethod = null;
        }
        THROWABLE_INITCAUSE_METHOD = causeMethod;
    }
}

