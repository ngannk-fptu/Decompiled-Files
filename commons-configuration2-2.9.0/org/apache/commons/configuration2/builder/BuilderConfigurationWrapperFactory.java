/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.configuration2.ConfigurationUtils;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.builder.ConfigurationBuilder;
import org.apache.commons.configuration2.event.EventSource;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class BuilderConfigurationWrapperFactory {
    private final EventSourceSupport eventSourceSupport;

    public BuilderConfigurationWrapperFactory(EventSourceSupport evSrcSupport) {
        this.eventSourceSupport = evSrcSupport;
    }

    public BuilderConfigurationWrapperFactory() {
        this(EventSourceSupport.NONE);
    }

    public <T extends ImmutableConfiguration> T createBuilderConfigurationWrapper(Class<T> ifcClass, ConfigurationBuilder<? extends T> builder) {
        return BuilderConfigurationWrapperFactory.createBuilderConfigurationWrapper(ifcClass, builder, this.getEventSourceSupport());
    }

    public EventSourceSupport getEventSourceSupport() {
        return this.eventSourceSupport;
    }

    public static <T extends ImmutableConfiguration> T createBuilderConfigurationWrapper(Class<T> ifcClass, ConfigurationBuilder<? extends T> builder, EventSourceSupport evSrcSupport) {
        if (ifcClass == null) {
            throw new IllegalArgumentException("Interface class must not be null!");
        }
        if (builder == null) {
            throw new IllegalArgumentException("Builder must not be null!");
        }
        return (T)((ImmutableConfiguration)ifcClass.cast(Proxy.newProxyInstance(BuilderConfigurationWrapperFactory.class.getClassLoader(), BuilderConfigurationWrapperFactory.getSupportedInterfaces(ifcClass, evSrcSupport), (InvocationHandler)new BuilderConfigurationWrapperInvocationHandler(builder, evSrcSupport))));
    }

    private static Class<?>[] getSupportedInterfaces(Class<?> ifcClass, EventSourceSupport evSrcSupport) {
        Class[] classArray;
        if (EventSourceSupport.NONE == evSrcSupport) {
            Class[] classArray2 = new Class[1];
            classArray = classArray2;
            classArray2[0] = ifcClass;
        } else {
            Class[] classArray3 = new Class[2];
            classArray3[0] = EventSource.class;
            classArray = classArray3;
            classArray3[1] = ifcClass;
        }
        return classArray;
    }

    private static class BuilderConfigurationWrapperInvocationHandler
    implements InvocationHandler {
        private final ConfigurationBuilder<? extends ImmutableConfiguration> builder;
        private final EventSourceSupport eventSourceSupport;

        public BuilderConfigurationWrapperInvocationHandler(ConfigurationBuilder<? extends ImmutableConfiguration> wrappedBuilder, EventSourceSupport evSrcSupport) {
            this.builder = wrappedBuilder;
            this.eventSourceSupport = evSrcSupport;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws ReflectiveOperationException, ConfigurationException {
            return EventSource.class.equals(method.getDeclaringClass()) ? this.handleEventSourceInvocation(method, args) : this.handleConfigurationInvocation(method, args);
        }

        private Object handleConfigurationInvocation(Method method, Object[] args) throws ReflectiveOperationException, ConfigurationException {
            return method.invoke((Object)this.builder.getConfiguration(), args);
        }

        private Object handleEventSourceInvocation(Method method, Object ... args) throws ReflectiveOperationException {
            return method.invoke(EventSourceSupport.DUMMY == this.eventSourceSupport ? ConfigurationUtils.asEventSource(this, true) : this.builder, args);
        }
    }

    public static enum EventSourceSupport {
        NONE,
        DUMMY,
        BUILDER;

    }
}

