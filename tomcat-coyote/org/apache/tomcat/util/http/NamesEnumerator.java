/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http;

import java.util.Enumeration;
import org.apache.tomcat.util.http.MimeHeaders;

class NamesEnumerator
implements Enumeration<String> {
    private int pos;
    private final int size;
    private String next;
    private final MimeHeaders headers;

    NamesEnumerator(MimeHeaders headers) {
        this.headers = headers;
        this.pos = 0;
        this.size = headers.size();
        this.findNext();
    }

    private void findNext() {
        this.next = null;
        while (this.pos < this.size) {
            this.next = this.headers.getName(this.pos).toString();
            for (int j = 0; j < this.pos; ++j) {
                if (!this.headers.getName(j).equalsIgnoreCase(this.next)) continue;
                this.next = null;
                break;
            }
            if (this.next != null) break;
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
        String current = this.next;
        this.findNext();
        return current;
    }
}

