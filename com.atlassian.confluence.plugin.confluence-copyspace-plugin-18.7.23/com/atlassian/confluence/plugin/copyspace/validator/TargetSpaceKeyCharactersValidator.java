/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.spaces.Space
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.confluence.spaces.Space;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="targetSpaceKeyCharactersValidator")
@Order(value=6)
public class TargetSpaceKeyCharactersValidator
implements Consumer<CopySpaceRequest> {
    private final I18NBeanProvider i18NBeanProvider;

    @Autowired
    public TargetSpaceKeyCharactersValidator(I18NBeanProvider i18NBeanProvider) {
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        if (!Space.isValidGlobalSpaceKey((String)request.getNewKey())) {
            throw new BadRequestException(this.i18NBeanProvider.getI18NBean().getText("space.key.invalid"));
        }
    }
}

