/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ClassLoaderUtils
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  org.apache.commons.lang3.StringUtils
 *  org.owasp.validator.html.AntiSamy
 *  org.owasp.validator.html.CleanResults
 *  org.owasp.validator.html.Policy
 *  org.owasp.validator.html.PolicyException
 *  org.owasp.validator.html.ScanException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.RenderedContentCleaner;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.StorageFormatCleaner;
import com.atlassian.confluence.content.render.xhtml.XhtmlCleaner;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.core.util.ClassLoaderUtils;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class PolicyConfiguredCleaner
implements StorageFormatCleaner,
RenderedContentCleaner,
XhtmlCleaner {
    private static final Logger log = LoggerFactory.getLogger(PolicyConfiguredCleaner.class);
    private static final MessageFormat CSS_CLEANING_FORMAT = new MessageFormat("<span style=\"{0}\">placeholder span</span>");
    private static final Pattern CSS_CLEANING_EXTRACT_PATTERN = Pattern.compile("style=\"([^\"]*)\"");
    private static final MessageFormat URL_CHECKING_FORMAT = new MessageFormat("<a href=\"{0}\">placeholder link</a>");
    private static final Pattern URL_CHECKING_EXTRACT_PATTERN = Pattern.compile("href=\"([^\"]*)\"");
    private final AntiSamy cleaner;

    private PolicyConfiguredCleaner(Policy policy) {
        this.cleaner = new AntiSamy(policy);
    }

    public static RenderedContentCleaner createRenderedContentCleaner() throws PolicyException, IOException {
        return PolicyConfiguredCleaner.create((Resource)new ClassPathResource("antisamy-confluence-rendered-content.xml", PolicyConfiguredCleaner.class));
    }

    public static PolicyConfiguredCleaner createStorageFormatCleaner() throws PolicyException, IOException {
        return PolicyConfiguredCleaner.create((Resource)new ClassPathResource("antisamy-confluence-storage.xml", PolicyConfiguredCleaner.class));
    }

    static PolicyConfiguredCleaner create(Resource policyResource) throws IOException, PolicyException {
        Policy policy = Policy.getInstance((URL)policyResource.getURL());
        return new PolicyConfiguredCleaner(policy);
    }

    @Deprecated(forRemoval=true)
    public PolicyConfiguredCleaner(String policyResource) {
        InputStream istr = ClassLoaderUtils.getResourceAsStream((String)policyResource, PolicyConfiguredCleaner.class);
        if (istr == null) {
            throw new IllegalArgumentException("The policy resource " + policyResource + " was not found on the classpath.");
        }
        try {
            Policy policy = Policy.getInstance((InputStream)istr);
            this.cleaner = new AntiSamy(policy);
        }
        catch (PolicyException ex) {
            throw new IllegalArgumentException("The policy resource " + policyResource + " for the PolicyConfiguredCleaner could not be parsed.", ex);
        }
    }

    @Override
    public XhtmlCleaner.Result clean(ContentEntityObject uncleanCeo) {
        return this.clean(uncleanCeo.getBodyAsString());
    }

    @Override
    public StorageFormatCleaner.Result cleanEntity(ContentEntityObject uncleanCeo) {
        return this.cleanEntityBody(uncleanCeo.getBodyAsString());
    }

    @Override
    public String cleanQuietly(ContentEntityObject uncleanCeo) {
        return this.cleanQuietlyWithLogging(uncleanCeo.getBodyAsString());
    }

    @Override
    public String cleanQuietly(String unclean, ConversionContext context) {
        return this.cleanQuietlyWithLogging(unclean);
    }

    @Override
    @HtmlSafe
    public String cleanQuietly(String unclean) {
        return this.cleanQuietlyWithLogging(unclean);
    }

    @Override
    @HtmlSafe
    public String cleanStyleAttribute(String uncleanStyle) {
        if (StringUtils.isBlank((CharSequence)uncleanStyle)) {
            return "";
        }
        String uncleanElement = CSS_CLEANING_FORMAT.format(new String[]{uncleanStyle}, new StringBuffer(), (FieldPosition)null).toString();
        String cleanedElement = this.antiSamyClean(uncleanElement).getCleanedData();
        Matcher matcher = CSS_CLEANING_EXTRACT_PATTERN.matcher(cleanedElement);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    @Override
    public boolean isCleanUrlAttribute(String urlValue) {
        if (StringUtils.isBlank((CharSequence)urlValue)) {
            return true;
        }
        String uncleanAnchor = URL_CHECKING_FORMAT.format(new String[]{urlValue}, new StringBuffer(), (FieldPosition)null).toString();
        String cleanedElement = this.antiSamyClean(uncleanAnchor).getCleanedData();
        Matcher matcher = URL_CHECKING_EXTRACT_PATTERN.matcher(cleanedElement);
        return matcher.find();
    }

    private XhtmlCleaner.Result clean(String content) {
        StorageFormatCleaner.Result result = this.antiSamyClean(content);
        if (StringUtils.isBlank((CharSequence)result.getCleanedData())) {
            result.setCleanedData("");
        }
        return this.toXhtmlCleanerResult(result);
    }

    private XhtmlCleaner.Result toXhtmlCleanerResult(StorageFormatCleaner.Result result) {
        if (result == null) {
            return null;
        }
        XhtmlCleaner.Result xhtmlResult = new XhtmlCleaner.Result();
        xhtmlResult.setCleanedData(result.getCleanedData());
        for (StorageFormatCleaner.AppliedRuleDescription ruleDescription : result.getAppliedRuleDescriptions()) {
            xhtmlResult.addAppliedRuleDescription(this.toXhtmlCleanerRuleDescription(ruleDescription));
        }
        return xhtmlResult;
    }

    private XhtmlCleaner.AppliedRuleDescription toXhtmlCleanerRuleDescription(StorageFormatCleaner.AppliedRuleDescription ruleDescription) {
        return new XhtmlCleaner.AppliedRuleDescription(ruleDescription.getKey(), ruleDescription.getParameters());
    }

    private StorageFormatCleaner.Result cleanEntityBody(String content) {
        StorageFormatCleaner.Result result = this.antiSamyClean(content);
        if (StringUtils.isBlank((CharSequence)result.getCleanedData())) {
            result.setCleanedData("");
        }
        return result;
    }

    private String cleanQuietlyWithLogging(String content) {
        StorageFormatCleaner.Result results = this.cleanEntityBody(content);
        if (log.isDebugEnabled() && !results.getAppliedRuleDescriptions().isEmpty()) {
            log.debug("The supplied HTML required cleaning. See the following log messages for more details.");
            for (StorageFormatCleaner.AppliedRuleDescription desc : results.getAppliedRuleDescriptions()) {
                log.debug(desc.getKey());
            }
        }
        return results.getCleanedData();
    }

    private StorageFormatCleaner.Result antiSamyClean(String unclean) {
        StorageFormatCleaner.Result result = new StorageFormatCleaner.Result();
        if (StringUtils.isBlank((CharSequence)unclean)) {
            result.setCleanedData("");
            return result;
        }
        try {
            CleanResults results = this.cleaner.scan(StaxUtils.stripIllegalControlChars(unclean).toString());
            if (results.getNumberOfErrors() > 0) {
                List errors = results.getErrorMessages();
                for (String error : errors) {
                    result.addAppliedRuleDescription(new StorageFormatCleaner.AppliedRuleDescription(error, Collections.emptyList()));
                }
            }
            result.setCleanedData(results.getCleanHTML());
            return result;
        }
        catch (ScanException ex) {
            throw new RuntimeException(ex);
        }
        catch (PolicyException ex) {
            throw new RuntimeException("The policy file for the PolicyConfiguredCleaner could not be parsed.", ex);
        }
    }
}

