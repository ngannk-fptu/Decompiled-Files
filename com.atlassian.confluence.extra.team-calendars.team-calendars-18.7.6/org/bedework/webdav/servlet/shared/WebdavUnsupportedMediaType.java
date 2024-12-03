/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.WebdavException;

public class WebdavUnsupportedMediaType
extends WebdavException {
    public WebdavUnsupportedMediaType() {
        super(415);
    }

    public WebdavUnsupportedMediaType(String msg) {
        super(415, msg);
    }
}

