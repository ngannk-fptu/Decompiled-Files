/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.keyresolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class KeyResolverSpi {
    protected abstract boolean engineCanResolve(Element var1, String var2, StorageResolver var3);

    protected abstract PublicKey engineResolvePublicKey(Element var1, String var2, StorageResolver var3, boolean var4) throws KeyResolverException;

    public PublicKey engineLookupAndResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!this.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return this.engineResolvePublicKey(element, baseURI, storage, secureValidation);
    }

    protected abstract X509Certificate engineResolveX509Certificate(Element var1, String var2, StorageResolver var3, boolean var4) throws KeyResolverException;

    public X509Certificate engineLookupResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!this.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return this.engineResolveX509Certificate(element, baseURI, storage, secureValidation);
    }

    protected abstract SecretKey engineResolveSecretKey(Element var1, String var2, StorageResolver var3, boolean var4) throws KeyResolverException;

    public SecretKey engineLookupAndResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!this.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return this.engineResolveSecretKey(element, baseURI, storage, secureValidation);
    }

    protected abstract PrivateKey engineResolvePrivateKey(Element var1, String var2, StorageResolver var3, boolean var4) throws KeyResolverException;

    public PrivateKey engineLookupAndResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!this.engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return this.engineResolvePrivateKey(element, baseURI, storage, secureValidation);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected static Element getDocFromBytes(byte[] bytes, boolean secureValidation) throws KeyResolverException {
        try (ByteArrayInputStream is = new ByteArrayInputStream(bytes);){
            Document doc = XMLUtils.read(is, secureValidation);
            Element element = doc.getDocumentElement();
            return element;
        }
        catch (XMLParserException ex) {
            throw new KeyResolverException(ex);
        }
        catch (IOException ex) {
            throw new KeyResolverException(ex);
        }
    }
}

