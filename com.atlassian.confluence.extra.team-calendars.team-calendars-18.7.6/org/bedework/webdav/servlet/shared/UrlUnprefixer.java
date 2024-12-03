/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.shared;

import org.bedework.webdav.servlet.shared.WebdavException;

public interface UrlUnprefixer {
    public String unprefix(String var1) throws WebdavException;
}

