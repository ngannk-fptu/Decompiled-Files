/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.google.template.soy.shared.internal;

import java.util.List;
import javax.annotation.Nullable;

public final class DirectiveDigest {
    private final String directiveName;
    private final int escapeMapVar;
    private String escapesName;
    private final int matcherVar;
    private String matcherName;
    private final int filterVar;
    private String filterName;
    @Nullable
    final String nonAsciiPrefix;
    private final String innocuousOutput;

    DirectiveDigest(String directiveName, int escapeMapVar, int matcherVar, int filterVar, @Nullable String nonAsciiPrefix, String innocuousOutput) {
        this.directiveName = directiveName;
        this.escapeMapVar = escapeMapVar;
        this.matcherVar = matcherVar;
        this.filterVar = filterVar;
        this.nonAsciiPrefix = nonAsciiPrefix;
        this.innocuousOutput = innocuousOutput;
    }

    public void updateNames(List<String> escapeMapNames, List<String> matcherNames, List<String> filterNames) {
        this.escapesName = this.escapeMapVar >= 0 ? escapeMapNames.get(this.escapeMapVar) : null;
        this.matcherName = this.matcherVar >= 0 ? matcherNames.get(this.matcherVar) : null;
        this.filterName = this.filterVar >= 0 ? filterNames.get(this.filterVar) : null;
    }

    public String getDirectiveName() {
        return this.directiveName;
    }

    public String getEscapesName() {
        return this.escapesName;
    }

    public String getMatcherName() {
        return this.matcherName;
    }

    public String getFilterName() {
        return this.filterName;
    }

    public String getNonAsciiPrefix() {
        return this.nonAsciiPrefix;
    }

    public String getInnocuousOutput() {
        return this.innocuousOutput;
    }
}

