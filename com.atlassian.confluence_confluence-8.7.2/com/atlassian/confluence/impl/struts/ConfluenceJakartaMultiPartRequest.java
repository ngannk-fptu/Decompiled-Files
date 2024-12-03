/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.permission.AuthorisationException
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.spring.container.LazyComponentReference
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.confluence.impl.struts.MultipartUploadConfigurator;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.sal.api.permission.AuthorisationException;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.spring.container.LazyComponentReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.multipart.JakartaMultiPartRequest;

public class ConfluenceJakartaMultiPartRequest
extends JakartaMultiPartRequest {
    private static final String MULTIPART_LIMIT_EXEMPT_PATTERNS = System.getProperty("multipart.limit.exempt.patterns", "/spaces/restore.action.*,/admin/restore.action.*");
    private static final String MULTIPART_AUTHENTICATED_MAXPARAMLEN = System.getProperty("multipart.authenticated.max.param.length", String.valueOf(100000));
    private static final String MULTIPART_AUTHENTICATED_MAXPARTS = System.getProperty("multipart.authenticated.max.parts", String.valueOf(10000));
    private static final List<Pattern> COMPILED_EXEMPT_PATTERNS = ConfluenceJakartaMultiPartRequest.buildPatternsList(MULTIPART_LIMIT_EXEMPT_PATTERNS, ",");
    private final Supplier<PermissionEnforcer> permissionEnforcerRef = new LazyComponentReference("salPermissionEnforcer");
    private final Supplier<MultipartUploadConfigurator> multipartUploadConfiguratorRef = new LazyComponentReference("multipartUploadConfigurator");

    public void parse(HttpServletRequest request, String saveDir) throws IOException {
        if (this.shouldBlockMultipart(request)) {
            return;
        }
        this.delimitSizeIfExempt(request);
        this.delimitParamsIfAuthorised();
        super.parse(request, saveDir);
    }

    void delimitSizeIfExempt(HttpServletRequest request) {
        if (this.permissionEnforcerRef.get().isLicensed() && COMPILED_EXEMPT_PATTERNS.stream().anyMatch(pattern -> pattern.matcher(request.getServletPath()).matches())) {
            this.maxSize = -1L;
            this.maxFileSize = -1L;
        }
    }

    void delimitParamsIfAuthorised() {
        if (this.permissionEnforcerRef.get().isLicensedOrLimitedUnlicensedUser()) {
            this.maxStringLength = Math.max(this.maxStringLength, Long.parseLong(MULTIPART_AUTHENTICATED_MAXPARAMLEN));
            this.maxFiles = Math.max(this.maxFiles, Long.parseLong(MULTIPART_AUTHENTICATED_MAXPARTS));
        }
    }

    public static List<Pattern> buildPatternsList(String patterns, String separator) {
        if (patterns == null || patterns.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(patterns.split(separator)).map(token -> Pattern.compile(token.trim())).collect(Collectors.toUnmodifiableList());
    }

    boolean shouldBlockMultipart(HttpServletRequest request) {
        block3: {
            BootstrapManager bootstrapManager = (BootstrapManager)ContainerManager.getComponent((String)"bootstrapManager", BootstrapManager.class);
            if (!bootstrapManager.isSetupComplete()) {
                return false;
            }
            try {
                this.permissionEnforcerRef.get().enforceSiteAccess();
            }
            catch (AuthorisationException e) {
                String path = request.getServletPath();
                if (!this.multipartUploadConfiguratorRef.get().getUnauthorisedAllowedPatterns().stream().noneMatch(pattern -> pattern.matcher(path).matches())) break block3;
                return true;
            }
        }
        return false;
    }
}

