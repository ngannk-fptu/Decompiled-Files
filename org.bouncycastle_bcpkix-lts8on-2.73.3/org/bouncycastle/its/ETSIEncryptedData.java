/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.oer.Element
 *  org.bouncycastle.oer.OEREncoder
 *  org.bouncycastle.oer.OERInputStream
 *  org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted
 *  org.bouncycastle.oer.its.ieee1609dot2.EncryptedData
 *  org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content
 *  org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo
 *  org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module
 *  org.bouncycastle.util.CollectionStore
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.its;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.its.ETSIRecipientInfo;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OEREncoder;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.oer.its.etsi103097.EtsiTs103097DataEncrypted;
import org.bouncycastle.oer.its.ieee1609dot2.EncryptedData;
import org.bouncycastle.oer.its.ieee1609dot2.Ieee1609Dot2Content;
import org.bouncycastle.oer.its.ieee1609dot2.RecipientInfo;
import org.bouncycastle.oer.its.template.etsi103097.EtsiTs103097Module;
import org.bouncycastle.util.CollectionStore;
import org.bouncycastle.util.Store;

public class ETSIEncryptedData {
    private static final Element oerDef = EtsiTs103097Module.EtsiTs103097Data_Encrypted.build();
    private final EncryptedData encryptedData;

    public ETSIEncryptedData(byte[] oerEncoded) throws IOException {
        this(new ByteArrayInputStream(oerEncoded));
    }

    public ETSIEncryptedData(InputStream str) throws IOException {
        OERInputStream oerIn = str instanceof OERInputStream ? (OERInputStream)str : new OERInputStream(str);
        ASN1Object asn1 = oerIn.parse(oerDef);
        Ieee1609Dot2Content content = EtsiTs103097DataEncrypted.getInstance((Object)asn1).getContent();
        if (content.getChoice() != 2) {
            throw new IllegalStateException("EtsiTs103097Data-Encrypted did not have encrypted data content");
        }
        this.encryptedData = EncryptedData.getInstance((Object)content.getIeee1609Dot2Content());
    }

    ETSIEncryptedData(EncryptedData data) {
        this.encryptedData = data;
    }

    public byte[] getEncoded() {
        return OEREncoder.toByteArray((ASN1Encodable)new EtsiTs103097DataEncrypted(Ieee1609Dot2Content.encryptedData((EncryptedData)this.encryptedData)), (Element)oerDef);
    }

    public EncryptedData getEncryptedData() {
        return this.encryptedData;
    }

    public Store<ETSIRecipientInfo> getRecipients() {
        ArrayList<ETSIRecipientInfo> recipients = new ArrayList<ETSIRecipientInfo>();
        for (RecipientInfo ri : this.encryptedData.getRecipients().getRecipientInfos()) {
            recipients.add(new ETSIRecipientInfo(this.encryptedData, ri));
        }
        return new CollectionStore(recipients);
    }
}

