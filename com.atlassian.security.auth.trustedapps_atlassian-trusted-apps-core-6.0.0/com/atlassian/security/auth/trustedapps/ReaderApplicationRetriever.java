/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.ListApplicationRetriever;
import com.atlassian.security.auth.trustedapps.Null;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReaderApplicationRetriever
implements ApplicationRetriever {
    private final ListApplicationRetriever delegate;

    public ReaderApplicationRetriever(Reader reader, EncryptionProvider encryptionProvider) {
        Null.not("reader", reader);
        Null.not("encryptionProvider", encryptionProvider);
        this.delegate = new ListApplicationRetriever(encryptionProvider, this.extract(reader));
    }

    @Override
    public Application getApplication() throws ApplicationRetriever.RetrievalException {
        return this.delegate.getApplication();
    }

    private List<String> extract(Reader r) {
        BufferedReader reader = new BufferedReader(r);
        ArrayList<String> result = new ArrayList<String>();
        try {
            String str = reader.readLine();
            while (str != null) {
                String line = str.trim();
                if (line.length() > 0) {
                    result.add(line);
                }
                str = reader.readLine();
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Collections.unmodifiableList(result);
    }
}

