/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

public interface INIBuilderProperties<T> {
    default public T setCommentLeadingCharsUsedInInput(String separator) {
        return (T)this;
    }

    default public T setSeparatorUsedInInput(String separator) {
        return (T)this;
    }

    public T setSeparatorUsedInOutput(String var1);
}

