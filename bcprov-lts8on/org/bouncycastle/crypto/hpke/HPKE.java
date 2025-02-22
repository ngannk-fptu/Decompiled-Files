/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.hpke;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.hpke.AEAD;
import org.bouncycastle.crypto.hpke.DHKEM;
import org.bouncycastle.crypto.hpke.HKDF;
import org.bouncycastle.crypto.hpke.HPKEContext;
import org.bouncycastle.crypto.hpke.HPKEContextWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;
import org.bouncycastle.util.Strings;

public class HPKE {
    public static final byte mode_base = 0;
    public static final byte mode_psk = 1;
    public static final byte mode_auth = 2;
    public static final byte mode_auth_psk = 3;
    public static final short kem_P256_SHA256 = 16;
    public static final short kem_P384_SHA348 = 17;
    public static final short kem_P521_SHA512 = 18;
    public static final short kem_X25519_SHA256 = 32;
    public static final short kem_X448_SHA512 = 33;
    public static final short kdf_HKDF_SHA256 = 1;
    public static final short kdf_HKDF_SHA384 = 2;
    public static final short kdf_HKDF_SHA512 = 3;
    public static final short aead_AES_GCM128 = 1;
    public static final short aead_AES_GCM256 = 2;
    public static final short aead_CHACHA20_POLY1305 = 3;
    public static final short aead_EXPORT_ONLY = -1;
    private final byte[] default_psk = null;
    private final byte[] default_psk_id = null;
    private final byte mode;
    private final short kemId;
    private final short kdfId;
    private final short aeadId;
    private final DHKEM dhkem;
    private final HKDF hkdf;
    short Nk;

    public HPKE(byte mode, short kemId, short kdfId, short aeadId) {
        this.mode = mode;
        this.kemId = kemId;
        this.kdfId = kdfId;
        this.aeadId = aeadId;
        this.hkdf = new HKDF(kdfId);
        this.dhkem = new DHKEM(kemId);
        this.Nk = aeadId == 1 ? (short)16 : (short)32;
    }

    private void VerifyPSKInputs(byte mode, byte[] psk, byte[] pskid) {
        boolean got_psk_id;
        boolean got_psk = !Arrays.areEqual(psk, this.default_psk);
        boolean bl = got_psk_id = !Arrays.areEqual(pskid, this.default_psk_id);
        if (got_psk != got_psk_id) {
            throw new IllegalArgumentException("Inconsistent PSK inputs");
        }
        if (got_psk && mode % 2 == 0) {
            throw new IllegalArgumentException("PSK input provided when not needed");
        }
        if (!got_psk && mode % 2 == 1) {
            throw new IllegalArgumentException("Missing required PSK input");
        }
    }

    private HPKEContext keySchedule(byte mode, byte[] sharedSecret, byte[] info, byte[] psk, byte[] pskid) {
        this.VerifyPSKInputs(mode, psk, pskid);
        byte[] suiteId = Arrays.concatenate(Strings.toByteArray("HPKE"), Pack.shortToBigEndian(this.kemId), Pack.shortToBigEndian(this.kdfId), Pack.shortToBigEndian(this.aeadId));
        byte[] pskidHash = this.hkdf.LabeledExtract(null, suiteId, "psk_id_hash", pskid);
        byte[] infoHash = this.hkdf.LabeledExtract(null, suiteId, "info_hash", info);
        byte[] modeArray = new byte[]{mode};
        byte[] keyScheduleContext = Arrays.concatenate(modeArray, pskidHash, infoHash);
        byte[] secret = this.hkdf.LabeledExtract(sharedSecret, suiteId, "secret", psk);
        byte[] key = this.hkdf.LabeledExpand(secret, suiteId, "key", keyScheduleContext, this.Nk);
        byte[] base_nonce = this.hkdf.LabeledExpand(secret, suiteId, "base_nonce", keyScheduleContext, 12);
        byte[] exporter_secret = this.hkdf.LabeledExpand(secret, suiteId, "exp", keyScheduleContext, this.hkdf.getHashSize());
        return new HPKEContext(new AEAD(this.aeadId, key, base_nonce), this.hkdf, exporter_secret, suiteId);
    }

    public AsymmetricCipherKeyPair generatePrivateKey() {
        return this.dhkem.GeneratePrivateKey();
    }

    public byte[] serializePublicKey(AsymmetricKeyParameter pk) {
        return this.dhkem.SerializePublicKey(pk);
    }

    public byte[] serializePrivateKey(AsymmetricKeyParameter sk) {
        return this.dhkem.SerializePrivateKey(sk);
    }

    public AsymmetricKeyParameter deserializePublicKey(byte[] pkEncoded) {
        return this.dhkem.DeserializePublicKey(pkEncoded);
    }

    public AsymmetricCipherKeyPair deserializePrivateKey(byte[] skEncoded, byte[] pkEncoded) {
        return this.dhkem.DeserializePrivateKey(skEncoded, pkEncoded);
    }

    public AsymmetricCipherKeyPair deriveKeyPair(byte[] ikm) {
        return this.dhkem.DeriveKeyPair(ikm);
    }

    public byte[][] sendExport(AsymmetricKeyParameter pkR, byte[] info, byte[] exporterContext, int L, byte[] psk, byte[] pskId, AsymmetricCipherKeyPair skS) {
        HPKEContextWithEncapsulation ctx;
        byte[][] output = new byte[2][];
        switch (this.mode) {
            case 0: {
                ctx = this.setupBaseS(pkR, info);
                break;
            }
            case 2: {
                ctx = this.setupAuthS(pkR, info, skS);
                break;
            }
            case 1: {
                ctx = this.SetupPSKS(pkR, info, psk, pskId);
                break;
            }
            case 3: {
                ctx = this.setupAuthPSKS(pkR, info, psk, pskId, skS);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        output[0] = ctx.encapsulation;
        output[1] = ctx.export(exporterContext, L);
        return output;
    }

    public byte[] receiveExport(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info, byte[] exporterContext, int L, byte[] psk, byte[] pskId, AsymmetricKeyParameter pkS) {
        HPKEContext ctx;
        switch (this.mode) {
            case 0: {
                ctx = this.setupBaseR(enc, skR, info);
                break;
            }
            case 2: {
                ctx = this.setupAuthR(enc, skR, info, pkS);
                break;
            }
            case 1: {
                ctx = this.setupPSKR(enc, skR, info, psk, pskId);
                break;
            }
            case 3: {
                ctx = this.setupAuthPSKR(enc, skR, info, psk, pskId, pkS);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        return ctx.export(exporterContext, L);
    }

    public byte[][] seal(AsymmetricKeyParameter pkR, byte[] info, byte[] aad, byte[] pt, byte[] psk, byte[] pskId, AsymmetricCipherKeyPair skS) throws InvalidCipherTextException {
        HPKEContextWithEncapsulation ctx;
        byte[][] output = new byte[2][];
        switch (this.mode) {
            case 0: {
                ctx = this.setupBaseS(pkR, info);
                break;
            }
            case 2: {
                ctx = this.setupAuthS(pkR, info, skS);
                break;
            }
            case 1: {
                ctx = this.SetupPSKS(pkR, info, psk, pskId);
                break;
            }
            case 3: {
                ctx = this.setupAuthPSKS(pkR, info, psk, pskId, skS);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        output[0] = ctx.seal(aad, pt);
        output[1] = ctx.getEncapsulation();
        return output;
    }

    public byte[] open(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info, byte[] aad, byte[] ct, byte[] psk, byte[] pskId, AsymmetricKeyParameter pkS) throws InvalidCipherTextException {
        HPKEContext ctx;
        switch (this.mode) {
            case 0: {
                ctx = this.setupBaseR(enc, skR, info);
                break;
            }
            case 2: {
                ctx = this.setupAuthR(enc, skR, info, pkS);
                break;
            }
            case 1: {
                ctx = this.setupPSKR(enc, skR, info, psk, pskId);
                break;
            }
            case 3: {
                ctx = this.setupAuthPSKR(enc, skR, info, psk, pskId, pkS);
                break;
            }
            default: {
                throw new IllegalStateException("Unknown mode");
            }
        }
        return ctx.open(aad, ct);
    }

    public HPKEContextWithEncapsulation setupBaseS(AsymmetricKeyParameter pkR, byte[] info) {
        byte[][] output = this.dhkem.Encap(pkR);
        HPKEContext ctx = this.keySchedule((byte)0, output[0], info, this.default_psk, this.default_psk_id);
        return new HPKEContextWithEncapsulation(ctx, output[1]);
    }

    public HPKEContext setupBaseR(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info) {
        byte[] sharedSecret = this.dhkem.Decap(enc, skR);
        return this.keySchedule((byte)0, sharedSecret, info, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation SetupPSKS(AsymmetricKeyParameter pkR, byte[] info, byte[] psk, byte[] psk_id) {
        byte[][] output = this.dhkem.Encap(pkR);
        HPKEContext ctx = this.keySchedule((byte)1, output[0], info, psk, psk_id);
        return new HPKEContextWithEncapsulation(ctx, output[1]);
    }

    public HPKEContext setupPSKR(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info, byte[] psk, byte[] psk_id) {
        byte[] sharedSecret = this.dhkem.Decap(enc, skR);
        return this.keySchedule((byte)1, sharedSecret, info, psk, psk_id);
    }

    public HPKEContextWithEncapsulation setupAuthS(AsymmetricKeyParameter pkR, byte[] info, AsymmetricCipherKeyPair skS) {
        byte[][] output = this.dhkem.AuthEncap(pkR, skS);
        HPKEContext ctx = this.keySchedule((byte)2, output[0], info, this.default_psk, this.default_psk_id);
        return new HPKEContextWithEncapsulation(ctx, output[1]);
    }

    public HPKEContext setupAuthR(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info, AsymmetricKeyParameter pkS) {
        byte[] sharedSecret = this.dhkem.AuthDecap(enc, skR, pkS);
        return this.keySchedule((byte)2, sharedSecret, info, this.default_psk, this.default_psk_id);
    }

    public HPKEContextWithEncapsulation setupAuthPSKS(AsymmetricKeyParameter pkR, byte[] info, byte[] psk, byte[] psk_id, AsymmetricCipherKeyPair skS) {
        byte[][] output = this.dhkem.AuthEncap(pkR, skS);
        HPKEContext ctx = this.keySchedule((byte)3, output[0], info, psk, psk_id);
        return new HPKEContextWithEncapsulation(ctx, output[1]);
    }

    public HPKEContext setupAuthPSKR(byte[] enc, AsymmetricCipherKeyPair skR, byte[] info, byte[] psk, byte[] psk_id, AsymmetricKeyParameter pkS) {
        byte[] sharedSecret = this.dhkem.AuthDecap(enc, skR, pkS);
        return this.keySchedule((byte)3, sharedSecret, info, psk, psk_id);
    }
}

