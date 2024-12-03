/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.common;

import javax.xml.namespace.QName;
import org.bedework.access.AccessXmlUtil;
import org.bedework.util.xml.XmlEmit;
import org.bedework.webdav.servlet.shared.WebdavException;

public class AccessUtil
extends AccessXmlUtil {
    private String namespacePrefix;

    public AccessUtil(String namespacePrefix, XmlEmit xml, AccessXmlUtil.AccessXmlCb cb) throws WebdavException {
        super(caldavPrivTags, xml, cb);
        this.namespacePrefix = namespacePrefix;
    }

    public String makeUserHref(String who) {
        return this.namespacePrefix + "/principals/users/" + who;
    }

    public String makeGroupHref(String who) {
        return this.namespacePrefix + "/principals/groups/" + who;
    }

    public QName[] getPrivTags() {
        return caldavPrivTags;
    }
}

