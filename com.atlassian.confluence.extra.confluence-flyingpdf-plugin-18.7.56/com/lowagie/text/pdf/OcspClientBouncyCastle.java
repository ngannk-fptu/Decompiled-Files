/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cert.jcajce.JcaX509CertificateHolder
 *  org.bouncycastle.cert.ocsp.BasicOCSPResp
 *  org.bouncycastle.cert.ocsp.CertificateID
 *  org.bouncycastle.cert.ocsp.CertificateStatus
 *  org.bouncycastle.cert.ocsp.OCSPException
 *  org.bouncycastle.cert.ocsp.OCSPReq
 *  org.bouncycastle.cert.ocsp.OCSPReqBuilder
 *  org.bouncycastle.cert.ocsp.OCSPResp
 *  org.bouncycastle.cert.ocsp.RevokedStatus
 *  org.bouncycastle.cert.ocsp.SingleResp
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.bouncycastle.operator.DigestCalculatorProvider
 *  org.bouncycastle.operator.OperatorCreationException
 *  org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.OcspClient;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Random;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.CertificateID;
import org.bouncycastle.cert.ocsp.CertificateStatus;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.OCSPReq;
import org.bouncycastle.cert.ocsp.OCSPReqBuilder;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.RevokedStatus;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;

public class OcspClientBouncyCastle
implements OcspClient {
    private final X509Certificate rootCert;
    private final X509Certificate checkCert;
    private final String url;

    public OcspClientBouncyCastle(X509Certificate checkCert, X509Certificate rootCert, String url) {
        this.checkCert = checkCert;
        this.rootCert = rootCert;
        this.url = url;
    }

    private static OCSPReq generateOCSPRequest(X509Certificate issuerCert, BigInteger serialNumber) throws OCSPException, IOException, OperatorCreationException, CertificateEncodingException {
        BouncyCastleProvider prov = new BouncyCastleProvider();
        Security.addProvider((Provider)prov);
        DigestCalculatorProvider digCalcProv = new JcaDigestCalculatorProviderBuilder().setProvider((Provider)prov).build();
        CertificateID id = new CertificateID(digCalcProv.get(CertificateID.HASH_SHA1), (X509CertificateHolder)new JcaX509CertificateHolder(issuerCert), serialNumber);
        OCSPReqBuilder gen = new OCSPReqBuilder();
        gen.addRequest(id);
        ExtensionsGenerator extGen = new ExtensionsGenerator();
        byte[] nonce = new byte[16];
        Random rand = new Random();
        rand.nextBytes(nonce);
        extGen.addExtension(OCSPObjectIdentifiers.id_pkix_ocsp_nonce, false, (ASN1Encodable)new DEROctetString(nonce));
        gen.setRequestExtensions(extGen.generate());
        return gen.build();
    }

    @Override
    public byte[] getEncoded() {
        try {
            SingleResp[] responses;
            OCSPReq request = OcspClientBouncyCastle.generateOCSPRequest(this.rootCert, this.checkCert.getSerialNumber());
            byte[] array = request.getEncoded();
            URL urlt = new URL(this.url);
            HttpURLConnection con = (HttpURLConnection)urlt.openConnection();
            con.setRequestProperty("Content-Type", "application/ocsp-request");
            con.setRequestProperty("Accept", "application/ocsp-response");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();
            DataOutputStream dataOut = new DataOutputStream(new BufferedOutputStream(out));
            dataOut.write(array);
            dataOut.flush();
            dataOut.close();
            if (con.getResponseCode() / 100 != 2) {
                throw new IOException(MessageLocalization.getComposedMessage("invalid.http.response.1", con.getResponseCode()));
            }
            InputStream in = (InputStream)con.getContent();
            OCSPResp ocspResponse = new OCSPResp(in);
            if (ocspResponse.getStatus() != 0) {
                throw new IOException(MessageLocalization.getComposedMessage("invalid.status.1", ocspResponse.getStatus()));
            }
            BasicOCSPResp basicResponse = (BasicOCSPResp)ocspResponse.getResponseObject();
            if (basicResponse != null && (responses = basicResponse.getResponses()).length == 1) {
                SingleResp resp = responses[0];
                CertificateStatus status = resp.getCertStatus();
                if (status == null) {
                    return basicResponse.getEncoded();
                }
                if (status instanceof RevokedStatus) {
                    throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.revoked"));
                }
                throw new IOException(MessageLocalization.getComposedMessage("ocsp.status.is.unknown"));
            }
        }
        catch (Exception ex) {
            throw new ExceptionConverter(ex);
        }
        return null;
    }
}

