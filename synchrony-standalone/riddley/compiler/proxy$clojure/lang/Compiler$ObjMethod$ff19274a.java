/*
 * Decompiled with CFR 0.152.
 */
package riddley.compiler.proxy$clojure.lang;

import clojure.asm.ClassVisitor;
import clojure.lang.Compiler;
import clojure.lang.IFn;
import clojure.lang.IPersistentCollection;
import clojure.lang.IPersistentMap;
import clojure.lang.IProxy;
import clojure.lang.RT;

public class Compiler$ObjMethod$ff19274a
extends Compiler.ObjMethod
implements IProxy {
    private volatile IPersistentMap __clojureFnMap;

    public Compiler$ObjMethod$ff19274a(Compiler.ObjExpr objExpr, Compiler.ObjMethod objMethod) {
        Compiler$ObjMethod$ff19274a compiler$ObjMethod$ff19274a = this;
        Compiler$ObjMethod$ff19274a compiler$ObjMethod$ff19274a2 = compiler$ObjMethod$ff19274a;
        super(objExpr, objMethod);
    }

    @Override
    public void __initClojureFnMappings(IPersistentMap iPersistentMap) {
        this.__clojureFnMap = iPersistentMap;
    }

    @Override
    public void __updateClojureFnMappings(IPersistentMap iPersistentMap) {
        this.__clojureFnMap = (IPersistentMap)((IPersistentCollection)this.__clojureFnMap).cons(iPersistentMap);
    }

    @Override
    public IPersistentMap __getClojureFnMappings() {
        return this.__clojureFnMap;
    }

    public Object clone() {
        Object object = RT.get(this.__clojureFnMap, "clone");
        return object != null ? ((IFn)object).invoke(this) : super.clone();
    }

    public int hashCode() {
        Object object = RT.get(this.__clojureFnMap, "hashCode");
        return object != null ? ((Number)((IFn)object).invoke(this)).intValue() : super.hashCode();
    }

    public String toString() {
        Object object = RT.get(this.__clojureFnMap, "toString");
        return object != null ? (String)((IFn)object).invoke(this) : super.toString();
    }

    public boolean equals(Object object) {
        Object object2 = RT.get(this.__clojureFnMap, "equals");
        return object2 != null ? ((Boolean)((IFn)object2).invoke(this, object)).booleanValue() : super.equals(object);
    }

    @Override
    public void emit(Compiler.ObjExpr objExpr, ClassVisitor classVisitor) {
        Object object = RT.get(this.__clojureFnMap, "emit");
        if (object != null) {
            ((IFn)object).invoke(this, objExpr, classVisitor);
        } else {
            super.emit(objExpr, classVisitor);
        }
    }
}

