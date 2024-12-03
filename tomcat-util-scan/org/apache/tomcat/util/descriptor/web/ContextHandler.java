/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.descriptor.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.util.descriptor.web.ResourceBase;

public class ContextHandler
extends ResourceBase {
    private static final long serialVersionUID = 1L;
    private String handlerclass = null;
    private final Map<String, String> soapHeaders = new HashMap<String, String>();
    private final List<String> soapRoles = new ArrayList<String>();
    private final List<String> portNames = new ArrayList<String>();

    public String getHandlerclass() {
        return this.handlerclass;
    }

    public void setHandlerclass(String handlerclass) {
        this.handlerclass = handlerclass;
    }

    public Iterator<String> getLocalparts() {
        return this.soapHeaders.keySet().iterator();
    }

    public String getNamespaceuri(String localpart) {
        return this.soapHeaders.get(localpart);
    }

    public void addSoapHeaders(String localpart, String namespaceuri) {
        this.soapHeaders.put(localpart, namespaceuri);
    }

    public void setProperty(String name, String value) {
        this.setProperty(name, (Object)value);
    }

    public String getSoapRole(int i) {
        return this.soapRoles.get(i);
    }

    public int getSoapRolesSize() {
        return this.soapRoles.size();
    }

    public void addSoapRole(String soapRole) {
        this.soapRoles.add(soapRole);
    }

    public String getPortName(int i) {
        return this.portNames.get(i);
    }

    public int getPortNamesSize() {
        return this.portNames.size();
    }

    public void addPortName(String portName) {
        this.portNames.add(portName);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ContextHandler[");
        sb.append("name=");
        sb.append(this.getName());
        if (this.handlerclass != null) {
            sb.append(", class=");
            sb.append(this.handlerclass);
        }
        if (this.soapHeaders != null) {
            sb.append(", soap-headers=");
            sb.append(this.soapHeaders);
        }
        if (this.getSoapRolesSize() > 0) {
            sb.append(", soap-roles=");
            sb.append(this.soapRoles);
        }
        if (this.getPortNamesSize() > 0) {
            sb.append(", port-name=");
            sb.append(this.portNames);
        }
        if (this.listProperties() != null) {
            sb.append(", init-param=");
            sb.append(this.listProperties());
        }
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.handlerclass == null ? 0 : this.handlerclass.hashCode());
        result = 31 * result + (this.portNames == null ? 0 : this.portNames.hashCode());
        result = 31 * result + (this.soapHeaders == null ? 0 : this.soapHeaders.hashCode());
        result = 31 * result + (this.soapRoles == null ? 0 : this.soapRoles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ContextHandler other = (ContextHandler)obj;
        if (this.handlerclass == null ? other.handlerclass != null : !this.handlerclass.equals(other.handlerclass)) {
            return false;
        }
        if (this.portNames == null ? other.portNames != null : !this.portNames.equals(other.portNames)) {
            return false;
        }
        if (this.soapHeaders == null ? other.soapHeaders != null : !this.soapHeaders.equals(other.soapHeaders)) {
            return false;
        }
        return !(this.soapRoles == null ? other.soapRoles != null : !this.soapRoles.equals(other.soapRoles));
    }
}

