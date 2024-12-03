/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver;

import java.util.ArrayList;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.TypeVariable;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.UnresolvedTypeVariableReferenceType;
import org.aspectj.weaver.WildcardedUnresolvedType;
import org.aspectj.weaver.World;

public class TypeFactory {
    public static ReferenceType createParameterizedType(ResolvedType aBaseType, UnresolvedType[] someTypeParameters, World inAWorld) {
        ResolvedType baseType = aBaseType;
        if (!aBaseType.isGenericType() && someTypeParameters != null && someTypeParameters.length > 0) {
            if (!aBaseType.isRawType()) {
                throw new IllegalStateException("Expecting raw type, but " + aBaseType + " is of type " + aBaseType.getTypekind());
            }
            if ((baseType = baseType.getGenericType()) == null) {
                throw new IllegalStateException("Raw type does not have generic type set");
            }
        }
        ResolvedType[] resolvedParameters = inAWorld.resolve(someTypeParameters);
        ReferenceType existingType = ((ReferenceType)baseType).findDerivativeType(resolvedParameters);
        ReferenceType pType = null;
        pType = existingType != null ? existingType : new ReferenceType(baseType, resolvedParameters, inAWorld);
        return (ReferenceType)pType.resolve(inAWorld);
    }

    public static UnresolvedType createUnresolvedParameterizedType(String sig, String erasuresig, UnresolvedType[] arguments) {
        return new UnresolvedType(sig, erasuresig, arguments);
    }

    static UnresolvedType convertSigToType(String aSignature) {
        UnresolvedType bound = null;
        int startOfParams = aSignature.indexOf(60);
        if (startOfParams == -1) {
            bound = UnresolvedType.forSignature(aSignature);
        } else {
            int endOfParams = aSignature.lastIndexOf(62);
            String signatureErasure = "L" + aSignature.substring(1, startOfParams) + ";";
            UnresolvedType[] typeParams = TypeFactory.createTypeParams(aSignature.substring(startOfParams + 1, endOfParams));
            bound = new UnresolvedType("P" + aSignature.substring(1), signatureErasure, typeParams);
        }
        return bound;
    }

    public static UnresolvedType createTypeFromSignature(String signature) {
        char firstChar = signature.charAt(0);
        if (firstChar == 'P') {
            int startOfParams = signature.indexOf(60);
            if (startOfParams == -1) {
                String signatureErasure = "L" + signature.substring(1);
                return new UnresolvedType(signature, signatureErasure, UnresolvedType.NONE);
            }
            int endOfParams = TypeFactory.locateMatchingEndAngleBracket(signature, startOfParams);
            StringBuffer erasureSig = new StringBuffer(signature);
            erasureSig.setCharAt(0, 'L');
            while (startOfParams != -1) {
                erasureSig.delete(startOfParams, endOfParams + 1);
                startOfParams = TypeFactory.locateFirstBracket(erasureSig);
                if (startOfParams == -1) continue;
                endOfParams = TypeFactory.locateMatchingEndAngleBracket(erasureSig, startOfParams);
            }
            String signatureErasure = erasureSig.toString();
            String lastType = null;
            int nestedTypePosition = signature.indexOf("$", endOfParams);
            lastType = nestedTypePosition != -1 ? signature.substring(nestedTypePosition + 1) : new String(signature);
            startOfParams = lastType.indexOf("<");
            UnresolvedType[] typeParams = UnresolvedType.NONE;
            if (startOfParams != -1) {
                endOfParams = TypeFactory.locateMatchingEndAngleBracket(lastType, startOfParams);
                typeParams = TypeFactory.createTypeParams(lastType.substring(startOfParams + 1, endOfParams));
            }
            StringBuilder s = new StringBuilder();
            int firstAngleBracket = signature.indexOf(60);
            s.append("P").append(signature.substring(1, firstAngleBracket));
            s.append('<');
            for (UnresolvedType typeParameter : typeParams) {
                s.append(typeParameter.getSignature());
            }
            s.append(">;");
            signature = s.toString();
            return new UnresolvedType(signature, signatureErasure, typeParams);
        }
        if ((firstChar == '?' || firstChar == '*') && signature.length() == 1) {
            return WildcardedUnresolvedType.QUESTIONMARK;
        }
        if (firstChar == '+') {
            UnresolvedType upperBound = TypeFactory.convertSigToType(signature.substring(1));
            WildcardedUnresolvedType wildcardedUT = new WildcardedUnresolvedType(signature, upperBound, null);
            return wildcardedUT;
        }
        if (firstChar == '-') {
            UnresolvedType lowerBound = TypeFactory.convertSigToType(signature.substring(1));
            WildcardedUnresolvedType wildcardedUT = new WildcardedUnresolvedType(signature, null, lowerBound);
            return wildcardedUT;
        }
        if (firstChar == 'T') {
            String typeVariableName = signature.substring(1);
            if (typeVariableName.endsWith(";")) {
                typeVariableName = typeVariableName.substring(0, typeVariableName.length() - 1);
            }
            return new UnresolvedTypeVariableReferenceType(new TypeVariable(typeVariableName));
        }
        if (firstChar == '[') {
            int dims = 0;
            while (signature.charAt(dims) == '[') {
                ++dims;
            }
            UnresolvedType componentType = TypeFactory.createTypeFromSignature(signature.substring(dims));
            return new UnresolvedType(signature, signature.substring(0, dims) + componentType.getErasureSignature());
        }
        if (signature.length() == 1) {
            switch (firstChar) {
                case 'V': {
                    return UnresolvedType.VOID;
                }
                case 'Z': {
                    return UnresolvedType.BOOLEAN;
                }
                case 'B': {
                    return UnresolvedType.BYTE;
                }
                case 'C': {
                    return UnresolvedType.CHAR;
                }
                case 'D': {
                    return UnresolvedType.DOUBLE;
                }
                case 'F': {
                    return UnresolvedType.FLOAT;
                }
                case 'I': {
                    return UnresolvedType.INT;
                }
                case 'J': {
                    return UnresolvedType.LONG;
                }
                case 'S': {
                    return UnresolvedType.SHORT;
                }
            }
        } else {
            if (firstChar == '@') {
                return ResolvedType.MISSING;
            }
            if (firstChar == 'L') {
                int leftAngleBracket = signature.indexOf(60);
                if (leftAngleBracket == -1) {
                    return new UnresolvedType(signature);
                }
                int endOfParams = TypeFactory.locateMatchingEndAngleBracket(signature, leftAngleBracket);
                StringBuffer erasureSig = new StringBuffer(signature);
                erasureSig.setCharAt(0, 'L');
                while (leftAngleBracket != -1) {
                    erasureSig.delete(leftAngleBracket, endOfParams + 1);
                    leftAngleBracket = TypeFactory.locateFirstBracket(erasureSig);
                    if (leftAngleBracket == -1) continue;
                    endOfParams = TypeFactory.locateMatchingEndAngleBracket(erasureSig, leftAngleBracket);
                }
                String signatureErasure = erasureSig.toString();
                String lastType = null;
                int nestedTypePosition = signature.indexOf("$", endOfParams);
                lastType = nestedTypePosition != -1 ? signature.substring(nestedTypePosition + 1) : new String(signature);
                leftAngleBracket = lastType.indexOf("<");
                UnresolvedType[] typeParams = UnresolvedType.NONE;
                if (leftAngleBracket != -1) {
                    endOfParams = TypeFactory.locateMatchingEndAngleBracket(lastType, leftAngleBracket);
                    typeParams = TypeFactory.createTypeParams(lastType.substring(leftAngleBracket + 1, endOfParams));
                }
                StringBuilder s = new StringBuilder();
                int firstAngleBracket = signature.indexOf(60);
                s.append("P").append(signature.substring(1, firstAngleBracket));
                s.append('<');
                for (UnresolvedType typeParameter : typeParams) {
                    s.append(typeParameter.getSignature());
                }
                s.append(">;");
                signature = s.toString();
                return new UnresolvedType(signature, signatureErasure, typeParams);
            }
        }
        return new UnresolvedType(signature);
    }

    private static int locateMatchingEndAngleBracket(CharSequence signature, int startOfParams) {
        if (startOfParams == -1) {
            return -1;
        }
        int count = 1;
        int idx = startOfParams;
        int max = signature.length();
        while (idx < max) {
            char ch;
            if ((ch = signature.charAt(++idx)) == '<') {
                ++count;
                continue;
            }
            if (ch != '>') continue;
            if (count == 1) break;
            --count;
        }
        return idx;
    }

    private static int locateFirstBracket(StringBuffer signature) {
        int max = signature.length();
        for (int idx = 0; idx < max; ++idx) {
            if (signature.charAt(idx) != '<') continue;
            return idx;
        }
        return -1;
    }

    private static UnresolvedType[] createTypeParams(String typeParameterSpecification) {
        String remainingToProcess = typeParameterSpecification;
        ArrayList<UnresolvedType> types = new ArrayList<UnresolvedType>();
        while (remainingToProcess.length() != 0) {
            int endOfSig = 0;
            int anglies = 0;
            boolean hadAnglies = false;
            boolean sigFound = false;
            block8: for (endOfSig = 0; endOfSig < remainingToProcess.length() && !sigFound; ++endOfSig) {
                char thisChar = remainingToProcess.charAt(endOfSig);
                switch (thisChar) {
                    case '<': {
                        ++anglies;
                        hadAnglies = true;
                        continue block8;
                    }
                    case '>': {
                        --anglies;
                        continue block8;
                    }
                    case '*': {
                        if (anglies != 0) continue block8;
                        int nextCharPos = endOfSig + 1;
                        if (nextCharPos >= remainingToProcess.length()) {
                            sigFound = true;
                            continue block8;
                        }
                        char nextChar = remainingToProcess.charAt(nextCharPos);
                        if (nextChar == '+' || nextChar == '-') continue block8;
                        sigFound = true;
                        continue block8;
                    }
                    case '[': {
                        if (anglies != 0) continue block8;
                        int nextChar = endOfSig + 1;
                        while (remainingToProcess.charAt(nextChar) == '[') {
                            ++nextChar;
                        }
                        if ("BCDFIJSZ".indexOf(remainingToProcess.charAt(nextChar)) == -1) continue block8;
                        sigFound = true;
                        endOfSig = nextChar;
                        continue block8;
                    }
                    case ';': {
                        if (anglies != 0) continue block8;
                        sigFound = true;
                    }
                }
            }
            String forProcessing = remainingToProcess.substring(0, endOfSig);
            if (hadAnglies && forProcessing.charAt(0) == 'L') {
                forProcessing = "P" + forProcessing.substring(1);
            }
            types.add(TypeFactory.createTypeFromSignature(forProcessing));
            remainingToProcess = remainingToProcess.substring(endOfSig);
        }
        UnresolvedType[] typeParams = new UnresolvedType[types.size()];
        types.toArray(typeParams);
        return typeParams;
    }

    public static UnresolvedType createUnresolvedParameterizedType(String baseTypeSignature, UnresolvedType[] arguments) {
        StringBuffer parameterizedSig = new StringBuffer();
        parameterizedSig.append("P");
        parameterizedSig.append(baseTypeSignature.substring(1, baseTypeSignature.length() - 1));
        if (arguments.length > 0) {
            parameterizedSig.append("<");
            for (int i = 0; i < arguments.length; ++i) {
                parameterizedSig.append(arguments[i].getSignature());
            }
            parameterizedSig.append(">");
        }
        parameterizedSig.append(";");
        return TypeFactory.createUnresolvedParameterizedType(parameterizedSig.toString(), baseTypeSignature, arguments);
    }
}

