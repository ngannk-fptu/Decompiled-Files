/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.oro.text.perl.Perl5Util
 *  org.apache.tools.ant.DirectoryScanner
 */
package org.apache.velocity.convert;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.velocity.util.StringUtils;

public class WebMacro {
    protected static final String VM_EXT = ".vm";
    protected static final String WM_EXT = ".wm";
    protected static String[] perLineREs = new String[]{"#if\\s*[(]\\s*(.*\\S)\\s*[)]\\s*(#begin|{)[ \\t]?", "#if( $1 )", "[ \\t]?(#end|})[ \\t]*\n(\\s*)#else\\s*(#begin|{)[ \\t]?(\\w)", "$2#else#**#$4", "[ \\t]?(#end|})[ \\t]*\n(\\s*)#else\\s*(#begin|{)[ \\t]?", "$2#else", "(#end|})(\\s*#else)\\s*(#begin|{)[ \\t]?", "$1\n$2", "#foreach\\s+(\\$\\w+)\\s+in\\s+(\\$[^\\s#]+)\\s*(#begin|{)[ \\t]?", "#foreach( $1 in $2 )", "#set\\s+(\\$[^\\s=]+)\\s*=\\s*([\\S \\t]+)", "#set( $1 = $2 )", "(##[# \\t\\w]*)\\)", ")$1", "#parse\\s+([^\\s#]+)[ \\t]?", "#parse( $1 )", "#include\\s+([^\\s#]+)[ \\t]?", "#include( $1 )", "\\$\\(([^\\)]+)\\)", "${$1}", "\\${([^}\\(]+)\\(([^}]+)}\\)", "${$1($2)}", "\\$_", "$l_", "\\${(_[^}]+)}", "${l$1}", "(#set\\s*\\([^;]+);(\\s*\\))", "$1$2", "(^|[^\\\\])\\$(\\w[^=\n;'\"]*);", "$1${$2}", "\\.wm", ".vm"};

    public void convert(String target) {
        File file = new File(target);
        if (!file.exists()) {
            throw new RuntimeException("The specified template or directory does not exist");
        }
        if (file.isDirectory()) {
            String basedir = file.getAbsolutePath();
            String newBasedir = basedir + VM_EXT;
            DirectoryScanner ds = new DirectoryScanner();
            ds.setBasedir(basedir);
            ds.addDefaultExcludes();
            ds.scan();
            String[] files = ds.getIncludedFiles();
            for (int i = 0; i < files.length; ++i) {
                this.writeTemplate(files[i], basedir, newBasedir);
            }
        } else {
            this.writeTemplate(file.getAbsolutePath(), "", "");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean writeTemplate(String file, String basedir, String newBasedir) {
        if (file.indexOf(WM_EXT) < 0) {
            return false;
        }
        System.out.println("Converting " + file + "...");
        String template = file;
        String newTemplate = this.convertName(file);
        if (basedir.length() > 0) {
            String templateDir = newBasedir + this.extractPath(file);
            File outputDirectory = new File(templateDir);
            template = basedir + File.separator + file;
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            newTemplate = newBasedir + File.separator + this.convertName(file);
        }
        String convertedTemplate = this.convertTemplate(template);
        FileWriter fw = null;
        fw = new FileWriter(newTemplate);
        fw.write(convertedTemplate);
        Object var10_8 = null;
        if (fw == null) return true;
        try {
            fw.close();
            return true;
        }
        catch (IOException io) {}
        return true;
        {
            catch (Exception e) {
                e.printStackTrace();
                Object var10_9 = null;
                if (fw == null) return true;
                try {
                    fw.close();
                    return true;
                }
                catch (IOException io) {}
                return true;
            }
        }
        catch (Throwable throwable) {
            Object var10_10 = null;
            if (fw == null) throw throwable;
            try {
                fw.close();
                throw throwable;
            }
            catch (IOException io) {
                // empty catch block
            }
            throw throwable;
        }
    }

    private final String extractPath(String file) {
        int lastSepPos = file.lastIndexOf(File.separator);
        return lastSepPos == -1 ? "" : File.separator + file.substring(0, lastSepPos);
    }

    private String convertName(String name) {
        return name.indexOf(WM_EXT) < 0 ? name : name.substring(0, name.indexOf(WM_EXT)) + VM_EXT;
    }

    private static final void usage() {
        System.err.println("Usage: convert-wm <template.wm | directory>");
    }

    public String convertTemplate(String template) {
        String contents = StringUtils.fileContentsToString(template);
        if (!contents.endsWith("\n")) {
            contents = contents + "\n";
        }
        Perl5Util perl = new Perl5Util();
        for (int i = 0; i < perLineREs.length; i += 2) {
            contents = perl.substitute(this.makeSubstRE(i), contents);
        }
        if (perl.match("m/javascript/i", contents)) {
            contents = perl.substitute("s/\n}/\n#end/g", contents);
        } else {
            contents = perl.substitute("s/(\n\\s*)}/$1#end/g", contents);
            contents = perl.substitute("s/#end\\s*\n\\s*#else/#else/g", contents);
        }
        return contents;
    }

    private final String makeSubstRE(int i) {
        return "s/" + perLineREs[i] + '/' + perLineREs[i + 1] + "/g";
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            for (int x = 0; x < args.length; ++x) {
                WebMacro converter = new WebMacro();
                converter.convert(args[x]);
            }
        } else {
            WebMacro.usage();
        }
    }
}

