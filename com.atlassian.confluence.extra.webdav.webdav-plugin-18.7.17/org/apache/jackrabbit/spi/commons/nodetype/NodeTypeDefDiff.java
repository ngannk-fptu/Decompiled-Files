/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.nodetype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.QItemDefinition;
import org.apache.jackrabbit.spi.QNodeDefinition;
import org.apache.jackrabbit.spi.QNodeTypeDefinition;
import org.apache.jackrabbit.spi.QPropertyDefinition;
import org.apache.jackrabbit.spi.QValueConstraint;
import org.apache.jackrabbit.spi.commons.name.NameConstants;

public class NodeTypeDefDiff {
    public static final int NONE = 0;
    public static final int TRIVIAL = 1;
    public static final int MAJOR = 2;
    private final QNodeTypeDefinition oldDef;
    private final QNodeTypeDefinition newDef;
    private int type;
    private final List<PropDefDiff> propDefDiffs = new ArrayList<PropDefDiff>();
    private final List<ChildNodeDefDiff> childNodeDefDiffs = new ArrayList<ChildNodeDefDiff>();

    private NodeTypeDefDiff(QNodeTypeDefinition oldDef, QNodeTypeDefinition newDef) {
        this.oldDef = oldDef;
        this.newDef = newDef;
        if (oldDef.equals(newDef)) {
            this.type = 0;
        } else {
            this.type = 1;
            int tmpType = this.supertypesDiff();
            if (tmpType > this.type) {
                this.type = tmpType;
            }
            if ((tmpType = this.mixinFlagDiff()) > this.type) {
                this.type = tmpType;
            }
            if ((tmpType = this.abstractFlagDiff()) > this.type) {
                this.type = tmpType;
            }
            PropDefDiffBuilder propDefDiffBuilder = new PropDefDiffBuilder(oldDef.getPropertyDefs(), newDef.getPropertyDefs());
            this.propDefDiffs.addAll(propDefDiffBuilder.getChildItemDefDiffs());
            tmpType = propDefDiffBuilder.getMaxType();
            if (tmpType > this.type) {
                this.type = tmpType;
            }
            ChildNodeDefDiffBuilder childNodeDefDiffBuilder = new ChildNodeDefDiffBuilder(oldDef.getChildNodeDefs(), newDef.getChildNodeDefs());
            this.childNodeDefDiffs.addAll(childNodeDefDiffBuilder.getChildItemDefDiffs());
            tmpType = childNodeDefDiffBuilder.getMaxType();
            if (tmpType > this.type) {
                this.type = tmpType;
            }
        }
    }

    public static NodeTypeDefDiff create(QNodeTypeDefinition oldDef, QNodeTypeDefinition newDef) {
        if (oldDef == null || newDef == null) {
            throw new IllegalArgumentException("arguments can not be null");
        }
        if (!oldDef.getName().equals(newDef.getName())) {
            throw new IllegalArgumentException("at least node type names must be matching");
        }
        return new NodeTypeDefDiff(oldDef, newDef);
    }

    public boolean isModified() {
        return this.type != 0;
    }

    public boolean isTrivial() {
        return this.type == 1;
    }

    public boolean isMajor() {
        return this.type == 2;
    }

    public int getType() {
        return this.type;
    }

    public int mixinFlagDiff() {
        return this.oldDef.isMixin() != this.newDef.isMixin() ? 2 : 0;
    }

    public int abstractFlagDiff() {
        return this.oldDef.isAbstract() && !this.newDef.isAbstract() ? 2 : 0;
    }

    public int supertypesDiff() {
        HashSet<Name> set2;
        HashSet<Name> set1 = new HashSet<Name>(Arrays.asList(this.oldDef.getSupertypes()));
        return !set1.equals(set2 = new HashSet<Name>(Arrays.asList(this.newDef.getSupertypes()))) ? 2 : 0;
    }

    public String toString() {
        String result = this.getClass().getName() + "[\n\tnodeTypeName=" + this.oldDef.getName();
        result = result + ",\n\tmixinFlagDiff=" + this.modificationTypeToString(this.mixinFlagDiff());
        result = result + ",\n\tsupertypesDiff=" + this.modificationTypeToString(this.supertypesDiff());
        result = result + ",\n\tpropertyDifferences=[\n";
        result = result + this.toString(this.propDefDiffs);
        result = result + "\t]";
        result = result + ",\n\tchildNodeDifferences=[\n";
        result = result + this.toString(this.childNodeDefDiffs);
        result = result + "\t]\n";
        result = result + "]\n";
        return result;
    }

    private String toString(List<? extends ChildItemDefDiff> childItemDefDiffs) {
        String result = "";
        Iterator<? extends ChildItemDefDiff> iter = childItemDefDiffs.iterator();
        while (iter.hasNext()) {
            ChildItemDefDiff propDefDiff = iter.next();
            result = result + "\t\t" + propDefDiff;
            if (iter.hasNext()) {
                result = result + ",";
            }
            result = result + "\n";
        }
        return result;
    }

    private String modificationTypeToString(int modificationType) {
        String typeString = "unknown";
        switch (modificationType) {
            case 0: {
                typeString = "NONE";
                break;
            }
            case 1: {
                typeString = "TRIVIAL";
                break;
            }
            case 2: {
                typeString = "MAJOR";
            }
        }
        return typeString;
    }

    private class PropDefDiffBuilder
    extends ChildItemDefDiffBuilder<QPropertyDefinition, PropDefDiff> {
        private PropDefDiffBuilder(QPropertyDefinition[] defs1, QPropertyDefinition[] defs2) {
            super(NodeTypeDefDiff.this, defs1, defs2);
        }

        @Override
        Object createQItemDefinitionId(QPropertyDefinition def) {
            return new QPropertyDefinitionId(def);
        }

        @Override
        PropDefDiff createChildItemDefDiff(QPropertyDefinition def1, QPropertyDefinition def2) {
            return new PropDefDiff(def1, def2);
        }
    }

    private class ChildNodeDefDiffBuilder
    extends ChildItemDefDiffBuilder<QNodeDefinition, ChildNodeDefDiff> {
        private ChildNodeDefDiffBuilder(QNodeDefinition[] defs1, QNodeDefinition[] defs2) {
            super(NodeTypeDefDiff.this, defs1, defs2);
        }

        @Override
        Object createQItemDefinitionId(QNodeDefinition def) {
            return new QNodeDefinitionId(def);
        }

        @Override
        ChildNodeDefDiff createChildItemDefDiff(QNodeDefinition def1, QNodeDefinition def2) {
            return new ChildNodeDefDiff(def1, def2);
        }
    }

    private static class QNodeDefinitionId {
        private Name declaringNodeType;
        private Name name;

        private QNodeDefinitionId(QNodeDefinition def) {
            this.declaringNodeType = def.getDeclaringNodeType();
            this.name = def.getName();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof QNodeDefinitionId) {
                QNodeDefinitionId other = (QNodeDefinitionId)obj;
                return this.declaringNodeType.equals(other.declaringNodeType) && this.name.equals(other.name);
            }
            return false;
        }

        public int hashCode() {
            int h = 17;
            h = 37 * h + this.declaringNodeType.hashCode();
            h = 37 * h + this.name.hashCode();
            return h;
        }
    }

    private static class QPropertyDefinitionId {
        private Name declaringNodeType;
        private Name name;

        private QPropertyDefinitionId(QPropertyDefinition def) {
            this.declaringNodeType = def.getDeclaringNodeType();
            this.name = def.getName();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof QPropertyDefinitionId) {
                QPropertyDefinitionId other = (QPropertyDefinitionId)obj;
                return this.declaringNodeType.equals(other.declaringNodeType) && this.name.equals(other.name);
            }
            return false;
        }

        public int hashCode() {
            int h = 17;
            h = 37 * h + this.declaringNodeType.hashCode();
            h = 37 * h + this.name.hashCode();
            return h;
        }
    }

    private class ChildNodeDefDiff
    extends ChildItemDefDiff<QNodeDefinition> {
        private ChildNodeDefDiff(QNodeDefinition oldDef, QNodeDefinition newDef) {
            super(NodeTypeDefDiff.this, oldDef, newDef);
        }

        @Override
        protected void init() {
            super.init();
            if (this.isModified() && this.type == 1) {
                boolean b2;
                boolean b1 = ((QNodeDefinition)this.getOldDef()).allowsSameNameSiblings();
                if (b1 != (b2 = ((QNodeDefinition)this.getNewDef()).allowsSameNameSiblings()) && !b2) {
                    this.type = 2;
                }
                if (this.type == 1) {
                    HashSet<Name> s1 = new HashSet<Name>(Arrays.asList(((QNodeDefinition)this.getOldDef()).getRequiredPrimaryTypes()));
                    HashSet<Name> s2 = new HashSet<Name>(Arrays.asList(((QNodeDefinition)this.getNewDef()).getRequiredPrimaryTypes()));
                    s1.remove(NameConstants.NT_BASE);
                    s2.remove(NameConstants.NT_BASE);
                    if (!s1.equals(s2)) {
                        this.type = s1.containsAll(s2) ? 1 : 2;
                    }
                }
            }
        }
    }

    private class PropDefDiff
    extends ChildItemDefDiff<QPropertyDefinition> {
        private PropDefDiff(QPropertyDefinition oldDef, QPropertyDefinition newDef) {
            super(NodeTypeDefDiff.this, oldDef, newDef);
        }

        @Override
        protected void init() {
            super.init();
            if (this.isModified() && this.type == 1) {
                QValueConstraint[] vca1 = ((QPropertyDefinition)this.getOldDef()).getValueConstraints();
                HashSet<String> set1 = new HashSet<String>();
                for (QValueConstraint aVca1 : vca1) {
                    set1.add(aVca1.getString());
                }
                QValueConstraint[] vca2 = ((QPropertyDefinition)this.getNewDef()).getValueConstraints();
                HashSet<String> set2 = new HashSet<String>();
                for (QValueConstraint aVca2 : vca2) {
                    set2.add(aVca2.getString());
                }
                if (!set1.equals(set2)) {
                    this.type = set2.isEmpty() ? 1 : (set1.isEmpty() ? 2 : (set2.containsAll(set1) ? 1 : 2));
                }
                if (this.type == 1) {
                    boolean b2;
                    boolean b1;
                    int t2;
                    int t1 = ((QPropertyDefinition)this.getOldDef()).getRequiredType();
                    if (t1 != (t2 = ((QPropertyDefinition)this.getNewDef()).getRequiredType())) {
                        this.type = t2 == 0 ? 1 : 2;
                    }
                    if ((b1 = ((QPropertyDefinition)this.getOldDef()).isMultiple()) != (b2 = ((QPropertyDefinition)this.getNewDef()).isMultiple())) {
                        this.type = b2 ? 1 : 2;
                    }
                }
            }
        }
    }

    private static abstract class ChildItemDefDiff<T extends QItemDefinition> {
        protected final T oldDef;
        protected final T newDef;
        protected int type;
        final /* synthetic */ NodeTypeDefDiff this$0;

        private ChildItemDefDiff(T oldDef, T newDef) {
            this.this$0 = var1_1;
            this.oldDef = oldDef;
            this.newDef = newDef;
            this.init();
        }

        protected void init() {
            this.type = this.isAdded() ? (!this.newDef.isMandatory() ? 1 : 2) : (this.isRemoved() ? 2 : (this.oldDef.equals(this.newDef) ? 0 : (this.oldDef.isMandatory() != this.newDef.isMandatory() && this.newDef.isMandatory() ? 2 : (!this.oldDef.definesResidual() && this.newDef.definesResidual() ? 1 : (!this.oldDef.getName().equals(this.newDef.getName()) ? 2 : 1)))));
        }

        T getOldDef() {
            return this.oldDef;
        }

        T getNewDef() {
            return this.newDef;
        }

        int getType() {
            return this.type;
        }

        boolean isAdded() {
            return this.oldDef == null && this.newDef != null;
        }

        boolean isRemoved() {
            return this.oldDef != null && this.newDef == null;
        }

        boolean isModified() {
            return this.oldDef != null && this.newDef != null && !this.oldDef.equals(this.newDef);
        }

        public String toString() {
            String typeString = this.this$0.modificationTypeToString(this.getType());
            String operationString = this.isAdded() ? "ADDED" : (this.isModified() ? "MODIFIED" : (this.isRemoved() ? "REMOVED" : "NONE"));
            T itemDefinition = this.oldDef != null ? this.oldDef : this.newDef;
            return this.getClass().getName() + "[itemName=" + itemDefinition.getName() + ", type=" + typeString + ", operation=" + operationString + "]";
        }
    }

    private static abstract class ChildItemDefDiffBuilder<T extends QItemDefinition, V extends ChildItemDefDiff<T>> {
        private final List<V> childItemDefDiffs = new ArrayList<V>();
        final /* synthetic */ NodeTypeDefDiff this$0;

        private ChildItemDefDiffBuilder(T[] oldDefs, T[] newDefs) {
            this.this$0 = var1_1;
            this.buildChildItemDefDiffs(this.collectChildNodeDefs((QItemDefinition[])oldDefs), this.collectChildNodeDefs((QItemDefinition[])newDefs));
        }

        private void buildChildItemDefDiffs(Map<Object, List<T>> oldDefs, Map<Object, List<T>> newDefs) {
            for (Object defId : oldDefs.keySet()) {
                this.childItemDefDiffs.addAll(this.getChildItemDefDiffs(oldDefs.get(defId), newDefs.get(defId)));
                newDefs.remove(defId);
            }
            for (Object defId : newDefs.keySet()) {
                this.childItemDefDiffs.addAll(this.getChildItemDefDiffs(null, newDefs.get(defId)));
            }
        }

        private Map<Object, List<T>> collectChildNodeDefs(T[] defs) {
            HashMap<Object, List<T>> result = new HashMap<Object, List<T>>();
            for (T def : defs) {
                Object defId = this.createQItemDefinitionId(def);
                ArrayList<T> list = (ArrayList<T>)result.get(defId);
                if (list == null) {
                    list = new ArrayList<T>();
                    result.put(defId, list);
                }
                list.add(def);
            }
            return result;
        }

        abstract Object createQItemDefinitionId(T var1);

        abstract V createChildItemDefDiff(T var1, T var2);

        Collection<V> getChildItemDefDiffs(List<T> defs1, List<T> defs2) {
            defs1 = defs1 != null ? defs1 : Collections.emptyList();
            defs2 = defs2 != null ? defs2 : Collections.emptyList();
            ArrayList<V> diffs = new ArrayList<V>();
            for (QItemDefinition def1 : defs1) {
                for (QItemDefinition def2 : defs2) {
                    diffs.add(this.createChildItemDefDiff(def1, def2));
                }
            }
            if (defs2.size() < defs1.size()) {
                for (QItemDefinition def1 : defs1) {
                    diffs.add(this.createChildItemDefDiff(def1, null));
                }
            }
            if (defs1.size() < defs2.size()) {
                for (QItemDefinition def2 : defs2) {
                    diffs.add(this.createChildItemDefDiff(null, def2));
                }
            }
            Collections.sort(diffs, new Comparator<V>(){

                @Override
                public int compare(V o1, V o2) {
                    return ((ChildItemDefDiff)o1).getType() - ((ChildItemDefDiff)o2).getType();
                }
            });
            int size = defs1.size() > defs2.size() ? defs1.size() : defs2.size();
            int allowedNewNull = defs1.size() - defs2.size();
            int allowedOldNull = defs2.size() - defs1.size();
            ArrayList<ChildItemDefDiff> results = new ArrayList<ChildItemDefDiff>();
            for (ChildItemDefDiff diff : diffs) {
                if (!this.alreadyMatched(results, diff.getNewDef(), diff.getOldDef(), allowedNewNull, allowedOldNull)) {
                    results.add(diff);
                    if (diff.getNewDef() == null) {
                        --allowedNewNull;
                    }
                    if (diff.getOldDef() == null) {
                        --allowedOldNull;
                    }
                }
                if (results.size() != size) continue;
                break;
            }
            return results;
        }

        private boolean alreadyMatched(List<V> result, T newDef, T oldDef, int allowedNewNull, int allowedOldNull) {
            boolean containsNewDef = false;
            boolean containsOldDef = false;
            for (ChildItemDefDiff d : result) {
                if (d.getNewDef() != null && d.getNewDef().equals(newDef)) {
                    containsNewDef = true;
                    break;
                }
                if (d.getOldDef() == null || !d.getOldDef().equals(oldDef)) continue;
                containsOldDef = true;
                break;
            }
            if (oldDef == null && allowedOldNull < 1) {
                containsOldDef = true;
            }
            if (newDef == null && allowedNewNull < 1) {
                containsNewDef = true;
            }
            return containsNewDef || containsOldDef;
        }

        List<V> getChildItemDefDiffs() {
            return this.childItemDefDiffs;
        }

        int getMaxType() {
            int maxType = 0;
            for (ChildItemDefDiff childItemDefDiff : this.childItemDefDiffs) {
                if (childItemDefDiff.getType() <= maxType) continue;
                maxType = childItemDefDiff.getType();
            }
            return maxType;
        }
    }
}

