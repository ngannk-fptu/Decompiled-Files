/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Selector
 */
package org.bouncycastle.tsp.ers;

import java.util.Date;
import org.bouncycastle.tsp.ers.ERSData;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.util.Selector;

public class ERSEvidenceRecordSelector
implements Selector<ERSEvidenceRecord> {
    private final ERSData data;
    private final Date date;

    public ERSEvidenceRecordSelector(ERSData data) {
        this(data, new Date());
    }

    public ERSEvidenceRecordSelector(ERSData data, Date atDate) {
        this.data = data;
        this.date = new Date(atDate.getTime());
    }

    public ERSData getData() {
        return this.data;
    }

    public boolean match(ERSEvidenceRecord obj) {
        try {
            if (obj.isContaining(this.data, this.date)) {
                try {
                    obj.validatePresent(this.data, this.date);
                    return true;
                }
                catch (Exception e) {
                    return false;
                }
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public Object clone() {
        return this;
    }
}

