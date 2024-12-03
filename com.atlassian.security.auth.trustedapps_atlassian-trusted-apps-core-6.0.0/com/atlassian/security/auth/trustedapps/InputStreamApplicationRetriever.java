/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.ReaderApplicationRetriever;
import java.io.InputStream;
import java.io.InputStreamReader;

public class InputStreamApplicationRetriever
implements ApplicationRetriever {
    private final InputStream in;
    private final EncryptionProvider encryptionProvider;

    public InputStreamApplicationRetriever(InputStream in, EncryptionProvider encryptionProvider) {
        this.in = in;
        this.encryptionProvider = encryptionProvider;
    }

    @Override
    public Application getApplication() throws ApplicationRetriever.RetrievalException {
        InputStreamReader reader = new InputStreamReader(this.in);
        ReaderApplicationRetriever retriever = new ReaderApplicationRetriever(reader, this.encryptionProvider);
        return retriever.getApplication();
    }
}

