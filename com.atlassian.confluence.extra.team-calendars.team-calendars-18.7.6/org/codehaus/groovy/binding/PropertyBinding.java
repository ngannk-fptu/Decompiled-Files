/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.binding;

import groovy.beans.DefaultPropertyAccessor;
import groovy.beans.PropertyAccessor;
import groovy.lang.GroovyRuntimeException;
import groovy.lang.MissingMethodException;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.codehaus.groovy.binding.AbstractFullBinding;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.InvokerInvocationException;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class PropertyBinding
implements SourceBinding,
TargetBinding,
TriggerBinding {
    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final Logger LOG = Logger.getLogger(PropertyBinding.class.getName());
    private static final Map<Class, Class<? extends PropertyAccessor>> ACCESSORS = new LinkedHashMap<Class, Class<? extends PropertyAccessor>>();
    Object bean;
    String propertyName;
    boolean nonChangeCheck;
    UpdateStrategy updateStrategy;
    private final Object[] lock = new Object[0];
    private PropertyAccessor propertyAccessor;

    private static void registerPropertyAccessors(List<String> lines) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        for (String line : lines) {
            if ((line = line.trim()).startsWith("#")) {
                return;
            }
            String[] parts = line.split("=");
            if (parts.length != 2) continue;
            try {
                ACCESSORS.put(cl.loadClass(parts[0].trim()), cl.loadClass(parts[1].trim()));
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static Enumeration<URL> fetchUrlsFor(String path) {
        try {
            return Thread.currentThread().getContextClassLoader().getResources(path);
        }
        catch (IOException e) {
            return new Enumeration<URL>(){

                @Override
                public boolean hasMoreElements() {
                    return false;
                }

                @Override
                public URL nextElement() {
                    return null;
                }
            };
        }
    }

    public PropertyBinding(Object bean, String propertyName) {
        this(bean, propertyName, (UpdateStrategy)null);
    }

    public PropertyBinding(Object bean, String propertyName, String updateStrategy) {
        this(bean, propertyName, UpdateStrategy.of(updateStrategy));
    }

    public PropertyBinding(Object bean, String propertyName, UpdateStrategy updateStrategy) {
        this.bean = bean;
        this.propertyName = propertyName;
        this.updateStrategy = PropertyBinding.pickUpdateStrategy(bean, updateStrategy);
        if (LOG.isLoggable(Level.FINER)) {
            LOG.finer("Updating with " + (Object)((Object)this.updateStrategy) + " property '" + propertyName + "' of bean " + bean);
        }
        this.setupPropertyReaderAndWriter();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setupPropertyReaderAndWriter() {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            this.propertyAccessor = this.fetchPropertyAccessor(this.bean != null ? this.bean.getClass() : null);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private PropertyAccessor propertyAccessor() {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.propertyAccessor;
        }
    }

    private PropertyAccessor fetchPropertyAccessor(Class klass) {
        if (klass == null) {
            return DefaultPropertyAccessor.INSTANCE;
        }
        Class<? extends PropertyAccessor> accessorClass = ACCESSORS.get(klass);
        if (accessorClass == null) {
            for (Class<?> c : klass.getInterfaces()) {
                PropertyAccessor propertyAccessor = this.fetchPropertyAccessor(c);
                if (propertyAccessor == DefaultPropertyAccessor.INSTANCE) continue;
                return propertyAccessor;
            }
            return this.fetchPropertyAccessor(klass.getSuperclass());
        }
        try {
            return accessorClass.newInstance();
        }
        catch (InstantiationException e) {
            return DefaultPropertyAccessor.INSTANCE;
        }
        catch (IllegalAccessException e) {
            return DefaultPropertyAccessor.INSTANCE;
        }
    }

    public UpdateStrategy getUpdateStrategy() {
        return this.updateStrategy;
    }

    private static UpdateStrategy pickUpdateStrategy(Object bean, UpdateStrategy updateStrategy) {
        if (bean instanceof Component) {
            return UpdateStrategy.MIXED;
        }
        if (updateStrategy != null) {
            return updateStrategy;
        }
        return UpdateStrategy.SAME;
    }

    @Override
    public void updateTargetValue(final Object newValue) {
        Runnable runnable = new Runnable(){

            @Override
            public void run() {
                Object sourceValue = PropertyBinding.this.getSourceValue();
                if (sourceValue == null && newValue == null || DefaultTypeTransformation.compareEqual(sourceValue, newValue)) {
                    return;
                }
                PropertyBinding.this.setBeanProperty(newValue);
            }
        };
        switch (this.updateStrategy) {
            case MIXED: {
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                    break;
                }
                SwingUtilities.invokeLater(runnable);
                break;
            }
            case ASYNC: {
                SwingUtilities.invokeLater(runnable);
                break;
            }
            case SYNC: {
                if (SwingUtilities.isEventDispatchThread()) {
                    runnable.run();
                    break;
                }
                try {
                    SwingUtilities.invokeAndWait(runnable);
                    break;
                }
                catch (InterruptedException e) {
                    LOG.log(Level.WARNING, "Error notifying propertyChangeListener", e);
                    throw new GroovyRuntimeException(e);
                }
                catch (InvocationTargetException e) {
                    LOG.log(Level.WARNING, "Error notifying propertyChangeListener", e.getTargetException());
                    throw new GroovyRuntimeException(e.getTargetException());
                }
            }
            case SAME: {
                runnable.run();
                break;
            }
            case OUTSIDE: {
                if (SwingUtilities.isEventDispatchThread()) {
                    DEFAULT_EXECUTOR_SERVICE.submit(runnable);
                    break;
                }
                runnable.run();
                break;
            }
            case DEFER: {
                DEFAULT_EXECUTOR_SERVICE.submit(runnable);
            }
        }
    }

    private void setBeanProperty(Object newValue) {
        block2: {
            try {
                this.propertyAccessor().write(this.bean, this.propertyName, newValue);
            }
            catch (InvokerInvocationException iie) {
                if (iie.getCause() instanceof PropertyVetoException) break block2;
                throw iie;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean isNonChangeCheck() {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return this.nonChangeCheck;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setNonChangeCheck(boolean nonChangeCheck) {
        Object[] objectArray = this.lock;
        synchronized (this.lock) {
            this.nonChangeCheck = nonChangeCheck;
            // ** MonitorExit[var2_2] (shouldn't be in output)
            return;
        }
    }

    @Override
    public Object getSourceValue() {
        return this.propertyAccessor().read(this.bean, this.propertyName);
    }

    @Override
    public FullBinding createBinding(SourceBinding source, TargetBinding target) {
        return new PropertyFullBinding(source, target);
    }

    public Object getBean() {
        return this.bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
        this.setupPropertyReaderAndWriter();
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    static {
        Enumeration<URL> urls = PropertyBinding.fetchUrlsFor("META-INF/services/" + PropertyAccessor.class.getName());
        while (urls.hasMoreElements()) {
            try {
                PropertyBinding.registerPropertyAccessors(ResourceGroovyMethods.readLines(urls.nextElement()));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static enum UpdateStrategy {
        MIXED,
        ASYNC,
        SYNC,
        SAME,
        OUTSIDE,
        DEFER;


        public static UpdateStrategy of(String str) {
            if ("mixed".equalsIgnoreCase(str)) {
                return MIXED;
            }
            if ("async".equalsIgnoreCase(str)) {
                return ASYNC;
            }
            if ("sync".equalsIgnoreCase(str)) {
                return SYNC;
            }
            if ("same".equalsIgnoreCase(str)) {
                return SAME;
            }
            if ("outside".equalsIgnoreCase(str)) {
                return OUTSIDE;
            }
            if ("defer".equalsIgnoreCase(str)) {
                return DEFER;
            }
            return null;
        }
    }

    class PropertyFullBinding
    extends AbstractFullBinding
    implements PropertyChangeListener {
        Object boundBean;
        Object boundProperty;
        boolean bound;
        boolean boundToProperty;

        PropertyFullBinding(SourceBinding source, TargetBinding target) {
            this.setSourceBinding(source);
            this.setTargetBinding(target);
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (this.boundToProperty || event.getPropertyName().equals(this.boundProperty)) {
                this.update();
            }
        }

        @Override
        public void bind() {
            if (!this.bound) {
                this.bound = true;
                this.boundBean = PropertyBinding.this.bean;
                this.boundProperty = PropertyBinding.this.propertyName;
                try {
                    InvokerHelper.invokeMethodSafe(this.boundBean, "addPropertyChangeListener", new Object[]{this.boundProperty, this});
                    this.boundToProperty = true;
                }
                catch (MissingMethodException mme) {
                    try {
                        this.boundToProperty = false;
                        InvokerHelper.invokeMethodSafe(this.boundBean, "addPropertyChangeListener", new Object[]{this});
                    }
                    catch (MissingMethodException mme2) {
                        throw new RuntimeException("Properties in beans of type " + PropertyBinding.this.bean.getClass().getName() + " are not observable in any capacity (no PropertyChangeListener support).");
                    }
                }
            }
        }

        @Override
        public void unbind() {
            if (this.bound) {
                if (this.boundToProperty) {
                    try {
                        InvokerHelper.invokeMethodSafe(this.boundBean, "removePropertyChangeListener", new Object[]{this.boundProperty, this});
                    }
                    catch (MissingMethodException missingMethodException) {}
                } else {
                    try {
                        InvokerHelper.invokeMethodSafe(this.boundBean, "removePropertyChangeListener", new Object[]{this});
                    }
                    catch (MissingMethodException missingMethodException) {
                        // empty catch block
                    }
                }
                this.boundBean = null;
                this.boundProperty = null;
                this.bound = false;
            }
        }

        @Override
        public void rebind() {
            if (this.bound) {
                this.unbind();
                this.bind();
            }
        }
    }
}

