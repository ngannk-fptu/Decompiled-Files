/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  ch.qos.logback.core.util.Loader
 *  ch.qos.logback.core.util.OptionHelper
 */
package ch.qos.logback.classic.util;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.selector.ContextJNDISelector;
import ch.qos.logback.classic.selector.ContextSelector;
import ch.qos.logback.classic.selector.DefaultContextSelector;
import ch.qos.logback.core.util.Loader;
import ch.qos.logback.core.util.OptionHelper;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ContextSelectorStaticBinder {
    static ContextSelectorStaticBinder singleton = new ContextSelectorStaticBinder();
    ContextSelector contextSelector;
    Object key;

    public static ContextSelectorStaticBinder getSingleton() {
        return singleton;
    }

    public void init(LoggerContext defaultLoggerContext, Object key) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (this.key == null) {
            this.key = key;
        } else if (this.key != key) {
            throw new IllegalAccessException("Only certain classes can access this method.");
        }
        String contextSelectorStr = OptionHelper.getSystemProperty((String)"logback.ContextSelector");
        this.contextSelector = contextSelectorStr == null ? new DefaultContextSelector(defaultLoggerContext) : (contextSelectorStr.equals("JNDI") ? new ContextJNDISelector(defaultLoggerContext) : ContextSelectorStaticBinder.dynamicalContextSelector(defaultLoggerContext, contextSelectorStr));
    }

    static ContextSelector dynamicalContextSelector(LoggerContext defaultLoggerContext, String contextSelectorStr) throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Class contextSelectorClass = Loader.loadClass((String)contextSelectorStr);
        Constructor cons = contextSelectorClass.getConstructor(LoggerContext.class);
        return (ContextSelector)cons.newInstance(new Object[]{defaultLoggerContext});
    }

    public ContextSelector getContextSelector() {
        return this.contextSelector;
    }
}

