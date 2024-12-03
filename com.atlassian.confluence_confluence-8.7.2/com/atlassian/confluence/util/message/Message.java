/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.message;

public interface Message {
    public String getId();

    public String getText();

    public String getCssClass();

    public boolean isClosable();

    public boolean isVisible();
}

