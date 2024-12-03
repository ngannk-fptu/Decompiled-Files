/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum ComponentScope {
    Singleton,
    PerRequest,
    Undefined;

    public static final List<ComponentScope> UNDEFINED_SINGLETON;
    public static final List<ComponentScope> PERREQUEST_UNDEFINED_SINGLETON;
    public static final List<ComponentScope> PERREQUEST_UNDEFINED;

    static {
        UNDEFINED_SINGLETON = Collections.unmodifiableList(Arrays.asList(Undefined, Singleton));
        PERREQUEST_UNDEFINED_SINGLETON = Collections.unmodifiableList(Arrays.asList(PerRequest, Undefined, Singleton));
        PERREQUEST_UNDEFINED = Collections.unmodifiableList(Arrays.asList(PerRequest, Undefined));
    }
}

