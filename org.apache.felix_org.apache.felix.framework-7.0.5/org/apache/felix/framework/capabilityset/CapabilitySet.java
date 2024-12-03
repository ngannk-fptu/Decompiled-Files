/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.capabilityset;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.SecureAction;
import org.apache.felix.framework.util.StringComparator;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.resource.Capability;

public class CapabilitySet {
    private final SortedMap<String, Map<Object, Set<BundleCapability>>> m_indices;
    private final Set<Capability> m_capSet = Collections.newSetFromMap(new ConcurrentHashMap());
    private static final SecureAction m_secureAction = new SecureAction();
    private static final Class<?>[] STRING_CLASS = new Class[]{String.class};
    private static final String VALUE_OF_METHOD_NAME = "valueOf";

    public void dump() {
        for (Map.Entry<String, Map<Object, Set<BundleCapability>>> entry : this.m_indices.entrySet()) {
            boolean header1 = false;
            for (Map.Entry<Object, Set<BundleCapability>> entry2 : entry.getValue().entrySet()) {
                boolean header2 = false;
                for (BundleCapability cap : entry2.getValue()) {
                    if (cap.getRevision().getBundle().getBundleId() == 0L) continue;
                    if (!header1) {
                        System.out.println(entry.getKey() + ":");
                        header1 = true;
                    }
                    if (!header2) {
                        System.out.println("   " + entry2.getKey());
                        header2 = true;
                    }
                    System.out.println("      " + cap);
                }
            }
        }
    }

    public CapabilitySet(List<String> indexProps, boolean caseSensitive) {
        this.m_indices = caseSensitive ? new ConcurrentSkipListMap<String, Map<Object, Set<BundleCapability>>>() : new ConcurrentSkipListMap(StringComparator.COMPARATOR);
        for (int i = 0; indexProps != null && i < indexProps.size(); ++i) {
            this.m_indices.put(indexProps.get(i), new ConcurrentHashMap());
        }
    }

    public void addCapability(BundleCapability cap) {
        this.m_capSet.add(cap);
        for (Map.Entry<String, Map<Object, Set<BundleCapability>>> entry : this.m_indices.entrySet()) {
            Object value = cap.getAttributes().get(entry.getKey());
            if (value == null) continue;
            if (value.getClass().isArray()) {
                value = CapabilitySet.convertArrayToList(value);
            }
            ConcurrentMap index = (ConcurrentMap)entry.getValue();
            if (value instanceof Collection) {
                Collection c = (Collection)value;
                for (Object o : c) {
                    this.indexCapability(index, cap, o);
                }
                continue;
            }
            this.indexCapability(index, cap, value);
        }
    }

    private void indexCapability(ConcurrentMap<Object, Set<BundleCapability>> index, BundleCapability cap, Object capValue) {
        Set caps = Collections.newSetFromMap(new ConcurrentHashMap());
        Set prevval = index.putIfAbsent(capValue, caps);
        if (prevval != null) {
            caps = prevval;
        }
        caps.add(cap);
    }

    public void removeCapability(BundleCapability cap) {
        if (this.m_capSet.remove(cap)) {
            for (Map.Entry<String, Map<Object, Set<BundleCapability>>> entry : this.m_indices.entrySet()) {
                Object value = cap.getAttributes().get(entry.getKey());
                if (value == null) continue;
                if (value.getClass().isArray()) {
                    value = CapabilitySet.convertArrayToList(value);
                }
                Map<Object, Set<BundleCapability>> index = entry.getValue();
                if (value instanceof Collection) {
                    Collection c = (Collection)value;
                    for (Object o : c) {
                        this.deindexCapability(index, cap, o);
                    }
                    continue;
                }
                this.deindexCapability(index, cap, value);
            }
        }
    }

    private void deindexCapability(Map<Object, Set<BundleCapability>> index, BundleCapability cap, Object value) {
        Set<BundleCapability> caps = index.get(value);
        if (caps != null) {
            caps.remove(cap);
            if (caps.isEmpty()) {
                index.remove(value);
            }
        }
    }

    public Set<Capability> match(SimpleFilter sf, boolean obeyMandatory) {
        Set<Capability> matches = this.match(this.m_capSet, sf);
        return obeyMandatory ? CapabilitySet.matchMandatory(matches, sf) : matches;
    }

    private Set<Capability> match(Set<Capability> caps, SimpleFilter sf) {
        Set<Capability> matches;
        block5: {
            block9: {
                block8: {
                    block7: {
                        block6: {
                            block4: {
                                matches = Collections.newSetFromMap(new ConcurrentHashMap());
                                if (sf.getOperation() != 0) break block4;
                                matches.addAll(caps);
                                break block5;
                            }
                            if (sf.getOperation() != 1) break block6;
                            List sfs = (List)sf.getValue();
                            for (int i = 0; caps.size() > 0 && i < sfs.size(); ++i) {
                                matches = this.match(caps, (SimpleFilter)sfs.get(i));
                                caps = matches;
                            }
                            break block5;
                        }
                        if (sf.getOperation() != 2) break block7;
                        List sfs = (List)sf.getValue();
                        for (int i = 0; i < sfs.size(); ++i) {
                            matches.addAll(this.match(caps, (SimpleFilter)sfs.get(i)));
                        }
                        break block5;
                    }
                    if (sf.getOperation() != 3) break block8;
                    matches.addAll(caps);
                    List sfs = (List)sf.getValue();
                    for (int i = 0; i < sfs.size(); ++i) {
                        matches.removeAll(this.match(caps, (SimpleFilter)sfs.get(i)));
                    }
                    break block5;
                }
                Map index = (Map)this.m_indices.get(sf.getName());
                if (sf.getOperation() != 4 || index == null) break block9;
                Set existingCaps = (Set)index.get(sf.getValue());
                if (existingCaps == null) break block5;
                matches.addAll(existingCaps);
                if (caps == this.m_capSet) break block5;
                matches.retainAll(caps);
                break block5;
            }
            for (Capability cap : caps) {
                Object lhs = cap.getAttributes().get(sf.getName());
                if (lhs == null || !CapabilitySet.compare(lhs, sf.getValue(), sf.getOperation())) continue;
                matches.add(cap);
            }
        }
        return matches;
    }

    public static boolean matches(Capability cap, SimpleFilter sf) {
        return CapabilitySet.matchesInternal(cap, sf) && CapabilitySet.matchMandatory(cap, sf);
    }

    private static boolean matchesInternal(Capability cap, SimpleFilter sf) {
        boolean matched = true;
        if (sf.getOperation() == 0) {
            matched = true;
        } else if (sf.getOperation() == 1) {
            List sfs = (List)sf.getValue();
            for (int i = 0; matched && i < sfs.size(); ++i) {
                matched = CapabilitySet.matchesInternal(cap, (SimpleFilter)sfs.get(i));
            }
        } else if (sf.getOperation() == 2) {
            matched = false;
            List sfs = (List)sf.getValue();
            for (int i = 0; !matched && i < sfs.size(); ++i) {
                matched = CapabilitySet.matchesInternal(cap, (SimpleFilter)sfs.get(i));
            }
        } else if (sf.getOperation() == 3) {
            List sfs = (List)sf.getValue();
            for (int i = 0; i < sfs.size(); ++i) {
                matched = !CapabilitySet.matchesInternal(cap, (SimpleFilter)sfs.get(i));
            }
        } else {
            matched = false;
            Object lhs = cap.getAttributes().get(sf.getName());
            if (lhs != null) {
                matched = CapabilitySet.compare(lhs, sf.getValue(), sf.getOperation());
            }
        }
        return matched;
    }

    private static Set<Capability> matchMandatory(Set<Capability> caps, SimpleFilter sf) {
        Iterator<Capability> it = caps.iterator();
        while (it.hasNext()) {
            Capability cap = it.next();
            if (CapabilitySet.matchMandatory(cap, sf)) continue;
            it.remove();
        }
        return caps;
    }

    private static boolean matchMandatory(Capability cap, SimpleFilter sf) {
        Map<String, Object> attrs = cap.getAttributes();
        for (Map.Entry<String, Object> entry : attrs.entrySet()) {
            if (!((BundleCapabilityImpl)cap).isAttributeMandatory(entry.getKey()) || CapabilitySet.matchMandatoryAttribute(entry.getKey(), sf)) continue;
            return false;
        }
        return true;
    }

    private static boolean matchMandatoryAttribute(String attrName, SimpleFilter sf) {
        if (sf.getName() != null && sf.getName().equals(attrName)) {
            return true;
        }
        if (sf.getOperation() == 1) {
            List list = (List)sf.getValue();
            for (int i = 0; i < list.size(); ++i) {
                SimpleFilter sf2 = (SimpleFilter)list.get(i);
                if (sf2.getName() == null || !sf2.getName().equals(attrName)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean compare(Object lhs, Object rhsUnknown, int op) {
        Object rhs;
        if (lhs == null) {
            return false;
        }
        if (op == 8) {
            return true;
        }
        if (lhs instanceof Version && op == 4) {
            rhs = null;
            try {
                rhs = CapabilitySet.coerceType(lhs, (String)rhsUnknown);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (rhs != null && rhs instanceof VersionRange) {
                return ((VersionRange)rhs).includes((Version)lhs);
            }
        }
        if (lhs instanceof Comparable) {
            if (op == 7 && !(lhs instanceof String)) {
                return false;
            }
            if (op == 7) {
                rhs = rhsUnknown;
            } else {
                try {
                    rhs = CapabilitySet.coerceType(lhs, (String)rhsUnknown);
                }
                catch (Exception ex) {
                    return false;
                }
            }
            switch (op) {
                case 4: {
                    try {
                        return ((Comparable)lhs).compareTo(rhs) == 0;
                    }
                    catch (Exception ex) {
                        return false;
                    }
                }
                case 6: {
                    try {
                        return ((Comparable)lhs).compareTo(rhs) >= 0;
                    }
                    catch (Exception ex) {
                        return false;
                    }
                }
                case 5: {
                    try {
                        return ((Comparable)lhs).compareTo(rhs) <= 0;
                    }
                    catch (Exception ex) {
                        return false;
                    }
                }
                case 9: {
                    return CapabilitySet.compareApproximate(lhs, rhs);
                }
                case 7: {
                    return SimpleFilter.compareSubstring((List)rhs, (String)lhs);
                }
            }
            throw new RuntimeException("Unknown comparison operator: " + op);
        }
        if (lhs instanceof Boolean) {
            try {
                rhs = CapabilitySet.coerceType(lhs, (String)rhsUnknown);
            }
            catch (Exception ex) {
                return false;
            }
            switch (op) {
                case 4: 
                case 5: 
                case 6: 
                case 9: {
                    return lhs.equals(rhs);
                }
            }
            throw new RuntimeException("Unknown comparison operator: " + op);
        }
        if (lhs.getClass().isArray()) {
            lhs = CapabilitySet.convertArrayToList(lhs);
        }
        if (lhs instanceof Collection) {
            Iterator iter = ((Collection)lhs).iterator();
            while (iter.hasNext()) {
                if (!CapabilitySet.compare(iter.next(), rhsUnknown, op)) continue;
                return true;
            }
            return false;
        }
        if (op == 7 && !(lhs instanceof String)) {
            return false;
        }
        try {
            return lhs.equals(CapabilitySet.coerceType(lhs, (String)rhsUnknown));
        }
        catch (Exception ex) {
            return false;
        }
    }

    private static boolean compareApproximate(Object lhs, Object rhs) {
        if (rhs instanceof String) {
            return CapabilitySet.removeWhitespace((String)lhs).equalsIgnoreCase(CapabilitySet.removeWhitespace((String)rhs));
        }
        if (rhs instanceof Character) {
            return Character.toLowerCase(((Character)lhs).charValue()) == Character.toLowerCase(((Character)rhs).charValue());
        }
        return lhs.equals(rhs);
    }

    private static String removeWhitespace(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); ++i) {
            if (Character.isWhitespace(s.charAt(i))) continue;
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    private static Object coerceType(Object lhs, String rhsString) throws Exception {
        Object rhs;
        block10: {
            if (lhs instanceof String) {
                return rhsString;
            }
            rhs = null;
            try {
                if (lhs instanceof Character) {
                    rhs = new Character(rhsString.charAt(0));
                    break block10;
                }
                if (lhs instanceof Version && rhsString.indexOf(44) >= 0) {
                    rhs = new VersionRange(rhsString);
                    break block10;
                }
                if (lhs instanceof Number || lhs instanceof Boolean) {
                    rhsString = rhsString.trim();
                }
                try {
                    Method valueOfMethod = m_secureAction.getDeclaredMethod(lhs.getClass(), VALUE_OF_METHOD_NAME, STRING_CLASS);
                    if (valueOfMethod.getReturnType().isAssignableFrom(lhs.getClass()) && (valueOfMethod.getModifiers() & 8) > 0) {
                        m_secureAction.setAccesssible(valueOfMethod);
                        rhs = valueOfMethod.invoke(null, rhsString);
                    }
                }
                catch (Exception valueOfMethod) {
                    // empty catch block
                }
                if (rhs == null) {
                    Constructor ctor = m_secureAction.getConstructor(lhs.getClass(), STRING_CLASS);
                    m_secureAction.setAccesssible(ctor);
                    rhs = ctor.newInstance(rhsString);
                }
            }
            catch (Exception ex) {
                throw new Exception("Could not instantiate class " + lhs.getClass().getName() + " from string constructor with argument '" + rhsString + "' because " + ex);
            }
        }
        return rhs;
    }

    private static List convertArrayToList(Object array) {
        int len = Array.getLength(array);
        ArrayList<Object> list = new ArrayList<Object>(len);
        for (int i = 0; i < len; ++i) {
            list.add(Array.get(array, i));
        }
        return list;
    }
}

