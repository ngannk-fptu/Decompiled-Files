/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.request.FileOptionsValidator;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipMonitor;
import com.atlassian.troubleshooting.stp.zip.SupportZipRequest;
import com.atlassian.troubleshooting.stp.zip.SupportZipService;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SupportZipAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "support-zip";
    static final String LIMIT_FILE_SIZE = "limit-file-sizes";
    static final String LAST_MODIFIED_AGE_CONSTRAINT = "last-modified-age-constraint";
    private static final String CONSTRAINT_FILE_SIZE = "constraint-file-size";
    private static final Logger LOG = LoggerFactory.getLogger(SupportZipAction.class);
    private final SupportApplicationInfo applicationInfo;
    private final SupportZipService supportZipService;

    public SupportZipAction(SupportApplicationInfo applicationInfo, SupportZipService supportZipService) {
        super(ACTION_NAME, "stp.contact.title", "stp.create.support.zip.title", null);
        this.applicationInfo = Objects.requireNonNull(applicationInfo);
        this.supportZipService = Objects.requireNonNull(supportZipService);
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new SupportZipAction(this.applicationInfo, this.supportZipService);
    }

    @Override
    public void validate(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        new FileOptionsValidator(this.applicationInfo).validate(context, req, validationLog);
    }

    @Override
    public void execute(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        try {
            Boolean limitFileSizes = req.getParameter(LIMIT_FILE_SIZE) != null ? Boolean.valueOf(true) : null;
            Integer fileConstraintSize = req.getParameter(CONSTRAINT_FILE_SIZE) != null ? Integer.valueOf(Integer.parseInt(req.getParameter(CONSTRAINT_FILE_SIZE))) : null;
            Integer fileConstraintLastModified = SupportZipAction.getFileConstraintLastModified(req.getParameter(LAST_MODIFIED_AGE_CONSTRAINT));
            Set<String> items = this.applicationInfo.getSelectedSupportZipBundles(req).stream().map(SupportZipBundle::getKey).collect(Collectors.toSet());
            SupportZipRequest supportZipRequest = new SupportZipRequest(items, limitFileSizes, fileConstraintSize, fileConstraintLastModified, null, null, SupportZipRequest.Source.WEB_V1);
            CreateSupportZipMonitor taskMonitor = this.supportZipService.createLocalSupportZipWithPermissionCheck(supportZipRequest);
            context.put("taskId", taskMonitor.getTaskId());
        }
        catch (Exception e) {
            validationLog.addError("stp.create.support.zip.error.message", new Serializable[]{this.applicationInfo.getApplicationLogDir(), e.getMessage()});
            LOG.error("Error creating support zip. Please zip up your {} directory and attach this file to the issue", (Object)this.applicationInfo.getApplicationLogDir(), (Object)e);
        }
    }

    private static Integer getFileConstraintLastModified(String parameter) {
        try {
            return Integer.parseInt(parameter);
        }
        catch (NumberFormatException ex) {
            return null;
        }
    }
}

