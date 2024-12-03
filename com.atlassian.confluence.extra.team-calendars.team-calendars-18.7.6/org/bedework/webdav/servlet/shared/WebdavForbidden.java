/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import javax.xml.namespace.QName;
import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavForbidden
extends WebdavException {
    public WebdavForbidden() {
        super(403);
    }

    public WebdavForbidden(String msg) {
        super(403, msg);
    }

    public WebdavForbidden(QName errorTag) {
        super(403, errorTag);
    }

    public WebdavForbidden(QName errorTag, String msg) {
        super(403, errorTag, msg);
    }
}

