/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.aspectj.asm.AsmManager;
import org.aspectj.asm.IProgramElement;
import org.aspectj.asm.internal.AspectJElementHierarchy;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.bridge.MessageUtil;
import org.aspectj.bridge.SourceLocation;
import org.aspectj.bridge.WeaveMessage;
import org.aspectj.bridge.context.CompilationAndWeavingContext;
import org.aspectj.bridge.context.ContextToken;
import org.aspectj.util.FileUtil;
import org.aspectj.util.FuzzyBoolean;
import org.aspectj.weaver.Advice;
import org.aspectj.weaver.AdviceKind;
import org.aspectj.weaver.AnnotationAJ;
import org.aspectj.weaver.AnnotationOnTypeMunger;
import org.aspectj.weaver.BCException;
import org.aspectj.weaver.CompressingDataOutputStream;
import org.aspectj.weaver.ConcreteTypeMunger;
import org.aspectj.weaver.CrosscuttingMembersSet;
import org.aspectj.weaver.CustomMungerFactory;
import org.aspectj.weaver.IClassFileProvider;
import org.aspectj.weaver.IUnwovenClassFile;
import org.aspectj.weaver.IWeaveRequestor;
import org.aspectj.weaver.NewParentTypeMunger;
import org.aspectj.weaver.ReferenceType;
import org.aspectj.weaver.ReferenceTypeDelegate;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.Shadow;
import org.aspectj.weaver.ShadowMunger;
import org.aspectj.weaver.UnresolvedType;
import org.aspectj.weaver.WeaverMessages;
import org.aspectj.weaver.WeaverStateInfo;
import org.aspectj.weaver.World;
import org.aspectj.weaver.bcel.BcelAdvice;
import org.aspectj.weaver.bcel.BcelClassWeaver;
import org.aspectj.weaver.bcel.BcelMethod;
import org.aspectj.weaver.bcel.BcelObjectType;
import org.aspectj.weaver.bcel.BcelPerClauseAspectAdder;
import org.aspectj.weaver.bcel.BcelTypeMunger;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.LazyClassGen;
import org.aspectj.weaver.bcel.UnwovenClassFile;
import org.aspectj.weaver.bcel.Utility;
import org.aspectj.weaver.model.AsmRelationshipProvider;
import org.aspectj.weaver.patterns.AndPointcut;
import org.aspectj.weaver.patterns.BindingPattern;
import org.aspectj.weaver.patterns.BindingTypePattern;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.aspectj.weaver.patterns.DeclareAnnotation;
import org.aspectj.weaver.patterns.DeclareParents;
import org.aspectj.weaver.patterns.DeclareTypeErrorOrWarning;
import org.aspectj.weaver.patterns.FastMatchInfo;
import org.aspectj.weaver.patterns.IfPointcut;
import org.aspectj.weaver.patterns.KindedPointcut;
import org.aspectj.weaver.patterns.NameBindingPointcut;
import org.aspectj.weaver.patterns.NotPointcut;
import org.aspectj.weaver.patterns.OrPointcut;
import org.aspectj.weaver.patterns.Pointcut;
import org.aspectj.weaver.patterns.PointcutRewriter;
import org.aspectj.weaver.patterns.WithinPointcut;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class BcelWeaver {
    public static final String CLOSURE_CLASS_PREFIX = "$Ajc";
    public static final String SYNTHETIC_CLASS_POSTFIX = "$ajc";
    private static Trace trace = TraceFactory.getTraceFactory().getTrace(BcelWeaver.class);
    private final transient BcelWorld world;
    private final CrosscuttingMembersSet xcutSet;
    private boolean inReweavableMode = false;
    private transient List<UnwovenClassFile> addedClasses = new ArrayList<UnwovenClassFile>();
    private transient List<String> deletedTypenames = new ArrayList<String>();
    private transient List<ShadowMunger> shadowMungerList = null;
    private transient List<ConcreteTypeMunger> typeMungerList = null;
    private transient List<ConcreteTypeMunger> lateTypeMungerList = null;
    private transient List<DeclareParents> declareParentsList = null;
    private Manifest manifest = null;
    private boolean needToReweaveWorld = false;
    private boolean isBatchWeave = true;
    private ZipOutputStream zipOutputStream;
    private CustomMungerFactory customMungerFactory;
    private Set<IProgramElement> candidatesForRemoval = null;

    public BcelWeaver(BcelWorld world) {
        if (trace.isTraceEnabled()) {
            trace.enter("<init>", (Object)this, world);
        }
        this.world = world;
        this.xcutSet = world.getCrosscuttingMembersSet();
        if (trace.isTraceEnabled()) {
            trace.exit("<init>");
        }
    }

    public ResolvedType addLibraryAspect(String aspectName) {
        Message message;
        if (trace.isTraceEnabled()) {
            trace.enter("addLibraryAspect", (Object)this, aspectName);
        }
        UnresolvedType unresolvedT = UnresolvedType.forName(aspectName);
        unresolvedT.setNeedsModifiableDelegate(true);
        ResolvedType type = this.world.resolve(unresolvedT, true);
        if (type.isMissing()) {
            String fixedName = aspectName;
            int hasDot = fixedName.lastIndexOf(46);
            while (hasDot > 0) {
                char[] fixedNameChars = fixedName.toCharArray();
                fixedNameChars[hasDot] = 36;
                fixedName = new String(fixedNameChars);
                hasDot = fixedName.lastIndexOf(46);
                UnresolvedType ut = UnresolvedType.forName(fixedName);
                ut.setNeedsModifiableDelegate(true);
                type = this.world.resolve(ut, true);
                if (type.isMissing()) continue;
                break;
            }
        }
        if (type.isAspect()) {
            WeaverStateInfo wsi = type.getWeaverState();
            if (wsi != null && wsi.isReweavable()) {
                BcelObjectType classType = this.getClassType(type.getName());
                JavaClass wovenJavaClass = classType.getJavaClass();
                byte[] bytes = wsi.getUnwovenClassFileData(wovenJavaClass.getBytes());
                JavaClass unwovenJavaClass = Utility.makeJavaClass(wovenJavaClass.getFileName(), bytes);
                this.world.storeClass(unwovenJavaClass);
                classType.setJavaClass(unwovenJavaClass, true);
            }
            this.xcutSet.addOrReplaceAspect(type);
            if (trace.isTraceEnabled()) {
                trace.exit("addLibraryAspect", type);
            }
            if (type.getSuperclass().isAspect()) {
                this.addLibraryAspect(type.getSuperclass().getName());
            }
            return type;
        }
        if (type.isMissing()) {
            message = new Message("The specified aspect '" + aspectName + "' cannot be found", null, true);
            this.world.getMessageHandler().handleMessage(message);
        } else {
            message = new Message("Cannot register '" + aspectName + "' because the type found with that name is not an aspect", null, true);
            this.world.getMessageHandler().handleMessage(message);
        }
        return null;
    }

    public void addLibraryJarFile(File inFile) throws IOException {
        List<ResolvedType> addedAspects = null;
        addedAspects = inFile.isDirectory() ? this.addAspectsFromDirectory(inFile) : this.addAspectsFromJarFile(inFile);
        for (ResolvedType addedAspect : addedAspects) {
            this.xcutSet.addOrReplaceAspect(addedAspect);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<ResolvedType> addAspectsFromJarFile(File inFile) throws FileNotFoundException, IOException {
        ArrayList<ResolvedType> addedAspects = new ArrayList<ResolvedType>();
        try (ZipInputStream inStream = new ZipInputStream(new FileInputStream(inFile));){
            ZipEntry entry;
            while ((entry = inStream.getNextEntry()) != null) {
                if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
                int size = (int)entry.getSize();
                ClassParser parser = new ClassParser(new ByteArrayInputStream(FileUtil.readAsByteArray(inStream)), entry.getName());
                JavaClass jc = parser.parse();
                inStream.closeEntry();
                ReferenceType type = this.world.addSourceObjectType(jc, false).getResolvedTypeX();
                type.setBinaryPath(inFile.getAbsolutePath());
                if (((ResolvedType)type).isAspect()) {
                    addedAspects.add(type);
                    continue;
                }
                this.world.demote(type);
            }
        }
        return addedAspects;
    }

    private List<ResolvedType> addAspectsFromDirectory(File directory) throws FileNotFoundException, IOException {
        File[] classFiles;
        ArrayList<ResolvedType> addedAspects = new ArrayList<ResolvedType>();
        for (File classFile : classFiles = FileUtil.listFiles(directory, new FileFilter(){

            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".class");
            }
        })) {
            FileInputStream fis = new FileInputStream(classFile);
            byte[] classBytes = FileUtil.readAsByteArray(fis);
            ResolvedType aspectType = this.isAspect(classBytes, classFile.getAbsolutePath(), directory);
            if (aspectType != null) {
                addedAspects.add(aspectType);
            }
            fis.close();
        }
        return addedAspects;
    }

    private ResolvedType isAspect(byte[] classbytes, String name, File dir) throws IOException {
        ClassParser parser = new ClassParser(new ByteArrayInputStream(classbytes), name);
        JavaClass jc = parser.parse();
        ReferenceType type = this.world.addSourceObjectType(jc, false).getResolvedTypeX();
        String typeName = type.getName().replace('.', File.separatorChar);
        int end = name.lastIndexOf(typeName + ".class");
        String binaryPath = null;
        binaryPath = end == -1 ? dir.getAbsolutePath() : name.substring(0, end - 1);
        type.setBinaryPath(binaryPath);
        if (((ResolvedType)type).isAspect()) {
            return type;
        }
        this.world.demote(type);
        return null;
    }

    public List<UnwovenClassFile> addDirectoryContents(File inFile, File outDir) throws IOException {
        ArrayList<UnwovenClassFile> addedClassFiles = new ArrayList<UnwovenClassFile>();
        File[] files = FileUtil.listFiles(inFile, new FileFilter(){

            @Override
            public boolean accept(File f) {
                boolean accept = !f.isDirectory();
                return accept;
            }
        });
        for (int i = 0; i < files.length; ++i) {
            addedClassFiles.add(this.addClassFile(files[i], inFile, outDir));
        }
        return addedClassFiles;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<UnwovenClassFile> addJarFile(File inFile, File outDir, boolean canBeDirectory) {
        ArrayList<UnwovenClassFile> addedClassFiles;
        block22: {
            Message message;
            addedClassFiles = new ArrayList<UnwovenClassFile>();
            this.needToReweaveWorld = true;
            ZipFile inJar = null;
            try {
                if (inFile.isDirectory() && canBeDirectory) {
                    addedClassFiles.addAll(this.addDirectoryContents(inFile, outDir));
                    break block22;
                }
                inJar = new JarFile(inFile);
                try {
                    this.addManifest(((JarFile)inJar).getManifest());
                    Enumeration<JarEntry> entries = ((JarFile)inJar).entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        InputStream inStream = ((JarFile)inJar).getInputStream(entry);
                        byte[] bytes = FileUtil.readAsByteArray(inStream);
                        String filename = entry.getName();
                        UnwovenClassFile classFile = new UnwovenClassFile(new File(outDir, filename).getAbsolutePath(), bytes);
                        if (filename.endsWith(".class")) {
                            ReferenceType type = this.addClassFile(classFile, false);
                            StringBuffer sb = new StringBuffer();
                            sb.append(inFile.getAbsolutePath());
                            sb.append("!");
                            sb.append(entry.getName());
                            type.setBinaryPath(sb.toString());
                            addedClassFiles.add(classFile);
                        }
                        inStream.close();
                    }
                }
                finally {
                    inJar.close();
                }
                inJar.close();
            }
            catch (FileNotFoundException ex) {
                message = new Message("Could not find input jar file " + inFile.getPath() + ", ignoring", new SourceLocation(inFile, 0), false);
                this.world.getMessageHandler().handleMessage(message);
            }
            catch (IOException ex) {
                Message message2 = new Message("Could not read input jar file " + inFile.getPath() + "(" + ex.getMessage() + ")", new SourceLocation(inFile, 0), true);
                this.world.getMessageHandler().handleMessage(message2);
            }
            finally {
                if (inJar != null) {
                    try {
                        inJar.close();
                    }
                    catch (IOException ex) {
                        message = new Message("Could not close input jar file " + inFile.getPath() + "(" + ex.getMessage() + ")", new SourceLocation(inFile, 0), true);
                        this.world.getMessageHandler().handleMessage(message);
                    }
                }
            }
        }
        return addedClassFiles;
    }

    public boolean needToReweaveWorld() {
        return this.needToReweaveWorld;
    }

    public ReferenceType addClassFile(UnwovenClassFile classFile, boolean fromInpath) {
        this.addedClasses.add(classFile);
        ReferenceType type = this.world.addSourceObjectType(classFile.getJavaClass(), false).getResolvedTypeX();
        if (fromInpath) {
            type.setBinaryPath(classFile.getFilename());
        }
        return type;
    }

    public UnwovenClassFile addClassFile(File classFile, File inPathDir, File outDir) throws IOException {
        FileInputStream fis = new FileInputStream(classFile);
        byte[] bytes = FileUtil.readAsByteArray(fis);
        String filename = classFile.getAbsolutePath().substring(inPathDir.getAbsolutePath().length() + 1);
        UnwovenClassFile ucf = new UnwovenClassFile(new File(outDir, filename).getAbsolutePath(), bytes);
        if (filename.endsWith(".class")) {
            StringBuffer sb = new StringBuffer();
            sb.append(inPathDir.getAbsolutePath());
            sb.append("!");
            sb.append(filename);
            ReferenceType type = this.addClassFile(ucf, false);
            type.setBinaryPath(sb.toString());
        }
        fis.close();
        return ucf;
    }

    public void deleteClassFile(String typename) {
        this.deletedTypenames.add(typename);
        this.world.deleteSourceObjectType(UnresolvedType.forName(typename));
    }

    public void setIsBatchWeave(boolean b) {
        this.isBatchWeave = b;
    }

    public void prepareForWeave() {
        if (trace.isTraceEnabled()) {
            trace.enter("prepareForWeave", this);
        }
        this.needToReweaveWorld = this.xcutSet.hasChangedSinceLastReset();
        for (UnwovenClassFile jc : this.addedClasses) {
            String name = jc.getClassName();
            ResolvedType type = this.world.resolve(name);
            if (!type.isAspect()) continue;
            this.needToReweaveWorld |= this.xcutSet.addOrReplaceAspect(type);
        }
        for (String name : this.deletedTypenames) {
            if (!this.xcutSet.deleteAspect(UnresolvedType.forName(name))) continue;
            this.needToReweaveWorld = true;
        }
        this.shadowMungerList = this.xcutSet.getShadowMungers();
        this.rewritePointcuts(this.shadowMungerList);
        this.typeMungerList = this.xcutSet.getTypeMungers();
        this.lateTypeMungerList = this.xcutSet.getLateTypeMungers();
        this.declareParentsList = this.xcutSet.getDeclareParents();
        this.addCustomMungers();
        Collections.sort(this.shadowMungerList, new Comparator<ShadowMunger>(){

            @Override
            public int compare(ShadowMunger sm1, ShadowMunger sm2) {
                if (sm1.getSourceLocation() == null) {
                    return sm2.getSourceLocation() == null ? 0 : 1;
                }
                if (sm2.getSourceLocation() == null) {
                    return -1;
                }
                return sm2.getSourceLocation().getOffset() - sm1.getSourceLocation().getOffset();
            }
        });
        if (this.inReweavableMode) {
            this.world.showMessage(IMessage.INFO, WeaverMessages.format("reweavableMode"), null, null);
        }
        if (trace.isTraceEnabled()) {
            trace.exit("prepareForWeave");
        }
    }

    private void addCustomMungers() {
        if (this.customMungerFactory != null) {
            for (UnwovenClassFile jc : this.addedClasses) {
                Collection<ConcreteTypeMunger> typeMungers;
                String name = jc.getClassName();
                ResolvedType type = this.world.resolve(name);
                if (!type.isAspect()) continue;
                Collection<ShadowMunger> shadowMungers = this.customMungerFactory.createCustomShadowMungers(type);
                if (shadowMungers != null) {
                    this.shadowMungerList.addAll(shadowMungers);
                }
                if ((typeMungers = this.customMungerFactory.createCustomTypeMungers(type)) == null) continue;
                this.typeMungerList.addAll(typeMungers);
            }
        }
    }

    public void setCustomMungerFactory(CustomMungerFactory factory) {
        this.customMungerFactory = factory;
    }

    private void rewritePointcuts(List<ShadowMunger> shadowMungers) {
        PointcutRewriter rewriter = new PointcutRewriter();
        for (ShadowMunger munger : shadowMungers) {
            Advice advice;
            Pointcut p = munger.getPointcut();
            Pointcut newP = rewriter.rewrite(p);
            if (munger instanceof Advice && (advice = (Advice)munger).getSignature() != null) {
                String[] names;
                int numFormals;
                if (advice.getConcreteAspect().isAnnotationStyleAspect() && advice.getDeclaringAspect() != null && advice.getDeclaringAspect().resolve(this.world).isAnnotationStyleAspect() || advice.isAnnotationStyle()) {
                    numFormals = advice.getBaseParameterCount();
                    int numArgs = advice.getSignature().getParameterTypes().length;
                    if (numFormals > 0) {
                        names = advice.getSignature().getParameterNames(this.world);
                        this.validateBindings(newP, p, numArgs, names);
                    }
                } else {
                    numFormals = advice.getBaseParameterCount();
                    if (numFormals > 0) {
                        names = advice.getBaseParameterNames(this.world);
                        this.validateBindings(newP, p, numFormals, names);
                    }
                }
            }
            newP.m_ignoreUnboundBindingForNames = p.m_ignoreUnboundBindingForNames;
            munger.setPointcut(newP);
        }
        HashMap<Pointcut, Pointcut> pcMap = new HashMap<Pointcut, Pointcut>();
        for (ShadowMunger munger : shadowMungers) {
            Pointcut p = munger.getPointcut();
            Pointcut newP = this.shareEntriesFromMap(p, pcMap);
            newP.m_ignoreUnboundBindingForNames = p.m_ignoreUnboundBindingForNames;
            munger.setPointcut(newP);
        }
    }

    private Pointcut shareEntriesFromMap(Pointcut p, Map<Pointcut, Pointcut> pcMap) {
        if (p instanceof NameBindingPointcut) {
            return p;
        }
        if (p instanceof IfPointcut) {
            return p;
        }
        if (p instanceof ConcreteCflowPointcut) {
            return p;
        }
        if (p instanceof AndPointcut) {
            AndPointcut apc = (AndPointcut)p;
            Pointcut left = this.shareEntriesFromMap(apc.getLeft(), pcMap);
            Pointcut right = this.shareEntriesFromMap(apc.getRight(), pcMap);
            return new AndPointcut(left, right);
        }
        if (p instanceof OrPointcut) {
            OrPointcut opc = (OrPointcut)p;
            Pointcut left = this.shareEntriesFromMap(opc.getLeft(), pcMap);
            Pointcut right = this.shareEntriesFromMap(opc.getRight(), pcMap);
            return new OrPointcut(left, right);
        }
        if (p instanceof NotPointcut) {
            NotPointcut npc = (NotPointcut)p;
            Pointcut not = this.shareEntriesFromMap(npc.getNegatedPointcut(), pcMap);
            return new NotPointcut(not);
        }
        if (pcMap.containsKey(p)) {
            return pcMap.get(p);
        }
        pcMap.put(p, p);
        return p;
    }

    private void validateBindings(Pointcut dnfPointcut, Pointcut userPointcut, int numFormals, String[] names) {
        if (numFormals == 0) {
            return;
        }
        if (dnfPointcut.couldMatchKinds() == Shadow.NO_SHADOW_KINDS_BITS) {
            return;
        }
        if (dnfPointcut instanceof OrPointcut) {
            OrPointcut orBasedDNFPointcut = (OrPointcut)dnfPointcut;
            Pointcut[] leftBindings = new Pointcut[numFormals];
            Pointcut[] rightBindings = new Pointcut[numFormals];
            this.validateOrBranch(orBasedDNFPointcut, userPointcut, numFormals, names, leftBindings, rightBindings);
        } else {
            Pointcut[] bindings = new Pointcut[numFormals];
            this.validateSingleBranch(dnfPointcut, userPointcut, numFormals, names, bindings);
        }
    }

    private void validateOrBranch(OrPointcut pc, Pointcut userPointcut, int numFormals, String[] names, Pointcut[] leftBindings, Pointcut[] rightBindings) {
        Pointcut left = pc.getLeft();
        Pointcut right = pc.getRight();
        if (left instanceof OrPointcut) {
            Pointcut[] newRightBindings = new Pointcut[numFormals];
            this.validateOrBranch((OrPointcut)left, userPointcut, numFormals, names, leftBindings, newRightBindings);
        } else if (left.couldMatchKinds() != Shadow.NO_SHADOW_KINDS_BITS) {
            this.validateSingleBranch(left, userPointcut, numFormals, names, leftBindings);
        }
        if (right instanceof OrPointcut) {
            Pointcut[] newLeftBindings = new Pointcut[numFormals];
            this.validateOrBranch((OrPointcut)right, userPointcut, numFormals, names, newLeftBindings, rightBindings);
        } else if (right.couldMatchKinds() != Shadow.NO_SHADOW_KINDS_BITS) {
            this.validateSingleBranch(right, userPointcut, numFormals, names, rightBindings);
        }
        int kindsInCommon = left.couldMatchKinds() & right.couldMatchKinds();
        if (kindsInCommon != Shadow.NO_SHADOW_KINDS_BITS && this.couldEverMatchSameJoinPoints(left, right)) {
            ArrayList<String> ambiguousNames = new ArrayList<String>();
            for (int i = 0; i < numFormals; ++i) {
                if (leftBindings[i] == null) {
                    if (rightBindings[i] == null) continue;
                    ambiguousNames.add(names[i]);
                    continue;
                }
                if (leftBindings[i].equals(rightBindings[i])) continue;
                ambiguousNames.add(names[i]);
            }
            if (!ambiguousNames.isEmpty()) {
                this.raiseAmbiguityInDisjunctionError(userPointcut, ambiguousNames);
            }
        }
    }

    private void validateSingleBranch(Pointcut pc, Pointcut userPointcut, int numFormals, String[] names, Pointcut[] bindings) {
        int i;
        boolean[] foundFormals = new boolean[numFormals];
        for (i = 0; i < foundFormals.length; ++i) {
            foundFormals[i] = false;
        }
        this.validateSingleBranchRecursion(pc, userPointcut, foundFormals, names, bindings);
        for (i = 0; i < foundFormals.length; ++i) {
            if (foundFormals[i]) continue;
            boolean ignore = false;
            for (int j = 0; j < userPointcut.m_ignoreUnboundBindingForNames.length; ++j) {
                if (names[i] == null || !names[i].equals(userPointcut.m_ignoreUnboundBindingForNames[j])) continue;
                ignore = true;
                break;
            }
            if (ignore) continue;
            this.raiseUnboundFormalError(names[i], userPointcut);
        }
    }

    /*
     * WARNING - void declaration
     */
    private void validateSingleBranchRecursion(Pointcut pc, Pointcut userPointcut, boolean[] foundFormals, String[] names, Pointcut[] bindings) {
        block13: {
            block12: {
                NameBindingPointcut nnbp;
                if (!(pc instanceof NotPointcut)) break block12;
                NotPointcut not = (NotPointcut)pc;
                if (!(not.getNegatedPointcut() instanceof NameBindingPointcut) || (nnbp = (NameBindingPointcut)not.getNegatedPointcut()).getBindingAnnotationTypePatterns().isEmpty() || nnbp.getBindingTypePatterns().isEmpty()) break block13;
                this.raiseNegationBindingError(userPointcut);
                break block13;
            }
            if (pc instanceof AndPointcut) {
                AndPointcut and = (AndPointcut)pc;
                this.validateSingleBranchRecursion(and.getLeft(), userPointcut, foundFormals, names, bindings);
                this.validateSingleBranchRecursion(and.getRight(), userPointcut, foundFormals, names, bindings);
            } else if (pc instanceof NameBindingPointcut) {
                List<BindingTypePattern> bindingTypePatterns = ((NameBindingPointcut)pc).getBindingTypePatterns();
                for (BindingTypePattern bindingTypePattern : bindingTypePatterns) {
                    int index = bindingTypePattern.getFormalIndex();
                    bindings[index] = pc;
                    if (foundFormals[index]) {
                        this.raiseAmbiguousBindingError(names[index], userPointcut);
                        continue;
                    }
                    foundFormals[index] = true;
                }
                List<BindingPattern> bindingAnnotationTypePatterns = ((NameBindingPointcut)pc).getBindingAnnotationTypePatterns();
                for (BindingPattern bindingAnnotationTypePattern : bindingAnnotationTypePatterns) {
                    int index = bindingAnnotationTypePattern.getFormalIndex();
                    bindings[index] = pc;
                    if (foundFormals[index]) {
                        this.raiseAmbiguousBindingError(names[index], userPointcut);
                        continue;
                    }
                    foundFormals[index] = true;
                }
            } else if (pc instanceof ConcreteCflowPointcut) {
                void var8_16;
                ConcreteCflowPointcut cfp = (ConcreteCflowPointcut)pc;
                int[] slots = cfp.getUsedFormalSlots();
                boolean bl = false;
                while (var8_16 < slots.length) {
                    bindings[slots[var8_16]] = cfp;
                    if (foundFormals[slots[var8_16]]) {
                        this.raiseAmbiguousBindingError(names[slots[var8_16]], userPointcut);
                    } else {
                        foundFormals[slots[var8_16]] = true;
                    }
                    ++var8_16;
                }
            }
        }
    }

    private boolean couldEverMatchSameJoinPoints(Pointcut left, Pointcut right) {
        if (left instanceof OrPointcut) {
            OrPointcut leftOrPointcut = (OrPointcut)left;
            if (this.couldEverMatchSameJoinPoints(leftOrPointcut.getLeft(), right)) {
                return true;
            }
            return this.couldEverMatchSameJoinPoints(leftOrPointcut.getRight(), right);
        }
        if (right instanceof OrPointcut) {
            OrPointcut rightOrPointcut = (OrPointcut)right;
            if (this.couldEverMatchSameJoinPoints(left, rightOrPointcut.getLeft())) {
                return true;
            }
            return this.couldEverMatchSameJoinPoints(left, rightOrPointcut.getRight());
        }
        WithinPointcut leftWithin = (WithinPointcut)this.findFirstPointcutIn(left, WithinPointcut.class);
        WithinPointcut rightWithin = (WithinPointcut)this.findFirstPointcutIn(right, WithinPointcut.class);
        if (leftWithin != null && rightWithin != null && !leftWithin.couldEverMatchSameJoinPointsAs(rightWithin)) {
            return false;
        }
        KindedPointcut leftKind = (KindedPointcut)this.findFirstPointcutIn(left, KindedPointcut.class);
        KindedPointcut rightKind = (KindedPointcut)this.findFirstPointcutIn(right, KindedPointcut.class);
        return leftKind == null || rightKind == null || leftKind.couldEverMatchSameJoinPointsAs(rightKind);
    }

    private Pointcut findFirstPointcutIn(Pointcut toSearch, Class toLookFor) {
        if (toSearch instanceof NotPointcut) {
            return null;
        }
        if (toLookFor.isInstance(toSearch)) {
            return toSearch;
        }
        if (toSearch instanceof AndPointcut) {
            AndPointcut apc = (AndPointcut)toSearch;
            Pointcut left = this.findFirstPointcutIn(apc.getLeft(), toLookFor);
            if (left != null) {
                return left;
            }
            return this.findFirstPointcutIn(apc.getRight(), toLookFor);
        }
        return null;
    }

    private void raiseNegationBindingError(Pointcut userPointcut) {
        this.world.showMessage(IMessage.ERROR, WeaverMessages.format("negationDoesntAllowBinding"), userPointcut.getSourceContext().makeSourceLocation(userPointcut), null);
    }

    private void raiseAmbiguousBindingError(String name, Pointcut pointcut) {
        this.world.showMessage(IMessage.ERROR, WeaverMessages.format("ambiguousBindingInPC", name), pointcut.getSourceContext().makeSourceLocation(pointcut), null);
    }

    private void raiseAmbiguityInDisjunctionError(Pointcut userPointcut, List<String> names) {
        StringBuffer formalNames = new StringBuffer(names.get(0).toString());
        for (int i = 1; i < names.size(); ++i) {
            formalNames.append(", ");
            formalNames.append(names.get(i));
        }
        this.world.showMessage(IMessage.ERROR, WeaverMessages.format("ambiguousBindingInOrPC", formalNames), userPointcut.getSourceContext().makeSourceLocation(userPointcut), null);
    }

    private void raiseUnboundFormalError(String name, Pointcut userPointcut) {
        this.world.showMessage(IMessage.ERROR, WeaverMessages.format("unboundFormalInPC", name), userPointcut.getSourceLocation(), null);
    }

    public void addManifest(Manifest newManifest) {
        if (this.manifest == null) {
            this.manifest = newManifest;
        }
    }

    public Manifest getManifest(boolean shouldCreate) {
        if (this.manifest == null && shouldCreate) {
            String WEAVER_MANIFEST_VERSION = "1.0";
            Attributes.Name CREATED_BY = new Attributes.Name("Created-By");
            String WEAVER_CREATED_BY = "AspectJ Compiler";
            this.manifest = new Manifest();
            Attributes attributes = this.manifest.getMainAttributes();
            attributes.put(Attributes.Name.MANIFEST_VERSION, WEAVER_MANIFEST_VERSION);
            attributes.put(CREATED_BY, WEAVER_CREATED_BY);
        }
        return this.manifest;
    }

    public Collection<String> weave(File file) throws IOException {
        BufferedOutputStream os = FileUtil.makeOutputStream(file);
        this.zipOutputStream = new ZipOutputStream(os);
        this.prepareForWeave();
        Collection<String> c = this.weave(new IClassFileProvider(){

            @Override
            public boolean isApplyAtAspectJMungersOnly() {
                return false;
            }

            @Override
            public Iterator<UnwovenClassFile> getClassFileIterator() {
                return BcelWeaver.this.addedClasses.iterator();
            }

            @Override
            public IWeaveRequestor getRequestor() {
                return new IWeaveRequestor(){

                    @Override
                    public void acceptResult(IUnwovenClassFile result) {
                        try {
                            BcelWeaver.this.writeZipEntry(result.getFilename(), result.getBytes());
                        }
                        catch (IOException iOException) {
                            // empty catch block
                        }
                    }

                    @Override
                    public void processingReweavableState() {
                    }

                    @Override
                    public void addingTypeMungers() {
                    }

                    @Override
                    public void weavingAspects() {
                    }

                    @Override
                    public void weavingClasses() {
                    }

                    @Override
                    public void weaveCompleted() {
                    }
                };
            }
        });
        this.zipOutputStream.close();
        return c;
    }

    public Collection<String> weave(IClassFileProvider input) throws IOException {
        String className;
        UnwovenClassFile classFile;
        Iterator<UnwovenClassFile> i;
        if (trace.isTraceEnabled()) {
            trace.enter("weave", (Object)this, input);
        }
        ContextToken weaveToken = CompilationAndWeavingContext.enteringPhase(22, "");
        ArrayList<String> wovenClassNames = new ArrayList<String>();
        IWeaveRequestor requestor = input.getRequestor();
        if (this.world.getModel() != null && this.world.isMinimalModel()) {
            this.candidatesForRemoval = new HashSet<IProgramElement>();
        }
        if (this.world.getModel() != null && !this.isBatchWeave) {
            AsmManager manager = this.world.getModelAsAsmManager();
            i = input.getClassFileIterator();
            while (i.hasNext()) {
                classFile = i.next();
                manager.removeRelationshipsTargettingThisType(classFile.getClassName());
            }
        }
        Iterator<UnwovenClassFile> i2 = input.getClassFileIterator();
        while (i2.hasNext()) {
            String className2;
            ResolvedType theType;
            UnwovenClassFile classFile2 = i2.next();
            if (!classFile2.shouldBeWoven() || (theType = this.world.resolve(className2 = classFile2.getClassName())) == null) continue;
            theType.ensureConsistent();
        }
        if (input.isApplyAtAspectJMungersOnly()) {
            ContextToken atAspectJMungersOnly = CompilationAndWeavingContext.enteringPhase(32, "");
            requestor.weavingAspects();
            CompilationAndWeavingContext.enteringPhase(25, "");
            i = input.getClassFileIterator();
            while (i.hasNext()) {
                ResolvedType theType;
                classFile = i.next();
                if (!classFile.shouldBeWoven() || !(theType = this.world.resolve(className = classFile.getClassName())).isAnnotationStyleAspect()) continue;
                BcelObjectType classType = BcelWorld.getBcelObjectType(theType);
                if (classType == null) {
                    throw new BCException("Can't find bcel delegate for " + className + " type=" + theType.getClass());
                }
                LazyClassGen clazz = classType.getLazyClassGen();
                BcelPerClauseAspectAdder selfMunger = new BcelPerClauseAspectAdder(theType, theType.getPerClause().getKind());
                selfMunger.forceMunge(clazz, true);
                classType.finishedWith();
                UnwovenClassFile[] newClasses = this.getClassFilesFor(clazz);
                for (int news = 0; news < newClasses.length; ++news) {
                    requestor.acceptResult(newClasses[news]);
                }
                wovenClassNames.add(classFile.getClassName());
            }
            requestor.weaveCompleted();
            CompilationAndWeavingContext.leavingPhase(atAspectJMungersOnly);
            return wovenClassNames;
        }
        requestor.processingReweavableState();
        ContextToken reweaveToken = CompilationAndWeavingContext.enteringPhase(23, "");
        this.prepareToProcessReweavableState();
        i = input.getClassFileIterator();
        while (i.hasNext()) {
            BcelObjectType classType;
            classFile = i.next();
            if (!classFile.shouldBeWoven() || (classType = this.getClassType(className = classFile.getClassName())) == null) continue;
            ContextToken tok = CompilationAndWeavingContext.enteringPhase(23, className);
            this.processReweavableStateIfPresent(className, classType);
            CompilationAndWeavingContext.leavingPhase(tok);
        }
        CompilationAndWeavingContext.leavingPhase(reweaveToken);
        ContextToken typeMungingToken = CompilationAndWeavingContext.enteringPhase(24, "");
        requestor.addingTypeMungers();
        ArrayList<String> typesToProcess = new ArrayList<String>();
        Iterator<UnwovenClassFile> iter = input.getClassFileIterator();
        while (iter.hasNext()) {
            UnwovenClassFile clf = iter.next();
            if (!clf.shouldBeWoven()) continue;
            typesToProcess.add(clf.getClassName());
        }
        while (typesToProcess.size() > 0) {
            this.weaveParentsFor(typesToProcess, (String)typesToProcess.get(0), null);
        }
        Iterator<UnwovenClassFile> i3 = input.getClassFileIterator();
        while (i3.hasNext()) {
            UnwovenClassFile classFile3 = i3.next();
            if (!classFile3.shouldBeWoven()) continue;
            String className3 = classFile3.getClassName();
            this.addNormalTypeMungers(className3);
        }
        CompilationAndWeavingContext.leavingPhase(typeMungingToken);
        requestor.weavingAspects();
        ContextToken aspectToken = CompilationAndWeavingContext.enteringPhase(25, "");
        Iterator<UnwovenClassFile> i4 = input.getClassFileIterator();
        while (i4.hasNext()) {
            String className4;
            ResolvedType theType;
            UnwovenClassFile classFile4 = i4.next();
            if (!classFile4.shouldBeWoven() || !(theType = this.world.resolve(className4 = classFile4.getClassName())).isAspect()) continue;
            BcelObjectType classType = BcelWorld.getBcelObjectType(theType);
            if (classType == null) {
                ReferenceTypeDelegate theDelegate = ((ReferenceType)theType).getDelegate();
                if (theDelegate.getClass().getName().endsWith("EclipseSourceType")) continue;
                throw new BCException("Can't find bcel delegate for " + className4 + " type=" + theType.getClass());
            }
            this.weaveAndNotify(classFile4, classType, requestor);
            wovenClassNames.add(className4);
        }
        CompilationAndWeavingContext.leavingPhase(aspectToken);
        requestor.weavingClasses();
        ContextToken classToken = CompilationAndWeavingContext.enteringPhase(26, "");
        Iterator<UnwovenClassFile> i5 = input.getClassFileIterator();
        while (i5.hasNext()) {
            String className5;
            ResolvedType theType;
            UnwovenClassFile classFile5 = i5.next();
            if (!classFile5.shouldBeWoven() || (theType = this.world.resolve(className5 = classFile5.getClassName())).isAspect()) continue;
            BcelObjectType classType = BcelWorld.getBcelObjectType(theType);
            if (classType == null) {
                ReferenceTypeDelegate theDelegate = ((ReferenceType)theType).getDelegate();
                if (theDelegate.getClass().getName().endsWith("EclipseSourceType")) continue;
                throw new BCException("Can't find bcel delegate for " + className5 + " type=" + theType.getClass());
            }
            this.weaveAndNotify(classFile5, classType, requestor);
            wovenClassNames.add(className5);
        }
        CompilationAndWeavingContext.leavingPhase(classToken);
        this.addedClasses.clear();
        this.deletedTypenames.clear();
        requestor.weaveCompleted();
        CompilationAndWeavingContext.leavingPhase(weaveToken);
        if (trace.isTraceEnabled()) {
            trace.exit("weave", wovenClassNames);
        }
        if (this.world.getModel() != null && this.world.isMinimalModel()) {
            this.candidatesForRemoval.clear();
        }
        return wovenClassNames;
    }

    public void allWeavingComplete() {
        this.warnOnUnmatchedAdvice();
    }

    private void warnOnUnmatchedAdvice() {
        if (this.world.isInJava5Mode() && this.world.getLint().adviceDidNotMatch.isEnabled()) {
            List<ShadowMunger> l = this.world.getCrosscuttingMembersSet().getShadowMungers();
            class AdviceLocation {
                private final int lineNo;
                private final UnresolvedType inAspect;

                public AdviceLocation(BcelAdvice advice) {
                    this.lineNo = advice.getSourceLocation().getLine();
                    this.inAspect = advice.getDeclaringAspect();
                }

                public boolean equals(Object obj) {
                    if (!(obj instanceof AdviceLocation)) {
                        return false;
                    }
                    AdviceLocation other = (AdviceLocation)obj;
                    if (this.lineNo != other.lineNo) {
                        return false;
                    }
                    return this.inAspect.equals(other.inAspect);
                }

                public int hashCode() {
                    return 37 + 17 * this.lineNo + 17 * this.inAspect.hashCode();
                }
            }
            HashSet<AdviceLocation> alreadyWarnedLocations = new HashSet<AdviceLocation>();
            for (ShadowMunger element : l) {
                AdviceLocation loc;
                BcelAdvice ba;
                if (!(element instanceof BcelAdvice) || (ba = (BcelAdvice)element).getKind() == AdviceKind.CflowEntry || ba.getKind() == AdviceKind.CflowBelowEntry || ba.hasMatchedSomething() || ba.getSignature() == null || alreadyWarnedLocations.contains(loc = new AdviceLocation(ba))) continue;
                alreadyWarnedLocations.add(loc);
                if (ba.getSignature() instanceof BcelMethod && Utility.isSuppressing(ba.getSignature(), "adviceDidNotMatch")) continue;
                this.world.getLint().adviceDidNotMatch.signal(ba.getDeclaringAspect().toString(), new SourceLocation(element.getSourceLocation().getSourceFile(), element.getSourceLocation().getLine()));
            }
        }
    }

    private void weaveParentsFor(List<String> typesForWeaving, String typeToWeave, ResolvedType resolvedTypeToWeave) {
        ResolvedType[] interfaceTypes;
        ResolvedType superclassType;
        String superclassTypename;
        if (resolvedTypeToWeave == null) {
            resolvedTypeToWeave = this.world.resolve(typeToWeave);
        }
        String string = superclassTypename = (superclassType = resolvedTypeToWeave.getSuperclass()) == null ? null : superclassType.getName();
        if (superclassType != null && !superclassType.isTypeHierarchyComplete() && superclassType.isExposedToWeaver() && typesForWeaving.contains(superclassTypename)) {
            this.weaveParentsFor(typesForWeaving, superclassTypename, superclassType);
        }
        for (ResolvedType resolvedSuperInterface : interfaceTypes = resolvedTypeToWeave.getDeclaredInterfaces()) {
            if (resolvedSuperInterface.isTypeHierarchyComplete()) continue;
            String interfaceTypename = resolvedSuperInterface.getName();
            if (!resolvedSuperInterface.isExposedToWeaver()) continue;
            this.weaveParentsFor(typesForWeaving, interfaceTypename, resolvedSuperInterface);
        }
        ContextToken tok = CompilationAndWeavingContext.enteringPhase(7, resolvedTypeToWeave.getName());
        if (!resolvedTypeToWeave.isTypeHierarchyComplete()) {
            this.weaveParentTypeMungers(resolvedTypeToWeave);
        }
        CompilationAndWeavingContext.leavingPhase(tok);
        typesForWeaving.remove(typeToWeave);
        resolvedTypeToWeave.tagAsTypeHierarchyComplete();
    }

    public void prepareToProcessReweavableState() {
    }

    public void processReweavableStateIfPresent(String className, BcelObjectType classType) {
        WeaverStateInfo wsi = classType.getWeaverState();
        if (wsi != null && wsi.isReweavable()) {
            this.world.showMessage(IMessage.INFO, WeaverMessages.format("processingReweavable", className, classType.getSourceLocation().getSourceFile()), null, null);
            Set<String> aspectsPreviouslyInWorld = wsi.getAspectsAffectingType();
            HashSet<String> alreadyConfirmedReweavableState = new HashSet<String>();
            for (String requiredTypeSignature : aspectsPreviouslyInWorld) {
                boolean exists;
                if (alreadyConfirmedReweavableState.contains(requiredTypeSignature)) continue;
                ResolvedType rtx = this.world.resolve(UnresolvedType.forSignature(requiredTypeSignature), true);
                boolean bl = exists = !rtx.isMissing();
                if (this.world.isOverWeaving()) continue;
                if (!exists) {
                    this.world.getLint().missingAspectForReweaving.signal(new String[]{rtx.getName(), className}, classType.getSourceLocation(), null);
                    continue;
                }
                if (!this.xcutSet.containsAspect(rtx)) {
                    this.world.showMessage(IMessage.ERROR, WeaverMessages.format("reweavableAspectNotRegistered", rtx.getName(), className), null, null);
                } else if (!this.world.getMessageHandler().isIgnoring(IMessage.INFO)) {
                    this.world.showMessage(IMessage.INFO, WeaverMessages.format("verifiedReweavableType", rtx.getName(), rtx.getSourceLocation().getSourceFile()), null, null);
                }
                alreadyConfirmedReweavableState.add(requiredTypeSignature);
            }
            if (!this.world.isOverWeaving()) {
                byte[] ucfd = wsi.getUnwovenClassFileData();
                if (ucfd.length == 0) {
                    this.world.getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("mustKeepOverweavingOnceStart", className)));
                } else {
                    byte[] bytes = wsi.getUnwovenClassFileData(classType.getJavaClass().getBytes());
                    JavaClass newJavaClass = Utility.makeJavaClass(classType.getJavaClass().getFileName(), bytes);
                    classType.setJavaClass(newJavaClass, true);
                    classType.getResolvedTypeX().ensureConsistent();
                }
            }
        }
    }

    private void weaveAndNotify(UnwovenClassFile classFile, BcelObjectType classType, IWeaveRequestor requestor) throws IOException {
        trace.enter("weaveAndNotify", (Object)this, new Object[]{classFile, classType, requestor});
        ContextToken tok = CompilationAndWeavingContext.enteringPhase(27, classType.getResolvedTypeX().getName());
        LazyClassGen clazz = this.weaveWithoutDump(classFile, classType);
        classType.finishedWith();
        if (clazz != null) {
            UnwovenClassFile[] newClasses = this.getClassFilesFor(clazz);
            if (newClasses[0].getClassName().equals(classFile.getClassName())) {
                newClasses[0].setClassNameAsChars(classFile.getClassNameAsChars());
            }
            for (int i = 0; i < newClasses.length; ++i) {
                requestor.acceptResult(newClasses[i]);
            }
        } else {
            requestor.acceptResult(classFile);
        }
        classType.weavingCompleted();
        CompilationAndWeavingContext.leavingPhase(tok);
        trace.exit("weaveAndNotify");
    }

    public BcelObjectType getClassType(String forClass) {
        return BcelWorld.getBcelObjectType(this.world.resolve(forClass));
    }

    public void addParentTypeMungers(String typeName) {
        this.weaveParentTypeMungers(this.world.resolve(typeName));
    }

    public void addNormalTypeMungers(String typeName) {
        this.weaveNormalTypeMungers(this.world.resolve(typeName));
    }

    public UnwovenClassFile[] getClassFilesFor(LazyClassGen clazz) {
        List<UnwovenClassFile.ChildClass> childClasses = clazz.getChildClasses(this.world);
        UnwovenClassFile[] ret = new UnwovenClassFile[1 + childClasses.size()];
        ret[0] = new UnwovenClassFile(clazz.getFileName(), clazz.getClassName(), clazz.getJavaClassBytesIncludingReweavable(this.world));
        int index = 1;
        for (UnwovenClassFile.ChildClass element : childClasses) {
            UnwovenClassFile childClass = new UnwovenClassFile(clazz.getFileName() + "$" + element.name, element.bytes);
            ret[index++] = childClass;
        }
        return ret;
    }

    public void weaveParentTypeMungers(ResolvedType onType) {
        boolean typeChanged;
        if (onType.isRawType() || onType.isParameterizedType()) {
            onType = onType.getGenericType();
        }
        onType.clearInterTypeMungers();
        ArrayList<DeclareParents> decpToRepeat = new ArrayList<DeclareParents>();
        boolean aParentChangeOccurred = false;
        boolean anAnnotationChangeOccurred = false;
        for (DeclareParents decp : this.declareParentsList) {
            typeChanged = this.applyDeclareParents(decp, onType);
            if (typeChanged) {
                aParentChangeOccurred = true;
                continue;
            }
            decpToRepeat.add(decp);
        }
        for (DeclareAnnotation decA : this.xcutSet.getDeclareAnnotationOnTypes()) {
            typeChanged = this.applyDeclareAtType(decA, onType, true);
            if (!typeChanged) continue;
            anAnnotationChangeOccurred = true;
        }
        while ((aParentChangeOccurred || anAnnotationChangeOccurred) && !decpToRepeat.isEmpty()) {
            boolean typeChanged2;
            aParentChangeOccurred = false;
            anAnnotationChangeOccurred = false;
            ArrayList<DeclareParents> decpToRepeatNextTime = new ArrayList<DeclareParents>();
            for (DeclareParents decp : decpToRepeat) {
                typeChanged2 = this.applyDeclareParents(decp, onType);
                if (typeChanged2) {
                    aParentChangeOccurred = true;
                    continue;
                }
                decpToRepeatNextTime.add(decp);
            }
            for (DeclareAnnotation decA : this.xcutSet.getDeclareAnnotationOnTypes()) {
                typeChanged2 = this.applyDeclareAtType(decA, onType, false);
                if (!typeChanged2) continue;
                anAnnotationChangeOccurred = true;
            }
            decpToRepeat = decpToRepeatNextTime;
        }
    }

    private boolean applyDeclareAtType(DeclareAnnotation decA, ResolvedType onType, boolean reportProblems) {
        boolean didSomething = false;
        if (decA.matches(onType)) {
            AnnotationAJ theAnnotation = decA.getAnnotation();
            if (theAnnotation == null) {
                return false;
            }
            if (onType.hasAnnotation(theAnnotation.getType())) {
                return false;
            }
            AnnotationAJ annoX = decA.getAnnotation();
            boolean problemReported = this.verifyTargetIsOK(decA, onType, annoX, reportProblems);
            if (!problemReported) {
                AsmRelationshipProvider.addDeclareAnnotationRelationship(this.world.getModelAsAsmManager(), decA.getSourceLocation(), onType.getSourceLocation(), false);
                if (!this.getWorld().getMessageHandler().isIgnoring(IMessage.WEAVEINFO)) {
                    this.getWorld().getMessageHandler().handleMessage(WeaveMessage.constructWeavingMessage(WeaveMessage.WEAVEMESSAGE_ANNOTATES, new String[]{onType.toString(), Utility.beautifyLocation(onType.getSourceLocation()), decA.getAnnotationString(), "type", decA.getAspect().toString(), Utility.beautifyLocation(decA.getSourceLocation())}));
                }
                didSomething = true;
                AnnotationOnTypeMunger newAnnotationTM = new AnnotationOnTypeMunger(annoX);
                newAnnotationTM.setSourceLocation(decA.getSourceLocation());
                onType.addInterTypeMunger(new BcelTypeMunger(newAnnotationTM, decA.getAspect().resolve(this.world)), false);
                decA.copyAnnotationTo(onType);
            }
        }
        return didSomething;
    }

    private boolean verifyTargetIsOK(DeclareAnnotation decA, ResolvedType onType, AnnotationAJ annoX, boolean outputProblems) {
        boolean problemReported = false;
        if (annoX.specifiesTarget() && (onType.isAnnotation() && !annoX.allowedOnAnnotationType() || !annoX.allowedOnRegularType())) {
            if (outputProblems) {
                if (decA.isExactPattern()) {
                    this.world.getMessageHandler().handleMessage(MessageUtil.error(WeaverMessages.format("incorrectTargetForDeclareAnnotation", onType.getName(), annoX.getTypeName(), annoX.getValidTargets()), decA.getSourceLocation()));
                } else if (this.world.getLint().invalidTargetForAnnotation.isEnabled()) {
                    this.world.getLint().invalidTargetForAnnotation.signal(new String[]{onType.getName(), annoX.getTypeName(), annoX.getValidTargets()}, decA.getSourceLocation(), new ISourceLocation[]{onType.getSourceLocation()});
                }
            }
            problemReported = true;
        }
        return problemReported;
    }

    private boolean applyDeclareParents(DeclareParents p, ResolvedType onType) {
        boolean didSomething = false;
        List<ResolvedType> newParents = p.findMatchingNewParents(onType, true);
        if (!newParents.isEmpty()) {
            didSomething = true;
            BcelWorld.getBcelObjectType(onType);
            for (ResolvedType newParent : newParents) {
                onType.addParent(newParent);
                NewParentTypeMunger newParentMunger = new NewParentTypeMunger(newParent, p.getDeclaringType());
                if (p.isMixin()) {
                    newParentMunger.setIsMixin(true);
                }
                newParentMunger.setSourceLocation(p.getSourceLocation());
                onType.addInterTypeMunger(new BcelTypeMunger(newParentMunger, this.xcutSet.findAspectDeclaringParents(p)), false);
            }
        }
        return didSomething;
    }

    public void weaveNormalTypeMungers(ResolvedType onType) {
        ContextToken tok = CompilationAndWeavingContext.enteringPhase(24, onType.getName());
        if (onType.isRawType() || onType.isParameterizedType()) {
            onType = onType.getGenericType();
        }
        for (ConcreteTypeMunger m : this.typeMungerList) {
            if (m.isLateMunger() || !m.matches(onType)) continue;
            onType.addInterTypeMunger(m, false);
        }
        CompilationAndWeavingContext.leavingPhase(tok);
    }

    public LazyClassGen weaveWithoutDump(UnwovenClassFile classFile, BcelObjectType classType) throws IOException {
        return this.weave(classFile, classType, false);
    }

    LazyClassGen weave(UnwovenClassFile classFile, BcelObjectType classType) throws IOException {
        LazyClassGen ret = this.weave(classFile, classType, true);
        return ret;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private LazyClassGen weave(UnwovenClassFile classFile, BcelObjectType classType, boolean dump) throws IOException {
        try {
            LazyClassGen lazyClassGen;
            if (classType.isSynthetic()) {
                if (dump) {
                    this.dumpUnchanged(classFile);
                }
                LazyClassGen lazyClassGen2 = null;
                return lazyClassGen2;
            }
            ReferenceType resolvedClassType = classType.getResolvedTypeX();
            if (this.world.isXmlConfigured() && this.world.getXmlConfiguration().excludesType(resolvedClassType)) {
                if (!this.world.getMessageHandler().isIgnoring(IMessage.INFO)) {
                    this.world.getMessageHandler().handleMessage(MessageUtil.info("Type '" + resolvedClassType.getName() + "' not woven due to exclusion via XML weaver exclude section"));
                }
                if (dump) {
                    this.dumpUnchanged(classFile);
                }
                LazyClassGen lazyClassGen3 = null;
                return lazyClassGen3;
            }
            List<ShadowMunger> shadowMungers = this.fastMatch(this.shadowMungerList, resolvedClassType);
            List<ConcreteTypeMunger> typeMungers = classType.getResolvedTypeX().getInterTypeMungers();
            resolvedClassType.checkInterTypeMungers();
            boolean mightNeedToWeave = shadowMungers.size() > 0 || typeMungers.size() > 0 || classType.isAspect() || this.world.getDeclareAnnotationOnMethods().size() > 0 || this.world.getDeclareAnnotationOnFields().size() > 0;
            boolean mightNeedBridgeMethods = this.world.isInJava5Mode() && !classType.isInterface() && resolvedClassType.getInterTypeMungersIncludingSupers().size() > 0;
            LazyClassGen clazz = null;
            if (mightNeedToWeave || mightNeedBridgeMethods) {
                String messageText;
                String classDebugInfo;
                clazz = classType.getLazyClassGen();
                try {
                    boolean isChanged = false;
                    if (mightNeedToWeave) {
                        isChanged = BcelClassWeaver.weave(this.world, clazz, shadowMungers, typeMungers, this.lateTypeMungerList, this.inReweavableMode);
                    }
                    this.checkDeclareTypeErrorOrWarning(this.world, classType);
                    if (mightNeedBridgeMethods) {
                        boolean bl = isChanged = BcelClassWeaver.calculateAnyRequiredBridgeMethods(this.world, clazz) || isChanged;
                    }
                    if (isChanged) {
                        if (dump) {
                            this.dump(classFile, clazz);
                        }
                        LazyClassGen lazyClassGen4 = clazz;
                        return lazyClassGen4;
                    }
                }
                catch (RuntimeException re) {
                    classDebugInfo = null;
                    try {
                        classDebugInfo = clazz.toLongString();
                    }
                    catch (Throwable e) {
                        new RuntimeException("Crashed whilst crashing with this exception: " + e, e).printStackTrace();
                        classDebugInfo = clazz.getClassName();
                    }
                    messageText = "trouble in: \n" + classDebugInfo;
                    this.getWorld().getMessageHandler().handleMessage(new Message(messageText, IMessage.ABORT, re, null));
                }
                catch (Error re) {
                    classDebugInfo = null;
                    try {
                        classDebugInfo = clazz.toLongString();
                    }
                    catch (OutOfMemoryError oome) {
                        System.err.println("Ran out of memory creating debug info for an error");
                        re.printStackTrace(System.err);
                        classDebugInfo = clazz.getClassName();
                    }
                    catch (Throwable e) {
                        classDebugInfo = clazz.getClassName();
                    }
                    messageText = "trouble in: \n" + classDebugInfo;
                    this.getWorld().getMessageHandler().handleMessage(new Message(messageText, IMessage.ABORT, re, null));
                }
            } else {
                this.checkDeclareTypeErrorOrWarning(this.world, classType);
            }
            AsmManager model = this.world.getModelAsAsmManager();
            if (this.world.isMinimalModel() && model != null && !classType.isAspect()) {
                IProgramElement parent;
                String tname;
                String pkgname;
                AspectJElementHierarchy hierarchy = (AspectJElementHierarchy)model.getHierarchy();
                IProgramElement typeElement = hierarchy.findElementForType(pkgname = classType.getResolvedTypeX().getPackageName(), tname = classType.getResolvedTypeX().getSimpleBaseName());
                if (typeElement != null && this.hasInnerType(typeElement)) {
                    this.candidatesForRemoval.add(typeElement);
                }
                if (typeElement != null && !this.hasInnerType(typeElement) && (parent = typeElement.getParent()) != null) {
                    parent.removeChild(typeElement);
                    if (parent.getKind().isSourceFile()) {
                        this.removeSourceFileIfNoMoreTypeDeclarationsInside(hierarchy, typeElement, parent);
                    } else {
                        hierarchy.forget(null, typeElement);
                        this.walkUpRemovingEmptyTypesAndPossiblyEmptySourceFile(hierarchy, tname, parent);
                    }
                }
            }
            if (dump) {
                this.dumpUnchanged(classFile);
                lazyClassGen = clazz;
                return lazyClassGen;
            }
            if (clazz != null && !clazz.getChildClasses(this.world).isEmpty()) {
                lazyClassGen = clazz;
                return lazyClassGen;
            }
            lazyClassGen = null;
            return lazyClassGen;
        }
        finally {
            this.world.demote();
        }
    }

    private void walkUpRemovingEmptyTypesAndPossiblyEmptySourceFile(AspectJElementHierarchy hierarchy, String tname, IProgramElement typeThatHasChildRemoved) {
        IProgramElement parent;
        while (typeThatHasChildRemoved != null && !typeThatHasChildRemoved.getKind().isType() && !typeThatHasChildRemoved.getKind().isSourceFile()) {
            typeThatHasChildRemoved = typeThatHasChildRemoved.getParent();
        }
        if (this.candidatesForRemoval.contains(typeThatHasChildRemoved) && !this.hasInnerType(typeThatHasChildRemoved) && (parent = typeThatHasChildRemoved.getParent()) != null) {
            parent.removeChild(typeThatHasChildRemoved);
            this.candidatesForRemoval.remove(typeThatHasChildRemoved);
            if (parent.getKind().isSourceFile()) {
                this.removeSourceFileIfNoMoreTypeDeclarationsInside(hierarchy, typeThatHasChildRemoved, parent);
            } else {
                this.walkUpRemovingEmptyTypesAndPossiblyEmptySourceFile(hierarchy, tname, parent);
            }
        }
    }

    private void removeSourceFileIfNoMoreTypeDeclarationsInside(AspectJElementHierarchy hierarchy, IProgramElement typeElement, IProgramElement sourceFileNode) {
        IProgramElement compilationUnit = sourceFileNode;
        boolean anyOtherTypeDeclarations = false;
        for (IProgramElement child : compilationUnit.getChildren()) {
            IProgramElement.Kind k = child.getKind();
            if (!k.isType()) continue;
            anyOtherTypeDeclarations = true;
            break;
        }
        if (!anyOtherTypeDeclarations) {
            IProgramElement cuParent = compilationUnit.getParent();
            if (cuParent != null) {
                compilationUnit.setParent(null);
                cuParent.removeChild(compilationUnit);
            }
            hierarchy.forget(sourceFileNode, typeElement);
        } else {
            hierarchy.forget(null, typeElement);
        }
    }

    private boolean hasInnerType(IProgramElement typeNode) {
        for (IProgramElement child : typeNode.getChildren()) {
            boolean b;
            IProgramElement.Kind kind = child.getKind();
            if (kind.isType()) {
                return true;
            }
            if (!kind.isType() && kind != IProgramElement.Kind.METHOD && kind != IProgramElement.Kind.CONSTRUCTOR || !(b = this.hasInnerType(child))) continue;
            return b;
        }
        return false;
    }

    private void checkDeclareTypeErrorOrWarning(BcelWorld world2, BcelObjectType classType) {
        List<DeclareTypeErrorOrWarning> dteows = this.world.getDeclareTypeEows();
        for (DeclareTypeErrorOrWarning dteow : dteows) {
            if (!dteow.getTypePattern().matchesStatically(classType.getResolvedTypeX())) continue;
            if (dteow.isError()) {
                this.world.getMessageHandler().handleMessage(MessageUtil.error(dteow.getMessage(), classType.getResolvedTypeX().getSourceLocation()));
                continue;
            }
            this.world.getMessageHandler().handleMessage(MessageUtil.warn(dteow.getMessage(), classType.getResolvedTypeX().getSourceLocation()));
        }
    }

    private void dumpUnchanged(UnwovenClassFile classFile) throws IOException {
        if (this.zipOutputStream != null) {
            this.writeZipEntry(this.getEntryName(classFile.getJavaClass().getClassName()), classFile.getBytes());
        } else {
            classFile.writeUnchangedBytes();
        }
    }

    private String getEntryName(String className) {
        return className.replace('.', '/') + ".class";
    }

    private void dump(UnwovenClassFile classFile, LazyClassGen clazz) throws IOException {
        if (this.zipOutputStream != null) {
            String mainClassName = classFile.getJavaClass().getClassName();
            this.writeZipEntry(this.getEntryName(mainClassName), clazz.getJavaClass(this.world).getBytes());
            List<UnwovenClassFile.ChildClass> childClasses = clazz.getChildClasses(this.world);
            if (!childClasses.isEmpty()) {
                for (UnwovenClassFile.ChildClass c : childClasses) {
                    this.writeZipEntry(this.getEntryName(mainClassName + "$" + c.name), c.bytes);
                }
            }
        } else {
            classFile.writeWovenBytes(clazz.getJavaClass(this.world).getBytes(), clazz.getChildClasses(this.world));
        }
    }

    private void writeZipEntry(String name, byte[] bytes) throws IOException {
        ZipEntry newEntry = new ZipEntry(name);
        this.zipOutputStream.putNextEntry(newEntry);
        this.zipOutputStream.write(bytes);
        this.zipOutputStream.closeEntry();
    }

    private List<ShadowMunger> fastMatch(List<ShadowMunger> list, ResolvedType type) {
        if (list == null) {
            return Collections.emptyList();
        }
        boolean isOverweaving = this.world.isOverWeaving();
        WeaverStateInfo typeWeaverState = isOverweaving ? type.getWeaverState() : null;
        FastMatchInfo info = new FastMatchInfo(type, null, this.world);
        ArrayList<ShadowMunger> result = new ArrayList<ShadowMunger>();
        if (this.world.areInfoMessagesEnabled() && this.world.isTimingEnabled()) {
            for (ShadowMunger munger : list) {
                ResolvedType declaringAspect;
                if (typeWeaverState != null && typeWeaverState.isAspectAlreadyApplied(declaringAspect = munger.getDeclaringType())) continue;
                Pointcut pointcut = munger.getPointcut();
                long starttime = System.nanoTime();
                FuzzyBoolean fb = pointcut.fastMatch(info);
                long endtime = System.nanoTime();
                this.world.recordFastMatch(pointcut, endtime - starttime);
                if (!fb.maybeTrue()) continue;
                result.add(munger);
            }
        } else {
            for (ShadowMunger munger : list) {
                Pointcut pointcut;
                FuzzyBoolean fb;
                ResolvedType declaringAspect;
                if (typeWeaverState != null && typeWeaverState.isAspectAlreadyApplied(declaringAspect = munger.getConcreteAspect()) || !(fb = (pointcut = munger.getPointcut()).fastMatch(info)).maybeTrue()) continue;
                result.add(munger);
            }
        }
        return result;
    }

    public void setReweavableMode(boolean xNotReweavable) {
        this.inReweavableMode = !xNotReweavable;
        WeaverStateInfo.setReweavableModeDefaults(!xNotReweavable, false, true);
    }

    public boolean isReweavable() {
        return this.inReweavableMode;
    }

    public World getWorld() {
        return this.world;
    }

    public void tidyUp() {
        if (trace.isTraceEnabled()) {
            trace.enter("tidyUp", this);
        }
        this.shadowMungerList = null;
        this.typeMungerList = null;
        this.lateTypeMungerList = null;
        this.declareParentsList = null;
        if (trace.isTraceEnabled()) {
            trace.exit("tidyUp");
        }
    }

    public void write(CompressingDataOutputStream dos) throws IOException {
        this.xcutSet.write(dos);
    }

    public void setShadowMungers(List<ShadowMunger> shadowMungers) {
        this.shadowMungerList = shadowMungers;
    }
}

