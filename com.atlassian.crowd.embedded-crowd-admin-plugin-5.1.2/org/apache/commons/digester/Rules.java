/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;

public interface Rules {
    public Digester getDigester();

    public void setDigester(Digester var1);

    public String getNamespaceURI();

    public void setNamespaceURI(String var1);

    public void add(String var1, Rule var2);

    public void clear();

    public List match(String var1);

    public List match(String var1, String var2);

    public List rules();
}

