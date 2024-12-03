/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.labels.service;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.service.AddLabelsCommand;
import com.atlassian.confluence.labels.service.RemoveLabelCommand;
import com.atlassian.confluence.labels.service.ValidateLabelsCommand;
import com.atlassian.user.User;

public interface LabelsService {
    public AddLabelsCommand newAddLabelCommand(String var1, User var2, long var3);

    public AddLabelsCommand newAddLabelCommand(String var1, User var2, long var3, String var5);

    public ValidateLabelsCommand newValidateLabelCommand(String var1, User var2);

    public RemoveLabelCommand newRemoveLabelCommand(Label var1, User var2, long var3);

    public RemoveLabelCommand newRemoveLabelCommand(Label var1, User var2, long var3, String var5);

    public RemoveLabelCommand newRemoveLabelCommand(String var1, User var2, long var3);

    public RemoveLabelCommand newRemoveLabelCommand(long var1, User var3, long var4);
}

