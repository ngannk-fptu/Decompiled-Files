/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.apache.jackrabbit.webdav.security.SupportedPrivilege;
import org.w3c.dom.Element;

public class SupportedPrivilegeSetProperty
extends AbstractDavProperty<List<SupportedPrivilege>> {
    private final SupportedPrivilege[] supportedPrivileges;

    public SupportedPrivilegeSetProperty(SupportedPrivilege[] supportedPrivileges) {
        super(SecurityConstants.SUPPORTED_PRIVILEGE_SET, true);
        this.supportedPrivileges = supportedPrivileges;
    }

    public SupportedPrivilegeSetProperty(DavProperty<?> p) throws DavException {
        super(SecurityConstants.SUPPORTED_PRIVILEGE_SET, true);
        if (!SecurityConstants.SUPPORTED_PRIVILEGE_SET.equals(this.getName())) {
            throw new DavException(400, "DAV:supported-privilege-set expected.");
        }
        ArrayList<SupportedPrivilege> supportedPrivs = new ArrayList<SupportedPrivilege>();
        for (Object obj : Collections.singletonList(p.getValue())) {
            if (obj instanceof Element) {
                supportedPrivs.add(SupportedPrivilege.getSupportedPrivilege((Element)obj));
                continue;
            }
            if (!(obj instanceof Collection)) continue;
            for (Object entry : (Collection)obj) {
                if (!(entry instanceof Element)) continue;
                supportedPrivs.add(SupportedPrivilege.getSupportedPrivilege((Element)entry));
            }
        }
        this.supportedPrivileges = supportedPrivs.toArray(new SupportedPrivilege[supportedPrivs.size()]);
    }

    @Override
    public List<SupportedPrivilege> getValue() {
        List<SupportedPrivilege> l = this.supportedPrivileges == null ? Collections.emptyList() : Arrays.asList(this.supportedPrivileges);
        return l;
    }
}

