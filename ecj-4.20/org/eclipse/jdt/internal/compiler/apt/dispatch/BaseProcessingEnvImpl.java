/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.apt.model.ElementsImpl;
import org.eclipse.jdt.internal.compiler.apt.model.Factory;
import org.eclipse.jdt.internal.compiler.apt.model.TypesImpl;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.ModuleBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public abstract class BaseProcessingEnvImpl
implements ProcessingEnvironment {
    protected Filer _filer;
    protected Messager _messager;
    protected Map<String, String> _processorOptions;
    protected Compiler _compiler;
    protected Elements _elementUtils;
    protected Types _typeUtils;
    private List<ICompilationUnit> _addedUnits = new ArrayList<ICompilationUnit>();
    private List<ReferenceBinding> _addedClassFiles = new ArrayList<ReferenceBinding>();
    private List<ICompilationUnit> _deletedUnits = new ArrayList<ICompilationUnit>();
    private boolean _errorRaised = false;
    private Factory _factory;
    public ModuleBinding _current_module;

    public BaseProcessingEnvImpl() {
        this._elementUtils = ElementsImpl.create(this);
        this._typeUtils = new TypesImpl(this);
        this._factory = new Factory(this);
    }

    public void addNewUnit(ICompilationUnit unit) {
        this._addedUnits.add(unit);
    }

    public void addNewClassFile(ReferenceBinding binding) {
        this._addedClassFiles.add(binding);
    }

    public Compiler getCompiler() {
        return this._compiler;
    }

    public ICompilationUnit[] getDeletedUnits() {
        ICompilationUnit[] result = new ICompilationUnit[this._deletedUnits.size()];
        this._deletedUnits.toArray(result);
        return result;
    }

    public ICompilationUnit[] getNewUnits() {
        ICompilationUnit[] result = new ICompilationUnit[this._addedUnits.size()];
        this._addedUnits.toArray(result);
        return result;
    }

    @Override
    public Elements getElementUtils() {
        return this._elementUtils;
    }

    @Override
    public Filer getFiler() {
        return this._filer;
    }

    @Override
    public Messager getMessager() {
        return this._messager;
    }

    @Override
    public Map<String, String> getOptions() {
        return this._processorOptions;
    }

    @Override
    public Types getTypeUtils() {
        return this._typeUtils;
    }

    public LookupEnvironment getLookupEnvironment() {
        return this._compiler.lookupEnvironment;
    }

    @Override
    public SourceVersion getSourceVersion() {
        if (this._compiler.options.sourceLevel <= 0x310000L) {
            return SourceVersion.RELEASE_5;
        }
        if (this._compiler.options.sourceLevel == 0x320000L) {
            return SourceVersion.RELEASE_6;
        }
        try {
            if (this._compiler.options.sourceLevel == 0x330000L) {
                return SourceVersion.valueOf("RELEASE_7");
            }
            if (this._compiler.options.sourceLevel == 0x340000L) {
                return SourceVersion.valueOf("RELEASE_8");
            }
            if (this._compiler.options.sourceLevel == 0x350000L) {
                return SourceVersion.valueOf("RELEASE_9");
            }
            if (this._compiler.options.sourceLevel == 0x360000L) {
                return SourceVersion.valueOf("RELEASE_10");
            }
            if (this._compiler.options.sourceLevel == 0x370000L) {
                return SourceVersion.valueOf("RELEASE_11");
            }
            if (this._compiler.options.sourceLevel == 0x380000L) {
                return SourceVersion.valueOf("RELEASE_12");
            }
            if (this._compiler.options.sourceLevel == 0x390000L) {
                return SourceVersion.valueOf("RELEASE_13");
            }
            if (this._compiler.options.sourceLevel == 0x3A0000L) {
                return SourceVersion.valueOf("RELEASE_14");
            }
            if (this._compiler.options.sourceLevel == 0x3B0000L) {
                return SourceVersion.valueOf("RELEASE_15");
            }
            if (this._compiler.options.sourceLevel == 0x3C0000L) {
                return SourceVersion.valueOf("RELEASE_16");
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            return SourceVersion.RELEASE_6;
        }
        return SourceVersion.RELEASE_6;
    }

    public void reset() {
        this._addedUnits.clear();
        this._addedClassFiles.clear();
        this._deletedUnits.clear();
    }

    public boolean errorRaised() {
        return this._errorRaised;
    }

    public void setErrorRaised(boolean b) {
        this._errorRaised = true;
    }

    public Factory getFactory() {
        return this._factory;
    }

    public ReferenceBinding[] getNewClassFiles() {
        ReferenceBinding[] result = new ReferenceBinding[this._addedClassFiles.size()];
        this._addedClassFiles.toArray(result);
        return result;
    }

    @Override
    public boolean isPreviewEnabled() {
        return this._compiler.options.enablePreviewFeatures;
    }
}

