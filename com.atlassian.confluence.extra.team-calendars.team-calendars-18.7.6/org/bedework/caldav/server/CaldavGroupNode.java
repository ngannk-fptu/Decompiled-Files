/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.caldav.server;

import org.bedework.caldav.server.CaldavPrincipalNode;
import org.bedework.caldav.server.CaldavURI;
import org.bedework.caldav.server.sysinterface.CalPrincipalInfo;
import org.bedework.caldav.server.sysinterface.SysIntf;
import org.bedework.webdav.servlet.shared.WebdavException;

public class CaldavGroupNode
extends CaldavPrincipalNode {
    public CaldavGroupNode(CaldavURI cdURI, SysIntf sysi, CalPrincipalInfo ui) throws WebdavException {
        super(cdURI, sysi, ui, false);
    }
}

