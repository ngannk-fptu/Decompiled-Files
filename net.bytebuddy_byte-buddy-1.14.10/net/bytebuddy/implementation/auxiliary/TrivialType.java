/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.auxiliary;

import java.util.Collection;
import java.util.Collections;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.utility.RandomString;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum TrivialType implements AuxiliaryType
{
    SIGNATURE_RELEVANT(true),
    PLAIN(false);

    private final boolean eager;

    private TrivialType(boolean eager) {
        this.eager = eager;
    }

    @Override
    public String getSuffix() {
        return RandomString.hashOf(this.name().hashCode());
    }

    @Override
    public DynamicType make(String auxiliaryTypeName, ClassFileVersion classFileVersion, MethodAccessorFactory methodAccessorFactory) {
        return new ByteBuddy(classFileVersion).with(TypeValidation.DISABLED).with(MethodGraph.Empty.INSTANCE).subclass(Object.class, (ConstructorStrategy)ConstructorStrategy.Default.NO_CONSTRUCTORS).annotateType((Collection<AnnotationDescription>)(this.eager ? Collections.singletonList(AnnotationDescription.Builder.ofType(AuxiliaryType.SignatureRelevant.class).build(false)) : Collections.emptyList())).name(auxiliaryTypeName).modifiers(DEFAULT_TYPE_MODIFIER).make();
    }
}

