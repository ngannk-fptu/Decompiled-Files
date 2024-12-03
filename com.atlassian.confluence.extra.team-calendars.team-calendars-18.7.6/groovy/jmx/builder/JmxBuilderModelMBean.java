/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxEventListener;
import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.AttributeChangeNotification;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.RequiredModelMBean;

public class JmxBuilderModelMBean
extends RequiredModelMBean
implements NotificationListener {
    private List<String> methodListeners = new ArrayList<String>(0);
    private Object managedObject;

    public JmxBuilderModelMBean(Object objectRef) throws MBeanException, RuntimeOperationsException, InstanceNotFoundException, InvalidTargetObjectTypeException {
        super.setManagedResource(objectRef, "ObjectReference");
    }

    public JmxBuilderModelMBean() throws MBeanException, RuntimeOperationsException {
    }

    public JmxBuilderModelMBean(ModelMBeanInfo mbi) throws MBeanException, RuntimeOperationsException {
        super(mbi);
    }

    public synchronized void setManagedResource(Object obj) {
        this.managedObject = obj;
        try {
            super.setManagedResource(obj, "ObjectReference");
        }
        catch (Exception ex) {
            throw new JmxBuilderException(ex);
        }
    }

    public void addOperationCallListeners(Map<String, Map<String, Map<String, Object>>> descriptor) {
        if (descriptor == null) {
            return;
        }
        for (Map.Entry<String, Map<String, Map<String, Object>>> item : descriptor.entrySet()) {
            if (!item.getValue().containsKey("methodListener")) continue;
            Map<String, Object> listener = item.getValue().get("methodListener");
            String target = (String)listener.get("target");
            this.methodListeners.add(target);
            String listenerType = (String)listener.get("type");
            listener.put("managedObject", this.managedObject);
            if (listenerType.equals("attributeChangeListener")) {
                try {
                    this.addAttributeChangeNotificationListener(AttributeChangedListener.getListener(), (String)listener.get("attribute"), listener);
                }
                catch (MBeanException e) {
                    throw new JmxBuilderException(e);
                }
            }
            if (!listenerType.equals("operationCallListener")) continue;
            String eventType = "jmx.operation.call." + target;
            NotificationFilterSupport filter = new NotificationFilterSupport();
            filter.enableType(eventType);
            this.addNotificationListener(JmxEventListener.getListener(), filter, listener);
        }
    }

    public void addEventListeners(MBeanServer server, Map<String, Map<String, Object>> descriptor) {
        for (Map.Entry<String, Map<String, Object>> item : descriptor.entrySet()) {
            Map<String, Object> listener = item.getValue();
            ObjectName broadcaster = (ObjectName)listener.get("from");
            try {
                String eventType = (String)listener.get("event");
                if (eventType != null) {
                    NotificationFilterSupport filter = new NotificationFilterSupport();
                    filter.enableType(eventType);
                    server.addNotificationListener(broadcaster, JmxEventListener.getListener(), (NotificationFilter)filter, listener);
                    continue;
                }
                server.addNotificationListener(broadcaster, JmxEventListener.getListener(), null, listener);
            }
            catch (InstanceNotFoundException e) {
                throw new JmxBuilderException(e);
            }
        }
    }

    @Override
    public Object invoke(String opName, Object[] opArgs, String[] signature) throws MBeanException, ReflectionException {
        Object result = super.invoke(opName, opArgs, signature);
        if (this.methodListeners.contains(opName)) {
            this.sendNotification(this.buildCallListenerNotification(opName));
        }
        return result;
    }

    @Override
    public void handleNotification(Notification note, Object handback) {
    }

    private Notification buildCallListenerNotification(String target) {
        return new Notification("jmx.operation.call." + target, (Object)this, NumberSequencer.getNextSequence(), System.currentTimeMillis());
    }

    private static final class AttributeChangedListener
    implements NotificationListener {
        private static AttributeChangedListener listener;

        public static synchronized AttributeChangedListener getListener() {
            if (listener == null) {
                listener = new AttributeChangedListener();
            }
            return listener;
        }

        private AttributeChangedListener() {
        }

        @Override
        public void handleNotification(Notification notification, Object handback) {
            AttributeChangeNotification note = (AttributeChangeNotification)notification;
            Map event = (Map)handback;
            if (event != null) {
                Object del = event.get("managedObject");
                Object callback = event.get("callback");
                if (callback != null && callback instanceof Closure) {
                    Closure closure = (Closure)callback;
                    closure.setDelegate(del);
                    if (closure.getMaximumNumberOfParameters() == 1) {
                        closure.call((Object)AttributeChangedListener.buildAttributeNotificationPacket(note));
                    } else {
                        closure.call();
                    }
                }
            }
        }

        private static Map buildAttributeNotificationPacket(AttributeChangeNotification note) {
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("oldValue", note.getOldValue());
            result.put("newValue", note.getNewValue());
            result.put("attribute", note.getAttributeName());
            result.put("attributeType", note.getAttributeType());
            result.put("sequenceNumber", note.getSequenceNumber());
            result.put("timeStamp", note.getTimeStamp());
            return result;
        }
    }

    private static class NumberSequencer {
        private static AtomicLong num = new AtomicLong(0L);

        private NumberSequencer() {
        }

        public static long getNextSequence() {
            return num.incrementAndGet();
        }
    }
}

