/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.preprocessor;

import groovyjarjarantlr.collections.impl.Vector;
import groovyjarjarantlr.preprocessor.GrammarFile;
import groovyjarjarantlr.preprocessor.Hierarchy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

public class Tool {
    protected Hierarchy theHierarchy;
    protected String grammarFileName;
    protected String[] args;
    protected int nargs;
    protected Vector grammars;
    protected groovyjarjarantlr.Tool antlrTool;

    public Tool(groovyjarjarantlr.Tool tool, String[] stringArray) {
        this.antlrTool = tool;
        this.processArguments(stringArray);
    }

    public static void main(String[] stringArray) {
        groovyjarjarantlr.Tool tool = new groovyjarjarantlr.Tool();
        Tool tool2 = new Tool(tool, stringArray);
        tool2.preprocess();
        String[] stringArray2 = tool2.preprocessedArgList();
        for (int i = 0; i < stringArray2.length; ++i) {
            System.out.print(" " + stringArray2[i]);
        }
        System.out.println();
    }

    public boolean preprocess() {
        boolean bl;
        Object object;
        if (this.grammarFileName == null) {
            this.antlrTool.toolError("no grammar file specified");
            return false;
        }
        if (this.grammars != null) {
            this.theHierarchy = new Hierarchy(this.antlrTool);
            Enumeration enumeration = this.grammars.elements();
            while (enumeration.hasMoreElements()) {
                object = (String)enumeration.nextElement();
                try {
                    this.theHierarchy.readGrammarFile((String)object);
                }
                catch (FileNotFoundException fileNotFoundException) {
                    this.antlrTool.toolError("file " + (String)object + " not found");
                    return false;
                }
            }
        }
        if (!(bl = this.theHierarchy.verifyThatHierarchyIsComplete())) {
            return false;
        }
        this.theHierarchy.expandGrammarsInFile(this.grammarFileName);
        object = this.theHierarchy.getFile(this.grammarFileName);
        String string = ((GrammarFile)object).nameForExpandedGrammarFile(this.grammarFileName);
        if (string.equals(this.grammarFileName)) {
            this.args[this.nargs++] = this.grammarFileName;
        } else {
            try {
                ((GrammarFile)object).generateExpandedFile();
                this.args[this.nargs++] = this.antlrTool.getOutputDirectory() + System.getProperty("file.separator") + string;
            }
            catch (IOException iOException) {
                this.antlrTool.toolError("cannot write expanded grammar file " + string);
                return false;
            }
        }
        return true;
    }

    public String[] preprocessedArgList() {
        String[] stringArray = new String[this.nargs];
        System.arraycopy(this.args, 0, stringArray, 0, this.nargs);
        this.args = stringArray;
        return this.args;
    }

    private void processArguments(String[] stringArray) {
        this.nargs = 0;
        this.args = new String[stringArray.length];
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].length() == 0) {
                this.antlrTool.warning("Zero length argument ignoring...");
                continue;
            }
            if (stringArray[i].equals("-glib")) {
                if (File.separator.equals("\\") && stringArray[i].indexOf(47) != -1) {
                    this.antlrTool.warning("-glib cannot deal with '/' on a PC: use '\\'; ignoring...");
                    continue;
                }
                this.grammars = groovyjarjarantlr.Tool.parseSeparatedList(stringArray[i + 1], ';');
                ++i;
                continue;
            }
            if (stringArray[i].equals("-o")) {
                this.args[this.nargs++] = stringArray[i];
                if (i + 1 >= stringArray.length) {
                    this.antlrTool.error("missing output directory with -o option; ignoring");
                    continue;
                }
                this.args[this.nargs++] = stringArray[++i];
                this.antlrTool.setOutputDirectory(stringArray[i]);
                continue;
            }
            if (stringArray[i].charAt(0) == '-') {
                this.args[this.nargs++] = stringArray[i];
                continue;
            }
            this.grammarFileName = stringArray[i];
            if (this.grammars == null) {
                this.grammars = new Vector(10);
            }
            this.grammars.appendElement(this.grammarFileName);
            if (i + 1 >= stringArray.length) continue;
            this.antlrTool.warning("grammar file must be last; ignoring other arguments...");
            break;
        }
    }
}

