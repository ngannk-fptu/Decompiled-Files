/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.validation.DefaultMessageHolder;
import com.atlassian.confluence.validation.MessageHolder;
import com.atlassian.confluence.validation.MessageHolderFactory;

public class DefaultMessageHolderFactory
implements MessageHolderFactory {
    @Override
    public MessageHolder newHolder() {
        return new DefaultMessageHolder();
    }
}

