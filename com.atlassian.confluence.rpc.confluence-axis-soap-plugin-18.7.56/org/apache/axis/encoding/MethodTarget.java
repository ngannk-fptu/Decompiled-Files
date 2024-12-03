/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.Target;
import org.apache.axis.utils.Messages;
import org.apache.commons.logging.Log;
import org.xml.sax.SAXException;

public class MethodTarget
implements Target {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$MethodTarget == null ? (class$org$apache$axis$encoding$MethodTarget = MethodTarget.class$("org.apache.axis.encoding.MethodTarget")) : class$org$apache$axis$encoding$MethodTarget).getName());
    private Object targetObject;
    private Method targetMethod;
    private static final Class[] objArg = new Class[]{class$java$lang$Object == null ? (class$java$lang$Object = MethodTarget.class$("java.lang.Object")) : class$java$lang$Object};
    static /* synthetic */ Class class$org$apache$axis$encoding$MethodTarget;
    static /* synthetic */ Class class$java$lang$Object;

    public MethodTarget(Object targetObject, Method targetMethod) {
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
    }

    public MethodTarget(Object targetObject, String methodName) throws NoSuchMethodException {
        this.targetObject = targetObject;
        Class<?> cls = targetObject.getClass();
        this.targetMethod = cls.getMethod(methodName, objArg);
    }

    public void set(Object value) throws SAXException {
        try {
            this.targetMethod.invoke(this.targetObject, value);
        }
        catch (IllegalAccessException accEx) {
            log.error((Object)Messages.getMessage("illegalAccessException00"), (Throwable)accEx);
            throw new SAXException(accEx);
        }
        catch (IllegalArgumentException argEx) {
            log.error((Object)Messages.getMessage("illegalArgumentException00"), (Throwable)argEx);
            throw new SAXException(argEx);
        }
        catch (InvocationTargetException targetEx) {
            log.error((Object)Messages.getMessage("invocationTargetException00"), (Throwable)targetEx);
            throw new SAXException(targetEx);
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

