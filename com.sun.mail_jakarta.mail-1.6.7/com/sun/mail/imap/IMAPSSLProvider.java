/*
 * Decompiled with CFR 0.152.
 */
package com.sun.mail.imap;

import com.sun.mail.imap.IMAPSSLStore;
import com.sun.mail.util.DefaultProvider;
import javax.mail.Provider;

@DefaultProvider
public class IMAPSSLProvider
extends Provider {
    public IMAPSSLProvider() {
        super(Provider.Type.STORE, "imaps", IMAPSSLStore.class.getName(), "Oracle", null);
    }
}

