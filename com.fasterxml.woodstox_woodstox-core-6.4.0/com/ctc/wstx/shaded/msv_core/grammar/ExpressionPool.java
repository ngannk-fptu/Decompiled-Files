/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.UnaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;

public class ExpressionPool
implements Serializable {
    private final ClosedHash expTable;
    private static final long serialVersionUID = 1L;

    public final Expression createAttribute(NameClass nameClass) {
        return this.unify(new AttributeExp(nameClass, Expression.anyString));
    }

    public final Expression createAttribute(NameClass nameClass, Expression content) {
        if (content == Expression.nullSet) {
            return content;
        }
        return this.unify(new AttributeExp(nameClass, content));
    }

    public final Expression createEpsilon() {
        return Expression.epsilon;
    }

    public final Expression createNullSet() {
        return Expression.nullSet;
    }

    public final Expression createAnyString() {
        return Expression.anyString;
    }

    public final Expression createChoice(Expression left, Expression right) {
        if (left == Expression.nullSet) {
            return right;
        }
        if (right == Expression.nullSet) {
            return left;
        }
        if (left == Expression.epsilon && right.isEpsilonReducible()) {
            return right;
        }
        if (right == Expression.epsilon && left.isEpsilonReducible()) {
            return left;
        }
        if (right instanceof ChoiceExp) {
            ChoiceExp c = (ChoiceExp)right;
            return this.createChoice(this.createChoice(left, c.exp1), c.exp2);
        }
        Expression next = left;
        while (true) {
            if (next == right) {
                return left;
            }
            if (!(next instanceof ChoiceExp)) break;
            ChoiceExp cp = (ChoiceExp)next;
            if (cp.exp2 == right) {
                return left;
            }
            next = cp.exp1;
        }
        Expression o = this.expTable.getBinExp(left, right, ChoiceExp.class);
        if (o == null) {
            return this.unify(new ChoiceExp(left, right));
        }
        return o;
    }

    public final Expression createOneOrMore(Expression child) {
        if (child == Expression.epsilon || child == Expression.anyString || child == Expression.nullSet || child instanceof OneOrMoreExp) {
            return child;
        }
        return this.unify(new OneOrMoreExp(child));
    }

    public final Expression createZeroOrMore(Expression child) {
        return this.createOptional(this.createOneOrMore(child));
    }

    public final Expression createOptional(Expression child) {
        return this.createChoice(child, Expression.epsilon);
    }

    public final Expression createData(XSDatatype dt) {
        String ns = dt.getNamespaceUri();
        if (ns == null) {
            ns = "\u0000";
        }
        return this.createData(dt, new StringPair(ns, dt.displayName()));
    }

    public final Expression createData(Datatype dt, StringPair typeName) {
        return this.createData(dt, typeName, Expression.nullSet);
    }

    public final Expression createData(Datatype dt, StringPair typeName, Expression except) {
        return this.unify(new DataExp(dt, typeName, except));
    }

    public final Expression createValue(XSDatatype dt, Object value) {
        return this.createValue(dt, new StringPair("", dt.displayName()), value);
    }

    public final Expression createValue(Datatype dt, StringPair typeName, Object value) {
        return this.unify(new ValueExp(dt, typeName, value));
    }

    public final Expression createList(Expression exp) {
        if (exp == Expression.nullSet) {
            return exp;
        }
        return this.unify(new ListExp(exp));
    }

    public final Expression createMixed(Expression body) {
        if (body == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (body == Expression.epsilon) {
            return Expression.anyString;
        }
        return this.unify(new MixedExp(body));
    }

    public final Expression createSequence(Expression left, Expression right) {
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (left == Expression.epsilon) {
            return right;
        }
        if (right == Expression.epsilon) {
            return left;
        }
        if (right instanceof SequenceExp) {
            SequenceExp s = (SequenceExp)right;
            return this.createSequence(this.createSequence(left, s.exp1), s.exp2);
        }
        Expression o = this.expTable.getBinExp(left, right, SequenceExp.class);
        if (o == null) {
            return this.unify(new SequenceExp(left, right));
        }
        return o;
    }

    public final Expression createConcur(Expression left, Expression right) {
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (left == Expression.epsilon) {
            if (right.isEpsilonReducible()) {
                return Expression.epsilon;
            }
            return Expression.nullSet;
        }
        if (right == Expression.epsilon) {
            if (left.isEpsilonReducible()) {
                return Expression.epsilon;
            }
            return Expression.nullSet;
        }
        if (right instanceof ConcurExp) {
            ConcurExp c = (ConcurExp)right;
            return this.createConcur(this.createConcur(left, c.exp1), c.exp2);
        }
        return this.unify(new ConcurExp(left, right));
    }

    public final Expression createInterleave(Expression left, Expression right) {
        if (left == Expression.epsilon) {
            return right;
        }
        if (right == Expression.epsilon) {
            return left;
        }
        if (left == Expression.nullSet || right == Expression.nullSet) {
            return Expression.nullSet;
        }
        if (right instanceof InterleaveExp) {
            InterleaveExp i = (InterleaveExp)right;
            return this.createInterleave(this.createInterleave(left, i.exp1), i.exp2);
        }
        return this.unify(new InterleaveExp(left, right));
    }

    public ExpressionPool(ExpressionPool parent) {
        this.expTable = new ClosedHash(parent.expTable);
    }

    public ExpressionPool() {
        this.expTable = new ClosedHash();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final Expression unify(Expression exp) {
        Expression o = this.expTable.get(exp);
        if (o == null) {
            ClosedHash closedHash = this.expTable;
            synchronized (closedHash) {
                o = this.expTable.get(exp);
                if (o == null) {
                    this.expTable.put(exp);
                    return exp;
                }
            }
        }
        return o;
    }

    public static final class ClosedHash
    implements Serializable {
        private Expression[] table = new Expression[191];
        private int count;
        private int threshold = 57;
        private static final float loadFactor = 0.3f;
        private static final int initialCapacity = 191;
        private ClosedHash parent;
        private static final long serialVersionUID = -2924295970572669668L;
        private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("count", Integer.TYPE), new ObjectStreamField("streamVersion", Byte.TYPE), new ObjectStreamField("parent", ExpressionPool.class)};

        public ClosedHash() {
            this(null);
        }

        public ClosedHash(ClosedHash parent) {
            this.parent = parent;
        }

        public Expression getBinExp(Expression left, Expression right, Class type) {
            int hash = left.hashCode() + right.hashCode() ^ type.hashCode();
            return this.getBinExp(hash, left, right, type);
        }

        private Expression getBinExp(int hash, Expression left, Expression right, Class type) {
            Expression e;
            if (this.parent != null && (e = this.parent.getBinExp(hash, left, right, type)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Expression e2;
            while ((e2 = tab[index]) != null) {
                if (e2.hashCode() == hash && e2.getClass() == type) {
                    BinaryExp be = (BinaryExp)e2;
                    if (be.exp1 == left && be.exp2 == right) {
                        return be;
                    }
                }
                index = (index + 1) % tab.length;
            }
            return null;
        }

        public Expression get(int hash, Expression child, Class type) {
            Expression e;
            if (this.parent != null && (e = this.parent.get(hash, child, type)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int index = (hash & Integer.MAX_VALUE) % tab.length;
            Expression e2;
            while ((e2 = tab[index]) != null) {
                if (e2.hashCode() == hash && e2.getClass() == type) {
                    UnaryExp ue = (UnaryExp)e2;
                    if (ue.exp == child) {
                        return ue;
                    }
                }
                index = (index + 1) % tab.length;
            }
            return null;
        }

        public Expression get(Expression key) {
            Expression e;
            if (this.parent != null && (e = this.parent.get(key)) != null) {
                return e;
            }
            Expression[] tab = this.table;
            int index = (key.hashCode() & Integer.MAX_VALUE) % tab.length;
            Expression e2;
            while ((e2 = tab[index]) != null) {
                if (e2.equals(key)) {
                    return e2;
                }
                index = (index + 1) % tab.length;
            }
            return null;
        }

        private void rehash() {
            int oldCapacity = this.table.length;
            Expression[] oldMap = this.table;
            int newCapacity = oldCapacity * 2 + 1;
            Expression[] newMap = new Expression[newCapacity];
            int i = oldCapacity;
            while (i-- > 0) {
                if (oldMap[i] == null) continue;
                int index = (oldMap[i].hashCode() & Integer.MAX_VALUE) % newMap.length;
                while (newMap[index] != null) {
                    index = (index + 1) % newMap.length;
                }
                newMap[index] = oldMap[i];
            }
            this.threshold = (int)((float)newCapacity * 0.3f);
            this.table = newMap;
        }

        public void put(Expression newExp) {
            if (this.count >= this.threshold) {
                this.rehash();
            }
            Expression[] tab = this.table;
            int index = (newExp.hashCode() & Integer.MAX_VALUE) % tab.length;
            while (tab[index] != null) {
                index = (index + 1) % tab.length;
            }
            tab[index] = newExp;
            ++this.count;
        }

        private void writeObject(ObjectOutputStream s) throws IOException {
            ObjectOutputStream.PutField fields = s.putFields();
            fields.put("count", this.count);
            fields.put("parent", this.parent);
            fields.put("streamVersion", (byte)1);
            s.writeFields();
            for (int i = 0; i < this.table.length; ++i) {
                if (this.table[i] == null) continue;
                s.writeObject(this.table[i]);
            }
        }

        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            ObjectInputStream.GetField fields = s.readFields();
            byte version = fields.get("streamVersion", (byte)0);
            if (version == 0) {
                this.count = fields.get("count", 0);
                this.parent = (ClosedHash)fields.get("parent", null);
                this.table = (Expression[])fields.get("table", null);
                this.threshold = fields.get("threshold", 0);
            } else {
                int objCnt = fields.get("count", 0);
                this.parent = (ClosedHash)fields.get("parent", null);
                int size = (int)((float)objCnt / 0.3f) * 2 + 10;
                this.threshold = this.count * 2;
                this.count = 0;
                this.table = new Expression[size];
                for (int i = 0; i < this.count; ++i) {
                    this.put((Expression)s.readObject());
                }
            }
        }
    }
}

