/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.xni.QName;

public class MixedContentModel
implements ContentModelValidator {
    private final int fCount;
    private final QName[] fChildren;
    private final int[] fChildrenType;
    private final boolean fOrdered;

    public MixedContentModel(QName[] qNameArray, int[] nArray, int n, int n2, boolean bl) {
        this.fCount = n2;
        this.fChildren = new QName[this.fCount];
        this.fChildrenType = new int[this.fCount];
        for (int i = 0; i < this.fCount; ++i) {
            this.fChildren[i] = new QName(qNameArray[n + i]);
            this.fChildrenType[i] = nArray[n + i];
        }
        this.fOrdered = bl;
    }

    @Override
    public int validate(QName[] qNameArray, int n, int n2) {
        if (this.fOrdered) {
            int n3 = 0;
            for (int i = 0; i < n2; ++i) {
                String string;
                QName qName = qNameArray[n + i];
                if (qName.localpart == null) continue;
                int n4 = this.fChildrenType[n3];
                if (n4 == 0 ? this.fChildren[n3].rawname != qNameArray[n + i].rawname : (n4 == 6 ? (string = this.fChildren[n3].uri) != null && string != qNameArray[i].uri : (n4 == 8 ? qNameArray[i].uri != null : n4 == 7 && this.fChildren[n3].uri == qNameArray[i].uri))) {
                    return i;
                }
                ++n3;
            }
        } else {
            for (int i = 0; i < n2; ++i) {
                String string;
                int n5;
                int n6;
                QName qName = qNameArray[n + i];
                if (qName.localpart == null) continue;
                for (n6 = 0; n6 < this.fCount && !((n5 = this.fChildrenType[n6]) == 0 ? qName.rawname == this.fChildren[n6].rawname : (n5 == 6 ? (string = this.fChildren[n6].uri) == null || string == qNameArray[i].uri : (n5 == 8 ? qNameArray[i].uri == null : n5 == 7 && this.fChildren[n6].uri != qNameArray[i].uri))); ++n6) {
                }
                if (n6 != this.fCount) continue;
                return i;
            }
        }
        return -1;
    }
}

