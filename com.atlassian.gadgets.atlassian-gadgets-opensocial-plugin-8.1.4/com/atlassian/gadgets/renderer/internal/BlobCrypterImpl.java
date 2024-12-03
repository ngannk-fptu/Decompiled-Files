/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.security.random.DefaultSecureRandomService
 *  com.atlassian.security.random.SecureRandomService
 *  com.atlassian.util.concurrent.LazyReference
 *  org.apache.shindig.common.crypto.BasicBlobCrypter
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.common.crypto.BlobCrypterException
 *  org.bouncycastle.util.encoders.Base64
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.security.random.DefaultSecureRandomService;
import com.atlassian.security.random.SecureRandomService;
import com.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.Objects;
import org.apache.shindig.common.crypto.BasicBlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="blobCrypter")
@ExportAsService
public class BlobCrypterImpl
implements BlobCrypter {
    private static final String KEY_PREFIX = BlobCrypter.class.getName() + ":";
    private static final int KEY_BYTE_ARRAY_SIZE = 32;
    private final LazyReference<BlobCrypter> crypter;
    private final TransactionTemplate txTemplate;

    @Autowired
    public BlobCrypterImpl(final @ComponentImport PluginSettingsFactory factory, @ComponentImport TransactionTemplate txTemplate) {
        this.txTemplate = txTemplate;
        this.crypter = new LazyReference<BlobCrypter>(){

            protected BlobCrypter create() {
                return new BasicBlobCrypter(BlobCrypterImpl.this.getKey(factory).getBytes());
            }
        };
    }

    protected String getKey(PluginSettingsFactory factory) {
        PluginSettings pluginSettings = factory.createGlobalSettings();
        return (String)this.txTemplate.execute(() -> {
            String key = (String)pluginSettings.get(KEY_PREFIX + "key");
            if (key == null) {
                SecureRandomService randomService = DefaultSecureRandomService.getInstance();
                byte[] keyBytes = new byte[32];
                randomService.nextBytes(keyBytes);
                key = new String(Base64.encode((byte[])keyBytes));
                pluginSettings.put(KEY_PREFIX + "key", (Object)key);
            }
            return key;
        });
    }

    public Map<String, String> unwrap(String paramString, int paramInt) throws BlobCrypterException {
        return ((BlobCrypter)Objects.requireNonNull(this.crypter.get())).unwrap(paramString, paramInt);
    }

    public String wrap(Map<String, String> paramMap) throws BlobCrypterException {
        return ((BlobCrypter)Objects.requireNonNull(this.crypter.get())).wrap(paramMap);
    }
}

