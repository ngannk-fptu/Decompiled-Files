/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.est.AttrOrOID
 *  org.bouncycastle.asn1.est.CsrAttrs
 *  org.bouncycastle.util.Encodable
 */
package org.bouncycastle.est;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.est.AttrOrOID;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.est.ESTException;
import org.bouncycastle.util.Encodable;

public class CSRAttributesResponse
implements Encodable {
    private final CsrAttrs csrAttrs;
    private final HashMap<ASN1ObjectIdentifier, AttrOrOID> index;

    public CSRAttributesResponse(byte[] responseEncoding) throws ESTException {
        this(CSRAttributesResponse.parseBytes(responseEncoding));
    }

    public CSRAttributesResponse(CsrAttrs csrAttrs) throws ESTException {
        this.csrAttrs = csrAttrs;
        this.index = new HashMap(csrAttrs.size());
        AttrOrOID[] attrOrOIDs = csrAttrs.getAttrOrOIDs();
        for (int i = 0; i != attrOrOIDs.length; ++i) {
            AttrOrOID attrOrOID = attrOrOIDs[i];
            if (attrOrOID.isOid()) {
                this.index.put(attrOrOID.getOid(), attrOrOID);
                continue;
            }
            this.index.put(attrOrOID.getAttribute().getAttrType(), attrOrOID);
        }
    }

    private static CsrAttrs parseBytes(byte[] responseEncoding) throws ESTException {
        try {
            return CsrAttrs.getInstance((Object)ASN1Primitive.fromByteArray((byte[])responseEncoding));
        }
        catch (Exception e) {
            throw new ESTException("malformed data: " + e.getMessage(), e);
        }
    }

    public boolean hasRequirement(ASN1ObjectIdentifier requirementOid) {
        return this.index.containsKey(requirementOid);
    }

    public boolean isAttribute(ASN1ObjectIdentifier requirementOid) {
        if (this.index.containsKey(requirementOid)) {
            return !this.index.get(requirementOid).isOid();
        }
        return false;
    }

    public boolean isEmpty() {
        return this.csrAttrs.size() == 0;
    }

    public Collection<ASN1ObjectIdentifier> getRequirements() {
        return this.index.keySet();
    }

    public byte[] getEncoded() throws IOException {
        return this.csrAttrs.getEncoded();
    }
}

