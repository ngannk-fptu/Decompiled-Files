/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.i18n;

import com.atlassian.confluence.util.i18n.DocumentationBean;
import com.atlassian.confluence.util.i18n.I18NBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionSpecificDocumentationBean
implements DocumentationBean {
    private static final String HELP_PREFIX_KEY = "help.prefix";
    private final I18NBean i18NBean;
    private final String versionNumber;
    private static final Pattern VERSION_PATTERN = Pattern.compile("^([0-9]+)\\.([0-9]+)");

    public VersionSpecificDocumentationBean(String versionNumber, I18NBean i18NBean) {
        this.i18NBean = i18NBean;
        this.versionNumber = VersionSpecificDocumentationBean.getVersionSpecificNumber(versionNumber);
    }

    static String getVersionSpecificNumber(String versionNumber) {
        Matcher matcher = VERSION_PATTERN.matcher(versionNumber);
        if (matcher.find()) {
            return matcher.group(1) + matcher.group(2);
        }
        return versionNumber.replaceAll("\\.", "");
    }

    @Override
    public String getLink(String key) {
        String docLink = this.i18NBean.getText(key, Arrays.asList(this.versionNumber));
        if (docLink == null) {
            docLink = this.i18NBean.getText(key, Arrays.asList(this.versionNumber));
        }
        try {
            URI uri = new URI(docLink);
            if (uri.getHost() != null) {
                return docLink;
            }
        }
        catch (URISyntaxException e) {
            return docLink;
        }
        if (docLink.contains("display/DOC")) {
            docLink = docLink.replace("display/DOC", "");
        }
        return this.i18NBean.getText(HELP_PREFIX_KEY, Arrays.asList(this.versionNumber, docLink));
    }

    @Override
    public boolean exists(String docLink) {
        return !this.i18NBean.getText(docLink).equals(docLink);
    }

    @Override
    public String getTitle(String docLink) {
        return null;
    }

    @Override
    public String getAlt(String docLink) {
        return null;
    }

    @Override
    public boolean isLocal(String docLink) {
        return false;
    }
}

