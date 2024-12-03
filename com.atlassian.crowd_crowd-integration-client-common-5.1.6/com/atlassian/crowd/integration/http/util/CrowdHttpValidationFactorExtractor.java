/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.integration.http.util;

import com.atlassian.crowd.model.authentication.ValidationFactor;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface CrowdHttpValidationFactorExtractor {
    public List<ValidationFactor> getValidationFactors(HttpServletRequest var1);
}

