/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.api.model.messages;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface Message<T extends Message>
extends Comparable<T> {
    public String getKey();

    public Object[] getArgs();

    public String getTranslation();

    @Override
    default public int compareTo(T message) {
        if (message == null) {
            return -1;
        }
        if (this.getKey() == null && message.getKey() == null) {
            return -1;
        }
        if (this.getKey() == null && message.getKey() != null) {
            return 1;
        }
        if (this.getKey() != null && message.getKey() == null) {
            return -1;
        }
        return this.getKey().compareTo(message.getKey());
    }
}

