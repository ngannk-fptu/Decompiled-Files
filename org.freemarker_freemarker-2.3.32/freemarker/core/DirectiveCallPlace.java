/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.CallPlaceCustomDataInitializationException;
import freemarker.template.Template;
import freemarker.template.utility.ObjectFactory;

public interface DirectiveCallPlace {
    public Template getTemplate();

    public int getBeginColumn();

    public int getBeginLine();

    public int getEndColumn();

    public int getEndLine();

    public Object getOrCreateCustomData(Object var1, ObjectFactory var2) throws CallPlaceCustomDataInitializationException;

    public boolean isNestedOutputCacheable();
}

