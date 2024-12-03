/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.http.HttpResponse;
import com.amazonaws.services.s3.internal.HeaderHandler;
import com.amazonaws.services.s3.internal.ObjectExpirationResult;
import com.amazonaws.services.s3.internal.ServiceUtils;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectExpirationHeaderHandler<T extends ObjectExpirationResult>
implements HeaderHandler<T> {
    private static final Pattern datePattern = Pattern.compile("expiry-date=\"(.*?)\"");
    private static final Pattern rulePattern = Pattern.compile("rule-id=\"(.*?)\"");
    private static final Log log = LogFactory.getLog(ObjectExpirationHeaderHandler.class);

    @Override
    public void handle(T result, HttpResponse response) {
        String expirationHeader = response.getHeaders().get("x-amz-expiration");
        if (expirationHeader != null) {
            result.setExpirationTime(this.parseDate(expirationHeader));
            result.setExpirationTimeRuleId(this.parseRuleId(expirationHeader));
        }
    }

    private String parseRuleId(String expirationHeader) {
        Matcher matcher = rulePattern.matcher(expirationHeader);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private Date parseDate(String expirationHeader) {
        Matcher matcher = datePattern.matcher(expirationHeader);
        if (matcher.find()) {
            String date = matcher.group(1);
            try {
                return ServiceUtils.parseRfc822Date(date);
            }
            catch (Exception exception) {
                log.warn((Object)"Error parsing expiry-date from x-amz-expiration header.", (Throwable)exception);
            }
        }
        return null;
    }
}

