/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.calltree;

import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.WriteResource;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CalltreeResource
extends WriteResource {
    Collection<Clazz> classes;
    static Comparator<Clazz.MethodDef> COMPARATOR = new Comparator<Clazz.MethodDef>(){

        @Override
        public int compare(Clazz.MethodDef a, Clazz.MethodDef b) {
            int r = a.getName().compareTo(b.getName());
            return r != 0 ? r : a.getDescriptor().toString().compareTo(b.getDescriptor().toString());
        }
    };

    public CalltreeResource(Collection<Clazz> values) {
        this.classes = values;
        System.err.println(values);
    }

    @Override
    public long lastModified() {
        return 0L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void write(OutputStream out) throws Exception {
        OutputStreamWriter osw = new OutputStreamWriter(out, Constants.DEFAULT_CHARSET);
        PrintWriter pw = new PrintWriter(osw);
        try {
            CalltreeResource.writeCalltree(pw, this.classes);
        }
        finally {
            pw.flush();
        }
    }

    public static void writeCalltree(PrintWriter out, Collection<Clazz> classes) throws Exception {
        final TreeMap<Clazz.MethodDef, Set<Clazz.MethodDef>> using = new TreeMap<Clazz.MethodDef, Set<Clazz.MethodDef>>(COMPARATOR);
        final TreeMap<Clazz.MethodDef, Set<Clazz.MethodDef>> usedby = new TreeMap<Clazz.MethodDef, Set<Clazz.MethodDef>>(COMPARATOR);
        ClassDataCollector cd = new ClassDataCollector(){

            @Override
            public void method(Clazz.MethodDef source) {
                CalltreeResource.xref(using, source, null);
                CalltreeResource.xref(usedby, source, null);
            }
        };
        for (Clazz clazz : classes) {
            clazz.parseClassFileWithCollector(cd);
        }
        out.println("<calltree>");
        CalltreeResource.xref(out, "using", using);
        CalltreeResource.xref(out, "usedby", usedby);
        out.println("</calltree>");
    }

    static void xref(Map<Clazz.MethodDef, Set<Clazz.MethodDef>> references, Clazz.MethodDef source, Clazz.MethodDef reference) {
        Set<Clazz.MethodDef> set = references.get(source);
        if (set == null) {
            set = new TreeSet<Clazz.MethodDef>(COMPARATOR);
            references.put(source, set);
        }
        if (reference != null) {
            set.add(reference);
        }
    }

    private static void xref(PrintWriter out, String group, Map<Clazz.MethodDef, Set<Clazz.MethodDef>> references) {
        out.println("  <" + group + ">");
        for (Map.Entry<Clazz.MethodDef, Set<Clazz.MethodDef>> entry : references.entrySet()) {
            Clazz.MethodDef source = entry.getKey();
            Set<Clazz.MethodDef> refs = entry.getValue();
            CalltreeResource.method(out, "method", source, ">");
            for (Clazz.MethodDef ref : refs) {
                CalltreeResource.method(out, "ref", ref, "/>");
            }
            out.println("      </method>");
        }
        out.println("  </" + group + ">");
    }

    private static void method(PrintWriter out, String element, Clazz.MethodDef source, String closeElement) {
        out.println("      <" + element + " class='" + source.getContainingClass().getFQN() + "'" + CalltreeResource.getAccess(source.getAccess()) + (source.isConstructor() ? "" : " name='" + source.getName() + "'") + " descriptor='" + source.getDescriptor() + "' pretty='" + source.toString() + "'" + closeElement);
    }

    private static String getAccess(int access) {
        StringBuilder sb = new StringBuilder();
        if (Modifier.isPublic(access)) {
            sb.append(" public='true'");
        }
        if (Modifier.isStatic(access)) {
            sb.append(" static='true'");
        }
        if (Modifier.isProtected(access)) {
            sb.append(" protected='true'");
        }
        if (Modifier.isInterface(access)) {
            sb.append(" interface='true'");
        }
        return sb.toString();
    }
}

