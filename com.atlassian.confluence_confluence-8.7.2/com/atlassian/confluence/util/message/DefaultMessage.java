/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.confluence.util.message;

import com.atlassian.confluence.util.message.Message;
import com.atlassian.confluence.velocity.htmlsafe.HtmlSafe;
import java.util.UUID;

public class DefaultMessage
implements Message {
    private String id;
    private String text;
    private String cssClass;
    private boolean closable = true;
    private boolean visible = true;

    public DefaultMessage() {
    }

    public DefaultMessage(String text, String cssClass, boolean closable, boolean visible) {
        this(text, cssClass, closable);
        this.visible = visible;
    }

    public DefaultMessage(String id, String text, String cssClass, boolean closable) {
        this(text, cssClass, closable);
        this.id = id;
    }

    public DefaultMessage(String text, String cssClass, boolean closable) {
        this(text, cssClass);
        this.closable = closable;
    }

    public DefaultMessage(String text, String cssClass) {
        this.id = UUID.randomUUID().toString();
        this.text = text;
        this.cssClass = cssClass;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    @HtmlSafe
    public String getText() {
        return this.text;
    }

    @Override
    public String getCssClass() {
        return this.cssClass;
    }

    @Override
    public boolean isClosable() {
        return this.closable;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

