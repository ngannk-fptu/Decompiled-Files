/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.plugins.rest.common.interceptor.MethodInvocation
 *  com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor
 *  com.atlassian.sal.api.user.UserManager
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.business.insights.core.audit.AuditEventFactory;
import com.atlassian.business.insights.core.rest.exception.InsufficientPermissionsException;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.plugins.rest.common.interceptor.MethodInvocation;
import com.atlassian.plugins.rest.common.interceptor.ResourceInterceptor;
import com.atlassian.sal.api.user.UserManager;
import java.lang.reflect.InvocationTargetException;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidateUserIsAuthedAsSysAdmin
implements ResourceInterceptor {
    private static final String BAD_REQUEST_INSUFFICIENT_PERMISSION = "data-pipeline.api.rest.insufficient.permissions.sysadmin.required";
    private final UserManager userManager;
    private final AuditService auditService;

    public ValidateUserIsAuthedAsSysAdmin(UserManager userManager, AuditService auditService) {
        this.userManager = userManager;
        this.auditService = auditService;
    }

    public void intercept(MethodInvocation methodInvocation) throws InvocationTargetException, IllegalAccessException {
        if (!this.isAuthedAsSysAdmin()) {
            this.raiseAuditEvent(this.findResourceName(methodInvocation));
            throw new InsufficientPermissionsException(new ValidationResult(BAD_REQUEST_INSUFFICIENT_PERMISSION));
        }
        methodInvocation.invoke();
    }

    private String findResourceName(MethodInvocation methodInvocation) {
        try {
            return methodInvocation.getHttpContext().getUriInfo().getPath();
        }
        catch (Exception e) {
            return "unknown";
        }
    }

    private void raiseAuditEvent(String request) {
        AuditEvent auditEvent = AuditEventFactory.createUnauthorizedExportAttemptAuditEvent(request);
        this.auditService.audit(auditEvent);
    }

    private boolean isAuthedAsSysAdmin() {
        return this.userManager.isSystemAdmin(this.userManager.getRemoteUserKey());
    }
}

