/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.context.MessageSource;

public interface MessageSourceAware
extends Aware {
    public void setMessageSource(MessageSource var1);
}

