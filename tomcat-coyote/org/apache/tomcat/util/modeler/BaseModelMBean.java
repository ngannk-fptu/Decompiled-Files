/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.modeler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InstanceNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.modeler.BaseAttributeFilter;
import org.apache.tomcat.util.modeler.BaseNotificationBroadcaster;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.res.StringManager;

public class BaseModelMBean
implements DynamicMBean,
MBeanRegistration,
ModelMBeanNotificationBroadcaster {
    private static final Log log = LogFactory.getLog(BaseModelMBean.class);
    private static final StringManager sm = StringManager.getManager(BaseModelMBean.class);
    protected ObjectName oname = null;
    protected BaseNotificationBroadcaster attributeBroadcaster = null;
    protected BaseNotificationBroadcaster generalBroadcaster = null;
    protected ManagedBean managedBean = null;
    protected Object resource = null;
    static final Object[] NO_ARGS_PARAM = new Object[0];
    protected String resourceType = null;

    @Override
    public Object getAttribute(String name) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeName")), sm.getString("baseModelMBean.nullAttributeName"));
        }
        if (this.resource instanceof DynamicMBean && !(this.resource instanceof BaseModelMBean)) {
            return ((DynamicMBean)this.resource).getAttribute(name);
        }
        Method m = this.managedBean.getGetter(name, this, this.resource);
        Object result = null;
        try {
            Class<?> declaring = m.getDeclaringClass();
            result = declaring.isAssignableFrom(this.getClass()) ? m.invoke((Object)this, NO_ARGS_PARAM) : m.invoke(this.resource, NO_ARGS_PARAM);
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        catch (Exception e) {
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        return result;
    }

    @Override
    public AttributeList getAttributes(String[] names) {
        if (names == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeNameList")), sm.getString("baseModelMBean.nullAttributeNameList"));
        }
        AttributeList response = new AttributeList();
        for (String name : names) {
            try {
                response.add(new Attribute(name, this.getAttribute(name)));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return response;
    }

    public void setManagedBean(ManagedBean managedBean) {
        this.managedBean = managedBean;
    }

    @Override
    public MBeanInfo getMBeanInfo() {
        return this.managedBean.getMBeanInfo();
    }

    @Override
    public Object invoke(String name, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        if (this.resource instanceof DynamicMBean && !(this.resource instanceof BaseModelMBean)) {
            return ((DynamicMBean)this.resource).invoke(name, params, signature);
        }
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullMethodName")), sm.getString("baseModelMBean.nullMethodName"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("Invoke " + name));
        }
        Method method = this.managedBean.getInvoke(name, params, signature, this, this.resource);
        Object result = null;
        try {
            result = method.getDeclaringClass().isAssignableFrom(this.getClass()) ? method.invoke((Object)this, params) : method.invoke(this.resource, params);
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            log.error((Object)sm.getString("baseModelMBean.invokeError", new Object[]{name}), t);
            if (t == null) {
                t = e;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            throw new MBeanException((Exception)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        catch (Exception e) {
            log.error((Object)sm.getString("baseModelMBean.invokeError", new Object[]{name}), (Throwable)e);
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        return result;
    }

    static Class<?> getAttributeClass(String signature) throws ReflectionException {
        if (signature.equals(Boolean.TYPE.getName())) {
            return Boolean.TYPE;
        }
        if (signature.equals(Byte.TYPE.getName())) {
            return Byte.TYPE;
        }
        if (signature.equals(Character.TYPE.getName())) {
            return Character.TYPE;
        }
        if (signature.equals(Double.TYPE.getName())) {
            return Double.TYPE;
        }
        if (signature.equals(Float.TYPE.getName())) {
            return Float.TYPE;
        }
        if (signature.equals(Integer.TYPE.getName())) {
            return Integer.TYPE;
        }
        if (signature.equals(Long.TYPE.getName())) {
            return Long.TYPE;
        }
        if (signature.equals(Short.TYPE.getName())) {
            return Short.TYPE;
        }
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            if (cl != null) {
                return cl.loadClass(signature);
            }
        }
        catch (ClassNotFoundException cl) {
            // empty catch block
        }
        try {
            return Class.forName(signature);
        }
        catch (ClassNotFoundException e) {
            throw new ReflectionException(e, sm.getString("baseModelMBean.cnfeForSignature", new Object[]{signature}));
        }
    }

    @Override
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Setting attribute " + this + " " + attribute));
        }
        if (this.resource instanceof DynamicMBean && !(this.resource instanceof BaseModelMBean)) {
            try {
                ((DynamicMBean)this.resource).setAttribute(attribute);
            }
            catch (InvalidAttributeValueException e) {
                throw new MBeanException(e);
            }
            return;
        }
        if (attribute == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttribute")), sm.getString("baseModelMBean.nullAttribute"));
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullAttributeName")), sm.getString("baseModelMBean.nullAttributeName"));
        }
        Object oldValue = null;
        Method m = this.managedBean.getSetter(name, this, this.resource);
        try {
            if (m.getDeclaringClass().isAssignableFrom(this.getClass())) {
                m.invoke((Object)this, value);
            } else {
                m.invoke(this.resource, value);
            }
        }
        catch (InvocationTargetException e) {
            Throwable t = e.getTargetException();
            if (t == null) {
                t = e;
            }
            if (t instanceof RuntimeException) {
                throw new RuntimeOperationsException((RuntimeException)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            if (t instanceof Error) {
                throw new RuntimeErrorException((Error)t, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
            }
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        catch (Exception e) {
            log.error((Object)sm.getString("baseModelMBean.invokeError", new Object[]{name}), (Throwable)e);
            throw new MBeanException(e, sm.getString("baseModelMBean.invokeError", new Object[]{name}));
        }
        try {
            this.sendAttributeChangeNotification(new Attribute(name, oldValue), attribute);
        }
        catch (Exception ex) {
            log.error((Object)sm.getString("baseModelMBean.notificationError", new Object[]{name}), (Throwable)ex);
        }
    }

    public String toString() {
        if (this.resource == null) {
            return "BaseModelMbean[" + this.resourceType + "]";
        }
        return this.resource.toString();
    }

    @Override
    public AttributeList setAttributes(AttributeList attributes) {
        AttributeList response = new AttributeList();
        if (attributes == null) {
            return response;
        }
        String[] names = new String[attributes.size()];
        int n = 0;
        for (Object attribute : attributes) {
            Attribute item = (Attribute)attribute;
            names[n++] = item.getName();
            try {
                this.setAttribute(item);
            }
            catch (Exception exception) {}
        }
        return this.getAttributes(names);
    }

    public Object getManagedResource() throws InstanceNotFoundException, InvalidTargetObjectTypeException, MBeanException, RuntimeOperationsException {
        if (this.resource == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullResource")), sm.getString("baseModelMBean.nullResource"));
        }
        return this.resource;
    }

    public void setManagedResource(Object resource, String type) throws InstanceNotFoundException, MBeanException, RuntimeOperationsException {
        if (resource == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullResource")), sm.getString("baseModelMBean.nullResource"));
        }
        this.resource = resource;
        this.resourceType = resource.getClass().getName();
    }

    @Override
    public void addAttributeChangeNotificationListener(NotificationListener listener, String name, Object handback) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new BaseNotificationBroadcaster();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("addAttributeNotificationListener " + listener));
        }
        BaseAttributeFilter filter = new BaseAttributeFilter(name);
        this.attributeBroadcaster.addNotificationListener(listener, filter, handback);
    }

    @Override
    public void removeAttributeChangeNotificationListener(NotificationListener listener, String name) throws ListenerNotFoundException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.removeNotificationListener(listener);
        }
    }

    @Override
    public void sendAttributeChangeNotification(AttributeChangeNotification notification) throws MBeanException, RuntimeOperationsException {
        if (notification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullNotification")), sm.getString("baseModelMBean.nullNotification"));
        }
        if (this.attributeBroadcaster == null) {
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("AttributeChangeNotification " + notification));
        }
        this.attributeBroadcaster.sendNotification(notification);
    }

    @Override
    public void sendAttributeChangeNotification(Attribute oldValue, Attribute newValue) throws MBeanException, RuntimeOperationsException {
        String type = null;
        if (newValue.getValue() != null) {
            type = newValue.getValue().getClass().getName();
        } else if (oldValue.getValue() != null) {
            type = oldValue.getValue().getClass().getName();
        } else {
            return;
        }
        AttributeChangeNotification notification = new AttributeChangeNotification(this, 1L, System.currentTimeMillis(), "Attribute value has changed", oldValue.getName(), type, oldValue.getValue(), newValue.getValue());
        this.sendAttributeChangeNotification(notification);
    }

    @Override
    public void sendNotification(Notification notification) throws MBeanException, RuntimeOperationsException {
        if (notification == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullNotification")), sm.getString("baseModelMBean.nullNotification"));
        }
        if (this.generalBroadcaster == null) {
            return;
        }
        this.generalBroadcaster.sendNotification(notification);
    }

    @Override
    public void sendNotification(String message) throws MBeanException, RuntimeOperationsException {
        if (message == null) {
            throw new RuntimeOperationsException(new IllegalArgumentException(sm.getString("baseModelMBean.nullMessage")), sm.getString("baseModelMBean.nullMessage"));
        }
        Notification notification = new Notification("jmx.modelmbean.generic", (Object)this, 1L, message);
        this.sendNotification(notification);
    }

    @Override
    public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("addNotificationListener " + listener));
        }
        if (this.generalBroadcaster == null) {
            this.generalBroadcaster = new BaseNotificationBroadcaster();
        }
        this.generalBroadcaster.addNotificationListener(listener, filter, handback);
        if (this.attributeBroadcaster == null) {
            this.attributeBroadcaster = new BaseNotificationBroadcaster();
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)("addAttributeNotificationListener " + listener));
        }
        this.attributeBroadcaster.addNotificationListener(listener, filter, handback);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        MBeanNotificationInfo[] current = this.getMBeanInfo().getNotifications();
        MBeanNotificationInfo[] response = new MBeanNotificationInfo[current.length + 2];
        response[0] = new MBeanNotificationInfo(new String[]{"jmx.modelmbean.generic"}, "GENERIC", "Text message notification from the managed resource");
        response[1] = new MBeanNotificationInfo(new String[]{"jmx.attribute.change"}, "ATTRIBUTE_CHANGE", "Observed MBean attribute value has changed");
        System.arraycopy(current, 0, response, 2, current.length);
        return response;
    }

    @Override
    public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
        if (listener == null) {
            throw new IllegalArgumentException(sm.getString("baseModelMBean.nullListener"));
        }
        if (this.generalBroadcaster != null) {
            this.generalBroadcaster.removeNotificationListener(listener);
        }
        if (this.attributeBroadcaster != null) {
            this.attributeBroadcaster.removeNotificationListener(listener);
        }
    }

    public String getModelerType() {
        return this.resourceType;
    }

    public String getClassName() {
        return this.getModelerType();
    }

    public ObjectName getJmxName() {
        return this.oname;
    }

    public String getObjectName() {
        if (this.oname != null) {
            return this.oname.toString();
        }
        return null;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug((Object)("preRegister " + this.resource + " " + name));
        }
        this.oname = name;
        if (this.resource instanceof MBeanRegistration) {
            this.oname = ((MBeanRegistration)this.resource).preRegister(server, name);
        }
        return this.oname;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).postRegister(registrationDone);
        }
    }

    @Override
    public void preDeregister() throws Exception {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).preDeregister();
        }
    }

    @Override
    public void postDeregister() {
        if (this.resource instanceof MBeanRegistration) {
            ((MBeanRegistration)this.resource).postDeregister();
        }
    }
}

