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

@Component(value="targetSpaceKeyLengthValidator")
@Order(value=7)
public class TargetSpaceKeyLengthValidator
implements Consumer<CopySpaceRequest> {
    public static final int MAX_SPACE_KEY_LENGTH = 255;
    private final I18NBeanProvider i18NBeanProvider;

    @Autowired
    public TargetSpaceKeyLengthValidator(I18NBeanProvider i18NBeanProvider) {
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        if (request.getNewKey().length() > 255) {
            throw new BadRequestException(this.i18NBeanProvider.getI18NBean().getText("copyspace.validation.space.key.too.long"));
        }
    }
}

