/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

public interface Result {
    public static final String PI_DISABLE_OUTPUT_ESCAPING = "javax.xml.transform.disable-output-escaping";
    public static final String PI_ENABLE_OUTPUT_ESCAPING = "javax.xml.transform.enable-output-escaping";

    public void setSystemId(String var1);

    public String getSystemId();
}

