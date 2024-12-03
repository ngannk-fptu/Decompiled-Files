/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavUnauthorized
extends WebdavException {
    public WebdavUnauthorized() {
        super(401);
    }

    public WebdavUnauthorized(String msg) {
        super(401, msg);
    }
}

