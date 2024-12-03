/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.util.concurrent.LazyReference
 *  com.google.common.collect.Maps
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.digest.DigestUtils
 *  org.apache.shindig.common.crypto.BlobCrypterException
 *  org.apache.shindig.common.crypto.Crypto
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.renderer.internal.BlobCrypterImpl;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.util.concurrent.LazyReference;
import com.google.common.collect.Maps;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.apache.shindig.common.crypto.Crypto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="nonExpirableBlobCrypter")
@ExportAsService
public class NonExpirableBlobCrypterImpl
extends BlobCrypterImpl {
    private static final byte CIPHER_KEY_LABEL = 0;
    private static final byte HMAC_KEY_LABEL = 1;
    public static final int MASTER_KEY_MIN_LEN = 16;
    private static final String UTF8 = "UTF-8";
    private final LazyReference<byte[]> masterKey;
    private final LazyReference<byte[]> cipherKey;
    private final LazyReference<byte[]> hmacKey;

    @Autowired
    public NonExpirableBlobCrypterImpl(final @ComponentImport PluginSettingsFactory factory, @ComponentImport TransactionTemplate txTemplate) {
        super(factory, txTemplate);
        this.masterKey = new LazyReference<byte[]>(){

            protected byte[] create() throws Exception {
                byte[] key = NonExpirableBlobCrypterImpl.this.getKey(factory).getBytes();
                if (key.length < 16) {
                    throw new IllegalArgumentException("Master key needs at least 16 bytes");
                }
                return NonExpirableBlobCrypterImpl.this.getKey(factory).getBytes();
            }
        };
        this.cipherKey = new LazyReference<byte[]>(){

            protected byte[] create() throws Exception {
                return NonExpirableBlobCrypterImpl.this.deriveKey((byte)0, (byte[])NonExpirableBlobCrypterImpl.this.masterKey.get(), 16);
            }
        };
        this.hmacKey = new LazyReference<byte[]>(){

            protected byte[] create() throws Exception {
                return NonExpirableBlobCrypterImpl.this.deriveKey((byte)1, (byte[])NonExpirableBlobCrypterImpl.this.masterKey.get(), 0);
            }
        };
    }

    @Override
    public Map<String, String> unwrap(String in, int maxAgeSec) throws BlobCrypterException {
        try {
            byte[] bin = Base64.decodeBase64((byte[])in.getBytes());
            byte[] hmac = new byte[20];
            byte[] cipherText = new byte[bin.length - 20];
            System.arraycopy(bin, 0, cipherText, 0, cipherText.length);
            System.arraycopy(bin, cipherText.length, hmac, 0, hmac.length);
            Crypto.hmacSha1Verify((byte[])((byte[])this.hmacKey.get()), (byte[])cipherText, (byte[])hmac);
            byte[] plain = Crypto.aes128cbcDecrypt((byte[])((byte[])this.cipherKey.get()), (byte[])cipherText);
            Map<String, String> out = this.deserialize(plain);
            return out;
        }
        catch (GeneralSecurityException e) {
            throw new BlobCrypterException("Invalid token signature", (Throwable)e);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new BlobCrypterException("Invalid token format", (Throwable)e);
        }
        catch (NegativeArraySizeException e) {
            throw new BlobCrypterException("Invalid token format", (Throwable)e);
        }
        catch (UnsupportedEncodingException e) {
            throw new BlobCrypterException((Throwable)e);
        }
    }

    private byte[] deriveKey(byte label, byte[] masterKey, int len) {
        byte[] base = Crypto.concat((byte[])new byte[]{label}, (byte[])masterKey);
        byte[] hash = DigestUtils.sha((byte[])base);
        if (len == 0) {
            return hash;
        }
        byte[] out = new byte[len];
        System.arraycopy(hash, 0, out, 0, out.length);
        return out;
    }

    private Map<String, String> deserialize(byte[] plain) throws UnsupportedEncodingException {
        String base = new String(plain, UTF8);
        String[] items = base.split("[&=]");
        HashMap map = Maps.newHashMapWithExpectedSize((int)items.length);
        int i = 0;
        while (i < items.length) {
            String key = URLDecoder.decode(items[i++], UTF8);
            String val = URLDecoder.decode(items[i++], UTF8);
            map.put(key, val);
        }
        return map;
    }
}

