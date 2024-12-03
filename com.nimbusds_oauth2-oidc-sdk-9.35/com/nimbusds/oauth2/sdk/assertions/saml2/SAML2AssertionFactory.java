/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 *  net.shibboleth.utilities.java.support.xml.SerializeSupport
 *  org.opensaml.core.xml.XMLObject
 *  org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
 *  org.opensaml.core.xml.io.MarshallingException
 *  org.opensaml.saml.saml2.core.Assertion
 *  org.opensaml.security.credential.BasicCredential
 *  org.opensaml.security.credential.Credential
 *  org.opensaml.security.credential.UsageType
 *  org.opensaml.xmlsec.signature.Signature
 *  org.opensaml.xmlsec.signature.support.SignatureException
 *  org.opensaml.xmlsec.signature.support.Signer
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2AssertionDetails;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2Utils;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import net.jcip.annotations.ThreadSafe;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.Signer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@ThreadSafe
public class SAML2AssertionFactory {
    public static Assertion create(SAML2AssertionDetails details, String xmlDsigAlg, Credential credential) {
        Assertion a = details.toSAML2Assertion();
        Signature signature = SAML2Utils.buildSAMLObject(Signature.class);
        signature.setSigningCredential(credential);
        signature.setSignatureAlgorithm(xmlDsigAlg);
        signature.setCanonicalizationAlgorithm("http://www.w3.org/2001/10/xml-exc-c14n#");
        a.setSignature(signature);
        try {
            XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller((XMLObject)a).marshall((XMLObject)a);
            Signer.signObject((Signature)signature);
        }
        catch (MarshallingException | SignatureException e) {
            throw new SerializeException(e.getMessage(), e);
        }
        return a;
    }

    public static Element createAsElement(SAML2AssertionDetails details, String xmlDsigAlg, Credential credential) {
        Assertion a = SAML2AssertionFactory.create(details, xmlDsigAlg, credential);
        try {
            return XMLObjectProviderRegistrySupport.getMarshallerFactory().getMarshaller((XMLObject)a).marshall((XMLObject)a);
        }
        catch (MarshallingException e) {
            throw new SerializeException(e.getMessage(), e);
        }
    }

    public static String createAsString(SAML2AssertionDetails details, String xmlDsigAlg, Credential credential) {
        Element a = SAML2AssertionFactory.createAsElement(details, xmlDsigAlg, credential);
        String xml = SerializeSupport.nodeToString((Node)a);
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        if (xml.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
            xml = xml.substring("<?xml version=\"1.0\" encoding=\"UTF-8\"?>".length());
        }
        return xml;
    }

    public static String createAsString(SAML2AssertionDetails details, RSAPublicKey rsaPublicKey, RSAPrivateKey rsaPrivateKey) {
        BasicCredential credential = new BasicCredential((PublicKey)rsaPublicKey, (PrivateKey)rsaPrivateKey);
        credential.setUsageType(UsageType.SIGNING);
        return SAML2AssertionFactory.createAsString(details, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", (Credential)credential);
    }

    private SAML2AssertionFactory() {
    }
}

