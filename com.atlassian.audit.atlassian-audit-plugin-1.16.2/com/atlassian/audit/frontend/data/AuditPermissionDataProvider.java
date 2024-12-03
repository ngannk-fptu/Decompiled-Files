/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.audit.frontend.data;

import com.atlassian.audit.frontend.data.AuditPermissionData;
import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class AuditPermissionDataProvider
implements WebResourceDataProvider {
    private final ObjectMapper objectMapper;
    private final PermissionChecker permissionChecker;

    public AuditPermissionDataProvider(ObjectMapper objectMapper, PermissionChecker permissionChecker) {
        this.objectMapper = objectMapper;
        this.permissionChecker = permissionChecker;
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.objectMapper.writeValue(writer, (Object)this.getData());
            }
            catch (Exception e) {
                throw new JsonMappingException(e.getMessage(), (Throwable)e);
            }
        };
    }

    private AuditPermissionData getData() {
        return new AuditPermissionData().allowUpdateConfiguration(this.permissionChecker.hasCoverageConfigUpdatePermission() && this.permissionChecker.hasRetentionConfigUpdatePermission()).allowViewConfiguration(this.permissionChecker.hasRetentionConfigViewPermission());
    }
}

