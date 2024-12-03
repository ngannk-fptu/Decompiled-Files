/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CSSProperty;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Term;
import java.util.Collection;

public interface NodeData {
    public <T extends CSSProperty> T getProperty(String var1);

    public <T extends CSSProperty> T getProperty(String var1, boolean var2);

    public <T extends CSSProperty> T getSpecifiedProperty(String var1);

    public Term<?> getValue(String var1, boolean var2);

    public <T extends Term<?>> T getValue(Class<T> var1, String var2);

    public <T extends Term<?>> T getValue(Class<T> var1, String var2, boolean var3);

    public Term<?> getSpecifiedValue(String var1);

    public <T extends Term<?>> T getSpecifiedValue(Class<T> var1, String var2);

    public String getAsString(String var1, boolean var2);

    public NodeData inheritFrom(NodeData var1) throws ClassCastException;

    public NodeData concretize();

    public NodeData push(Declaration var1);

    public Collection<String> getPropertyNames();

    public Declaration getSourceDeclaration(String var1);

    public Declaration getSourceDeclaration(String var1, boolean var2);
}

