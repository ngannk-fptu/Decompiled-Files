/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.parser;

import java.util.ArrayList;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ast.ArrayQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ArrayTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedQualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ParameterizedSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.QualifiedTypeReference;
import org.eclipse.jdt.internal.compiler.ast.SingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.TypeParameter;
import org.eclipse.jdt.internal.compiler.ast.TypeReference;
import org.eclipse.jdt.internal.compiler.ast.Wildcard;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;

public abstract class TypeConverter {
    int namePos;
    protected ProblemReporter problemReporter;
    protected boolean has1_5Compliance;
    protected boolean has14_Compliance;
    private char memberTypeSeparator;

    protected TypeConverter(ProblemReporter problemReporter, char memberTypeSeparator) {
        this.problemReporter = problemReporter;
        this.has1_5Compliance = problemReporter.options.originalComplianceLevel >= 0x310000L;
        this.has14_Compliance = problemReporter.options.originalComplianceLevel >= 0x3A0000L;
        this.memberTypeSeparator = memberTypeSeparator;
    }

    private void addIdentifiers(String typeSignature, int start, int endExclusive, int identCount, ArrayList fragments) {
        if (identCount == 1) {
            char[] identifier = new char[endExclusive - start];
            typeSignature.getChars(start, endExclusive, identifier, 0);
            fragments.add(identifier);
        } else {
            fragments.add(this.extractIdentifiers(typeSignature, start, endExclusive - 1, identCount));
        }
    }

    protected ImportReference createImportReference(String[] importName, int start, int end, boolean onDemand, int modifiers) {
        int length = importName.length;
        long[] positions = new long[length];
        long position = ((long)start << 32) + (long)end;
        char[][] qImportName = new char[length][];
        int i = 0;
        while (i < length) {
            qImportName[i] = importName[i].toCharArray();
            positions[i] = position;
            ++i;
        }
        return new ImportReference(qImportName, positions, onDemand, modifiers);
    }

    protected TypeParameter createTypeParameter(char[] typeParameterName, char[][] typeParameterBounds, int start, int end) {
        int length;
        TypeParameter parameter = new TypeParameter();
        parameter.name = typeParameterName;
        parameter.sourceStart = start;
        parameter.sourceEnd = end;
        if (typeParameterBounds != null && (length = typeParameterBounds.length) > 0) {
            parameter.type = this.createTypeReference(typeParameterBounds[0], start, end);
            if (length > 1) {
                parameter.bounds = new TypeReference[length - 1];
                int i = 1;
                while (i < length) {
                    TypeReference bound = this.createTypeReference(typeParameterBounds[i], start, end);
                    bound.bits |= 0x10;
                    parameter.bounds[i - 1] = bound;
                    ++i;
                }
            }
        }
        return parameter;
    }

    protected TypeReference createTypeReference(char[] typeName, int start, int end, boolean includeGenericsAnyway) {
        int length = typeName.length;
        this.namePos = 0;
        return this.decodeType2(typeName, length, start, end, true);
    }

    protected TypeReference createTypeReference(char[] typeName, int start, int end) {
        int length = typeName.length;
        this.namePos = 0;
        return this.decodeType2(typeName, length, start, end, false);
    }

    protected TypeReference createTypeReference(String typeSignature, int start, int end) {
        int length = typeSignature.length();
        this.namePos = 0;
        return this.decodeType(typeSignature, length, start, end);
    }

    /*
     * Unable to fully structure code
     */
    private TypeReference decodeType(String typeSignature, int length, int start, int end) {
        identCount = 1;
        dim = 0;
        nameFragmentStart = this.namePos;
        nameFragmentEnd = -1;
        nameStarted = false;
        fragments = null;
        block20: while (this.namePos < length) {
            currentChar = typeSignature.charAt(this.namePos);
            switch (currentChar) {
                case 'Z': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.BOOLEAN.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.BOOLEAN.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'B': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.BYTE.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.BYTE.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'C': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.CHAR.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.CHAR.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'D': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.DOUBLE.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.DOUBLE.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'F': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.FLOAT.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.FLOAT.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'I': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.INT.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.INT.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'J': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.LONG.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.LONG.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'S': {
                    if (!nameStarted) {
                        ++this.namePos;
                        if (dim == 0) {
                            return new SingleTypeReference(TypeBinding.SHORT.simpleName, ((long)start << 32) + (long)end);
                        }
                        return new ArrayTypeReference(TypeBinding.SHORT.simpleName, dim, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'V': {
                    if (!nameStarted) {
                        ++this.namePos;
                        return new SingleTypeReference(TypeBinding.VOID.simpleName, ((long)start << 32) + (long)end);
                    }
                    ** GOTO lbl126
                }
                case 'L': 
                case 'Q': 
                case 'T': {
                    if (!nameStarted) {
                        nameFragmentStart = this.namePos + 1;
                        nameStarted = true;
                    }
                    ** GOTO lbl126
                }
                case '*': {
                    ++this.namePos;
                    result = new Wildcard(0);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '+': {
                    ++this.namePos;
                    result = new Wildcard(1);
                    result.bound = this.decodeType(typeSignature, length, start, end);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '-': {
                    ++this.namePos;
                    result = new Wildcard(2);
                    result.bound = this.decodeType(typeSignature, length, start, end);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '[': {
                    ++dim;
                    ** GOTO lbl126
                }
                case ';': 
                case '>': {
                    nameFragmentEnd = this.namePos - 1;
                    ++this.namePos;
                    break block20;
                }
                case '$': {
                    if (this.memberTypeSeparator != '$') ** GOTO lbl126
                }
                case '.': {
                    if (!nameStarted) {
                        nameFragmentStart = this.namePos + 1;
                        nameStarted = true;
                    } else if (this.namePos > nameFragmentStart) {
                        ++identCount;
                    } else {
                        ** GOTO lbl113
                    }
                }
                {
lbl113:
                    // 3 sources

                    ** GOTO lbl126
                }
                case '<': {
                    nameFragmentEnd = this.namePos - 1;
                    if (!this.has1_5Compliance) break block20;
                    if (fragments == null) {
                        fragments = new ArrayList<TypeReference[][]>(2);
                    }
                    this.addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
                    ++this.namePos;
                    arguments = this.decodeTypeArguments(typeSignature, length, start, end);
                    fragments.add(arguments);
                    identCount = 1;
                    nameStarted = false;
                }
lbl126:
                // 16 sources

                default: {
                    ++this.namePos;
                }
            }
        }
        if (fragments == null) {
            if (identCount == 1) {
                if (dim == 0) {
                    nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
                    typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
                    return new SingleTypeReference(nameFragment, ((long)start << 32) + (long)end);
                }
                nameFragment = new char[nameFragmentEnd - nameFragmentStart + 1];
                typeSignature.getChars(nameFragmentStart, nameFragmentEnd + 1, nameFragment, 0);
                return new ArrayTypeReference(nameFragment, dim, ((long)start << 32) + (long)end);
            }
            positions = new long[identCount];
            pos = ((long)start << 32) + (long)end;
            i = 0;
            while (i < identCount) {
                positions[i] = pos;
                ++i;
            }
            identifiers = this.extractIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd, identCount);
            if (dim == 0) {
                return new QualifiedTypeReference(identifiers, positions);
            }
            return new ArrayQualifiedTypeReference(identifiers, dim, positions);
        }
        if (nameStarted) {
            this.addIdentifiers(typeSignature, nameFragmentStart, nameFragmentEnd + 1, identCount, fragments);
        }
        if ((fragmentLength = fragments.size()) == 2 && (firstFragment = fragments.get(0)) instanceof char[]) {
            return new ParameterizedSingleTypeReference((char[])firstFragment, (TypeReference[])fragments.get(1), dim, ((long)start << 32) + (long)end);
        }
        identCount = 0;
        i = 0;
        while (i < fragmentLength) {
            element = fragments.get(i);
            if (element instanceof char[][]) {
                identCount += ((char[][])element).length;
            } else if (element instanceof char[]) {
                ++identCount;
            }
            ++i;
        }
        tokens = new char[identCount][];
        arguments = new TypeReference[identCount][];
        index = 0;
        i = 0;
        while (i < fragmentLength) {
            element = fragments.get(i);
            if (element instanceof char[][]) {
                fragmentTokens = (char[][])element;
                fragmentTokenLength = fragmentTokens.length;
                System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
                index += fragmentTokenLength;
            } else if (element instanceof char[]) {
                tokens[index++] = (char[])element;
            } else {
                arguments[index - 1] = (TypeReference[])element;
            }
            ++i;
        }
        positions = new long[identCount];
        pos = ((long)start << 32) + (long)end;
        i = 0;
        while (i < identCount) {
            positions[i] = pos;
            ++i;
        }
        return new ParameterizedQualifiedTypeReference((char[][])tokens, arguments, dim, positions);
    }

    /*
     * Unable to fully structure code
     */
    private TypeReference decodeType2(char[] typeName, int length, int start, int end, boolean includeGenericsAnyway) {
        identCount = 1;
        dim = 0;
        nameFragmentStart = this.namePos;
        nameFragmentEnd = -1;
        fragments = null;
        block12: while (this.namePos < length) {
            currentChar = typeName[this.namePos];
            switch (currentChar) {
                case '?': {
                    ++this.namePos;
                    while (typeName[this.namePos] == ' ') {
                        ++this.namePos;
                    }
                    block8 : switch (typeName[this.namePos]) {
                        case 's': {
                            max = TypeConstants.WILDCARD_SUPER.length - 1;
                            ahead = 1;
                            while (ahead < max) {
                                if (typeName[this.namePos + ahead] != TypeConstants.WILDCARD_SUPER[ahead + 1]) break block8;
                                ++ahead;
                            }
                            this.namePos += max;
                            result = new Wildcard(2);
                            result.bound = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
                            result.sourceStart = start;
                            result.sourceEnd = end;
                            return result;
                        }
                        case 'e': {
                            max = TypeConstants.WILDCARD_EXTENDS.length - 1;
                            ahead = 1;
                            while (ahead < max) {
                                if (typeName[this.namePos + ahead] != TypeConstants.WILDCARD_EXTENDS[ahead + 1]) break block8;
                                ++ahead;
                            }
                            this.namePos += max;
                            result = new Wildcard(1);
                            result.bound = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
                            result.sourceStart = start;
                            result.sourceEnd = end;
                            return result;
                        }
                    }
                    result = new Wildcard(0);
                    result.sourceStart = start;
                    result.sourceEnd = end;
                    return result;
                }
                case '[': {
                    if (dim == 0 && nameFragmentEnd < 0) {
                        nameFragmentEnd = this.namePos - 1;
                    }
                    ++dim;
                    ** GOTO lbl75
                }
                case ']': {
                    ** GOTO lbl75
                }
                case ',': 
                case '>': {
                    break block12;
                }
                case '.': {
                    if (nameFragmentStart < 0) {
                        nameFragmentStart = this.namePos + 1;
                    }
                    ++identCount;
                    ** GOTO lbl75
                }
                case '<': {
                    if ((this.has1_5Compliance || includeGenericsAnyway) && fragments == null) {
                        fragments = new ArrayList<Object>(2);
                    }
                    nameFragmentEnd = this.namePos - 1;
                    if (this.has1_5Compliance || includeGenericsAnyway) {
                        identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, this.namePos);
                        fragments.add(identifiers);
                    }
                    ++this.namePos;
                    arguments = this.decodeTypeArguments(typeName, length, start, end, includeGenericsAnyway);
                    if (this.has1_5Compliance || includeGenericsAnyway) {
                        fragments.add(arguments);
                        identCount = 0;
                        nameFragmentStart = -1;
                        nameFragmentEnd = -1;
                    }
                }
lbl75:
                // 7 sources

                default: {
                    ++this.namePos;
                }
            }
        }
        return this.decodeType3(typeName, length, start, end, identCount, dim, nameFragmentStart, nameFragmentEnd, fragments);
    }

    private TypeReference decodeType3(char[] typeName, int length, int start, int end, int identCount, int dim, int nameFragmentStart, int nameFragmentEnd, ArrayList fragments) {
        char[][] firstFragment;
        int fragmentLength;
        if (nameFragmentEnd < 0) {
            nameFragmentEnd = this.namePos - 1;
        }
        if (fragments == null) {
            if (identCount == 1) {
                if (dim == 0) {
                    char[] nameFragment;
                    if (nameFragmentStart != 0 || nameFragmentEnd >= 0) {
                        int nameFragmentLength = nameFragmentEnd - nameFragmentStart + 1;
                        nameFragment = new char[nameFragmentLength];
                        System.arraycopy(typeName, nameFragmentStart, nameFragment, 0, nameFragmentLength);
                    } else {
                        nameFragment = typeName;
                    }
                    return new SingleTypeReference(nameFragment, ((long)start << 32) + (long)end);
                }
                int nameFragmentLength = nameFragmentEnd - nameFragmentStart + 1;
                char[] nameFragment = new char[nameFragmentLength];
                System.arraycopy(typeName, nameFragmentStart, nameFragment, 0, nameFragmentLength);
                return new ArrayTypeReference(nameFragment, dim, ((long)start << 32) + (long)end);
            }
            long[] positions = new long[identCount];
            long pos = ((long)start << 32) + (long)end;
            int i = 0;
            while (i < identCount) {
                positions[i] = pos;
                ++i;
            }
            char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
            if (dim == 0) {
                return new QualifiedTypeReference(identifiers, positions);
            }
            return new ArrayQualifiedTypeReference(identifiers, dim, positions);
        }
        if (nameFragmentStart > 0 && nameFragmentStart < length) {
            char[][] identifiers = CharOperation.splitOn('.', typeName, nameFragmentStart, nameFragmentEnd + 1);
            fragments.add(identifiers);
        }
        if ((fragmentLength = fragments.size()) == 2 && (firstFragment = (char[][])fragments.get(0)).length == 1) {
            return new ParameterizedSingleTypeReference(firstFragment[0], (TypeReference[])fragments.get(1), dim, ((long)start << 32) + (long)end);
        }
        identCount = 0;
        int i = 0;
        while (i < fragmentLength) {
            Object element = fragments.get(i);
            if (element instanceof char[][]) {
                identCount += ((char[][])element).length;
            }
            ++i;
        }
        char[][] tokens = new char[identCount][];
        TypeReference[][] arguments = new TypeReference[identCount][];
        int index = 0;
        int i2 = 0;
        while (i2 < fragmentLength) {
            Object element = fragments.get(i2);
            if (element instanceof char[][]) {
                char[][] fragmentTokens = (char[][])element;
                int fragmentTokenLength = fragmentTokens.length;
                System.arraycopy(fragmentTokens, 0, tokens, index, fragmentTokenLength);
                index += fragmentTokenLength;
            } else {
                arguments[index - 1] = (TypeReference[])element;
            }
            ++i2;
        }
        long[] positions = new long[identCount];
        long pos = ((long)start << 32) + (long)end;
        int i3 = 0;
        while (i3 < identCount) {
            positions[i3] = pos;
            ++i3;
        }
        return new ParameterizedQualifiedTypeReference((char[][])tokens, arguments, dim, positions);
    }

    private TypeReference[] decodeTypeArguments(char[] typeName, int length, int start, int end, boolean includeGenericsAnyway) {
        ArrayList<TypeReference> argumentList = new ArrayList<TypeReference>(1);
        int count = 0;
        while (this.namePos < length) {
            TypeReference argument = this.decodeType2(typeName, length, start, end, includeGenericsAnyway);
            ++count;
            argumentList.add(argument);
            if (this.namePos >= length || typeName[this.namePos] == '>') break;
            ++this.namePos;
        }
        TypeReference[] typeArguments = new TypeReference[count];
        argumentList.toArray(typeArguments);
        return typeArguments;
    }

    private TypeReference[] decodeTypeArguments(String typeSignature, int length, int start, int end) {
        ArrayList<TypeReference> argumentList = new ArrayList<TypeReference>(1);
        int count = 0;
        while (this.namePos < length) {
            TypeReference argument = this.decodeType(typeSignature, length, start, end);
            ++count;
            argumentList.add(argument);
            if (this.namePos >= length || typeSignature.charAt(this.namePos) == '>') break;
        }
        TypeReference[] typeArguments = new TypeReference[count];
        argumentList.toArray(typeArguments);
        return typeArguments;
    }

    private char[][] extractIdentifiers(String typeSignature, int start, int endInclusive, int identCount) {
        char[][] result = new char[identCount][];
        int charIndex = start;
        int i = 0;
        while (charIndex < endInclusive) {
            char currentChar = typeSignature.charAt(charIndex);
            if (currentChar == this.memberTypeSeparator || currentChar == '.') {
                int n = i++;
                char[] cArray = new char[charIndex - start];
                result[n] = cArray;
                typeSignature.getChars(start, charIndex, cArray, 0);
                start = ++charIndex;
                continue;
            }
            ++charIndex;
        }
        int n = i++;
        char[] cArray = new char[charIndex - start + 1];
        result[n] = cArray;
        typeSignature.getChars(start, charIndex + 1, cArray, 0);
        return result;
    }
}

