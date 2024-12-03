/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.fugue.Option;

public class OptionGetters {
    public static <T> T getOrThrowNotFound(Option<T> option, String errorMessage) throws NotFoundException {
        if (option.isEmpty()) {
            throw new NotFoundException(errorMessage);
        }
        return (T)option.get();
    }
}

