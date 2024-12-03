/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package org.bedework.webdav.servlet.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.webdav.servlet.common.CopyMethod;
import org.bedework.webdav.servlet.shared.WebdavException;

public class MoveMethod
extends CopyMethod {
    @Override
    public void doMethod(HttpServletRequest req, HttpServletResponse resp) throws WebdavException {
        this.process(req, resp, false);
    }
}

