/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.internal.http;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.TlsKeyManagersProvider;

@SdkInternalApi
public abstract class AbstractFileStoreTlsKeyManagersProvider
implements TlsKeyManagersProvider {
    protected final KeyManager[] createKeyManagers(Path storePath, String storeType, char[] password) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, UnrecoverableKeyException {
        KeyStore ks = this.createKeyStore(storePath, storeType, password);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password);
        return kmf.getKeyManagers();
    }

    private KeyStore createKeyStore(Path storePath, String storeType, char[] password) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        KeyStore ks = KeyStore.getInstance(storeType);
        try (InputStream storeIs = Files.newInputStream(storePath, new OpenOption[0]);){
            ks.load(storeIs, password);
            KeyStore keyStore = ks;
            return keyStore;
        }
    }
}

