/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

public class MimeHeader {
    private String name;
    private String value;

    public MimeHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }
}

