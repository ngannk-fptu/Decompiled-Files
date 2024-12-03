/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Optional;
import javax.net.ssl.KeyManager;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.internal.http.AbstractFileStoreTlsKeyManagersProvider;
import software.amazon.awssdk.utils.JavaSystemSetting;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.internal.SystemSettingUtils;

@SdkPublicApi
public final class SystemPropertyTlsKeyManagersProvider
extends AbstractFileStoreTlsKeyManagersProvider {
    private static final Logger log = Logger.loggerFor(SystemPropertyTlsKeyManagersProvider.class);

    private SystemPropertyTlsKeyManagersProvider() {
    }

    @Override
    public KeyManager[] keyManagers() {
        return SystemPropertyTlsKeyManagersProvider.getKeyStore().map(p -> {
            Path path = Paths.get(p, new String[0]);
            String type = SystemPropertyTlsKeyManagersProvider.getKeyStoreType();
            char[] password = SystemPropertyTlsKeyManagersProvider.getKeyStorePassword().map(String::toCharArray).orElse(null);
            try {
                return this.createKeyManagers(path, type, password);
            }
            catch (Exception e) {
                log.warn(() -> String.format("Unable to create KeyManagers from %s property value '%s'", JavaSystemSetting.SSL_KEY_STORE.property(), p), e);
                return null;
            }
        }).orElse(null);
    }

    public static SystemPropertyTlsKeyManagersProvider create() {
        return new SystemPropertyTlsKeyManagersProvider();
    }

    private static Optional<String> getKeyStore() {
        return SystemSettingUtils.resolveSetting(JavaSystemSetting.SSL_KEY_STORE);
    }

    private static String getKeyStoreType() {
        return SystemSettingUtils.resolveSetting(JavaSystemSetting.SSL_KEY_STORE_TYPE).orElseGet(KeyStore::getDefaultType);
    }

    private static Optional<String> getKeyStorePassword() {
        return SystemSettingUtils.resolveSetting(JavaSystemSetting.SSL_KEY_STORE_PASSWORD);
    }
}

