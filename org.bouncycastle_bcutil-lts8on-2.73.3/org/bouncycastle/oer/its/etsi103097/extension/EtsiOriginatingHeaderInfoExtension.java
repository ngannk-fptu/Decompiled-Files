/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Sequence
 */
package org.bouncycastle.oer.its.etsi103097.extension;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941CrlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.EtsiTs102941DeltaCtlRequest;
import org.bouncycastle.oer.its.etsi103097.extension.ExtId;
import org.bouncycastle.oer.its.etsi103097.extension.Extension;

public class EtsiOriginatingHeaderInfoExtension
extends Extension {
    public EtsiOriginatingHeaderInfoExtension(ExtId id, ASN1Encodable content) {
        super(id, content);
    }

    private EtsiOriginatingHeaderInfoExtension(ASN1Sequence sequence) {
        super(sequence);
    }

    public static EtsiOriginatingHeaderInfoExtension getInstance(Object o) {
        if (o instanceof EtsiOriginatingHeaderInfoExtension) {
            return (EtsiOriginatingHeaderInfoExtension)((Object)o);
        }
        if (o != null) {
            return new EtsiOriginatingHeaderInfoExtension(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public EtsiTs102941CrlRequest getEtsiTs102941CrlRequest() {
        return EtsiTs102941CrlRequest.getInstance(this.getContent());
    }

    public EtsiTs102941DeltaCtlRequest getEtsiTs102941DeltaCtlRequest() {
        return EtsiTs102941DeltaCtlRequest.getInstance(this.getContent());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ExtId id;
        private ASN1Encodable encodable;

        public Builder setId(ExtId id) {
            this.id = id;
            return this;
        }

        public Builder setEncodable(ASN1Encodable encodable) {
            this.encodable = encodable;
            return this;
        }

        public Builder setEtsiTs102941CrlRequest(EtsiTs102941CrlRequest value) {
            this.id = Extension.etsiTs102941CrlRequestId;
            this.encodable = value;
            return this;
        }

        public Builder setEtsiTs102941DeltaCtlRequest(EtsiTs102941DeltaCtlRequest value) {
            this.id = Extension.etsiTs102941DeltaCtlRequestId;
            this.encodable = value;
            return this;
        }

        public EtsiOriginatingHeaderInfoExtension createEtsiOriginatingHeaderInfoExtension() {
            return new EtsiOriginatingHeaderInfoExtension(this.id, this.encodable);
        }
    }
}

