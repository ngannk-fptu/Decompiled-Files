/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.PropertyImpl
 */
package com.atlassian.crowd.search.query.entity.restriction.constants;

import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.PropertyImpl;

public class AliasTermKeys {
    public static final Property<Long> APPLICATION_ID = new PropertyImpl("applicationId", Long.class);
    public static final Property<String> ALIAS = new PropertyImpl("alias", String.class);
}

