/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavBadRequest
extends WebdavException {
    public WebdavBadRequest() {
        super(400);
    }

    public WebdavBadRequest(String msg) {
        super(400, msg);
    }

    public WebdavBadRequest(QName errorTag) {
        super(400, errorTag);
    }

    public WebdavBadRequest(QName errorTag, String msg) {
        super(400, errorTag, msg);
    }
}

