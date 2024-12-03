/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.utils.bytecode;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.bytecode.ParamReader;
import org.apache.commons.logging.Log;

public class ParamNameExtractor {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$utils$bytecode$ParamNameExtractor == null ? (class$org$apache$axis$utils$bytecode$ParamNameExtractor = ParamNameExtractor.class$("org.apache.axis.utils.bytecode.ParamNameExtractor")) : class$org$apache$axis$utils$bytecode$ParamNameExtractor).getName());
    static /* synthetic */ Class class$org$apache$axis$utils$bytecode$ParamNameExtractor;

    public static String[] getParameterNamesFromDebugInfo(Method method) {
        int numParams = method.getParameterTypes().length;
        if (numParams == 0) {
            return null;
        }
        Class<?> c = method.getDeclaringClass();
        if (Proxy.isProxyClass(c)) {
            return null;
        }
        try {
            ParamReader pr = new ParamReader(c);
            String[] names = pr.getParameterNames(method);
            return names;
        }
        catch (IOException e) {
            log.info((Object)(Messages.getMessage("error00") + ":" + e));
            return null;
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

