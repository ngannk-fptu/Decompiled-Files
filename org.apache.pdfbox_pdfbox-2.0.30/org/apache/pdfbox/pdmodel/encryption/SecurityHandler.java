/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.encryption;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.MessageDigests;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.RC4Cipher;
import org.apache.pdfbox.util.Charsets;

public abstract class SecurityHandler {
    private static final Log LOG = LogFactory.getLog(SecurityHandler.class);
    private static final short DEFAULT_KEY_LENGTH = 40;
    private static final byte[] AES_SALT = new byte[]{115, 65, 108, 84};
    protected short keyLength = (short)40;
    protected byte[] encryptionKey;
    private final RC4Cipher rc4 = new RC4Cipher();
    private boolean decryptMetadata;
    private SecureRandom customSecureRandom;
    private final Set<COSBase> objects = Collections.newSetFromMap(new IdentityHashMap());
    private boolean useAES;
    private ProtectionPolicy protectionPolicy = null;
    private AccessPermission currentAccessPermission = null;
    private COSName streamFilterName;
    private COSName stringFilterName;

    protected void setDecryptMetadata(boolean decryptMetadata) {
        this.decryptMetadata = decryptMetadata;
    }

    public boolean isDecryptMetadata() {
        return this.decryptMetadata;
    }

    protected void setStringFilterName(COSName stringFilterName) {
        this.stringFilterName = stringFilterName;
    }

    protected void setStreamFilterName(COSName streamFilterName) {
        this.streamFilterName = streamFilterName;
    }

    public void setCustomSecureRandom(SecureRandom customSecureRandom) {
        this.customSecureRandom = customSecureRandom;
    }

    public abstract void prepareDocumentForEncryption(PDDocument var1) throws IOException;

    public abstract void prepareForDecryption(PDEncryption var1, COSArray var2, DecryptionMaterial var3) throws IOException;

    private void encryptData(long objectNumber, long genNumber, InputStream data, OutputStream output, boolean decrypt) throws IOException {
        if (this.useAES && this.encryptionKey.length == 32) {
            this.encryptDataAES256(data, output, decrypt);
        } else {
            byte[] finalKey = this.calcFinalKey(objectNumber, genNumber);
            if (this.useAES) {
                this.encryptDataAESother(finalKey, data, output, decrypt);
            } else {
                this.encryptDataRC4(finalKey, data, output);
            }
        }
        output.flush();
    }

    private byte[] calcFinalKey(long objectNumber, long genNumber) {
        byte[] newKey = new byte[this.encryptionKey.length + 5];
        System.arraycopy(this.encryptionKey, 0, newKey, 0, this.encryptionKey.length);
        newKey[newKey.length - 5] = (byte)(objectNumber & 0xFFL);
        newKey[newKey.length - 4] = (byte)(objectNumber >> 8 & 0xFFL);
        newKey[newKey.length - 3] = (byte)(objectNumber >> 16 & 0xFFL);
        newKey[newKey.length - 2] = (byte)(genNumber & 0xFFL);
        newKey[newKey.length - 1] = (byte)(genNumber >> 8 & 0xFFL);
        MessageDigest md = MessageDigests.getMD5();
        md.update(newKey);
        if (this.useAES) {
            md.update(AES_SALT);
        }
        byte[] digestedKey = md.digest();
        int length = Math.min(newKey.length, 16);
        byte[] finalKey = new byte[length];
        System.arraycopy(digestedKey, 0, finalKey, 0, length);
        return finalKey;
    }

    protected void encryptDataRC4(byte[] finalKey, InputStream input, OutputStream output) throws IOException {
        this.rc4.setKey(finalKey);
        this.rc4.write(input, output);
    }

    protected void encryptDataRC4(byte[] finalKey, byte[] input, OutputStream output) throws IOException {
        this.rc4.setKey(finalKey);
        this.rc4.write(input, output);
    }

    private void encryptDataAESother(byte[] finalKey, InputStream data, OutputStream output, boolean decrypt) throws IOException {
        byte[] iv = new byte[16];
        if (!this.prepareAESInitializationVector(decrypt, iv, data, output)) {
            return;
        }
        try {
            int n;
            Cipher decryptCipher = this.createCipher(finalKey, iv, decrypt);
            byte[] buffer = new byte[256];
            while ((n = data.read(buffer)) != -1) {
                byte[] dst = decryptCipher.update(buffer, 0, n);
                if (dst == null) continue;
                output.write(dst);
            }
            output.write(decryptCipher.doFinal());
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void encryptDataAES256(InputStream data, OutputStream output, boolean decrypt) throws IOException {
        Cipher cipher;
        byte[] iv = new byte[16];
        if (!this.prepareAESInitializationVector(decrypt, iv, data, output)) {
            return;
        }
        try {
            cipher = this.createCipher(this.encryptionKey, iv, decrypt);
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
        CipherInputStream cis = new CipherInputStream(data, cipher);
        try {
            IOUtils.copy(cis, output);
        }
        catch (IOException exception) {
            if (!(exception.getCause() instanceof GeneralSecurityException)) {
                throw exception;
            }
            LOG.debug((Object)"A GeneralSecurityException occurred when decrypting some stream data", (Throwable)exception);
        }
        finally {
            cis.close();
        }
    }

    private Cipher createCipher(byte[] key, byte[] iv, boolean decrypt) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ips = new IvParameterSpec(iv);
        cipher.init(decrypt ? 2 : 1, (Key)keySpec, ips);
        return cipher;
    }

    private boolean prepareAESInitializationVector(boolean decrypt, byte[] iv, InputStream data, OutputStream output) throws IOException {
        if (decrypt) {
            int ivSize = (int)IOUtils.populateBuffer(data, iv);
            if (ivSize == 0) {
                return false;
            }
            if (ivSize != iv.length) {
                throw new IOException("AES initialization vector not fully read: only " + ivSize + " bytes read instead of " + iv.length);
            }
        } else {
            SecureRandom rnd = this.getSecureRandom();
            rnd.nextBytes(iv);
            output.write(iv);
        }
        return true;
    }

    private SecureRandom getSecureRandom() {
        if (this.customSecureRandom != null) {
            return this.customSecureRandom;
        }
        return new SecureRandom();
    }

    public void decrypt(COSBase obj, long objNum, long genNum) throws IOException {
        if (obj instanceof COSString) {
            if (this.objects.contains(obj)) {
                return;
            }
            this.objects.add(obj);
            this.decryptString((COSString)obj, objNum, genNum);
        } else if (obj instanceof COSStream) {
            if (this.objects.contains(obj)) {
                return;
            }
            this.objects.add(obj);
            this.decryptStream((COSStream)obj, objNum, genNum);
        } else if (obj instanceof COSDictionary) {
            this.decryptDictionary((COSDictionary)obj, objNum, genNum);
        } else if (obj instanceof COSArray) {
            this.decryptArray((COSArray)obj, objNum, genNum);
        }
    }

    public void decryptStream(COSStream stream, long objNum, long genNum) throws IOException {
        if (COSName.IDENTITY.equals(this.streamFilterName)) {
            return;
        }
        COSName type = stream.getCOSName(COSName.TYPE);
        if (!this.decryptMetadata && COSName.METADATA.equals(type)) {
            return;
        }
        if (COSName.XREF.equals(type)) {
            return;
        }
        if (COSName.METADATA.equals(type)) {
            InputStream is = stream.createRawInputStream();
            byte[] buf = new byte[10];
            IOUtils.populateBuffer(is, buf);
            is.close();
            if (Arrays.equals(buf, "<?xpacket ".getBytes(Charsets.ISO_8859_1))) {
                LOG.warn((Object)"Metadata is not encrypted, but was expected to be");
                LOG.warn((Object)"Read PDF specification about EncryptMetadata (default value: true)");
                return;
            }
        }
        this.decryptDictionary(stream, objNum, genNum);
        byte[] encrypted = IOUtils.toByteArray(stream.createRawInputStream());
        ByteArrayInputStream encryptedStream = new ByteArrayInputStream(encrypted);
        OutputStream output = stream.createRawOutputStream();
        try {
            this.encryptData(objNum, genNum, encryptedStream, output, true);
        }
        catch (IOException ex) {
            LOG.error((Object)(ex.getClass().getSimpleName() + " thrown when decrypting object " + objNum + " " + genNum + " obj"));
            throw ex;
        }
        finally {
            output.close();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void encryptStream(COSStream stream, long objNum, int genNum) throws IOException {
        InputStream is = stream.createRawInputStream();
        byte[] rawData = IOUtils.toByteArray(is);
        is.close();
        ByteArrayInputStream encryptedStream = new ByteArrayInputStream(rawData);
        OutputStream output = stream.createRawOutputStream();
        try {
            this.encryptData(objNum, genNum, encryptedStream, output, false);
        }
        finally {
            output.close();
        }
    }

    private void decryptDictionary(COSDictionary dictionary, long objNum, long genNum) throws IOException {
        if (dictionary.getItem(COSName.CF) != null) {
            return;
        }
        COSBase type = dictionary.getDictionaryObject(COSName.TYPE);
        boolean isSignature = COSName.SIG.equals(type) || COSName.DOC_TIME_STAMP.equals(type) || dictionary.getDictionaryObject(COSName.CONTENTS) instanceof COSString && dictionary.getDictionaryObject(COSName.BYTERANGE) instanceof COSArray;
        for (Map.Entry<COSName, COSBase> entry : dictionary.entrySet()) {
            COSBase value;
            if (isSignature && COSName.CONTENTS.equals(entry.getKey()) || !((value = entry.getValue()) instanceof COSString) && !(value instanceof COSArray) && !(value instanceof COSDictionary)) continue;
            this.decrypt(value, objNum, genNum);
        }
    }

    private void decryptString(COSString string, long objNum, long genNum) {
        if (COSName.IDENTITY.equals(this.stringFilterName)) {
            return;
        }
        ByteArrayInputStream data = new ByteArrayInputStream(string.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            this.encryptData(objNum, genNum, data, outputStream, true);
            string.setValue(outputStream.toByteArray());
        }
        catch (IOException ex) {
            LOG.error((Object)("Failed to decrypt COSString of length " + string.getBytes().length + " in object " + objNum + ": " + ex.getMessage()));
        }
    }

    public void encryptString(COSString string, long objNum, int genNum) throws IOException {
        ByteArrayInputStream data = new ByteArrayInputStream(string.getBytes());
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        this.encryptData(objNum, genNum, data, buffer, false);
        string.setValue(buffer.toByteArray());
    }

    private void decryptArray(COSArray array, long objNum, long genNum) throws IOException {
        for (int i = 0; i < array.size(); ++i) {
            this.decrypt(array.get(i), objNum, genNum);
        }
    }

    public int getKeyLength() {
        return this.keyLength;
    }

    public void setKeyLength(int keyLen) {
        this.keyLength = (short)keyLen;
    }

    public void setCurrentAccessPermission(AccessPermission currentAccessPermission) {
        this.currentAccessPermission = currentAccessPermission;
    }

    public AccessPermission getCurrentAccessPermission() {
        return this.currentAccessPermission;
    }

    public boolean isAES() {
        return this.useAES;
    }

    public void setAES(boolean aesValue) {
        this.useAES = aesValue;
    }

    public boolean hasProtectionPolicy() {
        return this.protectionPolicy != null;
    }

    protected ProtectionPolicy getProtectionPolicy() {
        return this.protectionPolicy;
    }

    protected void setProtectionPolicy(ProtectionPolicy protectionPolicy) {
        this.protectionPolicy = protectionPolicy;
    }

    public byte[] getEncryptionKey() {
        return this.encryptionKey;
    }

    public void setEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    protected int computeVersionNumber() {
        if (this.keyLength == 40) {
            return 1;
        }
        if (this.keyLength == 128 && this.protectionPolicy.isPreferAES()) {
            return 4;
        }
        if (this.keyLength == 256) {
            return 5;
        }
        return 2;
    }
}

