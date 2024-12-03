/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.env;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.env.AccessRestriction;
import org.eclipse.jdt.internal.compiler.env.AccessRule;

public class AccessRuleSet {
    private AccessRule[] accessRules;
    public byte classpathEntryType;
    public String classpathEntryName;

    public AccessRuleSet(AccessRule[] accessRules, byte classpathEntryType, String classpathEntryName) {
        this.accessRules = accessRules;
        this.classpathEntryType = classpathEntryType;
        this.classpathEntryName = classpathEntryName;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof AccessRuleSet)) {
            return false;
        }
        AccessRuleSet otherRuleSet = (AccessRuleSet)object;
        if (this.classpathEntryType != otherRuleSet.classpathEntryType || this.classpathEntryName == null && otherRuleSet.classpathEntryName != null || !this.classpathEntryName.equals(otherRuleSet.classpathEntryName)) {
            return false;
        }
        int rulesLength = this.accessRules.length;
        if (rulesLength != otherRuleSet.accessRules.length) {
            return false;
        }
        int i = 0;
        while (i < rulesLength) {
            if (!this.accessRules[i].equals(otherRuleSet.accessRules[i])) {
                return false;
            }
            ++i;
        }
        return true;
    }

    public AccessRule[] getAccessRules() {
        return this.accessRules;
    }

    public AccessRestriction getViolatedRestriction(char[] targetTypeFilePath) {
        int i = 0;
        int length = this.accessRules.length;
        while (i < length) {
            AccessRule accessRule = this.accessRules[i];
            if (CharOperation.pathMatch(accessRule.pattern, targetTypeFilePath, true, '/')) {
                switch (accessRule.getProblemId()) {
                    case 0x1000118: 
                    case 0x1000133: {
                        return new AccessRestriction(accessRule, this.classpathEntryType, this.classpathEntryName);
                    }
                }
                return null;
            }
            ++i;
        }
        return null;
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.hashCode(this.accessRules);
        result = 31 * result + (this.classpathEntryName == null ? 0 : this.classpathEntryName.hashCode());
        result = 31 * result + this.classpathEntryType;
        return result;
    }

    private int hashCode(AccessRule[] rules) {
        if (rules == null) {
            return 0;
        }
        int result = 1;
        int i = 0;
        int length = rules.length;
        while (i < length) {
            result = 31 * result + (rules[i] == null ? 0 : rules[i].hashCode());
            ++i;
        }
        return result;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean wrap) {
        StringBuffer buffer = new StringBuffer(200);
        buffer.append("AccessRuleSet {");
        if (wrap) {
            buffer.append('\n');
        }
        int i = 0;
        int length = this.accessRules.length;
        while (i < length) {
            if (wrap) {
                buffer.append('\t');
            }
            AccessRule accessRule = this.accessRules[i];
            buffer.append(accessRule);
            if (wrap) {
                buffer.append('\n');
            } else if (i < length - 1) {
                buffer.append(", ");
            }
            ++i;
        }
        buffer.append("} [classpath entry: ");
        buffer.append(this.classpathEntryName);
        buffer.append("]");
        return buffer.toString();
    }
}

