/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.preprocessor;

import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.Tool;
import groovyjarjarantlr.collections.impl.IndexedVector;
import groovyjarjarantlr.preprocessor.GrammarFile;
import groovyjarjarantlr.preprocessor.Hierarchy;
import groovyjarjarantlr.preprocessor.Option;
import groovyjarjarantlr.preprocessor.Rule;
import java.io.IOException;

class Grammar {
    protected String name;
    protected String fileName;
    protected String superGrammar;
    protected String type;
    protected IndexedVector rules;
    protected IndexedVector options;
    protected String tokenSection;
    protected String preambleAction;
    protected String memberAction;
    protected Hierarchy hier;
    protected boolean predefined = false;
    protected boolean alreadyExpanded = false;
    protected boolean specifiedVocabulary = false;
    protected String superClass = null;
    protected String importVocab = null;
    protected String exportVocab = null;
    protected Tool antlrTool;

    public Grammar(Tool tool, String string, String string2, IndexedVector indexedVector) {
        this.name = string;
        this.superGrammar = string2;
        this.rules = indexedVector;
        this.antlrTool = tool;
    }

    public void addOption(Option option) {
        if (this.options == null) {
            this.options = new IndexedVector();
        }
        this.options.appendElement(option.getName(), option);
    }

    public void addRule(Rule rule) {
        this.rules.appendElement(rule.getName(), rule);
    }

    public void expandInPlace() {
        Object object;
        Object object2;
        if (this.alreadyExpanded) {
            return;
        }
        Grammar grammar = this.getSuperGrammar();
        if (grammar == null) {
            return;
        }
        if (this.exportVocab == null) {
            this.exportVocab = this.getName();
        }
        if (grammar.isPredefined()) {
            return;
        }
        grammar.expandInPlace();
        this.alreadyExpanded = true;
        GrammarFile grammarFile = this.hier.getFile(this.getFileName());
        grammarFile.setExpanded(true);
        IndexedVector indexedVector = grammar.getRules();
        Object object3 = indexedVector.elements();
        while (object3.hasMoreElements()) {
            object2 = (Rule)object3.nextElement();
            this.inherit((Rule)object2, grammar);
        }
        object3 = grammar.getOptions();
        if (object3 != null) {
            object2 = ((IndexedVector)object3).elements();
            while (object2.hasMoreElements()) {
                object = (Option)object2.nextElement();
                this.inherit((Option)object, grammar);
            }
        }
        if (this.options != null && this.options.getElement("importVocab") == null || this.options == null) {
            object2 = new Option("importVocab", grammar.exportVocab + ";", this);
            this.addOption((Option)object2);
            object = grammar.getFileName();
            String string = this.antlrTool.pathToFile((String)object);
            String string2 = string + grammar.exportVocab + CodeGenerator.TokenTypesFileSuffix + CodeGenerator.TokenTypesFileExt;
            String string3 = this.antlrTool.fileMinusPath(string2);
            if (!string.equals("." + System.getProperty("file.separator"))) {
                try {
                    this.antlrTool.copyFile(string2, string3);
                }
                catch (IOException iOException) {
                    this.antlrTool.toolError("cannot find/copy importVocab file " + string2);
                    return;
                }
            }
        }
        this.inherit(grammar.memberAction, grammar);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getName() {
        return this.name;
    }

    public IndexedVector getOptions() {
        return this.options;
    }

    public IndexedVector getRules() {
        return this.rules;
    }

    public Grammar getSuperGrammar() {
        if (this.superGrammar == null) {
            return null;
        }
        Grammar grammar = this.hier.getGrammar(this.superGrammar);
        return grammar;
    }

    public String getSuperGrammarName() {
        return this.superGrammar;
    }

    public String getType() {
        return this.type;
    }

    public void inherit(Option option, Grammar grammar) {
        if (option.getName().equals("importVocab") || option.getName().equals("exportVocab")) {
            return;
        }
        Option option2 = null;
        if (this.options != null) {
            option2 = (Option)this.options.getElement(option.getName());
        }
        if (option2 == null) {
            this.addOption(option);
        }
    }

    public void inherit(Rule rule, Grammar grammar) {
        Rule rule2 = (Rule)this.rules.getElement(rule.getName());
        if (rule2 != null) {
            if (!rule2.sameSignature(rule)) {
                this.antlrTool.warning("rule " + this.getName() + "." + rule2.getName() + " has different signature than " + grammar.getName() + "." + rule2.getName());
            }
        } else {
            this.addRule(rule);
        }
    }

    public void inherit(String string, Grammar grammar) {
        if (this.memberAction != null) {
            return;
        }
        if (string != null) {
            this.memberAction = string;
        }
    }

    public boolean isPredefined() {
        return this.predefined;
    }

    public void setFileName(String string) {
        this.fileName = string;
    }

    public void setHierarchy(Hierarchy hierarchy) {
        this.hier = hierarchy;
    }

    public void setMemberAction(String string) {
        this.memberAction = string;
    }

    public void setOptions(IndexedVector indexedVector) {
        this.options = indexedVector;
    }

    public void setPreambleAction(String string) {
        this.preambleAction = string;
    }

    public void setPredefined(boolean bl) {
        this.predefined = bl;
    }

    public void setTokenSection(String string) {
        this.tokenSection = string;
    }

    public void setType(String string) {
        this.type = string;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(10000);
        if (this.preambleAction != null) {
            stringBuffer.append(this.preambleAction);
        }
        if (this.superGrammar == null) {
            return "class " + this.name + ";";
        }
        if (this.superClass != null) {
            stringBuffer.append("class " + this.name + " extends " + this.superClass + ";");
        } else {
            stringBuffer.append("class " + this.name + " extends " + this.type + ";");
        }
        stringBuffer.append(System.getProperty("line.separator") + System.getProperty("line.separator"));
        if (this.options != null) {
            stringBuffer.append(Hierarchy.optionsToString(this.options));
        }
        if (this.tokenSection != null) {
            stringBuffer.append(this.tokenSection + "\n");
        }
        if (this.memberAction != null) {
            stringBuffer.append(this.memberAction + System.getProperty("line.separator"));
        }
        for (int i = 0; i < this.rules.size(); ++i) {
            Rule rule = (Rule)this.rules.elementAt(i);
            if (!this.getName().equals(rule.enclosingGrammar.getName())) {
                stringBuffer.append("// inherited from grammar " + rule.enclosingGrammar.getName() + System.getProperty("line.separator"));
            }
            stringBuffer.append(rule + System.getProperty("line.separator") + System.getProperty("line.separator"));
        }
        return stringBuffer.toString();
    }
}

