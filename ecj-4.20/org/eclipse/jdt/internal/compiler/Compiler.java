/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IDebugRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ProcessTaskManager;
import org.eclipse.jdt.internal.compiler.ReadManager;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.env.ISourceType;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.impl.CompilerStats;
import org.eclipse.jdt.internal.compiler.impl.ITypeRequestor;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeCollisionException;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.Parser;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilationUnit;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem;
import org.eclipse.jdt.internal.compiler.problem.ProblemReporter;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;
import org.eclipse.jdt.internal.compiler.util.Messages;
import org.eclipse.jdt.internal.compiler.util.Util;

public class Compiler
implements ITypeRequestor,
ProblemSeverities {
    public Parser parser;
    public ICompilerRequestor requestor;
    public CompilerOptions options;
    public ProblemReporter problemReporter;
    protected PrintWriter out;
    public CompilerStats stats;
    public CompilationProgress progress;
    public int remainingIterations = 1;
    public CompilationUnitDeclaration[] unitsToProcess;
    public int totalUnits;
    private Map<String, APTProblem[]> aptProblems;
    public LookupEnvironment lookupEnvironment;
    public static boolean DEBUG = false;
    public int parseThreshold = -1;
    public AbstractAnnotationProcessorManager annotationProcessorManager;
    public int annotationProcessorStartIndex = 0;
    public ReferenceBinding[] referenceBindings;
    public boolean useSingleThread = true;
    public static IDebugRequestor DebugRequestor = null;

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, Map<String, String> settings, ICompilerRequestor requestor, IProblemFactory problemFactory) {
        this(environment, policy, new CompilerOptions(settings), requestor, problemFactory, null, null);
    }

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, Map settings, ICompilerRequestor requestor, IProblemFactory problemFactory, boolean parseLiteralExpressionsAsConstants) {
        this(environment, policy, new CompilerOptions(settings, parseLiteralExpressionsAsConstants), requestor, problemFactory, null, null);
    }

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory) {
        this(environment, policy, options, requestor, problemFactory, null, null);
    }

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter out) {
        this(environment, policy, options, requestor, problemFactory, out, null);
    }

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options, final ICompilerRequestor requestor, IProblemFactory problemFactory, PrintWriter out, CompilationProgress progress) {
        this.options = options;
        this.progress = progress;
        this.requestor = DebugRequestor == null ? requestor : new ICompilerRequestor(){

            @Override
            public void acceptResult(CompilationResult result) {
                if (DebugRequestor.isActive()) {
                    DebugRequestor.acceptDebugResult(result);
                }
                requestor.acceptResult(result);
            }
        };
        this.problemReporter = new ProblemReporter(policy, this.options, problemFactory);
        this.lookupEnvironment = new LookupEnvironment(this, this.options, this.problemReporter, environment);
        this.out = out == null ? new PrintWriter(System.out, true) : out;
        this.stats = new CompilerStats();
        this.initializeParser();
    }

    @Override
    public void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        if (this.options.verbose) {
            this.out.println(Messages.bind(Messages.compilation_loadBinary, new String(binaryType.getName())));
        }
        LookupEnvironment env = packageBinding.environment;
        env.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
    }

    @Override
    public void accept(ICompilationUnit sourceUnit, AccessRestriction accessRestriction) {
        CompilationResult unitResult = new CompilationResult(sourceUnit, this.totalUnits, this.totalUnits, this.options.maxProblemsPerUnit);
        unitResult.checkSecondaryTypes = true;
        try {
            if (this.options.verbose) {
                String count = String.valueOf(this.totalUnits + 1);
                this.out.println(Messages.bind(Messages.compilation_request, new String[]{count, count, new String(sourceUnit.getFileName())}));
            }
            CompilationUnitDeclaration parsedUnit = this.totalUnits < this.parseThreshold ? this.parser.parse(sourceUnit, unitResult) : this.parser.dietParse(sourceUnit, unitResult);
            this.lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
            this.addCompilationUnit(sourceUnit, parsedUnit);
            this.lookupEnvironment.completeTypeBindings(parsedUnit);
        }
        catch (AbortCompilationUnit e) {
            if (unitResult.compilationUnit == sourceUnit) {
                this.requestor.acceptResult(unitResult.tagAsAccepted());
            }
            throw e;
        }
    }

    @Override
    public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        this.problemReporter.abortDueToInternalError(Messages.bind(Messages.abort_againstSourceModel, new String[]{String.valueOf(sourceTypes[0].getName()), String.valueOf(sourceTypes[0].getFileName())}));
    }

    protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit) {
        if (this.unitsToProcess == null) {
            return;
        }
        int size = this.unitsToProcess.length;
        if (this.totalUnits == size) {
            this.unitsToProcess = new CompilationUnitDeclaration[size * 2];
            System.arraycopy(this.unitsToProcess, 0, this.unitsToProcess, 0, this.totalUnits);
        }
        this.unitsToProcess[this.totalUnits++] = parsedUnit;
    }

    protected void beginToCompile(ICompilationUnit[] sourceUnits) {
        int maxUnits = sourceUnits.length;
        this.totalUnits = 0;
        this.unitsToProcess = new CompilationUnitDeclaration[maxUnits];
        this.internalBeginToCompile(sourceUnits, maxUnits);
    }

    protected void reportProgress(String taskDecription) {
        if (this.progress != null) {
            if (this.progress.isCanceled()) {
                throw new AbortCompilation(true, null);
            }
            this.progress.setTaskName(taskDecription);
        }
    }

    protected void reportWorked(int workIncrement, int currentUnitIndex) {
        if (this.progress != null) {
            if (this.progress.isCanceled()) {
                throw new AbortCompilation(true, null);
            }
            this.progress.worked(workIncrement, this.totalUnits * this.remainingIterations - currentUnitIndex - 1);
        }
    }

    public void compile(ICompilationUnit[] sourceUnits) {
        this.compile(sourceUnits, false);
    }

    private void compile(ICompilationUnit[] sourceUnits, boolean lastRound) {
        this.stats.startTime = System.currentTimeMillis();
        try {
            this.reportProgress(Messages.compilation_beginningToCompile);
            if (this.options.complianceLevel >= 0x350000L) {
                this.sortModuleDeclarationsFirst(sourceUnits);
            }
            if (this.annotationProcessorManager == null) {
                this.beginToCompile(sourceUnits);
            } else {
                ICompilationUnit[] originalUnits = (ICompilationUnit[])sourceUnits.clone();
                try {
                    this.beginToCompile(sourceUnits);
                    if (!lastRound) {
                        this.processAnnotations();
                    }
                    if (!this.options.generateClassFiles) {
                        return;
                    }
                }
                catch (SourceTypeCollisionException e) {
                    this.backupAptProblems();
                    this.reset();
                    int originalLength = originalUnits.length;
                    int newProcessedLength = e.newAnnotationProcessorUnits.length;
                    ICompilationUnit[] combinedUnits = new ICompilationUnit[originalLength + newProcessedLength];
                    System.arraycopy(originalUnits, 0, combinedUnits, 0, originalLength);
                    System.arraycopy(e.newAnnotationProcessorUnits, 0, combinedUnits, originalLength, newProcessedLength);
                    this.annotationProcessorStartIndex = originalLength;
                    this.compile(combinedUnits, e.isLastRound);
                    return;
                }
            }
            this.restoreAptProblems();
            this.processCompiledUnits(0, lastRound);
        }
        catch (AbortCompilation e) {
            this.handleInternalException(e, null);
        }
        if (this.options.verbose) {
            if (this.totalUnits > 1) {
                this.out.println(Messages.bind(Messages.compilation_units, String.valueOf(this.totalUnits)));
            } else {
                this.out.println(Messages.bind(Messages.compilation_unit, String.valueOf(this.totalUnits)));
            }
        }
    }

    private void sortModuleDeclarationsFirst(ICompilationUnit[] sourceUnits) {
        Arrays.sort(sourceUnits, (u1, u2) -> {
            boolean isMod2;
            char[] fn1 = u1.getFileName();
            char[] fn2 = u2.getFileName();
            boolean isMod1 = CharOperation.endsWith(fn1, TypeConstants.MODULE_INFO_FILE_NAME) || CharOperation.endsWith(fn1, TypeConstants.MODULE_INFO_CLASS_NAME);
            boolean bl = isMod2 = CharOperation.endsWith(fn2, TypeConstants.MODULE_INFO_FILE_NAME) || CharOperation.endsWith(fn2, TypeConstants.MODULE_INFO_CLASS_NAME);
            if (isMod1 == isMod2) {
                return 0;
            }
            return isMod1 ? -1 : 1;
        });
    }

    protected void backupAptProblems() {
        if (this.unitsToProcess == null) {
            return;
        }
        int i = 0;
        while (i < this.totalUnits) {
            CompilationUnitDeclaration unitDecl = this.unitsToProcess[i];
            CompilationResult result = unitDecl.compilationResult;
            if (result != null && result.hasErrors()) {
                CategorizedProblem[] errors;
                CategorizedProblem[] categorizedProblemArray = errors = result.getErrors();
                int n = errors.length;
                int n2 = 0;
                while (n2 < n) {
                    CategorizedProblem problem = categorizedProblemArray[n2];
                    if (problem.getCategoryID() == 0) {
                        APTProblem[] problems;
                        if (this.aptProblems == null) {
                            this.aptProblems = new HashMap<String, APTProblem[]>();
                        }
                        if ((problems = this.aptProblems.get(new String(unitDecl.getFileName()))) == null) {
                            this.aptProblems.put(new String(unitDecl.getFileName()), new APTProblem[]{new APTProblem(problem, result.getContext(problem))});
                        } else {
                            APTProblem[] temp = new APTProblem[problems.length + 1];
                            System.arraycopy(problems, 0, temp, 0, problems.length);
                            temp[problems.length] = new APTProblem(problem, result.getContext(problem));
                            this.aptProblems.put(new String(unitDecl.getFileName()), temp);
                        }
                    }
                    ++n2;
                }
            }
            ++i;
        }
    }

    protected void restoreAptProblems() {
        if (this.unitsToProcess != null && this.aptProblems != null) {
            int i = 0;
            while (i < this.totalUnits) {
                CompilationUnitDeclaration unitDecl = this.unitsToProcess[i];
                APTProblem[] problems = this.aptProblems.get(new String(unitDecl.getFileName()));
                if (problems != null) {
                    APTProblem[] aPTProblemArray = problems;
                    int n = problems.length;
                    int n2 = 0;
                    while (n2 < n) {
                        APTProblem problem = aPTProblemArray[n2];
                        unitDecl.compilationResult.record(problem.problem, problem.context);
                        ++n2;
                    }
                }
                ++i;
            }
        }
        this.aptProblems = null;
    }

    protected void processCompiledUnits(int startingIndex, boolean lastRound) throws Error {
        CompilationUnitDeclaration unit = null;
        ProcessTaskManager processingTask = null;
        try {
            try {
                if (this.useSingleThread) {
                    int i = startingIndex;
                    while (i < this.totalUnits) {
                        unit = this.unitsToProcess[i];
                        if (unit.compilationResult == null || !unit.compilationResult.hasBeenAccepted) {
                            this.reportProgress(Messages.bind(Messages.compilation_processing, new String(unit.getFileName())));
                            try {
                                if (this.options.verbose) {
                                    this.out.println(Messages.bind(Messages.compilation_process, new String[]{String.valueOf(i + 1), String.valueOf(this.totalUnits), new String(this.unitsToProcess[i].getFileName())}));
                                }
                                this.process(unit, i);
                            }
                            finally {
                                if (this.annotationProcessorManager == null || this.shouldCleanup(i)) {
                                    unit.cleanUp();
                                }
                            }
                            if (this.annotationProcessorManager == null) {
                                this.unitsToProcess[i] = null;
                            }
                            this.reportWorked(1, i);
                            this.stats.lineCount += (long)unit.compilationResult.lineSeparatorPositions.length;
                            long acceptStart = System.currentTimeMillis();
                            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
                            this.stats.generateTime += System.currentTimeMillis() - acceptStart;
                            if (this.options.verbose) {
                                this.out.println(Messages.bind(Messages.compilation_done, new String[]{String.valueOf(i + 1), String.valueOf(this.totalUnits), new String(unit.getFileName())}));
                            }
                        }
                        ++i;
                    }
                } else {
                    processingTask = new ProcessTaskManager(this, startingIndex);
                    int acceptedCount = 0;
                    while (true) {
                        try {
                            unit = processingTask.removeNextUnit();
                        }
                        catch (Error | RuntimeException e) {
                            unit = processingTask.unitToProcess;
                            throw e;
                        }
                        if (unit == null) break;
                        this.reportWorked(1, acceptedCount++);
                        this.stats.lineCount += (long)unit.compilationResult.lineSeparatorPositions.length;
                        this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
                        if (!this.options.verbose) continue;
                        this.out.println(Messages.bind(Messages.compilation_done, new String[]{String.valueOf(acceptedCount), String.valueOf(this.totalUnits), new String(unit.getFileName())}));
                    }
                }
                if (!lastRound && this.annotationProcessorManager != null && this.totalUnits > this.annotationProcessorStartIndex) {
                    int backup = this.annotationProcessorStartIndex;
                    int prevUnits = this.totalUnits;
                    this.processAnnotations();
                    int i = backup;
                    while (i < prevUnits) {
                        this.unitsToProcess[i].cleanUp();
                        ++i;
                    }
                    this.processCompiledUnits(backup, lastRound);
                }
            }
            catch (AbortCompilation e) {
                this.handleInternalException(e, unit);
                if (processingTask != null) {
                    processingTask.shutdown();
                    processingTask = null;
                }
                this.reset();
                this.annotationProcessorStartIndex = 0;
                this.stats.endTime = System.currentTimeMillis();
                this.stats.overallTime += this.stats.endTime - this.stats.startTime;
            }
            catch (Error | RuntimeException e) {
                this.handleInternalException(e, unit, null);
                throw e;
            }
        }
        finally {
            if (processingTask != null) {
                processingTask.shutdown();
                processingTask = null;
            }
            this.reset();
            this.annotationProcessorStartIndex = 0;
            this.stats.endTime = System.currentTimeMillis();
            this.stats.overallTime += this.stats.endTime - this.stats.startTime;
        }
    }

    public synchronized CompilationUnitDeclaration getUnitToProcess(int next) {
        if (next < this.totalUnits) {
            CompilationUnitDeclaration unit = this.unitsToProcess[next];
            if (this.annotationProcessorManager == null || next < this.annotationProcessorStartIndex) {
                this.unitsToProcess[next] = null;
            }
            return unit;
        }
        return null;
    }

    public boolean shouldCleanup(int index) {
        return index < this.annotationProcessorStartIndex;
    }

    public void setBinaryTypes(ReferenceBinding[] binaryTypes) {
        this.referenceBindings = binaryTypes;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleInternalException(Throwable internalException, CompilationUnitDeclaration unit, CompilationResult result) {
        if (result == null && unit != null) {
            result = unit.compilationResult;
        }
        if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
            result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
        }
        if (result == null) {
            Compiler compiler = this;
            synchronized (compiler) {
                if (this.unitsToProcess != null && this.totalUnits > 0) {
                    result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                }
            }
        }
        boolean needToPrint = true;
        if (result != null) {
            String[] pbArguments = new String[]{Messages.bind(Messages.compilation_internalError, Util.getExceptionSummary(internalException))};
            result.record(this.problemReporter.createProblem(result.getFileName(), 0, pbArguments, pbArguments, 1, 0, 0, 0, 0), unit, true);
            if (!result.hasBeenAccepted) {
                this.requestor.acceptResult(result.tagAsAccepted());
                needToPrint = false;
            }
        }
        if (needToPrint) {
            internalException.printStackTrace();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void handleInternalException(AbortCompilation abortException, CompilationUnitDeclaration unit) {
        if (abortException.isSilent) {
            if (abortException.silentException == null) {
                return;
            }
            throw abortException.silentException;
        }
        CompilationResult result = abortException.compilationResult;
        if (result == null && unit != null) {
            result = unit.compilationResult;
        }
        if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
            result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
        }
        if (result == null) {
            Compiler compiler = this;
            synchronized (compiler) {
                if (this.unitsToProcess != null && this.totalUnits > 0) {
                    result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                }
            }
        }
        if (result != null && !result.hasBeenAccepted) {
            block18: {
                if (abortException.problem != null) {
                    CategorizedProblem distantProblem = abortException.problem;
                    CategorizedProblem[] knownProblems = result.problems;
                    int i = 0;
                    while (i < result.problemCount) {
                        if (knownProblems[i] != distantProblem) {
                            ++i;
                            continue;
                        }
                        break block18;
                    }
                    if (distantProblem instanceof DefaultProblem) {
                        ((DefaultProblem)distantProblem).setOriginatingFileName(result.getFileName());
                    }
                    result.record(distantProblem, unit, true);
                } else if (abortException.exception != null) {
                    this.handleInternalException(abortException.exception, null, result);
                    return;
                }
            }
            if (!result.hasBeenAccepted) {
                this.requestor.acceptResult(result.tagAsAccepted());
            }
        } else {
            abortException.printStackTrace();
        }
    }

    public void initializeParser() {
        this.parser = new Parser(this.problemReporter, this.options.parseLiteralExpressionsAsConstants);
    }

    private void abortIfPreviewNotAllowed(ICompilationUnit[] sourceUnits, int maxUnits) {
        if (!this.options.enablePreviewFeatures) {
            return;
        }
        try {
            if (this.options.sourceLevel != ClassFileConstants.getLatestJDKLevel()) {
                this.problemReporter.abortDueToPreviewEnablingNotAllowed(CompilerOptions.versionFromJdkLevel(this.options.sourceLevel), CompilerOptions.getLatestVersion());
            }
        }
        catch (AbortCompilation a) {
            if (a.compilationResult == null) {
                a.compilationResult = new CompilationResult(sourceUnits[0], 0, maxUnits, this.options.maxProblemsPerUnit);
            }
            throw a;
        }
    }

    protected void internalBeginToCompile(ICompilationUnit[] sourceUnits, int maxUnits) {
        this.abortIfPreviewNotAllowed(sourceUnits, maxUnits);
        if (!this.useSingleThread && maxUnits >= 10) {
            this.parser.readManager = new ReadManager(sourceUnits, maxUnits);
        }
        int i = 0;
        while (i < maxUnits) {
            CompilationResult unitResult = null;
            try {
                try {
                    if (this.options.verbose) {
                        this.out.println(Messages.bind(Messages.compilation_request, new String[]{String.valueOf(i + 1), String.valueOf(maxUnits), new String(sourceUnits[i].getFileName())}));
                    }
                    unitResult = new CompilationResult(sourceUnits[i], i, maxUnits, this.options.maxProblemsPerUnit);
                    long parseStart = System.currentTimeMillis();
                    CompilationUnitDeclaration parsedUnit = this.totalUnits < this.parseThreshold ? this.parser.parse(sourceUnits[i], unitResult) : this.parser.dietParse(sourceUnits[i], unitResult);
                    long resolveStart = System.currentTimeMillis();
                    this.stats.parseTime += resolveStart - parseStart;
                    this.lookupEnvironment.buildTypeBindings(parsedUnit, null);
                    this.stats.resolveTime += System.currentTimeMillis() - resolveStart;
                    this.addCompilationUnit(sourceUnits[i], parsedUnit);
                    ImportReference currentPackage = parsedUnit.currentPackage;
                    if (currentPackage != null) {
                        unitResult.recordPackageName(currentPackage.tokens);
                    }
                }
                catch (AbortCompilation a) {
                    if (a.compilationResult == null) {
                        a.compilationResult = unitResult;
                    }
                    throw a;
                }
            }
            finally {
                sourceUnits[i] = null;
            }
            ++i;
        }
        if (this.parser.readManager != null) {
            this.parser.readManager.shutdown();
            this.parser.readManager = null;
        }
        this.lookupEnvironment.completeTypeBindings();
    }

    public void process(CompilationUnitDeclaration unit, int i) {
        this.lookupEnvironment.unitBeingCompleted = unit;
        long parseStart = System.currentTimeMillis();
        this.parser.getMethodBodies(unit);
        long resolveStart = System.currentTimeMillis();
        this.stats.parseTime += resolveStart - parseStart;
        if (unit.scope != null) {
            unit.scope.faultInTypes();
        }
        if (unit.scope != null) {
            unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
        }
        unit.resolve();
        long analyzeStart = System.currentTimeMillis();
        this.stats.resolveTime += analyzeStart - resolveStart;
        if (!this.options.ignoreMethodBodies) {
            unit.analyseCode();
        }
        long generateStart = System.currentTimeMillis();
        this.stats.analyzeTime += generateStart - analyzeStart;
        if (!this.options.ignoreMethodBodies) {
            unit.generateCode();
        }
        if (this.options.produceReferenceInfo && unit.scope != null) {
            unit.scope.storeDependencyInfo();
        }
        unit.finalizeProblems();
        this.stats.generateTime += System.currentTimeMillis() - generateStart;
        unit.compilationResult.totalUnitsKnown = this.totalUnits;
        this.lookupEnvironment.unitBeingCompleted = null;
    }

    protected void processAnnotations() {
        block17: {
            int newUnitSize = 0;
            int newClassFilesSize = 0;
            int bottom = this.annotationProcessorStartIndex;
            int top = this.totalUnits;
            ReferenceBinding[] binaryTypeBindingsTemp = this.referenceBindings;
            if (top == 0 && binaryTypeBindingsTemp == null) {
                return;
            }
            this.referenceBindings = null;
            do {
                int length = top - bottom;
                CompilationUnitDeclaration[] currentUnits = new CompilationUnitDeclaration[length];
                int index = 0;
                int i = bottom;
                while (i < top) {
                    CompilationUnitDeclaration currentUnit = this.unitsToProcess[i];
                    currentUnits[index++] = currentUnit;
                    ++i;
                }
                if (index != length) {
                    CompilationUnitDeclaration[] compilationUnitDeclarationArray = currentUnits;
                    currentUnits = new CompilationUnitDeclaration[index];
                    System.arraycopy(compilationUnitDeclarationArray, 0, currentUnits, 0, index);
                }
                this.annotationProcessorManager.processAnnotations(currentUnits, binaryTypeBindingsTemp, false);
                if (top < this.totalUnits) {
                    length = this.totalUnits - top;
                    CompilationUnitDeclaration[] addedUnits = new CompilationUnitDeclaration[length];
                    System.arraycopy(this.unitsToProcess, top, addedUnits, 0, length);
                    this.annotationProcessorManager.processAnnotations(addedUnits, binaryTypeBindingsTemp, false);
                }
                this.annotationProcessorStartIndex = top;
                ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
                newUnitSize = newUnits.length;
                ReferenceBinding[] newClassFiles = this.annotationProcessorManager.getNewClassFiles();
                binaryTypeBindingsTemp = newClassFiles;
                newClassFilesSize = newClassFiles.length;
                if (newUnitSize != 0) {
                    ICompilationUnit[] newProcessedUnits = (ICompilationUnit[])newUnits.clone();
                    try {
                        try {
                            this.lookupEnvironment.isProcessingAnnotations = true;
                            this.internalBeginToCompile(newUnits, newUnitSize);
                        }
                        catch (SourceTypeCollisionException e) {
                            e.newAnnotationProcessorUnits = newProcessedUnits;
                            throw e;
                        }
                    }
                    finally {
                        this.lookupEnvironment.isProcessingAnnotations = false;
                        this.annotationProcessorManager.reset();
                    }
                    bottom = top;
                    this.annotationProcessorStartIndex = top = this.totalUnits;
                    continue;
                }
                bottom = top;
                this.annotationProcessorManager.reset();
            } while (newUnitSize != 0 || newClassFilesSize != 0);
            this.annotationProcessorManager.processAnnotations(null, null, true);
            ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
            newUnitSize = newUnits.length;
            try {
                if (newUnitSize == 0) break block17;
                ICompilationUnit[] newProcessedUnits = (ICompilationUnit[])newUnits.clone();
                try {
                    this.lookupEnvironment.isProcessingAnnotations = true;
                    this.internalBeginToCompile(newUnits, newUnitSize);
                }
                catch (SourceTypeCollisionException e) {
                    e.isLastRound = true;
                    e.newAnnotationProcessorUnits = newProcessedUnits;
                    throw e;
                }
            }
            finally {
                this.lookupEnvironment.isProcessingAnnotations = false;
                this.annotationProcessorManager.reset();
                this.annotationProcessorManager.cleanUp();
            }
        }
        this.annotationProcessorStartIndex = this.totalUnits;
    }

    public void reset() {
        this.lookupEnvironment.reset();
        this.parser.scanner.source = null;
        this.unitsToProcess = null;
        if (DebugRequestor != null) {
            DebugRequestor.reset();
        }
        this.problemReporter.reset();
    }

    public CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit, ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode) {
        try {
            if (unit == null) {
                this.parseThreshold = 0;
                this.beginToCompile(new ICompilationUnit[]{sourceUnit});
                int i = 0;
                while (i < this.totalUnits) {
                    if (this.unitsToProcess[i] != null && this.unitsToProcess[i].compilationResult.compilationUnit == sourceUnit) {
                        unit = this.unitsToProcess[i];
                        break;
                    }
                    ++i;
                }
                if (unit == null) {
                    unit = this.unitsToProcess[0];
                }
            } else {
                this.lookupEnvironment.buildTypeBindings(unit, null);
                this.lookupEnvironment.completeTypeBindings();
            }
            this.lookupEnvironment.unitBeingCompleted = unit;
            this.parser.getMethodBodies(unit);
            if (unit.scope != null) {
                unit.scope.faultInTypes();
                if (unit.scope != null && verifyMethods) {
                    unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
                }
                unit.resolve();
                if (analyzeCode) {
                    unit.analyseCode();
                }
                if (generateCode) {
                    unit.generateCode();
                }
                unit.finalizeProblems();
            }
            if (this.unitsToProcess != null) {
                this.unitsToProcess[0] = null;
            }
            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            return unit;
        }
        catch (AbortCompilation e) {
            this.handleInternalException(e, unit);
            return unit == null ? this.unitsToProcess[0] : unit;
        }
        catch (Error | RuntimeException e) {
            this.handleInternalException(e, unit, null);
            throw e;
        }
    }

    public CompilationUnitDeclaration resolve(ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode) {
        return this.resolve(null, sourceUnit, verifyMethods, analyzeCode, generateCode);
    }

    static class APTProblem {
        CategorizedProblem problem;
        ReferenceContext context;

        APTProblem(CategorizedProblem problem, ReferenceContext context) {
            this.problem = problem;
            this.context = context;
        }
    }
}

