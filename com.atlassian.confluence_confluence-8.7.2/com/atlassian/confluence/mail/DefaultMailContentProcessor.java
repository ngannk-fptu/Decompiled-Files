/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.mail;

import com.atlassian.confluence.mail.MailContentProcessor;
import java.util.List;

public class DefaultMailContentProcessor
implements MailContentProcessor {
    private final List<MailContentProcessor> delegates;

    public DefaultMailContentProcessor(List<MailContentProcessor> delegates) {
        this.delegates = delegates;
    }

    @Override
    public String process(String input) {
        String output = input;
        for (MailContentProcessor delegate : this.delegates) {
            output = delegate.process(output);
        }
        return output;
    }
}

