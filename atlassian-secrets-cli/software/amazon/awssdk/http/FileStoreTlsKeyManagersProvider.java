/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.nio.file.Path;
import javax.net.ssl.KeyManager;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.internal.http.AbstractFileStoreTlsKeyManagersProvider;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.Validate;

@SdkPublicApi
public final class FileStoreTlsKeyManagersProvider
extends AbstractFileStoreTlsKeyManagersProvider {
    private static final Logger log = Logger.loggerFor(FileStoreTlsKeyManagersProvider.class);
    private final Path storePath;
    private final String storeType;
    private final char[] password;

    private FileStoreTlsKeyManagersProvider(Path storePath, String storeType, char[] password) {
        this.storePath = Validate.paramNotNull(storePath, "storePath");
        this.storeType = Validate.paramNotBlank(storeType, "storeType");
        this.password = password;
    }

    @Override
    public KeyManager[] keyManagers() {
        try {
            return this.createKeyManagers(this.storePath, this.storeType, this.password);
        }
        catch (Exception e) {
            log.warn(() -> String.format("Unable to create KeyManagers from file %s", this.storePath), e);
            return null;
        }
    }

    public static FileStoreTlsKeyManagersProvider create(Path path, String type, String password) {
        char[] passwordChars = password != null ? password.toCharArray() : null;
        return new FileStoreTlsKeyManagersProvider(path, type, passwordChars);
    }
}

