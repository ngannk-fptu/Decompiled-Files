/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.servlet;

import com.atlassian.confluence.extra.calendar3.caldav.servlet.SecureXmlMethod;
import java.io.Reader;
import org.bedework.webdav.servlet.common.AclMethod;
import org.bedework.webdav.servlet.shared.WebdavException;
import org.w3c.dom.Document;

public class AclMethodExt
extends AclMethod
implements SecureXmlMethod {
    @Override
    protected final Document parseContent(int contentLength, Reader reader) throws WebdavException {
        return this.parseContentSafe(contentLength, reader);
    }
}

