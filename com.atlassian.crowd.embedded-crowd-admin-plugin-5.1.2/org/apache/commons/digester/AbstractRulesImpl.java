/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

public abstract class AbstractRulesImpl
implements Rules {
    private Digester digester;
    private String namespaceURI;

    public Digester getDigester() {
        return this.digester;
    }

    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    public void add(String pattern, Rule rule) {
        if (this.digester != null) {
            rule.setDigester(this.digester);
        }
        if (this.namespaceURI != null) {
            rule.setNamespaceURI(this.namespaceURI);
        }
        this.registerRule(pattern, rule);
    }

    protected abstract void registerRule(String var1, Rule var2);

    public abstract void clear();

    public List match(String pattern) {
        return this.match(this.namespaceURI, pattern);
    }

    public abstract List match(String var1, String var2);

    public abstract List rules();
}

