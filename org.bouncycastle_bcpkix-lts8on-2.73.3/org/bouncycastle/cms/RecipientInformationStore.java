/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.util.Iterable
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cms.KeyTransRecipientId;
import org.bouncycastle.cms.RecipientId;
import org.bouncycastle.cms.RecipientInformation;
import org.bouncycastle.util.Iterable;

public class RecipientInformationStore
implements Iterable<RecipientInformation> {
    private final List all;
    private final Map table = new HashMap();

    public RecipientInformationStore(RecipientInformation recipientInformation) {
        this.all = new ArrayList(1);
        this.all.add(recipientInformation);
        RecipientId sid = recipientInformation.getRID();
        this.table.put(sid, this.all);
    }

    public RecipientInformationStore(Collection<RecipientInformation> recipientInfos) {
        for (RecipientInformation recipientInformation : recipientInfos) {
            RecipientId rid = recipientInformation.getRID();
            ArrayList<RecipientInformation> list = (ArrayList<RecipientInformation>)this.table.get(rid);
            if (list == null) {
                list = new ArrayList<RecipientInformation>(1);
                this.table.put(rid, list);
            }
            list.add(recipientInformation);
        }
        this.all = new ArrayList<RecipientInformation>(recipientInfos);
    }

    public RecipientInformation get(RecipientId selector) {
        Collection<RecipientInformation> list = this.getRecipients(selector);
        return list.size() == 0 ? null : list.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<RecipientInformation> getRecipients() {
        return new ArrayList<RecipientInformation>(this.all);
    }

    public Collection<RecipientInformation> getRecipients(RecipientId selector) {
        ArrayList list;
        if (selector instanceof KeyTransRecipientId) {
            KeyTransRecipientId keyTrans = (KeyTransRecipientId)selector;
            X500Name issuer = keyTrans.getIssuer();
            byte[] subjectKeyId = keyTrans.getSubjectKeyIdentifier();
            if (issuer != null && subjectKeyId != null) {
                Collection<RecipientInformation> match2;
                ArrayList<RecipientInformation> results = new ArrayList<RecipientInformation>();
                Collection<RecipientInformation> match1 = this.getRecipients(new KeyTransRecipientId(issuer, keyTrans.getSerialNumber()));
                if (match1 != null) {
                    results.addAll(match1);
                }
                if ((match2 = this.getRecipients(new KeyTransRecipientId(subjectKeyId))) != null) {
                    results.addAll(match2);
                }
                return results;
            }
        }
        return (list = (ArrayList)this.table.get(selector)) == null ? new ArrayList<RecipientInformation>() : new ArrayList(list);
    }

    public Iterator<RecipientInformation> iterator() {
        return this.getRecipients().iterator();
    }
}

