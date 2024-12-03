/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.spaces.Space
 */
package com.atlassian.confluence.plugins.ia;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.plugins.ia.PagePin;
import com.atlassian.confluence.spaces.Space;
import java.util.Collection;

public interface PagePinManager {
    public PagePin createPagePin(Space var1, AbstractPage var2, PagePin.ConcreteType var3);

    public PagePin findBySpaceAndPageAndConcreteType(Space var1, AbstractPage var2, PagePin.ConcreteType var3);

    public Collection<PagePin> findBySpace(Space var1);

    public void removePagePin(PagePin var1);

    public void removeByPageAndConcreteType(AbstractPage var1, PagePin.ConcreteType var2);
}

