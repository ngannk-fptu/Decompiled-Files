/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.jcr.security;

import javax.jcr.Session;

public final class SecurityUtils {
    private SecurityUtils() {
    }

    public static boolean supportsAccessControl(Session session) {
        String desc = session.getRepository().getDescriptor("option.access.control.supported");
        return Boolean.valueOf(desc);
    }
}

