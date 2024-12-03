/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.security.DefaultAcceptedPatternsChecker
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.struts;

import com.opensymphony.xwork2.security.DefaultAcceptedPatternsChecker;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluencePatternsChecker
extends DefaultAcceptedPatternsChecker {
    private static final Logger LOG = LoggerFactory.getLogger(ConfluencePatternsChecker.class);
    private static final String ACKNOWLEDGED_UPSTREAM_PATTERN = "\\w+((\\.\\w+)|(\\[\\d+])|(\\(\\d+\\))|(\\['(\\w-?|[\\u4e00-\\u9fa5]-?)+'])|(\\('(\\w-?|[\\u4e00-\\u9fa5]-?)+'\\)))*";

    protected void logPatternChange(Set<String> newPatterns) {
        if (!ACCEPTED_PATTERNS[0].equals(ACKNOWLEDGED_UPSTREAM_PATTERN)) {
            LOG.error("The upstream pattern for accepted patterns has changed.\nPlease assess the security implications and integrate any applicable changes into the struts.override.acceptedPatterns value in our struts.xml.\nOnce done, replace the comparison field with the upstream pattern to suppress this error.");
        }
        LOG.debug("Replacing Struts Parameters accepted patterns [{}] with [{}]!", (Object)this.acceptedPatterns, newPatterns);
    }
}

