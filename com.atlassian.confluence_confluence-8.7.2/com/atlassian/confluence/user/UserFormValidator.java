/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.user;

import com.atlassian.confluence.user.UserForm;
import com.atlassian.confluence.validation.MessageHolder;

public interface UserFormValidator {
    public MessageHolder validateNewUser(UserForm var1, MessageHolder var2);

    public MessageHolder validateNewUserBySignup(UserForm var1, MessageHolder var2);

    public MessageHolder validateEditUser(UserForm var1, MessageHolder var2);

    public MessageHolder validateEditUserAllowRename(UserForm var1, MessageHolder var2);
}

