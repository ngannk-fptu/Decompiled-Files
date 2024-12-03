/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.dsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.interfaces.DSAParams;
import java.security.interfaces.DSAPublicKey;
import java.security.spec.DSAParameterSpec;
import java.security.spec.DSAPublicKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DSAParameter;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.crypto.params.DSAPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSAUtil;
import org.bouncycastle.jcajce.provider.asymmetric.util.KeyUtil;
import org.bouncycastle.util.Strings;

public class BCDSAPublicKey
implements DSAPublicKey {
    private static final long serialVersionUID = 1752452449903495175L;
    private static BigInteger ZERO = BigInteger.valueOf(0L);
    private BigInteger y;
    private transient DSAPublicKeyParameters lwKeyParams;
    private transient DSAParams dsaSpec;

    BCDSAPublicKey(DSAPublicKeySpec spec) {
        this.y = spec.getY();
        this.dsaSpec = new DSAParameterSpec(spec.getP(), spec.getQ(), spec.getG());
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    BCDSAPublicKey(DSAPublicKey key) {
        this.y = key.getY();
        this.dsaSpec = key.getParams();
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    BCDSAPublicKey(DSAPublicKeyParameters params) {
        this.y = params.getY();
        this.dsaSpec = params.getParameters() != null ? new DSAParameterSpec(params.getParameters().getP(), params.getParameters().getQ(), params.getParameters().getG()) : null;
        this.lwKeyParams = params;
    }

    public BCDSAPublicKey(SubjectPublicKeyInfo info) {
        ASN1Integer derY;
        try {
            derY = (ASN1Integer)info.parsePublicKey();
        }
        catch (IOException e) {
            throw new IllegalArgumentException("invalid info structure in DSA public key");
        }
        this.y = derY.getValue();
        if (this.isNotNull(info.getAlgorithm().getParameters())) {
            DSAParameter params = DSAParameter.getInstance(info.getAlgorithm().getParameters());
            this.dsaSpec = new DSAParameterSpec(params.getP(), params.getQ(), params.getG());
        } else {
            this.dsaSpec = null;
        }
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    private boolean isNotNull(ASN1Encodable parameters) {
        return parameters != null && !DERNull.INSTANCE.equals(parameters.toASN1Primitive());
    }

    @Override
    public String getAlgorithm() {
        return "DSA";
    }

    @Override
    public String getFormat() {
        return "X.509";
    }

    DSAPublicKeyParameters engineGetKeyParameters() {
        return this.lwKeyParams;
    }

    @Override
    public byte[] getEncoded() {
        if (this.dsaSpec == null) {
            return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa), new ASN1Integer(this.y));
        }
        return KeyUtil.getEncodedSubjectPublicKeyInfo(new AlgorithmIdentifier(X9ObjectIdentifiers.id_dsa, new DSAParameter(this.dsaSpec.getP(), this.dsaSpec.getQ(), this.dsaSpec.getG()).toASN1Primitive()), new ASN1Integer(this.y));
    }

    @Override
    public DSAParams getParams() {
        return this.dsaSpec;
    }

    @Override
    public BigInteger getY() {
        return this.y;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String nl = Strings.lineSeparator();
        buf.append("DSA Public Key [").append(DSAUtil.generateKeyFingerprint(this.y, this.getParams())).append("]").append(nl);
        buf.append("            Y: ").append(this.getY().toString(16)).append(nl);
        return buf.toString();
    }

    public int hashCode() {
        if (this.dsaSpec != null) {
            return this.getY().hashCode() ^ this.getParams().getG().hashCode() ^ this.getParams().getP().hashCode() ^ this.getParams().getQ().hashCode();
        }
        return this.getY().hashCode();
    }

    public boolean equals(Object o) {
        if (!(o instanceof DSAPublicKey)) {
            return false;
        }
        DSAPublicKey other = (DSAPublicKey)o;
        if (this.dsaSpec != null) {
            return this.getY().equals(other.getY()) && other.getParams() != null && this.getParams().getG().equals(other.getParams().getG()) && this.getParams().getP().equals(other.getParams().getP()) && this.getParams().getQ().equals(other.getParams().getQ());
        }
        return this.getY().equals(other.getY()) && other.getParams() == null;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        BigInteger p = (BigInteger)in.readObject();
        this.dsaSpec = p.equals(ZERO) ? null : new DSAParameterSpec(p, (BigInteger)in.readObject(), (BigInteger)in.readObject());
        this.lwKeyParams = new DSAPublicKeyParameters(this.y, DSAUtil.toDSAParameters(this.dsaSpec));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if (this.dsaSpec == null) {
            out.writeObject(ZERO);
        } else {
            out.writeObject(this.dsaSpec.getP());
            out.writeObject(this.dsaSpec.getQ());
            out.writeObject(this.dsaSpec.getG());
        }
    }
}

