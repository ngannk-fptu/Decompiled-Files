/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.Response
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln;

import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveValidator;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResource;
import java.net.MalformedURLException;
import java.net.URL;
import javax.annotation.Nonnull;

public enum CveExternalResource implements SavedExternalResource
{
    BAMBOO_CVES("bamboo"),
    CONFLUENCE_CVES("confluence"),
    JIRA_CVES("jira"),
    JSD_CVES("jsd");

    private final URL cachedUrl;
    private final String localFilename;

    private CveExternalResource(String product) {
        String atstUrl = System.getProperty("atst.data.url", "https://atst-data.atl-paas.net");
        try {
            this.cachedUrl = new URL(String.format("%s/healthcheck/cve/%s.json", atstUrl, product));
            this.localFilename = String.format("healthcheck-cve-%s.json", product);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @Nonnull
    public URL getCachedUrl() {
        return this.cachedUrl;
    }

    @Override
    public String getLocalFilename() {
        return this.localFilename;
    }

    @Override
    public String parseResponse(Response response) throws ResponseException {
        String contents = response.getResponseBodyAsString();
        try {
            CveValidator.parseJson(contents);
        }
        catch (RuntimeException e) {
            throw new ResponseException("CVE Data was not in the expected format", (Throwable)e);
        }
        return contents;
    }
}

