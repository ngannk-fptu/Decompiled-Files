/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt;

public enum ChainingMode {
    ecb("ECB", 1, null),
    cbc("CBC", 2, "ChainingModeCBC"),
    cfb("CFB8", 3, "ChainingModeCFB");

    public final String jceId;
    public final int ecmaId;
    public final String xmlId;

    private ChainingMode(String jceId, int ecmaId, String xmlId) {
        this.jceId = jceId;
        this.ecmaId = ecmaId;
        this.xmlId = xmlId;
    }

    public static ChainingMode fromXmlId(String xmlId) {
        for (ChainingMode cm : ChainingMode.values()) {
            if (cm.xmlId == null || !cm.xmlId.equals(xmlId)) continue;
            return cm;
        }
        return null;
    }
}

