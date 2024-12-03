/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.pop3;

import com.sun.mail.pop3.POP3SSLStore;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class POP3SSLProvider
extends Provider {
    public POP3SSLProvider() {
        super(Provider.Type.STORE, "pop3s", POP3SSLStore.class.getName(), "Oracle", null);
    }
}

