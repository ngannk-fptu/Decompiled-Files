/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="readOnlyModeValidator")
@Order(value=2)
public class ReadOnlyModeValidator
implements Consumer<CopySpaceRequest> {
    private final AccessModeService accessModeService;
    private final I18NBeanProvider i18NBeanProvider;

    @Autowired
    public ReadOnlyModeValidator(@ComponentImport AccessModeService accessModeService, I18NBeanProvider i18NBeanProvider) {
        this.accessModeService = accessModeService;
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            throw new ReadOnlyException(this.i18NBeanProvider.getI18NBean().getText("read.only.mode.default.error.short.message"));
        }
    }
}

