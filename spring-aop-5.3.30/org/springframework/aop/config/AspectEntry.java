/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.parsing.ParseState$Entry
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;
import org.springframework.util.StringUtils;

public class AspectEntry
implements ParseState.Entry {
    private final String id;
    private final String ref;

    public AspectEntry(String id, String ref) {
        this.id = id;
        this.ref = ref;
    }

    public String toString() {
        return "Aspect: " + (StringUtils.hasLength((String)this.id) ? "id='" + this.id + "'" : "ref='" + this.ref + "'");
    }
}

