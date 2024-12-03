/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.httpclient.methods;

import org.apache.commons.httpclient.methods.EntityEnclosingMethod;

public class PutMethod
extends EntityEnclosingMethod {
    public PutMethod() {
    }

    public PutMethod(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return "PUT";
    }
}

