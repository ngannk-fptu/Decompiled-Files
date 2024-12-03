/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BuildInformation
 */
package com.atlassian.confluence.plugins.whatsnew.service;

import com.atlassian.confluence.plugins.whatsnew.service.BuildInformationService;
import com.atlassian.confluence.setup.BuildInformation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildInformationServiceImpl
implements BuildInformationService {
    private static final Pattern MAJOR_VERSION_PATTERN = Pattern.compile("^(\\d+[.]\\d+).*");

    @Override
    public String getVersionNumber() {
        return BuildInformation.INSTANCE.getVersionNumber();
    }

    @Override
    public String getMajorVersion() {
        String fullVersion = BuildInformation.INSTANCE.getVersionNumber();
        Matcher matcher = MAJOR_VERSION_PATTERN.matcher(fullVersion);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return fullVersion;
    }
}

