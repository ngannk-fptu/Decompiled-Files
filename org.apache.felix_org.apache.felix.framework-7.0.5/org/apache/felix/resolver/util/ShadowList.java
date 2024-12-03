/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.resolver.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.felix.resolver.util.CandidateSelector;
import org.osgi.resource.Capability;
import org.osgi.service.resolver.HostedCapability;
import org.osgi.service.resolver.ResolveContext;

public class ShadowList
extends CandidateSelector {
    private final List<Capability> m_original;

    public static ShadowList createShadowList(CandidateSelector original) {
        if (original instanceof ShadowList) {
            throw new IllegalArgumentException("Cannot create a ShadowList using another ShadowList.");
        }
        return new ShadowList(original.unmodifiable, original.unmodifiable, original.isUnmodifiable);
    }

    public static ShadowList deepCopy(ShadowList original) {
        return new ShadowList(original.unmodifiable, original.m_original, original.isUnmodifiable);
    }

    private ShadowList(CandidateSelector shadow, List<Capability> original) {
        super(shadow);
        this.m_original = original;
    }

    private ShadowList(List<Capability> unmodifiable, List<Capability> original, AtomicBoolean isUnmodifiable) {
        super(unmodifiable, isUnmodifiable);
        this.m_original = new ArrayList<Capability>(original);
    }

    @Override
    public ShadowList copy() {
        return new ShadowList(this, this.m_original);
    }

    public void insertHostedCapability(ResolveContext context, HostedCapability wrappedCapability, HostedCapability toInsertCapability) {
        this.checkModifiable();
        int removeIdx = this.m_original.indexOf(toInsertCapability.getDeclaredCapability());
        if (removeIdx != -1) {
            this.m_original.remove(removeIdx);
            this.unmodifiable.remove(removeIdx);
        }
        int insertIdx = context.insertHostedCapability(this.m_original, toInsertCapability);
        this.unmodifiable.add(insertIdx, wrappedCapability);
    }

    public void replace(Capability origCap, Capability c) {
        this.checkModifiable();
        int idx = this.unmodifiable.indexOf(origCap);
        this.unmodifiable.set(idx, c);
    }
}

