/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugin.copyspace.rest.CopySpaceRequest;
import com.atlassian.confluence.plugin.copyspace.service.ValidationService;
import java.util.List;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="validationServiceImpl")
public class ValidationServiceImpl
implements ValidationService {
    private final List<Consumer<CopySpaceRequest>> validationChain;

    @Autowired
    public ValidationServiceImpl(List<Consumer<CopySpaceRequest>> validationChain) {
        this.validationChain = validationChain;
    }

    @Override
    public void validate(CopySpaceRequest request) throws ServiceException {
        ((Consumer)this.validationChain.stream().reduce(Consumer::andThen).get()).accept(request);
    }
}

