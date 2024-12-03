/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.filter.EqualsFilter
 */
package com.atlassian.crowd.search.ldap.filter;

import com.atlassian.crowd.directory.ldap.util.GuidHelper;
import org.springframework.ldap.filter.EqualsFilter;

public class EqualsExternalIdFilter
extends EqualsFilter {
    public EqualsExternalIdFilter(String attribute, String value) {
        super(attribute, value);
    }

    protected String encodeValue(String value) {
        return GuidHelper.encodeGUIDForSearch(value);
    }
}

