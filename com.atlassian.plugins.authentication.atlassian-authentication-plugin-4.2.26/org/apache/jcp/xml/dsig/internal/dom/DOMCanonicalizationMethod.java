/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.xml.crypto.Data;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.TransformException;
import javax.xml.crypto.dsig.TransformService;
import org.apache.jcp.xml.dsig.internal.dom.ApacheCanonicalizer;
import org.apache.jcp.xml.dsig.internal.dom.DOMTransform;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.w3c.dom.Element;

public class DOMCanonicalizationMethod
extends DOMTransform
implements CanonicalizationMethod {
    private static final Set<String> C14N_ALGORITHMS;

    public DOMCanonicalizationMethod(TransformService spi) throws InvalidAlgorithmParameterException {
        super(spi);
        if (!(spi instanceof ApacheCanonicalizer) && !DOMCanonicalizationMethod.isC14Nalg(spi.getAlgorithm())) {
            throw new InvalidAlgorithmParameterException("Illegal CanonicalizationMethod");
        }
    }

    public DOMCanonicalizationMethod(Element cmElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        super(cmElem, context, provider);
        if (!(this.spi instanceof ApacheCanonicalizer) && !DOMCanonicalizationMethod.isC14Nalg(this.spi.getAlgorithm())) {
            throw new MarshalException("Illegal CanonicalizationMethod");
        }
    }

    public Data canonicalize(Data data, XMLCryptoContext xc) throws TransformException {
        return this.transform(data, xc);
    }

    public Data canonicalize(Data data, XMLCryptoContext xc, OutputStream os) throws TransformException {
        return this.transform(data, xc, os);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CanonicalizationMethod)) {
            return false;
        }
        CanonicalizationMethod ocm = (CanonicalizationMethod)o;
        return this.getAlgorithm().equals(ocm.getAlgorithm()) && DOMUtils.paramsEqual(this.getParameterSpec(), ocm.getParameterSpec());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.getAlgorithm().hashCode();
        AlgorithmParameterSpec spec = this.getParameterSpec();
        if (spec != null) {
            result = 31 * result + spec.hashCode();
        }
        return result;
    }

    private static boolean isC14Nalg(String alg) {
        return alg != null && C14N_ALGORITHMS.contains(alg);
    }

    static {
        HashSet<String> algorithms = new HashSet<String>();
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        algorithms.add("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#");
        algorithms.add("http://www.w3.org/2001/10/xml-exc-c14n#WithComments");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11");
        algorithms.add("http://www.w3.org/2006/12/xml-c14n11#WithComments");
        C14N_ALGORITHMS = Collections.unmodifiableSet(algorithms);
    }
}

