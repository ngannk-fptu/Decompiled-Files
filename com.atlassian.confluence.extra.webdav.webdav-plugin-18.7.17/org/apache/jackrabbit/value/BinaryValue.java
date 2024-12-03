/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import javax.jcr.Binary;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.value.BaseValue;
import org.apache.jackrabbit.value.BinaryImpl;

public class BinaryValue
extends BaseValue {
    public static final int TYPE = 2;
    private Binary bin = null;
    private String text = null;

    public BinaryValue(String text) {
        super(2);
        this.text = text;
    }

    public BinaryValue(Binary bin) {
        super(2);
        this.bin = bin;
    }

    public BinaryValue(InputStream stream) {
        super(2);
        try {
            this.bin = new BinaryImpl(stream);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("specified stream cannot be read", e);
        }
    }

    public BinaryValue(byte[] data) {
        super(2);
        this.bin = new BinaryImpl(data);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof BinaryValue) {
            BinaryValue other = (BinaryValue)obj;
            if (this.text == other.text && this.stream == other.stream && this.bin == other.bin) {
                return true;
            }
            return this.text != null && this.text.equals(other.text) || this.bin != null && this.bin.equals(other.bin);
        }
        return false;
    }

    public int hashCode() {
        return 0;
    }

    @Override
    public String getInternalString() throws ValueFormatException, RepositoryException {
        if (this.text == null) {
            try {
                byte[] bytes = new byte[(int)this.bin.getSize()];
                this.bin.read(bytes, 0L);
                this.text = new String(bytes, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RepositoryException("UTF-8 not supported on this platform", e);
            }
            catch (IOException e) {
                throw new RepositoryException("failed to retrieve binary data", e);
            }
        }
        return this.text;
    }

    @Override
    public InputStream getStream() throws IllegalStateException, RepositoryException {
        if (this.stream == null) {
            if (this.bin != null) {
                this.stream = this.bin.getStream();
            } else {
                try {
                    this.stream = new ByteArrayInputStream(this.text.getBytes("UTF-8"));
                }
                catch (UnsupportedEncodingException e) {
                    throw new RepositoryException("UTF-8 not supported on this platform", e);
                }
            }
        }
        return this.stream;
    }

    @Override
    public Binary getBinary() throws ValueFormatException, IllegalStateException, RepositoryException {
        if (this.bin == null) {
            try {
                this.bin = new BinaryImpl(new ByteArrayInputStream(this.text.getBytes("UTF-8")));
            }
            catch (UnsupportedEncodingException e) {
                throw new RepositoryException("UTF-8 not supported on this platform", e);
            }
            catch (IOException e) {
                throw new RepositoryException("failed to retrieve binary data", e);
            }
        }
        return this.bin;
    }
}

