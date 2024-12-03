/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.ReferenceContext;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.parser.RecoveryScannerData;
import org.eclipse.jdt.internal.compiler.util.Util;

public class CompilationResult {
    public CategorizedProblem[] problems;
    public CategorizedProblem[] tasks;
    public int problemCount;
    public int taskCount;
    public ICompilationUnit compilationUnit;
    private Map<CategorizedProblem, ReferenceContext> problemsMap;
    private Set firstErrors;
    private int maxProblemPerUnit;
    public char[][][] qualifiedReferences;
    public char[][] simpleNameReferences;
    public char[][] rootReferences;
    public boolean hasAnnotations = false;
    public boolean hasFunctionalTypes = false;
    public int[] lineSeparatorPositions;
    public RecoveryScannerData recoveryScannerData;
    public Map compiledTypes = new Hashtable(11);
    public int unitIndex;
    public int totalUnitsKnown;
    public boolean hasBeenAccepted = false;
    public char[] fileName;
    public boolean hasInconsistentToplevelHierarchies = false;
    public boolean hasSyntaxError = false;
    public char[][] packageName;
    public boolean checkSecondaryTypes = false;
    private int numberOfErrors;
    private boolean hasMandatoryErrors;
    private static final int[] EMPTY_LINE_ENDS = Util.EMPTY_INT_ARRAY;
    private static final Comparator PROBLEM_COMPARATOR = new Comparator(){

        public int compare(Object o1, Object o2) {
            return ((CategorizedProblem)o1).getSourceStart() - ((CategorizedProblem)o2).getSourceStart();
        }
    };

    public CompilationResult(char[] fileName, int unitIndex, int totalUnitsKnown, int maxProblemPerUnit) {
        this.fileName = fileName;
        this.unitIndex = unitIndex;
        this.totalUnitsKnown = totalUnitsKnown;
        this.maxProblemPerUnit = maxProblemPerUnit;
    }

    public CompilationResult(ICompilationUnit compilationUnit, int unitIndex, int totalUnitsKnown, int maxProblemPerUnit) {
        this.fileName = compilationUnit.getFileName();
        this.compilationUnit = compilationUnit;
        this.unitIndex = unitIndex;
        this.totalUnitsKnown = totalUnitsKnown;
        this.maxProblemPerUnit = maxProblemPerUnit;
    }

    private int computePriority(CategorizedProblem problem) {
        ReferenceContext context;
        int priority = 10000 - problem.getSourceLineNumber();
        if (priority < 0) {
            priority = 0;
        }
        if (problem.isError()) {
            priority += 100000;
        }
        ReferenceContext referenceContext = context = this.problemsMap == null ? null : this.problemsMap.get(problem);
        if (context != null) {
            if (context instanceof AbstractMethodDeclaration) {
                AbstractMethodDeclaration method = (AbstractMethodDeclaration)context;
                if (method.isStatic()) {
                    priority += 10000;
                }
            } else {
                priority += 40000;
            }
            if (this.firstErrors.contains(problem)) {
                priority += 20000;
            }
        } else {
            priority += 40000;
        }
        return priority;
    }

    public CategorizedProblem[] getAllProblems() {
        int onlyTaskCount;
        CategorizedProblem[] onlyProblems = this.getProblems();
        int onlyProblemCount = onlyProblems != null ? onlyProblems.length : 0;
        CategorizedProblem[] onlyTasks = this.getTasks();
        int n = onlyTaskCount = onlyTasks != null ? onlyTasks.length : 0;
        if (onlyTaskCount == 0) {
            return onlyProblems;
        }
        if (onlyProblemCount == 0) {
            return onlyTasks;
        }
        int totalNumberOfProblem = onlyProblemCount + onlyTaskCount;
        CategorizedProblem[] allProblems = new CategorizedProblem[totalNumberOfProblem];
        int allProblemIndex = 0;
        int taskIndex = 0;
        int problemIndex = 0;
        while (taskIndex + problemIndex < totalNumberOfProblem) {
            CategorizedProblem nextTask = null;
            CategorizedProblem nextProblem = null;
            if (taskIndex < onlyTaskCount) {
                nextTask = onlyTasks[taskIndex];
            }
            if (problemIndex < onlyProblemCount) {
                nextProblem = onlyProblems[problemIndex];
            }
            CategorizedProblem currentProblem = null;
            if (nextProblem != null) {
                if (nextTask != null) {
                    if (nextProblem.getSourceStart() < nextTask.getSourceStart()) {
                        currentProblem = nextProblem;
                        ++problemIndex;
                    } else {
                        currentProblem = nextTask;
                        ++taskIndex;
                    }
                } else {
                    currentProblem = nextProblem;
                    ++problemIndex;
                }
            } else if (nextTask != null) {
                currentProblem = nextTask;
                ++taskIndex;
            }
            allProblems[allProblemIndex++] = currentProblem;
        }
        return allProblems;
    }

    public ClassFile[] getClassFiles() {
        ClassFile[] classFiles = new ClassFile[this.compiledTypes.size()];
        this.compiledTypes.values().toArray(classFiles);
        return classFiles;
    }

    public ICompilationUnit getCompilationUnit() {
        return this.compilationUnit;
    }

    public CategorizedProblem[] getErrors() {
        CategorizedProblem[] reportedProblems = this.getProblems();
        int errorCount = 0;
        int i = 0;
        while (i < this.problemCount) {
            if (reportedProblems[i].isError()) {
                ++errorCount;
            }
            ++i;
        }
        if (errorCount == this.problemCount) {
            return reportedProblems;
        }
        CategorizedProblem[] errors = new CategorizedProblem[errorCount];
        int index = 0;
        int i2 = 0;
        while (i2 < this.problemCount) {
            if (reportedProblems[i2].isError()) {
                errors[index++] = reportedProblems[i2];
            }
            ++i2;
        }
        return errors;
    }

    public char[] getFileName() {
        return this.fileName;
    }

    public int[] getLineSeparatorPositions() {
        return this.lineSeparatorPositions == null ? EMPTY_LINE_ENDS : this.lineSeparatorPositions;
    }

    public CategorizedProblem[] getProblems() {
        if (this.problems != null) {
            if (this.problemCount != this.problems.length) {
                this.problems = new CategorizedProblem[this.problemCount];
                System.arraycopy(this.problems, 0, this.problems, 0, this.problemCount);
            }
            if (this.maxProblemPerUnit > 0 && this.problemCount > this.maxProblemPerUnit) {
                this.quickPrioritize(this.problems, 0, this.problemCount - 1);
                this.problemCount = this.maxProblemPerUnit;
                this.problems = new CategorizedProblem[this.problemCount];
                System.arraycopy(this.problems, 0, this.problems, 0, this.problemCount);
            }
            Arrays.sort(this.problems, 0, this.problems.length, PROBLEM_COMPARATOR);
        }
        return this.problems;
    }

    public CategorizedProblem[] getCUProblems() {
        if (this.problems != null) {
            CategorizedProblem[] filteredProblems = new CategorizedProblem[this.problemCount];
            int keep = 0;
            int i = 0;
            while (i < this.problemCount) {
                CategorizedProblem problem = this.problems[i];
                if (problem.getID() != 536871825) {
                    filteredProblems[keep++] = problem;
                } else if (this.compilationUnit != null && CharOperation.equals(this.compilationUnit.getMainTypeName(), TypeConstants.PACKAGE_INFO_NAME)) {
                    filteredProblems[keep++] = problem;
                }
                ++i;
            }
            if (keep < this.problemCount) {
                CategorizedProblem[] categorizedProblemArray = filteredProblems;
                filteredProblems = new CategorizedProblem[keep];
                System.arraycopy(categorizedProblemArray, 0, filteredProblems, 0, keep);
                this.problemCount = keep;
            }
            this.problems = filteredProblems;
            if (this.maxProblemPerUnit > 0 && this.problemCount > this.maxProblemPerUnit) {
                this.quickPrioritize(this.problems, 0, this.problemCount - 1);
                this.problemCount = this.maxProblemPerUnit;
                this.problems = new CategorizedProblem[this.problemCount];
                System.arraycopy(this.problems, 0, this.problems, 0, this.problemCount);
            }
            Arrays.sort(this.problems, 0, this.problems.length, PROBLEM_COMPARATOR);
        }
        return this.problems;
    }

    public CategorizedProblem[] getTasks() {
        if (this.tasks != null) {
            if (this.taskCount != this.tasks.length) {
                this.tasks = new CategorizedProblem[this.taskCount];
                System.arraycopy(this.tasks, 0, this.tasks, 0, this.taskCount);
            }
            Arrays.sort(this.tasks, 0, this.tasks.length, PROBLEM_COMPARATOR);
        }
        return this.tasks;
    }

    public boolean hasErrors() {
        return this.numberOfErrors != 0;
    }

    public boolean hasMandatoryErrors() {
        return this.hasMandatoryErrors;
    }

    public boolean hasProblems() {
        return this.problemCount != 0;
    }

    public boolean hasTasks() {
        return this.taskCount != 0;
    }

    public boolean hasWarnings() {
        if (this.problems != null) {
            int i = 0;
            while (i < this.problemCount) {
                if (this.problems[i].isWarning()) {
                    return true;
                }
                ++i;
            }
        }
        return false;
    }

    private void quickPrioritize(CategorizedProblem[] problemList, int left, int right) {
        if (left >= right) {
            return;
        }
        int original_left = left;
        int original_right = right;
        int mid = this.computePriority(problemList[left + (right - left) / 2]);
        while (true) {
            if (this.computePriority(problemList[right]) < mid) {
                --right;
                continue;
            }
            while (mid < this.computePriority(problemList[left])) {
                ++left;
            }
            if (left <= right) {
                CategorizedProblem tmp = problemList[left];
                problemList[left] = problemList[right];
                problemList[right] = tmp;
                ++left;
                --right;
            }
            if (left > right) break;
        }
        if (original_left < right) {
            this.quickPrioritize(problemList, original_left, right);
        }
        if (left < original_right) {
            this.quickPrioritize(problemList, left, original_right);
        }
    }

    public void recordPackageName(char[][] packName) {
        this.packageName = packName;
    }

    public void record(CategorizedProblem newProblem, ReferenceContext referenceContext) {
        this.record(newProblem, referenceContext, true);
    }

    public void record(CategorizedProblem newProblem, ReferenceContext referenceContext, boolean mandatoryError) {
        if (newProblem.getID() == 536871362) {
            this.recordTask(newProblem);
            return;
        }
        if (this.problemCount == 0) {
            this.problems = new CategorizedProblem[5];
        } else if (this.problemCount == this.problems.length) {
            this.problems = new CategorizedProblem[this.problemCount * 2];
            System.arraycopy(this.problems, 0, this.problems, 0, this.problemCount);
        }
        this.problems[this.problemCount++] = newProblem;
        if (referenceContext != null) {
            if (this.problemsMap == null) {
                this.problemsMap = new HashMap<CategorizedProblem, ReferenceContext>(5);
            }
            if (this.firstErrors == null) {
                this.firstErrors = new HashSet(5);
            }
            if (newProblem.isError() && !referenceContext.hasErrors()) {
                this.firstErrors.add(newProblem);
            }
            this.problemsMap.put(newProblem, referenceContext);
        }
        if (newProblem.isError()) {
            ++this.numberOfErrors;
            if (mandatoryError) {
                this.hasMandatoryErrors = true;
            }
            if ((newProblem.getID() & 0x40000000) != 0) {
                this.hasSyntaxError = true;
            }
        }
    }

    ReferenceContext getContext(CategorizedProblem problem) {
        if (problem != null) {
            return this.problemsMap.get(problem);
        }
        return null;
    }

    public void record(char[] typeName, ClassFile classFile) {
        SourceTypeBinding sourceType = classFile.referenceBinding;
        if (sourceType != null && !sourceType.isLocalType() && sourceType.isHierarchyInconsistent()) {
            this.hasInconsistentToplevelHierarchies = true;
        }
        this.compiledTypes.put(typeName, classFile);
    }

    private void recordTask(CategorizedProblem newProblem) {
        if (this.taskCount == 0) {
            this.tasks = new CategorizedProblem[5];
        } else if (this.taskCount == this.tasks.length) {
            this.tasks = new CategorizedProblem[this.taskCount * 2];
            System.arraycopy(this.tasks, 0, this.tasks, 0, this.taskCount);
        }
        this.tasks[this.taskCount++] = newProblem;
    }

    public void removeProblem(CategorizedProblem problem) {
        if (this.problemsMap != null) {
            this.problemsMap.remove(problem);
        }
        if (this.firstErrors != null) {
            this.firstErrors.remove(problem);
        }
        if (problem.isError()) {
            --this.numberOfErrors;
        }
        --this.problemCount;
    }

    public CompilationResult tagAsAccepted() {
        this.hasBeenAccepted = true;
        this.problemsMap = null;
        this.firstErrors = null;
        return this;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if (this.fileName != null) {
            buffer.append("Filename : ").append(this.fileName).append('\n');
        }
        if (this.compiledTypes != null) {
            buffer.append("COMPILED type(s)\t\n");
            for (char[] typeName : this.compiledTypes.keySet()) {
                buffer.append("\t - ").append(typeName).append('\n');
            }
        } else {
            buffer.append("No COMPILED type\n");
        }
        if (this.problems != null) {
            buffer.append(this.problemCount).append(" PROBLEM(s) detected \n");
            int i = 0;
            while (i < this.problemCount) {
                buffer.append("\t - ").append(this.problems[i]).append('\n');
                ++i;
            }
        } else {
            buffer.append("No PROBLEM\n");
        }
        return buffer.toString();
    }
}

