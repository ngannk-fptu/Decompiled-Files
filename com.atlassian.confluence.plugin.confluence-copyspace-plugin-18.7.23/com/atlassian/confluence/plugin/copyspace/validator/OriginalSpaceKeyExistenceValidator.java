/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="originalSpaceKeyExistenceValidator")
@Order(value=4)
public class OriginalSpaceKeyExistenceValidator
implements Consumer<CopySpaceRequest> {
    private final SpaceService spaceService;
    private final I18NBeanProvider i18NBeanProvider;

    @Autowired
    public OriginalSpaceKeyExistenceValidator(@ComponentImport(value="apiSpaceService") SpaceService spaceService, I18NBeanProvider i18NBeanProvider) {
        this.spaceService = spaceService;
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        Optional space = this.spaceService.find(new Expansion[0]).withKeys(new String[]{request.getOldKey()}).fetch();
        if (!space.isPresent()) {
            throw new BadRequestException(this.i18NBeanProvider.getI18NBean().getText("copyspace.validation.original.space.key.does.not.exist"));
        }
    }
}

