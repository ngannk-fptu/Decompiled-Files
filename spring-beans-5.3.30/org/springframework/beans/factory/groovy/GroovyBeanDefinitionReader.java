/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.lang.Binding
 *  groovy.lang.Closure
 *  groovy.lang.GString
 *  groovy.lang.GroovyObject
 *  groovy.lang.GroovyObjectSupport
 *  groovy.lang.GroovyShell
 *  groovy.lang.GroovySystem
 *  groovy.lang.MetaClass
 *  org.codehaus.groovy.runtime.DefaultGroovyMethods
 *  org.codehaus.groovy.runtime.InvokerHelper
 *  org.springframework.core.io.DescriptiveResource
 *  org.springframework.core.io.Resource
 *  org.springframework.core.io.support.EncodedResource
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.beans.factory.groovy;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.GroovyShell;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.groovy.GroovyBeanDefinitionWrapper;
import org.springframework.beans.factory.groovy.GroovyDynamicElementReader;
import org.springframework.beans.factory.parsing.BeanDefinitionParsingException;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.support.ManagedMap;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.NamespaceHandler;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class GroovyBeanDefinitionReader
extends AbstractBeanDefinitionReader
implements GroovyObject {
    private final XmlBeanDefinitionReader standardXmlBeanDefinitionReader;
    private final XmlBeanDefinitionReader groovyDslXmlBeanDefinitionReader;
    private final Map<String, String> namespaces = new HashMap<String, String>();
    private final Map<String, DeferredProperty> deferredProperties = new HashMap<String, DeferredProperty>();
    private MetaClass metaClass = GroovySystem.getMetaClassRegistry().getMetaClass(this.getClass());
    private Binding binding;
    private GroovyBeanDefinitionWrapper currentBeanDefinition;

    public GroovyBeanDefinitionReader(BeanDefinitionRegistry registry) {
        super(registry);
        this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
        this.groovyDslXmlBeanDefinitionReader = new XmlBeanDefinitionReader(registry);
        this.groovyDslXmlBeanDefinitionReader.setValidating(false);
    }

    public GroovyBeanDefinitionReader(XmlBeanDefinitionReader xmlBeanDefinitionReader) {
        super(xmlBeanDefinitionReader.getRegistry());
        this.standardXmlBeanDefinitionReader = new XmlBeanDefinitionReader(xmlBeanDefinitionReader.getRegistry());
        this.groovyDslXmlBeanDefinitionReader = xmlBeanDefinitionReader;
    }

    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    public void setBinding(Binding binding) {
        this.binding = binding;
    }

    public Binding getBinding() {
        return this.binding;
    }

    @Override
    public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
        return this.loadBeanDefinitions(new EncodedResource(resource));
    }

    public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
        String filename = encodedResource.getResource().getFilename();
        if (StringUtils.endsWithIgnoreCase((String)filename, (String)".xml")) {
            return this.standardXmlBeanDefinitionReader.loadBeanDefinitions(encodedResource);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Loading Groovy bean definitions from " + encodedResource));
        }
        Closure<Object> beans = new Closure<Object>((Object)this){

            public Object call(Object ... args) {
                GroovyBeanDefinitionReader.this.invokeBeanDefiningClosure((Closure)args[0]);
                return null;
            }
        };
        Binding binding = new Binding(){

            public void setVariable(String name, Object value) {
                if (GroovyBeanDefinitionReader.this.currentBeanDefinition != null) {
                    GroovyBeanDefinitionReader.this.applyPropertyToBeanDefinition(name, value);
                } else {
                    super.setVariable(name, value);
                }
            }
        };
        binding.setVariable("beans", (Object)beans);
        int countBefore = this.getRegistry().getBeanDefinitionCount();
        try {
            GroovyShell shell = new GroovyShell(this.getBeanClassLoader(), binding);
            shell.evaluate(encodedResource.getReader(), "beans");
        }
        catch (Throwable ex) {
            throw new BeanDefinitionParsingException(new Problem("Error evaluating Groovy script: " + ex.getMessage(), new Location(encodedResource.getResource()), null, ex));
        }
        int count = this.getRegistry().getBeanDefinitionCount() - countBefore;
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Loaded " + count + " bean definitions from " + encodedResource));
        }
        return count;
    }

    public GroovyBeanDefinitionReader beans(Closure<?> closure) {
        return this.invokeBeanDefiningClosure(closure);
    }

    public GenericBeanDefinition bean(Class<?> type) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(type);
        return beanDefinition;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AbstractBeanDefinition bean(Class<?> type, Object ... args) {
        GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
        try {
            Closure callable = null;
            List<Object> constructorArgs = null;
            if (!ObjectUtils.isEmpty((Object[])args)) {
                int index = args.length;
                Object lastArg = args[index - 1];
                if (lastArg instanceof Closure) {
                    callable = (Closure)lastArg;
                    --index;
                }
                constructorArgs = this.resolveConstructorArguments(args, 0, index);
            }
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, type, constructorArgs);
            if (callable != null) {
                callable.call((Object)this.currentBeanDefinition);
            }
            AbstractBeanDefinition abstractBeanDefinition = this.currentBeanDefinition.getBeanDefinition();
            return abstractBeanDefinition;
        }
        finally {
            this.currentBeanDefinition = current;
        }
    }

    public void xmlns(Map<String, String> definition) {
        if (!definition.isEmpty()) {
            for (Map.Entry<String, String> entry : definition.entrySet()) {
                String namespace = entry.getKey();
                String uri = entry.getValue();
                if (uri == null) {
                    throw new IllegalArgumentException("Namespace definition must supply a non-null URI");
                }
                NamespaceHandler namespaceHandler = this.groovyDslXmlBeanDefinitionReader.getNamespaceHandlerResolver().resolve(uri);
                if (namespaceHandler == null) {
                    throw new BeanDefinitionParsingException(new Problem("No namespace handler found for URI: " + uri, new Location((Resource)new DescriptiveResource("Groovy"))));
                }
                this.namespaces.put(namespace, uri);
            }
        }
    }

    public void importBeans(String resourcePattern) throws IOException {
        this.loadBeanDefinitions(resourcePattern);
    }

    public Object invokeMethod(String name, Object arg) {
        Object[] args = (Object[])arg;
        if ("beans".equals(name) && args.length == 1 && args[0] instanceof Closure) {
            return this.beans((Closure)args[0]);
        }
        if ("ref".equals(name)) {
            if (args[0] == null) {
                throw new IllegalArgumentException("Argument to ref() is not a valid bean or was not found");
            }
            String refName = args[0] instanceof RuntimeBeanReference ? ((RuntimeBeanReference)args[0]).getBeanName() : args[0].toString();
            boolean parentRef = false;
            if (args.length > 1 && args[1] instanceof Boolean) {
                parentRef = (Boolean)args[1];
            }
            return new RuntimeBeanReference(refName, parentRef);
        }
        if (this.namespaces.containsKey(name) && args.length > 0 && args[0] instanceof Closure) {
            GroovyDynamicElementReader reader = this.createDynamicElementReader(name);
            reader.invokeMethod("doCall", args);
        } else {
            if (args.length > 0 && args[0] instanceof Closure) {
                return this.invokeBeanDefiningMethod(name, args);
            }
            if (args.length > 0 && (args[0] instanceof Class || args[0] instanceof RuntimeBeanReference || args[0] instanceof Map)) {
                return this.invokeBeanDefiningMethod(name, args);
            }
            if (args.length > 1 && args[args.length - 1] instanceof Closure) {
                return this.invokeBeanDefiningMethod(name, args);
            }
        }
        MetaClass mc = DefaultGroovyMethods.getMetaClass((Object)this.getRegistry());
        if (!mc.respondsTo((Object)this.getRegistry(), name, args).isEmpty()) {
            return mc.invokeMethod((Object)this.getRegistry(), name, args);
        }
        return this;
    }

    private boolean addDeferredProperty(String property, Object newValue) {
        if (newValue instanceof List || newValue instanceof Map) {
            this.deferredProperties.put(this.currentBeanDefinition.getBeanName() + '.' + property, new DeferredProperty(this.currentBeanDefinition, property, newValue));
            return true;
        }
        return false;
    }

    private void finalizeDeferredProperties() {
        for (DeferredProperty dp : this.deferredProperties.values()) {
            if (dp.value instanceof List) {
                dp.value = this.manageListIfNecessary((List)dp.value);
            } else if (dp.value instanceof Map) {
                dp.value = this.manageMapIfNecessary((Map)dp.value);
            }
            dp.apply();
        }
        this.deferredProperties.clear();
    }

    protected GroovyBeanDefinitionReader invokeBeanDefiningClosure(Closure<?> callable) {
        callable.setDelegate((Object)this);
        callable.call();
        this.finalizeDeferredProperties();
        return this;
    }

    private GroovyBeanDefinitionWrapper invokeBeanDefiningMethod(String beanName, Object[] args) {
        List<Object> constructorArgs;
        boolean hasClosureArgument = args[args.length - 1] instanceof Closure;
        if (args[0] instanceof Class) {
            Class beanClass = (Class)args[0];
            this.currentBeanDefinition = hasClosureArgument ? (args.length - 1 != 1 ? new GroovyBeanDefinitionWrapper(beanName, beanClass, this.resolveConstructorArguments(args, 1, args.length - 1)) : new GroovyBeanDefinitionWrapper(beanName, beanClass)) : new GroovyBeanDefinitionWrapper(beanName, beanClass, this.resolveConstructorArguments(args, 1, args.length));
        } else if (args[0] instanceof RuntimeBeanReference) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(((RuntimeBeanReference)args[0]).getBeanName());
        } else if (args[0] instanceof Map) {
            if (args.length > 1 && args[1] instanceof Class) {
                constructorArgs = this.resolveConstructorArguments(args, 2, hasClosureArgument ? args.length - 1 : args.length);
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, (Class)args[1], constructorArgs);
                Map namedArgs = (Map)args[0];
                for (Map.Entry entity : namedArgs.entrySet()) {
                    String propName = (String)entity.getKey();
                    this.setProperty(propName, entity.getValue());
                }
            } else {
                int constructorArgsTest;
                this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                Map.Entry factoryBeanEntry = ((Map)args[0]).entrySet().iterator().next();
                int n = constructorArgsTest = hasClosureArgument ? 2 : 1;
                if (args.length > constructorArgsTest) {
                    int endOfConstructArgs = hasClosureArgument ? args.length - 1 : args.length;
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, this.resolveConstructorArguments(args, 1, endOfConstructArgs));
                } else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
                }
                this.currentBeanDefinition.getBeanDefinition().setFactoryBeanName(factoryBeanEntry.getKey().toString());
                this.currentBeanDefinition.getBeanDefinition().setFactoryMethodName(factoryBeanEntry.getValue().toString());
            }
        } else if (args[0] instanceof Closure) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName);
            this.currentBeanDefinition.getBeanDefinition().setAbstract(true);
        } else {
            constructorArgs = this.resolveConstructorArguments(args, 0, hasClosureArgument ? args.length - 1 : args.length);
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(beanName, null, constructorArgs);
        }
        if (hasClosureArgument) {
            Closure callable = (Closure)args[args.length - 1];
            callable.setDelegate((Object)this);
            callable.setResolveStrategy(1);
            callable.call((Object)this.currentBeanDefinition);
        }
        GroovyBeanDefinitionWrapper beanDefinition = this.currentBeanDefinition;
        this.currentBeanDefinition = null;
        beanDefinition.getBeanDefinition().setAttribute(GroovyBeanDefinitionWrapper.class.getName(), (Object)beanDefinition);
        this.getRegistry().registerBeanDefinition(beanName, beanDefinition.getBeanDefinition());
        return beanDefinition;
    }

    protected List<Object> resolveConstructorArguments(Object[] args, int start, int end) {
        Object[] constructorArgs = Arrays.copyOfRange(args, start, end);
        for (int i = 0; i < constructorArgs.length; ++i) {
            if (constructorArgs[i] instanceof GString) {
                constructorArgs[i] = constructorArgs[i].toString();
                continue;
            }
            if (constructorArgs[i] instanceof List) {
                constructorArgs[i] = this.manageListIfNecessary((List)constructorArgs[i]);
                continue;
            }
            if (!(constructorArgs[i] instanceof Map)) continue;
            constructorArgs[i] = this.manageMapIfNecessary((Map)constructorArgs[i]);
        }
        return Arrays.asList(constructorArgs);
    }

    private Object manageMapIfNecessary(Map<?, ?> map) {
        boolean containsRuntimeRefs = false;
        for (Object element : map.values()) {
            if (!(element instanceof RuntimeBeanReference)) continue;
            containsRuntimeRefs = true;
            break;
        }
        if (containsRuntimeRefs) {
            ManagedMap managedMap = new ManagedMap();
            managedMap.putAll(map);
            return managedMap;
        }
        return map;
    }

    private Object manageListIfNecessary(List<?> list) {
        boolean containsRuntimeRefs = false;
        for (Object element : list) {
            if (!(element instanceof RuntimeBeanReference)) continue;
            containsRuntimeRefs = true;
            break;
        }
        if (containsRuntimeRefs) {
            ManagedList managedList = new ManagedList();
            managedList.addAll(list);
            return managedList;
        }
        return list;
    }

    public void setProperty(String name, Object value) {
        if (this.currentBeanDefinition != null) {
            this.applyPropertyToBeanDefinition(name, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void applyPropertyToBeanDefinition(String name, Object value) {
        if (value instanceof GString) {
            value = value.toString();
        }
        if (this.addDeferredProperty(name, value)) {
            return;
        }
        if (value instanceof Closure) {
            GroovyBeanDefinitionWrapper current = this.currentBeanDefinition;
            try {
                Closure callable = (Closure)value;
                Class parameterType = callable.getParameterTypes()[0];
                if (Object.class == parameterType) {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper("");
                    callable.call((Object)this.currentBeanDefinition);
                } else {
                    this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(null, parameterType);
                    callable.call(null);
                }
                value = this.currentBeanDefinition.getBeanDefinition();
            }
            finally {
                this.currentBeanDefinition = current;
            }
        }
        this.currentBeanDefinition.addProperty(name, value);
    }

    public Object getProperty(String name) {
        Binding binding = this.getBinding();
        if (binding != null && binding.hasVariable(name)) {
            return binding.getVariable(name);
        }
        if (this.namespaces.containsKey(name)) {
            return this.createDynamicElementReader(name);
        }
        if (this.getRegistry().containsBeanDefinition(name)) {
            GroovyBeanDefinitionWrapper beanDefinition = (GroovyBeanDefinitionWrapper)((Object)this.getRegistry().getBeanDefinition(name).getAttribute(GroovyBeanDefinitionWrapper.class.getName()));
            if (beanDefinition != null) {
                return new GroovyRuntimeBeanReference(name, beanDefinition, false);
            }
            return new RuntimeBeanReference(name, false);
        }
        if (this.currentBeanDefinition != null) {
            MutablePropertyValues pvs = this.currentBeanDefinition.getBeanDefinition().getPropertyValues();
            if (pvs.contains(name)) {
                return pvs.get(name);
            }
            DeferredProperty dp = this.deferredProperties.get(this.currentBeanDefinition.getBeanName() + name);
            if (dp != null) {
                return dp.value;
            }
            return this.getMetaClass().getProperty((Object)this, name);
        }
        return this.getMetaClass().getProperty((Object)this, name);
    }

    private GroovyDynamicElementReader createDynamicElementReader(String namespace) {
        boolean decorating;
        XmlReaderContext readerContext = this.groovyDslXmlBeanDefinitionReader.createReaderContext((Resource)new DescriptiveResource("Groovy"));
        BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext);
        boolean bl = decorating = this.currentBeanDefinition != null;
        if (!decorating) {
            this.currentBeanDefinition = new GroovyBeanDefinitionWrapper(namespace);
        }
        return new GroovyDynamicElementReader(namespace, this.namespaces, delegate, this.currentBeanDefinition, decorating){

            @Override
            protected void afterInvocation() {
                if (!this.decorating) {
                    GroovyBeanDefinitionReader.this.currentBeanDefinition = null;
                }
            }
        };
    }

    private class GroovyRuntimeBeanReference
    extends RuntimeBeanReference
    implements GroovyObject {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private MetaClass metaClass;

        public GroovyRuntimeBeanReference(String beanName, GroovyBeanDefinitionWrapper beanDefinition, boolean toParent) {
            super(beanName, toParent);
            this.beanDefinition = beanDefinition;
            this.metaClass = InvokerHelper.getMetaClass((Object)this);
        }

        public MetaClass getMetaClass() {
            return this.metaClass;
        }

        public Object getProperty(String property) {
            if (property.equals("beanName")) {
                return this.getBeanName();
            }
            if (property.equals("source")) {
                return this.getSource();
            }
            if (this.beanDefinition != null) {
                return new GroovyPropertyValue(property, this.beanDefinition.getBeanDefinition().getPropertyValues().get(property));
            }
            return this.metaClass.getProperty((Object)this, property);
        }

        public Object invokeMethod(String name, Object args) {
            return this.metaClass.invokeMethod((Object)this, name, args);
        }

        public void setMetaClass(MetaClass metaClass) {
            this.metaClass = metaClass;
        }

        public void setProperty(String property, Object newValue) {
            if (!GroovyBeanDefinitionReader.this.addDeferredProperty(property, newValue)) {
                this.beanDefinition.getBeanDefinition().getPropertyValues().add(property, newValue);
            }
        }

        private class GroovyPropertyValue
        extends GroovyObjectSupport {
            private final String propertyName;
            private final Object propertyValue;

            public GroovyPropertyValue(String propertyName, Object propertyValue) {
                this.propertyName = propertyName;
                this.propertyValue = propertyValue;
            }

            public void leftShift(Object value) {
                InvokerHelper.invokeMethod((Object)this.propertyValue, (String)"leftShift", (Object)value);
                this.updateDeferredProperties(value);
            }

            public boolean add(Object value) {
                boolean retVal = (Boolean)InvokerHelper.invokeMethod((Object)this.propertyValue, (String)"add", (Object)value);
                this.updateDeferredProperties(value);
                return retVal;
            }

            public boolean addAll(Collection<?> values) {
                boolean retVal = (Boolean)InvokerHelper.invokeMethod((Object)this.propertyValue, (String)"addAll", values);
                for (Object value : values) {
                    this.updateDeferredProperties(value);
                }
                return retVal;
            }

            public Object invokeMethod(String name, Object args) {
                return InvokerHelper.invokeMethod((Object)this.propertyValue, (String)name, (Object)args);
            }

            public Object getProperty(String name) {
                return InvokerHelper.getProperty((Object)this.propertyValue, (String)name);
            }

            public void setProperty(String name, Object value) {
                InvokerHelper.setProperty((Object)this.propertyValue, (String)name, (Object)value);
            }

            private void updateDeferredProperties(Object value) {
                if (value instanceof RuntimeBeanReference) {
                    GroovyBeanDefinitionReader.this.deferredProperties.put(GroovyRuntimeBeanReference.this.beanDefinition.getBeanName(), new DeferredProperty(GroovyRuntimeBeanReference.this.beanDefinition, this.propertyName, this.propertyValue));
                }
            }
        }
    }

    private static class DeferredProperty {
        private final GroovyBeanDefinitionWrapper beanDefinition;
        private final String name;
        public Object value;

        public DeferredProperty(GroovyBeanDefinitionWrapper beanDefinition, String name, Object value) {
            this.beanDefinition = beanDefinition;
            this.name = name;
            this.value = value;
        }

        public void apply() {
            this.beanDefinition.addProperty(this.name, this.value);
        }
    }
}

