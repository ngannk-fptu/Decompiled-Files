/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.make.coverage;

import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Coverage {
    public static Map<Clazz.MethodDef, List<Clazz.MethodDef>> getCrossRef(Collection<Clazz> source, Collection<Clazz> dest) throws Exception {
        Map<Clazz.MethodDef, List<Clazz.MethodDef>> catalog = Coverage.buildCatalog(dest);
        Coverage.crossRef(source, catalog);
        return catalog;
    }

    private static void crossRef(Collection<Clazz> source, final Map<Clazz.MethodDef, List<Clazz.MethodDef>> catalog) throws Exception {
        for (final Clazz clazz : source) {
            clazz.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public void implementsInterfaces(Descriptors.TypeRef[] names) {
                    Clazz.MethodDef def = clazz.getMethodDef(0, "<implements>", "()V");
                    for (Descriptors.TypeRef interfaceName : names) {
                        for (Map.Entry entry : catalog.entrySet()) {
                            String catalogClass = ((Clazz.MethodDef)entry.getKey()).getContainingClass().getFQN();
                            List references = (List)entry.getValue();
                            if (!catalogClass.equals(interfaceName.getFQN())) continue;
                            references.add(def);
                        }
                    }
                }

                @Override
                public void method(Clazz.MethodDef source) {
                }
            });
        }
    }

    private static Map<Clazz.MethodDef, List<Clazz.MethodDef>> buildCatalog(Collection<Clazz> sources) throws Exception {
        final TreeMap<Clazz.MethodDef, List<Clazz.MethodDef>> catalog = new TreeMap<Clazz.MethodDef, List<Clazz.MethodDef>>(new Comparator<Clazz.MethodDef>(){

            @Override
            public int compare(Clazz.MethodDef a, Clazz.MethodDef b) {
                return a.getName().compareTo(b.getName());
            }
        });
        for (final Clazz clazz : sources) {
            clazz.parseClassFileWithCollector(new ClassDataCollector(){

                @Override
                public boolean classStart(int access, Descriptors.TypeRef name) {
                    return clazz.isPublic();
                }

                @Override
                public void method(Clazz.MethodDef source) {
                    if (source.isPublic() || source.isProtected()) {
                        catalog.put(source, new ArrayList());
                    }
                }
            });
        }
        return catalog;
    }
}

