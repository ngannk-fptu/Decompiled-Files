/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.Data
 *  com.opensymphony.util.XMLUtils
 */
package com.opensymphony.module.propertyset.ofbiz;

import com.opensymphony.module.propertyset.IllegalPropertyException;
import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertyImplementationException;
import com.opensymphony.module.propertyset.ofbiz.PropertyHandler;
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
import org.w3c.dom.Document;

public class DataPropertyHandler
implements PropertyHandler {
    private static final byte[] NULL_DATA = "hello world".getBytes();

    public Object processGet(int type, Object input) throws PropertyException {
        byte[] value = (byte[])input;
        switch (type) {
            case 6: {
                return new String(value);
            }
            case 8: {
                return this.readObject(value);
            }
            case 9: {
                return this.readXML(value);
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

    public Object processSet(int type, Object input) throws PropertyException {
        if (input == null) {
            return NULL_DATA;
        }
        try {
            switch (type) {
                case 6: {
                    return ((String)input).getBytes();
                }
                case 8: {
                    if (!(input instanceof Serializable)) {
                        throw new IllegalPropertyException("Object not serializable.");
                    }
                    return this.writeObject(input);
                }
                case 9: {
                    return this.writeXML((Document)input);
                }
                case 10: {
                    return ((Data)input).getBytes();
                }
                case 11: {
                    return this.writeProperties((Properties)input);
                }
            }
            throw new PropertyImplementationException("Cannot store this type of property.");
        }
        catch (ClassCastException ce) {
            throw new IllegalPropertyException("Cannot cast value to appropriate type for persistence.");
        }
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

