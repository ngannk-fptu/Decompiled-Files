/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeJavaPackage;
import org.mozilla.javascript.ObjArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

public class ImporterTopLevel
extends TopLevel {
    private static final long serialVersionUID = -9095380847465315412L;
    private static final Object IMPORTER_TAG = "Importer";
    private static final int Id_constructor = 1;
    private static final int Id_importClass = 2;
    private static final int Id_importPackage = 3;
    private static final int MAX_PROTOTYPE_ID = 3;
    private static final String AKEY = "importedPackages";
    private boolean topScopeFlag;

    public ImporterTopLevel() {
    }

    public ImporterTopLevel(Context cx) {
        this(cx, false);
    }

    public ImporterTopLevel(Context cx, boolean sealed) {
        this.initStandardObjects(cx, sealed);
    }

    @Override
    public String getClassName() {
        return this.topScopeFlag ? "global" : "JavaImporter";
    }

    public static void init(Context cx, Scriptable scope, boolean sealed) {
        ImporterTopLevel obj = new ImporterTopLevel();
        obj.exportAsJSClass(3, scope, sealed);
    }

    public void initStandardObjects(Context cx, boolean sealed) {
        cx.initStandardObjects(this, sealed);
        this.topScopeFlag = true;
        IdFunctionObject ctor = this.exportAsJSClass(3, this, false);
        if (sealed) {
            ctor.sealObject();
        }
        this.delete("constructor");
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return super.has(name, start) || this.getPackageProperty(name, start) != NOT_FOUND;
    }

    @Override
    public Object get(String name, Scriptable start) {
        Object result = super.get(name, start);
        if (result != NOT_FOUND) {
            return result;
        }
        result = this.getPackageProperty(name, start);
        return result;
    }

    private Object getPackageProperty(String name, Scriptable start) {
        Object[] elements;
        Object result = NOT_FOUND;
        Scriptable scope = start;
        if (this.topScopeFlag) {
            scope = ScriptableObject.getTopLevelScope(scope);
        }
        if ((elements = ImporterTopLevel.getNativeJavaPackages(scope)) == null) {
            return result;
        }
        for (int i = 0; i < elements.length; ++i) {
            NativeJavaPackage p = (NativeJavaPackage)elements[i];
            Object v = p.getPkgProperty(name, start, false);
            if (v == null || v instanceof NativeJavaPackage) continue;
            if (result == NOT_FOUND) {
                result = v;
                continue;
            }
            throw Context.reportRuntimeErrorById("msg.ambig.import", result.toString(), v.toString());
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Object[] getNativeJavaPackages(Scriptable scope) {
        Scriptable scriptable = scope;
        synchronized (scriptable) {
            ScriptableObject so;
            ObjArray importedPackages;
            if (scope instanceof ScriptableObject && (importedPackages = (ObjArray)(so = (ScriptableObject)scope).getAssociatedValue(AKEY)) != null) {
                return importedPackages.toArray();
            }
        }
        return null;
    }

    @Deprecated
    public void importPackage(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
        ImporterTopLevel.js_importPackage(this, args);
    }

    private Object js_construct(Scriptable scope, Object[] args) {
        ImporterTopLevel result = new ImporterTopLevel();
        for (int i = 0; i != args.length; ++i) {
            Object arg = args[i];
            if (arg instanceof NativeJavaClass) {
                ImporterTopLevel.importClass(result, (NativeJavaClass)arg);
                continue;
            }
            if (arg instanceof NativeJavaPackage) {
                ImporterTopLevel.importPackage(result, (NativeJavaPackage)arg);
                continue;
            }
            throw Context.reportRuntimeErrorById("msg.not.class.not.pkg", Context.toString(arg));
        }
        result.setParentScope(scope);
        result.setPrototype(this);
        return result;
    }

    private static Object js_importClass(Scriptable scope, Object[] args) {
        for (int i = 0; i != args.length; ++i) {
            Object arg = args[i];
            if (!(arg instanceof NativeJavaClass)) {
                throw Context.reportRuntimeErrorById("msg.not.class", Context.toString(arg));
            }
            ImporterTopLevel.importClass(scope, (NativeJavaClass)arg);
        }
        return Undefined.instance;
    }

    private static Object js_importPackage(ScriptableObject scope, Object[] args) {
        for (int i = 0; i != args.length; ++i) {
            Object arg = args[i];
            if (!(arg instanceof NativeJavaPackage)) {
                throw Context.reportRuntimeErrorById("msg.not.pkg", Context.toString(arg));
            }
            ImporterTopLevel.importPackage(scope, (NativeJavaPackage)arg);
        }
        return Undefined.instance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void importPackage(ScriptableObject scope, NativeJavaPackage pkg) {
        if (pkg == null) {
            return;
        }
        ScriptableObject scriptableObject = scope;
        synchronized (scriptableObject) {
            ObjArray importedPackages = (ObjArray)scope.getAssociatedValue(AKEY);
            if (importedPackages == null) {
                importedPackages = new ObjArray();
                scope.associateValue(AKEY, importedPackages);
            }
            for (int j = 0; j != importedPackages.size(); ++j) {
                if (!pkg.equals(importedPackages.get(j))) continue;
                return;
            }
            importedPackages.add(pkg);
        }
    }

    private static void importClass(Scriptable scope, NativeJavaClass cl) {
        String s = cl.getClassObject().getName();
        String n = s.substring(s.lastIndexOf(46) + 1);
        Object val = scope.get(n, scope);
        if (val != NOT_FOUND && val != cl) {
            throw Context.reportRuntimeErrorById("msg.prop.defined", n);
        }
        scope.put(n, scope, (Object)cl);
    }

    @Override
    protected void initPrototypeId(int id) {
        String s;
        int arity;
        switch (id) {
            case 1: {
                arity = 0;
                s = "constructor";
                break;
            }
            case 2: {
                arity = 1;
                s = "importClass";
                break;
            }
            case 3: {
                arity = 1;
                s = "importPackage";
                break;
            }
            default: {
                throw new IllegalArgumentException(String.valueOf(id));
            }
        }
        this.initPrototypeMethod(IMPORTER_TAG, id, s, arity);
    }

    @Override
    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (!f.hasTag(IMPORTER_TAG)) {
            return super.execIdCall(f, cx, scope, thisObj, args);
        }
        int id = f.methodId();
        switch (id) {
            case 1: {
                return this.js_construct(scope, args);
            }
            case 2: {
                return ImporterTopLevel.js_importClass(this.realScope(scope, thisObj, f), args);
            }
            case 3: {
                return ImporterTopLevel.js_importPackage(this.realScope(scope, thisObj, f), args);
            }
        }
        throw new IllegalArgumentException(String.valueOf(id));
    }

    private ScriptableObject realScope(Scriptable scope, Scriptable thisObj, IdFunctionObject f) {
        if (this.topScopeFlag) {
            thisObj = ScriptableObject.getTopLevelScope(scope);
        }
        return ImporterTopLevel.ensureType(thisObj, ScriptableObject.class, f);
    }

    @Override
    protected int findPrototypeId(String s) {
        int id;
        switch (s) {
            case "constructor": {
                id = 1;
                break;
            }
            case "importClass": {
                id = 2;
                break;
            }
            case "importPackage": {
                id = 3;
                break;
            }
            default: {
                id = 0;
            }
        }
        return id;
    }
}

