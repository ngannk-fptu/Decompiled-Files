/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.EvidenceRecord
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.EvidenceRecord;
import org.bouncycastle.operator.DigestCalculatorProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.ers.ERSArchiveTimeStamp;
import org.bouncycastle.tsp.ers.ERSEvidenceRecord;
import org.bouncycastle.tsp.ers.ERSException;

public class ERSEvidenceRecordGenerator {
    private final DigestCalculatorProvider digCalcProv;

    public ERSEvidenceRecordGenerator(DigestCalculatorProvider digCalcProv) {
        this.digCalcProv = digCalcProv;
    }

    public ERSEvidenceRecord generate(ERSArchiveTimeStamp archiveTimeStamp) throws TSPException, ERSException {
        return new ERSEvidenceRecord(new EvidenceRecord(null, null, archiveTimeStamp.toASN1Structure()), this.digCalcProv);
    }

    public List<ERSEvidenceRecord> generate(List<ERSArchiveTimeStamp> archiveTimeStamps) throws TSPException, ERSException {
        ArrayList<ERSEvidenceRecord> list = new ArrayList<ERSEvidenceRecord>(archiveTimeStamps.size());
        for (int i = 0; i != archiveTimeStamps.size(); ++i) {
            list.add(new ERSEvidenceRecord(new EvidenceRecord(null, null, archiveTimeStamps.get(i).toASN1Structure()), this.digCalcProv));
        }
        return list;
    }
}

