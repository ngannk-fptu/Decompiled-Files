/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.nav.NavigationAware;

@ExperimentalApi
public interface Collapsed
extends NavigationAware {

    public static class Exceptions {
        public static RuntimeException throwCollapsedException(String methodName) {
            throw new IllegalStateException("Cannot call " + methodName + " on collapsed object, ensure the property was included in the expansions on the original service request");
        }
    }
}

