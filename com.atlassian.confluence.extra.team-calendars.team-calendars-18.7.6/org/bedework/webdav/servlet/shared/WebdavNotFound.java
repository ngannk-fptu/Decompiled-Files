/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavNotFound
extends WebdavException {
    public WebdavNotFound() {
        super(404);
    }

    public WebdavNotFound(String msg) {
        super(404, msg);
    }

    public WebdavNotFound(QName errorTag) {
        super(404, errorTag);
    }

    public WebdavNotFound(QName errorTag, String msg) {
        super(404, errorTag, msg);
    }
}

