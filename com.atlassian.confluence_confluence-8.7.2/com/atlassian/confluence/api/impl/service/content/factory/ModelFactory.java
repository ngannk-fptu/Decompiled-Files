/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.google.common.base.Function
 */
package com.atlassian.confluence.api.impl.service.content.factory;

import com.atlassian.confluence.api.model.Expansions;
import com.google.common.base.Function;
import java.util.ArrayList;

public abstract class ModelFactory<H, M> {
    public abstract M buildFrom(H var1, Expansions var2);

    public <HC extends H> Iterable<M> buildFrom(Iterable<HC> hibernateObjects, Expansions expansions) {
        ArrayList<M> list = new ArrayList<M>();
        for (HC item : hibernateObjects) {
            list.add(this.buildFrom(item, expansions));
        }
        return list;
    }

    @Deprecated
    public final Function<H, M> asFunction(Expansions expansions) {
        return hibernateObject -> {
            if (hibernateObject == null) {
                return null;
            }
            return this.buildFrom(hibernateObject, expansions);
        };
    }
}

