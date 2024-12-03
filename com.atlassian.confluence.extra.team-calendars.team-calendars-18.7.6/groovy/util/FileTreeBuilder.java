/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;

public class FileTreeBuilder
implements GroovyObject {
    private File baseDir;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public FileTreeBuilder(File baseDir) {
        File file;
        MetaClass metaClass;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.baseDir = file = baseDir;
    }

    public FileTreeBuilder() {
        this(new File("."));
    }

    public File file(String name, CharSequence contents) {
        return ResourceGroovyMethods.leftShift(new File(this.baseDir, name), contents);
    }

    public File file(String name, byte ... contents) {
        return ResourceGroovyMethods.leftShift(new File(this.baseDir, name), contents);
    }

    public File file(String name, File source) {
        return this.file(name, ResourceGroovyMethods.getBytes(source));
    }

    public File file(String name, @DelegatesTo(strategy=1, value=File.class) Closure spec) {
        File file = new File(this.baseDir, name);
        Closure clone = (Closure)ScriptBytecodeAdapter.castToType(spec.clone(), Closure.class);
        File file2 = file;
        clone.setDelegate(file2);
        int n = Closure.DELEGATE_FIRST;
        clone.setResolveStrategy(n);
        clone.call((Object)file);
        return file;
    }

    public File dir(String name) {
        File f = new File(this.baseDir, name);
        f.mkdirs();
        return f;
    }

    public File dir(String name, @DelegatesTo(strategy=1, value=FileTreeBuilder.class) Closure cl) {
        File oldBase = this.baseDir;
        File newBase = this.dir(name);
        try {
            File file;
            this.baseDir = file = newBase;
            FileTreeBuilder fileTreeBuilder = this;
            cl.setDelegate(fileTreeBuilder);
            int n = Closure.DELEGATE_FIRST;
            cl.setResolveStrategy(n);
            cl.call();
        }
        finally {
            File file;
            this.baseDir = file = oldBase;
        }
        return newBase;
    }

    public File call(@DelegatesTo(strategy=1, value=FileTreeBuilder.class) Closure spec) {
        Closure clone = (Closure)ScriptBytecodeAdapter.castToType(spec.clone(), Closure.class);
        FileTreeBuilder fileTreeBuilder = this;
        clone.setDelegate(fileTreeBuilder);
        int n = Closure.DELEGATE_FIRST;
        clone.setResolveStrategy(n);
        clone.call();
        return this.baseDir;
    }

    public Object methodMissing(String name, Object args) {
        if (args instanceof Object[] && ((Object[])ScriptBytecodeAdapter.castToType(args, Object[].class)).length == 1) {
            Object arg = BytecodeInterface8.objectArrayGet((Object[])ScriptBytecodeAdapter.castToType(args, Object[].class), 0);
            if (arg instanceof Closure) {
                return this.dir(name, (Closure)ScriptBytecodeAdapter.castToType(arg, Closure.class));
            }
            if (arg instanceof CharSequence) {
                return this.file(name, DefaultGroovyMethods.toString(arg));
            }
            if (arg instanceof byte[]) {
                return this.file(name, (byte[])ScriptBytecodeAdapter.castToType(arg, byte[].class));
            }
            if (arg instanceof File) {
                return this.file(name, (File)ScriptBytecodeAdapter.castToType(arg, File.class));
            }
            return null;
        }
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != FileTreeBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public File getBaseDir() {
        return this.baseDir;
    }

    public void setBaseDir(File file) {
        this.baseDir = file;
    }
}

