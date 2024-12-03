/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.util;

import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.exception.XMLEntityException;
import com.onelogin.saml2.model.SamlResponseStatus;
import com.onelogin.saml2.util.SchemaFactory;
import com.onelogin.saml2.util.XMLErrorAccumulatorHandler;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.Init;
import org.apache.xml.security.encryption.EncryptedData;
import org.apache.xml.security.encryption.EncryptedKey;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.XMLUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class Util {
    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);
    private static final DateTimeFormatter DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis().withZoneUTC();
    private static final DateTimeFormatter DATE_TIME_FORMAT_MILLS = ISODateTimeFormat.dateTime().withZoneUTC();
    public static final String UNIQUE_ID_PREFIX = "ONELOGIN_";
    public static final String RESPONSE_SIGNATURE_XPATH = "/samlp:Response/ds:Signature";
    public static final String ASSERTION_SIGNATURE_XPATH = "/samlp:Response/saml:Assertion/ds:Signature";
    private static boolean JAXP_15_SUPPORTED = Util.isJaxp15Supported();

    private Util() {
    }

    public static boolean isJaxp15Supported() {
        boolean supported = true;
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser parser = spf.newSAXParser();
            parser.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "file");
        }
        catch (SAXException ex) {
            String err = ex.getMessage();
            if (err.contains("Property 'http://javax.xml.XMLConstants/property/accessExternalDTD' is not recognized.")) {
                supported = false;
            }
        }
        catch (Exception e) {
            LOGGER.info("An exception occurred while trying to determine if JAXP 1.5 options are supported.", (Throwable)e);
        }
        return supported;
    }

    public static Document loadXML(String xml) {
        try {
            if (xml.contains("<!ENTITY")) {
                throw new XMLEntityException("Detected use of ENTITY in XML, disabled to prevent XXE/XEE attacks");
            }
            return Util.convertStringToDocument(xml);
        }
        catch (XMLEntityException e) {
            LOGGER.debug("Load XML error due XMLEntityException.", (Throwable)e);
        }
        catch (Exception e) {
            LOGGER.debug("Load XML error: " + e.getMessage(), (Throwable)e);
        }
        return null;
    }

    private static XPathFactory getXPathFactory() {
        try {
            return XPathFactory.newInstance("http://java.sun.com/jaxp/xpath/dom", "com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl", ClassLoader.getSystemClassLoader());
        }
        catch (XPathFactoryConfigurationException e) {
            LOGGER.debug("Error generating XPathFactory instance: " + e.getMessage(), (Throwable)e);
            return XPathFactory.newInstance();
        }
    }

    public static NodeList query(Document dom, String query, Node context) throws XPathExpressionException {
        XPath xpath = Util.getXPathFactory().newXPath();
        xpath.setNamespaceContext(new NamespaceContext(){

            @Override
            public String getNamespaceURI(String prefix) {
                String result = null;
                if (prefix.equals("samlp") || prefix.equals("samlp2")) {
                    result = "urn:oasis:names:tc:SAML:2.0:protocol";
                } else if (prefix.equals("saml") || prefix.equals("saml2")) {
                    result = "urn:oasis:names:tc:SAML:2.0:assertion";
                } else if (prefix.equals("ds")) {
                    result = "http://www.w3.org/2000/09/xmldsig#";
                } else if (prefix.equals("xenc")) {
                    result = "http://www.w3.org/2001/04/xmlenc#";
                } else if (prefix.equals("md")) {
                    result = "urn:oasis:names:tc:SAML:2.0:metadata";
                }
                return result;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            public Iterator getPrefixes(String namespaceURI) {
                return null;
            }
        });
        NodeList nodeList = context == null ? (NodeList)xpath.evaluate(query, dom, XPathConstants.NODESET) : (NodeList)xpath.evaluate(query, context, XPathConstants.NODESET);
        return nodeList;
    }

    public static NodeList query(Document dom, String query) throws XPathExpressionException {
        return Util.query(dom, query, null);
    }

    public static boolean validateXML(Document xmlDocument, URL schemaUrl) {
        try {
            boolean isValid;
            if (xmlDocument == null) {
                throw new IllegalArgumentException("xmlDocument was null");
            }
            Schema schema = SchemaFactory.loadFromUrl(schemaUrl);
            Validator validator = schema.newValidator();
            if (JAXP_15_SUPPORTED) {
                validator.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
                validator.setProperty("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
            }
            XMLErrorAccumulatorHandler errorAcumulator = new XMLErrorAccumulatorHandler();
            validator.setErrorHandler(errorAcumulator);
            DOMSource xmlSource = new DOMSource(xmlDocument);
            validator.validate(xmlSource);
            boolean bl = isValid = !errorAcumulator.hasError();
            if (!isValid) {
                LOGGER.warn("Errors found when validating SAML response with schema: " + errorAcumulator.getErrorXML());
            }
            return isValid;
        }
        catch (Exception e) {
            LOGGER.warn("Error executing validateXML: " + e.getMessage(), (Throwable)e);
            return false;
        }
    }

    public static Document convertStringToDocument(String xmlStr) throws ParserConfigurationException, SAXException, IOException {
        return Util.parseXML(new InputSource(new StringReader(xmlStr)));
    }

    public static Document parseXML(InputSource inputSource) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docfactory = DocumentBuilderFactory.newInstance();
        docfactory.setNamespaceAware(true);
        docfactory.setExpandEntityReferences(false);
        docfactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://www.w3.org/2001/XMLSchema");
        try {
            docfactory.setAttribute("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setAttribute("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setAttribute("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setAttribute("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-external-dtd", Boolean.FALSE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setAttribute("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", Boolean.FALSE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            docfactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        DocumentBuilder builder = docfactory.newDocumentBuilder();
        Document doc = builder.parse(inputSource);
        XPath xpath = Util.getXPathFactory().newXPath();
        try {
            XPathExpression expr = xpath.compile("//*[@ID]");
            NodeList nodeList = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodeList.getLength(); ++i) {
                Element elem = (Element)nodeList.item(i);
                Attr attr = (Attr)elem.getAttributes().getNamedItem("ID");
                elem.setIdAttributeNode(attr, true);
            }
        }
        catch (XPathExpressionException e) {
            return null;
        }
        return doc;
    }

    public static String convertDocumentToString(Document doc, Boolean c14n) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (c14n.booleanValue()) {
            XMLUtils.outputDOMc14nWithComments(doc, baos);
        } else {
            XMLUtils.outputDOM(doc, baos);
        }
        return Util.toStringUtf8(baos.toByteArray());
    }

    public static String convertDocumentToString(Document doc) {
        return Util.convertDocumentToString(doc, false);
    }

    public static String formatCert(String cert, Boolean heads) {
        String x509cert = "";
        if (cert != null && !StringUtils.isEmpty((CharSequence)(x509cert = cert.replace("\\x0D", "").replace("\r", "").replace("\n", "").replace(" ", "")))) {
            x509cert = x509cert.replace("-----BEGINCERTIFICATE-----", "").replace("-----ENDCERTIFICATE-----", "");
            if (heads.booleanValue()) {
                x509cert = "-----BEGIN CERTIFICATE-----\n" + Util.chunkString(x509cert, 64) + "-----END CERTIFICATE-----";
            }
        }
        return x509cert;
    }

    public static String formatPrivateKey(String key, boolean heads) {
        String xKey = "";
        if (key != null && !StringUtils.isEmpty((CharSequence)(xKey = key.replace("\\x0D", "").replace("\r", "").replace("\n", "").replace(" ", "")))) {
            if (xKey.startsWith("-----BEGINPRIVATEKEY-----")) {
                xKey = xKey.replace("-----BEGINPRIVATEKEY-----", "").replace("-----ENDPRIVATEKEY-----", "");
                if (heads) {
                    xKey = "-----BEGIN PRIVATE KEY-----\n" + Util.chunkString(xKey, 64) + "-----END PRIVATE KEY-----";
                }
            } else {
                xKey = xKey.replace("-----BEGINRSAPRIVATEKEY-----", "").replace("-----ENDRSAPRIVATEKEY-----", "");
                if (heads) {
                    xKey = "-----BEGIN RSA PRIVATE KEY-----\n" + Util.chunkString(xKey, 64) + "-----END RSA PRIVATE KEY-----";
                }
            }
        }
        return xKey;
    }

    private static String chunkString(String str, int chunkSize) {
        String newStr = "";
        int stringLength = str.length();
        for (int i = 0; i < stringLength; i += chunkSize) {
            if (i + chunkSize > stringLength) {
                chunkSize = stringLength - i;
            }
            newStr = newStr + str.substring(i, chunkSize + i) + '\n';
        }
        return newStr;
    }

    public static X509Certificate loadCert(String certString) throws CertificateException {
        X509Certificate cert;
        certString = Util.formatCert(certString, true);
        try {
            cert = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certString.getBytes(StandardCharsets.UTF_8)));
        }
        catch (IllegalArgumentException e) {
            cert = null;
        }
        return cert;
    }

    public static PrivateKey loadPrivateKey(String keyString) throws GeneralSecurityException {
        PrivateKey privKey;
        String extractedKey = Util.formatPrivateKey(keyString, false);
        extractedKey = Util.chunkString(extractedKey, 64);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        try {
            byte[] encoded = Base64.decodeBase64(extractedKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
            privKey = kf.generatePrivate(keySpec);
        }
        catch (IllegalArgumentException e) {
            privKey = null;
        }
        return privKey;
    }

    public static String calculateX509Fingerprint(X509Certificate x509cert, String alg) {
        String fingerprint = "";
        try {
            byte[] dataBytes = x509cert.getEncoded();
            if (alg == null || alg.isEmpty() || alg.equals("SHA-1") || alg.equals("sha1")) {
                fingerprint = DigestUtils.sha1Hex(dataBytes);
            } else if (alg.equals("SHA-256") || alg.equals("sha256")) {
                fingerprint = DigestUtils.sha256Hex(dataBytes);
            } else if (alg.equals("SHA-384") || alg.equals("sha384")) {
                fingerprint = DigestUtils.sha384Hex(dataBytes);
            } else if (alg.equals("SHA-512") || alg.equals("sha512")) {
                fingerprint = DigestUtils.sha512Hex(dataBytes);
            } else {
                LOGGER.debug("Error executing calculateX509Fingerprint. alg " + alg + " not supported");
            }
        }
        catch (Exception e) {
            LOGGER.debug("Error executing calculateX509Fingerprint: " + e.getMessage(), (Throwable)e);
        }
        return fingerprint.toLowerCase();
    }

    public static String calculateX509Fingerprint(X509Certificate x509cert) {
        return Util.calculateX509Fingerprint(x509cert, "SHA-1");
    }

    public static String convertToPem(X509Certificate certificate) {
        String pemCert = "";
        try {
            Base64 encoder = new Base64(64);
            String cert_begin = "-----BEGIN CERTIFICATE-----\n";
            String end_cert = "-----END CERTIFICATE-----";
            byte[] derCert = certificate.getEncoded();
            String pemCertPre = new String(encoder.encode(derCert));
            pemCert = cert_begin + pemCertPre + end_cert;
        }
        catch (Exception e) {
            LOGGER.debug("Error converting certificate on PEM format: " + e.getMessage(), (Throwable)e);
        }
        return pemCert;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String getFileAsString(String relativeResourcePath) throws IOException {
        InputStream is = Util.class.getResourceAsStream("/" + relativeResourcePath);
        if (is == null) {
            throw new FileNotFoundException(relativeResourcePath);
        }
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            Util.copyBytes(new BufferedInputStream(is), bytes);
            String string = bytes.toString("utf-8");
            return string;
        }
        finally {
            is.close();
        }
    }

    private static void copyBytes(InputStream is, OutputStream bytes) throws IOException {
        int res = is.read();
        while (res != -1) {
            bytes.write(res);
            res = is.read();
        }
    }

    public static String base64decodedInflated(String input) {
        if (input.isEmpty()) {
            return input;
        }
        byte[] decoded = Base64.decodeBase64(input);
        try {
            Inflater decompresser = new Inflater(true);
            decompresser.setInput(decoded);
            byte[] result = new byte[1024];
            String inflated = "";
            for (long limit = 0L; !decompresser.finished() && limit < 150L; ++limit) {
                int resultLength = decompresser.inflate(result);
                inflated = inflated + new String(result, 0, resultLength, "UTF-8");
            }
            decompresser.end();
            return inflated;
        }
        catch (Exception e) {
            return new String(decoded);
        }
    }

    public static String deflatedBase64encoded(String input) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        Deflater deflater = new Deflater(8, true);
        DeflaterOutputStream deflaterStream = new DeflaterOutputStream((OutputStream)bytesOut, deflater);
        deflaterStream.write(input.getBytes(Charset.forName("UTF-8")));
        deflaterStream.finish();
        return new String(Base64.encodeBase64(bytesOut.toByteArray()));
    }

    public static String base64encoder(byte[] input) {
        return Util.toStringUtf8(Base64.encodeBase64(input));
    }

    public static String base64encoder(String input) {
        return Util.base64encoder(Util.toBytesUtf8(input));
    }

    public static byte[] base64decoder(byte[] input) {
        return Base64.decodeBase64(input);
    }

    public static byte[] base64decoder(String input) {
        return Util.base64decoder(Util.toBytesUtf8(input));
    }

    public static String urlEncoder(String input) {
        if (input != null) {
            try {
                return URLEncoder.encode(input, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                LOGGER.error("URL encoder error.", (Throwable)e);
                throw new IllegalArgumentException();
            }
        }
        return null;
    }

    public static String urlDecoder(String input) {
        if (input != null) {
            try {
                return URLDecoder.decode(input, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                LOGGER.error("URL decoder error.", (Throwable)e);
                throw new IllegalArgumentException();
            }
        }
        return null;
    }

    public static byte[] sign(String text, PrivateKey key, String signAlgorithm) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        if (signAlgorithm == null) {
            signAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
        Signature instance = Signature.getInstance(Util.signatureAlgConversion(signAlgorithm));
        instance.initSign(key);
        instance.update(text.getBytes());
        byte[] signature = instance.sign();
        return signature;
    }

    public static String signatureAlgConversion(String sign) {
        String convertedSignatureAlg = "";
        convertedSignatureAlg = sign == null ? "SHA1withRSA" : (sign.equals("http://www.w3.org/2000/09/xmldsig#dsa-sha1") ? "SHA1withDSA" : (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256") ? "SHA256withRSA" : (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384") ? "SHA384withRSA" : (sign.equals("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512") ? "SHA512withRSA" : "SHA1withRSA"))));
        return convertedSignatureAlg;
    }

    public static boolean validateSign(Document doc, X509Certificate cert, String fingerprint, String alg, String xpath) {
        try {
            NodeList signatures = Util.query(doc, xpath);
            return signatures.getLength() == 1 && Util.validateSignNode(signatures.item(0), cert, fingerprint, alg) != false;
        }
        catch (XPathExpressionException e) {
            LOGGER.warn("Failed to find signature nodes", (Throwable)e);
            return false;
        }
    }

    public static boolean validateSign(Document doc, List<X509Certificate> certList, String fingerprint, String alg, String xpath) {
        try {
            NodeList signatures = Util.query(doc, xpath);
            if (signatures.getLength() == 1) {
                Node signNode = signatures.item(0);
                Map<String, Object> signatureData = Util.getSignatureData(signNode, alg);
                if (signatureData.isEmpty()) {
                    return false;
                }
                XMLSignature signature = (XMLSignature)signatureData.get("signature");
                X509Certificate extractedCert = (X509Certificate)signatureData.get("cert");
                String extractedFingerprint = (String)signatureData.get("fingerprint");
                if (certList == null || certList.isEmpty()) {
                    return Util.validateSignNode(signature, null, fingerprint, extractedCert, extractedFingerprint);
                }
                Boolean certMatches = false;
                for (X509Certificate cert : certList) {
                    if (cert != null && extractedFingerprint != null) {
                        if (!extractedFingerprint.equals(Util.calculateX509Fingerprint(cert, alg))) continue;
                        certMatches = true;
                        if (!Util.validateSignNode(signature, cert, null, null, null).booleanValue()) continue;
                        return true;
                    }
                    if (!Util.validateSignNode(signature, cert, fingerprint, extractedCert, extractedFingerprint).booleanValue()) continue;
                    return true;
                }
                if (!certMatches.booleanValue()) {
                    LOGGER.warn("Certificate used in the document does not match any registered certificate");
                }
            }
        }
        catch (XPathExpressionException e) {
            LOGGER.warn("Failed to find signature nodes", (Throwable)e);
        }
        return false;
    }

    public static Boolean validateMetadataSign(Document doc, X509Certificate cert, String fingerprint, String alg) {
        try {
            NodeList signNodesToValidate = Util.query(doc, "/md:EntitiesDescriptor/ds:Signature");
            if (signNodesToValidate.getLength() == 0 && (signNodesToValidate = Util.query(doc, "/md:EntityDescriptor/ds:Signature")).getLength() == 0) {
                signNodesToValidate = Util.query(doc, "/md:EntityDescriptor/md:SPSSODescriptor/ds:Signature|/md:EntityDescriptor/IDPSSODescriptor/ds:Signature");
            }
            if (signNodesToValidate.getLength() > 0) {
                for (int i = 0; i < signNodesToValidate.getLength(); ++i) {
                    Node signNode = signNodesToValidate.item(i);
                    if (Util.validateSignNode(signNode, cert, fingerprint, alg).booleanValue()) continue;
                    return false;
                }
                return true;
            }
        }
        catch (XPathExpressionException e) {
            LOGGER.warn("Failed to find signature nodes", (Throwable)e);
        }
        return false;
    }

    private static Map<String, Object> getSignatureData(Node signNode, String alg) {
        HashMap<String, Object> signatureData = new HashMap<String, Object>();
        try {
            Element sigElement = (Element)signNode;
            XMLSignature signature = new XMLSignature(sigElement, "", true);
            String sigMethodAlg = signature.getSignedInfo().getSignatureMethodURI();
            if (!Util.isAlgorithmWhitelisted(sigMethodAlg)) {
                throw new Exception(sigMethodAlg + " is not a valid supported algorithm");
            }
            signatureData.put("signature", signature);
            String extractedFingerprint = null;
            X509Certificate extractedCert = null;
            KeyInfo keyInfo = signature.getKeyInfo();
            if (keyInfo != null && keyInfo.containsX509Data()) {
                extractedCert = keyInfo.getX509Certificate();
                extractedFingerprint = Util.calculateX509Fingerprint(extractedCert, alg);
                signatureData.put("cert", extractedCert);
                signatureData.put("fingerprint", extractedFingerprint);
            } else {
                LOGGER.debug("No KeyInfo or not x509CertificateData");
            }
        }
        catch (Exception e) {
            LOGGER.warn("Error executing getSignatureData: " + e.getMessage(), (Throwable)e);
        }
        return signatureData;
    }

    public static Boolean validateSignNode(Node signNode, X509Certificate cert, String fingerprint, String alg) {
        Map<String, Object> signatureData = Util.getSignatureData(signNode, alg);
        if (signatureData.isEmpty()) {
            return false;
        }
        XMLSignature signature = (XMLSignature)signatureData.get("signature");
        X509Certificate extractedCert = (X509Certificate)signatureData.get("cert");
        String extractedFingerprint = (String)signatureData.get("fingerprint");
        return Util.validateSignNode(signature, cert, fingerprint, extractedCert, extractedFingerprint);
    }

    public static Boolean validateSignNode(XMLSignature signature, X509Certificate cert, String fingerprint, X509Certificate extractedCert, String extractedFingerprint) {
        Boolean res = false;
        try {
            if (cert != null) {
                res = signature.checkSignatureValue(cert);
            } else if (extractedCert != null && fingerprint != null && extractedFingerprint != null) {
                Boolean fingerprintMatches = false;
                for (String fingerprintStr : fingerprint.split(",")) {
                    if (!extractedFingerprint.equalsIgnoreCase(fingerprintStr.trim())) continue;
                    fingerprintMatches = true;
                    res = signature.checkSignatureValue(extractedCert);
                    if (res.booleanValue()) break;
                }
                if (!fingerprintMatches.booleanValue()) {
                    LOGGER.warn("Fingerprint of the certificate used in the document does not match any registered fingerprints");
                }
            }
        }
        catch (Exception e) {
            LOGGER.warn("Error executing validateSignNode: " + e.getMessage(), (Throwable)e);
        }
        return res;
    }

    public static boolean isAlgorithmWhitelisted(String alg) {
        HashSet<String> whiteListedAlgorithm = new HashSet<String>();
        whiteListedAlgorithm.add("http://www.w3.org/2000/09/xmldsig#dsa-sha1");
        whiteListedAlgorithm.add("http://www.w3.org/2000/09/xmldsig#rsa-sha1");
        whiteListedAlgorithm.add("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");
        whiteListedAlgorithm.add("http://www.w3.org/2001/04/xmldsig-more#rsa-sha384");
        whiteListedAlgorithm.add("http://www.w3.org/2001/04/xmldsig-more#rsa-sha512");
        Boolean whitelisted = false;
        if (whiteListedAlgorithm.contains(alg)) {
            whitelisted = true;
        }
        return whitelisted;
    }

    public static void decryptElement(Element encryptedDataElement, PrivateKey inputKey) {
        try {
            XMLCipher xmlCipher = XMLCipher.getInstance();
            xmlCipher.init(2, null);
            NodeList keyInfoInEncData = encryptedDataElement.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
            if (keyInfoInEncData.getLength() == 0) {
                throw new ValidationError("No KeyInfo inside EncryptedData element", 35);
            }
            NodeList childs = keyInfoInEncData.item(0).getChildNodes();
            for (int i = 0; i < childs.getLength(); ++i) {
                if (childs.item(i).getLocalName() == null || !childs.item(i).getLocalName().equals("RetrievalMethod")) continue;
                Element retrievalMethodElem = (Element)childs.item(i);
                if (!retrievalMethodElem.getAttribute("Type").equals("http://www.w3.org/2001/04/xmlenc#EncryptedKey")) {
                    throw new ValidationError("Unsupported Retrieval Method found", 37);
                }
                String uri = retrievalMethodElem.getAttribute("URI").substring(1);
                NodeList encryptedKeyNodes = ((Element)encryptedDataElement.getParentNode()).getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
                for (int j = 0; j < encryptedKeyNodes.getLength(); ++j) {
                    if (!((Element)encryptedKeyNodes.item(j)).getAttribute("Id").equals(uri)) continue;
                    keyInfoInEncData.item(0).replaceChild(encryptedKeyNodes.item(j), childs.item(i));
                }
            }
            xmlCipher.setKEK(inputKey);
            xmlCipher.doFinal(encryptedDataElement.getOwnerDocument(), encryptedDataElement, false);
        }
        catch (Exception e) {
            LOGGER.warn("Error executing decryption: " + e.getMessage(), (Throwable)e);
        }
    }

    public static Document copyDocument(Document source) throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Element originalRoot = source.getDocumentElement();
        Document copiedDocument = db.newDocument();
        Node copiedRoot = copiedDocument.importNode(originalRoot, true);
        copiedDocument.appendChild(copiedRoot);
        return copiedDocument;
    }

    public static String addSign(Document document, PrivateKey key, X509Certificate certificate, String signAlgorithm) throws XMLSecurityException, XPathExpressionException {
        return Util.addSign(document, key, certificate, signAlgorithm, "http://www.w3.org/2000/09/xmldsig#sha1");
    }

    public static String addSign(Document document, PrivateKey key, X509Certificate certificate, String signAlgorithm, String digestAlgorithm) throws XMLSecurityException, XPathExpressionException {
        String id;
        if (document == null) {
            throw new IllegalArgumentException("Provided document was null");
        }
        if (document.getDocumentElement() == null) {
            throw new IllegalArgumentException("The Xml Document has no root element.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Provided key was null");
        }
        if (certificate == null) {
            throw new IllegalArgumentException("Provided certificate was null");
        }
        if (signAlgorithm == null || signAlgorithm.isEmpty()) {
            signAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
        if (digestAlgorithm == null || digestAlgorithm.isEmpty()) {
            digestAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
        }
        document.normalizeDocument();
        String c14nMethod = "http://www.w3.org/2001/10/xml-exc-c14n#";
        XMLSignature sig = new XMLSignature(document, null, signAlgorithm, c14nMethod);
        Element root = document.getDocumentElement();
        document.setXmlStandalone(false);
        NodeList issuerNodes = Util.query(document, "//saml:Issuer", null);
        Element elemToSign = null;
        if (issuerNodes.getLength() > 0) {
            Node issuer = issuerNodes.item(0);
            root.insertBefore(sig.getElement(), issuer.getNextSibling());
            elemToSign = (Element)issuer.getParentNode();
        } else {
            NodeList entityDescriptorNodes;
            NodeList entitiesDescriptorNodes = Util.query(document, "//md:EntitiesDescriptor", null);
            elemToSign = entitiesDescriptorNodes.getLength() > 0 ? (Element)entitiesDescriptorNodes.item(0) : ((entityDescriptorNodes = Util.query(document, "//md:EntityDescriptor", null)).getLength() > 0 ? (Element)entityDescriptorNodes.item(0) : root);
            root.insertBefore(sig.getElement(), elemToSign.getFirstChild());
        }
        String reference = id = elemToSign.getAttribute("ID");
        if (!id.isEmpty()) {
            elemToSign.setIdAttributeNS(null, "ID", true);
            reference = "#" + id;
        }
        Transforms transforms = new Transforms(document);
        transforms.addTransform("http://www.w3.org/2000/09/xmldsig#enveloped-signature");
        transforms.addTransform(c14nMethod);
        sig.addDocument(reference, transforms, digestAlgorithm);
        sig.addKeyInfo(certificate);
        sig.sign(key);
        return Util.convertDocumentToString(document, true);
    }

    public static String addSign(Node node, PrivateKey key, X509Certificate certificate, String signAlgorithm, String digestAlgorithm) throws ParserConfigurationException, XPathExpressionException, XMLSecurityException {
        if (node == null) {
            throw new IllegalArgumentException("Provided node was null");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().newDocument();
        Node newNode = doc.importNode(node, true);
        doc.appendChild(newNode);
        return Util.addSign(doc, key, certificate, signAlgorithm, digestAlgorithm);
    }

    public static String addSign(Node node, PrivateKey key, X509Certificate certificate, String signAlgorithm) throws ParserConfigurationException, XPathExpressionException, XMLSecurityException {
        return Util.addSign(node, key, certificate, signAlgorithm, "http://www.w3.org/2000/09/xmldsig#sha1");
    }

    public static Boolean validateBinarySignature(String signedQuery, byte[] signature, X509Certificate cert, String signAlg) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Boolean valid = false;
        try {
            String convertedSigAlg = Util.signatureAlgConversion(signAlg);
            Signature sig = Signature.getInstance(convertedSigAlg);
            sig.initVerify(cert.getPublicKey());
            sig.update(signedQuery.getBytes());
            valid = sig.verify(signature);
        }
        catch (Exception e) {
            LOGGER.warn("Error executing validateSign: " + e.getMessage(), (Throwable)e);
        }
        return valid;
    }

    public static Boolean validateBinarySignature(String signedQuery, byte[] signature, List<X509Certificate> certList, String signAlg) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        Boolean valid = false;
        String convertedSigAlg = Util.signatureAlgConversion(signAlg);
        Signature sig = Signature.getInstance(convertedSigAlg);
        for (X509Certificate cert : certList) {
            try {
                sig.initVerify(cert.getPublicKey());
                sig.update(signedQuery.getBytes());
                valid = sig.verify(signature);
                if (!valid.booleanValue()) continue;
                break;
            }
            catch (Exception e) {
                LOGGER.warn("Error executing validateSign: " + e.getMessage(), (Throwable)e);
            }
        }
        return valid;
    }

    public static SamlResponseStatus getStatus(String statusXpath, Document dom) throws ValidationError {
        try {
            NodeList messageEntry;
            NodeList statusEntry = Util.query(dom, statusXpath, null);
            if (statusEntry.getLength() != 1) {
                throw new ValidationError("Missing Status on response", 3);
            }
            NodeList codeEntry = Util.query(dom, statusXpath + "/samlp:StatusCode", (Element)statusEntry.item(0));
            if (codeEntry.getLength() == 0) {
                throw new ValidationError("Missing Status Code on response", 4);
            }
            String stausCode = codeEntry.item(0).getAttributes().getNamedItem("Value").getNodeValue();
            SamlResponseStatus status = new SamlResponseStatus(stausCode);
            NodeList subStatusCodeEntry = Util.query(dom, statusXpath + "/samlp:StatusCode/samlp:StatusCode", (Element)statusEntry.item(0));
            if (subStatusCodeEntry.getLength() > 0) {
                String subStatusCode = subStatusCodeEntry.item(0).getAttributes().getNamedItem("Value").getNodeValue();
                status.setSubStatusCode(subStatusCode);
            }
            if ((messageEntry = Util.query(dom, statusXpath + "/samlp:StatusMessage", (Element)statusEntry.item(0))).getLength() == 1) {
                status.setStatusMessage(messageEntry.item(0).getTextContent());
            }
            return status;
        }
        catch (XPathExpressionException e) {
            String error = "Unexpected error in getStatus." + e.getMessage();
            LOGGER.error(error);
            throw new IllegalArgumentException(error);
        }
    }

    public static String generateNameId(String value, String spnq, String format, String nq, X509Certificate cert) {
        String res = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();
            Element nameId = doc.createElement("saml:NameID");
            if (spnq != null && !spnq.isEmpty()) {
                nameId.setAttribute("SPNameQualifier", spnq);
            }
            if (format != null && !format.isEmpty()) {
                nameId.setAttribute("Format", format);
            }
            if (nq != null && !nq.isEmpty()) {
                nameId.setAttribute("NameQualifier", nq);
            }
            nameId.appendChild(doc.createTextNode(value));
            doc.appendChild(nameId);
            if (cert != null) {
                SecretKey symmetricKey = Util.generateSymmetricKey();
                XMLCipher xmlCipher = XMLCipher.getInstance("http://www.w3.org/2001/04/xmlenc#aes128-cbc");
                xmlCipher.init(1, symmetricKey);
                XMLCipher keyCipher = XMLCipher.getInstance("http://www.w3.org/2001/04/xmlenc#rsa-1_5");
                keyCipher.init(3, cert.getPublicKey());
                EncryptedKey encryptedKey = keyCipher.encryptKey(doc, symmetricKey);
                EncryptedData encryptedData = xmlCipher.getEncryptedData();
                KeyInfo keyInfo = new KeyInfo(doc);
                keyInfo.add(encryptedKey);
                encryptedData.setKeyInfo(keyInfo);
                xmlCipher.doFinal(doc, nameId, false);
                res = "<saml:EncryptedID>" + Util.convertDocumentToString(doc) + "</saml:EncryptedID>";
            } else {
                res = Util.convertDocumentToString(doc);
            }
        }
        catch (Exception e) {
            LOGGER.error("Error executing generateNameId: " + e.getMessage(), (Throwable)e);
        }
        return res;
    }

    public static String generateNameId(String value, String spnq, String format, X509Certificate cert) {
        return Util.generateNameId(value, spnq, format, null, cert);
    }

    public static String generateNameId(String value, String spnq, String format) {
        return Util.generateNameId(value, spnq, format, null);
    }

    public static String generateNameId(String value) {
        return Util.generateNameId(value, null, null, null);
    }

    private static SecretKey generateSymmetricKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    public static String generateUniqueID(String prefix) {
        if (prefix == null || StringUtils.isEmpty((CharSequence)prefix)) {
            prefix = UNIQUE_ID_PREFIX;
        }
        return prefix + UUID.randomUUID();
    }

    public static String generateUniqueID() {
        return Util.generateUniqueID(null);
    }

    public static long parseDuration(String duration) throws IllegalArgumentException {
        TimeZone timeZone = DateTimeZone.UTC.toTimeZone();
        return Util.parseDuration(duration, Calendar.getInstance(timeZone).getTimeInMillis() / 1000L);
    }

    public static long parseDuration(String durationString, long timestamp) throws IllegalArgumentException {
        boolean haveMinus = false;
        if (durationString.startsWith("-")) {
            durationString = durationString.substring(1);
            haveMinus = true;
        }
        PeriodFormatter periodFormatter = ISOPeriodFormat.standard().withLocale(new Locale("UTC"));
        Period period = periodFormatter.parsePeriod(durationString);
        DateTime dt = new DateTime(timestamp * 1000L, DateTimeZone.UTC);
        DateTime result = null;
        result = haveMinus ? dt.minus(period) : dt.plus(period);
        return result.getMillis() / 1000L;
    }

    public static Long getCurrentTimeStamp() {
        DateTime currentDate = new DateTime(DateTimeZone.UTC);
        return currentDate.getMillis() / 1000L;
    }

    public static long getExpireTime(String cacheDuration, String validUntil) {
        long expireTime = 0L;
        try {
            if (cacheDuration != null && !StringUtils.isEmpty((CharSequence)cacheDuration)) {
                expireTime = Util.parseDuration(cacheDuration);
            }
            if (validUntil != null && !StringUtils.isEmpty((CharSequence)validUntil)) {
                DateTime dt = Util.parseDateTime(validUntil);
                long validUntilTimeInt = dt.getMillis() / 1000L;
                if (expireTime == 0L || expireTime > validUntilTimeInt) {
                    expireTime = validUntilTimeInt;
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error executing getExpireTime: " + e.getMessage(), (Throwable)e);
        }
        return expireTime;
    }

    public static long getExpireTime(String cacheDuration, long validUntil) {
        long expireTime = 0L;
        try {
            if (cacheDuration != null && !StringUtils.isEmpty((CharSequence)cacheDuration)) {
                expireTime = Util.parseDuration(cacheDuration);
            }
            if (expireTime == 0L || expireTime > validUntil) {
                expireTime = validUntil;
            }
        }
        catch (Exception e) {
            LOGGER.error("Error executing getExpireTime: " + e.getMessage(), (Throwable)e);
        }
        return expireTime;
    }

    public static String formatDateTime(long timeInMillis) {
        return DATE_TIME_FORMAT.print(timeInMillis);
    }

    public static String formatDateTime(long time, boolean millis) {
        if (millis) {
            return DATE_TIME_FORMAT_MILLS.print(time);
        }
        return Util.formatDateTime(time);
    }

    public static DateTime parseDateTime(String dateTime) {
        DateTime parsedData = null;
        try {
            parsedData = DATE_TIME_FORMAT.parseDateTime(dateTime);
        }
        catch (Exception e) {
            return DATE_TIME_FORMAT_MILLS.parseDateTime(dateTime);
        }
        return parsedData;
    }

    private static String toStringUtf8(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static byte[] toBytesUtf8(String str) {
        try {
            return str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    static {
        System.setProperty("org.apache.xml.security.ignoreLineBreaks", "true");
        Init.init();
    }
}

