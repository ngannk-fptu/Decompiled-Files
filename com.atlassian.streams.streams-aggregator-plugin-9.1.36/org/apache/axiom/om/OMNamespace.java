/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om;

public interface OMNamespace {
    public boolean equals(String var1, String var2);

    public String getPrefix();

    public String getName();

    public String getNamespaceURI();
}

