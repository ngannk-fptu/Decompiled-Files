/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.pop3;

import com.sun.mail.pop3.POP3Store;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class POP3Provider
extends Provider {
    public POP3Provider() {
        super(Provider.Type.STORE, "pop3", POP3Store.class.getName(), "Oracle", null);
    }
}

