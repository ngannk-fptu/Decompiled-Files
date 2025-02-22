/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1RelativeOID;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UniversalType;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.OIDTokenizer;
import org.bouncycastle.util.Arrays;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class ASN1ObjectIdentifier
extends ASN1Primitive {
    static final ASN1UniversalType TYPE = new ASN1UniversalType(ASN1ObjectIdentifier.class, 6){

        @Override
        ASN1Primitive fromImplicitPrimitive(DEROctetString octetString) {
            return ASN1ObjectIdentifier.createPrimitive(octetString.getOctets(), false);
        }
    };
    private static final long LONG_LIMIT = 0xFFFFFFFFFFFF80L;
    private static final ConcurrentMap<OidHandle, ASN1ObjectIdentifier> pool = new ConcurrentHashMap<OidHandle, ASN1ObjectIdentifier>();
    private final String identifier;
    private byte[] contents;

    public static ASN1ObjectIdentifier fromContents(byte[] contents) {
        return ASN1ObjectIdentifier.createPrimitive(contents, true);
    }

    public static ASN1ObjectIdentifier getInstance(Object obj) {
        if (obj == null || obj instanceof ASN1ObjectIdentifier) {
            return (ASN1ObjectIdentifier)obj;
        }
        if (obj instanceof ASN1Encodable) {
            ASN1Primitive primitive = ((ASN1Encodable)obj).toASN1Primitive();
            if (primitive instanceof ASN1ObjectIdentifier) {
                return (ASN1ObjectIdentifier)primitive;
            }
        } else if (obj instanceof byte[]) {
            try {
                return (ASN1ObjectIdentifier)TYPE.fromByteArray((byte[])obj);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("failed to construct object identifier from byte[]: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static ASN1ObjectIdentifier getInstance(ASN1TaggedObject taggedObject, boolean explicit) {
        ASN1Primitive base;
        if (!(explicit || taggedObject.isParsed() || 128 != taggedObject.getTagClass() || (base = taggedObject.getBaseObject().toASN1Primitive()) instanceof ASN1ObjectIdentifier)) {
            return ASN1ObjectIdentifier.fromContents(ASN1OctetString.getInstance(base).getOctets());
        }
        return (ASN1ObjectIdentifier)TYPE.getContextInstance(taggedObject, explicit);
    }

    ASN1ObjectIdentifier(byte[] contents, boolean clone) {
        StringBuffer objId = new StringBuffer();
        long value = 0L;
        BigInteger bigValue = null;
        boolean first = true;
        for (int i = 0; i != contents.length; ++i) {
            int b = contents[i] & 0xFF;
            if (value <= 0xFFFFFFFFFFFF80L) {
                value += (long)(b & 0x7F);
                if ((b & 0x80) == 0) {
                    if (first) {
                        if (value < 40L) {
                            objId.append('0');
                        } else if (value < 80L) {
                            objId.append('1');
                            value -= 40L;
                        } else {
                            objId.append('2');
                            value -= 80L;
                        }
                        first = false;
                    }
                    objId.append('.');
                    objId.append(value);
                    value = 0L;
                    continue;
                }
                value <<= 7;
                continue;
            }
            if (bigValue == null) {
                bigValue = BigInteger.valueOf(value);
            }
            bigValue = bigValue.or(BigInteger.valueOf(b & 0x7F));
            if ((b & 0x80) == 0) {
                if (first) {
                    objId.append('2');
                    bigValue = bigValue.subtract(BigInteger.valueOf(80L));
                    first = false;
                }
                objId.append('.');
                objId.append(bigValue);
                bigValue = null;
                value = 0L;
                continue;
            }
            bigValue = bigValue.shiftLeft(7);
        }
        this.identifier = objId.toString();
        this.contents = clone ? Arrays.clone(contents) : contents;
    }

    public ASN1ObjectIdentifier(String identifier) {
        if (identifier == null) {
            throw new NullPointerException("'identifier' cannot be null");
        }
        if (!ASN1ObjectIdentifier.isValidIdentifier(identifier)) {
            throw new IllegalArgumentException("string " + identifier + " not an OID");
        }
        this.identifier = identifier;
    }

    ASN1ObjectIdentifier(ASN1ObjectIdentifier oid, String branchID) {
        if (!ASN1RelativeOID.isValidIdentifier(branchID, 0)) {
            throw new IllegalArgumentException("string " + branchID + " not a valid OID branch");
        }
        this.identifier = oid.getId() + "." + branchID;
    }

    public String getId() {
        return this.identifier;
    }

    public ASN1ObjectIdentifier branch(String branchID) {
        return new ASN1ObjectIdentifier(this, branchID);
    }

    public boolean on(ASN1ObjectIdentifier stem) {
        String id = this.getId();
        String stemId = stem.getId();
        return id.length() > stemId.length() && id.charAt(stemId.length()) == '.' && id.startsWith(stemId);
    }

    private void doOutput(ByteArrayOutputStream aOut) {
        OIDTokenizer tok = new OIDTokenizer(this.identifier);
        int first = Integer.parseInt(tok.nextToken()) * 40;
        String secondToken = tok.nextToken();
        if (secondToken.length() <= 18) {
            ASN1RelativeOID.writeField(aOut, (long)first + Long.parseLong(secondToken));
        } else {
            ASN1RelativeOID.writeField(aOut, new BigInteger(secondToken).add(BigInteger.valueOf(first)));
        }
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken();
            if (token.length() <= 18) {
                ASN1RelativeOID.writeField(aOut, Long.parseLong(token));
                continue;
            }
            ASN1RelativeOID.writeField(aOut, new BigInteger(token));
        }
    }

    private synchronized byte[] getContents() {
        if (this.contents == null) {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            this.doOutput(bOut);
            this.contents = bOut.toByteArray();
        }
        return this.contents;
    }

    @Override
    boolean encodeConstructed() {
        return false;
    }

    @Override
    int encodedLength(boolean withTag) {
        return ASN1OutputStream.getLengthOfEncodingDL(withTag, this.getContents().length);
    }

    @Override
    void encode(ASN1OutputStream out, boolean withTag) throws IOException {
        out.writeEncodingDL(withTag, 6, this.getContents());
    }

    @Override
    public int hashCode() {
        return this.identifier.hashCode();
    }

    @Override
    boolean asn1Equals(ASN1Primitive o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ASN1ObjectIdentifier)) {
            return false;
        }
        return this.identifier.equals(((ASN1ObjectIdentifier)o).identifier);
    }

    public String toString() {
        return this.getId();
    }

    private static boolean isValidIdentifier(String identifier) {
        if (identifier.length() < 3 || identifier.charAt(1) != '.') {
            return false;
        }
        char first = identifier.charAt(0);
        if (first < '0' || first > '2') {
            return false;
        }
        return ASN1RelativeOID.isValidIdentifier(identifier, 2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ASN1ObjectIdentifier intern() {
        OidHandle hdl = new OidHandle(this.getContents());
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)pool.get(hdl);
        if (oid == null) {
            ConcurrentMap<OidHandle, ASN1ObjectIdentifier> concurrentMap = pool;
            synchronized (concurrentMap) {
                if (!pool.containsKey(hdl)) {
                    pool.put(hdl, this);
                    return this;
                }
                return (ASN1ObjectIdentifier)pool.get(hdl);
            }
        }
        return oid;
    }

    static ASN1ObjectIdentifier createPrimitive(byte[] contents, boolean clone) {
        OidHandle hdl = new OidHandle(contents);
        ASN1ObjectIdentifier oid = (ASN1ObjectIdentifier)pool.get(hdl);
        if (oid == null) {
            return new ASN1ObjectIdentifier(contents, clone);
        }
        return oid;
    }

    /*
     * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
     */
    private static class OidHandle {
        private final int key;
        private final byte[] contents;

        OidHandle(byte[] contents) {
            this.key = Arrays.hashCode(contents);
            this.contents = contents;
        }

        public int hashCode() {
            return this.key;
        }

        public boolean equals(Object o) {
            if (o instanceof OidHandle) {
                return Arrays.areEqual(this.contents, ((OidHandle)o).contents);
            }
            return false;
        }
    }
}

