/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.StringUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.modeler;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import org.apache.tomcat.util.buf.StringUtils;
import org.apache.tomcat.util.modeler.AttributeInfo;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.FeatureInfo;
import org.apache.tomcat.util.modeler.NotificationInfo;
import org.apache.tomcat.util.modeler.OperationInfo;
import org.apache.tomcat.util.res.StringManager;

public class ManagedBean
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final StringManager sm = StringManager.getManager(ManagedBean.class);
    private static final String BASE_MBEAN = "org.apache.tomcat.util.modeler.BaseModelMBean";
    static final Class<?>[] NO_ARGS_PARAM_SIG = new Class[0];
    private final ReadWriteLock mBeanInfoLock = new ReentrantReadWriteLock();
    private volatile transient MBeanInfo info = null;
    private Map<String, AttributeInfo> attributes = new HashMap<String, AttributeInfo>();
    private Map<String, OperationInfo> operations = new HashMap<String, OperationInfo>();
    protected String className = "org.apache.tomcat.util.modeler.BaseModelMBean";
    protected String description = null;
    protected String domain = null;
    protected String group = null;
    protected String name = null;
    private NotificationInfo[] notifications = new NotificationInfo[0];
    protected String type = null;

    public ManagedBean() {
        AttributeInfo ai = new AttributeInfo();
        ai.setName("modelerType");
        ai.setDescription("Type of the modeled resource. Can be set only once");
        ai.setType("java.lang.String");
        ai.setWriteable(false);
        this.addAttribute(ai);
    }

    public AttributeInfo[] getAttributes() {
        return this.attributes.values().toArray(new AttributeInfo[0]);
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.className = className;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.description = description;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.name = name;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public NotificationInfo[] getNotifications() {
        return this.notifications;
    }

    public OperationInfo[] getOperations() {
        return this.operations.values().toArray(new OperationInfo[0]);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            this.type = type;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public void addAttribute(AttributeInfo attribute) {
        this.attributes.put(attribute.getName(), attribute);
    }

    public void addNotification(NotificationInfo notification) {
        this.mBeanInfoLock.writeLock().lock();
        try {
            NotificationInfo[] results = new NotificationInfo[this.notifications.length + 1];
            System.arraycopy(this.notifications, 0, results, 0, this.notifications.length);
            results[this.notifications.length] = notification;
            this.notifications = results;
            this.info = null;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public void addOperation(OperationInfo operation) {
        this.operations.put(this.createOperationKey(operation), operation);
    }

    public DynamicMBean createMBean(Object instance) throws InstanceNotFoundException, MBeanException, RuntimeOperationsException {
        BaseModelMBean mbean = null;
        if (this.getClassName().equals(BASE_MBEAN)) {
            mbean = new BaseModelMBean();
        } else {
            Class<?> clazz = null;
            Exception ex = null;
            try {
                clazz = Class.forName(this.getClassName());
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (clazz == null) {
                try {
                    ClassLoader cl = Thread.currentThread().getContextClassLoader();
                    if (cl != null) {
                        clazz = cl.loadClass(this.getClassName());
                    }
                }
                catch (Exception e) {
                    ex = e;
                }
            }
            if (clazz == null) {
                throw new MBeanException(ex, sm.getString("managedMBean.cannotLoadClass", new Object[]{this.getClassName()}));
            }
            try {
                mbean = (BaseModelMBean)clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (RuntimeOperationsException e) {
                throw e;
            }
            catch (Exception e) {
                throw new MBeanException(e, sm.getString("managedMBean.cannotInstantiateClass", new Object[]{this.getClassName()}));
            }
        }
        mbean.setManagedBean(this);
        if (instance != null) {
            mbean.setManagedResource(instance, "ObjectReference");
        }
        return mbean;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    MBeanInfo getMBeanInfo() {
        this.mBeanInfoLock.readLock().lock();
        try {
            if (this.info != null) {
                MBeanInfo mBeanInfo = this.info;
                return mBeanInfo;
            }
        }
        finally {
            this.mBeanInfoLock.readLock().unlock();
        }
        this.mBeanInfoLock.writeLock().lock();
        try {
            if (this.info == null) {
                AttributeInfo[] attrs = this.getAttributes();
                MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[attrs.length];
                for (int i = 0; i < attrs.length; ++i) {
                    attributes[i] = attrs[i].createAttributeInfo();
                }
                OperationInfo[] opers = this.getOperations();
                MBeanOperationInfo[] operations = new MBeanOperationInfo[opers.length];
                for (int i = 0; i < opers.length; ++i) {
                    operations[i] = opers[i].createOperationInfo();
                }
                NotificationInfo[] notifs = this.getNotifications();
                MBeanNotificationInfo[] notifications = new MBeanNotificationInfo[notifs.length];
                for (int i = 0; i < notifs.length; ++i) {
                    notifications[i] = notifs[i].createNotificationInfo();
                }
                this.info = new MBeanInfo(this.getClassName(), this.getDescription(), attributes, new MBeanConstructorInfo[0], operations, notifications);
            }
            MBeanInfo mBeanInfo = this.info;
            return mBeanInfo;
        }
        finally {
            this.mBeanInfoLock.writeLock().unlock();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ManagedBean[");
        sb.append("name=");
        sb.append(this.name);
        sb.append(", className=");
        sb.append(this.className);
        sb.append(", description=");
        sb.append(this.description);
        if (this.group != null) {
            sb.append(", group=");
            sb.append(this.group);
        }
        sb.append(", type=");
        sb.append(this.type);
        sb.append(']');
        return sb.toString();
    }

    Method getGetter(String aname, BaseModelMBean mbean, Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(sm.getString("managedMBean.noAttribute", new Object[]{aname, resource}));
        }
        String getMethod = attrInfo.getGetMethod();
        Object object = null;
        NoSuchMethodException exception = null;
        try {
            object = mbean;
            m = object.getClass().getMethod(getMethod, NO_ARGS_PARAM_SIG);
        }
        catch (NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                object = resource;
                m = object.getClass().getMethod(getMethod, NO_ARGS_PARAM_SIG);
                exception = null;
            }
            catch (NoSuchMethodException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, sm.getString("managedMBean.noGet", new Object[]{getMethod, resource}));
        }
        return m;
    }

    public Method getSetter(String aname, BaseModelMBean bean, Object resource) throws AttributeNotFoundException, ReflectionException {
        Method m = null;
        AttributeInfo attrInfo = this.attributes.get(aname);
        if (attrInfo == null) {
            throw new AttributeNotFoundException(sm.getString("managedMBean.noAttribute", new Object[]{aname, resource}));
        }
        String setMethod = attrInfo.getSetMethod();
        String argType = attrInfo.getType();
        Class[] signature = new Class[]{BaseModelMBean.getAttributeClass(argType)};
        Object object = null;
        NoSuchMethodException exception = null;
        try {
            object = bean;
            m = object.getClass().getMethod(setMethod, signature);
        }
        catch (NoSuchMethodException e) {
            exception = e;
        }
        if (m == null && resource != null) {
            try {
                object = resource;
                m = object.getClass().getMethod(setMethod, signature);
                exception = null;
            }
            catch (NoSuchMethodException e) {
                exception = e;
            }
        }
        if (exception != null) {
            throw new ReflectionException(exception, sm.getString("managedMBean.noSet", new Object[]{setMethod, resource}));
        }
        return m;
    }

    public Method getInvoke(String aname, Object[] params, String[] signature, BaseModelMBean bean, Object resource) throws MBeanException, ReflectionException {
        Method method = null;
        if (params == null) {
            params = new Object[]{};
        }
        if (signature == null) {
            signature = new String[]{};
        }
        if (params.length != signature.length) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("managedMBean.inconsistentArguments")), sm.getString("managedMBean.inconsistentArguments"));
        }
        OperationInfo opInfo = this.operations.get(this.createOperationKey(aname, signature));
        if (opInfo == null) {
            throw new MBeanException(new ServiceNotFoundException(sm.getString("managedMBean.noOperation", new Object[]{aname})), sm.getString("managedMBean.noOperation", new Object[]{aname}));
        }
        Class[] types = new Class[signature.length];
        for (int i = 0; i < signature.length; ++i) {
            types[i] = BaseModelMBean.getAttributeClass(signature[i]);
        }
        Object object = null;
        NoSuchMethodException exception = null;
        try {
            object = bean;
            method = object.getClass().getMethod(aname, types);
        }
        catch (NoSuchMethodException e) {
            exception = e;
        }
        try {
            if (method == null && resource != null) {
                object = resource;
                method = object.getClass().getMethod(aname, types);
            }
        }
        catch (NoSuchMethodException e) {
            exception = e;
        }
        if (method == null) {
            throw new ReflectionException(exception, sm.getString("managedMBean.noMethod", new Object[]{aname}));
        }
        return method;
    }

    private String createOperationKey(OperationInfo operation) {
        StringBuilder key = new StringBuilder(operation.getName());
        key.append('(');
        StringUtils.join((Object[])operation.getSignature(), (char)',', FeatureInfo::getType, (StringBuilder)key);
        key.append(')');
        return key.toString().intern();
    }

    private String createOperationKey(String methodName, String[] parameterTypes) {
        StringBuilder key = new StringBuilder(methodName);
        key.append('(');
        StringUtils.join((String[])parameterTypes, (char)',', (StringBuilder)key);
        key.append(')');
        return key.toString().intern();
    }
}

