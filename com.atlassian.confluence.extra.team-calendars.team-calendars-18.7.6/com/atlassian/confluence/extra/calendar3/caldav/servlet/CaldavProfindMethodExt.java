/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.servlet.SecureXmlMethod;
import java.io.Reader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bedework.webdav.servlet.common.PropFindMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.bedework.webdav.servlet.shared.WebdavUnauthorized;
import org.w3c.dom.Document;

public class CaldavProfindMethodExt
extends PropFindMethod
implements SecureXmlMethod {
    @Override
    protected final Document parseContent(int contentLength, Reader reader) throws WebdavException {
        return this.parseContentSafe(contentLength, reader);
    }

    @Override
    public final void doMethod(HttpServletRequest request, HttpServletResponse response) throws WebdavException {
        if (request.getRemoteUser() == null) {
            throw new WebdavUnauthorized();
        }
        super.doMethod(request, response);
    }
}

