/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import javax.xml.namespace.QName;

public class WebdavException
extends Throwable {
    int statusCode = -1;
    QName errorTag;

    public WebdavException(String s) {
        super(s);
        if (this.statusCode < 0) {
            this.statusCode = 500;
        }
    }

    public WebdavException(Throwable t) {
        super(t);
        if (this.statusCode < 0) {
            this.statusCode = 500;
        }
    }

    public WebdavException(int st) {
        this.statusCode = st;
    }

    public WebdavException(int st, String msg) {
        super(msg);
        this.statusCode = st;
    }

    public WebdavException(int st, QName errorTag) {
        this.statusCode = st;
        this.errorTag = errorTag;
    }

    public WebdavException(int st, QName errorTag, String msg) {
        super(msg);
        this.statusCode = st;
        this.errorTag = errorTag;
    }

    public void setStatusCode(int val) {
        this.statusCode = val;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public QName getErrorTag() {
        return this.errorTag;
    }
}

