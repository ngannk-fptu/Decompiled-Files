/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.tidy.TidyMessage
 *  org.w3c.tidy.TidyMessageListener
 */
package com.atlassian.renderer.wysiwyg;

import java.util.ArrayList;
import java.util.List;
import org.w3c.tidy.TidyMessage;
import org.w3c.tidy.TidyMessageListener;

public class DefaultTidyMessageListener
implements TidyMessageListener {
    private List messages = new ArrayList();

    public List getMessages() {
        return this.messages;
    }

    public void messageReceived(TidyMessage tidyMessage) {
        this.messages.add(tidyMessage);
    }
}

