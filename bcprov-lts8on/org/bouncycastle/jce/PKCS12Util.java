/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce;

import java.io.IOException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.MacData;
import org.bouncycastle.asn1.pkcs.Pfx;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;

public class PKCS12Util {
    public static byte[] convertToDefiniteLength(byte[] berPKCS12File) throws IOException {
        Pfx pfx = Pfx.getInstance(berPKCS12File);
        return pfx.getEncoded("DER");
    }

    public static byte[] convertToDefiniteLength(byte[] berPKCS12File, char[] passwd, String provider) throws IOException {
        Pfx pfx = Pfx.getInstance(berPKCS12File);
        ContentInfo info = pfx.getAuthSafe();
        ASN1OctetString content = ASN1OctetString.getInstance(info.getContent());
        ASN1Primitive obj = ASN1Primitive.fromByteArray(content.getOctets());
        byte[] derEncoding = obj.getEncoded("DER");
        info = new ContentInfo(info.getContentType(), new DEROctetString(derEncoding));
        MacData mData = pfx.getMacData();
        try {
            int itCount = mData.getIterationCount().intValue();
            byte[] data = ASN1OctetString.getInstance(info.getContent()).getOctets();
            byte[] res = PKCS12Util.calculatePbeMac(mData.getMac().getAlgorithmId().getAlgorithm(), mData.getSalt(), itCount, passwd, data, provider);
            AlgorithmIdentifier algId = new AlgorithmIdentifier(mData.getMac().getAlgorithmId().getAlgorithm(), DERNull.INSTANCE);
            DigestInfo dInfo = new DigestInfo(algId, res);
            mData = new MacData(dInfo, mData.getSalt(), itCount);
        }
        catch (Exception e) {
            throw new IOException("error constructing MAC: " + e.toString());
        }
        pfx = new Pfx(info, mData);
        return pfx.getEncoded("DER");
    }

    private static byte[] calculatePbeMac(ASN1ObjectIdentifier oid, byte[] salt, int itCount, char[] password, byte[] data, String provider) throws Exception {
        SecretKeyFactory keyFact = SecretKeyFactory.getInstance(oid.getId(), provider);
        PBEParameterSpec defParams = new PBEParameterSpec(salt, itCount);
        PBEKeySpec pbeSpec = new PBEKeySpec(password);
        SecretKey key = keyFact.generateSecret(pbeSpec);
        Mac mac = Mac.getInstance(oid.getId(), provider);
        mac.init(key, defParams);
        mac.update(data);
        return mac.doFinal();
    }
}

