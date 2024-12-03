/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  javax.annotation.Nonnull
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.request.SupportRequestCreationRequest;
import com.atlassian.troubleshooting.stp.request.SupportRequestService;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.EmailValidator;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.security.UserService;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import com.atlassian.troubleshooting.stp.task.TaskMonitor;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class CreateSupportRequestAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "create-support-request";
    private static final LocalDateTime SERVER_EOL_DATE_TIME = LocalDateTime.of(2024, 2, 15, 0, 0);
    private final LicenseService licenseService;
    private final SupportApplicationInfo info;
    private final MailUtility mailUtility;
    private final SupportRequestService supportRequestService;
    private final EventPublisher eventPublisher;
    private final UserService userService;
    private final String templateStart;
    private final String templateBlocked;
    private final Clock clock;

    public CreateSupportRequestAction(LicenseService licenseService, SupportApplicationInfo info, MailUtility mailUtility, SupportRequestService supportRequestService, EventPublisher eventPublisher, UserService userService, Clock clock) {
        super(ACTION_NAME, "stp.contact.title", "stp.get.help.title", null);
        this.licenseService = Objects.requireNonNull(licenseService);
        this.info = Objects.requireNonNull(info);
        this.mailUtility = Objects.requireNonNull(mailUtility);
        this.supportRequestService = Objects.requireNonNull(supportRequestService);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.userService = Objects.requireNonNull(userService);
        this.clock = clock;
        this.templateStart = this.getDefaultTemplatePath(null, "start");
        this.templateBlocked = this.getDefaultTemplatePath(null, "blocked");
    }

    private static String getParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        return value != null ? value : "";
    }

    private static int toInt(String str, int defaultValue) {
        try {
            return str == null ? defaultValue : Integer.parseInt(str);
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    @Override
    public void prepare(Map<String, Object> context, SafeHttpServletRequest request, ValidationLog validationLog) {
        context.put("description", CreateSupportRequestAction.getParameter(request, "description"));
        context.put("contactEmail", this.getContactEmail(request));
        context.put("subject", CreateSupportRequestAction.getParameter(request, "subject"));
        context.put("priority", String.valueOf(this.getPriority(request)));
        context.put("mailQueueURL", this.info.getMailQueueURL(request));
        context.put("mailExceptionAvailable", this.info.isMailExceptionAvailable());
        context.put("mailServerConfigured", this.mailUtility.isMailServerConfigured());
        context.put("requestUrl", request.getRequestURL());
        context.put("isLicenseExpired", this.info.getLicenseInfo().getDaysToExpiry() <= 0);
        context.put("licenseAdminUrl", this.info.getBaseURL(request) + this.info.getAdminLicenseUrl());
        context.put("userCanRequestTechnicalSupport", this.licenseService.userCanRequestTechnicalSupport());
        context.put("applicationName", this.info.getApplicationName());
        context.put("mailServerConfigurationUrl", this.info.getMailServerConfigurationURL(request));
    }

    @Override
    @Nonnull
    public String getStartTemplatePath() {
        if (this.isServerAndEOL()) {
            return this.templateBlocked;
        }
        return this.templateStart;
    }

    private boolean isServerAndEOL() {
        return LocalDateTime.now(this.clock).isAfter(SERVER_EOL_DATE_TIME) && (this.info.getApplicationType() != ApplicationType.FISHEYE && !this.licenseService.isLicensedForDataCenter() || this.info.getApplicationType() == ApplicationType.FISHEYE && this.licenseService.isExpired());
    }

    @Override
    public void validate(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        String contactEmail;
        if (StringUtils.isBlank((CharSequence)req.getParameter("subject"))) {
            validationLog.addFieldError("subject", "stp.create.support.request.subject.empty");
        }
        if (StringUtils.isBlank((CharSequence)req.getParameter("description"))) {
            validationLog.addFieldError("description", "stp.create.support.request.description.empty");
        }
        if (StringUtils.isBlank((CharSequence)(contactEmail = req.getParameter("contactEmail")))) {
            validationLog.addFieldError("contactEmail", "stp.create.support.request.from.empty");
        } else if (!EmailValidator.isValidEmailAddress(contactEmail)) {
            validationLog.addFieldError("contactEmail", "stp.create.support.request.from.invalid", new Serializable[]{StringEscapeUtils.escapeHtml4((String)contactEmail)});
        }
    }

    @Override
    public void execute(Map<String, Object> context, SafeHttpServletRequest req, ValidationLog validationLog) {
        TaskMonitor<Void> monitor = this.supportRequestService.createSupportRequest(new SupportRequestCreationRequest.Builder().description(req.getParameter("description")).fromAddress(this.getContactEmail(req)).priority(this.getPriority(req)).subject(req.getParameter("subject")).bundles(this.info.getSelectedSupportZipBundles(req)).limitFileSizes(req.getParameter("limit-file-sizes") != null ? Boolean.valueOf(true) : null).fileConstraintSize(req.getParameter("file-constraint-size") == null ? null : Integer.valueOf(Integer.parseInt(req.getParameter("file-constraint-size")))).fileConstraintLastModified(CreateSupportRequestAction.getFileConstraintLastModified(req.getParameter("last-modified-age-constraint"))).build());
        context.put("taskId", monitor.getTaskId());
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new CreateSupportRequestAction(this.licenseService, this.info, this.mailUtility, this.supportRequestService, this.eventPublisher, this.userService, this.clock);
    }

    private String getContactEmail(HttpServletRequest request) {
        String contactEmail = CreateSupportRequestAction.getParameter(request, "contactEmail");
        return StringUtils.isBlank((CharSequence)contactEmail) ? this.userService.getUserEmail().orElse("") : contactEmail;
    }

    private int getPriority(HttpServletRequest request) {
        return CreateSupportRequestAction.toInt(request.getParameter("priority"), 3);
    }

    @Override
    public boolean requiresWebSudo() {
        return false;
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

