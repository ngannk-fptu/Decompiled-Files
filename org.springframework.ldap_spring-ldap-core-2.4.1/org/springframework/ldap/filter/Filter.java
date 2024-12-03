/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

public interface Filter {
    public String encode();

    public StringBuffer encode(StringBuffer var1);

    public boolean equals(Object var1);

    public int hashCode();
}

