/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.streams.api.FeedContentSanitizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.streams.internal.feed;

import com.atlassian.streams.api.FeedContentSanitizer;
import java.io.IOException;
import java.io.InputStream;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedContentSanitizerImpl
implements FeedContentSanitizer {
    private static final Logger log = LoggerFactory.getLogger(FeedContentSanitizerImpl.class);
    public static final String ANTISAMY_POLICY_FILE = "antisamy-policy.xml";
    private AntiSamy as = new AntiSamy();
    private Policy policy;

    public FeedContentSanitizerImpl() {
        try (InputStream stream = this.getClass().getClassLoader().getResourceAsStream(ANTISAMY_POLICY_FILE);){
            this.policy = Policy.getInstance(stream);
        }
        catch (IOException | PolicyException e) {
            log.error("Error loading AntiSamy policy file", (Throwable)e);
        }
    }

    public String sanitize(String taintedInput) {
        CleanResults cr;
        try {
            cr = this.as.scan(taintedInput, this.policy);
        }
        catch (PolicyException e) {
            log.error("Error loading AntiSamy policy file", (Throwable)e);
            return "";
        }
        catch (ScanException e) {
            log.debug("Error scanning input with AntiSamy", (Throwable)e);
            return "";
        }
        return cr.getCleanHTML();
    }
}

