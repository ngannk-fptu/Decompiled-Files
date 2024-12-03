/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.ConfluenceUtilService;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="licenseExpirationValidator")
@Order(value=1)
public class LicenseExpirationValidator
implements Consumer<CopySpaceRequest> {
    private final UserChecker userChecker;
    private final I18NBeanProvider i18NBeanProvider;
    private final ConfluenceUtilService utilService;

    @Autowired
    public LicenseExpirationValidator(@ComponentImport UserChecker userChecker, I18NBeanProvider i18NBeanProvider, ConfluenceUtilService utilService) {
        this.userChecker = userChecker;
        this.i18NBeanProvider = i18NBeanProvider;
        this.utilService = utilService;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        if (this.utilService.isLicenseExpired() || this.userChecker != null && this.userChecker.hasTooManyUsers()) {
            throw new PermissionException(this.i18NBeanProvider.getI18NBean().getText("copyspace.validation.your.license.has.expired"));
        }
    }
}

