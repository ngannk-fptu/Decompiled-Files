/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.james.jdkim.api.Headers
 *  org.apache.james.jdkim.api.SignatureRecord
 *  org.apache.james.jdkim.tagvalue.SignatureRecordImpl
 */
package org.bedework.caldav.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.james.jdkim.api.Headers;
import org.apache.james.jdkim.api.SignatureRecord;
import org.apache.james.jdkim.tagvalue.SignatureRecordImpl;
import org.bedework.util.misc.ToString;

public class IscheduleMessage
implements Headers,
Serializable {
    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    private List<String> fields = new ArrayList<String>();
    protected String originator;
    protected Set<String> recipients = new TreeSet<String>();
    protected String iScheduleVersion;
    protected String iScheduleMessageId;
    protected SignatureRecordImpl dkimSignature;

    public void addField(String nameLc) {
        this.fields.add(nameLc);
    }

    public void addHeader(String name, String val) {
        String nameLc = name.toLowerCase();
        List<String> vals = this.headers.get(nameLc);
        if (vals == null) {
            vals = new ArrayList<String>();
            this.headers.put(nameLc, vals);
        }
        if (!this.fields.contains(nameLc)) {
            this.addField(nameLc);
        }
        vals.add(val);
    }

    public String getOriginator() {
        return this.originator;
    }

    public Set<String> getRecipients() {
        return this.recipients;
    }

    public String getIScheduleVersion() {
        return this.iScheduleVersion;
    }

    public String getIScheduleMessageId() {
        return this.iScheduleMessageId;
    }

    public SignatureRecord getDkimSignature() {
        return this.dkimSignature;
    }

    public List<String> getFields() {
        return this.fields;
    }

    public List<String> getFields(String val) {
        List<String> l = this.headers.get(val.toLowerCase());
        if (l == null || l.size() == 0) {
            return l;
        }
        StringBuilder sb = new StringBuilder();
        String delim = "";
        for (String s : l) {
            sb.append(delim);
            delim = ",";
            sb.append(s);
        }
        ArrayList<String> namedL = new ArrayList<String>();
        namedL.add(val + ":" + sb.toString());
        return namedL;
    }

    public List<String> getFieldVals(String val) {
        return this.headers.get(val.toLowerCase());
    }

    public String toString() {
        ToString ts = new ToString(this);
        ts.append("originator", this.getOriginator());
        return ts.toString();
    }
}

