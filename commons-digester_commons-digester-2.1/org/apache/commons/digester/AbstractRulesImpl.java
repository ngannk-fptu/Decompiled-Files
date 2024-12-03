/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.digester;

import java.util.List;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.digester.Rules;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class AbstractRulesImpl
implements Rules {
    private Digester digester;
    private String namespaceURI;

    @Override
    public Digester getDigester() {
        return this.digester;
    }

    @Override
    public void setDigester(Digester digester) {
        this.digester = digester;
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    public void setNamespaceURI(String namespaceURI) {
        this.namespaceURI = namespaceURI;
    }

    @Override
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

    @Override
    public abstract void clear();

    @Override
    @Deprecated
    public List<Rule> match(String pattern) {
        return this.match(this.namespaceURI, pattern);
    }

    @Override
    public abstract List<Rule> match(String var1, String var2);

    @Override
    public abstract List<Rule> rules();
}

