/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.util.CollectionUtils
 */
package com.atlassian.business.insights.core.rest.validation.validators.bodyparam;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.rest.model.ConfigExportScheduleRequest;
import com.atlassian.business.insights.core.rest.validation.RequestBodyValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import com.atlassian.business.insights.core.rest.validation.validators.bodyparam.ExportPathValidator;
import com.atlassian.business.insights.core.rest.validation.validators.util.ExportFromParser;
import com.atlassian.business.insights.core.rest.validation.validators.util.SchemaVersionValueParser;
import com.atlassian.business.insights.core.util.DateConversionUtil;
import java.time.Instant;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

public class ExportScheduleValidator
implements RequestBodyValidator {
    public static final String BAD_REQUEST_INVALID_SCHEMA_VERSION_KEY = "data-pipeline.api.rest.request.body.config.schema.version.invalid";
    public static final String BAD_REQUEST_INVALID_EXPORT_DAYS_OF_WEEK_KEY = "data-pipeline.api.rest.request.body.config.days.of.week.invalid";
    @VisibleForTesting
    static final String BAD_REQUEST_INVALID_FROM_DATE_KEY = "data-pipeline.api.rest.config.request.body.fromdate.invalid";
    @VisibleForTesting
    static final String BAD_REQUEST_FUTURE_FROM_DATE_KEY = "data-pipeline.api.rest.config.request.body.fromdate.not.be.future";
    @VisibleForTesting
    static final String BAD_REQUEST_INVALID_REQUEST_BODY_KEY = "data-pipeline.api.rest.request.body.config.schedule.invalid";
    @VisibleForTesting
    static final String BAD_REQUEST_INVALID_EXPORT_TIME_KEY = "data-pipeline.api.rest.request.body.config.time.invalid";
    @VisibleForTesting
    static final String BAD_REQUEST_INVALID_REPEAT_INTERVAL_IN_WEEKS_KEY = "data-pipeline.api.rest.request.body.config.repeat.interval.in.weeks.invalid";
    private static final int REPEAT_EVERY_LIMIT = 52;

    @Override
    public void validate(@Nullable Object[] bodyContent, @Nonnull ValidationResult validationResult) {
        if (bodyContent == null || ExportPathValidator.isBodyContentEmpty(bodyContent) || !(bodyContent[0] instanceof ConfigExportScheduleRequest)) {
            validationResult.add(BAD_REQUEST_INVALID_REQUEST_BODY_KEY);
        } else {
            ConfigExportScheduleRequest scheduleRequest = (ConfigExportScheduleRequest)bodyContent[0];
            Optional<Instant> exportFrom = ExportFromParser.parse(scheduleRequest.getFromDate());
            if (exportFrom.isPresent()) {
                if (exportFrom.get().isAfter(Instant.now())) {
                    validationResult.add(BAD_REQUEST_FUTURE_FROM_DATE_KEY);
                }
            } else {
                validationResult.add(BAD_REQUEST_INVALID_FROM_DATE_KEY);
            }
            if (CollectionUtils.isEmpty(scheduleRequest.getDays())) {
                validationResult.add(BAD_REQUEST_INVALID_EXPORT_DAYS_OF_WEEK_KEY);
            }
            if (!this.isTimeValid(scheduleRequest.getTime())) {
                validationResult.add(BAD_REQUEST_INVALID_EXPORT_TIME_KEY);
            }
            if (!this.isRepeatIntervalInWeeksValid(scheduleRequest.getRepeatIntervalInWeeks())) {
                validationResult.add(BAD_REQUEST_INVALID_REPEAT_INTERVAL_IN_WEEKS_KEY);
            }
            if (scheduleRequest.getSchemaVersion() != null && SchemaVersionValueParser.isNotParsable(scheduleRequest.getSchemaVersion())) {
                validationResult.add(BAD_REQUEST_INVALID_SCHEMA_VERSION_KEY);
            }
        }
    }

    private boolean isTimeValid(String time) {
        if (StringUtils.isBlank((CharSequence)time)) {
            return false;
        }
        try {
            DateConversionUtil.parseTimeAsLocalTime(time);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private boolean isRepeatIntervalInWeeksValid(String repeatWeeksString) {
        if (StringUtils.isBlank((CharSequence)repeatWeeksString)) {
            return false;
        }
        try {
            int repeatWeeks = Integer.parseInt(repeatWeeksString);
            return repeatWeeks > 0 && repeatWeeks <= 52;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}

