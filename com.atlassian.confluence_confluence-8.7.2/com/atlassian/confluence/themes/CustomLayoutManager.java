/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.admin.actions.lookandfeel.DefaultDecorator;
import com.atlassian.confluence.core.PersistentDecorator;
import java.util.Collection;

public interface CustomLayoutManager {
    public void saveOrUpdate(PersistentDecorator var1);

    public void saveOrUpdate(String var1, String var2, String var3);

    public PersistentDecorator getPersistentDecorator(String var1, String var2);

    public Collection<PersistentDecorator> getCustomSpaceDecorators(String var1);

    public Collection<PersistentDecorator> getApplicableCustomDecoratorsForSpace(String var1);

    public Collection<PersistentDecorator> getCustomGlobalDecorators();

    public boolean hasCustomSpaceDecorator(String var1, String var2);

    public boolean hasCustomGlobalDecorator(String var1);

    public boolean hasCustomDecorator(String var1, String var2);

    public boolean usesCustomLayout(String var1);

    public void remove(String var1, String var2);

    public void remove(PersistentDecorator var1);

    public void removeAllCustomSpaceDecorators(String var1);

    public void removeAllCustomGlobalDecorators();

    public DefaultDecorator getDefaultDecorator(String var1);

    public Collection<DefaultDecorator> getAllDefaultDecorators();
}

