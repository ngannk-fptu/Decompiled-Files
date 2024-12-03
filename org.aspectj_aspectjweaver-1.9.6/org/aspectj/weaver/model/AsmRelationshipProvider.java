/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IHierarchy;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.IRelationship;
import org.aspectj.asm.IRelationshipMap;
import org.aspectj.asm.internal.HandleProviderDelimiter;
import org.aspectj.asm.internal.ProgramElement;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.Checker;
import org.aspectj.weaver.Lint;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.NewParentTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ResolvedMember;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.ResolvedTypeMunger;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelShadow;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.model.AsmRelationshipUtils;
import org.aspectj.weaver.patterns.DeclareErrorOrWarning;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.TypePatternList;

public class AsmRelationshipProvider {
    public static final String ADVISES = "advises";
    public static final String ADVISED_BY = "advised by";
    public static final String DECLARES_ON = "declares on";
    public static final String DECLAREDY_BY = "declared by";
    public static final String SOFTENS = "softens";
    public static final String SOFTENED_BY = "softened by";
    public static final String MATCHED_BY = "matched by";
    public static final String MATCHES_DECLARE = "matches declare";
    public static final String INTER_TYPE_DECLARES = "declared on";
    public static final String INTER_TYPE_DECLARED_BY = "aspect declarations";
    public static final String ANNOTATES = "annotates";
    public static final String ANNOTATED_BY = "annotated by";
    private static final String NO_COMMENT = null;

    public static void addDeclareErrorOrWarningRelationship(AsmManager model, Shadow affectedShadow, Checker deow) {
        IProgramElement targetNode;
        if (model == null) {
            return;
        }
        if (affectedShadow.getSourceLocation() == null || deow.getSourceLocation() == null) {
            return;
        }
        if (World.createInjarHierarchy) {
            AsmRelationshipProvider.createHierarchyForBinaryAspect(model, deow);
        }
        if ((targetNode = AsmRelationshipProvider.getNode(model, affectedShadow)) == null) {
            return;
        }
        String targetHandle = targetNode.getHandleIdentifier();
        if (targetHandle == null) {
            return;
        }
        IProgramElement sourceNode = model.getHierarchy().findElementForSourceLine(deow.getSourceLocation());
        String sourceHandle = sourceNode.getHandleIdentifier();
        if (sourceHandle == null) {
            return;
        }
        IRelationshipMap relmap = model.getRelationshipMap();
        IRelationship foreward = relmap.get(sourceHandle, IRelationship.Kind.DECLARE, MATCHED_BY, false, true);
        foreward.addTarget(targetHandle);
        IRelationship back = relmap.get(targetHandle, IRelationship.Kind.DECLARE, MATCHES_DECLARE, false, true);
        if (back != null && back.getTargets() != null) {
            back.addTarget(sourceHandle);
        }
        if (sourceNode.getSourceLocation() != null) {
            model.addAspectInEffectThisBuild(sourceNode.getSourceLocation().getSourceFile());
        }
    }

    private static boolean isMixinRelated(ResolvedTypeMunger typeTransformer) {
        ResolvedTypeMunger.Kind kind = typeTransformer.getKind();
        return kind == ResolvedTypeMunger.MethodDelegate2 || kind == ResolvedTypeMunger.FieldHost || kind == ResolvedTypeMunger.Parent && ((NewParentTypeMunger)typeTransformer).isMixin();
    }

    public static void addRelationship(AsmManager model, ResolvedType onType, ResolvedTypeMunger typeTransformer, ResolvedType originatingAspect) {
        if (model == null) {
            return;
        }
        if (World.createInjarHierarchy && AsmRelationshipProvider.isBinaryAspect(originatingAspect)) {
            AsmRelationshipProvider.createHierarchy(model, typeTransformer, originatingAspect);
        }
        if (originatingAspect.getSourceLocation() != null) {
            String sourceHandle = "";
            IProgramElement sourceNode = null;
            if (typeTransformer.getSourceLocation() != null && typeTransformer.getSourceLocation().getOffset() != -1 && !AsmRelationshipProvider.isMixinRelated(typeTransformer)) {
                sourceNode = model.getHierarchy().findElementForType(originatingAspect.getPackageName(), originatingAspect.getClassName());
                IProgramElement closer = model.getHierarchy().findCloserMatchForLineNumber(sourceNode, typeTransformer.getSourceLocation().getLine());
                if (closer != null) {
                    sourceNode = closer;
                }
                if (sourceNode == null && World.createInjarHierarchy) {
                    AsmRelationshipProvider.createHierarchy(model, typeTransformer, originatingAspect);
                    if (typeTransformer.getSourceLocation() != null && typeTransformer.getSourceLocation().getOffset() != -1 && !AsmRelationshipProvider.isMixinRelated(typeTransformer)) {
                        sourceNode = model.getHierarchy().findElementForType(originatingAspect.getPackageName(), originatingAspect.getClassName());
                        IProgramElement closer2 = model.getHierarchy().findCloserMatchForLineNumber(sourceNode, typeTransformer.getSourceLocation().getLine());
                        if (closer2 != null) {
                            sourceNode = closer2;
                        }
                    } else {
                        sourceNode = model.getHierarchy().findElementForType(originatingAspect.getPackageName(), originatingAspect.getClassName());
                    }
                }
                sourceHandle = sourceNode.getHandleIdentifier();
            } else {
                sourceNode = model.getHierarchy().findElementForType(originatingAspect.getPackageName(), originatingAspect.getClassName());
                sourceHandle = sourceNode.getHandleIdentifier();
            }
            if (sourceHandle == null) {
                return;
            }
            String targetHandle = AsmRelationshipProvider.findOrFakeUpNode(model, onType);
            if (targetHandle == null) {
                return;
            }
            IRelationshipMap mapper = model.getRelationshipMap();
            IRelationship foreward = mapper.get(sourceHandle, IRelationship.Kind.DECLARE_INTER_TYPE, INTER_TYPE_DECLARES, false, true);
            foreward.addTarget(targetHandle);
            IRelationship back = mapper.get(targetHandle, IRelationship.Kind.DECLARE_INTER_TYPE, INTER_TYPE_DECLARED_BY, false, true);
            back.addTarget(sourceHandle);
            if (sourceNode != null && sourceNode.getSourceLocation() != null) {
                model.addAspectInEffectThisBuild(sourceNode.getSourceLocation().getSourceFile());
            }
        }
    }

    private static String findOrFakeUpNode(AsmManager model, ResolvedType onType) {
        IHierarchy hierarchy = model.getHierarchy();
        ISourceLocation sourceLocation = onType.getSourceLocation();
        String canonicalFilePath = model.getCanonicalFilePath(sourceLocation.getSourceFile());
        int lineNumber = sourceLocation.getLine();
        IProgramElement node = hierarchy.findNodeForSourceFile(hierarchy.getRoot(), canonicalFilePath);
        if (node == null) {
            String jarPath;
            String element;
            String bpath = onType.getBinaryPath();
            if (bpath == null) {
                return model.getHandleProvider().createHandleIdentifier(AsmRelationshipProvider.createFileStructureNode(model, canonicalFilePath));
            }
            IProgramElement programElement = model.getHierarchy().getRoot();
            StringBuffer phantomHandle = new StringBuffer();
            phantomHandle.append(programElement.getHandleIdentifier());
            phantomHandle.append(HandleProviderDelimiter.PACKAGEFRAGMENTROOT.getDelimiter()).append(HandleProviderDelimiter.PHANTOM.getDelimiter());
            int pos = bpath.indexOf(33);
            if (pos != -1 && (element = model.getHandleElementForInpath(jarPath = bpath.substring(0, pos))) != null) {
                phantomHandle.append(element);
            }
            String packageName = onType.getPackageName();
            phantomHandle.append(HandleProviderDelimiter.PACKAGEFRAGMENT.getDelimiter()).append(packageName);
            int dotClassPosition = bpath.lastIndexOf(".class");
            if (dotClassPosition == -1) {
                phantomHandle.append(HandleProviderDelimiter.CLASSFILE.getDelimiter()).append("UNKNOWN.class");
            } else {
                char ch;
                int startPosition;
                for (startPosition = dotClassPosition; startPosition > 0 && (ch = bpath.charAt(startPosition)) != '/' && ch != '\\' && ch != '!'; --startPosition) {
                }
                String classFile = bpath.substring(startPosition + 1, dotClassPosition + 6);
                phantomHandle.append(HandleProviderDelimiter.CLASSFILE.getDelimiter()).append(classFile);
            }
            phantomHandle.append(HandleProviderDelimiter.TYPE.getDelimiter()).append(onType.getClassName());
            return phantomHandle.toString();
        }
        IProgramElement closernode = hierarchy.findCloserMatchForLineNumber(node, lineNumber);
        if (closernode == null) {
            return node.getHandleIdentifier();
        }
        return closernode.getHandleIdentifier();
    }

    public static IProgramElement createFileStructureNode(AsmManager asm, String sourceFilePath) {
        int lastSlash = sourceFilePath.lastIndexOf(92);
        if (lastSlash == -1) {
            lastSlash = sourceFilePath.lastIndexOf(47);
        }
        int i = sourceFilePath.lastIndexOf(33);
        int j = sourceFilePath.indexOf(".class");
        if (i > lastSlash && i != -1 && j != -1) {
            lastSlash = i;
        }
        String fileName = sourceFilePath.substring(lastSlash + 1);
        ProgramElement fileNode = new ProgramElement(asm, fileName, IProgramElement.Kind.FILE_JAVA, new SourceLocation(new File(sourceFilePath), 1, 1), 0, null, null);
        fileNode.addChild(IHierarchy.NO_STRUCTURE);
        return fileNode;
    }

    private static boolean isBinaryAspect(ResolvedType aspect) {
        return aspect.getBinaryPath() != null;
    }

    private static ISourceLocation getBinarySourceLocation(ResolvedType aspect, ISourceLocation sl) {
        if (sl == null) {
            return null;
        }
        String sourceFileName = null;
        if (aspect instanceof ReferenceType) {
            String s = ((ReferenceType)aspect).getDelegate().getSourcefilename();
            int i = s.lastIndexOf(47);
            sourceFileName = i != -1 ? s.substring(i + 1) : s;
        }
        SourceLocation sLoc = new SourceLocation(AsmRelationshipProvider.getBinaryFile(aspect), sl.getLine(), sl.getEndLine(), sl.getColumn() == 0 ? -2147483647 : sl.getColumn(), sl.getContext(), sourceFileName);
        return sLoc;
    }

    private static ISourceLocation createSourceLocation(String sourcefilename, ResolvedType aspect, ISourceLocation sl) {
        SourceLocation sLoc = new SourceLocation(AsmRelationshipProvider.getBinaryFile(aspect), sl.getLine(), sl.getEndLine(), sl.getColumn() == 0 ? -2147483647 : sl.getColumn(), sl.getContext(), sourcefilename);
        return sLoc;
    }

    private static String getSourceFileName(ResolvedType aspect) {
        String sourceFileName = null;
        if (aspect instanceof ReferenceType) {
            String s = ((ReferenceType)aspect).getDelegate().getSourcefilename();
            int i = s.lastIndexOf(47);
            sourceFileName = i != -1 ? s.substring(i + 1) : s;
        }
        return sourceFileName;
    }

    private static File getBinaryFile(ResolvedType aspect) {
        String s = aspect.getBinaryPath();
        File f = aspect.getSourceLocation().getSourceFile();
        int i = f.getPath().lastIndexOf(46);
        String path = null;
        path = i != -1 ? f.getPath().substring(0, i) + ".class" : f.getPath() + ".class";
        return new File(s + "!" + path);
    }

    private static void createHierarchy(AsmManager model, ResolvedTypeMunger typeTransformer, ResolvedType aspect) {
        IProgramElement filenode = model.getHierarchy().findElementForSourceLine(typeTransformer.getSourceLocation());
        if (filenode == null && (typeTransformer.getKind() == ResolvedTypeMunger.MethodDelegate2 || typeTransformer.getKind() == ResolvedTypeMunger.FieldHost)) {
            return;
        }
        if (!filenode.getKind().equals(IProgramElement.Kind.FILE_JAVA)) {
            return;
        }
        ISourceLocation binLocation = AsmRelationshipProvider.getBinarySourceLocation(aspect, aspect.getSourceLocation());
        String f = AsmRelationshipProvider.getBinaryFile(aspect).getName();
        ProgramElement classFileNode = new ProgramElement(model, f, IProgramElement.Kind.FILE, binLocation, 0, null, null);
        IProgramElement root = model.getHierarchy().getRoot();
        IProgramElement binaries = model.getHierarchy().findElementForLabel(root, IProgramElement.Kind.SOURCE_FOLDER, "binaries");
        if (binaries == null) {
            binaries = new ProgramElement(model, "binaries", IProgramElement.Kind.SOURCE_FOLDER, new ArrayList<IProgramElement>());
            root.addChild(binaries);
        }
        String packagename = aspect.getPackageName() == null ? "" : aspect.getPackageName();
        IProgramElement pkgNode = model.getHierarchy().findElementForLabel(binaries, IProgramElement.Kind.PACKAGE, packagename);
        if (pkgNode == null) {
            pkgNode = new ProgramElement(model, packagename, IProgramElement.Kind.PACKAGE, new ArrayList<IProgramElement>());
            binaries.addChild(pkgNode);
            pkgNode.addChild(classFileNode);
        } else {
            pkgNode.addChild(classFileNode);
            for (IProgramElement element : pkgNode.getChildren()) {
                if (element.equals(classFileNode) || !element.getHandleIdentifier().equals(classFileNode.getHandleIdentifier())) continue;
                pkgNode.removeChild(classFileNode);
                return;
            }
        }
        ProgramElement aspectNode = new ProgramElement(model, aspect.getSimpleName(), IProgramElement.Kind.ASPECT, AsmRelationshipProvider.getBinarySourceLocation(aspect, aspect.getSourceLocation()), aspect.getModifiers(), null, null);
        classFileNode.addChild(aspectNode);
        AsmRelationshipProvider.addChildNodes(model, aspect, (IProgramElement)aspectNode, aspect.getDeclaredPointcuts());
        AsmRelationshipProvider.addChildNodes(model, aspect, (IProgramElement)aspectNode, aspect.getDeclaredAdvice());
        AsmRelationshipProvider.addChildNodes(model, aspect, (IProgramElement)aspectNode, aspect.getDeclares());
        AsmRelationshipProvider.addChildNodes(model, aspect, (IProgramElement)aspectNode, aspect.getTypeMungers());
    }

    public static void addDeclareAnnotationRelationship(AsmManager model, ISourceLocation declareAnnotationLocation, ISourceLocation annotatedLocation, boolean isRemove) {
        if (model == null) {
            return;
        }
        IProgramElement sourceNode = model.getHierarchy().findElementForSourceLine(declareAnnotationLocation);
        String sourceHandle = sourceNode.getHandleIdentifier();
        if (sourceHandle == null) {
            return;
        }
        IProgramElement targetNode = model.getHierarchy().findElementForSourceLine(annotatedLocation);
        String targetHandle = targetNode.getHandleIdentifier();
        if (targetHandle == null) {
            return;
        }
        IRelationshipMap mapper = model.getRelationshipMap();
        IRelationship foreward = mapper.get(sourceHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATES, false, true);
        foreward.addTarget(targetHandle);
        IRelationship back = mapper.get(targetHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATED_BY, false, true);
        back.addTarget(sourceHandle);
        if (sourceNode.getSourceLocation() != null) {
            model.addAspectInEffectThisBuild(sourceNode.getSourceLocation().getSourceFile());
        }
    }

    public static void createHierarchyForBinaryAspect(AsmManager asm, ShadowMunger munger) {
        if (!munger.isBinary()) {
            return;
        }
        IProgramElement sourceFileNode = asm.getHierarchy().findElementForSourceLine(munger.getSourceLocation());
        if (!sourceFileNode.getKind().equals(IProgramElement.Kind.FILE_JAVA)) {
            return;
        }
        ResolvedType aspect = munger.getDeclaringType();
        ProgramElement classFileNode = new ProgramElement(asm, sourceFileNode.getName(), IProgramElement.Kind.FILE, munger.getBinarySourceLocation(aspect.getSourceLocation()), 0, null, null);
        IProgramElement root = asm.getHierarchy().getRoot();
        IProgramElement binaries = asm.getHierarchy().findElementForLabel(root, IProgramElement.Kind.SOURCE_FOLDER, "binaries");
        if (binaries == null) {
            binaries = new ProgramElement(asm, "binaries", IProgramElement.Kind.SOURCE_FOLDER, new ArrayList<IProgramElement>());
            root.addChild(binaries);
        }
        String packagename = aspect.getPackageName() == null ? "" : aspect.getPackageName();
        IProgramElement pkgNode = asm.getHierarchy().findElementForLabel(binaries, IProgramElement.Kind.PACKAGE, packagename);
        if (pkgNode == null) {
            pkgNode = new ProgramElement(asm, packagename, IProgramElement.Kind.PACKAGE, new ArrayList<IProgramElement>());
            binaries.addChild(pkgNode);
            pkgNode.addChild(classFileNode);
        } else {
            pkgNode.addChild(classFileNode);
            for (IProgramElement element : pkgNode.getChildren()) {
                if (element.equals(classFileNode) || !element.getHandleIdentifier().equals(classFileNode.getHandleIdentifier())) continue;
                pkgNode.removeChild(classFileNode);
                return;
            }
        }
        ProgramElement aspectNode = new ProgramElement(asm, aspect.getSimpleName(), IProgramElement.Kind.ASPECT, munger.getBinarySourceLocation(aspect.getSourceLocation()), aspect.getModifiers(), null, null);
        classFileNode.addChild(aspectNode);
        String sourcefilename = AsmRelationshipProvider.getSourceFileName(aspect);
        AsmRelationshipProvider.addPointcuts(asm, sourcefilename, aspect, aspectNode, aspect.getDeclaredPointcuts());
        AsmRelationshipProvider.addChildNodes(asm, aspect, (IProgramElement)aspectNode, aspect.getDeclaredAdvice());
        AsmRelationshipProvider.addChildNodes(asm, aspect, (IProgramElement)aspectNode, aspect.getDeclares());
        AsmRelationshipProvider.addChildNodes(asm, aspect, (IProgramElement)aspectNode, aspect.getTypeMungers());
    }

    private static void addPointcuts(AsmManager model, String sourcefilename, ResolvedType aspect, IProgramElement containingAspect, ResolvedMember[] pointcuts) {
        for (int i = 0; i < pointcuts.length; ++i) {
            ISourceLocation sLoc;
            ResolvedMember pointcut = pointcuts[i];
            if (!(pointcut instanceof ResolvedPointcutDefinition)) continue;
            ResolvedPointcutDefinition rpcd = (ResolvedPointcutDefinition)pointcut;
            Pointcut p = rpcd.getPointcut();
            ISourceLocation iSourceLocation = sLoc = p == null ? null : p.getSourceLocation();
            if (sLoc == null) {
                sLoc = rpcd.getSourceLocation();
            }
            ISourceLocation pointcutLocation = sLoc == null ? null : AsmRelationshipProvider.createSourceLocation(sourcefilename, aspect, sLoc);
            ProgramElement pointcutElement = new ProgramElement(model, pointcut.getName(), IProgramElement.Kind.POINTCUT, pointcutLocation, pointcut.getModifiers(), NO_COMMENT, Collections.emptyList());
            containingAspect.addChild(pointcutElement);
        }
    }

    private static void addChildNodes(AsmManager asm, ResolvedType aspect, IProgramElement parent, ResolvedMember[] children) {
        for (int i = 0; i < children.length; ++i) {
            ISourceLocation sLoc;
            ResolvedMember pcd = children[i];
            if (!(pcd instanceof ResolvedPointcutDefinition)) continue;
            ResolvedPointcutDefinition rpcd = (ResolvedPointcutDefinition)pcd;
            Pointcut p = rpcd.getPointcut();
            ISourceLocation iSourceLocation = sLoc = p == null ? null : p.getSourceLocation();
            if (sLoc == null) {
                sLoc = rpcd.getSourceLocation();
            }
            parent.addChild(new ProgramElement(asm, pcd.getName(), IProgramElement.Kind.POINTCUT, AsmRelationshipProvider.getBinarySourceLocation(aspect, sLoc), pcd.getModifiers(), null, Collections.emptyList()));
        }
    }

    private static void addChildNodes(AsmManager asm, ResolvedType aspect, IProgramElement parent, Collection<?> children) {
        int deCtr = 1;
        int dwCtr = 1;
        for (Object element : children) {
            IProgramElement newChild;
            if (element instanceof DeclareErrorOrWarning) {
                DeclareErrorOrWarning decl = (DeclareErrorOrWarning)element;
                int counter = 0;
                counter = decl.isError() ? deCtr++ : dwCtr++;
                parent.addChild(AsmRelationshipProvider.createDeclareErrorOrWarningChild(asm, aspect, decl, counter));
                continue;
            }
            if (element instanceof Advice) {
                Advice advice = (Advice)element;
                parent.addChild(AsmRelationshipProvider.createAdviceChild(asm, advice));
                continue;
            }
            if (element instanceof DeclareParents) {
                parent.addChild(AsmRelationshipProvider.createDeclareParentsChild(asm, (DeclareParents)element));
                continue;
            }
            if (!(element instanceof BcelTypeMunger) || (newChild = AsmRelationshipProvider.createIntertypeDeclaredChild(asm, aspect, (BcelTypeMunger)element)) == null) continue;
            parent.addChild(newChild);
        }
    }

    private static IProgramElement createDeclareErrorOrWarningChild(AsmManager model, ResolvedType aspect, DeclareErrorOrWarning decl, int count) {
        ProgramElement deowNode = new ProgramElement(model, decl.getName(), decl.isError() ? IProgramElement.Kind.DECLARE_ERROR : IProgramElement.Kind.DECLARE_WARNING, AsmRelationshipProvider.getBinarySourceLocation(aspect, decl.getSourceLocation()), decl.getDeclaringType().getModifiers(), null, null);
        deowNode.setDetails("\"" + AsmRelationshipUtils.genDeclareMessage(decl.getMessage()) + "\"");
        if (count != -1) {
            deowNode.setBytecodeName(decl.getName() + "_" + count);
        }
        return deowNode;
    }

    private static IProgramElement createAdviceChild(AsmManager model, Advice advice) {
        ProgramElement adviceNode = new ProgramElement(model, advice.getKind().getName(), IProgramElement.Kind.ADVICE, advice.getBinarySourceLocation(advice.getSourceLocation()), advice.getSignature().getModifiers(), null, Collections.emptyList());
        adviceNode.setDetails(AsmRelationshipUtils.genPointcutDetails(advice.getPointcut()));
        adviceNode.setBytecodeName(advice.getSignature().getName());
        return adviceNode;
    }

    private static IProgramElement createIntertypeDeclaredChild(AsmManager model, ResolvedType aspect, BcelTypeMunger itd) {
        ResolvedTypeMunger rtMunger = itd.getMunger();
        ResolvedMember sig = rtMunger.getSignature();
        ResolvedTypeMunger.Kind kind = rtMunger.getKind();
        if (kind == ResolvedTypeMunger.Field) {
            String name = sig.getDeclaringType().getClassName() + "." + sig.getName();
            if (name.indexOf("$") != -1) {
                name = name.substring(name.indexOf("$") + 1);
            }
            ProgramElement pe = new ProgramElement(model, name, IProgramElement.Kind.INTER_TYPE_FIELD, AsmRelationshipProvider.getBinarySourceLocation(aspect, itd.getSourceLocation()), rtMunger.getSignature().getModifiers(), null, Collections.emptyList());
            pe.setCorrespondingType(sig.getReturnType().getName());
            return pe;
        }
        if (kind == ResolvedTypeMunger.Method) {
            String name = sig.getDeclaringType().getClassName() + "." + sig.getName();
            if (name.indexOf("$") != -1) {
                name = name.substring(name.indexOf("$") + 1);
            }
            ProgramElement pe = new ProgramElement(model, name, IProgramElement.Kind.INTER_TYPE_METHOD, AsmRelationshipProvider.getBinarySourceLocation(aspect, itd.getSourceLocation()), rtMunger.getSignature().getModifiers(), null, Collections.emptyList());
            AsmRelationshipProvider.setParams(pe, sig);
            return pe;
        }
        if (kind == ResolvedTypeMunger.Constructor) {
            String name = sig.getDeclaringType().getClassName() + "." + sig.getDeclaringType().getClassName();
            if (name.indexOf("$") != -1) {
                name = name.substring(name.indexOf("$") + 1);
            }
            ProgramElement pe = new ProgramElement(model, name, IProgramElement.Kind.INTER_TYPE_CONSTRUCTOR, AsmRelationshipProvider.getBinarySourceLocation(aspect, itd.getSourceLocation()), rtMunger.getSignature().getModifiers(), null, Collections.emptyList());
            AsmRelationshipProvider.setParams(pe, sig);
            return pe;
        }
        return null;
    }

    private static void setParams(IProgramElement pe, ResolvedMember sig) {
        UnresolvedType[] ts = sig.getParameterTypes();
        pe.setParameterNames(Collections.emptyList());
        if (ts == null) {
            pe.setParameterSignatures(Collections.emptyList(), Collections.emptyList());
        } else {
            ArrayList<char[]> paramSigs = new ArrayList<char[]>();
            for (int i = 0; i < ts.length; ++i) {
                paramSigs.add(ts[i].getSignature().toCharArray());
            }
            pe.setParameterSignatures(paramSigs, Collections.emptyList());
        }
        pe.setCorrespondingType(sig.getReturnType().getName());
    }

    private static IProgramElement createDeclareParentsChild(AsmManager model, DeclareParents decp) {
        ProgramElement decpElement = new ProgramElement(model, "declare parents", IProgramElement.Kind.DECLARE_PARENTS, AsmRelationshipProvider.getBinarySourceLocation(decp.getDeclaringType(), decp.getSourceLocation()), 1, null, Collections.emptyList());
        AsmRelationshipProvider.setParentTypesOnDeclareParentsNode(decp, decpElement);
        return decpElement;
    }

    private static void setParentTypesOnDeclareParentsNode(DeclareParents decp, IProgramElement decpElement) {
        TypePatternList tpl = decp.getParents();
        ArrayList<String> parents = new ArrayList<String>();
        for (int i = 0; i < tpl.size(); ++i) {
            parents.add(tpl.get(i).getExactType().getName().replaceAll("\\$", "."));
        }
        decpElement.setParentTypes(parents);
    }

    public static String getHandle(AsmManager asm, Advice advice) {
        ISourceLocation sl;
        if (null == advice.handle && (sl = advice.getSourceLocation()) != null) {
            IProgramElement ipe = asm.getHierarchy().findElementForSourceLine(sl);
            advice.handle = ipe.getHandleIdentifier();
        }
        return advice.handle;
    }

    public static void addAdvisedRelationship(AsmManager model, Shadow matchedShadow, ShadowMunger munger) {
        if (model == null) {
            return;
        }
        if (munger instanceof Advice) {
            Advice advice = (Advice)munger;
            if (advice.getKind().isPerEntry() || advice.getKind().isCflow()) {
                return;
            }
            if (World.createInjarHierarchy) {
                AsmRelationshipProvider.createHierarchyForBinaryAspect(model, advice);
            }
            IRelationshipMap mapper = model.getRelationshipMap();
            IProgramElement targetNode = AsmRelationshipProvider.getNode(model, matchedShadow);
            if (targetNode == null) {
                return;
            }
            boolean runtimeTest = advice.hasDynamicTests();
            IProgramElement.ExtraInformation extra = new IProgramElement.ExtraInformation();
            String adviceHandle = AsmRelationshipProvider.getHandle(model, advice);
            if (adviceHandle == null) {
                return;
            }
            extra.setExtraAdviceInformation(advice.getKind().getName());
            IProgramElement adviceElement = model.getHierarchy().findElementForHandle(adviceHandle);
            if (adviceElement != null) {
                adviceElement.setExtraInfo(extra);
            }
            String targetHandle = targetNode.getHandleIdentifier();
            if (advice.getKind().equals(AdviceKind.Softener)) {
                IRelationship back;
                IRelationship foreward = mapper.get(adviceHandle, IRelationship.Kind.DECLARE_SOFT, SOFTENS, runtimeTest, true);
                if (foreward != null) {
                    foreward.addTarget(targetHandle);
                }
                if ((back = mapper.get(targetHandle, IRelationship.Kind.DECLARE, SOFTENED_BY, runtimeTest, true)) != null) {
                    back.addTarget(adviceHandle);
                }
            } else {
                IRelationship back;
                IRelationship foreward = mapper.get(adviceHandle, IRelationship.Kind.ADVICE, ADVISES, runtimeTest, true);
                if (foreward != null) {
                    foreward.addTarget(targetHandle);
                }
                if ((back = mapper.get(targetHandle, IRelationship.Kind.ADVICE, ADVISED_BY, runtimeTest, true)) != null) {
                    back.addTarget(adviceHandle);
                }
            }
            if (adviceElement.getSourceLocation() != null) {
                model.addAspectInEffectThisBuild(adviceElement.getSourceLocation().getSourceFile());
            }
        }
    }

    protected static IProgramElement getNode(AsmManager model, Shadow shadow) {
        UnresolvedType actualType;
        UnresolvedType type;
        Member actualEnclosingMember;
        Member enclosingMember = shadow.getEnclosingCodeSignature();
        IProgramElement enclosingNode = null;
        enclosingNode = shadow instanceof BcelShadow ? ((actualEnclosingMember = ((BcelShadow)shadow).getRealEnclosingCodeSignature()) == null ? AsmRelationshipProvider.lookupMember(model.getHierarchy(), shadow.getEnclosingType(), enclosingMember) : ((type = enclosingMember.getDeclaringType()).equals(actualType = actualEnclosingMember.getDeclaringType()) ? AsmRelationshipProvider.lookupMember(model.getHierarchy(), shadow.getEnclosingType(), enclosingMember) : AsmRelationshipProvider.lookupMember(model.getHierarchy(), shadow.getEnclosingType(), actualEnclosingMember))) : AsmRelationshipProvider.lookupMember(model.getHierarchy(), shadow.getEnclosingType(), enclosingMember);
        if (enclosingNode == null) {
            Lint.Kind err = shadow.getIWorld().getLint().shadowNotInStructure;
            if (err.isEnabled()) {
                err.signal(shadow.toString(), shadow.getSourceLocation());
            }
            return null;
        }
        Member shadowSig = shadow.getSignature();
        if (shadow.getKind() == Shadow.MethodCall || shadow.getKind() == Shadow.ConstructorCall || !shadowSig.equals(enclosingMember)) {
            IProgramElement bodyNode = AsmRelationshipProvider.findOrCreateCodeNode(model, enclosingNode, shadowSig, shadow);
            return bodyNode;
        }
        return enclosingNode;
    }

    private static boolean sourceLinesMatch(ISourceLocation location1, ISourceLocation location2) {
        return location1.getLine() == location2.getLine();
    }

    private static IProgramElement findOrCreateCodeNode(AsmManager asm, IProgramElement enclosingNode, Member shadowSig, Shadow shadow) {
        for (IProgramElement node : enclosingNode.getChildren()) {
            int excl = node.getBytecodeName().lastIndexOf(33);
            if ((excl == -1 || !shadowSig.getName().equals(node.getBytecodeName().substring(0, excl))) && !shadowSig.getName().equals(node.getBytecodeName()) || !shadowSig.getSignature().equals(node.getBytecodeSignature()) || !AsmRelationshipProvider.sourceLinesMatch(node.getSourceLocation(), shadow.getSourceLocation())) continue;
            return node;
        }
        ISourceLocation sl = shadow.getSourceLocation();
        SourceLocation peLoc = new SourceLocation(enclosingNode.getSourceLocation().getSourceFile(), sl.getLine());
        peLoc.setOffset(sl.getOffset());
        ProgramElement peNode = new ProgramElement(asm, shadow.toString(), IProgramElement.Kind.CODE, peLoc, 0, null, null);
        int numberOfChildrenWithThisName = 0;
        for (IProgramElement child : enclosingNode.getChildren()) {
            if (!child.getName().equals(shadow.toString())) continue;
            ++numberOfChildrenWithThisName;
        }
        peNode.setBytecodeName(shadowSig.getName() + "!" + String.valueOf(numberOfChildrenWithThisName + 1));
        peNode.setBytecodeSignature(shadowSig.getSignature());
        enclosingNode.addChild(peNode);
        return peNode;
    }

    private static IProgramElement lookupMember(IHierarchy model, UnresolvedType declaringType, Member member) {
        IProgramElement typeElement = model.findElementForType(declaringType.getPackageName(), declaringType.getClassName());
        if (typeElement == null) {
            return null;
        }
        for (IProgramElement element : typeElement.getChildren()) {
            if (!member.getName().equals(element.getBytecodeName()) || !member.getSignature().equals(element.getBytecodeSignature())) continue;
            return element;
        }
        return typeElement;
    }

    public static void addDeclareAnnotationMethodRelationship(ISourceLocation sourceLocation, String affectedTypeName, ResolvedMember affectedMethod, AsmManager model) {
        IHierarchy hierarchy;
        IProgramElement typeElem;
        if (model == null) {
            return;
        }
        String pkg = null;
        String type = affectedTypeName;
        int packageSeparator = affectedTypeName.lastIndexOf(".");
        if (packageSeparator != -1) {
            pkg = affectedTypeName.substring(0, packageSeparator);
            type = affectedTypeName.substring(packageSeparator + 1);
        }
        if ((typeElem = (hierarchy = model.getHierarchy()).findElementForType(pkg, type)) == null) {
            return;
        }
        if (!typeElem.getKind().isType()) {
            throw new IllegalStateException("Did not find a type element, found a " + typeElem.getKind() + " element");
        }
        StringBuilder parmString = new StringBuilder("(");
        UnresolvedType[] args = affectedMethod.getParameterTypes();
        for (int i = 0; i < args.length; ++i) {
            parmString.append(args[i].getName());
            if (i + 1 >= args.length) continue;
            parmString.append(",");
        }
        parmString.append(")");
        IProgramElement methodElem = null;
        if (affectedMethod.getName().startsWith("<init>")) {
            methodElem = hierarchy.findElementForSignature(typeElem, IProgramElement.Kind.CONSTRUCTOR, type + parmString);
            if (methodElem == null && args.length == 0) {
                methodElem = typeElem;
            }
        } else {
            methodElem = hierarchy.findElementForSignature(typeElem, IProgramElement.Kind.METHOD, affectedMethod.getName() + parmString);
        }
        if (methodElem == null) {
            return;
        }
        try {
            String targetHandle = methodElem.getHandleIdentifier();
            if (targetHandle == null) {
                return;
            }
            IProgramElement sourceNode = hierarchy.findElementForSourceLine(sourceLocation);
            String sourceHandle = sourceNode.getHandleIdentifier();
            if (sourceHandle == null) {
                return;
            }
            IRelationshipMap mapper = model.getRelationshipMap();
            IRelationship foreward = mapper.get(sourceHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATES, false, true);
            foreward.addTarget(targetHandle);
            IRelationship back = mapper.get(targetHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATED_BY, false, true);
            back.addTarget(sourceHandle);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void addDeclareAnnotationFieldRelationship(AsmManager model, ISourceLocation declareLocation, String affectedTypeName, ResolvedMember affectedFieldName, boolean isRemove) {
        IHierarchy hierarchy;
        IProgramElement typeElem;
        if (model == null) {
            return;
        }
        String pkg = null;
        String type = affectedTypeName;
        int packageSeparator = affectedTypeName.lastIndexOf(".");
        if (packageSeparator != -1) {
            pkg = affectedTypeName.substring(0, packageSeparator);
            type = affectedTypeName.substring(packageSeparator + 1);
        }
        if ((typeElem = (hierarchy = model.getHierarchy()).findElementForType(pkg, type)) == null) {
            return;
        }
        IProgramElement fieldElem = hierarchy.findElementForSignature(typeElem, IProgramElement.Kind.FIELD, affectedFieldName.getName());
        if (fieldElem == null) {
            return;
        }
        String targetHandle = fieldElem.getHandleIdentifier();
        if (targetHandle == null) {
            return;
        }
        IProgramElement sourceNode = hierarchy.findElementForSourceLine(declareLocation);
        String sourceHandle = sourceNode.getHandleIdentifier();
        if (sourceHandle == null) {
            return;
        }
        IRelationshipMap relmap = model.getRelationshipMap();
        IRelationship foreward = relmap.get(sourceHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATES, false, true);
        foreward.addTarget(targetHandle);
        IRelationship back = relmap.get(targetHandle, IRelationship.Kind.DECLARE_INTER_TYPE, ANNOTATED_BY, false, true);
        back.addTarget(sourceHandle);
    }
}

