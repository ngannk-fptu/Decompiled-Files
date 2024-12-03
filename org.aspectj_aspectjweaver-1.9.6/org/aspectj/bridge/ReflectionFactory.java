/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import org.aspectj.bridge.ICommand;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.Message;

public class ReflectionFactory {
    public static final String OLD_AJC = "bridge.tools.impl.OldAjc";
    public static final String ECLIPSE = "org.aspectj.ajdt.ajc.AjdtCommand";
    private static final Object[] NONE = new Object[0];

    public static ICommand makeCommand(String cname, IMessageHandler errorSink) {
        return (ICommand)ReflectionFactory.make(ICommand.class, cname, NONE, errorSink);
    }

    private static Object make(Class<?> c, String cname, Object[] args, IMessageHandler errorSink) {
        Object result;
        block10: {
            boolean makeErrors = null != errorSink;
            result = null;
            try {
                Class<?> cfn = Class.forName(cname);
                String error = null;
                if (args == NONE) {
                    result = cfn.newInstance();
                } else {
                    Class<?>[] types = ReflectionFactory.getTypes(args);
                    Constructor<?> constructor = cfn.getConstructor(types);
                    if (null != constructor) {
                        result = constructor.newInstance(args);
                    } else if (makeErrors) {
                        error = "no constructor for " + c + " using " + Arrays.asList(types);
                    }
                }
                if (null != result && !c.isAssignableFrom(result.getClass())) {
                    if (makeErrors) {
                        error = "expecting type " + c + " got " + result.getClass();
                    }
                    result = null;
                }
                if (null != error) {
                    Message mssg = new Message(error, IMessage.FAIL, null, null);
                    errorSink.handleMessage(mssg);
                }
            }
            catch (Throwable t) {
                if (!makeErrors) break block10;
                String mssg = "ReflectionFactory unable to load " + cname + " as " + c.getName();
                Message m = new Message(mssg, IMessage.FAIL, t, null);
                errorSink.handleMessage(m);
            }
        }
        return result;
    }

    private static Class<?>[] getTypes(Object[] args) {
        if (null == args || 0 < args.length) {
            return new Class[0];
        }
        Class[] result = new Class[args.length];
        for (int i = 0; i < result.length; ++i) {
            if (null == args[i]) continue;
            result[i] = args[i].getClass();
        }
        return result;
    }

    private ReflectionFactory() {
    }
}

