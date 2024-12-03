/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.integration.http.util;

import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.model.authentication.ValidationFactor;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public class CrowdHttpValidationFactorExtractorImpl
implements CrowdHttpValidationFactorExtractor {
    private static final CrowdHttpValidationFactorExtractor INSTANCE = new CrowdHttpValidationFactorExtractorImpl();
    private static final Pattern ZONE = Pattern.compile("%[\\w\\d]+$");

    private CrowdHttpValidationFactorExtractorImpl() {
    }

    @Override
    public List<ValidationFactor> getValidationFactors(HttpServletRequest request) {
        ArrayList<ValidationFactor> validationFactors = new ArrayList<ValidationFactor>();
        validationFactors.add(new ValidationFactor("remote_address", request.getRemoteAddr()));
        String remoteAddressXForwardFor = request.getHeader("X-Forwarded-For");
        if (remoteAddressXForwardFor != null && !remoteAddressXForwardFor.equals(request.getRemoteAddr())) {
            validationFactors.add(new ValidationFactor("X-Forwarded-For", remoteAddressXForwardFor));
        }
        return validationFactors;
    }

    public static CrowdHttpValidationFactorExtractor getInstance() {
        return INSTANCE;
    }

    static String remoteAddrWithoutIpv6ZoneId(String remoteAddr) {
        Matcher m;
        if (remoteAddr != null && (m = ZONE.matcher(remoteAddr)).find()) {
            return remoteAddr.substring(0, m.start());
        }
        return remoteAddr;
    }
}

