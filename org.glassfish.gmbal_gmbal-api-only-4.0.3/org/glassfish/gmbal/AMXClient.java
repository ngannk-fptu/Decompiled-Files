/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.gmbal;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.Descriptor;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.ModelMBeanInfo;
import org.glassfish.gmbal.AMXMBeanInterface;
import org.glassfish.gmbal.GmbalException;

public class AMXClient
implements AMXMBeanInterface {
    public static final ObjectName NULL_OBJECTNAME = AMXClient.makeObjectName("null:type=Null,name=Null");
    private MBeanServerConnection server;
    private ObjectName oname;

    private static ObjectName makeObjectName(String str) {
        try {
            return new ObjectName(str);
        }
        catch (MalformedObjectNameException ex) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AMXClient)) {
            return false;
        }
        AMXClient other = (AMXClient)obj;
        return this.oname.equals(other.oname);
    }

    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.oname != null ? this.oname.hashCode() : 0);
        return hash;
    }

    public String toString() {
        return "AMXClient[" + this.oname + "]";
    }

    private <T> T fetchAttribute(String name, Class<T> type) {
        try {
            Object obj = this.server.getAttribute(this.oname, name);
            if (NULL_OBJECTNAME.equals(obj)) {
                return null;
            }
            return type.cast(obj);
        }
        catch (JMException exc) {
            throw new GmbalException("Exception in fetchAttribute", exc);
        }
        catch (IOException exc) {
            throw new GmbalException("Exception in fetchAttribute", exc);
        }
    }

    public AMXClient(MBeanServerConnection server, ObjectName oname) {
        this.server = server;
        this.oname = oname;
    }

    private AMXClient makeAMX(ObjectName on) {
        if (on == null) {
            return null;
        }
        return new AMXClient(this.server, on);
    }

    @Override
    public String getName() {
        return this.fetchAttribute("Name", String.class);
    }

    @Override
    public Map<String, ?> getMeta() {
        try {
            ModelMBeanInfo mbi = (ModelMBeanInfo)((Object)this.server.getMBeanInfo(this.oname));
            Descriptor desc = mbi.getMBeanDescriptor();
            HashMap<String, Object> result = new HashMap<String, Object>();
            for (String str : desc.getFieldNames()) {
                result.put(str, desc.getFieldValue(str));
            }
            return result;
        }
        catch (MBeanException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (RuntimeOperationsException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (IntrospectionException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in getMeta", ex);
        }
    }

    @Override
    public AMXClient getParent() {
        ObjectName res = this.fetchAttribute("Parent", ObjectName.class);
        return this.makeAMX(res);
    }

    public AMXClient[] getChildren() {
        ObjectName[] onames = this.fetchAttribute("Children", ObjectName[].class);
        return this.makeAMXArray(onames);
    }

    private AMXClient[] makeAMXArray(ObjectName[] onames) {
        AMXClient[] result = new AMXClient[onames.length];
        int ctr = 0;
        for (ObjectName on : onames) {
            result[ctr++] = this.makeAMX(on);
        }
        return result;
    }

    public Object getAttribute(String attribute) {
        try {
            return this.server.getAttribute(this.oname, attribute);
        }
        catch (MBeanException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
        catch (AttributeNotFoundException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in getAttribute", ex);
        }
    }

    public void setAttribute(String name, Object value) {
        Attribute attr = new Attribute(name, value);
        this.setAttribute(attr);
    }

    public void setAttribute(Attribute attribute) {
        try {
            this.server.setAttribute(this.oname, attribute);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (AttributeNotFoundException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (InvalidAttributeValueException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (MBeanException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in setAttribute", ex);
        }
    }

    public AttributeList getAttributes(String[] attributes) {
        try {
            return this.server.getAttributes(this.oname, attributes);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in getAttributes", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in getAttributes", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in getAttributes", ex);
        }
    }

    public AttributeList setAttributes(AttributeList attributes) {
        try {
            return this.server.setAttributes(this.oname, attributes);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in setAttributes", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in setAttributes", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in setAttributes", ex);
        }
    }

    public Object invoke(String actionName, Object[] params, String[] signature) throws MBeanException, ReflectionException {
        try {
            return this.server.invoke(this.oname, actionName, params, signature);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
    }

    public MBeanInfo getMBeanInfo() {
        try {
            return this.server.getMBeanInfo(this.oname);
        }
        catch (InstanceNotFoundException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (IntrospectionException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (ReflectionException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
        catch (IOException ex) {
            throw new GmbalException("Exception in invoke", ex);
        }
    }

    public ObjectName objectName() {
        return this.oname;
    }
}

