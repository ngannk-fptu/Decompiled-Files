/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.util.StringHelper;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Term
implements Comparable<Term>,
Serializable {
    String field;
    String text;

    public Term(String fld, String txt) {
        this.field = StringHelper.intern(fld);
        this.text = txt;
    }

    public Term(String fld) {
        this(fld, "", true);
    }

    Term(String fld, String txt, boolean intern) {
        this.field = intern ? StringHelper.intern(fld) : fld;
        this.text = txt;
    }

    public final String field() {
        return this.field;
    }

    public final String text() {
        return this.text;
    }

    public Term createTerm(String text) {
        return new Term(this.field, text, false);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Term other = (Term)obj;
        if (this.field == null ? other.field != null : this.field != other.field) {
            return false;
        }
        return !(this.text == null ? other.text != null : !this.text.equals(other.text));
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
        result = 31 * result + (this.text == null ? 0 : this.text.hashCode());
        return result;
    }

    @Override
    public final int compareTo(Term other) {
        if (this.field == other.field) {
            return this.text.compareTo(other.text);
        }
        return this.field.compareTo(other.field);
    }

    final void set(String fld, String txt) {
        this.field = fld;
        this.text = txt;
    }

    public final String toString() {
        return this.field + ":" + this.text;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.field = StringHelper.intern(this.field);
    }
}

