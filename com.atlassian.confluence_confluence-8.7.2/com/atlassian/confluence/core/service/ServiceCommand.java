/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ValidationError;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

@Transactional(noRollbackFor={NotValidException.class, NotAuthorizedException.class})
public interface ServiceCommand {
    @Transactional(readOnly=true, noRollbackFor={NotValidException.class, NotAuthorizedException.class})
    public boolean isValid();

    public Collection<ValidationError> getValidationErrors();

    @Transactional(readOnly=true, noRollbackFor={NotValidException.class, NotAuthorizedException.class})
    public boolean isAuthorized();

    public void execute();
}

