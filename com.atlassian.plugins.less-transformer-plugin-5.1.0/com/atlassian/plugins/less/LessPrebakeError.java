/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webresource.api.assembler.resource.PrebakeError
 */
package com.atlassian.plugins.less;

import com.atlassian.webresource.api.assembler.resource.PrebakeError;
import java.util.List;

public class LessPrebakeError
implements PrebakeError {
    private final String location;
    private final List<PrebakeError> errors;

    public LessPrebakeError(String location, List<PrebakeError> errors) {
        this.location = location;
        this.errors = errors;
    }

    public String toString() {
        return "LessPrebakeError{location='" + this.location + '\'' + ", errors=" + this.errors + '}';
    }
}

