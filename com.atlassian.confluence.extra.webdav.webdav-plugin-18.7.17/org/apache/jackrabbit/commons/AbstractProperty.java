/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.ItemVisitor;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.commons.AbstractItem;

public abstract class AbstractProperty
extends AbstractItem
implements Item,
Property {
    @Override
    public void accept(ItemVisitor visitor) throws RepositoryException {
        visitor.visit(this);
    }

    @Override
    public String getPath() throws RepositoryException {
        StringBuffer buffer = new StringBuffer(this.getParent().getPath());
        if (buffer.length() > 1) {
            buffer.append('/');
        }
        buffer.append(this.getName());
        return buffer.toString();
    }

    @Override
    public boolean isNode() {
        return false;
    }

    @Override
    public void remove() throws RepositoryException {
        this.getParent().setProperty(this.getName(), (Value)null);
    }

    @Override
    public boolean getBoolean() throws RepositoryException {
        return this.getValue().getBoolean();
    }

    @Override
    public Calendar getDate() throws RepositoryException {
        return this.getValue().getDate();
    }

    @Override
    public double getDouble() throws RepositoryException {
        return this.getValue().getDouble();
    }

    @Override
    public long getLength() throws RepositoryException {
        return this.getLength(this.getValue());
    }

    @Override
    public long[] getLengths() throws RepositoryException {
        Value[] values = this.getValues();
        long[] lengths = new long[values.length];
        for (int i = 0; i < values.length; ++i) {
            lengths[i] = this.getLength(values[i]);
        }
        return lengths;
    }

    @Override
    public long getLong() throws RepositoryException {
        return this.getValue().getLong();
    }

    @Override
    public Node getNode() throws ValueFormatException, RepositoryException {
        String value = this.getString();
        switch (this.getType()) {
            case 9: 
            case 10: {
                return this.getSession().getNodeByIdentifier(value);
            }
            case 8: {
                try {
                    return value.startsWith("/") ? this.getSession().getNode(value) : this.getParent().getNode(value);
                }
                catch (PathNotFoundException e) {
                    throw new ItemNotFoundException(value);
                }
            }
            case 7: {
                try {
                    return this.getParent().getNode(value);
                }
                catch (PathNotFoundException e) {
                    throw new ItemNotFoundException(value);
                }
            }
            case 1: {
                try {
                    Value refValue = this.getSession().getValueFactory().createValue(value, 9);
                    return this.getSession().getNodeByIdentifier(refValue.getString());
                }
                catch (ItemNotFoundException e) {
                    throw e;
                }
                catch (RepositoryException e) {
                    Value pathValue = this.getSession().getValueFactory().createValue(value, 8);
                    try {
                        return value.startsWith("/") ? this.getSession().getNode(pathValue.getString()) : this.getParent().getNode(pathValue.getString());
                    }
                    catch (PathNotFoundException e1) {
                        throw new ItemNotFoundException(pathValue.getString());
                    }
                }
            }
        }
        throw new ValueFormatException("Property value cannot be converted to a PATH, REFERENCE or WEAKREFERENCE: " + value);
    }

    @Override
    public Property getProperty() throws RepositoryException {
        String value = this.getString();
        switch (this.getType()) {
            case 8: {
                try {
                    return value.startsWith("/") ? this.getSession().getProperty(value) : this.getParent().getProperty(value);
                }
                catch (PathNotFoundException e) {
                    throw new ItemNotFoundException(value);
                }
            }
            case 7: {
                try {
                    return this.getParent().getProperty(value);
                }
                catch (PathNotFoundException e) {
                    throw new ItemNotFoundException(value);
                }
            }
        }
        try {
            String path = this.getSession().getValueFactory().createValue(value, 8).getString();
            return path.startsWith("/") ? this.getSession().getProperty(path) : this.getParent().getProperty(path);
        }
        catch (PathNotFoundException e) {
            throw new ItemNotFoundException(value);
        }
    }

    @Override
    public InputStream getStream() throws RepositoryException {
        return this.getValue().getStream();
    }

    @Override
    public String getString() throws RepositoryException {
        return this.getValue().getString();
    }

    @Override
    public int getType() throws RepositoryException {
        return this.getValue().getType();
    }

    @Override
    public void setValue(Value value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(Value[] values) throws RepositoryException {
        this.getParent().setProperty(this.getName(), values);
    }

    @Override
    public void setValue(String value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(String[] values) throws RepositoryException {
        this.getParent().setProperty(this.getName(), values);
    }

    @Override
    public void setValue(InputStream value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(long value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(double value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(Calendar value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(boolean value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    @Override
    public void setValue(Node value) throws RepositoryException {
        this.getParent().setProperty(this.getName(), value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long getLength(Value value) throws RepositoryException {
        long l;
        if (value.getType() != 2) {
            return value.getString().length();
        }
        InputStream stream = value.getStream();
        try {
            long length = 0L;
            byte[] buffer = new byte[4096];
            int n = stream.read(buffer);
            while (n != -1) {
                length += (long)n;
                n = stream.read(buffer);
            }
            l = length;
        }
        catch (Throwable throwable) {
            try {
                stream.close();
                throw throwable;
            }
            catch (IOException e) {
                throw new RepositoryException("Failed to count the length of a binary value", e);
            }
        }
        stream.close();
        return l;
    }
}

