/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.PolicyAssertion;
import com.sun.xml.ws.policy.PolicyIntersector;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public final class AssertionSet
implements Iterable<PolicyAssertion>,
Comparable<AssertionSet> {
    private static final AssertionSet EMPTY_ASSERTION_SET = new AssertionSet(Collections.unmodifiableList(new LinkedList()));
    private static final Comparator<PolicyAssertion> ASSERTION_COMPARATOR = new Comparator<PolicyAssertion>(){

        @Override
        public int compare(PolicyAssertion pa1, PolicyAssertion pa2) {
            if (pa1.equals(pa2)) {
                return 0;
            }
            int result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(pa1.getName(), pa2.getName());
            if (result != 0) {
                return result;
            }
            result = PolicyUtils.Comparison.compareNullableStrings(pa1.getValue(), pa2.getValue());
            if (result != 0) {
                return result;
            }
            result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedAssertions(), pa2.hasNestedAssertions());
            if (result != 0) {
                return result;
            }
            result = PolicyUtils.Comparison.compareBoolean(pa1.hasNestedPolicy(), pa2.hasNestedPolicy());
            if (result != 0) {
                return result;
            }
            return Math.round(Math.signum(pa1.hashCode() - pa2.hashCode()));
        }
    };
    private final List<PolicyAssertion> assertions;
    private final Set<QName> vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
    private final Collection<QName> immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);

    private AssertionSet(List<PolicyAssertion> list) {
        assert (list != null) : LocalizationMessages.WSP_0037_PRIVATE_CONSTRUCTOR_DOES_NOT_TAKE_NULL();
        this.assertions = list;
    }

    private AssertionSet(Collection<AssertionSet> alternatives) {
        this.assertions = new LinkedList<PolicyAssertion>();
        for (AssertionSet alternative : alternatives) {
            this.addAll(alternative.assertions);
        }
    }

    private boolean add(PolicyAssertion assertion) {
        if (assertion == null) {
            return false;
        }
        if (this.assertions.contains(assertion)) {
            return false;
        }
        this.assertions.add(assertion);
        this.vocabulary.add(assertion.getName());
        return true;
    }

    private boolean addAll(Collection<? extends PolicyAssertion> assertions) {
        boolean result = true;
        if (assertions != null) {
            for (PolicyAssertion policyAssertion : assertions) {
                result &= this.add(policyAssertion);
            }
        }
        return result;
    }

    Collection<PolicyAssertion> getAssertions() {
        return this.assertions;
    }

    Collection<QName> getVocabulary() {
        return this.immutableVocabulary;
    }

    boolean isCompatibleWith(AssertionSet alternative, PolicyIntersector.CompatibilityMode mode) {
        boolean result = mode == PolicyIntersector.CompatibilityMode.LAX || this.vocabulary.equals(alternative.vocabulary);
        result = result && this.areAssertionsCompatible(alternative, mode);
        result = result && alternative.areAssertionsCompatible(this, mode);
        return result;
    }

    private boolean areAssertionsCompatible(AssertionSet alternative, PolicyIntersector.CompatibilityMode mode) {
        block0: for (PolicyAssertion thisAssertion : this.assertions) {
            if (mode != PolicyIntersector.CompatibilityMode.STRICT && thisAssertion.isIgnorable()) continue;
            for (PolicyAssertion thatAssertion : alternative.assertions) {
                if (!thisAssertion.isCompatibleWith(thatAssertion, mode)) continue;
                continue block0;
            }
            return false;
        }
        return true;
    }

    public static AssertionSet createMergedAssertionSet(Collection<AssertionSet> alternatives) {
        if (alternatives == null || alternatives.isEmpty()) {
            return EMPTY_ASSERTION_SET;
        }
        AssertionSet result = new AssertionSet(alternatives);
        Collections.sort(result.assertions, ASSERTION_COMPARATOR);
        return result;
    }

    public static AssertionSet createAssertionSet(Collection<? extends PolicyAssertion> assertions) {
        if (assertions == null || assertions.isEmpty()) {
            return EMPTY_ASSERTION_SET;
        }
        AssertionSet result = new AssertionSet((List<PolicyAssertion>)new LinkedList<PolicyAssertion>());
        result.addAll(assertions);
        Collections.sort(result.assertions, ASSERTION_COMPARATOR);
        return result;
    }

    public static AssertionSet emptyAssertionSet() {
        return EMPTY_ASSERTION_SET;
    }

    @Override
    public Iterator<PolicyAssertion> iterator() {
        return this.assertions.iterator();
    }

    public Collection<PolicyAssertion> get(QName name) {
        LinkedList<PolicyAssertion> matched = new LinkedList<PolicyAssertion>();
        if (this.vocabulary.contains(name)) {
            for (PolicyAssertion assertion : this.assertions) {
                if (!assertion.getName().equals(name)) continue;
                matched.add(assertion);
            }
        }
        return matched;
    }

    public boolean isEmpty() {
        return this.assertions.isEmpty();
    }

    public boolean contains(QName assertionName) {
        return this.vocabulary.contains(assertionName);
    }

    @Override
    public int compareTo(AssertionSet that) {
        if (this.equals(that)) {
            return 0;
        }
        Iterator<QName> vIterator1 = this.getVocabulary().iterator();
        Iterator<QName> vIterator2 = that.getVocabulary().iterator();
        while (vIterator1.hasNext()) {
            QName entry1 = vIterator1.next();
            if (vIterator2.hasNext()) {
                QName entry2 = vIterator2.next();
                int result = PolicyUtils.Comparison.QNAME_COMPARATOR.compare(entry1, entry2);
                if (result == 0) continue;
                return result;
            }
            return 1;
        }
        if (vIterator2.hasNext()) {
            return -1;
        }
        Iterator<PolicyAssertion> pIterator1 = this.getAssertions().iterator();
        Iterator<PolicyAssertion> pIterator2 = that.getAssertions().iterator();
        while (pIterator1.hasNext()) {
            PolicyAssertion pa1 = pIterator1.next();
            if (pIterator2.hasNext()) {
                PolicyAssertion pa2 = pIterator2.next();
                int result = ASSERTION_COMPARATOR.compare(pa1, pa2);
                if (result == 0) continue;
                return result;
            }
            return 1;
        }
        if (pIterator2.hasNext()) {
            return -1;
        }
        return 1;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AssertionSet)) {
            return false;
        }
        AssertionSet that = (AssertionSet)obj;
        boolean result = true;
        result = result && this.vocabulary.equals(that.vocabulary);
        result = result && this.assertions.size() == that.assertions.size() && this.assertions.containsAll(that.assertions);
        return result;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.vocabulary.hashCode();
        result = 37 * result + this.assertions.hashCode();
        return result;
    }

    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    StringBuffer toString(int indentLevel, StringBuffer buffer) {
        String indent = PolicyUtils.Text.createIndent(indentLevel);
        String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        buffer.append(indent).append("assertion set {").append(PolicyUtils.Text.NEW_LINE);
        if (this.assertions.isEmpty()) {
            buffer.append(innerIndent).append("no assertions").append(PolicyUtils.Text.NEW_LINE);
        } else {
            for (PolicyAssertion assertion : this.assertions) {
                assertion.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(indent).append('}');
        return buffer;
    }
}

