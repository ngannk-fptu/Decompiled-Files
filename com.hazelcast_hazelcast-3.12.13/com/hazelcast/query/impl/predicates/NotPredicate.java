/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.VisitablePredicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.predicates.NegatablePredicate;
import com.hazelcast.query.impl.predicates.Visitor;
import java.io.IOException;
import java.util.Map;

@BinaryInterface
public final class NotPredicate
implements Predicate,
VisitablePredicate,
NegatablePredicate,
IdentifiedDataSerializable {
    private static final long serialVersionUID = 1L;
    protected Predicate predicate;

    public NotPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public NotPredicate() {
    }

    public Predicate getPredicate() {
        return this.predicate;
    }

    public boolean apply(Map.Entry mapEntry) {
        return !this.predicate.apply(mapEntry);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.predicate);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.predicate = (Predicate)in.readObject();
    }

    public String toString() {
        return "NOT(" + this.predicate + ")";
    }

    @Override
    public Predicate accept(Visitor visitor, Indexes indexes) {
        Predicate target = this.predicate;
        if (this.predicate instanceof VisitablePredicate) {
            target = ((VisitablePredicate)((Object)this.predicate)).accept(visitor, indexes);
        }
        if (target == this.predicate) {
            return visitor.visit(this, indexes);
        }
        NotPredicate copy = new NotPredicate(target);
        return visitor.visit(copy, indexes);
    }

    @Override
    public Predicate negate() {
        return this.predicate;
    }

    @Override
    public int getFactoryId() {
        return -32;
    }

    @Override
    public int getId() {
        return 10;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof NotPredicate)) {
            return false;
        }
        NotPredicate that = (NotPredicate)o;
        return this.predicate != null ? this.predicate.equals(that.predicate) : that.predicate == null;
    }

    public int hashCode() {
        return this.predicate != null ? this.predicate.hashCode() : 0;
    }
}

