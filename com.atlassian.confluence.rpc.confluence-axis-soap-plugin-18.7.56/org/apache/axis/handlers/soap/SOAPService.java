/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.handlers.soap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.xml.rpc.soap.SOAPFaultException;
import org.apache.axis.AxisEngine;
import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.SimpleTargetedChain;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.constants.Style;
import org.apache.axis.constants.Use;
import org.apache.axis.description.JavaServiceDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.TypeMappingRegistry;
import org.apache.axis.handlers.HandlerChainImpl;
import org.apache.axis.handlers.HandlerInfoChainFactory;
import org.apache.axis.handlers.soap.MustUnderstandChecker;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.providers.BasicProvider;
import org.apache.axis.session.Session;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.LockableHashtable;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.logging.Log;
import org.w3c.dom.Document;

public class SOAPService
extends SimpleTargetedChain {
    private static Log log = LogFactory.getLog((class$org$apache$axis$handlers$soap$SOAPService == null ? (class$org$apache$axis$handlers$soap$SOAPService = SOAPService.class$("org.apache.axis.handlers.soap.SOAPService")) : class$org$apache$axis$handlers$soap$SOAPService).getName());
    private Vector validTransports = null;
    private boolean highFidelityRecording = true;
    private int sendType = 1;
    private ServiceDesc serviceDescription = new JavaServiceDesc();
    private AxisEngine engine;
    public Map serviceObjects = new HashMap();
    public int nextObjectID = 1;
    private static Hashtable sessions = new Hashtable();
    private boolean isRunning = true;
    ArrayList actors = new ArrayList();
    static /* synthetic */ Class class$org$apache$axis$handlers$soap$SOAPService;

    public void addSession(Session session) {
        Vector<Session> v = (Vector<Session>)sessions.get(this.getName());
        if (v == null) {
            v = new Vector<Session>();
            sessions.put(this.getName(), v);
        }
        if (!v.contains(session)) {
            v.add(session);
        }
    }

    public void clearSessions() {
        Vector v = (Vector)sessions.get(this.getName());
        if (v == null) {
            return;
        }
        Iterator iter = v.iterator();
        while (iter.hasNext()) {
            Session session = (Session)iter.next();
            session.remove(this.getName());
        }
    }

    public ArrayList getServiceActors() {
        return this.actors;
    }

    public ArrayList getActors() {
        ArrayList acts = (ArrayList)this.actors.clone();
        if (this.engine != null) {
            acts.addAll(this.engine.getActorURIs());
        }
        return acts;
    }

    public List getRoles() {
        return this.getActors();
    }

    public void setRoles(List roles) {
        this.actors = new ArrayList(roles);
    }

    public SOAPService() {
        this.setOptionsLockable(true);
        this.initHashtable();
        this.actors.add("");
    }

    public SOAPService(Handler reqHandler, Handler pivHandler, Handler respHandler) {
        this();
        this.init(reqHandler, new MustUnderstandChecker(this), pivHandler, null, respHandler);
    }

    public TypeMappingRegistry getTypeMappingRegistry() {
        return this.serviceDescription.getTypeMappingRegistry();
    }

    public SOAPService(Handler serviceHandler) {
        this();
        this.init(null, new MustUnderstandChecker(this), serviceHandler, null, null);
    }

    public void setEngine(AxisEngine engine) {
        if (engine == null) {
            throw new IllegalArgumentException(Messages.getMessage("nullEngine"));
        }
        this.engine = engine;
        ((LockableHashtable)this.options).setParent(engine.getOptions());
        TypeMappingRegistry tmr = engine.getTypeMappingRegistry();
        this.getTypeMappingRegistry().delegate(tmr);
    }

    public AxisEngine getEngine() {
        return this.engine;
    }

    public boolean availableFromTransport(String transportName) {
        if (this.validTransports != null) {
            for (int i = 0; i < this.validTransports.size(); ++i) {
                if (!this.validTransports.elementAt(i).equals(transportName)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public Style getStyle() {
        return this.serviceDescription.getStyle();
    }

    public void setStyle(Style style) {
        this.serviceDescription.setStyle(style);
    }

    public Use getUse() {
        return this.serviceDescription.getUse();
    }

    public void setUse(Use style) {
        this.serviceDescription.setUse(style);
    }

    public ServiceDesc getServiceDescription() {
        return this.serviceDescription;
    }

    public synchronized ServiceDesc getInitializedServiceDesc(MessageContext msgContext) throws AxisFault {
        if (!this.serviceDescription.isInitialized() && this.pivotHandler instanceof BasicProvider) {
            ((BasicProvider)this.pivotHandler).initServiceDesc(this, msgContext);
        }
        return this.serviceDescription;
    }

    public void setServiceDescription(ServiceDesc serviceDescription) {
        if (serviceDescription == null) {
            return;
        }
        this.serviceDescription = serviceDescription;
    }

    public void setPropertyParent(Hashtable parent) {
        if (this.options == null) {
            this.options = new LockableHashtable();
        }
        ((LockableHashtable)this.options).setParent(parent);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public void generateWSDL(MessageContext msgContext) throws AxisFault {
        if (this.serviceDescription == null || this.serviceDescription.getWSDLFile() == null) {
            super.generateWSDL(msgContext);
            return;
        }
        InputStream instream = null;
        try {
            try {
                String path;
                String filename = this.serviceDescription.getWSDLFile();
                File file = new File(filename);
                if (file.exists()) {
                    instream = new FileInputStream(filename);
                } else if (msgContext.getStrProp("home.dir") != null && (file = new File(path = msgContext.getStrProp("home.dir") + '/' + filename)).exists()) {
                    instream = new FileInputStream(path);
                }
                if (instream == null && (instream = ClassUtils.getResourceAsStream(this.getClass(), filename)) == null) {
                    String errorText = Messages.getMessage("wsdlFileMissing", filename);
                    throw new AxisFault(errorText);
                }
                Document doc = XMLUtils.newDocument(instream);
                msgContext.setProperty("WSDL", doc);
            }
            catch (Exception e) {
                throw AxisFault.makeFault(e);
            }
            Object var7_7 = null;
            if (instream == null) return;
        }
        catch (Throwable throwable) {
            Object var7_8 = null;
            if (instream == null) throw throwable;
            try {
                instream.close();
                throw throwable;
            }
            catch (IOException e) {
                // empty catch block
            }
            throw throwable;
        }
        try {}
        catch (IOException e) {}
        instream.close();
        return;
    }

    public void start() {
        this.isRunning = true;
    }

    public void stop() {
        this.isRunning = false;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public void enableTransport(String transportName) {
        if (log.isDebugEnabled()) {
            log.debug((Object)Messages.getMessage("enableTransport00", "" + this, transportName));
        }
        if (this.validTransports == null) {
            this.validTransports = new Vector();
        }
        this.validTransports.addElement(transportName);
    }

    public void disableTransport(String transportName) {
        if (this.validTransports != null) {
            this.validTransports.removeElement(transportName);
        }
    }

    public boolean needsHighFidelityRecording() {
        return this.highFidelityRecording;
    }

    public void setHighFidelityRecording(boolean highFidelityRecording) {
        this.highFidelityRecording = highFidelityRecording;
    }

    public int getSendType() {
        return this.sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public void invoke(MessageContext msgContext) throws AxisFault {
        HandlerInfoChainFactory handlerFactory = (HandlerInfoChainFactory)this.getOption("handlerInfoChain");
        HandlerChainImpl handlerImpl = null;
        if (handlerFactory != null) {
            handlerImpl = (HandlerChainImpl)handlerFactory.createHandlerChain();
        }
        boolean result = true;
        try {
            if (handlerImpl != null) {
                try {
                    result = handlerImpl.handleRequest(msgContext);
                }
                catch (SOAPFaultException e) {
                    msgContext.setPastPivot(true);
                    handlerImpl.handleFault(msgContext);
                    if (handlerImpl != null) {
                        handlerImpl.destroy();
                    }
                    return;
                }
            }
            if (result) {
                try {
                    super.invoke(msgContext);
                }
                catch (AxisFault e) {
                    msgContext.setPastPivot(true);
                    if (handlerImpl != null) {
                        handlerImpl.handleFault(msgContext);
                    }
                    throw e;
                }
            } else {
                msgContext.setPastPivot(true);
            }
            if (handlerImpl != null) {
                handlerImpl.handleResponse(msgContext);
            }
        }
        catch (SOAPFaultException e) {
            msgContext.setPastPivot(true);
            throw AxisFault.makeFault(e);
        }
        catch (RuntimeException e) {
            SOAPFault fault = new SOAPFault(new AxisFault("Server", "Server Error", null, null));
            SOAPEnvelope env = new SOAPEnvelope();
            env.addBodyElement(fault);
            Message message = new Message(env);
            message.setMessageType("response");
            msgContext.setResponseMessage(message);
            throw AxisFault.makeFault(e);
        }
        finally {
            if (handlerImpl != null) {
                handlerImpl.destroy();
            }
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

