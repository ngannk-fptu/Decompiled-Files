/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.access;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMException;
import javax.management.JMX;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXServiceURL;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.jmx.access.ConnectorDelegate;
import org.springframework.jmx.access.InvalidInvocationException;
import org.springframework.jmx.access.InvocationFailureException;
import org.springframework.jmx.access.MBeanConnectFailureException;
import org.springframework.jmx.access.MBeanInfoRetrievalException;
import org.springframework.jmx.support.JmxUtils;
import org.springframework.jmx.support.ObjectNameManager;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class MBeanClientInterceptor
implements MethodInterceptor,
BeanClassLoaderAware,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private MBeanServerConnection server;
    @Nullable
    private JMXServiceURL serviceUrl;
    @Nullable
    private Map<String, ?> environment;
    @Nullable
    private String agentId;
    private boolean connectOnStartup = true;
    private boolean refreshOnConnectFailure = false;
    @Nullable
    private ObjectName objectName;
    private boolean useStrictCasing = true;
    @Nullable
    private Class<?> managementInterface;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private final ConnectorDelegate connector = new ConnectorDelegate();
    @Nullable
    private MBeanServerConnection serverToUse;
    @Nullable
    private MBeanServerInvocationHandler invocationHandler;
    private Map<String, MBeanAttributeInfo> allowedAttributes = Collections.emptyMap();
    private Map<MethodCacheKey, MBeanOperationInfo> allowedOperations = Collections.emptyMap();
    private final Map<Method, String[]> signatureCache = new HashMap<Method, String[]>();
    private final Object preparationMonitor = new Object();

    public void setServer(MBeanServerConnection server) {
        this.server = server;
    }

    public void setServiceUrl(String url) throws MalformedURLException {
        this.serviceUrl = new JMXServiceURL(url);
    }

    public void setEnvironment(@Nullable Map<String, ?> environment2) {
        this.environment = environment2;
    }

    @Nullable
    public Map<String, ?> getEnvironment() {
        return this.environment;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public void setConnectOnStartup(boolean connectOnStartup) {
        this.connectOnStartup = connectOnStartup;
    }

    public void setRefreshOnConnectFailure(boolean refreshOnConnectFailure) {
        this.refreshOnConnectFailure = refreshOnConnectFailure;
    }

    public void setObjectName(Object objectName) throws MalformedObjectNameException {
        this.objectName = ObjectNameManager.getInstance(objectName);
    }

    public void setUseStrictCasing(boolean useStrictCasing) {
        this.useStrictCasing = useStrictCasing;
    }

    public void setManagementInterface(@Nullable Class<?> managementInterface) {
        this.managementInterface = managementInterface;
    }

    @Nullable
    protected final Class<?> getManagementInterface() {
        return this.managementInterface;
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.server != null && this.refreshOnConnectFailure) {
            throw new IllegalArgumentException("'refreshOnConnectFailure' does not work when setting a 'server' reference. Prefer 'serviceUrl' etc instead.");
        }
        if (this.connectOnStartup) {
            this.prepare();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void prepare() {
        Object object = this.preparationMonitor;
        synchronized (object) {
            if (this.server != null) {
                this.serverToUse = this.server;
            } else {
                this.serverToUse = null;
                this.serverToUse = this.connector.connect(this.serviceUrl, this.environment, this.agentId);
            }
            this.invocationHandler = null;
            if (this.useStrictCasing) {
                Assert.state(this.objectName != null, "No ObjectName set");
                this.invocationHandler = new MBeanServerInvocationHandler(this.serverToUse, this.objectName, this.managementInterface != null && JMX.isMXBeanInterface(this.managementInterface));
            } else {
                this.retrieveMBeanInfo(this.serverToUse);
            }
        }
    }

    private void retrieveMBeanInfo(MBeanServerConnection server) throws MBeanInfoRetrievalException {
        try {
            MBeanInfo info = server.getMBeanInfo(this.objectName);
            MBeanAttributeInfo[] attributeInfo = info.getAttributes();
            this.allowedAttributes = new HashMap<String, MBeanAttributeInfo>(attributeInfo.length);
            for (MBeanAttributeInfo infoEle : attributeInfo) {
                this.allowedAttributes.put(infoEle.getName(), infoEle);
            }
            MBeanOperationInfo[] operationInfo = info.getOperations();
            this.allowedOperations = new HashMap<MethodCacheKey, MBeanOperationInfo>(operationInfo.length);
            for (MBeanOperationInfo infoEle : operationInfo) {
                Class<?>[] paramTypes = JmxUtils.parameterInfoToTypes(infoEle.getSignature(), this.beanClassLoader);
                this.allowedOperations.put(new MethodCacheKey(infoEle.getName(), paramTypes), infoEle);
            }
        }
        catch (ClassNotFoundException ex) {
            throw new MBeanInfoRetrievalException("Unable to locate class specified in method signature", ex);
        }
        catch (IntrospectionException ex) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]", ex);
        }
        catch (InstanceNotFoundException ex) {
            throw new MBeanInfoRetrievalException("Unable to obtain MBean info for bean [" + this.objectName + "]: it is likely that this bean was unregistered during the proxy creation process", ex);
        }
        catch (ReflectionException ex) {
            throw new MBeanInfoRetrievalException("Unable to read MBean info for bean [ " + this.objectName + "]", ex);
        }
        catch (IOException ex) {
            throw new MBeanInfoRetrievalException("An IOException occurred when communicating with the MBeanServer. It is likely that you are communicating with a remote MBeanServer. Check the inner exception for exact details.", ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean isPrepared() {
        Object object = this.preparationMonitor;
        synchronized (object) {
            return this.serverToUse != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nullable
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object object = this.preparationMonitor;
        synchronized (object) {
            if (!this.isPrepared()) {
                this.prepare();
            }
        }
        try {
            return this.doInvoke(invocation);
        }
        catch (IOException | MBeanConnectFailureException ex) {
            return this.handleConnectFailure(invocation, ex);
        }
    }

    @Nullable
    protected Object handleConnectFailure(MethodInvocation invocation, Exception ex) throws Throwable {
        if (this.refreshOnConnectFailure) {
            String msg = "Could not connect to JMX server - retrying";
            if (this.logger.isDebugEnabled()) {
                this.logger.warn(msg, ex);
            } else if (this.logger.isWarnEnabled()) {
                this.logger.warn(msg);
            }
            this.prepare();
            return this.doInvoke(invocation);
        }
        throw ex;
    }

    @Nullable
    protected Object doInvoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        try {
            PropertyDescriptor pd;
            Object result = this.invocationHandler != null ? this.invocationHandler.invoke(invocation.getThis(), method, invocation.getArguments()) : ((pd = BeanUtils.findPropertyForMethod(method)) != null ? this.invokeAttribute(pd, invocation) : this.invokeOperation(method, invocation.getArguments()));
            return this.convertResultValueIfNecessary(result, new MethodParameter(method, -1));
        }
        catch (MBeanException ex) {
            throw ex.getTargetException();
        }
        catch (RuntimeMBeanException ex) {
            throw ex.getTargetException();
        }
        catch (RuntimeErrorException ex) {
            throw ex.getTargetError();
        }
        catch (RuntimeOperationsException ex) {
            RuntimeException rex = ex.getTargetException();
            if (rex instanceof RuntimeMBeanException) {
                throw ((RuntimeMBeanException)rex).getTargetException();
            }
            if (rex instanceof RuntimeErrorException) {
                throw ((RuntimeErrorException)rex).getTargetError();
            }
            throw rex;
        }
        catch (OperationsException ex) {
            if (ReflectionUtils.declaresException(method, ex.getClass())) {
                throw ex;
            }
            throw new InvalidInvocationException(ex.getMessage());
        }
        catch (JMException ex) {
            if (ReflectionUtils.declaresException(method, ex.getClass())) {
                throw ex;
            }
            throw new InvocationFailureException("JMX access failed", ex);
        }
        catch (IOException ex) {
            if (ReflectionUtils.declaresException(method, ex.getClass())) {
                throw ex;
            }
            throw new MBeanConnectFailureException("I/O failure during JMX access", ex);
        }
    }

    @Nullable
    private Object invokeAttribute(PropertyDescriptor pd, MethodInvocation invocation) throws JMException, IOException {
        Assert.state(this.serverToUse != null, "No MBeanServerConnection available");
        String attributeName = JmxUtils.getAttributeName(pd, this.useStrictCasing);
        MBeanAttributeInfo inf = this.allowedAttributes.get(attributeName);
        if (inf == null) {
            throw new InvalidInvocationException("Attribute '" + pd.getName() + "' is not exposed on the management interface");
        }
        if (invocation.getMethod().equals(pd.getReadMethod())) {
            if (inf.isReadable()) {
                return this.serverToUse.getAttribute(this.objectName, attributeName);
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not readable");
        }
        if (invocation.getMethod().equals(pd.getWriteMethod())) {
            if (inf.isWritable()) {
                this.serverToUse.setAttribute(this.objectName, new Attribute(attributeName, invocation.getArguments()[0]));
                return null;
            }
            throw new InvalidInvocationException("Attribute '" + attributeName + "' is not writable");
        }
        throw new IllegalStateException("Method [" + invocation.getMethod() + "] is neither a bean property getter nor a setter");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object invokeOperation(Method method, Object[] args) throws JMException, IOException {
        String[] signature;
        Assert.state(this.serverToUse != null, "No MBeanServerConnection available");
        MethodCacheKey key = new MethodCacheKey(method.getName(), method.getParameterTypes());
        MBeanOperationInfo info = this.allowedOperations.get(key);
        if (info == null) {
            throw new InvalidInvocationException("Operation '" + method.getName() + "' is not exposed on the management interface");
        }
        Map<Method, String[]> map = this.signatureCache;
        synchronized (map) {
            signature = this.signatureCache.get(method);
            if (signature == null) {
                signature = JmxUtils.getMethodSignature(method);
                this.signatureCache.put(method, signature);
            }
        }
        return this.serverToUse.invoke(this.objectName, method.getName(), args, signature);
    }

    @Nullable
    protected Object convertResultValueIfNecessary(@Nullable Object result, MethodParameter parameter) {
        Class<?> targetClass = parameter.getParameterType();
        try {
            if (result == null) {
                return null;
            }
            if (ClassUtils.isAssignableValue(targetClass, result)) {
                return result;
            }
            if (result instanceof CompositeData) {
                Method fromMethod = targetClass.getMethod("from", CompositeData.class);
                return ReflectionUtils.invokeMethod(fromMethod, null, result);
            }
            if (result instanceof CompositeData[]) {
                Class<?> elementType;
                Object[] array = (CompositeData[])result;
                if (targetClass.isArray()) {
                    return this.convertDataArrayToTargetArray(array, targetClass);
                }
                if (Collection.class.isAssignableFrom(targetClass) && (elementType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric(new int[0])) != null) {
                    return this.convertDataArrayToTargetCollection(array, targetClass, elementType);
                }
            } else {
                if (result instanceof TabularData) {
                    Method fromMethod = targetClass.getMethod("from", TabularData.class);
                    return ReflectionUtils.invokeMethod(fromMethod, null, result);
                }
                if (result instanceof TabularData[]) {
                    Class<?> elementType;
                    Object[] array = (TabularData[])result;
                    if (targetClass.isArray()) {
                        return this.convertDataArrayToTargetArray(array, targetClass);
                    }
                    if (Collection.class.isAssignableFrom(targetClass) && (elementType = ResolvableType.forMethodParameter(parameter).asCollection().resolveGeneric(new int[0])) != null) {
                        return this.convertDataArrayToTargetCollection(array, targetClass, elementType);
                    }
                }
            }
            throw new InvocationFailureException("Incompatible result value [" + result + "] for target type [" + targetClass.getName() + "]");
        }
        catch (NoSuchMethodException ex) {
            throw new InvocationFailureException("Could not obtain 'from(CompositeData)' / 'from(TabularData)' method on target type [" + targetClass.getName() + "] for conversion of MXBean data structure [" + result + "]");
        }
    }

    private Object convertDataArrayToTargetArray(Object[] array, Class<?> targetClass) throws NoSuchMethodException {
        Class<?> targetType = targetClass.getComponentType();
        Method fromMethod = targetType.getMethod("from", array.getClass().getComponentType());
        Object resultArray = Array.newInstance(targetType, array.length);
        for (int i = 0; i < array.length; ++i) {
            Array.set(resultArray, i, ReflectionUtils.invokeMethod(fromMethod, null, array[i]));
        }
        return resultArray;
    }

    private Collection<?> convertDataArrayToTargetCollection(Object[] array, Class<?> collectionType, Class<?> elementType) throws NoSuchMethodException {
        Method fromMethod = elementType.getMethod("from", array.getClass().getComponentType());
        Collection<Object> resultColl = CollectionFactory.createCollection(collectionType, Array.getLength(array));
        for (int i = 0; i < array.length; ++i) {
            resultColl.add(ReflectionUtils.invokeMethod(fromMethod, null, array[i]));
        }
        return resultColl;
    }

    @Override
    public void destroy() {
        this.connector.close();
    }

    private static final class MethodCacheKey
    implements Comparable<MethodCacheKey> {
        private final String name;
        private final Class<?>[] parameterTypes;

        public MethodCacheKey(String name, @Nullable Class<?>[] parameterTypes) {
            this.name = name;
            this.parameterTypes = parameterTypes != null ? parameterTypes : new Class[]{};
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            MethodCacheKey otherKey = (MethodCacheKey)other;
            return this.name.equals(otherKey.name) && Arrays.equals(this.parameterTypes, otherKey.parameterTypes);
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public String toString() {
            return this.name + "(" + StringUtils.arrayToCommaDelimitedString(this.parameterTypes) + ")";
        }

        @Override
        public int compareTo(MethodCacheKey other) {
            int result = this.name.compareTo(other.name);
            if (result != 0) {
                return result;
            }
            if (this.parameterTypes.length < other.parameterTypes.length) {
                return -1;
            }
            if (this.parameterTypes.length > other.parameterTypes.length) {
                return 1;
            }
            for (int i = 0; i < this.parameterTypes.length; ++i) {
                result = this.parameterTypes[i].getName().compareTo(other.parameterTypes[i].getName());
                if (result == 0) continue;
                return result;
            }
            return 0;
        }
    }
}

