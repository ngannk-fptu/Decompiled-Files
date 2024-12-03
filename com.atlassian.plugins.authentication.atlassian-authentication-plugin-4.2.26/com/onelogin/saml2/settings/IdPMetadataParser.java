/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.settings;

import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.onelogin.saml2.util.Util;
import java.io.InputStream;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.xpath.XPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class IdPMetadataParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdPMetadataParser.class);

    public static Map<String, Object> parseXML(Document xmlDocument, String entityId, String desiredNameIdFormat, String desiredSSOBinding, String desiredSLOBinding) throws XPathException {
        LinkedHashMap<String, Object> metadataInfo;
        block15: {
            metadataInfo = new LinkedHashMap<String, Object>();
            try {
                NodeList sloNodes;
                NodeList ssoNodes;
                Node entityIDNode;
                String idpDescryptorXPath;
                NodeList idpDescriptorNodes;
                String customIdPStr = "";
                if (entityId != null && !entityId.isEmpty()) {
                    customIdPStr = "[@entityID=\"" + entityId + "\"]";
                }
                if ((idpDescriptorNodes = Util.query(xmlDocument, idpDescryptorXPath = "//md:EntityDescriptor" + customIdPStr + "/md:IDPSSODescriptor")).getLength() <= 0) break block15;
                Node idpDescriptorNode = idpDescriptorNodes.item(0);
                if ((entityId == null || entityId.isEmpty()) && (entityIDNode = idpDescriptorNode.getParentNode().getAttributes().getNamedItem("entityID")) != null) {
                    entityId = entityIDNode.getNodeValue();
                }
                if (entityId != null && !entityId.isEmpty()) {
                    metadataInfo.put("onelogin.saml2.idp.entityid", entityId);
                }
                if ((ssoNodes = Util.query(xmlDocument, "./md:SingleSignOnService[@Binding=\"" + desiredSSOBinding + "\"]", idpDescriptorNode)).getLength() < 1) {
                    ssoNodes = Util.query(xmlDocument, "./md:SingleSignOnService", idpDescriptorNode);
                }
                if (ssoNodes.getLength() > 0) {
                    metadataInfo.put("onelogin.saml2.idp.single_sign_on_service.url", ssoNodes.item(0).getAttributes().getNamedItem("Location").getNodeValue());
                    metadataInfo.put("onelogin.saml2.idp.single_sign_on_service.binding", ssoNodes.item(0).getAttributes().getNamedItem("Binding").getNodeValue());
                }
                if ((sloNodes = Util.query(xmlDocument, "./md:SingleLogoutService[@Binding=\"" + desiredSLOBinding + "\"]", idpDescriptorNode)).getLength() < 1) {
                    sloNodes = Util.query(xmlDocument, "./md:SingleLogoutService", idpDescriptorNode);
                }
                if (sloNodes.getLength() > 0) {
                    metadataInfo.put("onelogin.saml2.idp.single_logout_service.url", sloNodes.item(0).getAttributes().getNamedItem("Location").getNodeValue());
                    metadataInfo.put("onelogin.saml2.idp.single_logout_service.binding", sloNodes.item(0).getAttributes().getNamedItem("Binding").getNodeValue());
                }
                NodeList keyDescriptorCertSigningNodes = Util.query(xmlDocument, "./md:KeyDescriptor[not(contains(@use, \"encryption\"))]/ds:KeyInfo/ds:X509Data/ds:X509Certificate", idpDescriptorNode);
                NodeList keyDescriptorCertEncryptionNodes = Util.query(xmlDocument, "./md:KeyDescriptor[not(contains(@use, \"signing\"))]/ds:KeyInfo/ds:X509Data/ds:X509Certificate", idpDescriptorNode);
                if (keyDescriptorCertSigningNodes.getLength() > 0 || keyDescriptorCertEncryptionNodes.getLength() > 0) {
                    boolean hasEncryptionCert = keyDescriptorCertEncryptionNodes.getLength() > 0;
                    String encryptionCert = null;
                    if (hasEncryptionCert) {
                        encryptionCert = keyDescriptorCertEncryptionNodes.item(0).getTextContent();
                        metadataInfo.put("onelogin.saml2.idp.x509cert", encryptionCert);
                    }
                    if (keyDescriptorCertSigningNodes.getLength() > 0) {
                        int index = 0;
                        for (int i = 0; i < keyDescriptorCertSigningNodes.getLength(); ++i) {
                            String signingCert = keyDescriptorCertSigningNodes.item(i).getTextContent();
                            if (i == 0 && !hasEncryptionCert) {
                                metadataInfo.put("onelogin.saml2.idp.x509cert", signingCert);
                                continue;
                            }
                            if (hasEncryptionCert && encryptionCert.equals(signingCert)) continue;
                            metadataInfo.put("onelogin.saml2.idp.x509certMulti." + index++, signingCert);
                        }
                    }
                }
                NodeList nameIdFormatNodes = Util.query(xmlDocument, "./md:NameIDFormat", idpDescriptorNode);
                for (int i = 0; i < nameIdFormatNodes.getLength(); ++i) {
                    String nameIdFormat = nameIdFormatNodes.item(i).getTextContent();
                    if (nameIdFormat == null || desiredNameIdFormat != null && !desiredNameIdFormat.equals(nameIdFormat)) continue;
                    metadataInfo.put("onelogin.saml2.sp.nameidformat", nameIdFormat);
                    break;
                }
            }
            catch (XPathException e) {
                String errorMsg = "Error parsing metadata. " + e.getMessage();
                LOGGER.error(errorMsg, (Throwable)e);
                throw e;
            }
        }
        return metadataInfo;
    }

    public static Map<String, Object> parseXML(Document xmlDocument, String entityId) throws XPathException {
        return IdPMetadataParser.parseXML(xmlDocument, entityId, null, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    }

    public static Map<String, Object> parseXML(Document xmlDocument) throws XPathException {
        return IdPMetadataParser.parseXML(xmlDocument, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static Map<String, Object> parseFileXML(String xmlFileName, String entityId, String desiredNameIdFormat, String desiredSSOBinding, String desiredSLOBinding) throws Exception {
        ClassLoader classLoader = IdPMetadataParser.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(xmlFileName);){
            if (inputStream != null) {
                Document xmlDocument = Util.parseXML(new InputSource(inputStream));
                Map<String, Object> map = IdPMetadataParser.parseXML(xmlDocument, entityId, desiredNameIdFormat, desiredSSOBinding, desiredSLOBinding);
                return map;
            }
            throw new Exception("XML file '" + xmlFileName + "' not found in the classpath");
        }
        catch (Exception e) {
            String errorMsg = "XML file'" + xmlFileName + "' cannot be loaded." + e.getMessage();
            LOGGER.error(errorMsg, (Throwable)e);
            throw new Error(errorMsg, 1);
        }
    }

    public static Map<String, Object> parseFileXML(String xmlFileName, String entityId) throws Exception {
        return IdPMetadataParser.parseFileXML(xmlFileName, entityId, null, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    }

    public static Map<String, Object> parseFileXML(String xmlFileName) throws Exception {
        return IdPMetadataParser.parseFileXML(xmlFileName, null);
    }

    public static Map<String, Object> parseRemoteXML(URL xmlURL, String entityId, String desiredNameIdFormat, String desiredSSOBinding, String desiredSLOBinding) throws Exception {
        Document xmlDocument = Util.parseXML(new InputSource(xmlURL.openStream()));
        return IdPMetadataParser.parseXML(xmlDocument, entityId, desiredNameIdFormat, desiredSSOBinding, desiredSLOBinding);
    }

    public static Map<String, Object> parseRemoteXML(URL xmlURL, String entityId) throws Exception {
        return IdPMetadataParser.parseRemoteXML(xmlURL, entityId, null, "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect", "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect");
    }

    public static Map<String, Object> parseRemoteXML(URL xmlURL) throws Exception {
        return IdPMetadataParser.parseRemoteXML(xmlURL, null);
    }

    public static Saml2Settings injectIntoSettings(Saml2Settings settings, Map<String, Object> metadataInfo) {
        SettingsBuilder settingsBuilder = new SettingsBuilder().fromValues(metadataInfo);
        settingsBuilder.build(settings);
        return settings;
    }
}

