/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.net.openssl;

import java.io.Serializable;

public class OpenSSLConfCmd
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name = null;
    private String value = null;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

