/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.content;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.KeyInfoContent;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class DEREncodedKeyValue
extends Signature11ElementProxy
implements KeyInfoContent {
    private static final String[] supportedKeyTypes = new String[]{"RSA", "DSA", "EC"};

    public DEREncodedKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public DEREncodedKeyValue(Document doc, PublicKey publicKey) throws XMLSecurityException {
        super(doc);
        this.addBase64Text(this.getEncodedDER(publicKey));
    }

    public DEREncodedKeyValue(Document doc, byte[] encodedKey) {
        super(doc);
        this.addBase64Text(encodedKey);
    }

    public void setId(String id) {
        this.setLocalIdAttribute("Id", id);
    }

    public String getId() {
        return this.getLocalAttribute("Id");
    }

    @Override
    public String getBaseLocalName() {
        return "DEREncodedKeyValue";
    }

    public PublicKey getPublicKey() throws XMLSecurityException {
        byte[] encodedKey = this.getBytesFromTextChild();
        for (String keyType : supportedKeyTypes) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance(keyType);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
                PublicKey publicKey = keyFactory.generatePublic(keySpec);
                if (publicKey == null) continue;
                return publicKey;
            }
            catch (NoSuchAlgorithmException | InvalidKeySpecException generalSecurityException) {
                // empty catch block
            }
        }
        throw new XMLSecurityException("DEREncodedKeyValue.UnsupportedEncodedKey");
    }

    protected byte[] getEncodedDER(PublicKey publicKey) throws XMLSecurityException {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getAlgorithm());
            X509EncodedKeySpec keySpec = keyFactory.getKeySpec(publicKey, X509EncodedKeySpec.class);
            return keySpec.getEncoded();
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Object[] exArgs = new Object[]{publicKey.getAlgorithm(), publicKey.getFormat(), publicKey.getClass().getName()};
            throw new XMLSecurityException(e, "DEREncodedKeyValue.UnsupportedPublicKey", exArgs);
        }
    }
}

