/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ConfluenceSystemProperties
 *  com.atlassian.confluence.mail.MailQueueManager
 *  com.atlassian.confluence.mail.notification.listeners.NotificationTemplate
 *  com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.core.task.Task
 *  com.atlassian.fugue.Pair
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.dailysummary.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.mail.MailQueueManager;
import com.atlassian.confluence.mail.notification.listeners.NotificationTemplate;
import com.atlassian.confluence.mail.template.PreRenderedMailNotificationQueueItem;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailService;
import com.atlassian.confluence.plugins.dailysummary.components.SummaryEmailTaskFactory;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.core.task.Task;
import com.atlassian.fugue.Pair;
import com.atlassian.user.User;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PopularContentAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger((String)PopularContentAction.class.getName());
    private static final Pair<Integer, TimeUnit> MAIL_QUEUE_FLUSH_TIMEOUT = new Pair((Object)1, (Object)TimeUnit.SECONDS);
    private SummaryEmailService summaryEmailService;
    private SummaryEmailTaskFactory summaryEmailFactory;
    private VelocityHelperService velocityHelperService;
    private boolean fireemail;
    private String schedule;
    private String renderedContent;
    private MailQueueManager mailQueueManager;
    private SpaceManager spaceManager;
    private Space space;

    public String execute() throws Exception {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        this.schedule = this.userAccessor.getUserPreferences((User)user).getString("confluence.prefs.daily.summary.schedule");
        Date date = new Date();
        Optional<Task> email = this.summaryEmailFactory.createEmailTask((User)user, date, this.space);
        if (this.fireemail) {
            if (this.isDevMode() || NotificationTemplate.ADG.isEnabled("recommended")) {
                if (!email.isPresent() || !this.summaryEmailService.sendEmail((User)user, new Date())) {
                    this.addActionError("daily.summary.action.email.nocontent.error", new Object[]{user.getName()});
                } else {
                    this.flushWithTimeout();
                }
            } else {
                this.addActionError("daily.summary.action.email.devmode.error", new Object[]{user.getName()});
            }
        }
        if (email.isPresent()) {
            this.renderedContent = ((PreRenderedMailNotificationQueueItem)email.get()).getBody();
        } else if (!this.hasActionErrors()) {
            this.getMessageHolder().addActionInfo("daily.summary.action.email.nocontent.info", new Object[]{user.getName(), 3, "hourly".equals(this.schedule) ? 1 : ("daily".equals(this.schedule) ? 2 : 3)});
        }
        return super.execute();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void flushWithTimeout() throws InterruptedException, ExecutionException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        try {
            Future<Void> flush = executor.submit(() -> {
                this.mailQueueManager.flushQueue();
                return null;
            });
            try {
                flush.get(((Integer)MAIL_QUEUE_FLUSH_TIMEOUT.left()).intValue(), (TimeUnit)((Object)MAIL_QUEUE_FLUSH_TIMEOUT.right()));
            }
            catch (TimeoutException e) {
                this.addActionError(this.getI18n().getText("daily.summary.action.email.flush.timeout.error", new Object[]{MAIL_QUEUE_FLUSH_TIMEOUT.left()}));
            }
        }
        finally {
            executor.shutdownNow();
        }
    }

    public void setFireemail(boolean sendIt) {
        this.fireemail = sendIt;
    }

    public void setSummaryEmailService(SummaryEmailService service) {
        this.summaryEmailService = service;
    }

    public String getRenderedContent() throws Exception {
        return this.renderedContent;
    }

    public SummaryEmailTaskFactory getSummaryEmailFactory() {
        return this.summaryEmailFactory;
    }

    public void setSummaryEmailFactory(SummaryEmailTaskFactory summaryEmailFactory) {
        this.summaryEmailFactory = summaryEmailFactory;
    }

    public VelocityHelperService getVelocityHelperService() {
        return this.velocityHelperService;
    }

    public void setVelocityHelperService(VelocityHelperService velocityHelperService) {
        this.velocityHelperService = velocityHelperService;
    }

    public boolean isDevMode() {
        return ConfluenceSystemProperties.isDevMode();
    }

    public boolean hasContent() {
        return StringUtils.isNotEmpty((CharSequence)this.renderedContent);
    }

    public void setMailQueueManager(MailQueueManager mailQueueManager) {
        this.mailQueueManager = mailQueueManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setSpaceKey(String spaceKey) {
        this.space = this.spaceManager.getSpace(spaceKey);
    }
}

