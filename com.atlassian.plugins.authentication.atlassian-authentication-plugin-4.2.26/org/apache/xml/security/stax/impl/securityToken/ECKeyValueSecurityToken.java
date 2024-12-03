/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.securityToken;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import org.apache.xml.security.algorithms.implementations.ECDSAUtils;
import org.apache.xml.security.binding.xmldsig11.ECKeyValueType;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InboundSecurityContext;
import org.apache.xml.security.stax.impl.securityToken.AbstractInboundSecurityToken;
import org.apache.xml.security.stax.impl.util.IDGenerator;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;

public class ECKeyValueSecurityToken
extends AbstractInboundSecurityToken {
    private ECKeyValueType ecKeyValueType;

    public ECKeyValueSecurityToken(ECKeyValueType ecKeyValueType, InboundSecurityContext inboundSecurityContext) throws XMLSecurityException {
        super(inboundSecurityContext, IDGenerator.generateID(null), SecurityTokenConstants.KeyIdentifier_KeyValue, true);
        if (ecKeyValueType.getECParameters() != null) {
            throw new XMLSecurityException("stax.ecParametersNotSupported");
        }
        if (ecKeyValueType.getNamedCurve() == null) {
            throw new XMLSecurityException("stax.namedCurveMissing");
        }
        this.ecKeyValueType = ecKeyValueType;
    }

    private PublicKey buildPublicKey(ECKeyValueType ecKeyValueType) throws InvalidKeySpecException, NoSuchAlgorithmException, XMLSecurityException {
        ECDSAUtils.ECCurveDefinition ecCurveDefinition;
        String oid = ecKeyValueType.getNamedCurve().getURI();
        if (oid.startsWith("urn:oid:")) {
            oid = oid.substring(8);
        }
        if ((ecCurveDefinition = ECDSAUtils.getECCurveDefinition(oid)) == null) {
            throw new XMLSecurityException("stax.unsupportedKeyValue");
        }
        EllipticCurve curve = new EllipticCurve(new ECFieldFp(new BigInteger(ecCurveDefinition.getField(), 16)), new BigInteger(ecCurveDefinition.getA(), 16), new BigInteger(ecCurveDefinition.getB(), 16));
        ECPoint ecPointG = ECDSAUtils.decodePoint(ecKeyValueType.getPublicKey(), curve);
        ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(new ECPoint(ecPointG.getAffineX(), ecPointG.getAffineY()), new ECParameterSpec(curve, new ECPoint(new BigInteger(ecCurveDefinition.getX(), 16), new BigInteger(ecCurveDefinition.getY(), 16)), new BigInteger(ecCurveDefinition.getN(), 16), ecCurveDefinition.getH()));
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(ecPublicKeySpec);
    }

    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        if (super.getPublicKey() == null) {
            try {
                this.setPublicKey(this.buildPublicKey(this.ecKeyValueType));
            }
            catch (InvalidKeySpecException e) {
                throw new XMLSecurityException(e);
            }
            catch (NoSuchAlgorithmException e) {
                throw new XMLSecurityException(e);
            }
        }
        return super.getPublicKey();
    }

    @Override
    public boolean isAsymmetric() {
        return true;
    }

    @Override
    public SecurityTokenConstants.TokenType getTokenType() {
        return SecurityTokenConstants.KeyValueToken;
    }
}

