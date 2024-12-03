/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.sun.jersey.api.NotFoundException
 */
package com.atlassian.confluence.plugins.restapi.resources;

import com.google.common.base.Supplier;
import com.sun.jersey.api.NotFoundException;

public class ResultSuppliers {
    public static Supplier notFoundException(final String msg) {
        return new Supplier(){

            public Object get() {
                throw new NotFoundException(msg);
            }
        };
    }
}

