/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder.fluent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.DatabaseBuilderParametersImpl;
import org.apache.commons.configuration2.builder.DefaultParametersHandler;
import org.apache.commons.configuration2.builder.DefaultParametersManager;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.HierarchicalBuilderParametersImpl;
import org.apache.commons.configuration2.builder.INIBuilderParametersImpl;
import org.apache.commons.configuration2.builder.JndiBuilderParametersImpl;
import org.apache.commons.configuration2.builder.PropertiesBuilderParametersImpl;
import org.apache.commons.configuration2.builder.XMLBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.CombinedBuilderParametersImpl;
import org.apache.commons.configuration2.builder.combined.MultiFileBuilderParametersImpl;
import org.apache.commons.configuration2.builder.fluent.CombinedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.DatabaseBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.FileBasedBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.HierarchicalBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.INIBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.JndiBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.MultiFileBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.PropertiesBuilderParameters;
import org.apache.commons.configuration2.builder.fluent.XMLBuilderParameters;

public final class Parameters {
    private final DefaultParametersManager defaultParametersManager;

    public Parameters() {
        this(null);
    }

    public Parameters(DefaultParametersManager manager) {
        this.defaultParametersManager = manager != null ? manager : new DefaultParametersManager();
    }

    public DefaultParametersManager getDefaultParametersManager() {
        return this.defaultParametersManager;
    }

    public <T> void registerDefaultsHandler(Class<T> paramsClass, DefaultParametersHandler<? super T> handler) {
        this.getDefaultParametersManager().registerDefaultsHandler(paramsClass, handler);
    }

    public <T> void registerDefaultsHandler(Class<T> paramsClass, DefaultParametersHandler<? super T> handler, Class<?> startClass) {
        this.getDefaultParametersManager().registerDefaultsHandler(paramsClass, handler, startClass);
    }

    public BasicBuilderParameters basic() {
        return new BasicBuilderParameters();
    }

    public FileBasedBuilderParameters fileBased() {
        return this.createParametersProxy(new FileBasedBuilderParametersImpl(), FileBasedBuilderParameters.class, new Class[0]);
    }

    public CombinedBuilderParameters combined() {
        return this.createParametersProxy(new CombinedBuilderParametersImpl(), CombinedBuilderParameters.class, new Class[0]);
    }

    public JndiBuilderParameters jndi() {
        return this.createParametersProxy(new JndiBuilderParametersImpl(), JndiBuilderParameters.class, new Class[0]);
    }

    public HierarchicalBuilderParameters hierarchical() {
        return this.createParametersProxy(new HierarchicalBuilderParametersImpl(), HierarchicalBuilderParameters.class, FileBasedBuilderParameters.class);
    }

    public XMLBuilderParameters xml() {
        return this.createParametersProxy(new XMLBuilderParametersImpl(), XMLBuilderParameters.class, FileBasedBuilderParameters.class, HierarchicalBuilderParameters.class);
    }

    public PropertiesBuilderParameters properties() {
        return this.createParametersProxy(new PropertiesBuilderParametersImpl(), PropertiesBuilderParameters.class, FileBasedBuilderParameters.class);
    }

    public MultiFileBuilderParameters multiFile() {
        return this.createParametersProxy(new MultiFileBuilderParametersImpl(), MultiFileBuilderParameters.class, new Class[0]);
    }

    public DatabaseBuilderParameters database() {
        return this.createParametersProxy(new DatabaseBuilderParametersImpl(), DatabaseBuilderParameters.class, new Class[0]);
    }

    public INIBuilderParameters ini() {
        return this.createParametersProxy(new INIBuilderParametersImpl(), INIBuilderParameters.class, FileBasedBuilderParameters.class, HierarchicalBuilderParameters.class);
    }

    private <T> T createParametersProxy(Object target, Class<T> ifcClass, Class<?> ... superIfcs) {
        Class[] ifcClasses = new Class[1 + superIfcs.length];
        ifcClasses[0] = ifcClass;
        System.arraycopy(superIfcs, 0, ifcClasses, 1, superIfcs.length);
        Object obj = Proxy.newProxyInstance(Parameters.class.getClassLoader(), ifcClasses, (InvocationHandler)new ParametersIfcInvocationHandler(target));
        this.getDefaultParametersManager().initializeParameters((BuilderParameters)obj);
        return ifcClass.cast(obj);
    }

    private static class ParametersIfcInvocationHandler
    implements InvocationHandler {
        private final Object target;

        public ParametersIfcInvocationHandler(Object targetObj) {
            this.target = targetObj;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object result = method.invoke(this.target, args);
            return ParametersIfcInvocationHandler.isFluentResult(method) ? proxy : result;
        }

        private static boolean isFluentResult(Method method) {
            Class<?> declaringClass = method.getDeclaringClass();
            return declaringClass.isInterface() && !declaringClass.equals(BuilderParameters.class);
        }
    }
}

