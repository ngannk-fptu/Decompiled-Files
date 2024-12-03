/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.benryan.conversion;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SandboxConversionFeature {
    private static final boolean DOCUMENT_CONVERSION_SANDBOX_DISABLE = Boolean.getBoolean("document.conversion.sandbox.disable");
    private final LicenseService licenseService;

    @Autowired
    public SandboxConversionFeature(@ComponentImport LicenseService licenseService) {
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    public Boolean isEnable() {
        return this.licenseService.isLicensedForDataCenterOrExempt() && !DOCUMENT_CONVERSION_SANDBOX_DISABLE;
    }
}

