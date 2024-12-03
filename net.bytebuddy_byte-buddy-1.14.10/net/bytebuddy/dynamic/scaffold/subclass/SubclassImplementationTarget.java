/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.dynamic.scaffold.subclass;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

@HashCodeAndEqualsPlugin.Enhance
public class SubclassImplementationTarget
extends Implementation.Target.AbstractBase {
    protected final OriginTypeResolver originTypeResolver;

    protected SubclassImplementationTarget(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, Implementation.Target.AbstractBase.DefaultMethodInvocation defaultMethodInvocation, OriginTypeResolver originTypeResolver) {
        super(instrumentedType, methodGraph, defaultMethodInvocation);
        this.originTypeResolver = originTypeResolver;
    }

    public Implementation.SpecialMethodInvocation invokeSuper(MethodDescription.SignatureToken token) {
        return token.getName().equals("<init>") ? this.invokeConstructor(token) : this.invokeMethod(token);
    }

    private Implementation.SpecialMethodInvocation invokeConstructor(MethodDescription.SignatureToken token) {
        TypeDescription.Generic superClass = this.instrumentedType.getSuperClass();
        MethodList.Empty candidates = superClass == null ? new MethodList.Empty() : (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.hasSignature(token).and(ElementMatchers.isVisibleTo(this.instrumentedType)));
        return candidates.size() == 1 ? Implementation.SpecialMethodInvocation.Simple.of((MethodDescription)candidates.getOnly(), superClass.asErasure()) : Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
    }

    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming super class for given instance.")
    private Implementation.SpecialMethodInvocation invokeMethod(MethodDescription.SignatureToken token) {
        MethodGraph.Node methodNode = this.methodGraph.getSuperClassGraph().locate(token);
        return methodNode.getSort().isUnique() ? Implementation.SpecialMethodInvocation.Simple.of(methodNode.getRepresentative(), this.instrumentedType.getSuperClass().asErasure()) : Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
    }

    public TypeDefinition getOriginType() {
        return this.originTypeResolver.identify(this.instrumentedType);
    }

    public boolean equals(@MaybeNull Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return this.originTypeResolver.equals((Object)((SubclassImplementationTarget)object).originTypeResolver);
    }

    public int hashCode() {
        return super.hashCode() * 31 + this.originTypeResolver.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Factory implements Implementation.Target.Factory
    {
        SUPER_CLASS(OriginTypeResolver.SUPER_CLASS),
        LEVEL_TYPE(OriginTypeResolver.LEVEL_TYPE);

        private final OriginTypeResolver originTypeResolver;

        private Factory(OriginTypeResolver originTypeResolver) {
            this.originTypeResolver = originTypeResolver;
        }

        @Override
        public Implementation.Target make(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, ClassFileVersion classFileVersion) {
            return new SubclassImplementationTarget(instrumentedType, methodGraph, Implementation.Target.AbstractBase.DefaultMethodInvocation.of(classFileVersion), this.originTypeResolver);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum OriginTypeResolver {
        SUPER_CLASS{

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming super class for given instance.")
            protected TypeDefinition identify(TypeDescription typeDescription) {
                return typeDescription.getSuperClass();
            }
        }
        ,
        LEVEL_TYPE{

            protected TypeDefinition identify(TypeDescription typeDescription) {
                return typeDescription;
            }
        };


        protected abstract TypeDefinition identify(TypeDescription var1);
    }
}

