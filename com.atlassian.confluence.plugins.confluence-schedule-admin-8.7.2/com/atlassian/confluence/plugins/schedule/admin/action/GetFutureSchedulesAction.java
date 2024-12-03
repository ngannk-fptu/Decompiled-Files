/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.scheduler.cron.CronSyntaxException
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.schedule.admin.action;

import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.schedule.admin.support.CronExpressionValidator;
import com.atlassian.scheduler.cron.CronSyntaxException;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;

public class GetFutureSchedulesAction
extends ConfluenceActionSupport
implements Beanable {
    private static final long serialVersionUID = -2441988255609084708L;
    private static final int MAX_RESULTS = 10;
    private Result result;
    private String cronExpressionValue;
    private transient CronExpressionValidator cronExpressionValidator;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        Locale userLocale = this.getLocale();
        if (StringUtils.isEmpty((CharSequence)this.cronExpressionValue)) {
            this.result = new Result(this.getText("scheduledjob.cron.notspecified"));
            return "error";
        }
        try {
            List<Date> futureDateSchedules = this.cronExpressionValidator.getFutureSchedules(this.cronExpressionValue, 10);
            ArrayList<FormattedDateTime> futureSchedules = new ArrayList<FormattedDateTime>(10);
            for (Date d : futureDateSchedules) {
                String formattedDate = DateFormat.getDateInstance(2, userLocale).format(d);
                String formattedTime = DateFormat.getTimeInstance(2, userLocale).format(d);
                futureSchedules.add(new FormattedDateTime(formattedDate, formattedTime));
            }
            this.result = new Result(futureSchedules);
        }
        catch (CronSyntaxException e) {
            this.result = new Result(this.getText("scheduledjob.cron.invalidformat"), e.getMessage());
            return "error";
        }
        return "success";
    }

    public Object getBean() {
        return this.result;
    }

    public String getCronExpression() {
        return this.cronExpressionValue;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpressionValue = cronExpression;
    }

    public void setCronExpressionValidator(CronExpressionValidator cronExpressionValidator) {
        this.cronExpressionValidator = cronExpressionValidator;
    }

    public static class FormattedDateTime {
        private String date;
        private String time;

        public FormattedDateTime(String date, String time) {
            this.date = date;
            this.time = time;
        }

        public String getDate() {
            return this.date;
        }

        public String getTime() {
            return this.time;
        }
    }

    public static class Result {
        private List<FormattedDateTime> futureSchedules;
        private String error;
        private String reason;

        private Result(List<FormattedDateTime> futureSchedules) {
            this.futureSchedules = futureSchedules;
        }

        private Result(String error) {
            this.error = error;
        }

        private Result(String error, String reason) {
            this.error = error;
            this.reason = reason;
        }

        public List<FormattedDateTime> getFutureSchedules() {
            return this.futureSchedules;
        }

        public String getError() {
            return this.error;
        }

        public String getReason() {
            return this.reason;
        }
    }
}

