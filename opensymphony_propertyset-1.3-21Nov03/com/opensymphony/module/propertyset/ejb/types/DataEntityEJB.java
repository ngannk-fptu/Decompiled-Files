/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  com.opensymphony.util.XMLUtils
 *  javax.ejb.CreateException
 *  javax.ejb.EntityBean
 *  javax.ejb.EntityContext
 *  javax.ejb.RemoveException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.util.Data;
import com.opensymphony.util.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Properties;
import javax.ejb.CreateException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;
import org.w3c.dom.Document;

public abstract class DataEntityEJB
implements EntityBean {
    private static final byte[] NULL_DATA = "hello world".getBytes();
    private EntityContext context;

    public abstract void setBytes(byte[] var1);

    public abstract byte[] getBytes();

    public abstract void setId(Long var1);

    public abstract Long getId();

    public void setEntityContext(EntityContext context) {
        this.context = context;
    }

    public void setValue(int type, Serializable value) {
        if (value == null) {
            this.setBytes(NULL_DATA);
            return;
        }
        try {
            switch (type) {
                case 6: {
                    this.setBytes(((String)((Object)value)).getBytes());
                    break;
                }
                case 8: {
                    if (!(value instanceof Serializable)) {
                        throw new IllegalPropertyException("Object not serializable.");
                    }
                    this.setBytes(this.writeObject(value));
                    break;
                }
                case 9: {
                    this.setBytes(this.writeXML((Document)((Object)value)));
                    break;
                }
                case 10: {
                    this.setBytes(((Data)value).getBytes());
                    break;
                }
                case 11: {
                    this.setBytes(this.writeProperties((Properties)value));
                    break;
                }
                default: {
                    throw new PropertyImplementationException("Cannot store this type of property.");
                }
            }
            if (this.getBytes().length == 0) {
                this.setBytes(NULL_DATA);
            }
        }
        catch (ClassCastException ce) {
            throw new IllegalPropertyException("Cannot cast value to appropriate type for persistence.");
        }
    }

    public Serializable getValue(int type) {
        byte[] value = this.getBytes();
        switch (type) {
            case 6: {
                return new String(value);
            }
            case 8: {
                return (Serializable)this.readObject(value);
            }
            case 9: {
                return (Serializable)((Object)this.readXML(value));
            }
            case 10: {
                return new Data(value);
            }
            case 11: {
                return this.readProperties(value);
            }
        }
        throw new PropertyImplementationException("Cannot retrieve this type of property.");
    }

    public void ejbActivate() {
    }

    public Long ejbCreate(int type, long id) throws CreateException {
        this.setId(new Long(id));
        this.setValue(type, null);
        return null;
    }

    public void ejbLoad() {
    }

    public void ejbPassivate() {
    }

    public void ejbPostCreate(int type, long id) throws CreateException {
    }

    public void ejbRemove() throws RemoveException {
    }

    public void ejbStore() {
    }

    public void unsetEntityContext() {
        this.context = null;
    }

    protected int[] allowedTypes() {
        return new int[]{6, 8, 9, 10, 11};
    }

    private Object readObject(byte[] data) {
        try {
            ByteArrayInputStream bytes = new ByteArrayInputStream(data);
            ObjectInputStream stream = new ObjectInputStream(bytes);
            Object result = stream.readObject();
            stream.close();
            return result;
        }
        catch (IOException e) {
            throw new PropertyImplementationException("Cannot deserialize Object", e);
        }
        catch (ClassNotFoundException e) {
            throw new PropertyImplementationException("Class not found for Object", e);
        }
    }

    private Properties readProperties(byte[] data) {
        try {
            ByteArrayInputStream bytes = new ByteArrayInputStream(data);
            Properties result = new Properties();
            result.load(bytes);
            return result;
        }
        catch (Exception e) {
            throw new PropertyImplementationException("Cannot load Properties.", e);
        }
    }

    private Document readXML(byte[] data) {
        try {
            ByteArrayInputStream bytes = new ByteArrayInputStream(data);
            return XMLUtils.parse((InputStream)bytes);
        }
        catch (Exception e) {
            throw new PropertyImplementationException("Cannot parse XML data.", e);
        }
    }

    private byte[] writeObject(Object o) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream stream = new ObjectOutputStream(bytes);
            stream.writeObject(o);
            stream.close();
            bytes.flush();
            return bytes.toByteArray();
        }
        catch (IOException e) {
            throw new PropertyImplementationException("Cannot serialize Object", e);
        }
    }

    private byte[] writeProperties(Properties p) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            p.store(bytes, null);
            bytes.flush();
            return bytes.toByteArray();
        }
        catch (IOException e) {
            throw new PropertyImplementationException("Cannot store Properties.", e);
        }
    }

    private byte[] writeXML(Document doc) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            XMLUtils.print((Document)doc, (OutputStream)bytes);
            bytes.flush();
            return bytes.toByteArray();
        }
        catch (IOException e) {
            throw new PropertyImplementationException("Cannot serialize XML", e);
        }
    }
}

