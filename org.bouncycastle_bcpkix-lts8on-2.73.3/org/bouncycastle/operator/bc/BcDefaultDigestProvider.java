/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers
 *  org.bouncycastle.asn1.gm.GMObjectIdentifiers
 *  org.bouncycastle.asn1.misc.MiscObjectIdentifiers
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.oiw.OIWObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers
 *  org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.crypto.ExtendedDigest
 *  org.bouncycastle.crypto.Xof
 *  org.bouncycastle.crypto.digests.Blake3Digest
 *  org.bouncycastle.crypto.digests.GOST3411Digest
 *  org.bouncycastle.crypto.digests.GOST3411_2012_256Digest
 *  org.bouncycastle.crypto.digests.GOST3411_2012_512Digest
 *  org.bouncycastle.crypto.digests.MD2Digest
 *  org.bouncycastle.crypto.digests.MD4Digest
 *  org.bouncycastle.crypto.digests.MD5Digest
 *  org.bouncycastle.crypto.digests.RIPEMD128Digest
 *  org.bouncycastle.crypto.digests.RIPEMD160Digest
 *  org.bouncycastle.crypto.digests.RIPEMD256Digest
 *  org.bouncycastle.crypto.digests.SHA1Digest
 *  org.bouncycastle.crypto.digests.SHA224Digest
 *  org.bouncycastle.crypto.digests.SHA256Digest
 *  org.bouncycastle.crypto.digests.SHA384Digest
 *  org.bouncycastle.crypto.digests.SHA3Digest
 *  org.bouncycastle.crypto.digests.SHA512Digest
 *  org.bouncycastle.crypto.digests.SHAKEDigest
 *  org.bouncycastle.crypto.digests.SM3Digest
 */
package org.bouncycastle.operator.bc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.Blake3Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.bc.BcDigestProvider;

public class BcDefaultDigestProvider
implements BcDigestProvider {
    private static final Map lookup = BcDefaultDigestProvider.createTable();
    public static final BcDigestProvider INSTANCE = new BcDefaultDigestProvider();

    private static Map createTable() {
        HashMap<ASN1ObjectIdentifier, BcDigestProvider> table = new HashMap<ASN1ObjectIdentifier, BcDigestProvider>();
        table.put(OIWObjectIdentifiers.idSHA1, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA1Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha224, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA224Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA256Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha384, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA384Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA512Digest();
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_224, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(224);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(256);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_384, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(384);
            }
        });
        table.put(NISTObjectIdentifiers.id_sha3_512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHA3Digest(512);
            }
        });
        table.put(NISTObjectIdentifiers.id_shake128, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHAKEDigest(128);
            }
        });
        table.put(NISTObjectIdentifiers.id_shake256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SHAKEDigest(256);
            }
        });
        table.put(NISTObjectIdentifiers.id_shake128_len, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new AdjustedXof((Xof)new SHAKEDigest(128), ASN1Integer.getInstance((Object)digestAlgorithmIdentifier.getParameters()).intValueExact());
            }
        });
        table.put(NISTObjectIdentifiers.id_shake256_len, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new AdjustedXof((Xof)new SHAKEDigest(256), ASN1Integer.getInstance((Object)digestAlgorithmIdentifier.getParameters()).intValueExact());
            }
        });
        table.put(PKCSObjectIdentifiers.md5, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD5Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.md4, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD4Digest();
            }
        });
        table.put(PKCSObjectIdentifiers.md2, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new MD2Digest();
            }
        });
        table.put(CryptoProObjectIdentifiers.gostR3411, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411Digest();
            }
        });
        table.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411_2012_256Digest();
            }
        });
        table.put(RosstandartObjectIdentifiers.id_tc26_gost_3411_12_512, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new GOST3411_2012_512Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd128, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD128Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd160, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD160Digest();
            }
        });
        table.put(TeleTrusTObjectIdentifiers.ripemd256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new RIPEMD256Digest();
            }
        });
        table.put(GMObjectIdentifiers.sm3, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new SM3Digest();
            }
        });
        table.put(MiscObjectIdentifiers.blake3_256, new BcDigestProvider(){

            @Override
            public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) {
                return new Blake3Digest(256);
            }
        });
        return Collections.unmodifiableMap(table);
    }

    private BcDefaultDigestProvider() {
    }

    @Override
    public ExtendedDigest get(AlgorithmIdentifier digestAlgorithmIdentifier) throws OperatorCreationException {
        BcDigestProvider extProv = (BcDigestProvider)lookup.get(digestAlgorithmIdentifier.getAlgorithm());
        if (extProv == null) {
            throw new OperatorCreationException("cannot recognise digest");
        }
        return extProv.get(digestAlgorithmIdentifier);
    }

    private static class AdjustedXof
    implements Xof {
        private final Xof xof;
        private final int length;

        AdjustedXof(Xof xof, int length) {
            this.xof = xof;
            this.length = length;
        }

        public String getAlgorithmName() {
            return this.xof.getAlgorithmName() + "-" + this.length;
        }

        public int getDigestSize() {
            return (this.length + 7) / 8;
        }

        public void update(byte in) {
            this.xof.update(in);
        }

        public void update(byte[] in, int inOff, int len) {
            this.xof.update(in, inOff, len);
        }

        public int doFinal(byte[] out, int outOff) {
            return this.doFinal(out, outOff, this.getDigestSize());
        }

        public void reset() {
            this.xof.reset();
        }

        public int getByteLength() {
            return this.xof.getByteLength();
        }

        public int doFinal(byte[] out, int outOff, int outLen) {
            return this.xof.doFinal(out, outOff, outLen);
        }

        public int doOutput(byte[] out, int outOff, int outLen) {
            return this.xof.doOutput(out, outOff, outLen);
        }
    }
}

