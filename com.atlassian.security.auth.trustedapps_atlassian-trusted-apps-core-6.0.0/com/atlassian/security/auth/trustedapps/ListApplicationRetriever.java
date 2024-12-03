/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;
import com.atlassian.security.auth.trustedapps.ApplicationRetriever;
import com.atlassian.security.auth.trustedapps.EncryptionProvider;
import com.atlassian.security.auth.trustedapps.InvalidCertificateException;
import com.atlassian.security.auth.trustedapps.Null;
import com.atlassian.security.auth.trustedapps.SimpleApplication;
import com.atlassian.security.auth.trustedapps.Transcoder;
import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TransportException;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class ListApplicationRetriever
implements ApplicationRetriever {
    private final List<String> values;
    private final EncryptionProvider encryptionProvider;
    private final Transcoder transcoder = new Transcoder.Base64Transcoder();

    ListApplicationRetriever(EncryptionProvider encryptionProvider, List<String> values) {
        Null.not("encryptionProvider", encryptionProvider);
        Null.not("values", values);
        int i = 0;
        for (String element : values) {
            Null.not("value: " + i++, element);
        }
        this.encryptionProvider = encryptionProvider;
        this.values = new ArrayList<String>(values);
    }

    @Override
    public Application getApplication() throws ApplicationRetriever.RetrievalException {
        if (this.values.size() < 2) {
            TransportErrorMessage error = new TransportErrorMessage(TransportErrorMessage.Code.MISSING_CERT, "\"Application Certificate too small.\" Values found: [" + this.values.size() + "] ." + this.values);
            TransportException cause = new TransportException(error){};
            throw new ApplicationRetriever.ApplicationNotFoundException(cause);
        }
        if (this.values.size() == 2) {
            return this.getApplicationProtocolV0();
        }
        return this.getApplicationProtocolV1();
    }

    private Application getApplicationProtocolV1() throws ApplicationRetriever.RetrievalException {
        Application result = this.getApplicationProtocolV0();
        String protocol = this.values.get(2);
        String magic = this.values.get(3);
        try {
            Integer protocolVersion = ListApplicationRetriever.isBlank(protocol) ? null : Integer.valueOf(protocol);
            try {
                TrustedApplicationUtils.validateMagicNumber("application details", result.getID(), protocolVersion, magic);
            }
            catch (InvalidCertificateException e) {
                throw new ApplicationRetriever.InvalidApplicationDetailsException(e);
            }
        }
        catch (NumberFormatException e) {
            throw new ApplicationRetriever.InvalidApplicationDetailsException(e);
        }
        return result;
    }

    private Application getApplicationProtocolV0() throws ApplicationRetriever.RetrievalException {
        try {
            String id = this.values.get(0);
            String keyStr = this.values.get(1);
            if (keyStr == null) {
                throw new ApplicationRetriever.ApplicationNotFoundException("Public Key not found");
            }
            byte[] data = this.transcoder.decode(keyStr);
            PublicKey key = this.encryptionProvider.toPublicKey(data);
            return new SimpleApplication(id, key);
        }
        catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isBlank(String input) {
        return input == null || input.trim().length() == 0;
    }
}

