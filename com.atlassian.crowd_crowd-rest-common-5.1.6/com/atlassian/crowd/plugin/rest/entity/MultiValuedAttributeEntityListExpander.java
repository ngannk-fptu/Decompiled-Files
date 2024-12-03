/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander
 */
package com.atlassian.crowd.plugin.rest.entity;

import com.atlassian.crowd.plugin.rest.entity.MultiValuedAttributeEntityList;
import com.atlassian.plugins.rest.common.expand.AbstractRecursiveEntityExpander;

public class MultiValuedAttributeEntityListExpander
extends AbstractRecursiveEntityExpander<MultiValuedAttributeEntityList> {
    protected MultiValuedAttributeEntityList expandInternal(MultiValuedAttributeEntityList entity) {
        return entity;
    }
}

