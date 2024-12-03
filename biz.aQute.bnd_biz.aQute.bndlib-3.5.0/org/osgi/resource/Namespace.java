/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.resource;

import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public abstract class Namespace {
    public static final String CAPABILITY_USES_DIRECTIVE = "uses";
    public static final String CAPABILITY_EFFECTIVE_DIRECTIVE = "effective";
    public static final String REQUIREMENT_FILTER_DIRECTIVE = "filter";
    public static final String REQUIREMENT_RESOLUTION_DIRECTIVE = "resolution";
    public static final String RESOLUTION_MANDATORY = "mandatory";
    public static final String RESOLUTION_OPTIONAL = "optional";
    public static final String REQUIREMENT_EFFECTIVE_DIRECTIVE = "effective";
    public static final String EFFECTIVE_RESOLVE = "resolve";
    public static final String EFFECTIVE_ACTIVE = "active";
    public static final String REQUIREMENT_CARDINALITY_DIRECTIVE = "cardinality";
    public static final String CARDINALITY_MULTIPLE = "multiple";
    public static final String CARDINALITY_SINGLE = "single";

    protected Namespace() {
    }
}

