/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.cmp.PKIFailureInfo
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.CRLDistPoint
 *  org.bouncycastle.asn1.x509.DistributionPoint
 *  org.bouncycastle.asn1.x509.Extension
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.X509ObjectIdentifiers
 *  org.bouncycastle.cert.X509CertificateHolder
 *  org.bouncycastle.cert.jcajce.JcaX509CertificateConverter
 *  org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils
 *  org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator
 *  org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator
 *  org.bouncycastle.cms.SignerId
 *  org.bouncycastle.cms.SignerInformationVerifier
 *  org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder
 *  org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder
 *  org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder
 *  org.bouncycastle.operator.DigestAlgorithmIdentifierFinder
 *  org.bouncycastle.operator.DigestCalculatorProvider
 *  org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder
 *  org.bouncycastle.operator.bc.BcDigestCalculatorProvider
 *  org.bouncycastle.tsp.TimeStampRequest
 *  org.bouncycastle.tsp.TimeStampRequestGenerator
 *  org.bouncycastle.tsp.TimeStampResponse
 *  org.bouncycastle.tsp.TimeStampToken
 */
package org.apache.poi.poifs.crypt.dsig.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.security.auth.x500.X500Principal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.services.RevocationData;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampHttpClient;
import org.apache.poi.poifs.crypt.dsig.services.TimeStampService;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cmp.PKIFailureInfo;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.CRLDistPoint;
import org.bouncycastle.asn1.x509.DistributionPoint;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cms.CMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.DefaultCMSSignatureAlgorithmNameGenerator;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.bc.BcRSASignerInfoVerifierBuilder;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.operator.SignatureAlgorithmIdentifierFinder;
import org.bouncycastle.operator.bc.BcDigestCalculatorProvider;
import org.bouncycastle.tsp.TimeStampRequest;
import org.bouncycastle.tsp.TimeStampRequestGenerator;
import org.bouncycastle.tsp.TimeStampResponse;
import org.bouncycastle.tsp.TimeStampToken;

public class TSPTimeStampService
implements TimeStampService {
    private static final Logger LOG = LogManager.getLogger(TSPTimeStampService.class);

    public ASN1ObjectIdentifier mapDigestAlgoToOID(HashAlgorithm digestAlgo) {
        switch (digestAlgo) {
            case sha1: {
                return X509ObjectIdentifiers.id_SHA1;
            }
            case sha256: {
                return NISTObjectIdentifiers.id_sha256;
            }
            case sha384: {
                return NISTObjectIdentifiers.id_sha384;
            }
            case sha512: {
                return NISTObjectIdentifiers.id_sha512;
            }
        }
        throw new IllegalArgumentException("unsupported digest algo: " + (Object)((Object)digestAlgo));
    }

    @Override
    public byte[] timeStamp(SignatureInfo signatureInfo, byte[] data, RevocationData revocationData) throws Exception {
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        MessageDigest messageDigest = CryptoFunctions.getMessageDigest(signatureConfig.getTspDigestAlgo());
        byte[] digest = messageDigest.digest(data);
        BigInteger nonce = new BigInteger(128, new SecureRandom());
        TimeStampRequestGenerator requestGenerator = new TimeStampRequestGenerator();
        requestGenerator.setCertReq(true);
        String requestPolicy = signatureConfig.getTspRequestPolicy();
        if (requestPolicy != null) {
            requestGenerator.setReqPolicy(new ASN1ObjectIdentifier(requestPolicy));
        }
        ASN1ObjectIdentifier digestAlgoOid = this.mapDigestAlgoToOID(signatureConfig.getTspDigestAlgo());
        TimeStampRequest request = requestGenerator.generate(digestAlgoOid, digest, nonce);
        TimeStampHttpClient httpClient = signatureConfig.getTspHttpClient();
        httpClient.init(signatureConfig);
        httpClient.setContentTypeIn(signatureConfig.isTspOldProtocol() ? "application/timestamp-request" : "application/timestamp-query");
        TimeStampHttpClient.TimeStampHttpClientResponse response = httpClient.post(signatureConfig.getTspUrl(), request.getEncoded());
        if (!response.isOK()) {
            throw new IOException("Requesting timestamp data failed");
        }
        byte[] responseBytes = response.getResponseBytes();
        if (responseBytes.length == 0) {
            throw new RuntimeException("Content-Length is zero");
        }
        TimeStampResponse timeStampResponse = new TimeStampResponse(responseBytes);
        timeStampResponse.validate(request);
        if (0 != timeStampResponse.getStatus()) {
            LOG.atDebug().log("status: {}", (Object)Unbox.box(timeStampResponse.getStatus()));
            LOG.atDebug().log("status string: {}", (Object)timeStampResponse.getStatusString());
            PKIFailureInfo failInfo = timeStampResponse.getFailInfo();
            if (null != failInfo) {
                LOG.atDebug().log("fail info int value: {}", (Object)Unbox.box(failInfo.intValue()));
                if (256 == failInfo.intValue()) {
                    LOG.atDebug().log("unaccepted policy");
                }
            }
            throw new RuntimeException("timestamp response status != 0: " + timeStampResponse.getStatus());
        }
        TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
        SignerId signerId = timeStampToken.getSID();
        BigInteger signerCertSerialNumber = signerId.getSerialNumber();
        X500Name signerCertIssuer = signerId.getIssuer();
        LOG.atDebug().log("signer cert serial number: {}", (Object)signerCertSerialNumber);
        LOG.atDebug().log("signer cert issuer: {}", (Object)signerCertIssuer);
        Map certificateMap = timeStampToken.getCertificates().getMatches(null).stream().collect(Collectors.toMap(h -> h.getSubject().toString(), Function.identity()));
        X509CertificateHolder signerCert = certificateMap.values().stream().filter(h -> signerCertIssuer.equals((Object)h.getIssuer()) && signerCertSerialNumber.equals(h.getSerialNumber())).findFirst().orElseThrow(() -> new RuntimeException("TSP response token has no signer certificate"));
        JcaX509CertificateConverter x509converter = new JcaX509CertificateConverter();
        x509converter.setProvider("BC");
        X509Certificate child = x509converter.getCertificate(signerCert);
        do {
            revocationData.addCertificate(child);
            X500Principal issuer = child.getIssuerX500Principal();
            if (child.getSubjectX500Principal().equals(issuer)) break;
            X509CertificateHolder parentHolder = (X509CertificateHolder)certificateMap.get(issuer.getName());
            X509Certificate x509Certificate = child = parentHolder != null ? x509converter.getCertificate(parentHolder) : signatureConfig.getCachedCertificateByPrinicipal(issuer.getName());
            if (child == null) continue;
            this.retrieveCRL(signatureConfig, child).forEach(revocationData::addCRL);
        } while (child != null);
        BcRSASignerInfoVerifierBuilder verifierBuilder = new BcRSASignerInfoVerifierBuilder((CMSSignatureAlgorithmNameGenerator)new DefaultCMSSignatureAlgorithmNameGenerator(), (SignatureAlgorithmIdentifierFinder)new DefaultSignatureAlgorithmIdentifierFinder(), (DigestAlgorithmIdentifierFinder)new DefaultDigestAlgorithmIdentifierFinder(), (DigestCalculatorProvider)new BcDigestCalculatorProvider());
        SignerInformationVerifier verifier = verifierBuilder.build(signerCert);
        timeStampToken.validate(verifier);
        if (signatureConfig.getTspValidator() != null) {
            signatureConfig.getTspValidator().validate(revocationData.getX509chain(), revocationData);
        }
        LOG.atDebug().log("time-stamp token time: {}", (Object)timeStampToken.getTimeStampInfo().getGenTime());
        return timeStampToken.getEncoded();
    }

    protected List<byte[]> retrieveCRL(SignatureConfig signatureConfig, X509Certificate holder) throws IOException {
        List<SignatureConfig.CRLEntry> crlEntries = signatureConfig.getCrlEntries();
        byte[] crlPoints = holder.getExtensionValue(Extension.cRLDistributionPoints.getId());
        if (crlPoints == null) {
            return Collections.emptyList();
        }
        ASN1Primitive extVal = JcaX509ExtensionUtils.parseExtensionValue((byte[])crlPoints);
        return Stream.of(CRLDistPoint.getInstance((Object)extVal).getDistributionPoints()).map(DistributionPoint::getDistributionPoint).filter(Objects::nonNull).filter(dpn -> dpn.getType() == 0).flatMap(dpn -> Stream.of(GeneralNames.getInstance((Object)dpn.getName()).getNames())).filter(genName -> genName.getTagNo() == 6).map(genName -> ASN1IA5String.getInstance((Object)genName.getName()).getString()).flatMap(url -> {
            SignatureConfig.CRLEntry ce2;
            List ul = crlEntries.stream().filter(ce -> this.matchCRLbyUrl((SignatureConfig.CRLEntry)ce, holder, (String)url)).collect(Collectors.toList());
            Stream<SignatureConfig.CRLEntry> cl = crlEntries.stream().filter(ce -> this.matchCRLbyCN((SignatureConfig.CRLEntry)ce, holder, (String)url));
            if (ul.isEmpty() && (ce2 = this.downloadCRL(signatureConfig, (String)url)) != null) {
                ul.add(ce2);
            }
            return Stream.concat(ul.stream(), cl).map(SignatureConfig.CRLEntry::getCrlBytes);
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected boolean matchCRLbyUrl(SignatureConfig.CRLEntry other, X509Certificate holder, String url) {
        return url.equals(other.getCrlURL());
    }

    protected boolean matchCRLbyCN(SignatureConfig.CRLEntry other, X509Certificate holder, String url) {
        return holder.getSubjectX500Principal().getName().equals(other.getCertCN());
    }

    protected SignatureConfig.CRLEntry downloadCRL(SignatureConfig signatureConfig, String url) {
        TimeStampHttpClient.TimeStampHttpClientResponse response;
        if (!signatureConfig.isAllowCRLDownload()) {
            return null;
        }
        TimeStampHttpClient httpClient = signatureConfig.getTspHttpClient();
        httpClient.init(signatureConfig);
        httpClient.setBasicAuthentication(null, null);
        try {
            response = httpClient.get(url);
            if (!response.isOK()) {
                return null;
            }
        }
        catch (IOException e) {
            return null;
        }
        try {
            CertificateFactory certFact = CertificateFactory.getInstance("X.509");
            byte[] crlBytes = response.getResponseBytes();
            X509CRL crl = (X509CRL)certFact.generateCRL(new ByteArrayInputStream(crlBytes));
            return signatureConfig.addCRL(url, crl.getIssuerX500Principal().getName(), crlBytes);
        }
        catch (GeneralSecurityException e) {
            LOG.atWarn().withThrowable(e).log("CRL download failed from {}", (Object)url);
            return null;
        }
    }
}

