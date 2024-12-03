/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import javax.xml.soap.MimeHeader;

public class MimeHeaders
extends javax.xml.soap.MimeHeaders
implements Externalizable {
    public MimeHeaders() {
    }

    public MimeHeaders(javax.xml.soap.MimeHeaders h) {
        Iterator iterator = h.getAllHeaders();
        while (iterator.hasNext()) {
            MimeHeader hdr = (MimeHeader)iterator.next();
            this.addHeader(hdr.getName(), hdr.getValue());
        }
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int size = in.readInt();
        for (int i = 0; i < size; ++i) {
            Object key = in.readObject();
            Object value = in.readObject();
            this.addHeader((String)key, (String)value);
        }
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.getHeaderSize());
        Iterator iterator = this.getAllHeaders();
        while (iterator.hasNext()) {
            MimeHeader hdr = (MimeHeader)iterator.next();
            out.writeObject(hdr.getName());
            out.writeObject(hdr.getValue());
        }
    }

    private int getHeaderSize() {
        int size = 0;
        Iterator iterator = this.getAllHeaders();
        while (iterator.hasNext()) {
            iterator.next();
            ++size;
        }
        return size;
    }
}

