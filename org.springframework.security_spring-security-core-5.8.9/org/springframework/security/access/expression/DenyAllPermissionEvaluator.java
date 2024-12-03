/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.log.LogMessage
 */
package org.springframework.security.access.expression;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

public class DenyAllPermissionEvaluator
implements PermissionEvaluator {
    private final Log logger = LogFactory.getLog(this.getClass());

    @Override
    public boolean hasPermission(Authentication authentication, Object target, Object permission) {
        this.logger.warn((Object)LogMessage.format((String)"Denying user %s permission '%s' on object %s", (Object)authentication.getName(), (Object)permission, (Object)target));
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        this.logger.warn((Object)LogMessage.format((String)"Denying user %s permission '%s' on object with Id %s", (Object)authentication.getName(), (Object)permission, (Object)targetId));
        return false;
    }
}

