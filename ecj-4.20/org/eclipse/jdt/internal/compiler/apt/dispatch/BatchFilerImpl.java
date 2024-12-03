/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.apt.dispatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;
import javax.annotation.processing.Filer;
import javax.annotation.processing.FilerException;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.apt.dispatch.HookedJavaFileObject;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

public class BatchFilerImpl
implements Filer {
    protected final BaseAnnotationProcessorManager _dispatchManager;
    protected final BatchProcessingEnvImpl _env;
    protected final JavaFileManager _fileManager;
    protected final HashSet<URI> _createdFiles;

    public BatchFilerImpl(BaseAnnotationProcessorManager dispatchManager, BatchProcessingEnvImpl env) {
        this._dispatchManager = dispatchManager;
        this._fileManager = env._fileManager;
        this._env = env;
        this._createdFiles = new HashSet();
    }

    public void addNewUnit(ICompilationUnit unit) {
        this._env.addNewUnit(unit);
    }

    public void addNewClassFile(ReferenceBinding binding) {
        this._env.addNewClassFile(binding);
    }

    @Override
    public JavaFileObject createClassFile(CharSequence name, Element ... originatingElements) throws IOException {
        JavaFileObject jfo = this._fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, name.toString(), JavaFileObject.Kind.CLASS, null);
        URI uri = jfo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Class file already created : " + name);
        }
        this._createdFiles.add(uri);
        return new HookedJavaFileObject(jfo, jfo.getName(), name.toString(), this);
    }

    @Override
    public FileObject createResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName, Element ... originatingElements) throws IOException {
        BatchFilerImpl.validateName(relativeName);
        FileObject fo = this._fileManager.getFileForOutput(location, pkg.toString(), relativeName.toString(), null);
        URI uri = fo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Resource already created : " + location + '/' + pkg + '/' + relativeName);
        }
        this._createdFiles.add(uri);
        return fo;
    }

    private static void validateName(CharSequence relativeName) {
        int length = relativeName.length();
        if (length == 0) {
            throw new IllegalArgumentException("relative path cannot be empty");
        }
        String path = relativeName.toString();
        if (path.indexOf(92) != -1) {
            path = path.replace('\\', '/');
        }
        if (path.charAt(0) == '/') {
            throw new IllegalArgumentException("relative path is absolute");
        }
        boolean hasDot = false;
        int i = 0;
        while (i < length) {
            switch (path.charAt(i)) {
                case '/': {
                    if (!hasDot) break;
                    throw new IllegalArgumentException("relative name " + relativeName + " is not relative");
                }
                case '.': {
                    hasDot = true;
                    break;
                }
                default: {
                    hasDot = false;
                }
            }
            ++i;
        }
        if (hasDot) {
            throw new IllegalArgumentException("relative name " + relativeName + " is not relative");
        }
    }

    @Override
    public JavaFileObject createSourceFile(CharSequence name, Element ... originatingElements) throws IOException {
        TypeElement typeElement;
        String moduleAndPkgString = name.toString();
        int slash = moduleAndPkgString.indexOf(47);
        String mod = null;
        if (slash != -1) {
            name = moduleAndPkgString.substring(slash + 1, name.length());
            mod = moduleAndPkgString.substring(0, slash);
        }
        if ((typeElement = this._env._elementUtils.getTypeElement(name)) != null) {
            throw new FilerException("Source file already exists : " + moduleAndPkgString);
        }
        JavaFileManager.Location location = mod == null ? StandardLocation.SOURCE_OUTPUT : this._fileManager.getLocationForModule((JavaFileManager.Location)StandardLocation.SOURCE_OUTPUT, mod);
        JavaFileObject jfo = this._fileManager.getJavaFileForOutput(location, name.toString(), JavaFileObject.Kind.SOURCE, null);
        URI uri = jfo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Source file already created : " + name);
        }
        this._createdFiles.add(uri);
        return new HookedJavaFileObject(jfo, jfo.getName(), name.toString(), this);
    }

    @Override
    public FileObject getResource(JavaFileManager.Location location, CharSequence pkg, CharSequence relativeName) throws IOException {
        BatchFilerImpl.validateName(relativeName);
        FileObject fo = this._fileManager.getFileForInput(location, pkg.toString(), relativeName.toString());
        if (fo == null) {
            throw new FileNotFoundException("Resource does not exist : " + location + '/' + pkg + '/' + relativeName);
        }
        URI uri = fo.toUri();
        if (this._createdFiles.contains(uri)) {
            throw new FilerException("Resource already created : " + location + '/' + pkg + '/' + relativeName);
        }
        return fo;
    }
}

