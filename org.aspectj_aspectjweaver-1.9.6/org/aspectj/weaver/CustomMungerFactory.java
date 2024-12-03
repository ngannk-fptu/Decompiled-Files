/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.Collection;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ShadowMunger;

public interface CustomMungerFactory {
    public Collection<ShadowMunger> createCustomShadowMungers(ResolvedType var1);

    public Collection<ConcreteTypeMunger> createCustomTypeMungers(ResolvedType var1);

    public Collection<ShadowMunger> getAllCreatedCustomShadowMungers();

    public Collection<ConcreteTypeMunger> getAllCreatedCustomTypeMungers();
}

