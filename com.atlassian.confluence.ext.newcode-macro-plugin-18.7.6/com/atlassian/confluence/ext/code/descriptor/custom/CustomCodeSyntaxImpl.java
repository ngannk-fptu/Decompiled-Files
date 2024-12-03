/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.descriptor.custom;

import com.atlassian.confluence.ext.code.descriptor.custom.CustomCodeSyntax;

class CustomCodeSyntaxImpl
implements CustomCodeSyntax {
    private final String resourceKey;
    private final String friendlyName;

    CustomCodeSyntaxImpl(String resourceKey, String friendlyName) {
        this.resourceKey = resourceKey;
        this.friendlyName = friendlyName;
    }

    @Override
    public String getResourceKey() {
        return this.resourceKey;
    }

    @Override
    public String getFriendlyName() {
        return this.friendlyName;
    }
}

