/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.mail.MailException
 *  com.atlassian.templaterenderer.RenderingException
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.mail.MessagingException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.mail.MailException;
import com.atlassian.templaterenderer.RenderingException;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.action.DefaultMessage;
import com.atlassian.troubleshooting.stp.events.StpSupportRequestEmailEvent;
import com.atlassian.troubleshooting.stp.request.SupportRequestCreationRequest;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.salext.mail.MailUtility;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequest;
import com.atlassian.troubleshooting.stp.salext.mail.SupportRequestAttachment;
import com.atlassian.troubleshooting.stp.spi.HostApplication;
import com.atlassian.troubleshooting.stp.task.MonitoredCallable;
import com.atlassian.troubleshooting.stp.task.MutableTaskMonitor;
import com.atlassian.troubleshooting.stp.task.SubtaskMonitorListener;
import com.atlassian.troubleshooting.stp.zip.CreateSupportZipTask;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateSupportRequestTask
implements MonitoredCallable<Void, MutableTaskMonitor<Void>> {
    private static final Logger LOG = LoggerFactory.getLogger(CreateSupportRequestTask.class);
    private static final int WEIGHT_CREATE_SUPPORT_ZIP = 90;
    private final SupportApplicationInfo applicationInfo;
    private final CreateSupportZipTask createZipTask;
    private final HostApplication hostApplication;
    private final MailUtility mailUtility;
    private final MutableTaskMonitor<Void> monitor;
    private final SupportRequestCreationRequest request;
    private final String username;
    private final EventPublisher eventPublisher;
    private final TemplateRenderer templateRenderer;

    CreateSupportRequestTask(SupportRequestCreationRequest request, SupportApplicationInfo applicationInfo, HostApplication hostApplication, MailUtility mailUtility, EventPublisher eventPublisher, MutableTaskMonitor<Void> monitor, CreateSupportZipTask createSupportZipTask, @Nullable String username) {
        this.applicationInfo = applicationInfo;
        this.hostApplication = hostApplication;
        this.mailUtility = mailUtility;
        this.request = request;
        this.templateRenderer = applicationInfo.getTemplateRenderer();
        this.username = username;
        this.eventPublisher = eventPublisher;
        this.monitor = monitor;
        this.createZipTask = createSupportZipTask;
        this.createZipTask.getMonitor().addListener(new SubtaskMonitorListener(monitor, 90));
    }

    @Override
    public Void call() throws Exception {
        return this.hostApplication.asUser(this.username, () -> {
            this.createSupportRequest();
            return null;
        }).call();
    }

    protected void createSupportRequest() {
        try {
            File zipFile = this.createZipTask.call();
            this.monitor.updateProgress(90, this.applicationInfo.getText("stp.create.support.request.inprogress.message"));
            if (zipFile != null) {
                SupportRequest supportRequest = new SupportRequest(this.request.getDescription(), this.request.getSubject(), this.applicationInfo.getCreateSupportRequestEmail(), this.request.getFromAddress(), this.generateMailBody(), this.request.getPriority());
                supportRequest.addAttachment(new SupportRequestAttachment(zipFile.getName(), "application/zip", zipFile));
                this.mailUtility.sendSupportRequestMail(supportRequest, this.applicationInfo);
            }
            this.monitor.updateProgress(100, this.applicationInfo.getText("stp.create.support.request.success.title"));
            this.doAnalytics(true);
        }
        catch (MailException | RenderingException | MessagingException e) {
            this.handleException((Exception)e, "Sending support request", "stp.mail.delivery.error");
        }
        catch (Exception e) {
            this.handleException(e, "General error", "stp.mail.general.error");
        }
    }

    private void doAnalytics(boolean successful) {
        Set<String> selectedBundles = this.request.getBundles().stream().map(SupportZipBundle::getKey).collect(Collectors.toSet());
        Integer fileConstraintSize = this.request.getFileConstraintSize();
        Boolean isFileSizeLimited = this.request.isLimitFileSizes();
        Integer fileConstraintLastModified = this.request.getFileConstraintLastModified();
        this.eventPublisher.publish((Object)new StpSupportRequestEmailEvent(successful, selectedBundles, isFileSizeLimited, fileConstraintSize, fileConstraintLastModified));
    }

    @Override
    @Nonnull
    public MutableTaskMonitor<Void> getMonitor() {
        return this.monitor;
    }

    private String generateMailBody() throws RenderingException, IOException {
        HashMap<String, String> context = new HashMap<String, String>();
        context.put("description", this.request.getDescription());
        context.put("contactEmail", this.request.getFromAddress());
        context.put("priority", String.valueOf(this.request.getPriority()));
        StringWriter writer = new StringWriter();
        this.templateRenderer.render(this.getTemplateFile(), context, (Writer)writer);
        return writer.toString();
    }

    private void handleException(Exception e, String name, String key) {
        LOG.error(name, (Throwable)e);
        String errorMessage = this.applicationInfo.getText(key);
        this.monitor.updateProgress(100, errorMessage);
        this.monitor.addError(new DefaultMessage(name, errorMessage));
        this.doAnalytics(false);
    }

    private String getTemplateFile() {
        return "/templates/email/create-support-request.vm";
    }
}

