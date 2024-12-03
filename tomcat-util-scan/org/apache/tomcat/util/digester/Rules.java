/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.digester;

import java.util.List;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;

public interface Rules {
    public Digester getDigester();

    public void setDigester(Digester var1);

    public void add(String var1, Rule var2);

    public void clear();

    public List<Rule> match(String var1, String var2);

    public List<Rule> rules();
}

