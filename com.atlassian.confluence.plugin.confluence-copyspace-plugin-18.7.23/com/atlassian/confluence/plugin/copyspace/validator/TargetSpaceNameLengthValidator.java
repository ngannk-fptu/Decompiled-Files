/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="targetSpaceNameLengthValidator")
@Order(value=9)
public class TargetSpaceNameLengthValidator
implements Consumer<CopySpaceRequest> {
    private final I18NBeanProvider i18NBeanProvider;

    @Autowired
    public TargetSpaceNameLengthValidator(I18NBeanProvider i18NBeanProvider) {
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        if (request.getNewName().length() > 255) {
            throw new BadRequestException(this.i18NBeanProvider.getI18NBean().getText("copyspace.validation.space.name.too.long"));
        }
    }
}

