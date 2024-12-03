/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  com.sun.xml.bind.marshaller.SAX2DOMEx
 *  javax.xml.ws.EndpointReference
 *  javax.xml.ws.WebServiceContext
 *  javax.xml.ws.WebServiceException
 *  javax.xml.ws.wsaddressing.W3CEndpointReference
 *  org.glassfish.ha.store.api.BackingStore
 *  org.glassfish.ha.store.api.Storeable
 */
package com.sun.xml.ws.server;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.bind.marshaller.SAX2DOMEx;
import com.sun.xml.ws.api.ha.HighAvailabilityProvider;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.developer.EPRRecipe;
import com.sun.xml.ws.developer.StatefulWebServiceManager;
import com.sun.xml.ws.resources.ServerMessages;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;
import com.sun.xml.ws.server.InvokerTube;
import com.sun.xml.ws.util.DOMUtil;
import com.sun.xml.ws.util.InjectionPlan;
import com.sun.xml.ws.util.xml.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.glassfish.ha.store.api.BackingStore;
import org.glassfish.ha.store.api.Storeable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public final class StatefulInstanceResolver<T>
extends AbstractMultiInstanceResolver<T>
implements StatefulWebServiceManager<T> {
    @Nullable
    private volatile T fallback;
    private HAMap haMap;
    private volatile long timeoutMilliseconds = 0L;
    private volatile StatefulWebServiceManager.Callback<T> timeoutCallback;
    private volatile Timer timer;
    private final ClassLoader appCL;
    private final boolean haEnabled;
    private static final QName COOKIE_TAG = new QName("http://jax-ws.dev.java.net/xml/ns/", "objectId", "jaxws");
    private static final Logger logger = Logger.getLogger("com.sun.xml.ws.server");

    public StatefulInstanceResolver(Class<T> clazz) {
        super(clazz);
        this.appCL = clazz.getClassLoader();
        boolean ha = false;
        if (HighAvailabilityProvider.INSTANCE.isHaEnvironmentConfigured() && Serializable.class.isAssignableFrom(clazz)) {
            logger.log(Level.WARNING, "{0} doesn''t implement Serializable. High availibility is disabled i.e.if a failover happens, stateful instance state is not failed over.", clazz);
            ha = true;
        }
        this.haEnabled = ha;
    }

    @Override
    @NotNull
    public T resolve(Packet request) {
        MessageHeaders headers = request.getMessage().getHeaders();
        Header header = headers.get(COOKIE_TAG, true);
        String id = null;
        if (header != null) {
            id = header.getStringContent();
            Instance o = this.haMap.get(id);
            if (o != null) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Restarting timer for objectId/Instance = [ {0} / {1} ]", new Object[]{id, o});
                }
                o.restartTimer();
                return o.instance;
            }
            logger.log(Level.INFO, "Request had an unrecognized object ID {0}", id);
        } else {
            logger.fine("No objectId header received");
        }
        T flbk = this.fallback;
        if (flbk != null) {
            return flbk;
        }
        if (id == null) {
            throw new WebServiceException(ServerMessages.STATEFUL_COOKIE_HEADER_REQUIRED(COOKIE_TAG));
        }
        throw new WebServiceException(ServerMessages.STATEFUL_COOKIE_HEADER_INCORRECT(COOKIE_TAG, id));
    }

    @Override
    public void postInvoke(@NotNull Packet request, @NotNull T servant) {
        this.haMap.put(servant);
    }

    @Override
    public void start(WSWebServiceContext wsc, WSEndpoint endpoint) {
        super.start(wsc, endpoint);
        this.haMap = new HAMap();
        if (endpoint.getBinding().getAddressingVersion() == null) {
            throw new WebServiceException(ServerMessages.STATEFUL_REQURES_ADDRESSING(this.clazz));
        }
        for (Field field : this.clazz.getDeclaredFields()) {
            if (field.getType() != StatefulWebServiceManager.class) continue;
            if (!Modifier.isStatic(field.getModifiers())) {
                throw new WebServiceException(ServerMessages.STATIC_RESOURCE_INJECTION_ONLY(StatefulWebServiceManager.class, field));
            }
            new InjectionPlan.FieldInjectionPlan<Object, StatefulInstanceResolver>(field).inject(null, this);
        }
        for (AccessibleObject accessibleObject : this.clazz.getDeclaredMethods()) {
            Class<?>[] paramTypes = ((Method)accessibleObject).getParameterTypes();
            if (paramTypes.length != 1 || paramTypes[0] != StatefulWebServiceManager.class) continue;
            if (!Modifier.isStatic(((Method)accessibleObject).getModifiers())) {
                throw new WebServiceException(ServerMessages.STATIC_RESOURCE_INJECTION_ONLY(StatefulWebServiceManager.class, accessibleObject));
            }
            new InjectionPlan.MethodInjectionPlan<Object, StatefulInstanceResolver>((Method)accessibleObject).inject(null, this);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispose() {
        HAMap hAMap = this.haMap;
        synchronized (hAMap) {
            for (Instance t : this.haMap.values()) {
                t.cancel();
                this.dispose(t.instance);
            }
            this.haMap.destroy();
        }
        if (this.fallback != null) {
            this.dispose(this.fallback);
            this.fallback = null;
        }
        this.stopTimer();
    }

    @Override
    @NotNull
    public W3CEndpointReference export(T o) {
        return this.export(W3CEndpointReference.class, o);
    }

    @Override
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> epr, T o) {
        return this.export(epr, o, null);
    }

    @Override
    public <EPR extends EndpointReference> EPR export(Class<EPR> epr, T o, EPRRecipe recipe) {
        return this.export(epr, InvokerTube.getCurrentPacket(), o, recipe);
    }

    @Override
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> epr, WebServiceContext context, T o) {
        if (context instanceof WSWebServiceContext) {
            WSWebServiceContext wswsc = (WSWebServiceContext)context;
            return this.export(epr, wswsc.getRequestPacket(), o);
        }
        throw new WebServiceException(ServerMessages.STATEFUL_INVALID_WEBSERVICE_CONTEXT(context));
    }

    @Override
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> adrsVer, @NotNull Packet currentRequest, T o) {
        return this.export(adrsVer, currentRequest, o, null);
    }

    @Override
    public <EPR extends EndpointReference> EPR export(Class<EPR> adrsVer, @NotNull Packet currentRequest, T o, EPRRecipe recipe) {
        return this.export(adrsVer, currentRequest.webServiceContextDelegate.getEPRAddress(currentRequest, this.owner), currentRequest.webServiceContextDelegate.getWSDLAddress(currentRequest, this.owner), o, recipe);
    }

    @Override
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> adrsVer, String endpointAddress, T o) {
        return this.export(adrsVer, endpointAddress, null, o, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public <EPR extends EndpointReference> EPR export(Class<EPR> adrsVer, String endpointAddress, String wsdlAddress, T o, EPRRecipe recipe) {
        if (endpointAddress == null) {
            throw new IllegalArgumentException("No address available");
        }
        String key = this.haMap.get(o);
        if (key != null) {
            return this.createEPR(key, adrsVer, endpointAddress, wsdlAddress, recipe);
        }
        StatefulInstanceResolver statefulInstanceResolver = this;
        synchronized (statefulInstanceResolver) {
            key = this.haMap.get(o);
            if (key != null) {
                return this.createEPR(key, adrsVer, endpointAddress, wsdlAddress, recipe);
            }
            if (o != null) {
                this.prepare(o);
            }
            key = UUID.randomUUID().toString();
            Instance instance = new Instance(o);
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Storing instance ID/Instance/Object/TimerTask = [ {0} / {1} / {2} / {3} ]", new Object[]{key, instance, instance.instance, instance.task});
            }
            this.haMap.put(key, instance);
            if (this.timeoutMilliseconds != 0L) {
                instance.restartTimer();
            }
        }
        return this.createEPR(key, adrsVer, endpointAddress, wsdlAddress, recipe);
    }

    private <EPR extends EndpointReference> EPR createEPR(String key, Class<EPR> eprClass, String address, String wsdlAddress, EPRRecipe recipe) {
        ArrayList<Element> referenceParameters = new ArrayList<Element>();
        ArrayList<Element> metadata = new ArrayList<Element>();
        Document doc = DOMUtil.createDom();
        Element cookie = doc.createElementNS(COOKIE_TAG.getNamespaceURI(), COOKIE_TAG.getPrefix() + ":" + COOKIE_TAG.getLocalPart());
        cookie.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + COOKIE_TAG.getPrefix(), COOKIE_TAG.getNamespaceURI());
        cookie.setTextContent(key);
        referenceParameters.add(cookie);
        if (recipe != null) {
            for (Header h : recipe.getReferenceParameters()) {
                doc = DOMUtil.createDom();
                SAX2DOMEx s2d = new SAX2DOMEx((Node)doc);
                try {
                    h.writeTo((ContentHandler)s2d, XmlUtil.DRACONIAN_ERROR_HANDLER);
                    referenceParameters.add((Element)doc.getLastChild());
                }
                catch (SAXException e) {
                    throw new WebServiceException("Unable to write EPR Reference parameters " + h, (Throwable)e);
                }
            }
            Transformer t = XmlUtil.newTransformer();
            for (Source s : recipe.getMetadata()) {
                try {
                    DOMResult r = new DOMResult();
                    t.transform(s, r);
                    Document d = (Document)r.getNode();
                    metadata.add(d.getDocumentElement());
                }
                catch (TransformerException e) {
                    throw new IllegalArgumentException("Unable to write EPR metadata " + s, e);
                }
            }
        }
        return (EPR)((EndpointReference)eprClass.cast(this.owner.getEndpointReference(eprClass, address, wsdlAddress, metadata, referenceParameters)));
    }

    @Override
    public void unexport(@Nullable T o) {
        if (o == null) {
            return;
        }
        Instance i = this.haMap.remove(o);
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "Removed Instance = [ {0} ], remaining instance keys = [ {1} ]", new Object[]{o, this.haMap.instances.keySet()});
        }
        if (i != null) {
            i.cancel();
        }
    }

    @Override
    public T resolve(EndpointReference epr) {
        class CookieSniffer
        extends DefaultHandler {
            StringBuilder buf = new StringBuilder();
            boolean inCookie = false;

            CookieSniffer() {
            }

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                if (localName.equals(COOKIE_TAG.getLocalPart()) && uri.equals(COOKIE_TAG.getNamespaceURI())) {
                    this.inCookie = true;
                }
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                if (this.inCookie) {
                    this.buf.append(ch, start, length);
                }
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {
                this.inCookie = false;
            }
        }
        CookieSniffer sniffer = new CookieSniffer();
        epr.writeTo((Result)new SAXResult(sniffer));
        Instance o = this.haMap.get(sniffer.buf.toString());
        if (o != null) {
            return o.instance;
        }
        return null;
    }

    @Override
    public void setFallbackInstance(T o) {
        if (o != null) {
            this.prepare(o);
        }
        this.fallback = o;
    }

    @Override
    public void setTimeout(long milliseconds, StatefulWebServiceManager.Callback<T> callback) {
        if (milliseconds < 0L) {
            throw new IllegalArgumentException();
        }
        this.timeoutMilliseconds = milliseconds;
        this.timeoutCallback = callback;
        this.haMap.getExpiredTask().cancel();
        if (this.timeoutMilliseconds > 0L) {
            this.startTimer();
            this.timer.schedule(this.haMap.newExpiredTask(), this.timeoutMilliseconds, this.timeoutMilliseconds);
        } else {
            this.stopTimer();
        }
    }

    @Override
    public void touch(T o) {
        Instance i = this.haMap.touch(o);
        if (i != null) {
            i.restartTimer();
        }
    }

    private synchronized void startTimer() {
        if (this.timer == null) {
            this.timer = new Timer("JAX-WS stateful web service timeout timer");
        }
    }

    private synchronized void stopTimer() {
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
    }

    private class HAMap {
        final Map<String, Instance> instances = new HashMap<String, Instance>();
        final Map<T, String> reverseInstances = new HashMap();
        final BackingStore<String, HAInstance> bs;
        TimerTask expiredTask;

        HAMap() {
            HighAvailabilityProvider.StoreType type = StatefulInstanceResolver.this.haEnabled ? HighAvailabilityProvider.StoreType.IN_MEMORY : HighAvailabilityProvider.StoreType.NOOP;
            this.bs = HighAvailabilityProvider.INSTANCE.createBackingStore(HighAvailabilityProvider.INSTANCE.getBackingStoreFactory(type), StatefulInstanceResolver.this.owner.getServiceName() + ":" + StatefulInstanceResolver.this.owner.getPortName() + ":STATEFUL_WEB_SERVICE", String.class, HAInstance.class);
            this.expiredTask = this.newExpiredTask();
        }

        TimerTask getExpiredTask() {
            return this.expiredTask;
        }

        private TimerTask newExpiredTask() {
            this.expiredTask = new TimerTask(){

                @Override
                public void run() {
                    HighAvailabilityProvider.removeExpired(HAMap.this.bs);
                }
            };
            return this.expiredTask;
        }

        synchronized String get(T t) {
            return this.reverseInstances.get(t);
        }

        synchronized Instance touch(T t) {
            Instance i;
            String id = this.get(t);
            if (id != null && (i = this.get(id)) != null) {
                this.put(id, i);
                return i;
            }
            return null;
        }

        synchronized Instance get(String id) {
            HAInstance hai;
            Instance i = this.instances.get(id);
            if (i == null && (hai = HighAvailabilityProvider.loadFrom(this.bs, id, null)) != null) {
                Object t = hai.getInstance(StatefulInstanceResolver.this.appCL);
                i = new Instance(t);
                this.instances.put(id, i);
                this.reverseInstances.put(t, id);
            }
            return i;
        }

        synchronized void put(String id, Instance newi) {
            boolean isNew;
            Instance oldi = this.instances.get(id);
            boolean bl = isNew = oldi == null;
            if (!isNew) {
                this.reverseInstances.remove(oldi.instance);
                newi.setTask(oldi.task);
            }
            this.instances.put(id, newi);
            this.reverseInstances.put(newi.instance, id);
            HAInstance hai = new HAInstance(newi.instance, StatefulInstanceResolver.this.timeoutMilliseconds);
            HighAvailabilityProvider.saveTo(this.bs, id, hai, isNew);
        }

        synchronized void put(T t) {
            String id = this.reverseInstances.get(t);
            if (id != null) {
                this.put(id, new Instance(t));
            }
        }

        synchronized void remove(String id) {
            Instance i = this.instances.get(id);
            if (i != null) {
                this.instances.remove(id);
                this.reverseInstances.remove(i.instance);
                HighAvailabilityProvider.removeFrom(this.bs, id);
            }
        }

        synchronized Instance remove(T t) {
            String id = this.reverseInstances.get(t);
            if (id != null) {
                this.reverseInstances.remove(t);
                Instance i = this.instances.remove(id);
                HighAvailabilityProvider.removeFrom(this.bs, id);
                return i;
            }
            return null;
        }

        synchronized void destroy() {
            this.instances.clear();
            this.reverseInstances.clear();
            HighAvailabilityProvider.destroy(this.bs);
        }

        Collection<Instance> values() {
            return this.instances.values();
        }
    }

    private final class Instance {
        @NotNull
        final T instance;
        volatile TimerTask task;

        public Instance(T instance) {
            this.instance = instance;
        }

        public synchronized void restartTimer() {
            this.cancel();
            if (StatefulInstanceResolver.this.timeoutMilliseconds == 0L) {
                return;
            }
            this.task = new TimerTask(){

                @Override
                public void run() {
                    try {
                        StatefulWebServiceManager.Callback cb = StatefulInstanceResolver.this.timeoutCallback;
                        if (cb != null) {
                            if (logger.isLoggable(Level.FINEST)) {
                                logger.log(Level.FINEST, "Invoking timeout callback for instance/timeouttask = [ {0} / {1} ]", new Object[]{Instance.this.instance, this});
                            }
                            cb.onTimeout(Instance.this.instance, StatefulInstanceResolver.this);
                            return;
                        }
                        StatefulInstanceResolver.this.unexport(Instance.this.instance);
                    }
                    catch (Throwable e) {
                        logger.log(Level.SEVERE, "time out handler failed", e);
                    }
                }
            };
            StatefulInstanceResolver.this.timer.schedule(this.task, StatefulInstanceResolver.this.timeoutMilliseconds);
        }

        public synchronized void cancel() {
            if (this.task != null) {
                boolean result = this.task.cancel();
                if (logger.isLoggable(Level.FINEST)) {
                    logger.log(Level.FINEST, "Timeout callback CANCELED for instance/timeouttask/cancel result = [ {0} / {1} / {2} ]", new Object[]{this.instance, this, result});
                }
            } else if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Timeout callback NOT CANCELED for instance = [ {0} ]; task is null ...", this.instance);
            }
            this.task = null;
        }

        public synchronized void setTask(TimerTask t) {
            this.task = t;
        }
    }

    private static final class HAInstance<T>
    implements Storeable {
        @NotNull
        transient T instance;
        private byte[] buf;
        private long lastAccess = 0L;
        private boolean isNew = false;
        private long version = -1L;
        private long maxIdleTime;

        public HAInstance() {
        }

        public HAInstance(T instance, long timeout) {
            this.instance = instance;
            this.lastAccess = System.currentTimeMillis();
            this.maxIdleTime = timeout;
        }

        public T getInstance(final ClassLoader cl) {
            if (this.instance == null) {
                try {
                    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(this.buf)){

                        @Override
                        protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                            Class<?> clazz = cl.loadClass(desc.getName());
                            if (clazz == null) {
                                clazz = super.resolveClass(desc);
                            }
                            return clazz;
                        }
                    };
                    this.instance = in.readObject();
                    in.close();
                }
                catch (Exception ioe) {
                    throw new WebServiceException((Throwable)ioe);
                }
            }
            return this.instance;
        }

        public long _storeable_getVersion() {
            return this.version;
        }

        public void _storeable_setVersion(long version) {
            this.version = version;
        }

        public long _storeable_getLastAccessTime() {
            return this.lastAccess;
        }

        public void _storeable_setLastAccessTime(long time) {
            this.lastAccess = time;
        }

        public long _storeable_getMaxIdleTime() {
            return this.maxIdleTime;
        }

        public void _storeable_setMaxIdleTime(long time) {
            this.maxIdleTime = time;
        }

        public String[] _storeable_getAttributeNames() {
            return new String[0];
        }

        public boolean[] _storeable_getDirtyStatus() {
            return new boolean[0];
        }

        public void _storeable_writeState(OutputStream os) throws IOException {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream boos = new ObjectOutputStream(bos);
            boos.writeObject(this.instance);
            boos.close();
            this.buf = bos.toByteArray();
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeLong(this.version);
            oos.writeLong(this.lastAccess);
            oos.writeLong(this.maxIdleTime);
            oos.writeBoolean(this.isNew);
            oos.writeInt(this.buf.length);
            oos.write(this.buf);
            oos.close();
        }

        public void _storeable_readState(InputStream is) throws IOException {
            ObjectInputStream ois = new ObjectInputStream(is);
            this.version = ois.readLong();
            this.lastAccess = ois.readLong();
            this.maxIdleTime = ois.readLong();
            this.isNew = ois.readBoolean();
            int len = ois.readInt();
            this.buf = new byte[len];
            ois.readFully(this.buf);
            ois.close();
        }
    }
}

