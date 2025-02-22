/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import java.util.Dictionary;
import java.util.Vector;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.LocationPathPattern;
import org.apache.xalan.xsltc.compiler.Mode;
import org.apache.xalan.xsltc.compiler.Pattern;
import org.apache.xalan.xsltc.compiler.Template;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;

final class TestSeq {
    private int _kernelType;
    private Vector _patterns = null;
    private Mode _mode = null;
    private Template _default = null;
    private InstructionList _instructionList;
    private InstructionHandle _start = null;

    public TestSeq(Vector patterns, Mode mode) {
        this(patterns, -2, mode);
    }

    public TestSeq(Vector patterns, int kernelType, Mode mode) {
        this._patterns = patterns;
        this._kernelType = kernelType;
        this._mode = mode;
    }

    public String toString() {
        int count = this._patterns.size();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < count; ++i) {
            LocationPathPattern pattern = (LocationPathPattern)this._patterns.elementAt(i);
            if (i == 0) {
                result.append("Testseq for kernel " + this._kernelType).append('\n');
            }
            result.append("   pattern " + i + ": ").append(pattern.toString()).append('\n');
        }
        return result.toString();
    }

    public InstructionList getInstructionList() {
        return this._instructionList;
    }

    public double getPriority() {
        Template template = this._patterns.size() == 0 ? this._default : ((Pattern)this._patterns.elementAt(0)).getTemplate();
        return template.getPriority();
    }

    public int getPosition() {
        Template template = this._patterns.size() == 0 ? this._default : ((Pattern)this._patterns.elementAt(0)).getTemplate();
        return template.getPosition();
    }

    public void reduce() {
        Vector<LocationPathPattern> newPatterns = new Vector<LocationPathPattern>();
        int count = this._patterns.size();
        for (int i = 0; i < count; ++i) {
            LocationPathPattern pattern = (LocationPathPattern)this._patterns.elementAt(i);
            pattern.reduceKernelPattern();
            if (pattern.isWildcard()) {
                this._default = pattern.getTemplate();
                break;
            }
            newPatterns.addElement(pattern);
        }
        this._patterns = newPatterns;
    }

    public void findTemplates(Dictionary templates) {
        if (this._default != null) {
            templates.put(this._default, this);
        }
        for (int i = 0; i < this._patterns.size(); ++i) {
            LocationPathPattern pattern = (LocationPathPattern)this._patterns.elementAt(i);
            templates.put(pattern.getTemplate(), this);
        }
    }

    private InstructionHandle getTemplateHandle(Template template) {
        return this._mode.getTemplateInstructionHandle(template);
    }

    private LocationPathPattern getPattern(int n) {
        return (LocationPathPattern)this._patterns.elementAt(n);
    }

    public InstructionHandle compile(ClassGenerator classGen, MethodGenerator methodGen, InstructionHandle continuation) {
        if (this._start != null) {
            return this._start;
        }
        int count = this._patterns.size();
        if (count == 0) {
            this._start = this.getTemplateHandle(this._default);
            return this._start;
        }
        InstructionHandle fail = this._default == null ? continuation : this.getTemplateHandle(this._default);
        for (int n = count - 1; n >= 0; --n) {
            FlowList falseList;
            LocationPathPattern pattern = this.getPattern(n);
            Template template = pattern.getTemplate();
            InstructionList il = new InstructionList();
            il.append(methodGen.loadCurrentNode());
            InstructionList ilist = methodGen.getInstructionList(pattern);
            if (ilist == null) {
                ilist = pattern.compile(classGen, methodGen);
                methodGen.addInstructionList(pattern, ilist);
            }
            InstructionList copyOfilist = ilist.copy();
            FlowList trueList = pattern.getTrueList();
            if (trueList != null) {
                trueList = trueList.copyAndRedirect(ilist, copyOfilist);
            }
            if ((falseList = pattern.getFalseList()) != null) {
                falseList = falseList.copyAndRedirect(ilist, copyOfilist);
            }
            il.append(copyOfilist);
            InstructionHandle gtmpl = this.getTemplateHandle(template);
            BranchHandle success = il.append(new GOTO_W(gtmpl));
            if (trueList != null) {
                trueList.backPatch(success);
            }
            if (falseList != null) {
                falseList.backPatch(fail);
            }
            fail = il.getStart();
            if (this._instructionList != null) {
                il.append(this._instructionList);
            }
            this._instructionList = il;
        }
        this._start = fail;
        return this._start;
    }
}

