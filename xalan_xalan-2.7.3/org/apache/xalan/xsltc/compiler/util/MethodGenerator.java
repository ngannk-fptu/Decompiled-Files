/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.ICONST;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.TargetLostException;
import org.apache.bcel.generic.Type;
import org.apache.xalan.xsltc.compiler.Constants;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.XSLTC;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.InternalError;
import org.apache.xalan.xsltc.compiler.util.MarkerInstruction;
import org.apache.xalan.xsltc.compiler.util.ObjectType;
import org.apache.xalan.xsltc.compiler.util.OutlineableChunkEnd;
import org.apache.xalan.xsltc.compiler.util.OutlineableChunkStart;
import org.apache.xalan.xsltc.compiler.util.SlotAllocator;

public class MethodGenerator
extends MethodGen
implements Constants {
    protected static final int INVALID_INDEX = -1;
    private static final String START_ELEMENT_SIG = "(Ljava/lang/String;)V";
    private static final String END_ELEMENT_SIG = "(Ljava/lang/String;)V";
    private InstructionList _mapTypeSub;
    private static final int DOM_INDEX = 1;
    private static final int ITERATOR_INDEX = 2;
    private static final int HANDLER_INDEX = 3;
    private static final int MAX_METHOD_SIZE = 65535;
    private static final int MAX_BRANCH_TARGET_OFFSET = Short.MAX_VALUE;
    private static final int MIN_BRANCH_TARGET_OFFSET = Short.MIN_VALUE;
    private static final int TARGET_METHOD_SIZE = 60000;
    private static final int MINIMUM_OUTLINEABLE_CHUNK_SIZE = 1000;
    private Instruction _iloadCurrent;
    private Instruction _istoreCurrent;
    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;
    private final Instruction _aloadDom;
    private final Instruction _astoreDom;
    private final Instruction _startElement;
    private final Instruction _endElement;
    private final Instruction _startDocument;
    private final Instruction _endDocument;
    private final Instruction _attribute;
    private final Instruction _uniqueAttribute;
    private final Instruction _namespace;
    private final Instruction _setStartNode;
    private final Instruction _reset;
    private final Instruction _nextNode;
    private SlotAllocator _slotAllocator;
    private boolean _allocatorInit = false;
    private LocalVariableRegistry _localVariableRegistry;
    private Hashtable _preCompiled = new Hashtable();
    private int m_totalChunks = 0;
    private int m_openChunks = 0;

    public MethodGenerator(int access_flags, Type return_type, Type[] arg_types, String[] arg_names, String method_name, String class_name, InstructionList il, ConstantPoolGen cpg) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cpg);
        this._astoreHandler = new ASTORE(3);
        this._aloadHandler = new ALOAD(3);
        this._astoreIterator = new ASTORE(2);
        this._aloadIterator = new ALOAD(2);
        this._aloadDom = new ALOAD(1);
        this._astoreDom = new ASTORE(1);
        int startElement = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "startElement", "(Ljava/lang/String;)V");
        this._startElement = new INVOKEINTERFACE(startElement, 2);
        int endElement = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "endElement", "(Ljava/lang/String;)V");
        this._endElement = new INVOKEINTERFACE(endElement, 2);
        int attribute = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "addAttribute", "(Ljava/lang/String;Ljava/lang/String;)V");
        this._attribute = new INVOKEINTERFACE(attribute, 3);
        int uniqueAttribute = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "addUniqueAttribute", "(Ljava/lang/String;Ljava/lang/String;I)V");
        this._uniqueAttribute = new INVOKEINTERFACE(uniqueAttribute, 4);
        int namespace = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "namespaceAfterStartElement", "(Ljava/lang/String;Ljava/lang/String;)V");
        this._namespace = new INVOKEINTERFACE(namespace, 3);
        int index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "startDocument", "()V");
        this._startDocument = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref(TRANSLET_OUTPUT_INTERFACE, "endDocument", "()V");
        this._endDocument = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "setStartNode", "(I)Lorg/apache/xml/dtm/DTMAxisIterator;");
        this._setStartNode = new INVOKEINTERFACE(index, 2);
        index = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "reset", "()Lorg/apache/xml/dtm/DTMAxisIterator;");
        this._reset = new INVOKEINTERFACE(index, 1);
        index = cpg.addInterfaceMethodref("org.apache.xml.dtm.DTMAxisIterator", "next", "()I");
        this._nextNode = new INVOKEINTERFACE(index, 1);
        this._slotAllocator = new SlotAllocator();
        this._slotAllocator.initialize(this.getLocalVariableRegistry().getLocals(false));
        this._allocatorInit = true;
    }

    @Override
    public LocalVariableGen addLocalVariable(String name, Type type, InstructionHandle start, InstructionHandle end) {
        LocalVariableGen lvg;
        if (this._allocatorInit) {
            lvg = this.addLocalVariable2(name, type, start);
        } else {
            lvg = super.addLocalVariable(name, type, start, end);
            this.getLocalVariableRegistry().registerLocalVariable(lvg);
        }
        return lvg;
    }

    public LocalVariableGen addLocalVariable2(String name, Type type, InstructionHandle start) {
        LocalVariableGen lvg = super.addLocalVariable(name, type, this._slotAllocator.allocateSlot(type), start, null);
        this.getLocalVariableRegistry().registerLocalVariable(lvg);
        return lvg;
    }

    private LocalVariableRegistry getLocalVariableRegistry() {
        if (this._localVariableRegistry == null) {
            this._localVariableRegistry = new LocalVariableRegistry();
        }
        return this._localVariableRegistry;
    }

    boolean offsetInLocalVariableGenRange(LocalVariableGen lvg, int offset) {
        InstructionHandle lvgStart = lvg.getStart();
        InstructionHandle lvgEnd = lvg.getEnd();
        if (lvgStart == null) {
            lvgStart = this.getInstructionList().getStart();
        }
        if (lvgEnd == null) {
            lvgEnd = this.getInstructionList().getEnd();
        }
        return lvgStart.getPosition() <= offset && lvgEnd.getPosition() + lvgEnd.getInstruction().getLength() >= offset;
    }

    @Override
    public void removeLocalVariable(LocalVariableGen lvg) {
        this._slotAllocator.releaseSlot(lvg);
        this.getLocalVariableRegistry().removeByNameTracking(lvg);
        super.removeLocalVariable(lvg);
    }

    public Instruction loadDOM() {
        return this._aloadDom;
    }

    public Instruction storeDOM() {
        return this._astoreDom;
    }

    public Instruction storeHandler() {
        return this._astoreHandler;
    }

    public Instruction loadHandler() {
        return this._aloadHandler;
    }

    public Instruction storeIterator() {
        return this._astoreIterator;
    }

    public Instruction loadIterator() {
        return this._aloadIterator;
    }

    public final Instruction setStartNode() {
        return this._setStartNode;
    }

    public final Instruction reset() {
        return this._reset;
    }

    public final Instruction nextNode() {
        return this._nextNode;
    }

    public final Instruction startElement() {
        return this._startElement;
    }

    public final Instruction endElement() {
        return this._endElement;
    }

    public final Instruction startDocument() {
        return this._startDocument;
    }

    public final Instruction endDocument() {
        return this._endDocument;
    }

    public final Instruction attribute() {
        return this._attribute;
    }

    public final Instruction uniqueAttribute() {
        return this._uniqueAttribute;
    }

    public final Instruction namespace() {
        return this._namespace;
    }

    public Instruction loadCurrentNode() {
        if (this._iloadCurrent == null) {
            int idx = this.getLocalIndex("current");
            this._iloadCurrent = idx > 0 ? new ILOAD(idx) : new ICONST(0);
        }
        return this._iloadCurrent;
    }

    public Instruction storeCurrentNode() {
        return this._istoreCurrent != null ? this._istoreCurrent : (this._istoreCurrent = new ISTORE(this.getLocalIndex("current")));
    }

    public Instruction loadContextNode() {
        return this.loadCurrentNode();
    }

    public Instruction storeContextNode() {
        return this.storeCurrentNode();
    }

    public int getLocalIndex(String name) {
        return this.getLocalVariable(name).getIndex();
    }

    public LocalVariableGen getLocalVariable(String name) {
        return this.getLocalVariableRegistry().lookUpByName(name);
    }

    @Override
    public void setMaxLocals() {
        int maxLocals = super.getMaxLocals();
        LocalVariableGen[] localVars = super.getLocalVariables();
        if (localVars != null && localVars.length > maxLocals) {
            maxLocals = localVars.length;
        }
        if (maxLocals < 5) {
            maxLocals = 5;
        }
        super.setMaxLocals(maxLocals);
    }

    public void addInstructionList(Pattern pattern, InstructionList ilist) {
        this._preCompiled.put(pattern, ilist);
    }

    public InstructionList getInstructionList(Pattern pattern) {
        return (InstructionList)this._preCompiled.get(pattern);
    }

    private ArrayList getCandidateChunks(ClassGenerator classGen, int totalMethodSize) {
        InstructionHandle currentHandle;
        Iterator<InstructionHandle> instructions = this.getInstructionList().iterator();
        ArrayList<Chunk> candidateChunks = new ArrayList<Chunk>();
        ArrayList currLevelChunks = new ArrayList();
        Stack<ArrayList> subChunkStack = new Stack<ArrayList>();
        boolean openChunkAtCurrLevel = false;
        boolean firstInstruction = true;
        if (this.m_openChunks != 0) {
            String msg = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
            throw new InternalError(msg);
        }
        do {
            InstructionHandle chunkStart;
            int chunkEndPosition;
            int chunkSize;
            Instruction inst;
            currentHandle = instructions.hasNext() ? instructions.next() : null;
            Instruction instruction = inst = currentHandle != null ? currentHandle.getInstruction() : null;
            if (firstInstruction) {
                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
                firstInstruction = false;
            }
            if (inst instanceof OutlineableChunkStart) {
                if (openChunkAtCurrLevel) {
                    subChunkStack.push(currLevelChunks);
                    currLevelChunks = new ArrayList();
                }
                openChunkAtCurrLevel = true;
                currLevelChunks.add(currentHandle);
                continue;
            }
            if (currentHandle != null && !(inst instanceof OutlineableChunkEnd)) continue;
            ArrayList nestedSubChunks = null;
            if (!openChunkAtCurrLevel) {
                nestedSubChunks = currLevelChunks;
                currLevelChunks = (ArrayList)subChunkStack.pop();
            }
            if ((chunkSize = (chunkEndPosition = currentHandle != null ? currentHandle.getPosition() : totalMethodSize) - (chunkStart = (InstructionHandle)currLevelChunks.get(currLevelChunks.size() - 1)).getPosition()) <= 60000) {
                currLevelChunks.add(currentHandle);
            } else {
                int childChunkCount;
                if (!openChunkAtCurrLevel && (childChunkCount = nestedSubChunks.size() / 2) > 0) {
                    Chunk[] childChunks = new Chunk[childChunkCount];
                    for (int i = 0; i < childChunkCount; ++i) {
                        InstructionHandle start = (InstructionHandle)nestedSubChunks.get(i * 2);
                        InstructionHandle end = (InstructionHandle)nestedSubChunks.get(i * 2 + 1);
                        childChunks[i] = new Chunk(start, end);
                    }
                    ArrayList mergedChildChunks = this.mergeAdjacentChunks(childChunks);
                    for (int i = 0; i < mergedChildChunks.size(); ++i) {
                        Chunk mergedChunk = (Chunk)mergedChildChunks.get(i);
                        int mergedSize = mergedChunk.getChunkSize();
                        if (mergedSize < 1000 || mergedSize > 60000) continue;
                        candidateChunks.add(mergedChunk);
                    }
                }
                currLevelChunks.remove(currLevelChunks.size() - 1);
            }
            boolean bl = openChunkAtCurrLevel = (currLevelChunks.size() & 1) == 1;
        } while (currentHandle != null);
        return candidateChunks;
    }

    private ArrayList mergeAdjacentChunks(Chunk[] chunks) {
        int i;
        int[] adjacencyRunStart = new int[chunks.length];
        int[] adjacencyRunLength = new int[chunks.length];
        boolean[] chunkWasMerged = new boolean[chunks.length];
        int maximumRunOfChunks = 0;
        int numAdjacentRuns = 0;
        ArrayList<Chunk> mergedChunks = new ArrayList<Chunk>();
        int startOfCurrentRun = 0;
        for (i = 1; i < chunks.length; ++i) {
            if (chunks[i - 1].isAdjacentTo(chunks[i])) continue;
            int lengthOfRun = i - startOfCurrentRun;
            if (maximumRunOfChunks < lengthOfRun) {
                maximumRunOfChunks = lengthOfRun;
            }
            if (lengthOfRun > 1) {
                adjacencyRunLength[numAdjacentRuns] = lengthOfRun;
                adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
                ++numAdjacentRuns;
            }
            startOfCurrentRun = i;
        }
        if (chunks.length - startOfCurrentRun > 1) {
            int lengthOfRun = chunks.length - startOfCurrentRun;
            if (maximumRunOfChunks < lengthOfRun) {
                maximumRunOfChunks = lengthOfRun;
            }
            adjacencyRunLength[numAdjacentRuns] = chunks.length - startOfCurrentRun;
            adjacencyRunStart[numAdjacentRuns] = startOfCurrentRun;
            ++numAdjacentRuns;
        }
        for (int numToMerge = maximumRunOfChunks; numToMerge > 1; --numToMerge) {
            for (int run = 0; run < numAdjacentRuns; ++run) {
                int runStart = adjacencyRunStart[run];
                int runEnd = runStart + adjacencyRunLength[run] - 1;
                boolean foundChunksToMerge = false;
                int mergeStart = runStart;
                while (mergeStart + numToMerge - 1 <= runEnd && !foundChunksToMerge) {
                    int j;
                    int mergeEnd = mergeStart + numToMerge - 1;
                    int mergeSize = 0;
                    for (j = mergeStart; j <= mergeEnd; ++j) {
                        mergeSize += chunks[j].getChunkSize();
                    }
                    if (mergeSize <= 60000) {
                        foundChunksToMerge = true;
                        for (j = mergeStart; j <= mergeEnd; ++j) {
                            chunkWasMerged[j] = true;
                        }
                        mergedChunks.add(new Chunk(chunks[mergeStart].getChunkStart(), chunks[mergeEnd].getChunkEnd()));
                        adjacencyRunLength[run] = adjacencyRunStart[run] - mergeStart;
                        int trailingRunLength = runEnd - mergeEnd;
                        if (trailingRunLength >= 2) {
                            adjacencyRunStart[numAdjacentRuns] = mergeEnd + 1;
                            adjacencyRunLength[numAdjacentRuns] = trailingRunLength;
                            ++numAdjacentRuns;
                        }
                    }
                    ++mergeStart;
                }
            }
        }
        for (i = 0; i < chunks.length; ++i) {
            if (chunkWasMerged[i]) continue;
            mergedChunks.add(chunks[i]);
        }
        return mergedChunks;
    }

    public Method[] outlineChunks(ClassGenerator classGen, int originalMethodSize) {
        boolean moreMethodsOutlined;
        ArrayList<Method> methodsOutlined = new ArrayList<Method>();
        int currentMethodSize = originalMethodSize;
        int outlinedCount = 0;
        String originalMethodName = this.getName();
        if (originalMethodName.equals("<init>")) {
            originalMethodName = "$lt$init$gt$";
        } else if (originalMethodName.equals("<clinit>")) {
            originalMethodName = "$lt$clinit$gt$";
        }
        do {
            ArrayList candidateChunks = this.getCandidateChunks(classGen, currentMethodSize);
            Collections.sort(candidateChunks);
            moreMethodsOutlined = false;
            for (int i = candidateChunks.size() - 1; i >= 0 && currentMethodSize > 60000; --i) {
                Chunk chunkToOutline = (Chunk)candidateChunks.get(i);
                methodsOutlined.add(this.outline(chunkToOutline.getChunkStart(), chunkToOutline.getChunkEnd(), originalMethodName + "$outline$" + outlinedCount, classGen));
                ++outlinedCount;
                moreMethodsOutlined = true;
                InstructionList il = this.getInstructionList();
                InstructionHandle lastInst = il.getEnd();
                il.setPositions();
                currentMethodSize = lastInst.getPosition() + lastInst.getInstruction().getLength();
            }
        } while (moreMethodsOutlined && currentMethodSize > 60000);
        if (currentMethodSize > 65535) {
            String msg = new ErrorMsg("OUTLINE_ERR_METHOD_TOO_BIG").toString();
            throw new InternalError(msg);
        }
        Method[] methodsArr = new Method[methodsOutlined.size() + 1];
        methodsOutlined.toArray(methodsArr);
        methodsArr[methodsOutlined.size()] = this.getThisMethod();
        return methodsArr;
    }

    private Method outline(InstructionHandle first, InstructionHandle last, String outlinedMethodName, ClassGenerator classGen) {
        String varName;
        InstructionHandle ih;
        InstructionHandle outlinedMethodRef;
        boolean isStaticMethod;
        if (this.getExceptionHandlers().length != 0) {
            String msg = new ErrorMsg("OUTLINE_ERR_TRY_CATCH").toString();
            throw new InternalError(msg);
        }
        int outlineChunkStartOffset = first.getPosition();
        int outlineChunkEndOffset = last.getPosition() + last.getInstruction().getLength();
        ConstantPoolGen cpg = this.getConstantPool();
        InstructionList newIL = new InstructionList();
        XSLTC xsltc = classGen.getParser().getXSLTC();
        String argTypeName = xsltc.getHelperClassName();
        Type[] argTypes = new Type[]{new ObjectType(argTypeName).toJCType()};
        String argName = "copyLocals";
        String[] argNames = new String[]{"copyLocals"};
        int methodAttributes = 18;
        boolean bl = isStaticMethod = (this.getAccessFlags() & 8) != 0;
        if (isStaticMethod) {
            methodAttributes |= 8;
        }
        MethodGenerator outlinedMethodGen = new MethodGenerator(methodAttributes, Type.VOID, argTypes, argNames, outlinedMethodName, this.getClassName(), newIL, cpg);
        ClassGenerator copyAreaCG = new ClassGenerator(argTypeName, "java.lang.Object", argTypeName + ".java", 49, null, classGen.getStylesheet()){

            @Override
            public boolean isExternal() {
                return true;
            }
        };
        ConstantPoolGen copyAreaCPG = copyAreaCG.getConstantPool();
        copyAreaCG.addEmptyConstructor(1);
        int copyAreaFieldCount = 0;
        InstructionHandle limit = last.getNext();
        InstructionList oldMethCopyInIL = new InstructionList();
        InstructionList oldMethCopyOutIL = new InstructionList();
        InstructionList newMethCopyInIL = new InstructionList();
        InstructionList newMethCopyOutIL = new InstructionList();
        InstructionHandle outlinedMethodCallSetup = oldMethCopyInIL.append(new NEW(cpg.addClass(argTypeName)));
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(InstructionConstants.DUP);
        oldMethCopyInIL.append(new INVOKESPECIAL(cpg.addMethodref(argTypeName, "<init>", "()V")));
        if (isStaticMethod) {
            outlinedMethodRef = oldMethCopyOutIL.append(new INVOKESTATIC(cpg.addMethodref(classGen.getClassName(), outlinedMethodName, outlinedMethodGen.getSignature())));
        } else {
            oldMethCopyOutIL.append(InstructionConstants.THIS);
            oldMethCopyOutIL.append(InstructionConstants.SWAP);
            outlinedMethodRef = oldMethCopyOutIL.append(new INVOKEVIRTUAL(cpg.addMethodref(classGen.getClassName(), outlinedMethodName, outlinedMethodGen.getSignature())));
        }
        boolean chunkStartTargetMappingsPending = false;
        InstructionHandle pendingTargetMappingHandle = null;
        InstructionHandle lastCopyHandle = null;
        HashMap<InstructionHandle, InstructionHandle> targetMap = new HashMap<InstructionHandle, InstructionHandle>();
        HashMap<LocalVariableGen, LocalVariableGen> localVarMap = new HashMap<LocalVariableGen, LocalVariableGen>();
        HashMap<LocalVariableGen, InstructionHandle> revisedLocalVarStart = new HashMap<LocalVariableGen, InstructionHandle>();
        HashMap<LocalVariableGen, InstructionHandle> revisedLocalVarEnd = new HashMap<LocalVariableGen, InstructionHandle>();
        for (ih = first; ih != limit; ih = ih.getNext()) {
            Instruction inst = ih.getInstruction();
            if (inst instanceof MarkerInstruction) {
                if (!ih.hasTargeters()) continue;
                if (inst instanceof OutlineableChunkEnd) {
                    targetMap.put(ih, lastCopyHandle);
                    continue;
                }
                if (chunkStartTargetMappingsPending) continue;
                chunkStartTargetMappingsPending = true;
                pendingTargetMappingHandle = ih;
                continue;
            }
            Instruction c = inst.copy();
            lastCopyHandle = c instanceof BranchInstruction ? newIL.append((BranchInstruction)c) : newIL.append(c);
            if (c instanceof LocalVariableInstruction || c instanceof RET) {
                IndexedInstruction lvi = (IndexedInstruction)((Object)c);
                int oldLocalVarIndex = lvi.getIndex();
                LocalVariableGen oldLVG = this.getLocalVariableRegistry().lookupRegisteredLocalVariable(oldLocalVarIndex, ih.getPosition());
                LocalVariableGen newLVG = (LocalVariableGen)localVarMap.get(oldLVG);
                if (localVarMap.get(oldLVG) == null) {
                    boolean copyInLocalValue = this.offsetInLocalVariableGenRange(oldLVG, outlineChunkStartOffset != 0 ? outlineChunkStartOffset - 1 : 0);
                    boolean copyOutLocalValue = this.offsetInLocalVariableGenRange(oldLVG, outlineChunkEndOffset + 1);
                    if (copyInLocalValue || copyOutLocalValue) {
                        varName = oldLVG.getName();
                        Type varType = oldLVG.getType();
                        newLVG = outlinedMethodGen.addLocalVariable(varName, varType, null, null);
                        int newLocalVarIndex = newLVG.getIndex();
                        String varSignature = varType.getSignature();
                        localVarMap.put(oldLVG, newLVG);
                        String copyAreaFieldName = "field" + ++copyAreaFieldCount;
                        copyAreaCG.addField(new Field(1, copyAreaCPG.addUtf8(copyAreaFieldName), copyAreaCPG.addUtf8(varSignature), null, copyAreaCPG.getConstantPool()));
                        int fieldRef = cpg.addFieldref(argTypeName, copyAreaFieldName, varSignature);
                        if (copyInLocalValue) {
                            oldMethCopyInIL.append(InstructionConstants.DUP);
                            InstructionHandle copyInLoad = oldMethCopyInIL.append(MethodGenerator.loadLocal(oldLocalVarIndex, varType));
                            oldMethCopyInIL.append(new PUTFIELD(fieldRef));
                            if (!copyOutLocalValue) {
                                revisedLocalVarEnd.put(oldLVG, copyInLoad);
                            }
                            newMethCopyInIL.append(InstructionConstants.ALOAD_1);
                            newMethCopyInIL.append(new GETFIELD(fieldRef));
                            newMethCopyInIL.append(MethodGenerator.storeLocal(newLocalVarIndex, varType));
                        }
                        if (copyOutLocalValue) {
                            newMethCopyOutIL.append(InstructionConstants.ALOAD_1);
                            newMethCopyOutIL.append(MethodGenerator.loadLocal(newLocalVarIndex, varType));
                            newMethCopyOutIL.append(new PUTFIELD(fieldRef));
                            oldMethCopyOutIL.append(InstructionConstants.DUP);
                            oldMethCopyOutIL.append(new GETFIELD(fieldRef));
                            InstructionHandle copyOutStore = oldMethCopyOutIL.append(MethodGenerator.storeLocal(oldLocalVarIndex, varType));
                            if (!copyInLocalValue) {
                                revisedLocalVarStart.put(oldLVG, copyOutStore);
                            }
                        }
                    }
                }
            }
            if (ih.hasTargeters()) {
                targetMap.put(ih, lastCopyHandle);
            }
            if (!chunkStartTargetMappingsPending) continue;
            do {
                targetMap.put(pendingTargetMappingHandle, lastCopyHandle);
            } while ((pendingTargetMappingHandle = pendingTargetMappingHandle.getNext()) != ih);
            chunkStartTargetMappingsPending = false;
        }
        ih = first;
        InstructionHandle ch = newIL.getStart();
        while (ch != null) {
            Instruction i = ih.getInstruction();
            Instruction c = ch.getInstruction();
            if (i instanceof BranchInstruction) {
                BranchInstruction bc = (BranchInstruction)c;
                BranchInstruction bi = (BranchInstruction)i;
                InstructionHandle itarget = bi.getTarget();
                InstructionHandle newTarget = (InstructionHandle)targetMap.get(itarget);
                bc.setTarget(newTarget);
                if (bi instanceof Select) {
                    InstructionHandle[] itargets = ((Select)bi).getTargets();
                    InstructionHandle[] ctargets = ((Select)bc).getTargets();
                    for (int j = 0; j < itargets.length; ++j) {
                        ctargets[j] = (InstructionHandle)targetMap.get(itargets[j]);
                    }
                }
            } else if (i instanceof LocalVariableInstruction || i instanceof RET) {
                int newLocalVarIndex;
                IndexedInstruction lvi = (IndexedInstruction)((Object)c);
                int oldLocalVarIndex = lvi.getIndex();
                LocalVariableGen oldLVG = this.getLocalVariableRegistry().lookupRegisteredLocalVariable(oldLocalVarIndex, ih.getPosition());
                LocalVariableGen newLVG = (LocalVariableGen)localVarMap.get(oldLVG);
                if (newLVG == null) {
                    varName = oldLVG.getName();
                    Type varType = oldLVG.getType();
                    newLVG = outlinedMethodGen.addLocalVariable(varName, varType, null, null);
                    newLocalVarIndex = newLVG.getIndex();
                    localVarMap.put(oldLVG, newLVG);
                    revisedLocalVarStart.put(oldLVG, outlinedMethodRef);
                    revisedLocalVarEnd.put(oldLVG, outlinedMethodRef);
                } else {
                    newLocalVarIndex = newLVG.getIndex();
                }
                lvi.setIndex(newLocalVarIndex);
            }
            if (ih.hasTargeters()) {
                InstructionTargeter[] targeters = ih.getTargeters();
                for (int idx = 0; idx < targeters.length; ++idx) {
                    Object newLVG;
                    InstructionTargeter targeter = targeters[idx];
                    if (!(targeter instanceof LocalVariableGen) || ((LocalVariableGen)targeter).getEnd() != ih || (newLVG = localVarMap.get(targeter)) == null) continue;
                    outlinedMethodGen.removeLocalVariable((LocalVariableGen)newLVG);
                }
            }
            if (!(i instanceof MarkerInstruction)) {
                ch = ch.getNext();
            }
            ih = ih.getNext();
        }
        oldMethCopyOutIL.append(InstructionConstants.POP);
        for (Map.Entry lvgRangeStartPair : revisedLocalVarStart.entrySet()) {
            LocalVariableGen lvg = (LocalVariableGen)lvgRangeStartPair.getKey();
            InstructionHandle startInst = (InstructionHandle)lvgRangeStartPair.getValue();
            lvg.setStart(startInst);
        }
        for (Map.Entry lvgRangeEndPair : revisedLocalVarEnd.entrySet()) {
            LocalVariableGen lvg = (LocalVariableGen)lvgRangeEndPair.getKey();
            InstructionHandle endInst = (InstructionHandle)lvgRangeEndPair.getValue();
            lvg.setEnd(endInst);
        }
        xsltc.dumpClass(copyAreaCG.getJavaClass());
        InstructionList oldMethodIL = this.getInstructionList();
        oldMethodIL.insert(first, oldMethCopyInIL);
        oldMethodIL.insert(first, oldMethCopyOutIL);
        newIL.insert(newMethCopyInIL);
        newIL.append(newMethCopyOutIL);
        newIL.append(InstructionConstants.RETURN);
        try {
            oldMethodIL.delete(first, last);
        }
        catch (TargetLostException e) {
            InstructionHandle[] targets = e.getTargets();
            for (int i = 0; i < targets.length; ++i) {
                InstructionHandle lostTarget = targets[i];
                InstructionTargeter[] targeters = lostTarget.getTargeters();
                for (int j = 0; j < targeters.length; ++j) {
                    if (targeters[j] instanceof LocalVariableGen) {
                        LocalVariableGen lvgTargeter = (LocalVariableGen)targeters[j];
                        if (lvgTargeter.getStart() == lostTarget) {
                            lvgTargeter.setStart(outlinedMethodRef);
                        }
                        if (lvgTargeter.getEnd() != lostTarget) continue;
                        lvgTargeter.setEnd(outlinedMethodRef);
                        continue;
                    }
                    targeters[j].updateTarget(lostTarget, outlinedMethodCallSetup);
                }
            }
        }
        String[] exceptions = this.getExceptions();
        for (int i = 0; i < exceptions.length; ++i) {
            outlinedMethodGen.addException(exceptions[i]);
        }
        return outlinedMethodGen.getThisMethod();
    }

    private static Instruction loadLocal(int index, Type type) {
        if (type == Type.BOOLEAN) {
            return new ILOAD(index);
        }
        if (type == Type.INT) {
            return new ILOAD(index);
        }
        if (type == Type.SHORT) {
            return new ILOAD(index);
        }
        if (type == Type.LONG) {
            return new LLOAD(index);
        }
        if (type == Type.BYTE) {
            return new ILOAD(index);
        }
        if (type == Type.CHAR) {
            return new ILOAD(index);
        }
        if (type == Type.FLOAT) {
            return new FLOAD(index);
        }
        if (type == Type.DOUBLE) {
            return new DLOAD(index);
        }
        return new ALOAD(index);
    }

    private static Instruction storeLocal(int index, Type type) {
        if (type == Type.BOOLEAN) {
            return new ISTORE(index);
        }
        if (type == Type.INT) {
            return new ISTORE(index);
        }
        if (type == Type.SHORT) {
            return new ISTORE(index);
        }
        if (type == Type.LONG) {
            return new LSTORE(index);
        }
        if (type == Type.BYTE) {
            return new ISTORE(index);
        }
        if (type == Type.CHAR) {
            return new ISTORE(index);
        }
        if (type == Type.FLOAT) {
            return new FSTORE(index);
        }
        if (type == Type.DOUBLE) {
            return new DSTORE(index);
        }
        return new ASTORE(index);
    }

    public void markChunkStart() {
        this.getInstructionList().append(OutlineableChunkStart.OUTLINEABLECHUNKSTART);
        ++this.m_totalChunks;
        ++this.m_openChunks;
    }

    public void markChunkEnd() {
        this.getInstructionList().append(OutlineableChunkEnd.OUTLINEABLECHUNKEND);
        --this.m_openChunks;
        if (this.m_openChunks < 0) {
            String msg = new ErrorMsg("OUTLINE_ERR_UNBALANCED_MARKERS").toString();
            throw new InternalError(msg);
        }
    }

    Method[] getGeneratedMethods(ClassGenerator classGen) {
        boolean ilChanged;
        InstructionList il = this.getInstructionList();
        InstructionHandle last = il.getEnd();
        il.setPositions();
        int instructionListSize = last.getPosition() + last.getInstruction().getLength();
        if (instructionListSize > Short.MAX_VALUE && (ilChanged = this.widenConditionalBranchTargetOffsets())) {
            il.setPositions();
            last = il.getEnd();
            instructionListSize = last.getPosition() + last.getInstruction().getLength();
        }
        Method[] generatedMethods = instructionListSize > 65535 ? this.outlineChunks(classGen, instructionListSize) : new Method[]{this.getThisMethod()};
        return generatedMethods;
    }

    protected Method getThisMethod() {
        this.stripAttributes(true);
        this.setMaxLocals();
        this.setMaxStack();
        this.removeNOPs();
        return this.getMethod();
    }

    boolean widenConditionalBranchTargetOffsets() {
        Instruction inst;
        InstructionHandle ih;
        boolean ilChanged = false;
        int maxOffsetChange = 0;
        InstructionList il = this.getInstructionList();
        block7: for (ih = il.getStart(); ih != null; ih = ih.getNext()) {
            inst = ih.getInstruction();
            switch (inst.getOpcode()) {
                case 167: 
                case 168: {
                    maxOffsetChange += 2;
                    continue block7;
                }
                case 170: 
                case 171: {
                    maxOffsetChange += 3;
                    continue block7;
                }
                case 153: 
                case 154: 
                case 155: 
                case 156: 
                case 157: 
                case 158: 
                case 159: 
                case 160: 
                case 161: 
                case 162: 
                case 163: 
                case 164: 
                case 165: 
                case 166: 
                case 198: 
                case 199: {
                    maxOffsetChange += 5;
                }
            }
        }
        for (ih = il.getStart(); ih != null; ih = ih.getNext()) {
            inst = ih.getInstruction();
            if (!(inst instanceof IfInstruction)) continue;
            IfInstruction oldIfInst = (IfInstruction)inst;
            BranchHandle oldIfHandle = (BranchHandle)ih;
            InstructionHandle target = oldIfInst.getTarget();
            int relativeTargetOffset = target.getPosition() - oldIfHandle.getPosition();
            if (relativeTargetOffset - maxOffsetChange >= Short.MIN_VALUE && relativeTargetOffset + maxOffsetChange <= Short.MAX_VALUE) continue;
            InstructionHandle nextHandle = oldIfHandle.getNext();
            IfInstruction invertedIfInst = oldIfInst.negate();
            BranchHandle invertedIfHandle = il.append((InstructionHandle)oldIfHandle, invertedIfInst);
            BranchHandle gotoHandle = il.append((InstructionHandle)invertedIfHandle, new GOTO(target));
            if (nextHandle == null) {
                nextHandle = il.append((InstructionHandle)gotoHandle, NOP);
            }
            invertedIfHandle.updateTarget(target, nextHandle);
            if (oldIfHandle.hasTargeters()) {
                InstructionTargeter[] targeters = oldIfHandle.getTargeters();
                for (int i = 0; i < targeters.length; ++i) {
                    InstructionTargeter targeter = targeters[i];
                    if (targeter instanceof LocalVariableGen) {
                        LocalVariableGen lvg = (LocalVariableGen)targeter;
                        if (lvg.getStart() == oldIfHandle) {
                            lvg.setStart(invertedIfHandle);
                            continue;
                        }
                        if (lvg.getEnd() != oldIfHandle) continue;
                        lvg.setEnd(gotoHandle);
                        continue;
                    }
                    targeter.updateTarget(oldIfHandle, invertedIfHandle);
                }
            }
            try {
                il.delete(oldIfHandle);
            }
            catch (TargetLostException tle) {
                String msg = new ErrorMsg("OUTLINE_ERR_DELETED_TARGET", tle.getMessage()).toString();
                throw new InternalError(msg);
            }
            ih = gotoHandle;
            ilChanged = true;
        }
        return ilChanged;
    }

    private static class Chunk
    implements Comparable {
        private InstructionHandle m_start;
        private InstructionHandle m_end;
        private int m_size;

        Chunk(InstructionHandle start, InstructionHandle end) {
            this.m_start = start;
            this.m_end = end;
            this.m_size = end.getPosition() - start.getPosition();
        }

        boolean isAdjacentTo(Chunk neighbour) {
            return this.getChunkEnd().getNext() == neighbour.getChunkStart();
        }

        InstructionHandle getChunkStart() {
            return this.m_start;
        }

        InstructionHandle getChunkEnd() {
            return this.m_end;
        }

        int getChunkSize() {
            return this.m_size;
        }

        public int compareTo(Object comparand) {
            return this.getChunkSize() - ((Chunk)comparand).getChunkSize();
        }
    }

    protected class LocalVariableRegistry {
        protected ArrayList _variables = new ArrayList();
        protected HashMap _nameToLVGMap = new HashMap();

        protected LocalVariableRegistry() {
        }

        protected void registerLocalVariable(LocalVariableGen lvg) {
            int registrySize;
            int slot = lvg.getIndex();
            if (slot >= (registrySize = this._variables.size())) {
                for (int i = registrySize; i < slot; ++i) {
                    this._variables.add(null);
                }
                this._variables.add(lvg);
            } else {
                Object localsInSlot = this._variables.get(slot);
                if (localsInSlot != null) {
                    if (localsInSlot instanceof LocalVariableGen) {
                        ArrayList listOfLocalsInSlot = new ArrayList();
                        listOfLocalsInSlot.add(localsInSlot);
                        listOfLocalsInSlot.add(lvg);
                        this._variables.set(slot, listOfLocalsInSlot);
                    } else {
                        ((ArrayList)localsInSlot).add(lvg);
                    }
                } else {
                    this._variables.set(slot, lvg);
                }
            }
            this.registerByName(lvg);
        }

        protected LocalVariableGen lookupRegisteredLocalVariable(int slot, int offset) {
            Object localsInSlot;
            Object v0 = localsInSlot = this._variables != null ? this._variables.get(slot) : null;
            if (localsInSlot != null) {
                if (localsInSlot instanceof LocalVariableGen) {
                    LocalVariableGen lvg = localsInSlot;
                    if (MethodGenerator.this.offsetInLocalVariableGenRange(lvg, offset)) {
                        return lvg;
                    }
                } else {
                    ArrayList listOfLocalsInSlot = localsInSlot;
                    int size = listOfLocalsInSlot.size();
                    for (int i = 0; i < size; ++i) {
                        LocalVariableGen lvg = (LocalVariableGen)listOfLocalsInSlot.get(i);
                        if (!MethodGenerator.this.offsetInLocalVariableGenRange(lvg, offset)) continue;
                        return lvg;
                    }
                }
            }
            return null;
        }

        protected void registerByName(LocalVariableGen lvg) {
            Object duplicateNameEntry = this._nameToLVGMap.get(lvg.getName());
            if (duplicateNameEntry == null) {
                this._nameToLVGMap.put(lvg.getName(), lvg);
            } else {
                ArrayList<LocalVariableGen> sameNameList;
                if (duplicateNameEntry instanceof ArrayList) {
                    sameNameList = (ArrayList<LocalVariableGen>)duplicateNameEntry;
                    sameNameList.add(lvg);
                } else {
                    sameNameList = new ArrayList<LocalVariableGen>();
                    sameNameList.add((LocalVariableGen)duplicateNameEntry);
                    sameNameList.add(lvg);
                }
                this._nameToLVGMap.put(lvg.getName(), sameNameList);
            }
        }

        protected void removeByNameTracking(LocalVariableGen lvg) {
            Object duplicateNameEntry = this._nameToLVGMap.get(lvg.getName());
            if (duplicateNameEntry instanceof ArrayList) {
                ArrayList sameNameList = (ArrayList)duplicateNameEntry;
                for (int i = 0; i < sameNameList.size(); ++i) {
                    if (sameNameList.get(i) != lvg) continue;
                    sameNameList.remove(i);
                    break;
                }
            } else {
                this._nameToLVGMap.remove(lvg);
            }
        }

        protected LocalVariableGen lookUpByName(String name) {
            LocalVariableGen lvg = null;
            Object duplicateNameEntry = this._nameToLVGMap.get(name);
            if (duplicateNameEntry instanceof ArrayList) {
                ArrayList sameNameList = (ArrayList)duplicateNameEntry;
                for (int i = 0; i < sameNameList.size() && (lvg = (LocalVariableGen)sameNameList.get(i)).getName() != name; ++i) {
                }
            } else {
                lvg = (LocalVariableGen)duplicateNameEntry;
            }
            return lvg;
        }

        protected LocalVariableGen[] getLocals(boolean includeRemoved) {
            LocalVariableGen[] locals = null;
            ArrayList<Object> allVarsEverDeclared = new ArrayList<Object>();
            if (includeRemoved) {
                int slotCount = allVarsEverDeclared.size();
                for (int i = 0; i < slotCount; ++i) {
                    Object slotEntries = this._variables.get(i);
                    if (slotEntries == null) continue;
                    if (slotEntries instanceof ArrayList) {
                        ArrayList slotList = (ArrayList)slotEntries;
                        for (int j = 0; j < slotList.size(); ++j) {
                            allVarsEverDeclared.add(slotList.get(i));
                        }
                        continue;
                    }
                    allVarsEverDeclared.add(slotEntries);
                }
            } else {
                for (Map.Entry nameVarsPair : this._nameToLVGMap.entrySet()) {
                    Object vars = nameVarsPair.getValue();
                    if (vars == null) continue;
                    if (vars instanceof ArrayList) {
                        ArrayList varsList = (ArrayList)vars;
                        for (int i = 0; i < varsList.size(); ++i) {
                            allVarsEverDeclared.add(varsList.get(i));
                        }
                        continue;
                    }
                    allVarsEverDeclared.add(vars);
                }
            }
            locals = new LocalVariableGen[allVarsEverDeclared.size()];
            allVarsEverDeclared.toArray(locals);
            return locals;
        }
    }
}

