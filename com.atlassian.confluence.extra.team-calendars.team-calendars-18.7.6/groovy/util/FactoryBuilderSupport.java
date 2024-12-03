/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.MetaClass;
import groovy.lang.MissingMethodException;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.lang.Script;
import groovy.util.AbstractFactory;
import groovy.util.Factory;
import groovy.util.FactoryInterceptorMetaClass;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MetaClassHelper;
import org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack;

public abstract class FactoryBuilderSupport
extends Binding {
    public static final String CURRENT_FACTORY = "_CURRENT_FACTORY_";
    public static final String PARENT_FACTORY = "_PARENT_FACTORY_";
    public static final String PARENT_NODE = "_PARENT_NODE_";
    public static final String CURRENT_NODE = "_CURRENT_NODE_";
    public static final String PARENT_CONTEXT = "_PARENT_CONTEXT_";
    public static final String PARENT_NAME = "_PARENT_NAME_";
    public static final String CURRENT_NAME = "_CURRENT_NAME_";
    public static final String OWNER = "owner";
    public static final String PARENT_BUILDER = "_PARENT_BUILDER_";
    public static final String CURRENT_BUILDER = "_CURRENT_BUILDER_";
    public static final String CHILD_BUILDER = "_CHILD_BUILDER_";
    public static final String SCRIPT_CLASS_NAME = "_SCRIPT_CLASS_NAME_";
    private static final Logger LOG = Logger.getLogger(FactoryBuilderSupport.class.getName());
    private static final Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>(){

        @Override
        public int compare(Method o1, Method o2) {
            int cmp = o1.getName().compareTo(o2.getName());
            if (cmp != 0) {
                return cmp;
            }
            cmp = o1.getParameterTypes().length - o2.getParameterTypes().length;
            return cmp;
        }
    };
    private ThreadLocal<LinkedList<Map<String, Object>>> contexts = new ThreadLocal();
    protected LinkedList<Closure> attributeDelegates = new LinkedList();
    private List<Closure> disposalClosures = new ArrayList<Closure>();
    private Map<String, Factory> factories = new HashMap<String, Factory>();
    private Closure nameMappingClosure;
    private ThreadLocal<FactoryBuilderSupport> localProxyBuilder = new ThreadLocal();
    private FactoryBuilderSupport globalProxyBuilder;
    protected LinkedList<Closure> preInstantiateDelegates = new LinkedList();
    protected LinkedList<Closure> postInstantiateDelegates = new LinkedList();
    protected LinkedList<Closure> postNodeCompletionDelegates = new LinkedList();
    protected Closure methodMissingDelegate;
    protected Closure propertyMissingDelegate;
    protected Map<String, Closure[]> explicitProperties = new HashMap<String, Closure[]>();
    protected Map<String, Closure> explicitMethods = new HashMap<String, Closure>();
    protected Map<String, Set<String>> registrationGroup = new HashMap<String, Set<String>>();
    protected String registrationGroupName = "";
    protected boolean autoRegistrationRunning = false;
    protected boolean autoRegistrationComplete = false;

    public static void checkValueIsNull(Object value, Object name) {
        if (value != null) {
            throw new RuntimeException("'" + name + "' elements do not accept a value argument.");
        }
    }

    public static boolean checkValueIsType(Object value, Object name, Class type) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return true;
            }
            throw new RuntimeException("The value argument of '" + name + "' must be of type " + type.getName() + ". Found: " + value.getClass());
        }
        return false;
    }

    public static boolean checkValueIsTypeNotString(Object value, Object name, Class type) {
        if (value != null) {
            if (type.isAssignableFrom(value.getClass())) {
                return true;
            }
            if (value instanceof String) {
                return false;
            }
            throw new RuntimeException("The value argument of '" + name + "' must be of type " + type.getName() + " or a String. Found: " + value.getClass());
        }
        return false;
    }

    public FactoryBuilderSupport() {
        this(false);
    }

    public FactoryBuilderSupport(boolean init) {
        this.globalProxyBuilder = this;
        this.registrationGroup.put(this.registrationGroupName, new TreeSet());
        if (init) {
            this.autoRegisterNodes();
        }
    }

    private Set<String> getRegistrationGroup(String name) {
        Set<String> group = this.registrationGroup.get(name);
        if (group == null) {
            group = new TreeSet<String>();
            this.registrationGroup.put(name, group);
        }
        return group;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void autoRegisterNodes() {
        FactoryBuilderSupport factoryBuilderSupport = this;
        synchronized (factoryBuilderSupport) {
            if (this.autoRegistrationRunning || this.autoRegistrationComplete) {
                return;
            }
        }
        this.autoRegistrationRunning = true;
        try {
            this.callAutoRegisterMethods(this.getClass());
        }
        finally {
            this.autoRegistrationComplete = true;
            this.autoRegistrationRunning = false;
        }
    }

    private void callAutoRegisterMethods(Class declaredClass) {
        if (declaredClass == null) {
            return;
        }
        this.callAutoRegisterMethods(declaredClass.getSuperclass());
        Method[] declaredMethods = declaredClass.getDeclaredMethods();
        Arrays.sort(declaredMethods, METHOD_COMPARATOR);
        for (Method method : declaredMethods) {
            if (!method.getName().startsWith("register") || method.getParameterTypes().length != 0) continue;
            this.registrationGroupName = method.getName().substring("register".length());
            this.registrationGroup.put(this.registrationGroupName, new TreeSet());
            try {
                if (!Modifier.isPublic(method.getModifiers())) continue;
                method.invoke((Object)this, new Object[0]);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Could not init " + this.getClass().getName() + " because of an access error in " + declaredClass.getName() + "." + method.getName(), e);
            }
            catch (InvocationTargetException e) {
                throw new RuntimeException("Could not init " + this.getClass().getName() + " because of an exception in " + declaredClass.getName() + "." + method.getName(), e);
            }
            finally {
                this.registrationGroupName = "";
            }
        }
    }

    @Override
    public Object getVariable(String name) {
        try {
            return this.getProxyBuilder().doGetVariable(name);
        }
        catch (MissingPropertyException mpe) {
            if (mpe.getProperty().equals(name) && this.propertyMissingDelegate != null) {
                return this.propertyMissingDelegate.call(new Object[]{name});
            }
            throw mpe;
        }
    }

    private Object doGetVariable(String name) {
        return super.getVariable(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        this.getProxyBuilder().doSetVariable(name, value);
    }

    private void doSetVariable(String name, Object value) {
        super.setVariable(name, value);
    }

    @Override
    public Map getVariables() {
        return this.getProxyBuilder().doGetVariables();
    }

    private Map doGetVariables() {
        return super.getVariables();
    }

    @Override
    public Object getProperty(String property) {
        try {
            return this.getProxyBuilder().doGetProperty(property);
        }
        catch (MissingPropertyException mpe) {
            if (this.getContext() != null && this.getContext().containsKey(property)) {
                return this.getContext().get(property);
            }
            try {
                return this.getMetaClass().getProperty(this, property);
            }
            catch (MissingPropertyException mpe2) {
                if (mpe2.getProperty().equals(property) && this.propertyMissingDelegate != null) {
                    return this.propertyMissingDelegate.call(new Object[]{property});
                }
                throw mpe2;
            }
        }
    }

    private Object doGetProperty(String property) {
        Closure[] accessors = this.resolveExplicitProperty(property);
        if (accessors != null) {
            if (accessors[0] == null) {
                throw new MissingPropertyException(property + " is declared as write only");
            }
            return accessors[0].call();
        }
        return super.getProperty(property);
    }

    @Override
    public void setProperty(String property, Object newValue) {
        this.getProxyBuilder().doSetProperty(property, newValue);
    }

    private void doSetProperty(String property, Object newValue) {
        Closure[] accessors = this.resolveExplicitProperty(property);
        if (accessors != null) {
            if (accessors[1] == null) {
                throw new MissingPropertyException(property + " is declared as read only");
            }
            accessors[1].call(newValue);
        } else {
            super.setProperty(property, newValue);
        }
    }

    public Map<String, Factory> getFactories() {
        return Collections.unmodifiableMap(this.getProxyBuilder().factories);
    }

    public Map<String, Closure> getExplicitMethods() {
        return Collections.unmodifiableMap(this.getProxyBuilder().explicitMethods);
    }

    public Map<String, Closure[]> getExplicitProperties() {
        return Collections.unmodifiableMap(this.getProxyBuilder().explicitProperties);
    }

    public Map<String, Factory> getLocalFactories() {
        return Collections.unmodifiableMap(this.factories);
    }

    public Map<String, Closure> getLocalExplicitMethods() {
        return Collections.unmodifiableMap(this.explicitMethods);
    }

    public Map<String, Closure[]> getLocalExplicitProperties() {
        return Collections.unmodifiableMap(this.explicitProperties);
    }

    public Set<String> getRegistrationGroups() {
        return Collections.unmodifiableSet(this.registrationGroup.keySet());
    }

    public Set<String> getRegistrationGroupItems(String group) {
        Set<String> groupSet = this.registrationGroup.get(group);
        if (groupSet != null) {
            return Collections.unmodifiableSet(groupSet);
        }
        return Collections.emptySet();
    }

    public List<Closure> getAttributeDelegates() {
        return Collections.unmodifiableList(this.attributeDelegates);
    }

    public List<Closure> getPreInstantiateDelegates() {
        return Collections.unmodifiableList(this.preInstantiateDelegates);
    }

    public List<Closure> getPostInstantiateDelegates() {
        return Collections.unmodifiableList(this.postInstantiateDelegates);
    }

    public List<Closure> getPostNodeCompletionDelegates() {
        return Collections.unmodifiableList(this.postNodeCompletionDelegates);
    }

    public Closure getMethodMissingDelegate() {
        return this.methodMissingDelegate;
    }

    public void setMethodMissingDelegate(Closure delegate) {
        this.methodMissingDelegate = delegate;
    }

    public Closure getPropertyMissingDelegate() {
        return this.propertyMissingDelegate;
    }

    public void setPropertyMissingDelegate(Closure delegate) {
        this.propertyMissingDelegate = delegate;
    }

    public Map<String, Object> getContext() {
        LinkedList<Map<String, Object>> contexts = this.getProxyBuilder().contexts.get();
        if (contexts != null && !contexts.isEmpty()) {
            return contexts.getFirst();
        }
        return null;
    }

    public Object getCurrent() {
        return this.getContextAttribute(CURRENT_NODE);
    }

    public Factory getCurrentFactory() {
        return (Factory)this.getContextAttribute(CURRENT_FACTORY);
    }

    public String getCurrentName() {
        return (String)this.getContextAttribute(CURRENT_NAME);
    }

    public FactoryBuilderSupport getCurrentBuilder() {
        return (FactoryBuilderSupport)this.getContextAttribute(CURRENT_BUILDER);
    }

    public Object getParentNode() {
        return this.getContextAttribute(PARENT_NODE);
    }

    public Factory getParentFactory() {
        return (Factory)this.getContextAttribute(PARENT_FACTORY);
    }

    public Map getParentContext() {
        return (Map)this.getContextAttribute(PARENT_CONTEXT);
    }

    public String getParentName() {
        return (String)this.getContextAttribute(PARENT_NAME);
    }

    public FactoryBuilderSupport getChildBuilder() {
        return (FactoryBuilderSupport)this.getContextAttribute(CHILD_BUILDER);
    }

    public Object getContextAttribute(String key) {
        Map<String, Object> context = this.getContext();
        if (context != null) {
            return context.get(key);
        }
        return null;
    }

    public Object invokeMethod(String methodName) {
        return this.getProxyBuilder().invokeMethod(methodName, null);
    }

    @Override
    public Object invokeMethod(String methodName, Object args) {
        Object result;
        Object name = this.getProxyBuilder().getName(methodName);
        Map<String, Object> previousContext = this.getProxyBuilder().getContext();
        try {
            result = this.getProxyBuilder().doInvokeMethod(methodName, name, args);
        }
        catch (RuntimeException e) {
            if (this.getContexts().contains(previousContext)) {
                Map<String, Object> context = this.getProxyBuilder().getContext();
                while (context != null && context != previousContext) {
                    this.getProxyBuilder().popContext();
                    context = this.getProxyBuilder().getContext();
                }
            }
            throw e;
        }
        return result;
    }

    public Closure addAttributeDelegate(Closure attrDelegate) {
        this.getProxyBuilder().attributeDelegates.addFirst(attrDelegate);
        return attrDelegate;
    }

    public void removeAttributeDelegate(Closure attrDelegate) {
        this.getProxyBuilder().attributeDelegates.remove(attrDelegate);
    }

    public Closure addPreInstantiateDelegate(Closure delegate) {
        this.getProxyBuilder().preInstantiateDelegates.addFirst(delegate);
        return delegate;
    }

    public void removePreInstantiateDelegate(Closure delegate) {
        this.getProxyBuilder().preInstantiateDelegates.remove(delegate);
    }

    public Closure addPostInstantiateDelegate(Closure delegate) {
        this.getProxyBuilder().postInstantiateDelegates.addFirst(delegate);
        return delegate;
    }

    public void removePostInstantiateDelegate(Closure delegate) {
        this.getProxyBuilder().postInstantiateDelegates.remove(delegate);
    }

    public Closure addPostNodeCompletionDelegate(Closure delegate) {
        this.getProxyBuilder().postNodeCompletionDelegates.addFirst(delegate);
        return delegate;
    }

    public void removePostNodeCompletionDelegate(Closure delegate) {
        this.getProxyBuilder().postNodeCompletionDelegates.remove(delegate);
    }

    public void registerExplicitProperty(String name, Closure getter, Closure setter) {
        this.registerExplicitProperty(name, this.registrationGroupName, getter, setter);
    }

    public void registerExplicitProperty(String name, String groupName, Closure getter, Closure setter) {
        if (getter != null) {
            getter.setDelegate(this);
        }
        if (setter != null) {
            setter.setDelegate(this);
        }
        this.explicitProperties.put(name, new Closure[]{getter, setter});
        String methodNameBase = MetaClassHelper.capitalize(name);
        if (getter != null) {
            this.getRegistrationGroup(groupName).add("get" + methodNameBase);
        }
        if (setter != null) {
            this.getRegistrationGroup(groupName).add("set" + methodNameBase);
        }
    }

    public void registerExplicitMethod(String name, Closure closure) {
        this.registerExplicitMethod(name, this.registrationGroupName, closure);
    }

    public void registerExplicitMethod(String name, String groupName, Closure closure) {
        closure.setDelegate(this);
        this.explicitMethods.put(name, closure);
        this.getRegistrationGroup(groupName).add(name);
    }

    public void registerBeanFactory(String theName, Class beanClass) {
        this.registerBeanFactory(theName, this.registrationGroupName, beanClass);
    }

    public void registerBeanFactory(String theName, String groupName, final Class beanClass) {
        this.getProxyBuilder().registerFactory(theName, new AbstractFactory(){

            @Override
            public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map properties) throws InstantiationException, IllegalAccessException {
                if (FactoryBuilderSupport.checkValueIsTypeNotString(value, name, beanClass)) {
                    return value;
                }
                return beanClass.newInstance();
            }
        });
        this.getRegistrationGroup(groupName).add(theName);
    }

    public void registerFactory(String name, Factory factory) {
        this.registerFactory(name, this.registrationGroupName, factory);
    }

    public void registerFactory(String name, String groupName, Factory factory) {
        this.getProxyBuilder().factories.put(name, factory);
        this.getRegistrationGroup(groupName).add(name);
        factory.onFactoryRegistration(this, name, groupName);
    }

    protected Object createNode(Object name, Map attributes, Object value) {
        Object node;
        Factory factory = this.getProxyBuilder().resolveFactory(name, attributes, value);
        if (factory == null) {
            LOG.log(Level.WARNING, "Could not find match for name '" + name + "'");
            throw new MissingMethodExceptionNoStack((String)name, Object.class, new Object[]{attributes, value});
        }
        this.getProxyBuilder().getContext().put(CURRENT_FACTORY, factory);
        this.getProxyBuilder().getContext().put(CURRENT_NAME, String.valueOf(name));
        this.getProxyBuilder().preInstantiate(name, attributes, value);
        try {
            node = factory.newInstance(this.getProxyBuilder().getChildBuilder(), name, value, attributes);
            if (node == null) {
                LOG.log(Level.WARNING, "Factory for name '" + name + "' returned null");
                return null;
            }
            if (LOG.isLoggable(Level.FINE)) {
                LOG.fine("For name: " + name + " created node: " + node);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create component for '" + name + "' reason: " + e, e);
        }
        this.getProxyBuilder().postInstantiate(name, attributes, node);
        this.getProxyBuilder().handleNodeAttributes(node, attributes);
        return node;
    }

    protected Factory resolveFactory(Object name, Map attributes, Object value) {
        this.getProxyBuilder().getContext().put(CHILD_BUILDER, this.getProxyBuilder());
        return this.getProxyBuilder().getFactories().get(name);
    }

    protected Closure resolveExplicitMethod(String methodName, Object args) {
        return this.getExplicitMethods().get(methodName);
    }

    protected Closure[] resolveExplicitProperty(String propertyName) {
        return this.getExplicitProperties().get(propertyName);
    }

    private Object doInvokeMethod(String methodName, Object name, Object args) {
        Reference explicitResult = new Reference();
        if (this.checkExplicitMethod(methodName, args, explicitResult)) {
            return explicitResult.get();
        }
        try {
            return this.dispatchNodeCall(name, args);
        }
        catch (MissingMethodException mme) {
            if (mme.getMethod().equals(methodName) && this.methodMissingDelegate != null) {
                return this.methodMissingDelegate.call(methodName, args);
            }
            throw mme;
        }
    }

    protected boolean checkExplicitMethod(String methodName, Object args, Reference result) {
        Closure explicitMethod = this.resolveExplicitMethod(methodName, args);
        if (explicitMethod != null) {
            if (args instanceof Object[]) {
                result.set(explicitMethod.call((Object[])args));
            } else {
                result.set(explicitMethod.call(args));
            }
            return true;
        }
        return false;
    }

    @Deprecated
    protected Object dispathNodeCall(Object name, Object args) {
        return this.dispatchNodeCall(name, args);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object dispatchNodeCall(Object name, Object args) {
        Object node;
        boolean needToPopContext;
        Closure closure = null;
        List list = InvokerHelper.asList(args);
        if (this.getProxyBuilder().getContexts().isEmpty()) {
            this.getProxyBuilder().newContext();
            needToPopContext = true;
        } else {
            needToPopContext = false;
        }
        try {
            Map namedArgs = Collections.EMPTY_MAP;
            if (!list.isEmpty() && list.get(0) instanceof LinkedHashMap) {
                namedArgs = (Map)list.get(0);
                list = list.subList(1, list.size());
            }
            if (!list.isEmpty() && list.get(list.size() - 1) instanceof Closure) {
                closure = (Closure)list.get(list.size() - 1);
                list = list.subList(0, list.size() - 1);
            }
            Object arg = list.isEmpty() ? null : (list.size() == 1 ? list.get(0) : list);
            node = this.getProxyBuilder().createNode(name, namedArgs, arg);
            Object current = this.getProxyBuilder().getCurrent();
            if (current != null) {
                this.getProxyBuilder().setParent(current, node);
            }
            if (closure != null) {
                Factory parentFactory = this.getProxyBuilder().getCurrentFactory();
                if (parentFactory.isLeaf()) {
                    throw new RuntimeException("'" + name + "' doesn't support nesting.");
                }
                boolean processContent = true;
                if (parentFactory.isHandlesNodeChildren()) {
                    processContent = parentFactory.onNodeChildren(this, node, closure);
                }
                if (processContent) {
                    String parentName = this.getProxyBuilder().getCurrentName();
                    Map<String, Object> parentContext = this.getProxyBuilder().getContext();
                    this.getProxyBuilder().newContext();
                    try {
                        this.getProxyBuilder().getContext().put(OWNER, closure.getOwner());
                        this.getProxyBuilder().getContext().put(CURRENT_NODE, node);
                        this.getProxyBuilder().getContext().put(PARENT_FACTORY, parentFactory);
                        this.getProxyBuilder().getContext().put(PARENT_NODE, current);
                        this.getProxyBuilder().getContext().put(PARENT_CONTEXT, parentContext);
                        this.getProxyBuilder().getContext().put(PARENT_NAME, parentName);
                        this.getProxyBuilder().getContext().put(PARENT_BUILDER, parentContext.get(CURRENT_BUILDER));
                        this.getProxyBuilder().getContext().put(CURRENT_BUILDER, parentContext.get(CHILD_BUILDER));
                        this.getProxyBuilder().setClosureDelegate(closure, node);
                        closure.call();
                    }
                    finally {
                        this.getProxyBuilder().popContext();
                    }
                }
            }
            this.getProxyBuilder().nodeCompleted(current, node);
            node = this.getProxyBuilder().postNodeCompletion(current, node);
        }
        finally {
            if (needToPopContext) {
                this.getProxyBuilder().popContext();
            }
        }
        return node;
    }

    public Object getName(String methodName) {
        if (this.getProxyBuilder().nameMappingClosure != null) {
            return this.getProxyBuilder().nameMappingClosure.call((Object)methodName);
        }
        return methodName;
    }

    protected FactoryBuilderSupport getProxyBuilder() {
        FactoryBuilderSupport proxy = this.localProxyBuilder.get();
        if (proxy == null) {
            return this.globalProxyBuilder;
        }
        return proxy;
    }

    protected void setProxyBuilder(FactoryBuilderSupport proxyBuilder) {
        this.globalProxyBuilder = proxyBuilder;
    }

    public Closure getNameMappingClosure() {
        return this.nameMappingClosure;
    }

    public void setNameMappingClosure(Closure nameMappingClosure) {
        this.nameMappingClosure = nameMappingClosure;
    }

    protected void handleNodeAttributes(Object node, Map attributes) {
        if (node == null) {
            return;
        }
        for (Closure attrDelegate : this.getProxyBuilder().getAttributeDelegates()) {
            FactoryBuilderSupport builder = this;
            if (attrDelegate.getOwner() instanceof FactoryBuilderSupport) {
                builder = (FactoryBuilderSupport)attrDelegate.getOwner();
            } else if (attrDelegate.getDelegate() instanceof FactoryBuilderSupport) {
                builder = (FactoryBuilderSupport)attrDelegate.getDelegate();
            }
            attrDelegate.call(builder, node, attributes);
        }
        if (this.getProxyBuilder().getCurrentFactory().onHandleNodeAttributes(this.getProxyBuilder().getChildBuilder(), node, attributes)) {
            this.getProxyBuilder().setNodeAttributes(node, attributes);
        }
    }

    protected void newContext() {
        this.getContexts().addFirst(new HashMap());
    }

    protected void nodeCompleted(Object parent, Object node) {
        this.getProxyBuilder().getCurrentFactory().onNodeCompleted(this.getProxyBuilder().getChildBuilder(), parent, node);
    }

    protected Map<String, Object> popContext() {
        if (!this.getProxyBuilder().getContexts().isEmpty()) {
            return this.getProxyBuilder().getContexts().removeFirst();
        }
        return null;
    }

    protected void postInstantiate(Object name, Map attributes, Object node) {
        for (Closure postInstantiateDelegate : this.getProxyBuilder().getPostInstantiateDelegates()) {
            postInstantiateDelegate.call(this, attributes, node);
        }
    }

    protected Object postNodeCompletion(Object parent, Object node) {
        for (Closure postNodeCompletionDelegate : this.getProxyBuilder().getPostNodeCompletionDelegates()) {
            postNodeCompletionDelegate.call(this, parent, node);
        }
        return node;
    }

    protected void preInstantiate(Object name, Map attributes, Object value) {
        for (Closure preInstantiateDelegate : this.getProxyBuilder().getPreInstantiateDelegates()) {
            preInstantiateDelegate.call(this, attributes, value);
        }
    }

    protected void reset() {
        this.getProxyBuilder().getContexts().clear();
    }

    protected void setClosureDelegate(Closure closure, Object node) {
        closure.setDelegate(this);
    }

    protected void setNodeAttributes(Object node, Map attributes) {
        for (Map.Entry entry : attributes.entrySet()) {
            String property = entry.getKey().toString();
            Object value = entry.getValue();
            InvokerHelper.setProperty(node, property, value);
        }
    }

    protected void setParent(Object parent, Object child) {
        this.getProxyBuilder().getCurrentFactory().setParent(this.getProxyBuilder().getChildBuilder(), parent, child);
        Factory parentFactory = this.getProxyBuilder().getParentFactory();
        if (parentFactory != null) {
            parentFactory.setChild(this.getProxyBuilder().getCurrentBuilder(), parent, child);
        }
    }

    protected LinkedList<Map<String, Object>> getContexts() {
        LinkedList<Map<String, Object>> contexts = this.getProxyBuilder().contexts.get();
        if (contexts == null) {
            contexts = new LinkedList();
            this.getProxyBuilder().contexts.set(contexts);
        }
        return contexts;
    }

    protected Map<String, Object> getContinuationData() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("proxyBuilder", this.localProxyBuilder.get());
        data.put("contexts", this.contexts.get());
        return data;
    }

    protected void restoreFromContinuationData(Map<String, Object> data) {
        this.localProxyBuilder.set((FactoryBuilderSupport)data.get("proxyBuilder"));
        this.contexts.set((LinkedList)data.get("contexts"));
    }

    public Object build(Class viewClass) {
        if (Script.class.isAssignableFrom(viewClass)) {
            Script script = InvokerHelper.createScript(viewClass, this);
            return this.build(script);
        }
        throw new RuntimeException("Only scripts can be executed via build(Class)");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object build(Script script) {
        MetaClass scriptMetaClass = script.getMetaClass();
        script.setMetaClass(new FactoryInterceptorMetaClass(scriptMetaClass, this));
        script.setBinding(this);
        Object oldScriptName = this.getProxyBuilder().getVariables().get(SCRIPT_CLASS_NAME);
        try {
            this.getProxyBuilder().setVariable(SCRIPT_CLASS_NAME, script.getClass().getName());
            Object object = script.run();
            return object;
        }
        finally {
            if (oldScriptName != null) {
                this.getProxyBuilder().setVariable(SCRIPT_CLASS_NAME, oldScriptName);
            } else {
                this.getProxyBuilder().getVariables().remove(SCRIPT_CLASS_NAME);
            }
        }
    }

    public Object build(String script, GroovyClassLoader loader) {
        return this.build(loader.parseClass(script));
    }

    public Object withBuilder(FactoryBuilderSupport builder, Closure closure) {
        if (builder == null || closure == null) {
            return null;
        }
        Object result = null;
        Map<String, Object> previousContext = this.getProxyBuilder().getContext();
        FactoryBuilderSupport previousProxyBuilder = this.localProxyBuilder.get();
        try {
            this.localProxyBuilder.set(builder);
            closure.setDelegate(builder);
            result = closure.call();
        }
        catch (RuntimeException e) {
            this.localProxyBuilder.set(previousProxyBuilder);
            if (this.getProxyBuilder().getContexts().contains(previousContext)) {
                Map<String, Object> context = this.getProxyBuilder().getContext();
                while (context != null && context != previousContext) {
                    this.getProxyBuilder().popContext();
                    context = this.getProxyBuilder().getContext();
                }
            }
            throw e;
        }
        finally {
            this.localProxyBuilder.set(previousProxyBuilder);
        }
        return result;
    }

    public Object withBuilder(FactoryBuilderSupport builder, String name, Closure closure) {
        if (name == null) {
            return null;
        }
        Object result = this.getProxyBuilder().withBuilder(builder, closure);
        return this.getProxyBuilder().invokeMethod(name, new Object[]{result});
    }

    public Object withBuilder(Map attributes, FactoryBuilderSupport builder, String name, Closure closure) {
        if (name == null) {
            return null;
        }
        Object result = this.getProxyBuilder().withBuilder(builder, closure);
        return this.getProxyBuilder().invokeMethod(name, new Object[]{attributes, result});
    }

    public void addDisposalClosure(Closure closure) {
        this.disposalClosures.add(closure);
    }

    public List<Closure> getDisposalClosures() {
        return Collections.unmodifiableList(this.disposalClosures);
    }

    public void dispose() {
        for (int i = this.disposalClosures.size() - 1; i >= 0; --i) {
            this.disposalClosures.get(i).call();
        }
    }
}

