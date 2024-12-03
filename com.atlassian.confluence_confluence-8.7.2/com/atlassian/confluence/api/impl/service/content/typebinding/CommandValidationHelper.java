/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.google.common.base.Preconditions
 */
package com.atlassian.confluence.api.impl.service.content.typebinding;

import com.atlassian.confluence.api.impl.model.validation.CoreValidationResultFactory;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.google.common.base.Preconditions;

class CommandValidationHelper {
    CommandValidationHelper() {
    }

    public static SimpleValidationResult.Builder validateCommand(ServiceCommand command) {
        Preconditions.checkNotNull((Object)command);
        return new SimpleValidationResult.Builder().authorized(command.isAuthorized()).addErrors(CoreValidationResultFactory.convertCoreErrorsToApiErrors(command.getValidationErrors()));
    }
}

