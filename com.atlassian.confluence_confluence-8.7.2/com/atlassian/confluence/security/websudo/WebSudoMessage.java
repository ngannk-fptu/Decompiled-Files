/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.websudo;

import com.atlassian.confluence.util.message.DefaultMessage;

public class WebSudoMessage
extends DefaultMessage {
    public static final String ID = "websudo-message";

    public WebSudoMessage(String text) {
        super(text, "noteMessage", false);
    }

    @Override
    public String getId() {
        return ID;
    }
}

