/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Redirector;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Mapper;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.util.MergingMapper;

public class RedirectorElement
extends DataType {
    private boolean usingInput = false;
    private boolean usingOutput = false;
    private boolean usingError = false;
    private Boolean logError;
    private String outputProperty;
    private String errorProperty;
    private String inputString;
    private Boolean append;
    private Boolean alwaysLog;
    private Boolean createEmptyFiles;
    private Mapper inputMapper;
    private Mapper outputMapper;
    private Mapper errorMapper;
    private Vector<FilterChain> inputFilterChains = new Vector();
    private Vector<FilterChain> outputFilterChains = new Vector();
    private Vector<FilterChain> errorFilterChains = new Vector();
    private String outputEncoding;
    private String errorEncoding;
    private String inputEncoding;
    private Boolean logInputString;
    private boolean outputIsBinary = false;

    public void addConfiguredInputMapper(Mapper inputMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.inputMapper != null) {
            if (this.usingInput) {
                throw new BuildException("attribute \"input\" cannot coexist with a nested <inputmapper>");
            }
            throw new BuildException("Cannot have > 1 <inputmapper>");
        }
        this.setChecked(false);
        this.inputMapper = inputMapper;
    }

    public void addConfiguredOutputMapper(Mapper outputMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.outputMapper != null) {
            if (this.usingOutput) {
                throw new BuildException("attribute \"output\" cannot coexist with a nested <outputmapper>");
            }
            throw new BuildException("Cannot have > 1 <outputmapper>");
        }
        this.setChecked(false);
        this.outputMapper = outputMapper;
    }

    public void addConfiguredErrorMapper(Mapper errorMapper) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        if (this.errorMapper != null) {
            if (this.usingError) {
                throw new BuildException("attribute \"error\" cannot coexist with a nested <errormapper>");
            }
            throw new BuildException("Cannot have > 1 <errormapper>");
        }
        this.setChecked(false);
        this.errorMapper = errorMapper;
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.usingInput || this.usingOutput || this.usingError || this.inputString != null || this.logError != null || this.append != null || this.createEmptyFiles != null || this.inputEncoding != null || this.outputEncoding != null || this.errorEncoding != null || this.outputProperty != null || this.errorProperty != null || this.logInputString != null) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public void setInput(File input) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.inputString != null) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.usingInput = true;
        this.inputMapper = this.createMergeMapper(input);
    }

    public void setInputString(String inputString) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.usingInput) {
            throw new BuildException("The \"input\" and \"inputstring\" attributes cannot both be specified");
        }
        this.inputString = inputString;
    }

    public void setLogInputString(boolean logInputString) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.logInputString = logInputString ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setOutput(File out) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (out == null) {
            throw new IllegalArgumentException("output file specified as null");
        }
        this.usingOutput = true;
        this.outputMapper = this.createMergeMapper(out);
    }

    public void setOutputEncoding(String outputEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.outputEncoding = outputEncoding;
    }

    public void setErrorEncoding(String errorEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.errorEncoding = errorEncoding;
    }

    public void setInputEncoding(String inputEncoding) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.inputEncoding = inputEncoding;
    }

    public void setLogError(boolean logError) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.logError = logError ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setError(File error) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (error == null) {
            throw new IllegalArgumentException("error file specified as null");
        }
        this.usingError = true;
        this.errorMapper = this.createMergeMapper(error);
    }

    public void setOutputProperty(String outputProperty) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.outputProperty = outputProperty;
    }

    public void setAppend(boolean append) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.append = append ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setAlwaysLog(boolean alwaysLog) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.alwaysLog = alwaysLog ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setCreateEmptyFiles(boolean createEmptyFiles) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createEmptyFiles = createEmptyFiles ? Boolean.TRUE : Boolean.FALSE;
    }

    public void setErrorProperty(String errorProperty) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.errorProperty = errorProperty;
    }

    public FilterChain createInputFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.inputFilterChains.add(result);
        this.setChecked(false);
        return result;
    }

    public FilterChain createOutputFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.outputFilterChains.add(result);
        this.setChecked(false);
        return result;
    }

    public FilterChain createErrorFilterChain() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        FilterChain result = new FilterChain();
        result.setProject(this.getProject());
        this.errorFilterChains.add(result);
        this.setChecked(false);
        return result;
    }

    public void setBinaryOutput(boolean b) {
        this.outputIsBinary = b;
    }

    public void configure(Redirector redirector) {
        this.configure(redirector, null);
    }

    public void configure(Redirector redirector, String sourcefile) {
        if (this.isReference()) {
            this.getRef().configure(redirector, sourcefile);
            return;
        }
        this.dieOnCircularReference();
        if (this.alwaysLog != null) {
            redirector.setAlwaysLog(this.alwaysLog);
        }
        if (this.logError != null) {
            redirector.setLogError(this.logError);
        }
        if (this.append != null) {
            redirector.setAppend(this.append);
        }
        if (this.createEmptyFiles != null) {
            redirector.setCreateEmptyFiles(this.createEmptyFiles);
        }
        if (this.outputProperty != null) {
            redirector.setOutputProperty(this.outputProperty);
        }
        if (this.errorProperty != null) {
            redirector.setErrorProperty(this.errorProperty);
        }
        if (this.inputString != null) {
            redirector.setInputString(this.inputString);
        }
        if (this.logInputString != null) {
            redirector.setLogInputString(this.logInputString);
        }
        if (this.inputMapper != null) {
            String[] inputTargets;
            block27: {
                inputTargets = null;
                try {
                    inputTargets = this.inputMapper.getImplementation().mapFileName(sourcefile);
                }
                catch (NullPointerException enPeaEx) {
                    if (sourcefile == null) break block27;
                    throw enPeaEx;
                }
            }
            if (inputTargets != null && inputTargets.length > 0) {
                redirector.setInput(this.toFileArray(inputTargets));
            }
        }
        if (this.outputMapper != null) {
            String[] outputTargets;
            block28: {
                outputTargets = null;
                try {
                    outputTargets = this.outputMapper.getImplementation().mapFileName(sourcefile);
                }
                catch (NullPointerException enPeaEx) {
                    if (sourcefile == null) break block28;
                    throw enPeaEx;
                }
            }
            if (outputTargets != null && outputTargets.length > 0) {
                redirector.setOutput(this.toFileArray(outputTargets));
            }
        }
        if (this.errorMapper != null) {
            String[] errorTargets;
            block29: {
                errorTargets = null;
                try {
                    errorTargets = this.errorMapper.getImplementation().mapFileName(sourcefile);
                }
                catch (NullPointerException enPeaEx) {
                    if (sourcefile == null) break block29;
                    throw enPeaEx;
                }
            }
            if (errorTargets != null && errorTargets.length > 0) {
                redirector.setError(this.toFileArray(errorTargets));
            }
        }
        if (!this.inputFilterChains.isEmpty()) {
            redirector.setInputFilterChains(this.inputFilterChains);
        }
        if (!this.outputFilterChains.isEmpty()) {
            redirector.setOutputFilterChains(this.outputFilterChains);
        }
        if (!this.errorFilterChains.isEmpty()) {
            redirector.setErrorFilterChains(this.errorFilterChains);
        }
        if (this.inputEncoding != null) {
            redirector.setInputEncoding(this.inputEncoding);
        }
        if (this.outputEncoding != null) {
            redirector.setOutputEncoding(this.outputEncoding);
        }
        if (this.errorEncoding != null) {
            redirector.setErrorEncoding(this.errorEncoding);
        }
        redirector.setBinaryOutput(this.outputIsBinary);
    }

    protected Mapper createMergeMapper(File destfile) {
        Mapper result = new Mapper(this.getProject());
        result.setClassname(MergingMapper.class.getName());
        result.setTo(destfile.getAbsolutePath());
        return result;
    }

    protected File[] toFileArray(String[] name) {
        if (name == null) {
            return null;
        }
        ArrayList<File> list = new ArrayList<File>(name.length);
        for (String n : name) {
            if (n == null) continue;
            list.add(this.getProject().resolveFile(n));
        }
        return list.toArray(new File[0]);
    }

    @Override
    protected void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (Mapper m : Arrays.asList(this.inputMapper, this.outputMapper, this.errorMapper)) {
                if (m == null) continue;
                stk.push(m);
                m.dieOnCircularReference(stk, p);
                stk.pop();
            }
            List<List> filterChainLists = Arrays.asList(this.inputFilterChains, this.outputFilterChains, this.errorFilterChains);
            for (List filterChains : filterChainLists) {
                if (filterChains == null) continue;
                for (FilterChain fc : filterChains) {
                    RedirectorElement.pushAndInvokeCircularReferenceCheck(fc, stk, p);
                }
            }
            this.setChecked(true);
        }
    }

    private RedirectorElement getRef() {
        return this.getCheckedRef(RedirectorElement.class);
    }
}

