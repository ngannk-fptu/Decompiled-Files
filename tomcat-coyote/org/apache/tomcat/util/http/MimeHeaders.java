/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.MessageBytes
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaderField;
import org.apache.tomcat.util.http.NamesEnumerator;
import org.apache.tomcat.util.http.ValuesEnumerator;
import org.apache.tomcat.util.res.StringManager;

public class MimeHeaders {
    public static final int DEFAULT_HEADER_SIZE = 8;
    private static final StringManager sm = StringManager.getManager((String)"org.apache.tomcat.util.http");
    private MimeHeaderField[] headers = new MimeHeaderField[8];
    private int count;
    private int limit = -1;

    public void setLimit(int limit) {
        this.limit = limit;
        if (limit > 0 && this.headers.length > limit && this.count < limit) {
            MimeHeaderField[] tmp = new MimeHeaderField[limit];
            System.arraycopy(this.headers, 0, tmp, 0, this.count);
            this.headers = tmp;
        }
    }

    public void recycle() {
        for (int i = 0; i < this.count; ++i) {
            this.headers[i].recycle();
        }
        this.count = 0;
    }

    @Deprecated
    public void clear() {
        this.recycle();
    }

    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.println("=== MimeHeaders ===");
        Enumeration<String> e = this.names();
        while (e.hasMoreElements()) {
            String n = e.nextElement();
            Enumeration<String> ev = this.values(n);
            while (ev.hasMoreElements()) {
                pw.print(n);
                pw.print(" = ");
                pw.println(ev.nextElement());
            }
        }
        return sw.toString();
    }

    public void duplicate(MimeHeaders source) throws IOException {
        for (int i = 0; i < source.size(); ++i) {
            MimeHeaderField mhf = this.createHeader();
            mhf.getName().duplicate(source.getName(i));
            mhf.getValue().duplicate(source.getValue(i));
        }
    }

    public int size() {
        return this.count;
    }

    public MessageBytes getName(int n) {
        return n >= 0 && n < this.count ? this.headers[n].getName() : null;
    }

    public MessageBytes getValue(int n) {
        return n >= 0 && n < this.count ? this.headers[n].getValue() : null;
    }

    public int findHeader(String name, int starting) {
        for (int i = starting; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) continue;
            return i;
        }
        return -1;
    }

    public Enumeration<String> names() {
        return new NamesEnumerator(this);
    }

    public Enumeration<String> values(String name) {
        return new ValuesEnumerator(this, name);
    }

    private MimeHeaderField createHeader() {
        MimeHeaderField mh;
        if (this.limit > -1 && this.count >= this.limit) {
            throw new IllegalStateException(sm.getString("headers.maxCountFail", new Object[]{this.limit}));
        }
        int len = this.headers.length;
        if (this.count >= len) {
            int newLength = this.count * 2;
            if (this.limit > 0 && newLength > this.limit) {
                newLength = this.limit;
            }
            MimeHeaderField[] tmp = new MimeHeaderField[newLength];
            System.arraycopy(this.headers, 0, tmp, 0, len);
            this.headers = tmp;
        }
        if ((mh = this.headers[this.count]) == null) {
            this.headers[this.count] = mh = new MimeHeaderField();
        }
        ++this.count;
        return mh;
    }

    public MessageBytes addValue(String name) {
        MimeHeaderField mh = this.createHeader();
        mh.getName().setString(name);
        return mh.getValue();
    }

    public MessageBytes addValue(byte[] b, int startN, int len) {
        MimeHeaderField mhf = this.createHeader();
        mhf.getName().setBytes(b, startN, len);
        return mhf.getValue();
    }

    public MessageBytes setValue(String name) {
        for (int i = 0; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) continue;
            for (int j = i + 1; j < this.count; ++j) {
                if (!this.headers[j].getName().equalsIgnoreCase(name)) continue;
                this.removeHeader(j--);
            }
            return this.headers[i].getValue();
        }
        MimeHeaderField mh = this.createHeader();
        mh.getName().setString(name);
        return mh.getValue();
    }

    public MessageBytes getValue(String name) {
        for (int i = 0; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) continue;
            return this.headers[i].getValue();
        }
        return null;
    }

    public MessageBytes getUniqueValue(String name) {
        MessageBytes result = null;
        for (int i = 0; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) continue;
            if (result == null) {
                result = this.headers[i].getValue();
                continue;
            }
            throw new IllegalArgumentException();
        }
        return result;
    }

    public String getHeader(String name) {
        MessageBytes mh = this.getValue(name);
        return mh != null ? mh.toString() : null;
    }

    public void removeHeader(String name) {
        for (int i = 0; i < this.count; ++i) {
            if (!this.headers[i].getName().equalsIgnoreCase(name)) continue;
            this.removeHeader(i--);
        }
    }

    public void removeHeader(int idx) {
        MimeHeaderField mh = this.headers[idx];
        mh.recycle();
        System.arraycopy(this.headers, idx + 1, this.headers, idx, this.count - idx - 1);
        this.headers[this.count - 1] = mh;
        --this.count;
    }
}

