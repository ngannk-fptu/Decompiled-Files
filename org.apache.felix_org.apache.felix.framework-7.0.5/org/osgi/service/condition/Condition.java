/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.service.condition;

import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.condition.ConditionImpl;

@ConsumerType
public interface Condition {
    public static final String CONDITION_ID = "osgi.condition.id";
    public static final String CONDITION_ID_TRUE = "true";
    public static final Condition INSTANCE = new ConditionImpl();
}

