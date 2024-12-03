/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.ext.Provider
 */
package com.atlassian.upm.rest;

import com.atlassian.upm.core.rest.AbstractWebSudoRequiredExceptionMapper;
import com.atlassian.upm.core.rest.representations.BaseRepresentationFactory;
import javax.ws.rs.ext.Provider;

@Provider
public class UpmWebSudoRequiredExceptionMapper
extends AbstractWebSudoRequiredExceptionMapper {
    public UpmWebSudoRequiredExceptionMapper(BaseRepresentationFactory representationFactory) {
        super(representationFactory);
    }

    @Override
    protected String getI18nErrorKey() {
        return "upm.websudo.error";
    }
}

