/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

public final class DavCompliance {
    public static final String _1_ = "1";
    public static final String _2_ = "2";
    public static final String _3_ = "3";
    public static final String ACTIVITY = "activity";
    public static final String BASELINE = "baseline";
    public static final String CHECKOUT_IN_PLACE = "checkout-in-place";
    public static final String LABEL = "label";
    public static final String MERGE = "merge";
    public static final String UPDATE = "update";
    public static final String VERSION_CONTROL = "version-control";
    public static final String VERSION_CONTROLLED_COLLECTION = "version-controlled-collection";
    public static final String VERSION_HISTORY = "version-history";
    public static final String WORKING_RESOURCE = "working-resource";
    public static final String WORKSPACE = "workspace";
    public static final String ORDERED_COLLECTIONS = "ordered-collections";
    public static final String ACCESS_CONTROL = "access-control";
    public static final String BIND = "bind";
    public static final String OBSERVATION = "observation";

    private DavCompliance() {
    }

    public static String concatComplianceClasses(String[] complianceClasses) {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < complianceClasses.length; ++i) {
            if (i > 0) {
                b.append(",");
            }
            b.append(complianceClasses[i]);
        }
        return b.toString();
    }
}

