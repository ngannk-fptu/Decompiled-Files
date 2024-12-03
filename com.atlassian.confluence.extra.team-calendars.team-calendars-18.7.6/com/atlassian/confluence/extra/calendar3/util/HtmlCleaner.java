/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.util;

import com.atlassian.core.util.ClassLoaderUtils;
import java.io.InputStream;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmlCleaner {
    private final AntiSamy antiSamy;
    private static final Logger LOG = LoggerFactory.getLogger(HtmlCleaner.class);

    public HtmlCleaner(String policyFilePath) {
        InputStream istr = ClassLoaderUtils.getResourceAsStream((String)policyFilePath, HtmlCleaner.class);
        if (istr == null) {
            throw new IllegalArgumentException("The policy resource " + policyFilePath + " was not found on the classpath.");
        }
        try {
            Policy policy = Policy.getInstance(istr);
            this.antiSamy = new AntiSamy(policy);
        }
        catch (PolicyException ex) {
            throw new IllegalArgumentException("The policy resource " + policyFilePath + " for the HtmlCleaner could not be parsed.", ex);
        }
    }

    public String clean(String unclean) {
        CleanResults cleanResults;
        try {
            cleanResults = this.antiSamy.scan(unclean);
        }
        catch (PolicyException e) {
            LOG.warn("Antisamy detected an error in your policy file.", (Object)e.getMessage());
            return "";
        }
        catch (ScanException e) {
            LOG.warn("Antisamy was unable to scan a string.", (Object)e.getMessage());
            return "";
        }
        return cleanResults.getCleanHTML();
    }
}

