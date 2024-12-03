/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.security.Privilege;
import org.apache.jackrabbit.webdav.security.SecurityConstants;
import org.w3c.dom.Element;

public class CurrentUserPrivilegeSetProperty
extends AbstractDavProperty<Collection<Privilege>> {
    private final Set<Privilege> privileges;

    public CurrentUserPrivilegeSetProperty(Privilege[] privileges) {
        super(SecurityConstants.CURRENT_USER_PRIVILEGE_SET, true);
        this.privileges = new HashSet<Privilege>();
        for (Privilege privilege : privileges) {
            if (privilege == null) continue;
            this.privileges.add(privilege);
        }
    }

    public CurrentUserPrivilegeSetProperty(DavProperty<?> xmlDavProperty) throws DavException {
        super(xmlDavProperty.getName(), true);
        if (!SecurityConstants.CURRENT_USER_PRIVILEGE_SET.equals(this.getName())) {
            throw new DavException(400, "DAV:current-user-privilege-set expected.");
        }
        this.privileges = new HashSet<Privilege>();
        Object value = xmlDavProperty.getValue();
        if (value != null) {
            if (value instanceof Element) {
                this.privileges.add(Privilege.getPrivilege((Element)value));
            } else if (value instanceof Collection) {
                for (Object entry : (Collection)value) {
                    if (!(entry instanceof Element)) continue;
                    this.privileges.add(Privilege.getPrivilege((Element)entry));
                }
            }
        }
    }

    @Override
    public Collection<Privilege> getValue() {
        return this.privileges;
    }
}

