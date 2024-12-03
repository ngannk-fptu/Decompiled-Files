/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.Vote
 *  com.atlassian.gadgets.plugins.PluginGadgetSpec
 *  com.atlassian.gadgets.publisher.spi.PluginGadgetSpecProviderPermission
 *  com.google.common.base.Preconditions
 */
package com.atlassian.gadgets.publisher.internal.impl;

import com.atlassian.gadgets.Vote;
import com.atlassian.gadgets.plugins.PluginGadgetSpec;
import com.atlassian.gadgets.publisher.spi.PluginGadgetSpecProviderPermission;
import com.google.common.base.Preconditions;

public class AggregatePluginGadgetSpecProviderPermissionImpl
implements PluginGadgetSpecProviderPermission {
    private final Iterable<PluginGadgetSpecProviderPermission> permissions;

    public AggregatePluginGadgetSpecProviderPermissionImpl(Iterable<PluginGadgetSpecProviderPermission> permissions) {
        this.permissions = (Iterable)Preconditions.checkNotNull(permissions, (Object)"permissions");
    }

    public Vote voteOn(final PluginGadgetSpec gadgetSpec) {
        return AggregatePluginGadgetSpecProviderPermissionImpl.foldLeft(this.permissions, Vote.PASS, new FoldFunction<Vote, PluginGadgetSpecProviderPermission>(){

            @Override
            public Vote apply(Vote a, PluginGadgetSpecProviderPermission b) {
                Vote voteB;
                if (a == Vote.DENY) {
                    return Vote.DENY;
                }
                try {
                    voteB = b.voteOn(gadgetSpec);
                }
                catch (RuntimeException e) {
                    if (e.getClass().getSimpleName().equals("ServiceUnavailableException")) {
                        return a;
                    }
                    throw e;
                }
                if (voteB == Vote.DENY) {
                    return Vote.DENY;
                }
                if (a == Vote.ALLOW || voteB == Vote.ALLOW) {
                    return Vote.ALLOW;
                }
                return Vote.PASS;
            }
        });
    }

    static <A, B> A foldLeft(Iterable<B> xs, A z, FoldFunction<A, B> f) {
        A p = z;
        for (B x : xs) {
            p = f.apply(p, x);
        }
        return p;
    }

    static interface FoldFunction<A, B> {
        public A apply(A var1, B var2);
    }
}

