/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.preprocessor;

import groovyjarjarantlr.collections.impl.IndexedVector;
import groovyjarjarantlr.preprocessor.Grammar;
import groovyjarjarantlr.preprocessor.Option;
import java.util.Enumeration;

class Rule {
    protected String name;
    protected String block;
    protected String args;
    protected String returnValue;
    protected String throwsSpec;
    protected String initAction;
    protected IndexedVector options;
    protected String visibility;
    protected Grammar enclosingGrammar;
    protected boolean bang = false;

    public Rule(String string, String string2, IndexedVector indexedVector, Grammar grammar) {
        this.name = string;
        this.block = string2;
        this.options = indexedVector;
        this.setEnclosingGrammar(grammar);
    }

    public String getArgs() {
        return this.args;
    }

    public boolean getBang() {
        return this.bang;
    }

    public String getName() {
        return this.name;
    }

    public String getReturnValue() {
        return this.returnValue;
    }

    public String getVisibility() {
        return this.visibility;
    }

    public boolean narrowerVisibility(Rule rule) {
        if (this.visibility.equals("public")) {
            return !rule.equals("public");
        }
        if (this.visibility.equals("protected")) {
            return rule.equals("private");
        }
        if (this.visibility.equals("private")) {
            return false;
        }
        return false;
    }

    public boolean sameSignature(Rule rule) {
        boolean bl = true;
        boolean bl2 = true;
        boolean bl3 = true;
        bl = this.name.equals(rule.getName());
        if (this.args != null) {
            bl2 = this.args.equals(rule.getArgs());
        }
        if (this.returnValue != null) {
            bl3 = this.returnValue.equals(rule.getReturnValue());
        }
        return bl && bl2 && bl3;
    }

    public void setArgs(String string) {
        this.args = string;
    }

    public void setBang() {
        this.bang = true;
    }

    public void setEnclosingGrammar(Grammar grammar) {
        this.enclosingGrammar = grammar;
    }

    public void setInitAction(String string) {
        this.initAction = string;
    }

    public void setOptions(IndexedVector indexedVector) {
        this.options = indexedVector;
    }

    public void setReturnValue(String string) {
        this.returnValue = string;
    }

    public void setThrowsSpec(String string) {
        this.throwsSpec = string;
    }

    public void setVisibility(String string) {
        this.visibility = string;
    }

    public String toString() {
        String string = "";
        String string2 = this.returnValue == null ? "" : "returns " + this.returnValue;
        String string3 = this.args == null ? "" : this.args;
        String string4 = this.getBang() ? "!" : "";
        string = string + (this.visibility == null ? "" : this.visibility + " ");
        string = string + this.name + string4 + string3 + " " + string2 + this.throwsSpec;
        if (this.options != null) {
            string = string + System.getProperty("line.separator") + "options {" + System.getProperty("line.separator");
            Enumeration enumeration = this.options.elements();
            while (enumeration.hasMoreElements()) {
                string = string + (Option)enumeration.nextElement() + System.getProperty("line.separator");
            }
            string = string + "}" + System.getProperty("line.separator");
        }
        if (this.initAction != null) {
            string = string + this.initAction + System.getProperty("line.separator");
        }
        string = string + this.block;
        return string;
    }
}

