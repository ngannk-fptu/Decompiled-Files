/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.keys.content.keyvalues;

import java.io.IOException;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECFieldFp;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.EllipticCurve;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import javax.xml.crypto.MarshalException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.content.keyvalues.KeyValueContent;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.Signature11ElementProxy;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ECKeyValue
extends Signature11ElementProxy
implements KeyValueContent {
    private static final Curve SECP256R1 = ECKeyValue.initializeCurve("secp256r1 [NIST P-256, X9.62 prime256v1]", "1.2.840.10045.3.1.7", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFF", "FFFFFFFF00000001000000000000000000000000FFFFFFFFFFFFFFFFFFFFFFFC", "5AC635D8AA3A93E7B3EBBD55769886BC651D06B0CC53B0F63BCE3C3E27D2604B", "6B17D1F2E12C4247F8BCE6E563A440F277037D812DEB33A0F4A13945D898C296", "4FE342E2FE1A7F9B8EE7EB4A7C0F9E162BCE33576B315ECECBB6406837BF51F5", "FFFFFFFF00000000FFFFFFFFFFFFFFFFBCE6FAADA7179E84F3B9CAC2FC632551", 1);
    private static final Curve SECP384R1 = ECKeyValue.initializeCurve("secp384r1 [NIST P-384]", "1.3.132.0.34", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFF", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFFFF0000000000000000FFFFFFFC", "B3312FA7E23EE7E4988E056BE3F82D19181D9C6EFE8141120314088F5013875AC656398D8A2ED19D2A85C8EDD3EC2AEF", "AA87CA22BE8B05378EB1C71EF320AD746E1D3B628BA79B9859F741E082542A385502F25DBF55296C3A545E3872760AB7", "3617DE4A96262C6F5D9E98BF9292DC29F8F41DBD289A147CE9DA3113B5F0B8C00A60B1CE1D7E819D7A431D7C90EA0E5F", "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC7634D81F4372DDF581A0DB248B0A77AECEC196ACCC52973", 1);
    private static final Curve SECP521R1 = ECKeyValue.initializeCurve("secp521r1 [NIST P-521]", "1.3.132.0.35", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFC", "0051953EB9618E1C9A1F929A21A0B68540EEA2DA725B99B315F3B8B489918EF109E156193951EC7E937B1652C0BD3BB1BF073573DF883D2C34F1EF451FD46B503F00", "00C6858E06B70404E9CD9E3ECB662395B4429C648139053FB521F828AF606B4D3DBAA14B5E77EFE75928FE1DC127A2FFA8DE3348B3C1856A429BF97E7E31C2E5BD66", "011839296A789A3BC0045C8A5FB42C7D1BD998F54449579B446817AFBD17273E662C97EE72995EF42640C550B9013FAD0761353C7086A272C24088BE94769FD16650", "01FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFA51868783BF2F966B7FCC0148F709A5D03BB5C9B8899C47AEBB6FB71E91386409", 1);

    private static Curve initializeCurve(String name, String oid, String sfield, String a, String b, String x, String y, String n, int h) {
        BigInteger p = ECKeyValue.bigInt(sfield);
        ECFieldFp field = new ECFieldFp(p);
        EllipticCurve curve = new EllipticCurve(field, ECKeyValue.bigInt(a), ECKeyValue.bigInt(b));
        ECPoint g = new ECPoint(ECKeyValue.bigInt(x), ECKeyValue.bigInt(y));
        return new Curve(name, oid, curve, g, ECKeyValue.bigInt(n), h);
    }

    public ECKeyValue(Element element, String baseURI) throws XMLSecurityException {
        super(element, baseURI);
    }

    public ECKeyValue(Document doc, Key key) throws IllegalArgumentException {
        super(doc);
        String oid;
        ECParameterSpec ecParams;
        this.addReturnToSelf();
        if (key instanceof ECPublicKey) {
            ecParams = ((ECPublicKey)key).getParams();
            oid = ECKeyValue.getCurveOid(ecParams);
            if (oid == null) {
                throw new IllegalArgumentException("Invalid ECParameterSpec");
            }
        } else {
            Object[] exArgs = new Object[]{"ECKeyValue", key.getClass().getName()};
            throw new IllegalArgumentException(I18n.translate("KeyValue.IllegalArgument", exArgs));
        }
        Element namedCurveElement = XMLUtils.createElementInSignature11Space(this.getDocument(), "NamedCurve");
        namedCurveElement.setAttributeNS(null, "URI", "urn:oid:" + oid);
        this.appendSelf(namedCurveElement);
        this.addReturnToSelf();
        ECPoint ecPoint = ((ECPublicKey)key).getW();
        byte[] secPublicKey = ECKeyValue.encodePoint(ecPoint, ecParams.getCurve());
        String encoded = XMLUtils.encodeToString(secPublicKey);
        Element publicKeyElement = XMLUtils.createElementInSignature11Space(this.getDocument(), "PublicKey");
        Text text = this.getDocument().createTextNode(encoded);
        publicKeyElement.appendChild(text);
        this.appendSelf(publicKeyElement);
        this.addReturnToSelf();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public PublicKey getPublicKey() throws XMLSecurityException {
        try {
            ECParameterSpec ecParams = null;
            Element curElem = ECKeyValue.getFirstChildElement(this.getElement());
            if (curElem == null) {
                throw new MarshalException("KeyValue must contain at least one type");
            }
            if ("ECParameters".equals(curElem.getLocalName()) && "http://www.w3.org/2009/xmldsig11#".equals(curElem.getNamespaceURI())) {
                throw new UnsupportedOperationException("ECParameters not supported");
            }
            if (!"NamedCurve".equals(curElem.getLocalName()) || !"http://www.w3.org/2009/xmldsig11#".equals(curElem.getNamespaceURI())) throw new MarshalException("Invalid ECKeyValue");
            String uri = null;
            if (curElem.hasAttributeNS(null, "URI")) {
                uri = curElem.getAttributeNS(null, "URI");
            }
            if (uri == null || !uri.startsWith("urn:oid:")) throw new MarshalException("Invalid NamedCurve URI");
            String oid = uri.substring("urn:oid:".length());
            ecParams = ECKeyValue.getECParameterSpec(oid);
            if (ecParams == null) {
                throw new MarshalException("Invalid curve OID");
            }
            curElem = ECKeyValue.getNextSiblingElement(curElem, "PublicKey", "http://www.w3.org/2009/xmldsig11#");
            ECPoint ecPoint = null;
            try {
                String content = XMLUtils.getFullTextChildrenFromNode(curElem);
                ecPoint = ECKeyValue.decodePoint(XMLUtils.decode(content), ecParams.getCurve());
            }
            catch (IOException ioe) {
                throw new MarshalException("Invalid EC Point", ioe);
            }
            ECPublicKeySpec spec = new ECPublicKeySpec(ecPoint, ecParams);
            return KeyFactory.getInstance("EC").generatePublic(spec);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException | MarshalException ex) {
            throw new XMLSecurityException(ex);
        }
    }

    @Override
    public String getBaseLocalName() {
        return "ECKeyValue";
    }

    private static Element getFirstChildElement(Node node) {
        Node child;
        for (child = node.getFirstChild(); child != null && child.getNodeType() != 1; child = child.getNextSibling()) {
        }
        return (Element)child;
    }

    private static Element getNextSiblingElement(Node node, String localName, String namespaceURI) throws MarshalException {
        return ECKeyValue.verifyElement(ECKeyValue.getNextSiblingElement(node), localName, namespaceURI);
    }

    private static Element getNextSiblingElement(Node node) {
        Node sibling;
        for (sibling = node.getNextSibling(); sibling != null && sibling.getNodeType() != 1; sibling = sibling.getNextSibling()) {
        }
        return (Element)sibling;
    }

    private static Element verifyElement(Element elem, String localName, String namespaceURI) throws MarshalException {
        if (elem == null) {
            throw new MarshalException("Missing " + localName + " element");
        }
        String name = elem.getLocalName();
        String namespace = elem.getNamespaceURI();
        if (!name.equals(localName) || namespace == null && namespaceURI != null || namespace != null && !namespace.equals(namespaceURI)) {
            throw new MarshalException("Invalid element name: " + namespace + ":" + name + ", expected " + namespaceURI + ":" + localName);
        }
        return elem;
    }

    private static String getCurveOid(ECParameterSpec params) {
        Curve match;
        if (ECKeyValue.matchCurve(params, SECP256R1)) {
            match = SECP256R1;
        } else if (ECKeyValue.matchCurve(params, SECP384R1)) {
            match = SECP384R1;
        } else if (ECKeyValue.matchCurve(params, SECP521R1)) {
            match = SECP521R1;
        } else {
            return null;
        }
        return match.getObjectId();
    }

    private static boolean matchCurve(ECParameterSpec params, Curve curve) {
        int fieldSize = params.getCurve().getField().getFieldSize();
        return curve.getCurve().getField().getFieldSize() == fieldSize && curve.getCurve().equals(params.getCurve()) && curve.getGenerator().equals(params.getGenerator()) && curve.getOrder().equals(params.getOrder()) && curve.getCofactor() == params.getCofactor();
    }

    private static ECPoint decodePoint(byte[] data, EllipticCurve curve) throws IOException {
        if (data.length == 0 || data[0] != 4) {
            throw new IOException("Only uncompressed point format supported");
        }
        int n = (data.length - 1) / 2;
        if (n != curve.getField().getFieldSize() + 7 >> 3) {
            throw new IOException("Point does not match field size");
        }
        byte[] xb = Arrays.copyOfRange(data, 1, 1 + n);
        byte[] yb = Arrays.copyOfRange(data, n + 1, n + 1 + n);
        return new ECPoint(new BigInteger(1, xb), new BigInteger(1, yb));
    }

    private static byte[] encodePoint(ECPoint point, EllipticCurve curve) {
        int n = curve.getField().getFieldSize() + 7 >> 3;
        byte[] xb = ECKeyValue.trimZeroes(point.getAffineX().toByteArray());
        byte[] yb = ECKeyValue.trimZeroes(point.getAffineY().toByteArray());
        if (xb.length > n || yb.length > n) {
            throw new RuntimeException("Point coordinates do not match field size");
        }
        byte[] b = new byte[1 + (n << 1)];
        b[0] = 4;
        System.arraycopy(xb, 0, b, n - xb.length + 1, xb.length);
        System.arraycopy(yb, 0, b, b.length - yb.length, yb.length);
        return b;
    }

    private static byte[] trimZeroes(byte[] b) {
        int i;
        for (i = 0; i < b.length - 1 && b[i] == 0; ++i) {
        }
        if (i == 0) {
            return b;
        }
        return Arrays.copyOfRange(b, i, b.length);
    }

    private static ECParameterSpec getECParameterSpec(String oid) {
        if (oid.equals(ECKeyValue.SECP256R1.getObjectId())) {
            return SECP256R1;
        }
        if (oid.equals(ECKeyValue.SECP384R1.getObjectId())) {
            return SECP384R1;
        }
        if (oid.equals(ECKeyValue.SECP521R1.getObjectId())) {
            return SECP521R1;
        }
        return null;
    }

    private static BigInteger bigInt(String s) {
        return new BigInteger(s, 16);
    }

    static final class Curve
    extends ECParameterSpec {
        private final String name;
        private final String oid;

        Curve(String name, String oid, EllipticCurve curve, ECPoint g, BigInteger n, int h) {
            super(curve, g, n, h);
            this.name = name;
            this.oid = oid;
        }

        private String getName() {
            return this.name;
        }

        private String getObjectId() {
            return this.oid;
        }
    }
}

