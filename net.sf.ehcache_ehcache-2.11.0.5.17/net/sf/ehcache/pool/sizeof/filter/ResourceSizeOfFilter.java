/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.pool.sizeof.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.sf.ehcache.pool.sizeof.filter.SizeOfFilter;

public class ResourceSizeOfFilter
implements SizeOfFilter {
    private final Set<String> filteredTerms;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ResourceSizeOfFilter(URL filterData) throws IOException {
        if (filterData == null) {
            this.filteredTerms = Collections.emptySet();
        } else {
            try (InputStream is = filterData.openStream();){
                HashSet<String> filtered = new HashSet<String>();
                try (BufferedReader r = new BufferedReader(new InputStreamReader(is));){
                    String field;
                    while ((field = r.readLine()) != null) {
                        if ((field = field.trim()).isEmpty() || field.startsWith("#")) continue;
                        filtered.add(field);
                    }
                    this.filteredTerms = Collections.unmodifiableSet(filtered);
                }
            }
        }
    }

    @Override
    public Collection<Field> filterFields(Class<?> klazz, Collection<Field> fields) {
        Iterator<Field> it = fields.iterator();
        while (it.hasNext()) {
            Field f = it.next();
            if (!this.filteredTerms.contains(f.getDeclaringClass().getName() + "." + f.getName())) continue;
            it.remove();
        }
        return fields;
    }

    @Override
    public boolean filterClass(Class<?> klazz) {
        String packageName;
        String klazzName = klazz.getName();
        if (this.filteredTerms.contains(klazzName)) {
            return false;
        }
        int lastDot = klazzName.lastIndexOf(46);
        return lastDot < 0 || (packageName = klazzName.substring(0, lastDot)).isEmpty() || !this.filteredTerms.contains(packageName);
    }
}

