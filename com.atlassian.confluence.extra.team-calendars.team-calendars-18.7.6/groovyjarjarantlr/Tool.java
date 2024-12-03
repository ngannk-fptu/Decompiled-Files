/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr;

import groovyjarjarantlr.ANTLRLexer;
import groovyjarjarantlr.ANTLRParser;
import groovyjarjarantlr.CodeGenerator;
import groovyjarjarantlr.DefaultToolErrorHandler;
import groovyjarjarantlr.FileCopyException;
import groovyjarjarantlr.FileLineFormatter;
import groovyjarjarantlr.LLkAnalyzer;
import groovyjarjarantlr.MakeGrammar;
import groovyjarjarantlr.NameSpace;
import groovyjarjarantlr.PreservingFileWriter;
import groovyjarjarantlr.RecognitionException;
import groovyjarjarantlr.StringUtils;
import groovyjarjarantlr.TokenBuffer;
import groovyjarjarantlr.TokenStreamException;
import groovyjarjarantlr.ToolErrorHandler;
import groovyjarjarantlr.Utils;
import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.collections.impl.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.StringTokenizer;

public class Tool {
    public static String version = "";
    ToolErrorHandler errorHandler;
    protected boolean hasError = false;
    boolean genDiagnostics = false;
    boolean genDocBook = false;
    boolean genHTML = false;
    protected String outputDir = ".";
    protected String grammarFile;
    transient Reader f = new InputStreamReader(System.in);
    protected String literalsPrefix = "LITERAL_";
    protected boolean upperCaseMangledLiterals = false;
    protected NameSpace nameSpace = null;
    protected String namespaceAntlr = null;
    protected String namespaceStd = null;
    protected boolean genHashLines = true;
    protected boolean noConstructors = false;
    private BitSet cmdLineArgValid = new BitSet();

    public Tool() {
        this.errorHandler = new DefaultToolErrorHandler(this);
    }

    public String getGrammarFile() {
        return this.grammarFile;
    }

    public boolean hasError() {
        return this.hasError;
    }

    public NameSpace getNameSpace() {
        return this.nameSpace;
    }

    public String getNamespaceStd() {
        return this.namespaceStd;
    }

    public String getNamespaceAntlr() {
        return this.namespaceAntlr;
    }

    public boolean getGenHashLines() {
        return this.genHashLines;
    }

    public String getLiteralsPrefix() {
        return this.literalsPrefix;
    }

    public boolean getUpperCaseMangledLiterals() {
        return this.upperCaseMangledLiterals;
    }

    public void setFileLineFormatter(FileLineFormatter fileLineFormatter) {
        FileLineFormatter.setFormatter(fileLineFormatter);
    }

    protected void checkForInvalidArguments(String[] stringArray, BitSet bitSet) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (bitSet.member(i)) continue;
            this.warning("invalid command-line argument: " + stringArray[i] + "; ignored");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void copyFile(String string, String string2) throws IOException {
        IOException iOException3;
        Writer writer;
        block17: {
            File file = new File(string);
            File file2 = new File(string2);
            Reader reader = null;
            writer = null;
            try {
                int n;
                Object object;
                if (!file.exists() || !file.isFile()) {
                    throw new FileCopyException("FileCopy: no such source file: " + string);
                }
                if (!file.canRead()) {
                    throw new FileCopyException("FileCopy: source file is unreadable: " + string);
                }
                if (file2.exists()) {
                    if (!file2.isFile()) throw new FileCopyException("FileCopy: destination is not a file: " + string2);
                    object = new DataInputStream(System.in);
                    if (!file2.canWrite()) {
                        throw new FileCopyException("FileCopy: destination file is unwriteable: " + string2);
                    }
                } else {
                    object = this.parent(file2);
                    if (!((File)object).exists()) {
                        throw new FileCopyException("FileCopy: destination directory doesn't exist: " + string2);
                    }
                    if (!((File)object).canWrite()) {
                        throw new FileCopyException("FileCopy: destination directory is unwriteable: " + string2);
                    }
                }
                reader = new BufferedReader(new FileReader(file));
                writer = new BufferedWriter(new FileWriter(file2));
                char[] cArray = new char[1024];
                while ((n = reader.read(cArray, 0, 1024)) != -1) {
                    writer.write(cArray, 0, n);
                }
                Object var11_10 = null;
                if (reader == null) break block17;
            }
            catch (Throwable throwable) {
                IOException iOException22;
                Object var11_11 = null;
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (IOException iOException22) {
                        // empty catch block
                    }
                }
                if (writer == null) throw throwable;
                try {
                    writer.close();
                    throw throwable;
                }
                catch (IOException iOException22) {
                    // empty catch block
                }
                throw throwable;
            }
            try {
                reader.close();
            }
            catch (IOException iOException3) {
                // empty catch block
            }
        }
        if (writer == null) return;
        try {
            writer.close();
            return;
        }
        catch (IOException iOException3) {}
    }

    public void doEverythingWrapper(String[] stringArray) {
        int n = this.doEverything(stringArray);
        System.exit(n);
    }

    public int doEverything(String[] stringArray) {
        groovyjarjarantlr.preprocessor.Tool tool = new groovyjarjarantlr.preprocessor.Tool(this, stringArray);
        boolean bl = tool.preprocess();
        String[] stringArray2 = tool.preprocessedArgList();
        this.processArguments(stringArray2);
        if (!bl) {
            return 1;
        }
        this.f = this.getGrammarReader();
        ANTLRLexer aNTLRLexer = new ANTLRLexer(this.f);
        TokenBuffer tokenBuffer = new TokenBuffer(aNTLRLexer);
        LLkAnalyzer lLkAnalyzer = new LLkAnalyzer(this);
        MakeGrammar makeGrammar = new MakeGrammar(this, stringArray, lLkAnalyzer);
        try {
            ANTLRParser aNTLRParser = new ANTLRParser(tokenBuffer, makeGrammar, this);
            aNTLRParser.setFilename(this.grammarFile);
            aNTLRParser.grammar();
            if (this.hasError()) {
                this.fatalError("Exiting due to errors.");
            }
            this.checkForInvalidArguments(stringArray2, this.cmdLineArgValid);
            String string = "groovyjarjarantlr." + this.getLanguage(makeGrammar) + "CodeGenerator";
            try {
                CodeGenerator codeGenerator = (CodeGenerator)Utils.createInstanceOf(string);
                codeGenerator.setBehavior(makeGrammar);
                codeGenerator.setAnalyzer(lLkAnalyzer);
                codeGenerator.setTool(this);
                codeGenerator.gen();
            }
            catch (ClassNotFoundException classNotFoundException) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (InstantiationException instantiationException) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                this.panic("Cannot instantiate code-generator: " + string);
            }
            catch (IllegalAccessException illegalAccessException) {
                this.panic("code-generator class '" + string + "' is not accessible");
            }
        }
        catch (RecognitionException recognitionException) {
            this.fatalError("Unhandled parser error: " + recognitionException.getMessage());
        }
        catch (TokenStreamException tokenStreamException) {
            this.fatalError("TokenStreamException: " + tokenStreamException.getMessage());
        }
        return 0;
    }

    public void error(String string) {
        this.hasError = true;
        System.err.println("error: " + string);
    }

    public void error(String string, String string2, int n, int n2) {
        this.hasError = true;
        System.err.println(FileLineFormatter.getFormatter().getFormatString(string2, n, n2) + string);
    }

    public String fileMinusPath(String string) {
        String string2 = System.getProperty("file.separator");
        int n = string.lastIndexOf(string2);
        if (n == -1) {
            return string;
        }
        return string.substring(n + 1);
    }

    public String getLanguage(MakeGrammar makeGrammar) {
        if (this.genDiagnostics) {
            return "Diagnostic";
        }
        if (this.genHTML) {
            return "HTML";
        }
        if (this.genDocBook) {
            return "DocBook";
        }
        return makeGrammar.language;
    }

    public String getOutputDirectory() {
        return this.outputDir;
    }

    private static void help() {
        System.err.println("usage: java antlr.Tool [args] file.g");
        System.err.println("  -o outputDir       specify output directory where all output generated.");
        System.err.println("  -glib superGrammar specify location of supergrammar file.");
        System.err.println("  -debug             launch the ParseView debugger upon parser invocation.");
        System.err.println("  -html              generate a html file from your grammar.");
        System.err.println("  -docbook           generate a docbook sgml file from your grammar.");
        System.err.println("  -diagnostic        generate a textfile with diagnostics.");
        System.err.println("  -trace             have all rules call traceIn/traceOut.");
        System.err.println("  -traceLexer        have lexer rules call traceIn/traceOut.");
        System.err.println("  -traceParser       have parser rules call traceIn/traceOut.");
        System.err.println("  -traceTreeParser   have tree parser rules call traceIn/traceOut.");
        System.err.println("  -h|-help|--help    this message");
    }

    public static void main(String[] stringArray) {
        System.err.println("ANTLR Parser Generator   Version 2.7.7 (20060906)   1989-2005");
        version = "2.7.7 (20060906)";
        try {
            boolean bl = false;
            if (stringArray.length == 0) {
                bl = true;
            } else {
                for (int i = 0; i < stringArray.length; ++i) {
                    if (!stringArray[i].equals("-h") && !stringArray[i].equals("-help") && !stringArray[i].equals("--help")) continue;
                    bl = true;
                    break;
                }
            }
            if (bl) {
                Tool.help();
            } else {
                Tool tool = new Tool();
                tool.doEverything(stringArray);
                tool = null;
            }
        }
        catch (Exception exception) {
            System.err.println(System.getProperty("line.separator") + System.getProperty("line.separator"));
            System.err.println("#$%%*&@# internal error: " + exception.toString());
            System.err.println("[complain to nearest government official");
            System.err.println(" or send hate-mail to parrt@antlr.org;");
            System.err.println(" please send stack trace with report.]" + System.getProperty("line.separator"));
            exception.printStackTrace();
        }
    }

    public PrintWriter openOutputFile(String string) throws IOException {
        File file;
        if (this.outputDir != "." && !(file = new File(this.outputDir)).exists()) {
            file.mkdirs();
        }
        return new PrintWriter(new PreservingFileWriter(this.outputDir + System.getProperty("file.separator") + string));
    }

    public Reader getGrammarReader() {
        BufferedReader bufferedReader = null;
        try {
            if (this.grammarFile != null) {
                bufferedReader = new BufferedReader(new FileReader(this.grammarFile));
            }
        }
        catch (IOException iOException) {
            this.fatalError("cannot open grammar file " + this.grammarFile);
        }
        return bufferedReader;
    }

    public void reportException(Exception exception, String string) {
        System.err.println(string == null ? exception.getMessage() : string + ": " + exception.getMessage());
    }

    public void reportProgress(String string) {
        System.out.println(string);
    }

    public void fatalError(String string) {
        System.err.println(string);
        Utils.error(string);
    }

    public void panic() {
        this.fatalError("panic");
    }

    public void panic(String string) {
        this.fatalError("panic: " + string);
    }

    public File parent(File file) {
        String string = file.getParent();
        if (string == null) {
            if (file.isAbsolute()) {
                return new File(File.separator);
            }
            return new File(System.getProperty("user.dir"));
        }
        return new File(string);
    }

    public static Vector parseSeparatedList(String string, char c) {
        StringTokenizer stringTokenizer = new StringTokenizer(string, String.valueOf(c));
        Vector vector = new Vector(10);
        while (stringTokenizer.hasMoreTokens()) {
            vector.appendElement(stringTokenizer.nextToken());
        }
        if (vector.size() == 0) {
            return null;
        }
        return vector;
    }

    public String pathToFile(String string) {
        String string2 = System.getProperty("file.separator");
        int n = string.lastIndexOf(string2);
        if (n == -1) {
            return "." + System.getProperty("file.separator");
        }
        return string.substring(0, n + 1);
    }

    protected void processArguments(String[] stringArray) {
        for (int i = 0; i < stringArray.length; ++i) {
            if (stringArray[i].equals("-diagnostic")) {
                this.genDiagnostics = true;
                this.genHTML = false;
                this.setArgOK(i);
                continue;
            }
            if (stringArray[i].equals("-o")) {
                this.setArgOK(i);
                if (i + 1 >= stringArray.length) {
                    this.error("missing output directory with -o option; ignoring");
                    continue;
                }
                this.setOutputDirectory(stringArray[++i]);
                this.setArgOK(i);
                continue;
            }
            if (stringArray[i].equals("-html")) {
                this.genHTML = true;
                this.genDiagnostics = false;
                this.setArgOK(i);
                continue;
            }
            if (stringArray[i].equals("-docbook")) {
                this.genDocBook = true;
                this.genDiagnostics = false;
                this.setArgOK(i);
                continue;
            }
            if (stringArray[i].charAt(0) == '-') continue;
            this.grammarFile = stringArray[i];
            this.setArgOK(i);
        }
    }

    public void setArgOK(int n) {
        this.cmdLineArgValid.add(n);
    }

    public void setOutputDirectory(String string) {
        this.outputDir = string;
    }

    public void toolError(String string) {
        System.err.println("error: " + string);
    }

    public void warning(String string) {
        System.err.println("warning: " + string);
    }

    public void warning(String string, String string2, int n, int n2) {
        System.err.println(FileLineFormatter.getFormatter().getFormatString(string2, n, n2) + "warning:" + string);
    }

    public void warning(String[] stringArray, String string, int n, int n2) {
        if (stringArray == null || stringArray.length == 0) {
            this.panic("bad multi-line message to Tool.warning");
        }
        System.err.println(FileLineFormatter.getFormatter().getFormatString(string, n, n2) + "warning:" + stringArray[0]);
        for (int i = 1; i < stringArray.length; ++i) {
            System.err.println(FileLineFormatter.getFormatter().getFormatString(string, n, n2) + "    " + stringArray[i]);
        }
    }

    public void setNameSpace(String string) {
        if (null == this.nameSpace) {
            this.nameSpace = new NameSpace(StringUtils.stripFrontBack(string, "\"", "\""));
        }
    }
}

