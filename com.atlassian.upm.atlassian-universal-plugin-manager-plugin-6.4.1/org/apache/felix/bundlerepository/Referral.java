/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.bundlerepository;

public class Referral {
    private int m_depth = 1;
    private String m_url;

    public int getDepth() {
        return this.m_depth;
    }

    public String getUrl() {
        return this.m_url;
    }

    public void setUrl(String url) {
        this.m_url = url;
    }

    public void setDepth(String depth) {
        try {
            this.m_depth = Integer.parseInt(depth);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
    }
}

