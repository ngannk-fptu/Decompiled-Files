/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sed;

import aQute.lib.io.IO;
import aQute.libg.sed.Replacer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sed {
    final File file;
    final Replacer macro;
    File output;
    boolean backup = true;
    final Map<Pattern, String> replacements = new LinkedHashMap<Pattern, String>();

    public Sed(Replacer macro, File file) {
        assert (file.isFile());
        this.file = file;
        this.macro = macro;
    }

    public Sed(File file) {
        assert (file.isFile());
        this.file = file;
        this.macro = null;
    }

    public void setOutput(File f) {
        this.output = f;
    }

    public void replace(String pattern, String replacement) {
        this.replacements.put(Pattern.compile(pattern), replacement);
    }

    public int doIt() throws IOException {
        int actions = 0;
        File out = this.output != null ? this.output : new File(this.file.getAbsolutePath() + ".tmp");
        try (BufferedReader brdr = IO.reader(this.file, StandardCharsets.UTF_8);
             PrintWriter pw = IO.writer(out, StandardCharsets.UTF_8);){
            String line;
            while ((line = brdr.readLine()) != null) {
                for (Pattern p : this.replacements.keySet()) {
                    try {
                        String replace = this.replacements.get(p);
                        Matcher m = p.matcher(line);
                        StringBuffer sb = new StringBuffer();
                        while (m.find()) {
                            String tmp = this.setReferences(m, replace);
                            if (this.macro != null) {
                                tmp = Matcher.quoteReplacement(this.macro.process(tmp));
                            }
                            m.appendReplacement(sb, tmp);
                            ++actions;
                        }
                        m.appendTail(sb);
                        line = sb.toString();
                    }
                    catch (Exception e) {
                        throw new IOException("where: " + line + ", pattern: " + p.pattern() + ": " + e, e);
                    }
                }
                pw.print(line);
                pw.print('\n');
            }
        }
        if (this.output == null) {
            if (this.backup) {
                File bak = new File(this.file.getAbsolutePath() + ".bak");
                IO.rename(this.file, bak);
            }
            IO.rename(out, this.file);
        }
        return actions;
    }

    private String setReferences(Matcher m, String replace) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < replace.length(); ++i) {
            char c = replace.charAt(i);
            if (c == '$' && i < replace.length() - 1 && Character.isDigit(replace.charAt(i + 1))) {
                int n = replace.charAt(i + 1) - 48;
                if (n <= m.groupCount()) {
                    sb.append(m.group(n));
                }
                ++i;
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public void setBackup(boolean b) {
        this.backup = b;
    }
}

