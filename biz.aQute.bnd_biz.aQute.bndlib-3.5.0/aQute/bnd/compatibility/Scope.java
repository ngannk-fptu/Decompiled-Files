/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.compatibility;

import aQute.bnd.compatibility.Access;
import aQute.bnd.compatibility.GenericParameter;
import aQute.bnd.compatibility.GenericType;
import aQute.bnd.compatibility.Kind;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    final Map<String, Scope> children = new LinkedHashMap<String, Scope>();
    final String name;
    Access access;
    Kind kind;
    Scope enclosing;
    Scope declaring;
    GenericParameter[] typeVars;
    Map<String, String[]> name2bounds;
    GenericType base;
    GenericType[] parameters;
    GenericType[] exceptions;

    public Scope(Access access, Kind kind, String name) {
        this.access = access;
        this.kind = kind;
        this.name = name;
    }

    Scope getScope(String name) {
        Scope s = this.children.get(name);
        if (s != null) {
            return s;
        }
        s = new Scope(Access.UNKNOWN, Kind.UNKNOWN, name);
        this.children.put(name, s);
        s.declaring = this;
        return s;
    }

    public void setParameterTypes(GenericType[] convert) {
        this.parameters = convert;
    }

    public void setExceptionTypes(GenericType[] convert) {
        this.exceptions = convert;
    }

    public void setBase(GenericType typeSignature) {
        this.base = typeSignature;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.typeVars != null && this.typeVars.length != 0) {
            sb.append("<");
            for (GenericParameter v : this.typeVars) {
                sb.append(v);
            }
            sb.append(">");
        }
        sb.append(this.access.toString());
        sb.append(" ");
        sb.append(this.kind.toString());
        sb.append(" ");
        sb.append(this.name);
        return sb.toString();
    }

    public void report(Appendable a, int indent) throws IOException {
        for (int i = 0; i < indent; ++i) {
            a.append("  ");
        }
        a.append(this.toString());
        a.append("\n");
        for (Scope s : this.children.values()) {
            s.report(a, indent + 1);
        }
    }

    public void add(Scope m) {
        this.children.put(m.name, m);
    }

    public void setDeclaring(Scope declaring) {
        this.declaring = declaring;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public void setEnclosing(Scope enclosing) {
        this.enclosing = enclosing;
        if (this.enclosing != null) {
            this.enclosing.add(this);
        }
    }

    public boolean isTop() {
        return this.enclosing == null;
    }

    public void setKind(Kind kind) {
        this.kind = kind;
    }

    public static String classIdentity(String name2) {
        return name2.replace('.', '/');
    }

    public static String methodIdentity(String name, String descriptor) {
        return name + ":" + descriptor;
    }

    public static String constructorIdentity(String descriptor) {
        return ":" + descriptor;
    }

    public static String fieldIdentity(String name, String descriptor) {
        return name + ":" + descriptor;
    }

    public void cleanRoot() {
        Iterator<Map.Entry<String, Scope>> i = this.children.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Scope> entry = i.next();
            if (entry.getValue().isTop()) continue;
            i.remove();
        }
    }

    public void prune(EnumSet<Access> level) {
        Iterator<Map.Entry<String, Scope>> i = this.children.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, Scope> entry = i.next();
            if (!level.contains((Object)entry.getValue().access)) {
                i.remove();
                continue;
            }
            entry.getValue().prune(level);
        }
    }

    public void setGenericParameter(GenericParameter[] typeVars) {
        this.typeVars = typeVars;
    }
}

