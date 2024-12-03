/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.Validateable
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.validation.MessageHolder;
import com.opensymphony.xwork2.Validateable;

public interface MessageHolderAware
extends Validateable {
    public void validate();

    public void setMessageHolder(MessageHolder var1);

    public MessageHolder getMessageHolder();
}

