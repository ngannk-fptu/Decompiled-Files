/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.util.Vector;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;

public class GeneralNamesBuilder {
    private Vector names = new Vector();

    public GeneralNamesBuilder addNames(GeneralNames names) {
        GeneralName[] n = names.getNames();
        for (int i = 0; i != n.length; ++i) {
            this.names.addElement(n[i]);
        }
        return this;
    }

    public GeneralNamesBuilder addName(GeneralName name) {
        this.names.addElement(name);
        return this;
    }

    public GeneralNames build() {
        GeneralName[] tmp = new GeneralName[this.names.size()];
        for (int i = 0; i != tmp.length; ++i) {
            tmp[i] = (GeneralName)this.names.elementAt(i);
        }
        return new GeneralNames(tmp);
    }
}

