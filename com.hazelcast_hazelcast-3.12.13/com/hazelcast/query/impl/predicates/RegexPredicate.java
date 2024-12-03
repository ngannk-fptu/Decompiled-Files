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
public class RegexPredicate
extends AbstractPredicate {
    private static final long serialVersionUID = 1L;
    private String regex;
    private volatile transient Pattern pattern;

    public RegexPredicate() {
    }

    public RegexPredicate(String attributeName, String regex) {
        super(attributeName);
        this.regex = regex;
    }

    @Override
    protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
        String stringAttributeValue = (String)((Object)attributeValue);
        if (stringAttributeValue == null) {
            return this.regex == null;
        }
        if (this.regex == null) {
            return false;
        }
        if (this.pattern == null) {
            this.pattern = Pattern.compile(this.regex);
        }
        Matcher m = this.pattern.matcher(stringAttributeValue);
        return m.matches();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeUTF(this.regex);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.regex = in.readUTF();
    }

    public String toString() {
        return this.attributeName + " REGEX '" + this.regex + "'";
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!super.equals(o)) {
            return false;
        }
        if (!(o instanceof RegexPredicate)) {
            return false;
        }
        RegexPredicate that = (RegexPredicate)o;
        if (!that.canEqual(this)) {
            return false;
        }
        return this.regex != null ? this.regex.equals(that.regex) : that.regex == null;
    }

    @Override
    public boolean canEqual(Object other) {
        return other instanceof RegexPredicate;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.regex != null ? this.regex.hashCode() : 0);
        return result;
    }
}

