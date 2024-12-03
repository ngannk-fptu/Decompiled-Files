/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import org.apache.tomcat.util.http.parser.StructuredField;

public class Priority {
    public static final int DEFAULT_URGENCY = 3;
    public static final boolean DEFAULT_INCREMENTAL = false;
    private int urgency = 3;
    private boolean incremental = false;

    public int getUrgency() {
        return this.urgency;
    }

    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }

    public boolean getIncremental() {
        return this.incremental;
    }

    public void setIncremental(boolean incremental) {
        this.incremental = incremental;
    }

    public static Priority parsePriority(Reader input) throws IOException {
        StructuredField.SfListMember incrementalListMember;
        long urgency;
        Priority result = new Priority();
        StructuredField.SfDictionary dictionary = StructuredField.parseSfDictionary(input);
        StructuredField.SfListMember urgencyListMember = dictionary.getDictionaryMember("u");
        if (urgencyListMember instanceof StructuredField.SfInteger && (urgency = ((Long)((StructuredField.SfInteger)urgencyListMember).getVaue()).longValue()) > -1L && urgency < 8L) {
            result.setUrgency((int)urgency);
        }
        if ((incrementalListMember = dictionary.getDictionaryMember("i")) instanceof StructuredField.SfBoolean) {
            result.setIncremental((Boolean)((StructuredField.SfBoolean)incrementalListMember).getVaue());
        }
        return result;
    }
}

