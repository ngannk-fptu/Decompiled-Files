/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects;

import java.util.Set;
import net.java.ao.RawEntity;
import net.java.ao.schema.NameConverters;

public interface EntitiesValidator {
    public Set<Class<? extends RawEntity<?>>> check(Set<Class<? extends RawEntity<?>>> var1, NameConverters var2);
}

