/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.CaseFormat
 *  com.google.common.base.Charsets
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.io.Files
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.tools.ant.Task
 */
package com.google.template.soy.shared.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CaseFormat;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.template.soy.shared.internal.DirectiveDigest;
import com.google.template.soy.shared.restricted.EscapingConventions;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.tools.ant.Task;

@ParametersAreNonnullByDefault
public abstract class AbstractGenerateSoyEscapingDirectiveCode
extends Task {
    private List<FileRef> inputs = Lists.newArrayList();
    private FileRef output;
    protected Predicate<String> availableIdentifiers = new Predicate<String>(){

        public boolean apply(String functionName) {
            return functionName.indexOf(46) < 0;
        }
    };
    final String GENERATED_CODE_START_MARKER = this.getLineCommentSyntax() + " START GENERATED CODE FOR ESCAPERS.";
    final String GENERATED_CODE_END_MARKER = this.getLineCommentSyntax() + " END GENERATED CODE";

    public Predicate<String> getAvailableIdentifiers() {
        return this.availableIdentifiers;
    }

    public FileRef createInput() {
        FileRef ref = new FileRef(true);
        this.inputs.add(ref);
        return ref;
    }

    public FileRef createOutput() {
        if (this.output != null) {
            throw new IllegalStateException("Too many <output>s");
        }
        this.output = new FileRef(false);
        return this.output;
    }

    public void addConfiguredLibdefined(FunctionNamePredicate p) {
        final Pattern namePattern = p.namePattern;
        if (namePattern == null) {
            throw new IllegalStateException("Please specify a pattern attribute for <libdefined>");
        }
        this.availableIdentifiers = Predicates.or(this.availableIdentifiers, (Predicate)new Predicate<String>(){

            public boolean apply(String identifierName) {
                return namePattern.matcher(identifierName).matches();
            }
        });
    }

    protected void configure(String[] args) throws IOException {
        for (String arg : args) {
            FileRef ref;
            if (arg.startsWith("--input=")) {
                ref = this.createInput();
                ref.setPath(arg.substring(arg.indexOf(61) + 1));
                continue;
            }
            if (arg.startsWith("--output=")) {
                ref = this.createOutput();
                ref.setPath(arg.substring(arg.indexOf(61) + 1));
                continue;
            }
            if (arg.startsWith("--libdefined=")) {
                FunctionNamePredicate libdefined = new FunctionNamePredicate();
                libdefined.setPattern(arg.substring(arg.indexOf(61) + 1));
                this.addConfiguredLibdefined(libdefined);
                continue;
            }
            throw new IllegalArgumentException(arg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute() {
        super.execute();
        if (this.output == null) {
            System.err.println("Please add an <output> for the <" + this.getTaskName() + "> at " + this.getLocation());
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (FileRef input : this.inputs) {
            try {
                boolean inGeneratedCode = false;
                for (String line : Files.readLines((File)input.file, (Charset)Charsets.UTF_8)) {
                    if (inGeneratedCode) {
                        if (!this.GENERATED_CODE_END_MARKER.equals(line.trim())) continue;
                        inGeneratedCode = false;
                        continue;
                    }
                    if (this.GENERATED_CODE_START_MARKER.equals(line.trim())) {
                        inGeneratedCode = true;
                        continue;
                    }
                    sb.append(line).append('\n');
                }
                sb.append('\n');
            }
            catch (IOException ex) {
                System.err.println("Failed to read " + input.file);
                ex.printStackTrace();
                return;
            }
        }
        this.generateCode(this.availableIdentifiers, sb);
        try (OutputStreamWriter out = new OutputStreamWriter((OutputStream)new FileOutputStream(this.output.file), Charsets.UTF_8);){
            ((Writer)out).append(sb);
        }
        catch (IOException ex) {
            this.output.file.delete();
        }
    }

    @VisibleForTesting
    void generateCode(Predicate<String> availableIdentifiers, StringBuilder outputCode) {
        int i;
        outputCode.append('\n').append(this.GENERATED_CODE_START_MARKER).append('\n');
        ArrayList escapeMaps = Lists.newArrayList();
        ArrayList escapeMapNames = Lists.newArrayList();
        ArrayList matchers = Lists.newArrayList();
        ArrayList matcherNames = Lists.newArrayList();
        ArrayList filters = Lists.newArrayList();
        ArrayList filterNames = Lists.newArrayList();
        ArrayList digests = Lists.newArrayList();
        block0: for (EscapingConventions.CrossLanguageStringXform escaper : EscapingConventions.getAllEscapers()) {
            String escapeDirectiveIdent = escaper.getDirectiveName().substring(1);
            String escapeDirectiveUIdent = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, escapeDirectiveIdent);
            for (String existingFunction : escaper.getLangFunctionNames(this.getLanguage())) {
                if (!availableIdentifiers.apply((Object)existingFunction)) continue;
                this.useExistingLibraryFunction(outputCode, escapeDirectiveIdent, existingFunction);
                continue block0;
            }
            int escapesVar = -1;
            int matcherVar = -1;
            if (!escaper.getEscapes().isEmpty()) {
                LinkedHashMap escapeMap = Maps.newLinkedHashMap();
                StringBuilder matcherRegexBuf = new StringBuilder(this.getRegexStart() + "[");
                char lastCodeUnit = Integer.MIN_VALUE;
                int rangeStart = Integer.MIN_VALUE;
                for (EscapingConventions.Escape esc : escaper.getEscapes()) {
                    char ch = esc.getPlainText();
                    if (ch == lastCodeUnit) {
                        throw new IllegalStateException("Ambiguous escape " + esc.getEscaped() + " for " + escapeDirectiveIdent);
                    }
                    escapeMap.put(Character.valueOf(ch), esc.getEscaped());
                    if (ch != lastCodeUnit + '\u0001') {
                        if (rangeStart != Integer.MIN_VALUE) {
                            AbstractGenerateSoyEscapingDirectiveCode.escapeRegexpRangeOnto((char)rangeStart, lastCodeUnit, matcherRegexBuf);
                        }
                        rangeStart = ch;
                    }
                    lastCodeUnit = ch;
                }
                if (rangeStart < 0) {
                    throw new IllegalStateException();
                }
                AbstractGenerateSoyEscapingDirectiveCode.escapeRegexpRangeOnto((char)rangeStart, lastCodeUnit, matcherRegexBuf);
                matcherRegexBuf.append("]").append(this.getRegexEnd());
                int numEscapeMaps = escapeMaps.size();
                for (int i2 = 0; i2 < numEscapeMaps; ++i2) {
                    if (!AbstractGenerateSoyEscapingDirectiveCode.mapsHaveCompatibleOverlap((Map)escapeMaps.get(i2), escapeMap)) continue;
                    escapesVar = i2;
                    break;
                }
                if (escapesVar == -1) {
                    escapesVar = numEscapeMaps;
                    escapeMaps.add(escapeMap);
                    escapeMapNames.add(escapeDirectiveUIdent);
                } else {
                    ((Map)escapeMaps.get(escapesVar)).putAll(escapeMap);
                    escapeMapNames.set(escapesVar, (String)escapeMapNames.get(escapesVar) + "__AND__" + escapeDirectiveUIdent);
                }
                String matcherRegex = matcherRegexBuf.toString();
                matcherVar = matchers.indexOf(matcherRegex);
                if (matcherVar < 0) {
                    matcherVar = matchers.size();
                    matchers.add(matcherRegex);
                    matcherNames.add(escapeDirectiveUIdent);
                } else {
                    matcherNames.set(matcherVar, (String)matcherNames.get(matcherVar) + "__AND__" + escapeDirectiveUIdent);
                }
            }
            int filterVar = -1;
            Pattern filterPatternJava = escaper.getValueFilter();
            if (filterPatternJava != null) {
                String filterPattern = this.convertFromJavaRegex(filterPatternJava);
                filterVar = filters.indexOf(filterPattern);
                if (filterVar == -1) {
                    filterVar = filters.size();
                    filters.add(filterPattern);
                    filterNames.add(escapeDirectiveUIdent);
                } else {
                    filterNames.set(filterVar, (String)filterNames.get(filterVar) + "__AND__" + escapeDirectiveUIdent);
                }
            }
            digests.add(new DirectiveDigest(escapeDirectiveIdent, escapesVar, matcherVar, filterVar, escaper.getNonAsciiPrefix(), escaper.getInnocuousOutput()));
        }
        for (i = 0; i < escapeMaps.size(); ++i) {
            Map escapeMap = (Map)escapeMaps.get(i);
            String escapeMapName = (String)escapeMapNames.get(i);
            this.generateCharacterMapSignature(outputCode, escapeMapName);
            outputCode.append(" = {");
            boolean needsComma = false;
            for (Map.Entry e : escapeMap.entrySet()) {
                if (needsComma) {
                    outputCode.append(',');
                }
                outputCode.append("\n  ");
                AbstractGenerateSoyEscapingDirectiveCode.writeUnsafeStringLiteral(((Character)e.getKey()).charValue(), outputCode);
                outputCode.append(": ");
                AbstractGenerateSoyEscapingDirectiveCode.writeStringLiteral((String)e.getValue(), outputCode);
                needsComma = true;
            }
            outputCode.append("\n}").append(this.getLineEndSyntax()).append("\n");
            this.generateReplacerFunction(outputCode, escapeMapName);
        }
        for (i = 0; i < matchers.size(); ++i) {
            String matcherName = (String)matcherNames.get(i);
            String matcher = (String)matchers.get(i);
            this.generateMatcher(outputCode, matcherName, matcher);
        }
        for (i = 0; i < filters.size(); ++i) {
            String filterName = (String)filterNames.get(i);
            String filter = (String)filters.get(i);
            this.generateFilter(outputCode, filterName, filter);
        }
        for (DirectiveDigest digest : digests) {
            digest.updateNames(escapeMapNames, matcherNames, filterNames);
            this.generateHelperFunction(outputCode, digest);
        }
        this.generateCommonConstants(outputCode);
        outputCode.append('\n').append(this.GENERATED_CODE_END_MARKER).append('\n');
    }

    private static <K, V> boolean mapsHaveCompatibleOverlap(Map<K, V> a, Map<K, V> b) {
        if (b.size() < a.size()) {
            Map<K, V> t = a;
            a = b;
            b = t;
        }
        boolean overlap = false;
        for (Map.Entry<K, V> e : a.entrySet()) {
            V value = b.get(e.getKey());
            if (value != null) {
                if (!value.equals(e.getValue())) {
                    return false;
                }
                overlap = true;
                continue;
            }
            if (!b.containsKey(e.getKey())) continue;
            if (e.getValue() != null) {
                return false;
            }
            overlap = true;
        }
        return overlap;
    }

    protected static void writeStringLiteral(String value, StringBuilder out) {
        out.append('\'').append(EscapingConventions.EscapeJsString.INSTANCE.escape(value)).append('\'');
    }

    private static void writeUnsafeStringLiteral(char value, StringBuilder out) {
        if (!AbstractGenerateSoyEscapingDirectiveCode.isPrintable(value)) {
            out.append(String.format(value >= '\u0100' ? "'\\u%04x'" : "'\\x%02x'", value));
        } else {
            out.append('\'').append(EscapingConventions.EscapeJsString.INSTANCE.escape(String.valueOf(value))).append('\'');
        }
    }

    private static void escapeRegexpRangeOnto(char start, char end, StringBuilder out) {
        if (!AbstractGenerateSoyEscapingDirectiveCode.isPrintable(start)) {
            out.append(String.format(start >= '\u0100' ? "\\u%04x" : "\\x%02x", start));
        } else {
            out.append(EscapingConventions.EscapeJsRegex.INSTANCE.escape(String.valueOf(start)));
        }
        if (start != end) {
            if (end - start > 1) {
                out.append('-');
            }
            if (!AbstractGenerateSoyEscapingDirectiveCode.isPrintable(end)) {
                out.append(String.format(end >= '\u0100' ? "\\u%04x" : "\\x%02x", end));
            } else {
                out.append(EscapingConventions.EscapeJsRegex.INSTANCE.escape(String.valueOf(end)));
            }
        }
    }

    private static boolean isPrintable(char ch) {
        return ' ' <= ch && ch <= '~';
    }

    protected abstract EscapingConventions.EscapingLanguage getLanguage();

    protected abstract String getLineCommentSyntax();

    protected abstract String getLineEndSyntax();

    protected abstract String getRegexStart();

    protected abstract String getRegexEnd();

    protected abstract String convertFromJavaRegex(Pattern var1);

    protected abstract void generateCharacterMapSignature(StringBuilder var1, String var2);

    protected abstract void generateMatcher(StringBuilder var1, String var2, String var3);

    protected abstract void generateFilter(StringBuilder var1, String var2, String var3);

    protected abstract void generateReplacerFunction(StringBuilder var1, String var2);

    protected abstract void useExistingLibraryFunction(StringBuilder var1, String var2, String var3);

    protected abstract void generateHelperFunction(StringBuilder var1, DirectiveDigest var2);

    protected abstract void generateCommonConstants(StringBuilder var1);

    public static final class FunctionNamePredicate {
        private Pattern namePattern;

        public void setPattern(String s) {
            String regex = "\\Q" + s.replace("*", "\\E\\w+\\Q") + "\\E";
            this.namePattern = Pattern.compile(regex);
        }
    }

    public static final class FileRef {
        private final boolean isInput;
        private File file;

        public FileRef(boolean isInput) {
            this.isInput = isInput;
        }

        public void setPath(String path) throws IOException {
            this.file = new File(path);
            if (this.isInput) {
                if (!this.file.isFile() || !this.file.canRead()) {
                    throw new IOException("Missing input file " + path);
                }
            } else if (this.file.isDirectory() || !this.file.getParentFile().isDirectory()) {
                throw new IOException("Cannot write output file " + path);
            }
        }
    }
}

