/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.policy;

import com.sun.xml.ws.policy.AssertionSet;
import com.sun.xml.ws.policy.privateutil.LocalizationMessages;
import com.sun.xml.ws.policy.privateutil.PolicyUtils;
import com.sun.xml.ws.policy.sourcemodel.wspolicy.NamespaceVersion;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.xml.namespace.QName;

public class Policy
implements Iterable<AssertionSet> {
    private static final String POLICY_TOSTRING_NAME = "policy";
    private static final List<AssertionSet> NULL_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList());
    private static final List<AssertionSet> EMPTY_POLICY_ASSERTION_SETS = Collections.unmodifiableList(new LinkedList<AssertionSet>(Arrays.asList(AssertionSet.emptyAssertionSet())));
    private static final Set<QName> EMPTY_VOCABULARY = Collections.unmodifiableSet(new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR));
    private static final Policy ANONYMOUS_NULL_POLICY = new Policy(null, null, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    private static final Policy ANONYMOUS_EMPTY_POLICY = new Policy(null, null, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    private String policyId;
    private String name;
    private NamespaceVersion nsVersion;
    private final List<AssertionSet> assertionSets;
    private final Set<QName> vocabulary;
    private final Collection<QName> immutableVocabulary;
    private final String toStringName;

    public static Policy createNullPolicy() {
        return ANONYMOUS_NULL_POLICY;
    }

    public static Policy createEmptyPolicy() {
        return ANONYMOUS_EMPTY_POLICY;
    }

    public static Policy createNullPolicy(String name, String policyId) {
        if (name == null && policyId == null) {
            return ANONYMOUS_NULL_POLICY;
        }
        return new Policy(name, policyId, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    }

    public static Policy createNullPolicy(NamespaceVersion nsVersion, String name, String policyId) {
        if ((nsVersion == null || nsVersion == NamespaceVersion.getLatestVersion()) && name == null && policyId == null) {
            return ANONYMOUS_NULL_POLICY;
        }
        return new Policy(nsVersion, name, policyId, NULL_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    }

    public static Policy createEmptyPolicy(String name, String policyId) {
        if (name == null && policyId == null) {
            return ANONYMOUS_EMPTY_POLICY;
        }
        return new Policy(name, policyId, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    }

    public static Policy createEmptyPolicy(NamespaceVersion nsVersion, String name, String policyId) {
        if ((nsVersion == null || nsVersion == NamespaceVersion.getLatestVersion()) && name == null && policyId == null) {
            return ANONYMOUS_EMPTY_POLICY;
        }
        return new Policy(nsVersion, name, policyId, EMPTY_POLICY_ASSERTION_SETS, EMPTY_VOCABULARY);
    }

    public static Policy createPolicy(Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return Policy.createNullPolicy();
        }
        return new Policy(POLICY_TOSTRING_NAME, sets);
    }

    public static Policy createPolicy(String name, String policyId, Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return Policy.createNullPolicy(name, policyId);
        }
        return new Policy(POLICY_TOSTRING_NAME, name, policyId, sets);
    }

    public static Policy createPolicy(NamespaceVersion nsVersion, String name, String policyId, Collection<AssertionSet> sets) {
        if (sets == null || sets.isEmpty()) {
            return Policy.createNullPolicy(nsVersion, name, policyId);
        }
        return new Policy(nsVersion, POLICY_TOSTRING_NAME, name, policyId, sets);
    }

    private Policy(String name, String policyId, List<AssertionSet> assertionSets, Set<QName> vocabulary) {
        this.nsVersion = NamespaceVersion.getLatestVersion();
        this.toStringName = POLICY_TOSTRING_NAME;
        this.name = name;
        this.policyId = policyId;
        this.assertionSets = assertionSets;
        this.vocabulary = vocabulary;
        this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
    }

    Policy(String toStringName, Collection<AssertionSet> sets) {
        this.nsVersion = NamespaceVersion.getLatestVersion();
        this.toStringName = toStringName;
        if (sets == null || sets.isEmpty()) {
            this.assertionSets = NULL_POLICY_ASSERTION_SETS;
            this.vocabulary = EMPTY_VOCABULARY;
            this.immutableVocabulary = EMPTY_VOCABULARY;
        } else {
            this.assertionSets = new LinkedList<AssertionSet>();
            this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
            this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
            this.addAll(sets);
        }
    }

    Policy(String toStringName, String name, String policyId, Collection<AssertionSet> sets) {
        this(toStringName, sets);
        this.name = name;
        this.policyId = policyId;
    }

    private Policy(NamespaceVersion nsVersion, String name, String policyId, List<AssertionSet> assertionSets, Set<QName> vocabulary) {
        this.nsVersion = nsVersion;
        this.toStringName = POLICY_TOSTRING_NAME;
        this.name = name;
        this.policyId = policyId;
        this.assertionSets = assertionSets;
        this.vocabulary = vocabulary;
        this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
    }

    Policy(NamespaceVersion nsVersion, String toStringName, Collection<AssertionSet> sets) {
        this.nsVersion = nsVersion;
        this.toStringName = toStringName;
        if (sets == null || sets.isEmpty()) {
            this.assertionSets = NULL_POLICY_ASSERTION_SETS;
            this.vocabulary = EMPTY_VOCABULARY;
            this.immutableVocabulary = EMPTY_VOCABULARY;
        } else {
            this.assertionSets = new LinkedList<AssertionSet>();
            this.vocabulary = new TreeSet<QName>(PolicyUtils.Comparison.QNAME_COMPARATOR);
            this.immutableVocabulary = Collections.unmodifiableCollection(this.vocabulary);
            this.addAll(sets);
        }
    }

    Policy(NamespaceVersion nsVersion, String toStringName, String name, String policyId, Collection<AssertionSet> sets) {
        this(nsVersion, toStringName, sets);
        this.name = name;
        this.policyId = policyId;
    }

    private boolean add(AssertionSet set) {
        if (set == null) {
            return false;
        }
        if (this.assertionSets.contains(set)) {
            return false;
        }
        this.assertionSets.add(set);
        this.vocabulary.addAll(set.getVocabulary());
        return true;
    }

    private boolean addAll(Collection<AssertionSet> sets) {
        assert (sets != null && !sets.isEmpty()) : LocalizationMessages.WSP_0036_PRIVATE_METHOD_DOES_NOT_ACCEPT_NULL_OR_EMPTY_COLLECTION();
        boolean result = true;
        for (AssertionSet set : sets) {
            result &= this.add(set);
        }
        Collections.sort(this.assertionSets);
        return result;
    }

    Collection<AssertionSet> getContent() {
        return this.assertionSets;
    }

    public String getId() {
        return this.policyId;
    }

    public String getName() {
        return this.name;
    }

    public NamespaceVersion getNamespaceVersion() {
        return this.nsVersion;
    }

    public String getIdOrName() {
        if (this.policyId != null) {
            return this.policyId;
        }
        return this.name;
    }

    public int getNumberOfAssertionSets() {
        return this.assertionSets.size();
    }

    @Override
    public Iterator<AssertionSet> iterator() {
        return this.assertionSets.iterator();
    }

    public boolean isNull() {
        return this.assertionSets.size() == 0;
    }

    public boolean isEmpty() {
        return this.assertionSets.size() == 1 && this.assertionSets.get(0).isEmpty();
    }

    public boolean contains(String namespaceUri) {
        for (QName entry : this.vocabulary) {
            if (!entry.getNamespaceURI().equals(namespaceUri)) continue;
            return true;
        }
        return false;
    }

    public Collection<QName> getVocabulary() {
        return this.immutableVocabulary;
    }

    public boolean contains(QName assertionName) {
        return this.vocabulary.contains(assertionName);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Policy)) {
            return false;
        }
        Policy that = (Policy)obj;
        boolean result = true;
        result = result && this.vocabulary.equals(that.vocabulary);
        result = result && this.assertionSets.size() == that.assertionSets.size() && this.assertionSets.containsAll(that.assertionSets);
        return result;
    }

    public int hashCode() {
        int result = 17;
        result = 37 * result + this.vocabulary.hashCode();
        result = 37 * result + this.assertionSets.hashCode();
        return result;
    }

    public String toString() {
        return this.toString(0, new StringBuffer()).toString();
    }

    StringBuffer toString(int indentLevel, StringBuffer buffer) {
        String indent = PolicyUtils.Text.createIndent(indentLevel);
        String innerIndent = PolicyUtils.Text.createIndent(indentLevel + 1);
        String innerDoubleIndent = PolicyUtils.Text.createIndent(indentLevel + 2);
        buffer.append(indent).append(this.toStringName).append(" {").append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("namespace version = '").append(this.nsVersion.name()).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("id = '").append(this.policyId).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("name = '").append(this.name).append('\'').append(PolicyUtils.Text.NEW_LINE);
        buffer.append(innerIndent).append("vocabulary {").append(PolicyUtils.Text.NEW_LINE);
        if (this.vocabulary.isEmpty()) {
            buffer.append(innerDoubleIndent).append("no entries").append(PolicyUtils.Text.NEW_LINE);
        } else {
            int index = 1;
            for (QName entry : this.vocabulary) {
                buffer.append(innerDoubleIndent).append(index++).append(". entry = '").append(entry.getNamespaceURI()).append(':').append(entry.getLocalPart()).append('\'').append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(innerIndent).append('}').append(PolicyUtils.Text.NEW_LINE);
        if (this.assertionSets.isEmpty()) {
            buffer.append(innerIndent).append("no assertion sets").append(PolicyUtils.Text.NEW_LINE);
        } else {
            for (AssertionSet set : this.assertionSets) {
                set.toString(indentLevel + 1, buffer).append(PolicyUtils.Text.NEW_LINE);
            }
        }
        buffer.append(indent).append('}');
        return buffer;
    }
}

