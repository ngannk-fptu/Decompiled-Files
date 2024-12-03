/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Iterable
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.util.Iterable;

public class SignerInformationStore
implements Iterable<SignerInformation> {
    private List all = new ArrayList();
    private Map table = new HashMap();

    public SignerInformationStore(SignerInformation signerInfo) {
        this.all = new ArrayList(1);
        this.all.add(signerInfo);
        SignerId sid = signerInfo.getSID();
        this.table.put(sid, this.all);
    }

    public SignerInformationStore(Collection<SignerInformation> signerInfos) {
        for (SignerInformation signer : signerInfos) {
            SignerId sid = signer.getSID();
            ArrayList<SignerInformation> list = (ArrayList<SignerInformation>)this.table.get(sid);
            if (list == null) {
                list = new ArrayList<SignerInformation>(1);
                this.table.put(sid, list);
            }
            list.add(signer);
        }
        this.all = new ArrayList<SignerInformation>(signerInfos);
    }

    public SignerInformation get(SignerId selector) {
        Collection<SignerInformation> list = this.getSigners(selector);
        return list.size() == 0 ? null : list.iterator().next();
    }

    public int size() {
        return this.all.size();
    }

    public Collection<SignerInformation> getSigners() {
        return new ArrayList<SignerInformation>(this.all);
    }

    public Collection<SignerInformation> getSigners(SignerId selector) {
        if (selector.getIssuer() != null && selector.getSubjectKeyIdentifier() != null) {
            Collection<SignerInformation> match2;
            ArrayList<SignerInformation> results = new ArrayList<SignerInformation>();
            Collection<SignerInformation> match1 = this.getSigners(new SignerId(selector.getIssuer(), selector.getSerialNumber()));
            if (match1 != null) {
                results.addAll(match1);
            }
            if ((match2 = this.getSigners(new SignerId(selector.getSubjectKeyIdentifier()))) != null) {
                results.addAll(match2);
            }
            return results;
        }
        ArrayList list = (ArrayList)this.table.get(selector);
        return list == null ? new ArrayList<SignerInformation>() : new ArrayList(list);
    }

    public Iterator<SignerInformation> iterator() {
        return this.getSigners().iterator();
    }
}

