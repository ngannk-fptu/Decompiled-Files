/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.system;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import software.amazon.ion.IonMutableCatalog;
import software.amazon.ion.SymbolTable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleCatalog
implements IonMutableCatalog,
Iterable<SymbolTable> {
    private Map<String, TreeMap<Integer, SymbolTable>> myTablesByName = new HashMap<String, TreeMap<Integer, SymbolTable>>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SymbolTable getTable(String name) {
        TreeMap<Integer, SymbolTable> versions;
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name is empty");
        }
        Map<String, TreeMap<Integer, SymbolTable>> map = this.myTablesByName;
        synchronized (map) {
            versions = this.myTablesByName.get(name);
        }
        if (versions == null) {
            return null;
        }
        map = versions;
        synchronized (map) {
            Integer highestVersion = versions.lastKey();
            return versions.get(highestVersion);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SymbolTable getTable(String name, int version) {
        TreeMap<Integer, SymbolTable> versions;
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        if (name.length() == 0) {
            throw new IllegalArgumentException("name is empty");
        }
        if (version < 1) {
            throw new IllegalArgumentException("version is < 1");
        }
        Map<String, TreeMap<Integer, SymbolTable>> map = this.myTablesByName;
        synchronized (map) {
            versions = this.myTablesByName.get(name);
        }
        if (versions == null) {
            return null;
        }
        map = versions;
        synchronized (map) {
            SymbolTable st = versions.get(version);
            if (st == null) {
                assert (!versions.isEmpty());
                Integer ibest = SimpleCatalog.bestMatch(version, versions.keySet());
                assert (ibest != null);
                st = versions.get(ibest);
                assert (st != null);
            }
            return st;
        }
    }

    static Integer bestMatch(int requestedVersion, Iterable<Integer> availableVersions) {
        int best = requestedVersion;
        Integer ibest = null;
        for (Integer available : availableVersions) {
            assert (available != requestedVersion);
            int v = available;
            if (requestedVersion < best) {
                if (requestedVersion >= v || v >= best) continue;
                best = v;
                ibest = available;
                continue;
            }
            if (best < requestedVersion) {
                if (best >= v) continue;
                best = v;
                ibest = available;
                continue;
            }
            best = v;
            ibest = available;
        }
        return ibest;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void putTable(SymbolTable table) {
        if (table.isLocalTable() || table.isSystemTable() || table.isSubstitute()) {
            throw new IllegalArgumentException("table cannot be local or system or substitute table");
        }
        String name = table.getName();
        int version = table.getVersion();
        assert (version >= 0);
        Map<String, TreeMap<Integer, SymbolTable>> map = this.myTablesByName;
        synchronized (map) {
            TreeMap<Integer, SymbolTable> versions = this.myTablesByName.get(name);
            if (versions == null) {
                versions = new TreeMap();
                this.myTablesByName.put(name, versions);
            }
            TreeMap<Integer, SymbolTable> treeMap = versions;
            synchronized (treeMap) {
                versions.put(version, table);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public SymbolTable removeTable(String name, int version) {
        SymbolTable removed = null;
        Map<String, TreeMap<Integer, SymbolTable>> map = this.myTablesByName;
        synchronized (map) {
            TreeMap<Integer, SymbolTable> versions = this.myTablesByName.get(name);
            if (versions != null) {
                TreeMap<Integer, SymbolTable> treeMap = versions;
                synchronized (treeMap) {
                    removed = versions.remove(version);
                    if (versions.isEmpty()) {
                        this.myTablesByName.remove(name);
                    }
                }
            }
        }
        return removed;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator<SymbolTable> iterator() {
        ArrayList<SymbolTable> tables;
        Map<String, TreeMap<Integer, SymbolTable>> map = this.myTablesByName;
        synchronized (map) {
            tables = new ArrayList<SymbolTable>(this.myTablesByName.size());
            Collection<TreeMap<Integer, SymbolTable>> symtabNames = this.myTablesByName.values();
            Iterator<TreeMap<Integer, SymbolTable>> iterator = symtabNames.iterator();
            while (iterator.hasNext()) {
                TreeMap<Integer, SymbolTable> versions;
                TreeMap<Integer, SymbolTable> treeMap = versions = iterator.next();
                synchronized (treeMap) {
                    tables.addAll(versions.values());
                }
            }
        }
        return tables.iterator();
    }
}

