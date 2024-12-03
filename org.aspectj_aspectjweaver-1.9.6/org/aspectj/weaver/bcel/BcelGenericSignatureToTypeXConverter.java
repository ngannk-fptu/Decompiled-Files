/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.util.GenericSignature;
import org.aspectj.weaver.BoundedReferenceType;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeFactory;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.TypeVariableReferenceType;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class BcelGenericSignatureToTypeXConverter {
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(BcelGenericSignatureToTypeXConverter.class);

    public static ResolvedType classTypeSignature2TypeX(GenericSignature.ClassTypeSignature aClassTypeSignature, GenericSignature.FormalTypeParameter[] typeParams, World world) throws GenericSignatureFormatException {
        HashMap<GenericSignature.FormalTypeParameter, ReferenceType> typeMap = new HashMap<GenericSignature.FormalTypeParameter, ReferenceType>();
        ResolvedType ret = BcelGenericSignatureToTypeXConverter.classTypeSignature2TypeX(aClassTypeSignature, typeParams, world, typeMap);
        BcelGenericSignatureToTypeXConverter.fixUpCircularDependencies(ret, typeMap);
        return ret;
    }

    private static ResolvedType classTypeSignature2TypeX(GenericSignature.ClassTypeSignature aClassTypeSignature, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        StringBuffer sig = new StringBuffer();
        sig.append(aClassTypeSignature.outerType.identifier.replace(';', ' ').trim());
        for (int i = 0; i < aClassTypeSignature.nestedTypes.length; ++i) {
            sig.append("$");
            sig.append(aClassTypeSignature.nestedTypes[i].identifier.replace(';', ' ').trim());
        }
        sig.append(";");
        GenericSignature.SimpleClassTypeSignature innerType = aClassTypeSignature.outerType;
        if (aClassTypeSignature.nestedTypes.length > 0) {
            innerType = aClassTypeSignature.nestedTypes[aClassTypeSignature.nestedTypes.length - 1];
        }
        if (innerType.typeArguments.length > 0) {
            ResolvedType theBaseType = UnresolvedType.forSignature(sig.toString()).resolve(world);
            if (!theBaseType.isGenericType() && !theBaseType.isRawType()) {
                if (trace.isTraceEnabled()) {
                    trace.event("classTypeSignature2TypeX: this type is not a generic type:", (Object)null, new Object[]{theBaseType});
                }
                return theBaseType;
            }
            UnresolvedType[] typeArgumentTypes = new ResolvedType[innerType.typeArguments.length];
            for (int i = 0; i < typeArgumentTypes.length; ++i) {
                typeArgumentTypes[i] = BcelGenericSignatureToTypeXConverter.typeArgument2TypeX(innerType.typeArguments[i], typeParams, world, inProgressTypeVariableResolutions);
            }
            return TypeFactory.createParameterizedType(theBaseType, typeArgumentTypes, world);
        }
        return world.resolve(UnresolvedType.forSignature(sig.toString()));
    }

    public static ResolvedType fieldTypeSignature2TypeX(GenericSignature.FieldTypeSignature aFieldTypeSignature, GenericSignature.FormalTypeParameter[] typeParams, World world) throws GenericSignatureFormatException {
        HashMap<GenericSignature.FormalTypeParameter, ReferenceType> typeMap = new HashMap<GenericSignature.FormalTypeParameter, ReferenceType>();
        ResolvedType ret = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aFieldTypeSignature, typeParams, world, typeMap);
        BcelGenericSignatureToTypeXConverter.fixUpCircularDependencies(ret, typeMap);
        return ret;
    }

    private static ResolvedType fieldTypeSignature2TypeX(GenericSignature.FieldTypeSignature aFieldTypeSignature, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        if (aFieldTypeSignature.isClassTypeSignature()) {
            return BcelGenericSignatureToTypeXConverter.classTypeSignature2TypeX((GenericSignature.ClassTypeSignature)aFieldTypeSignature, typeParams, world, inProgressTypeVariableResolutions);
        }
        if (aFieldTypeSignature.isArrayTypeSignature()) {
            int dims = 0;
            GenericSignature.TypeSignature ats = aFieldTypeSignature;
            while (ats instanceof GenericSignature.ArrayTypeSignature) {
                ++dims;
                ats = ((GenericSignature.ArrayTypeSignature)ats).typeSig;
            }
            return world.resolve(UnresolvedType.makeArray(BcelGenericSignatureToTypeXConverter.typeSignature2TypeX(ats, typeParams, world, inProgressTypeVariableResolutions), dims));
        }
        if (aFieldTypeSignature.isTypeVariableSignature()) {
            ResolvedType rtx = BcelGenericSignatureToTypeXConverter.typeVariableSignature2TypeX((GenericSignature.TypeVariableSignature)aFieldTypeSignature, typeParams, world, inProgressTypeVariableResolutions);
            return rtx;
        }
        throw new GenericSignatureFormatException("Cant understand field type signature: " + aFieldTypeSignature);
    }

    public static TypeVariable formalTypeParameter2TypeVariable(GenericSignature.FormalTypeParameter aFormalTypeParameter, GenericSignature.FormalTypeParameter[] typeParams, World world) throws GenericSignatureFormatException {
        HashMap<GenericSignature.FormalTypeParameter, ReferenceType> typeMap = new HashMap<GenericSignature.FormalTypeParameter, ReferenceType>();
        return BcelGenericSignatureToTypeXConverter.formalTypeParameter2TypeVariable(aFormalTypeParameter, typeParams, world, typeMap);
    }

    private static TypeVariable formalTypeParameter2TypeVariable(GenericSignature.FormalTypeParameter aFormalTypeParameter, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        ResolvedType upperBound = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aFormalTypeParameter.classBound, typeParams, world, inProgressTypeVariableResolutions);
        UnresolvedType[] ifBounds = new UnresolvedType[aFormalTypeParameter.interfaceBounds.length];
        for (int i = 0; i < ifBounds.length; ++i) {
            ifBounds[i] = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aFormalTypeParameter.interfaceBounds[i], typeParams, world, inProgressTypeVariableResolutions);
        }
        return new TypeVariable(aFormalTypeParameter.identifier, upperBound, ifBounds);
    }

    private static ResolvedType typeArgument2TypeX(GenericSignature.TypeArgument aTypeArgument, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        if (aTypeArgument.isWildcard) {
            return UnresolvedType.SOMETHING.resolve(world);
        }
        if (aTypeArgument.isMinus) {
            ResolvedType bound = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aTypeArgument.signature, typeParams, world, inProgressTypeVariableResolutions);
            ResolvedType resolvedBound = world.resolve((UnresolvedType)bound);
            if (resolvedBound.isMissing()) {
                world.getLint().cantFindType.signal("Unable to find type (for bound): " + resolvedBound.getName(), null);
                resolvedBound = world.resolve(UnresolvedType.OBJECT);
            }
            ReferenceType rBound = (ReferenceType)resolvedBound;
            return new BoundedReferenceType(rBound, false, world);
        }
        if (aTypeArgument.isPlus) {
            ResolvedType bound = BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aTypeArgument.signature, typeParams, world, inProgressTypeVariableResolutions);
            ResolvedType resolvedBound = world.resolve((UnresolvedType)bound);
            if (resolvedBound.isMissing()) {
                world.getLint().cantFindType.signal("Unable to find type (for bound): " + resolvedBound.getName(), null);
                resolvedBound = world.resolve(UnresolvedType.OBJECT);
            }
            ReferenceType rBound = (ReferenceType)resolvedBound;
            return new BoundedReferenceType(rBound, true, world);
        }
        return BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX(aTypeArgument.signature, typeParams, world, inProgressTypeVariableResolutions);
    }

    public static ResolvedType typeSignature2TypeX(GenericSignature.TypeSignature aTypeSig, GenericSignature.FormalTypeParameter[] typeParams, World world) throws GenericSignatureFormatException {
        HashMap<GenericSignature.FormalTypeParameter, ReferenceType> typeMap = new HashMap<GenericSignature.FormalTypeParameter, ReferenceType>();
        ResolvedType ret = BcelGenericSignatureToTypeXConverter.typeSignature2TypeX(aTypeSig, typeParams, world, typeMap);
        BcelGenericSignatureToTypeXConverter.fixUpCircularDependencies(ret, typeMap);
        return ret;
    }

    private static ResolvedType typeSignature2TypeX(GenericSignature.TypeSignature aTypeSig, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        if (aTypeSig.isBaseType()) {
            return world.resolve(UnresolvedType.forSignature(((GenericSignature.BaseTypeSignature)aTypeSig).toString()));
        }
        return BcelGenericSignatureToTypeXConverter.fieldTypeSignature2TypeX((GenericSignature.FieldTypeSignature)aTypeSig, typeParams, world, inProgressTypeVariableResolutions);
    }

    private static ResolvedType typeVariableSignature2TypeX(GenericSignature.TypeVariableSignature aTypeVarSig, GenericSignature.FormalTypeParameter[] typeParams, World world, Map<GenericSignature.FormalTypeParameter, ReferenceType> inProgressTypeVariableResolutions) throws GenericSignatureFormatException {
        GenericSignature.FormalTypeParameter typeVarBounds = null;
        for (int i = 0; i < typeParams.length; ++i) {
            if (!typeParams[i].identifier.equals(aTypeVarSig.typeVariableName)) continue;
            typeVarBounds = typeParams[i];
            break;
        }
        if (typeVarBounds == null) {
            return new TypeVariableReferenceType(new TypeVariable(aTypeVarSig.typeVariableName), world);
        }
        if (inProgressTypeVariableResolutions.containsKey(typeVarBounds)) {
            return inProgressTypeVariableResolutions.get(typeVarBounds);
        }
        inProgressTypeVariableResolutions.put(typeVarBounds, new FTPHolder(typeVarBounds, world));
        TypeVariableReferenceType ret = new TypeVariableReferenceType(BcelGenericSignatureToTypeXConverter.formalTypeParameter2TypeVariable(typeVarBounds, typeParams, world, inProgressTypeVariableResolutions), world);
        inProgressTypeVariableResolutions.put(typeVarBounds, ret);
        return ret;
    }

    private static void fixUpCircularDependencies(ResolvedType aTypeX, Map<GenericSignature.FormalTypeParameter, ReferenceType> typeVariableResolutions) {
        if (!(aTypeX instanceof ReferenceType)) {
            return;
        }
        ReferenceType rt = (ReferenceType)aTypeX;
        TypeVariable[] typeVars = rt.getTypeVariables();
        if (typeVars != null) {
            for (int i = 0; i < typeVars.length; ++i) {
                if (!(typeVars[i].getUpperBound() instanceof FTPHolder)) continue;
                GenericSignature.FormalTypeParameter key = ((FTPHolder)typeVars[i].getUpperBound()).ftpToBeSubstituted;
                typeVars[i].setUpperBound(typeVariableResolutions.get(key));
            }
        }
    }

    public static class GenericSignatureFormatException
    extends Exception {
        public GenericSignatureFormatException(String explanation) {
            super(explanation);
        }
    }

    private static class FTPHolder
    extends ReferenceType {
        public GenericSignature.FormalTypeParameter ftpToBeSubstituted;

        public FTPHolder(GenericSignature.FormalTypeParameter ftp, World world) {
            super("Ljava/lang/Object;", world);
            this.ftpToBeSubstituted = ftp;
        }

        @Override
        public String toString() {
            return "placeholder for TypeVariable of " + this.ftpToBeSubstituted.toString();
        }

        @Override
        public ResolvedType resolve(World world) {
            return this;
        }

        @Override
        public boolean isCacheable() {
            return false;
        }
    }
}

