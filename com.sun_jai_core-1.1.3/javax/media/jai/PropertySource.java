/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

public interface PropertySource {
    public String[] getPropertyNames();

    public String[] getPropertyNames(String var1);

    public Class getPropertyClass(String var1);

    public Object getProperty(String var1);
}

