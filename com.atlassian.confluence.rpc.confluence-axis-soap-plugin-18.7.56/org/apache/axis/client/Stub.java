/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;
import javax.xml.rpc.Service;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.Messages;

public abstract class Stub
implements javax.xml.rpc.Stub {
    protected Service service = null;
    protected boolean maintainSessionSet = false;
    protected boolean maintainSession = false;
    protected Properties cachedProperties = new Properties();
    protected String cachedUsername = null;
    protected String cachedPassword = null;
    protected URL cachedEndpoint = null;
    protected Integer cachedTimeout = null;
    protected QName cachedPortName = null;
    private Vector headers = new Vector();
    private Vector attachments = new Vector();
    private boolean firstCall = true;
    protected Call _call = null;

    protected boolean firstCall() {
        boolean ret = this.firstCall;
        this.firstCall = false;
        return ret;
    }

    public void _setProperty(String name, Object value) {
        if (name == null || value == null) {
            throw new JAXRPCException(Messages.getMessage(name == null ? "badProp03" : "badProp04"));
        }
        if (name.equals("javax.xml.rpc.security.auth.username")) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.cachedUsername = (String)value;
        } else if (name.equals("javax.xml.rpc.security.auth.password")) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            this.cachedPassword = (String)value;
        } else if (name.equals("javax.xml.rpc.service.endpoint.address")) {
            if (!(value instanceof String)) {
                throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.String", value.getClass().getName()}));
            }
            try {
                this.cachedEndpoint = new URL((String)value);
            }
            catch (MalformedURLException mue) {
                throw new JAXRPCException(mue.getMessage());
            }
        } else if (name.equals("javax.xml.rpc.session.maintain")) {
            if (!(value instanceof Boolean)) {
                throw new JAXRPCException(Messages.getMessage("badProp00", new String[]{name, "java.lang.Boolean", value.getClass().getName()}));
            }
            this.maintainSessionSet = true;
            this.maintainSession = (Boolean)value;
        } else {
            if (name.startsWith("java.") || name.startsWith("javax.")) {
                throw new JAXRPCException(Messages.getMessage("badProp05", name));
            }
            this.cachedProperties.put(name, value);
        }
    }

    public Object _getProperty(String name) {
        if (name == null) {
            throw new JAXRPCException(Messages.getMessage("badProp05", name));
        }
        if (name.equals("javax.xml.rpc.security.auth.username")) {
            return this.cachedUsername;
        }
        if (name.equals("javax.xml.rpc.security.auth.password")) {
            return this.cachedPassword;
        }
        if (name.equals("javax.xml.rpc.service.endpoint.address")) {
            return this.cachedEndpoint.toString();
        }
        if (name.equals("javax.xml.rpc.session.maintain")) {
            return this.maintainSessionSet ? (this.maintainSession ? Boolean.TRUE : Boolean.FALSE) : null;
        }
        if (name.startsWith("java.") || name.startsWith("javax.")) {
            throw new JAXRPCException(Messages.getMessage("badProp05", name));
        }
        return this.cachedProperties.get(name);
    }

    public Object removeProperty(String name) {
        return this.cachedProperties.remove(name);
    }

    public Iterator _getPropertyNames() {
        return this.cachedProperties.keySet().iterator();
    }

    public void setUsername(String username) {
        this.cachedUsername = username;
    }

    public String getUsername() {
        return this.cachedUsername;
    }

    public void setPassword(String password) {
        this.cachedPassword = password;
    }

    public String getPassword() {
        return this.cachedPassword;
    }

    public int getTimeout() {
        return this.cachedTimeout == null ? 0 : this.cachedTimeout;
    }

    public void setTimeout(int timeout) {
        this.cachedTimeout = new Integer(timeout);
    }

    public QName getPortName() {
        return this.cachedPortName;
    }

    public void setPortName(QName portName) {
        this.cachedPortName = portName;
    }

    public void setPortName(String portName) {
        this.setPortName(new QName(portName));
    }

    public void setMaintainSession(boolean session) {
        this.maintainSessionSet = true;
        this.maintainSession = session;
        this.cachedProperties.put("javax.xml.rpc.session.maintain", session ? Boolean.TRUE : Boolean.FALSE);
    }

    public void setHeader(String namespace, String partName, Object headerValue) {
        this.headers.add(new SOAPHeaderElement(namespace, partName, headerValue));
    }

    public void setHeader(SOAPHeaderElement header) {
        this.headers.add(header);
    }

    public void extractAttachments(Call call) {
        this.attachments.clear();
        if (call.getResponseMessage() != null) {
            Iterator iterator = call.getResponseMessage().getAttachments();
            while (iterator.hasNext()) {
                this.attachments.add(iterator.next());
            }
        }
    }

    public void addAttachment(Object handler) {
        this.attachments.add(handler);
    }

    public SOAPHeaderElement getHeader(String namespace, String partName) {
        for (int i = 0; i < this.headers.size(); ++i) {
            SOAPHeaderElement header = (SOAPHeaderElement)this.headers.get(i);
            if (!header.getNamespaceURI().equals(namespace) || !header.getName().equals(partName)) continue;
            return header;
        }
        return null;
    }

    public SOAPHeaderElement getResponseHeader(String namespace, String partName) {
        try {
            if (this._call == null) {
                return null;
            }
            return this._call.getResponseMessage().getSOAPEnvelope().getHeaderByName(namespace, partName);
        }
        catch (Exception e) {
            return null;
        }
    }

    public SOAPHeaderElement[] getHeaders() {
        Object[] array = new SOAPHeaderElement[this.headers.size()];
        this.headers.copyInto(array);
        return array;
    }

    public SOAPHeaderElement[] getResponseHeaders() {
        Object[] array = new SOAPHeaderElement[]{};
        try {
            if (this._call == null) {
                return array;
            }
            Vector h = this._call.getResponseMessage().getSOAPEnvelope().getHeaders();
            array = new SOAPHeaderElement[h.size()];
            h.copyInto(array);
            return array;
        }
        catch (Exception e) {
            return array;
        }
    }

    public Object[] getAttachments() {
        Object[] array = new Object[this.attachments.size()];
        this.attachments.copyInto(array);
        this.attachments.clear();
        return array;
    }

    public void clearHeaders() {
        this.headers.clear();
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    protected void setRequestHeaders(Call call) throws AxisFault {
        SOAPHeaderElement[] headers = this.getHeaders();
        for (int i = 0; i < headers.length; ++i) {
            call.addHeader(headers[i]);
        }
    }

    protected void setAttachments(Call call) throws AxisFault {
        Object[] attachments = this.getAttachments();
        for (int i = 0; i < attachments.length; ++i) {
            call.addAttachmentPart(attachments[i]);
        }
        this.clearAttachments();
    }

    public Service _getService() {
        return this.service;
    }

    public Call _createCall() throws ServiceException {
        this._call = (Call)this.service.createCall();
        return this._call;
    }

    public Call _getCall() {
        return this._call;
    }

    protected void getResponseHeaders(Call call) throws AxisFault {
    }
}

