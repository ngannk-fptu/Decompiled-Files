/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.core.annotation.Order
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.validator;

import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component(value="targetSpaceKeyExistenceValidator")
@Order(value=10)
public class TargetSpaceKeyExistenceValidator
implements Consumer<CopySpaceRequest> {
    private final SpaceService spaceService;

    @Autowired
    public TargetSpaceKeyExistenceValidator(@ComponentImport(value="apiSpaceService") SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @Override
    public void accept(CopySpaceRequest request) {
        Space newSpace = Space.builder().key(request.getNewKey()).name(request.getNewName()).build();
        ValidationResult validationResult = this.spaceService.validator().validateCreate(newSpace, true);
        if (!validationResult.isValid()) {
            StringBuilder errorMessageBuilder = new StringBuilder();
            validationResult.getErrors().forEach(error -> errorMessageBuilder.append(error.getMessage().getTranslation()).append(" "));
            throw new BadRequestException(errorMessageBuilder.toString().trim());
        }
    }
}

