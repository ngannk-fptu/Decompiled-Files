/*
 * Decompiled with CFR 0.152.
 */
package com.onelogin.saml2.model;

import java.util.List;

public class RequestedAttribute {
    private final String name;
    private final String friendlyName;
    private final Boolean isRequired;
    private final String nameFormat;
    private final List<String> attributeValues;

    public RequestedAttribute(String name, String friendlyName, Boolean isRequired, String nameFormat, List<String> attributeValues) {
        this.name = name;
        this.friendlyName = friendlyName;
        this.isRequired = isRequired;
        this.nameFormat = nameFormat;
        this.attributeValues = attributeValues;
    }

    public final String getName() {
        return this.name;
    }

    public final String getFriendlyName() {
        return this.friendlyName;
    }

    public final Boolean isRequired() {
        return this.isRequired;
    }

    public final String getNameFormat() {
        return this.nameFormat;
    }

    public final List<String> getAttributeValues() {
        return this.attributeValues;
    }
}

