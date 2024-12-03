/*
 * Decompiled with CFR 0.152.
 */
package io.github.classgraph;

import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationInfoList;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassRefOrTypeVariableSignature;
import io.github.classgraph.Classfile;
import io.github.classgraph.ScanResult;
import io.github.classgraph.TypeArgument;
import io.github.classgraph.TypeSignature;
import io.github.classgraph.TypeVariableSignature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import nonapi.io.github.classgraph.types.ParseException;
import nonapi.io.github.classgraph.types.Parser;
import nonapi.io.github.classgraph.types.TypeUtils;

public final class ClassRefTypeSignature
extends ClassRefOrTypeVariableSignature {
    final String className;
    private final List<TypeArgument> typeArguments;
    private final List<String> suffixes;
    private final List<List<TypeArgument>> suffixTypeArguments;
    private List<AnnotationInfoList> suffixTypeAnnotations;

    private ClassRefTypeSignature(String className, List<TypeArgument> typeArguments, List<String> suffixes, List<List<TypeArgument>> suffixTypeArguments) {
        this.className = className;
        this.typeArguments = typeArguments;
        this.suffixes = suffixes;
        this.suffixTypeArguments = suffixTypeArguments;
    }

    public String getBaseClassName() {
        return this.className;
    }

    public String getFullyQualifiedClassName() {
        if (this.suffixes.isEmpty()) {
            return this.className;
        }
        StringBuilder buf = new StringBuilder();
        buf.append(this.className);
        for (String suffix : this.suffixes) {
            buf.append('$');
            buf.append(suffix);
        }
        return buf.toString();
    }

    public List<TypeArgument> getTypeArguments() {
        return this.typeArguments;
    }

    public List<String> getSuffixes() {
        return this.suffixes;
    }

    public List<List<TypeArgument>> getSuffixTypeArguments() {
        return this.suffixTypeArguments;
    }

    public List<AnnotationInfoList> getSuffixTypeAnnotationInfo() {
        return this.suffixTypeAnnotations;
    }

    private void addSuffixTypeAnnotation(int suffixIdx, AnnotationInfo annotationInfo) {
        if (this.suffixTypeAnnotations == null) {
            this.suffixTypeAnnotations = new ArrayList<AnnotationInfoList>(this.suffixes.size());
            for (int i = 0; i < this.suffixes.size(); ++i) {
                this.suffixTypeAnnotations.add(new AnnotationInfoList(1));
            }
        }
        this.suffixTypeAnnotations.get(suffixIdx).add(annotationInfo);
    }

    @Override
    protected void addTypeAnnotation(List<Classfile.TypePathNode> typePath, AnnotationInfo annotationInfo) {
        int numDeeperNestedLevels = 0;
        int nextTypeArgIdx = -1;
        for (Classfile.TypePathNode typePathNode : typePath) {
            if (typePathNode.typePathKind == 1) {
                ++numDeeperNestedLevels;
                continue;
            }
            if (typePathNode.typePathKind == 3) {
                nextTypeArgIdx = typePathNode.typeArgumentIdx;
                break;
            }
            throw new IllegalArgumentException("Bad typePathKind: " + typePathNode.typePathKind);
        }
        int suffixIdx = -1;
        int nestingLevel = -1;
        String typePrefix = this.className;
        while (true) {
            boolean skipSuffix;
            if (suffixIdx >= this.suffixes.size()) {
                throw new IllegalArgumentException("Ran out of nested types while trying to add type annotation");
            }
            if (suffixIdx == this.suffixes.size() - 1) {
                skipSuffix = false;
            } else {
                ClassInfo outerClassInfo = this.scanResult.getClassInfo(typePrefix);
                typePrefix = typePrefix + '$' + this.suffixes.get(suffixIdx + 1);
                ClassInfo innerClassInfo = this.scanResult.getClassInfo(typePrefix);
                boolean bl = skipSuffix = outerClassInfo == null || innerClassInfo == null || outerClassInfo.isInterfaceOrAnnotation() || innerClassInfo.isInterfaceOrAnnotation() || innerClassInfo.isStatic() || !outerClassInfo.getInnerClasses().contains(innerClassInfo);
            }
            if (!skipSuffix && ++nestingLevel >= numDeeperNestedLevels) break;
            ++suffixIdx;
        }
        if (nextTypeArgIdx == -1) {
            if (suffixIdx == -1) {
                this.addTypeAnnotation(annotationInfo);
            } else {
                this.addSuffixTypeAnnotation(suffixIdx, annotationInfo);
            }
        } else {
            List<TypeArgument> typeArgumentList;
            List<TypeArgument> list = typeArgumentList = suffixIdx == -1 ? this.typeArguments : this.suffixTypeArguments.get(suffixIdx);
            if (nextTypeArgIdx < typeArgumentList.size()) {
                List<Classfile.TypePathNode> remainingTypePath = typePath.subList(numDeeperNestedLevels + 1, typePath.size());
                typeArgumentList.get(nextTypeArgIdx).addTypeAnnotation(remainingTypePath, annotationInfo);
            }
        }
    }

    @Override
    public Class<?> loadClass(boolean ignoreExceptions) {
        return super.loadClass(ignoreExceptions);
    }

    @Override
    public Class<?> loadClass() {
        return super.loadClass();
    }

    @Override
    protected String getClassName() {
        return this.getFullyQualifiedClassName();
    }

    @Override
    public ClassInfo getClassInfo() {
        return super.getClassInfo();
    }

    @Override
    void setScanResult(ScanResult scanResult) {
        super.setScanResult(scanResult);
        for (TypeArgument typeArgument : this.typeArguments) {
            typeArgument.setScanResult(scanResult);
        }
        for (List list : this.suffixTypeArguments) {
            for (TypeArgument typeArgument : list) {
                typeArgument.setScanResult(scanResult);
            }
        }
    }

    @Override
    protected void findReferencedClassNames(Set<String> refdClassNames) {
        refdClassNames.add(this.getFullyQualifiedClassName());
        for (TypeArgument typeArgument : this.typeArguments) {
            typeArgument.findReferencedClassNames(refdClassNames);
        }
        for (List list : this.suffixTypeArguments) {
            for (TypeArgument typeArgument : list) {
                typeArgument.findReferencedClassNames(refdClassNames);
            }
        }
    }

    public int hashCode() {
        return this.className.hashCode() + 7 * this.typeArguments.hashCode() + 15 * this.suffixTypeArguments.hashCode() + 31 * (this.typeAnnotationInfo == null ? 0 : this.typeAnnotationInfo.hashCode()) + 64 * (this.suffixTypeAnnotations == null ? 0 : this.suffixTypeAnnotations.hashCode());
    }

    private static boolean suffixesMatch(ClassRefTypeSignature a, ClassRefTypeSignature b) {
        return a.suffixes.equals(b.suffixes) && a.suffixTypeArguments.equals(b.suffixTypeArguments) && Objects.equals(a.suffixTypeAnnotations, b.suffixTypeAnnotations);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClassRefTypeSignature)) {
            return false;
        }
        ClassRefTypeSignature o = (ClassRefTypeSignature)obj;
        return o.className.equals(this.className) && o.typeArguments.equals(this.typeArguments) && Objects.equals(this.typeAnnotationInfo, o.typeAnnotationInfo) && ClassRefTypeSignature.suffixesMatch(o, this);
    }

    @Override
    public boolean equalsIgnoringTypeParams(TypeSignature other) {
        if (other instanceof TypeVariableSignature) {
            return other.equalsIgnoringTypeParams(this);
        }
        if (!(other instanceof ClassRefTypeSignature)) {
            return false;
        }
        ClassRefTypeSignature o = (ClassRefTypeSignature)other;
        return o.className.equals(this.className) && Objects.equals(this.typeAnnotationInfo, o.typeAnnotationInfo) && ClassRefTypeSignature.suffixesMatch(o, this);
    }

    @Override
    protected void toStringInternal(boolean useSimpleNames, AnnotationInfoList annotationsToExclude, StringBuilder buf) {
        if (!useSimpleNames || this.suffixes.isEmpty()) {
            if (this.typeAnnotationInfo != null) {
                for (AnnotationInfo annotationInfo : this.typeAnnotationInfo) {
                    if (annotationsToExclude != null && annotationsToExclude.contains(annotationInfo)) continue;
                    annotationInfo.toString(useSimpleNames, buf);
                    buf.append(' ');
                }
            }
            buf.append(useSimpleNames ? ClassInfo.getSimpleName(this.className) : this.className);
            if (!this.typeArguments.isEmpty()) {
                buf.append('<');
                for (int i = 0; i < this.typeArguments.size(); ++i) {
                    if (i > 0) {
                        buf.append(", ");
                    }
                    this.typeArguments.get(i).toString(useSimpleNames, buf);
                }
                buf.append('>');
            }
        }
        if (!this.suffixes.isEmpty()) {
            int i;
            int n = i = useSimpleNames ? this.suffixes.size() - 1 : 0;
            while (i < this.suffixes.size()) {
                AnnotationInfoList typeAnnotations;
                if (!useSimpleNames) {
                    buf.append('$');
                }
                AnnotationInfoList annotationInfoList = typeAnnotations = this.suffixTypeAnnotations == null ? null : this.suffixTypeAnnotations.get(i);
                if (typeAnnotations != null && !typeAnnotations.isEmpty()) {
                    for (AnnotationInfo annotationInfo : typeAnnotations) {
                        annotationInfo.toString(useSimpleNames, buf);
                        buf.append(' ');
                    }
                }
                buf.append(this.suffixes.get(i));
                List<TypeArgument> suffixTypeArgumentsList = this.suffixTypeArguments.get(i);
                if (!suffixTypeArgumentsList.isEmpty()) {
                    buf.append('<');
                    for (int j = 0; j < suffixTypeArgumentsList.size(); ++j) {
                        if (j > 0) {
                            buf.append(", ");
                        }
                        suffixTypeArgumentsList.get(j).toString(useSimpleNames, buf);
                    }
                    buf.append('>');
                }
                ++i;
            }
        }
    }

    static ClassRefTypeSignature parse(Parser parser, String definingClassName) throws ParseException {
        if (parser.peek() == 'L') {
            List<List<TypeArgument>> suffixTypeArguments;
            List<Object> suffixes;
            parser.next();
            int startParserPosition = parser.getPosition();
            if (!TypeUtils.getIdentifierToken(parser, true)) {
                throw new ParseException(parser, "Could not parse identifier token");
            }
            String className = parser.currToken();
            List<TypeArgument> typeArguments = TypeArgument.parseList(parser, definingClassName);
            boolean dropSuffixes = false;
            if (parser.peek() == '.' || parser.peek() == '$') {
                suffixes = new ArrayList();
                suffixTypeArguments = new ArrayList();
                while (parser.peek() == '.' || parser.peek() == '$') {
                    parser.advance(1);
                    if (!TypeUtils.getIdentifierToken(parser, true)) {
                        suffixes.add("");
                        suffixTypeArguments.add(Collections.emptyList());
                        dropSuffixes = true;
                        continue;
                    }
                    suffixes.add(parser.currToken());
                    suffixTypeArguments.add(TypeArgument.parseList(parser, definingClassName));
                }
                if (dropSuffixes) {
                    className = parser.getSubstring(startParserPosition, parser.getPosition()).replace('/', '.');
                    suffixes = Collections.emptyList();
                    suffixTypeArguments = Collections.emptyList();
                }
            } else {
                suffixes = Collections.emptyList();
                suffixTypeArguments = Collections.emptyList();
            }
            parser.expect(';');
            return new ClassRefTypeSignature(className, typeArguments, suffixes, suffixTypeArguments);
        }
        return null;
    }
}

