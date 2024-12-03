/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.symmetric.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Destroyable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

public class BCPBEKey
implements PBEKey,
Destroyable {
    private final AtomicBoolean hasBeenDestroyed = new AtomicBoolean(false);
    String algorithm;
    ASN1ObjectIdentifier oid;
    int type;
    int digest;
    int keySize;
    int ivSize;
    private final char[] password;
    private final byte[] salt;
    private final int iterationCount;
    private final CipherParameters param;
    boolean tryWrong = false;

    public BCPBEKey(String algorithm, ASN1ObjectIdentifier oid, int type, int digest, int keySize, int ivSize, PBEKeySpec pbeKeySpec, CipherParameters param) {
        this.algorithm = algorithm;
        this.oid = oid;
        this.type = type;
        this.digest = digest;
        this.keySize = keySize;
        this.ivSize = ivSize;
        this.password = pbeKeySpec.getPassword();
        this.iterationCount = pbeKeySpec.getIterationCount();
        this.salt = pbeKeySpec.getSalt();
        this.param = param;
    }

    public BCPBEKey(String algName, CipherParameters param) {
        this.algorithm = algName;
        this.param = param;
        this.password = null;
        this.iterationCount = -1;
        this.salt = null;
    }

    @Override
    public String getAlgorithm() {
        String rv = this.algorithm;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    @Override
    public String getFormat() {
        BCPBEKey.checkDestroyed(this);
        return "RAW";
    }

    @Override
    public byte[] getEncoded() {
        byte[] enc;
        if (this.param != null) {
            KeyParameter kParam = this.param instanceof ParametersWithIV ? (KeyParameter)((ParametersWithIV)this.param).getParameters() : (KeyParameter)this.param;
            enc = kParam.getKey();
        } else {
            enc = this.type == 2 ? PBEParametersGenerator.PKCS12PasswordToBytes(this.password) : (this.type == 5 ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password) : PBEParametersGenerator.PKCS5PasswordToBytes(this.password));
        }
        BCPBEKey.checkDestroyed(this);
        return enc;
    }

    int getType() {
        int rv = this.type;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    int getDigest() {
        int rv = this.digest;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    int getKeySize() {
        int rv = this.keySize;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    public int getIvSize() {
        int rv = this.ivSize;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    public CipherParameters getParam() {
        CipherParameters rv = this.param;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    @Override
    public char[] getPassword() {
        char[] clone = Arrays.clone(this.password);
        BCPBEKey.checkDestroyed(this);
        if (clone == null) {
            throw new IllegalStateException("no password available");
        }
        return clone;
    }

    @Override
    public byte[] getSalt() {
        byte[] clone = Arrays.clone(this.salt);
        BCPBEKey.checkDestroyed(this);
        return clone;
    }

    @Override
    public int getIterationCount() {
        int rv = this.iterationCount;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    public ASN1ObjectIdentifier getOID() {
        ASN1ObjectIdentifier rv = this.oid;
        BCPBEKey.checkDestroyed(this);
        return rv;
    }

    public void setTryWrongPKCS12Zero(boolean tryWrong) {
        this.tryWrong = tryWrong;
    }

    boolean shouldTryWrongPKCS12() {
        return this.tryWrong;
    }

    @Override
    public void destroy() {
        if (!this.hasBeenDestroyed.getAndSet(true)) {
            if (this.password != null) {
                Arrays.fill(this.password, '\u0000');
            }
            if (this.salt != null) {
                Arrays.fill(this.salt, (byte)0);
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    static void checkDestroyed(Destroyable destroyable) {
        if (destroyable.isDestroyed()) {
            throw new IllegalStateException("key has been destroyed");
        }
    }
}

