/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavServerError
extends WebdavException {
    public WebdavServerError() {
        super(500);
    }

    public WebdavServerError(String msg) {
        super(500, msg);
    }
}

