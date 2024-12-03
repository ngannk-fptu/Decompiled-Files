/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.holders;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.Holder;

public final class QNameHolder
implements Holder {
    public QName value;

    public QNameHolder() {
    }

    public QNameHolder(QName value) {
        this.value = value;
    }
}

