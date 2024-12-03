/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.tools;

import groovy.transform.stc.IncorrectTypeHintException;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.TokenStreamException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.antlr.AntlrParserPlugin;
import org.codehaus.groovy.antlr.parser.GroovyLexer;
import org.codehaus.groovy.antlr.parser.GroovyRecognizer;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.ResolveVisitor;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.ParserException;
import org.codehaus.groovy.syntax.Reduction;
import org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport;

public class GenericsUtils {
    public static final GenericsType[] EMPTY_GENERICS_ARRAY = new GenericsType[0];
    public static final String JAVA_LANG_OBJECT = "java.lang.Object";

    @Deprecated
    public static GenericsType[] alignGenericTypes(GenericsType[] redirectGenericTypes, GenericsType[] parameterizedTypes, GenericsType[] alignmentTarget) {
        if (alignmentTarget == null) {
            return EMPTY_GENERICS_ARRAY;
        }
        if (parameterizedTypes == null || parameterizedTypes.length == 0) {
            return alignmentTarget;
        }
        GenericsType[] generics = new GenericsType[alignmentTarget.length];
        for (GenericsType currentTarget : alignmentTarget) {
            GenericsType match = null;
            if (redirectGenericTypes != null) {
                for (int j = 0; j < redirectGenericTypes.length && match == null; ++j) {
                    ClassNode[] upper;
                    GenericsType redirectGenericType = redirectGenericTypes[j];
                    if (!redirectGenericType.isCompatibleWith(currentTarget.getType())) continue;
                    if (currentTarget.isPlaceholder() && redirectGenericType.isPlaceholder() && !currentTarget.getName().equals(redirectGenericType.getName())) {
                        boolean skip = false;
                        for (int k = j + 1; k < redirectGenericTypes.length && !skip; ++k) {
                            GenericsType ogt = redirectGenericTypes[k];
                            if (!ogt.isPlaceholder() || !ogt.isCompatibleWith(currentTarget.getType()) || !ogt.getName().equals(currentTarget.getName())) continue;
                            skip = true;
                        }
                        if (skip) continue;
                    }
                    match = parameterizedTypes[j];
                    if (!currentTarget.isWildcard()) continue;
                    ClassNode lower = currentTarget.getLowerBound() != null ? match.getType() : null;
                    ClassNode[] currentUpper = currentTarget.getUpperBounds();
                    ClassNode[] classNodeArray = upper = currentUpper != null ? new ClassNode[currentUpper.length] : null;
                    if (upper != null) {
                        for (int k = 0; k < upper.length; ++k) {
                            upper[k] = currentUpper[k].isGenericsPlaceHolder() ? match.getType() : currentUpper[k];
                        }
                    }
                    match = new GenericsType(ClassHelper.makeWithoutCaching("?"), upper, lower);
                    match.setWildcard(true);
                }
            }
            if (match == null) {
                match = currentTarget;
            }
            generics[i] = match;
        }
        return generics;
    }

    public static GenericsType buildWildcardType(ClassNode ... types) {
        ClassNode base = ClassHelper.makeWithoutCaching("?");
        GenericsType gt = new GenericsType(base, types, null);
        gt.setWildcard(true);
        return gt;
    }

    public static Map<String, GenericsType> extractPlaceholders(ClassNode cn) {
        HashMap<String, GenericsType> ret = new HashMap<String, GenericsType>();
        GenericsUtils.extractPlaceholders(cn, ret);
        return ret;
    }

    public static void extractPlaceholders(ClassNode node, Map<String, GenericsType> map) {
        if (node == null) {
            return;
        }
        if (node.isArray()) {
            GenericsUtils.extractPlaceholders(node.getComponentType(), map);
            return;
        }
        if (!node.isUsingGenerics() || !node.isRedirectNode()) {
            return;
        }
        GenericsType[] parameterized = node.getGenericsTypes();
        if (parameterized == null || parameterized.length == 0) {
            return;
        }
        GenericsType[] redirectGenericsTypes = node.redirect().getGenericsTypes();
        if (redirectGenericsTypes == null) {
            redirectGenericsTypes = parameterized;
        }
        for (int i = 0; i < redirectGenericsTypes.length; ++i) {
            String name;
            GenericsType redirectType = redirectGenericsTypes[i];
            if (!redirectType.isPlaceholder() || map.containsKey(name = redirectType.getName())) continue;
            GenericsType value = parameterized[i];
            map.put(name, value);
            if (value.isWildcard()) {
                ClassNode[] upperBounds;
                ClassNode lowerBound = value.getLowerBound();
                if (lowerBound != null) {
                    GenericsUtils.extractPlaceholders(lowerBound, map);
                }
                if ((upperBounds = value.getUpperBounds()) == null) continue;
                for (ClassNode upperBound : upperBounds) {
                    GenericsUtils.extractPlaceholders(upperBound, map);
                }
                continue;
            }
            if (value.isPlaceholder()) continue;
            GenericsUtils.extractPlaceholders(value.getType(), map);
        }
    }

    @Deprecated
    public static ClassNode parameterizeInterfaceGenerics(ClassNode hint, ClassNode target) {
        return GenericsUtils.parameterizeType(hint, target);
    }

    public static ClassNode parameterizeType(ClassNode hint, ClassNode target) {
        ClassNode nextSuperClass;
        if (hint.isArray()) {
            if (target.isArray()) {
                return GenericsUtils.parameterizeType(hint.getComponentType(), target.getComponentType()).makeArray();
            }
            return target;
        }
        if (!target.equals(hint) && StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(target, hint) && !hint.equals(nextSuperClass = ClassHelper.getNextSuperClass(target, hint))) {
            Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(hint);
            GenericsUtils.extractSuperClassGenerics(hint, nextSuperClass, genericsSpec);
            ClassNode result = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, nextSuperClass);
            return GenericsUtils.parameterizeType(result, target);
        }
        Map<String, ClassNode> genericsSpec = GenericsUtils.createGenericsSpec(hint);
        ClassNode targetRedirect = target.redirect();
        genericsSpec = GenericsUtils.createGenericsSpec(targetRedirect, genericsSpec);
        GenericsUtils.extractSuperClassGenerics(hint, targetRedirect, genericsSpec);
        return GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, targetRedirect);
    }

    public static ClassNode nonGeneric(ClassNode type) {
        if (type.isUsingGenerics()) {
            ClassNode nonGen = ClassHelper.makeWithoutCaching(type.getName());
            nonGen.setRedirect(type);
            nonGen.setGenericsTypes(null);
            nonGen.setUsingGenerics(false);
            return nonGen;
        }
        if (type.isArray() && type.getComponentType().isUsingGenerics()) {
            return type.getComponentType().getPlainNodeReference().makeArray();
        }
        return type;
    }

    public static ClassNode newClass(ClassNode type) {
        return type.getPlainNodeReference();
    }

    public static ClassNode makeClassSafe(Class klass) {
        return GenericsUtils.makeClassSafeWithGenerics(ClassHelper.make(klass), new GenericsType[0]);
    }

    public static ClassNode makeClassSafeWithGenerics(Class klass, ClassNode genericsType) {
        GenericsType[] genericsTypes = new GenericsType[]{new GenericsType(genericsType)};
        return GenericsUtils.makeClassSafeWithGenerics(ClassHelper.make(klass), genericsTypes);
    }

    public static ClassNode makeClassSafe0(ClassNode type, GenericsType ... genericTypes) {
        ClassNode plainNodeReference = GenericsUtils.newClass(type);
        if (genericTypes != null && genericTypes.length > 0) {
            plainNodeReference.setGenericsTypes(genericTypes);
            if (type.isGenericsPlaceHolder()) {
                plainNodeReference.setGenericsPlaceHolder(true);
            }
        }
        return plainNodeReference;
    }

    public static ClassNode makeClassSafeWithGenerics(ClassNode type, GenericsType ... genericTypes) {
        if (type.isArray()) {
            return GenericsUtils.makeClassSafeWithGenerics(type.getComponentType(), genericTypes).makeArray();
        }
        GenericsType[] gtypes = new GenericsType[]{};
        if (genericTypes != null) {
            gtypes = new GenericsType[genericTypes.length];
            System.arraycopy(genericTypes, 0, gtypes, 0, gtypes.length);
        }
        return GenericsUtils.makeClassSafe0(type, gtypes);
    }

    public static MethodNode correctToGenericsSpec(Map<String, ClassNode> genericsSpec, MethodNode mn) {
        ClassNode correctedType = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, mn.getReturnType());
        Parameter[] origParameters = mn.getParameters();
        Parameter[] newParameters = new Parameter[origParameters.length];
        for (int i = 0; i < origParameters.length; ++i) {
            Parameter origParameter = origParameters[i];
            newParameters[i] = new Parameter(GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, origParameter.getType()), origParameter.getName(), origParameter.getInitialExpression());
        }
        return new MethodNode(mn.getName(), mn.getModifiers(), correctedType, newParameters, mn.getExceptions(), mn.getCode());
    }

    public static ClassNode correctToGenericsSpecRecurse(Map<String, ClassNode> genericsSpec, ClassNode type) {
        return GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, type, new ArrayList<String>());
    }

    public static ClassNode[] correctToGenericsSpecRecurse(Map<String, ClassNode> genericsSpec, ClassNode[] types) {
        if (types == null || types.length == 1) {
            return types;
        }
        ClassNode[] newTypes = new ClassNode[types.length];
        boolean modified = false;
        for (int i = 0; i < types.length; ++i) {
            newTypes[i] = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, types[i], new ArrayList<String>());
            modified = modified || types[i] != newTypes[i];
        }
        if (!modified) {
            return types;
        }
        return newTypes;
    }

    public static ClassNode correctToGenericsSpecRecurse(Map<String, ClassNode> genericsSpec, ClassNode type, List<String> exclusions) {
        String name;
        if (type.isArray()) {
            return GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, type.getComponentType(), exclusions).makeArray();
        }
        if (type.isGenericsPlaceHolder() && !exclusions.contains(type.getUnresolvedName()) && (type = genericsSpec.get(name = type.getGenericsTypes()[0].getName())) != null && type.isGenericsPlaceHolder() && type.getGenericsTypes() == null) {
            ClassNode placeholder = ClassHelper.makeWithoutCaching(type.getUnresolvedName());
            placeholder.setGenericsPlaceHolder(true);
            type = GenericsUtils.makeClassSafeWithGenerics(type, new GenericsType(placeholder));
        }
        if (type == null) {
            type = ClassHelper.OBJECT_TYPE;
        }
        GenericsType[] oldgTypes = type.getGenericsTypes();
        GenericsType[] newgTypes = GenericsType.EMPTY_ARRAY;
        if (oldgTypes != null) {
            newgTypes = new GenericsType[oldgTypes.length];
            for (int i = 0; i < newgTypes.length; ++i) {
                GenericsType oldgType = oldgTypes[i];
                if (oldgType.isPlaceholder()) {
                    if (genericsSpec.get(oldgType.getName()) != null) {
                        newgTypes[i] = new GenericsType(genericsSpec.get(oldgType.getName()));
                        continue;
                    }
                    newgTypes[i] = new GenericsType(ClassHelper.OBJECT_TYPE);
                    continue;
                }
                if (oldgType.isWildcard()) {
                    ClassNode oldLower = oldgType.getLowerBound();
                    ClassNode lower = oldLower != null ? GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, oldLower, exclusions) : null;
                    ClassNode[] oldUpper = oldgType.getUpperBounds();
                    ClassNode[] upper = null;
                    if (oldUpper != null) {
                        upper = new ClassNode[oldUpper.length];
                        for (int j = 0; j < oldUpper.length; ++j) {
                            upper[j] = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, oldUpper[j], exclusions);
                        }
                    }
                    GenericsType fixed = new GenericsType(oldgType.getType(), upper, lower);
                    fixed.setName(oldgType.getName());
                    fixed.setWildcard(true);
                    newgTypes[i] = fixed;
                    continue;
                }
                newgTypes[i] = new GenericsType(GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, GenericsUtils.correctToGenericsSpec(genericsSpec, oldgType), exclusions));
            }
        }
        return GenericsUtils.makeClassSafeWithGenerics(type, newgTypes);
    }

    public static ClassNode correctToGenericsSpec(Map<String, ClassNode> genericsSpec, GenericsType type) {
        ClassNode ret = null;
        if (type.isPlaceholder()) {
            String name = type.getName();
            ret = genericsSpec.get(name);
        }
        if (ret == null) {
            ret = type.getType();
        }
        return ret;
    }

    public static ClassNode correctToGenericsSpec(Map<String, ClassNode> genericsSpec, ClassNode type) {
        if (type.isArray()) {
            return GenericsUtils.correctToGenericsSpec(genericsSpec, type.getComponentType()).makeArray();
        }
        if (type.isGenericsPlaceHolder()) {
            String name = type.getGenericsTypes()[0].getName();
            type = genericsSpec.get(name);
        }
        if (type == null) {
            type = ClassHelper.OBJECT_TYPE;
        }
        return type;
    }

    public static Map<String, ClassNode> createGenericsSpec(ClassNode current) {
        return GenericsUtils.createGenericsSpec(current, Collections.EMPTY_MAP);
    }

    public static Map<String, ClassNode> createGenericsSpec(ClassNode current, Map<String, ClassNode> oldSpec) {
        HashMap<String, ClassNode> ret = new HashMap<String, ClassNode>(oldSpec);
        GenericsType[] sgts = current.getGenericsTypes();
        if (sgts != null) {
            ClassNode[] spec = new ClassNode[sgts.length];
            for (int i = 0; i < spec.length; ++i) {
                spec[i] = GenericsUtils.correctToGenericsSpec(ret, sgts[i]);
            }
            GenericsType[] newGts = current.redirect().getGenericsTypes();
            if (newGts == null) {
                return ret;
            }
            ret.clear();
            for (int i = 0; i < spec.length; ++i) {
                ret.put(newGts[i].getName(), spec[i]);
            }
        }
        return ret;
    }

    public static Map<String, ClassNode> addMethodGenerics(MethodNode current, Map<String, ClassNode> oldSpec) {
        HashMap<String, ClassNode> ret = new HashMap<String, ClassNode>(oldSpec);
        GenericsType[] sgts = current.getGenericsTypes();
        if (sgts != null) {
            for (GenericsType sgt : sgts) {
                ret.put(sgt.getName(), sgt.getType());
            }
        }
        return ret;
    }

    public static void extractSuperClassGenerics(ClassNode type, ClassNode target, Map<String, ClassNode> spec) {
        if (target == null || type == target) {
            return;
        }
        if (type.isArray() && target.isArray()) {
            GenericsUtils.extractSuperClassGenerics(type.getComponentType(), target.getComponentType(), spec);
        } else if (!type.isArray() || !JAVA_LANG_OBJECT.equals(target.getName())) {
            if (target.isGenericsPlaceHolder() || type.equals(target) || !StaticTypeCheckingSupport.implementsInterfaceOrIsSubclassOf(type, target)) {
                if (target.isGenericsPlaceHolder()) {
                    spec.put(target.getGenericsTypes()[0].getName(), type);
                } else {
                    GenericsUtils.extractSuperClassGenerics(type.getGenericsTypes(), target.getGenericsTypes(), spec);
                }
            } else {
                ClassNode superClass = GenericsUtils.getSuperClass(type, target);
                if (superClass != null) {
                    ClassNode corrected = StaticTypeCheckingSupport.getCorrectedClassNode(type, superClass, false);
                    GenericsUtils.extractSuperClassGenerics(corrected, target, spec);
                } else {
                    throw new GroovyBugError("The type " + type + " seems not to normally extend " + target + ". Sorry, I cannot handle this.");
                }
            }
        }
    }

    public static ClassNode getSuperClass(ClassNode type, ClassNode target) {
        ClassNode superClass = ClassHelper.getNextSuperClass(type, target);
        if (superClass == null && ClassHelper.isPrimitiveType(type)) {
            superClass = ClassHelper.getNextSuperClass(ClassHelper.getWrapper(type), target);
        }
        return superClass;
    }

    private static void extractSuperClassGenerics(GenericsType[] usage, GenericsType[] declaration, Map<String, ClassNode> spec) {
        if (usage == null || declaration == null || declaration.length == 0) {
            return;
        }
        if (usage.length != declaration.length) {
            return;
        }
        for (int i = 0; i < usage.length; ++i) {
            GenericsType ui = usage[i];
            GenericsType di = declaration[i];
            if (di.isPlaceholder()) {
                spec.put(di.getName(), ui.getType());
                continue;
            }
            if (di.isWildcard()) {
                if (ui.isWildcard()) {
                    GenericsUtils.extractSuperClassGenerics(ui.getLowerBound(), di.getLowerBound(), spec);
                    GenericsUtils.extractSuperClassGenerics(ui.getUpperBounds(), di.getUpperBounds(), spec);
                    continue;
                }
                ClassNode cu = ui.getType();
                GenericsUtils.extractSuperClassGenerics(cu, di.getLowerBound(), spec);
                ClassNode[] upperBounds = di.getUpperBounds();
                if (upperBounds == null) continue;
                for (ClassNode cn : upperBounds) {
                    GenericsUtils.extractSuperClassGenerics(cu, cn, spec);
                }
                continue;
            }
            GenericsUtils.extractSuperClassGenerics(ui.getType(), di.getType(), spec);
        }
    }

    private static void extractSuperClassGenerics(ClassNode[] usage, ClassNode[] declaration, Map<String, ClassNode> spec) {
        if (usage == null || declaration == null || declaration.length == 0) {
            return;
        }
        for (int i = 0; i < usage.length; ++i) {
            ClassNode ui = usage[i];
            ClassNode di = declaration[i];
            if (di.isGenericsPlaceHolder()) {
                spec.put(di.getGenericsTypes()[0].getName(), di);
                continue;
            }
            if (!di.isUsingGenerics()) continue;
            GenericsUtils.extractSuperClassGenerics(ui.getGenericsTypes(), di.getGenericsTypes(), spec);
        }
    }

    public static ClassNode[] parseClassNodesFromString(String option, SourceUnit sourceUnit, CompilationUnit compilationUnit, MethodNode mn, ASTNode usage) {
        GroovyLexer lexer = new GroovyLexer(new StringReader("DummyNode<" + option + ">"));
        final GroovyRecognizer rn = GroovyRecognizer.make(lexer);
        try {
            rn.classOrInterfaceType(true);
            final AtomicReference ref = new AtomicReference();
            AntlrParserPlugin plugin = new AntlrParserPlugin(){

                @Override
                public ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) throws ParserException {
                    ref.set(this.makeTypeWithArguments(rn.getAST()));
                    return null;
                }
            };
            plugin.buildAST(null, null, null);
            ClassNode parsedNode = (ClassNode)ref.get();
            GenericsType[] parsedNodeGenericsTypes = parsedNode.getGenericsTypes();
            if (parsedNodeGenericsTypes == null) {
                return null;
            }
            ClassNode[] signature = new ClassNode[parsedNodeGenericsTypes.length];
            for (int i = 0; i < parsedNodeGenericsTypes.length; ++i) {
                GenericsType genericsType = parsedNodeGenericsTypes[i];
                signature[i] = GenericsUtils.resolveClassNode(sourceUnit, compilationUnit, mn, usage, genericsType.getType());
            }
            return signature;
        }
        catch (RecognitionException e) {
            sourceUnit.addError(new IncorrectTypeHintException(mn, (Throwable)e, usage.getLineNumber(), usage.getColumnNumber()));
        }
        catch (TokenStreamException e) {
            sourceUnit.addError(new IncorrectTypeHintException(mn, (Throwable)e, usage.getLineNumber(), usage.getColumnNumber()));
        }
        catch (ParserException e) {
            sourceUnit.addError(new IncorrectTypeHintException(mn, (Throwable)e, usage.getLineNumber(), usage.getColumnNumber()));
        }
        return null;
    }

    private static ClassNode resolveClassNode(final SourceUnit sourceUnit, CompilationUnit compilationUnit, final MethodNode mn, final ASTNode usage, ClassNode parsedNode) {
        ClassNode dummyClass = new ClassNode("dummy", 0, ClassHelper.OBJECT_TYPE);
        dummyClass.setModule(new ModuleNode(sourceUnit));
        dummyClass.setGenericsTypes(mn.getDeclaringClass().getGenericsTypes());
        MethodNode dummyMN = new MethodNode("dummy", 0, parsedNode, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, EmptyStatement.INSTANCE);
        dummyMN.setGenericsTypes(mn.getGenericsTypes());
        dummyClass.addMethod(dummyMN);
        ResolveVisitor visitor = new ResolveVisitor(compilationUnit){

            @Override
            protected void addError(String msg, ASTNode expr) {
                sourceUnit.addError(new IncorrectTypeHintException(mn, msg, usage.getLineNumber(), usage.getColumnNumber()));
            }
        };
        visitor.startResolving(dummyClass, sourceUnit);
        return dummyMN.getReturnType();
    }

    public static GenericsType[] applyGenericsContextToPlaceHolders(Map<String, ClassNode> genericsSpec, GenericsType[] oldPlaceHolders) {
        if (oldPlaceHolders == null || oldPlaceHolders.length == 0) {
            return oldPlaceHolders;
        }
        if (genericsSpec.isEmpty()) {
            return oldPlaceHolders;
        }
        GenericsType[] newTypes = new GenericsType[oldPlaceHolders.length];
        for (int i = 0; i < oldPlaceHolders.length; ++i) {
            ClassNode newLower;
            ClassNode lower;
            ClassNode[] upper;
            GenericsType old = oldPlaceHolders[i];
            if (!old.isPlaceholder()) {
                throw new GroovyBugError("Given generics type " + old + " must be a placeholder!");
            }
            ClassNode fromSpec = genericsSpec.get(old.getName());
            if (fromSpec != null) {
                if (fromSpec.isGenericsPlaceHolder()) {
                    upper = new ClassNode[]{fromSpec.redirect()};
                    newTypes[i] = new GenericsType(fromSpec, upper, null);
                    continue;
                }
                newTypes[i] = new GenericsType(fromSpec);
                continue;
            }
            ClassNode[] newUpper = upper = old.getUpperBounds();
            if (upper != null && upper.length > 0) {
                ClassNode[] upperCorrected = new ClassNode[upper.length];
                for (int j = 0; j < upper.length; ++j) {
                    upperCorrected[i] = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, upper[j]);
                }
                upper = upperCorrected;
            }
            if ((lower = old.getLowerBound()) == (newLower = GenericsUtils.correctToGenericsSpecRecurse(genericsSpec, lower)) && upper == newUpper) {
                newTypes[i] = oldPlaceHolders[i];
                continue;
            }
            ClassNode newPlaceHolder = ClassHelper.make(old.getName());
            GenericsType gt = new GenericsType(newPlaceHolder, newUpper, newLower);
            gt.setPlaceholder(true);
            newTypes[i] = gt;
        }
        return newTypes;
    }
}

