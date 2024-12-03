/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.io.File;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Pattern;
import org.aspectj.bridge.IMessage;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.Traceable;

public abstract class AbstractTrace
implements Trace {
    private static final Pattern packagePrefixPattern = Pattern.compile("([^.])[^.]*(\\.)");
    protected Class<?> tracedClass;
    private static SimpleDateFormat timeFormat;

    protected AbstractTrace(Class clazz) {
        this.tracedClass = clazz;
    }

    @Override
    public abstract void enter(String var1, Object var2, Object[] var3);

    @Override
    public abstract void enter(String var1, Object var2);

    @Override
    public abstract void exit(String var1, Object var2);

    @Override
    public abstract void exit(String var1, Throwable var2);

    public void enter(String methodName) {
        this.enter(methodName, null, null);
    }

    @Override
    public void enter(String methodName, Object thiz, Object arg) {
        this.enter(methodName, thiz, new Object[]{arg});
    }

    @Override
    public void enter(String methodName, Object thiz, boolean z) {
        this.enter(methodName, thiz, new Boolean(z));
    }

    @Override
    public void exit(String methodName, boolean b) {
        this.exit(methodName, new Boolean(b));
    }

    @Override
    public void exit(String methodName, int i) {
        this.exit(methodName, new Integer(i));
    }

    @Override
    public void event(String methodName, Object thiz, Object arg) {
        this.event(methodName, thiz, new Object[]{arg});
    }

    @Override
    public void warn(String message) {
        this.warn(message, null);
    }

    @Override
    public void error(String message) {
        this.error(message, null);
    }

    @Override
    public void fatal(String message) {
        this.fatal(message, null);
    }

    protected String formatMessage(String kind, String className, String methodName, Object thiz, Object[] args) {
        StringBuffer message = new StringBuffer();
        Date now = new Date();
        message.append(AbstractTrace.formatDate(now)).append(" ");
        message.append(Thread.currentThread().getName()).append(" ");
        message.append(kind).append(" ");
        message.append(this.formatClassName(className));
        message.append(".").append(methodName);
        if (thiz != null) {
            message.append(" ").append(this.formatObj(thiz));
        }
        if (args != null) {
            message.append(" ").append(this.formatArgs(args));
        }
        return message.toString();
    }

    private String formatClassName(String className) {
        return packagePrefixPattern.matcher(className).replaceAll("$1.");
    }

    protected String formatMessage(String kind, String text, Throwable th) {
        StringBuffer message = new StringBuffer();
        Date now = new Date();
        message.append(AbstractTrace.formatDate(now)).append(" ");
        message.append(Thread.currentThread().getName()).append(" ");
        message.append(kind).append(" ");
        message.append(text);
        if (th != null) {
            message.append(" ").append(this.formatObj(th));
        }
        return message.toString();
    }

    private static String formatDate(Date date) {
        if (timeFormat == null) {
            timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        }
        return timeFormat.format(date);
    }

    protected Object formatObj(Object obj) {
        if (obj == null || obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Exception || obj instanceof Character || obj instanceof Class || obj instanceof File || obj instanceof StringBuffer || obj instanceof URL || obj instanceof IMessage.Kind) {
            return obj;
        }
        if (obj.getClass().isArray()) {
            return this.formatArray(obj);
        }
        if (obj instanceof Collection) {
            return this.formatCollection((Collection)obj);
        }
        try {
            if (obj instanceof Traceable) {
                return ((Traceable)obj).toTraceString();
            }
            return this.formatClassName(obj.getClass().getName()) + "@" + Integer.toHexString(System.identityHashCode(obj));
        }
        catch (Exception ex) {
            return obj.getClass().getName() + "@FFFFFFFF";
        }
    }

    protected String formatArray(Object obj) {
        return obj.getClass().getComponentType().getName() + "[" + Array.getLength(obj) + "]";
    }

    protected String formatCollection(Collection<?> c) {
        return c.getClass().getName() + "(" + c.size() + ")";
    }

    protected String formatArgs(Object[] args) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; ++i) {
            sb.append(this.formatObj(args[i]));
            if (i >= args.length - 1) continue;
            sb.append(", ");
        }
        return sb.toString();
    }

    protected Object[] formatObjects(Object[] args) {
        for (int i = 0; i < args.length; ++i) {
            args[i] = this.formatObj(args[i]);
        }
        return args;
    }
}

