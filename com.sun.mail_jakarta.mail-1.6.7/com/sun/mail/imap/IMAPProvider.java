/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap;

import com.sun.mail.imap.IMAPStore;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class IMAPProvider
extends Provider {
    public IMAPProvider() {
        super(Provider.Type.STORE, "imap", IMAPStore.class.getName(), "Oracle", null);
    }
}

