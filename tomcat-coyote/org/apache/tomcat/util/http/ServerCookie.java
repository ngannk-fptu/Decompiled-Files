/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.MessageBytes
 */
package org.apache.tomcat.util.http;

import java.io.Serializable;
import org.apache.tomcat.util.buf.MessageBytes;

public class ServerCookie
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final MessageBytes name = MessageBytes.newInstance();
    private final MessageBytes value = MessageBytes.newInstance();
    private final MessageBytes path = MessageBytes.newInstance();
    private final MessageBytes domain = MessageBytes.newInstance();
    private final MessageBytes comment = MessageBytes.newInstance();
    private int version = 0;

    public void recycle() {
        this.name.recycle();
        this.value.recycle();
        this.comment.recycle();
        this.path.recycle();
        this.domain.recycle();
        this.version = 0;
    }

    public MessageBytes getComment() {
        return this.comment;
    }

    public MessageBytes getDomain() {
        return this.domain;
    }

    public MessageBytes getPath() {
        return this.path;
    }

    public MessageBytes getName() {
        return this.name;
    }

    public MessageBytes getValue() {
        return this.value;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(int v) {
        this.version = v;
    }

    public String toString() {
        return "Cookie " + this.getName() + "=" + this.getValue() + " ; " + this.getVersion() + " " + this.getPath() + " " + this.getDomain();
    }
}

