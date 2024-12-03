/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.streams.api;

import com.atlassian.streams.api.common.Option;
import java.net.URI;

public interface ActivityObjectType {
    public URI iri();

    public String key();

    public Option<ActivityObjectType> parent();
}

