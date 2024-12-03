/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.MessageBytes
 */
package org.apache.tomcat.util.http;

import java.util.Enumeration;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.MimeHeaders;

class ValuesEnumerator
implements Enumeration<String> {
    private int pos;
    private final int size;
    private MessageBytes next;
    private final MimeHeaders headers;
    private final String name;

    ValuesEnumerator(MimeHeaders headers, String name) {
        this.name = name;
        this.headers = headers;
        this.pos = 0;
        this.size = headers.size();
        this.findNext();
    }

    private void findNext() {
        this.next = null;
        while (this.pos < this.size) {
            MessageBytes n1 = this.headers.getName(this.pos);
            if (n1.equalsIgnoreCase(this.name)) {
                this.next = this.headers.getValue(this.pos);
                break;
            }
            ++this.pos;
        }
        ++this.pos;
    }

    @Override
    public boolean hasMoreElements() {
        return this.next != null;
    }

    @Override
    public String nextElement() {
        MessageBytes current = this.next;
        this.findNext();
        return current.toString();
    }
}

