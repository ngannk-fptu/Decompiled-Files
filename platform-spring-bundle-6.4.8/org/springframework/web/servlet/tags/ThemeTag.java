/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.tags;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.web.servlet.tags.MessageTag;

public class ThemeTag
extends MessageTag {
    @Override
    protected MessageSource getMessageSource() {
        return this.getRequestContext().getTheme().getMessageSource();
    }

    @Override
    protected String getNoSuchMessageExceptionDescription(NoSuchMessageException ex) {
        return "Theme '" + this.getRequestContext().getTheme().getName() + "': " + ex.getMessage();
    }
}

