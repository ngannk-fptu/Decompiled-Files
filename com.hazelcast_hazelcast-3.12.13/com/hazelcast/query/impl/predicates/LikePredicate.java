/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.predicates;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.query.impl.predicates.AbstractPredicate;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@BinaryInterface
public class LikePredicate
extends AbstractPredicate {
    private static final long serialVersionUID = 1L;
    protected String expression;
    private volatile transient Pattern pattern;

    public LikePredicate() {
    }

    public LikePredicate(String attributeName, String expression) {
        super(attributeName);
        this.expression = expression;
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        String attributeValueString = (String)((Object)attributeValue);
        if (attributeValueString == null) {
            return this.expression == null;
        }
        if (this.expression == null) {
            return false;
        }
        this.pattern = this.pattern != null ? this.pattern : this.createPattern(this.expression);
        Matcher m = this.pattern.matcher(attributeValueString);
        return m.matches();
    }

    private Pattern createPattern(String expression) {
        String quotedExpression = Pattern.quote(expression);
        String regex = quotedExpression.replaceAll("(?<!\\\\)[%]", "\\\\E.*\\\\Q").replaceAll("(?<!\\\\)[_]", "\\\\E.\\\\Q").replaceAll("\\\\%", "%").replaceAll("\\\\_", "_");
        int flags = this.getFlags();
        return Pattern.compile(regex, flags);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeUTF(this.expression);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.expression = in.readUTF();
    }

    protected int getFlags() {
        return 32;
    }

    public String toString() {
        return this.attributeName + " LIKE '" + this.expression + "'";
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof LikePredicate)) {
            return false;
        }
        LikePredicate that = (LikePredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        return this.expression != null ? this.expression.equals(that.expression) : that.expression == null;
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof LikePredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.expression != null ? this.expression.hashCode() : 0);
        return result;
    }
}

