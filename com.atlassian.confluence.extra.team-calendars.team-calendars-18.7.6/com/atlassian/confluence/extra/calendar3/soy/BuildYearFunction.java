/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.soy;

import com.atlassian.confluence.extra.calendar3.util.DefaultBuildInformationManager;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.google.common.collect.ImmutableSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BuildYearFunction
implements SoyServerFunction<String> {
    private final DefaultBuildInformationManager defaultBuildInformationManager;

    @Autowired
    public BuildYearFunction(DefaultBuildInformationManager defaultBuildInformationManager) {
        this.defaultBuildInformationManager = Objects.requireNonNull(defaultBuildInformationManager);
    }

    public String apply(Object ... objects) {
        return String.valueOf(this.defaultBuildInformationManager.getBuildDate().getYear());
    }

    public String getName() {
        return "tcBuildYear";
    }

    public Set<Integer> validArgSizes() {
        return ImmutableSet.of((Object)0);
    }
}

