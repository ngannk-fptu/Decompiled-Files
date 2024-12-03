/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class ReplaceRegExp
extends Task {
    private File file = null;
    private String flags = "";
    private boolean byline = false;
    private Union resources;
    private RegularExpression regex = null;
    private Substitution subs = null;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private boolean preserveLastModified = false;
    private String encoding = null;

    public void setFile(File file) {
        this.file = file;
    }

    public void setMatch(String match) {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed");
        }
        this.regex = new RegularExpression();
        this.regex.setPattern(match);
    }

    public void setReplace(String replace) {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        this.subs = new Substitution();
        this.subs.setExpression(replace);
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }

    @Deprecated
    public void setByLine(String byline) {
        this.byline = Boolean.parseBoolean(byline);
    }

    public void setByLine(boolean byline) {
        this.byline = byline;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void addFileset(FileSet set) {
        this.addConfigured(set);
    }

    public void addConfigured(ResourceCollection rc) {
        if (!rc.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }

    public RegularExpression createRegexp() {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        this.regex = new RegularExpression();
        return this.regex;
    }

    public Substitution createSubstitution() {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        this.subs = new Substitution();
        return this.subs;
    }

    public void setPreserveLastModified(boolean b) {
        this.preserveLastModified = b;
    }

    protected String doReplace(RegularExpression r, Substitution s, String input, int options) {
        String res = input;
        Regexp regexp = r.getRegexp(this.getProject());
        if (regexp.matches(input, options)) {
            this.log("Found match; substituting", 4);
            res = regexp.substitute(input, s.getExpression(this.getProject()), options);
        }
        return res;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doReplace(File f, int options) throws IOException {
        block32: {
            File temp = FILE_UTILS.createTempFile(this.getProject(), "replace", ".txt", null, true, true);
            try {
                boolean changes = false;
                Charset charset = this.encoding == null ? Charset.defaultCharset() : Charset.forName(this.encoding);
                try (InputStream is = Files.newInputStream(f.toPath(), new OpenOption[0]);
                     OutputStream os = Files.newOutputStream(temp.toPath(), new OpenOption[0]);){
                    Writer w;
                    Reader r;
                    block30: {
                        r = null;
                        w = null;
                        try {
                            r = new InputStreamReader(is, charset);
                            w = new OutputStreamWriter(os, charset);
                            this.log("Replacing pattern '" + this.regex.getPattern(this.getProject()) + "' with '" + this.subs.getExpression(this.getProject()) + "' in '" + f.getPath() + "'" + (this.byline ? " by line" : "") + (this.flags.isEmpty() ? "" : " with flags: '" + this.flags + "'") + ".", 3);
                            if (this.byline) {
                                int c;
                                r = new BufferedReader(r);
                                w = new BufferedWriter(w);
                                StringBuilder linebuf = new StringBuilder();
                                boolean hasCR = false;
                                do {
                                    if ((c = r.read()) == 13) {
                                        if (hasCR) {
                                            changes |= this.replaceAndWrite(linebuf.toString(), w, options);
                                            w.write(13);
                                            linebuf = new StringBuilder();
                                            continue;
                                        }
                                        hasCR = true;
                                        continue;
                                    }
                                    if (c == 10) {
                                        changes |= this.replaceAndWrite(linebuf.toString(), w, options);
                                        if (hasCR) {
                                            w.write(13);
                                            hasCR = false;
                                        }
                                        w.write(10);
                                        linebuf = new StringBuilder();
                                        continue;
                                    }
                                    if (hasCR || c < 0) {
                                        changes |= this.replaceAndWrite(linebuf.toString(), w, options);
                                        if (hasCR) {
                                            w.write(13);
                                            hasCR = false;
                                        }
                                        linebuf = new StringBuilder();
                                    }
                                    if (c < 0) continue;
                                    linebuf.append((char)c);
                                } while (c >= 0);
                                break block30;
                            }
                            changes = this.multilineReplace(r, w, options);
                        }
                        catch (Throwable throwable) {
                            FileUtils.close(r);
                            FileUtils.close(w);
                            throw throwable;
                        }
                    }
                    FileUtils.close(r);
                    FileUtils.close(w);
                }
                if (changes) {
                    this.log("File has changed; saving the updated file", 3);
                    try {
                        long origLastModified = f.lastModified();
                        FILE_UTILS.rename(temp, f, true);
                        if (this.preserveLastModified) {
                            FILE_UTILS.setFileLastModified(f, origLastModified);
                        }
                        temp = null;
                        break block32;
                    }
                    catch (IOException e) {
                        throw new BuildException("Couldn't rename temporary file " + temp, e, this.getLocation());
                    }
                }
                this.log("No change made", 4);
            }
            finally {
                if (temp != null) {
                    temp.delete();
                }
            }
        }
    }

    @Override
    public void execute() throws BuildException {
        if (this.regex == null) {
            throw new BuildException("No expression to match.");
        }
        if (this.subs == null) {
            throw new BuildException("Nothing to replace expression with.");
        }
        if (this.file != null && this.resources != null) {
            throw new BuildException("You cannot supply the 'file' attribute and resource collections at the same time.");
        }
        int options = RegexpUtil.asOptions(this.flags);
        if (this.file != null && this.file.exists()) {
            try {
                this.doReplace(this.file, options);
            }
            catch (IOException e) {
                this.log("An error occurred processing file: '" + this.file.getAbsolutePath() + "': " + e.toString(), 0);
            }
        } else if (this.file != null) {
            this.log("The following file is missing: '" + this.file.getAbsolutePath() + "'", 0);
        }
        if (this.resources != null) {
            for (Resource r : this.resources) {
                File f = r.as(FileProvider.class).getFile();
                if (f.exists()) {
                    try {
                        this.doReplace(f, options);
                    }
                    catch (Exception e) {
                        this.log("An error occurred processing file: '" + f.getAbsolutePath() + "': " + e.toString(), 0);
                    }
                    continue;
                }
                this.log("The following file is missing: '" + f.getAbsolutePath() + "'", 0);
            }
        }
    }

    private boolean multilineReplace(Reader r, Writer w, int options) throws IOException {
        return this.replaceAndWrite(FileUtils.safeReadFully(r), w, options);
    }

    private boolean replaceAndWrite(String s, Writer w, int options) throws IOException {
        String res = this.doReplace(this.regex, this.subs, s, options);
        w.write(res);
        return !res.equals(s);
    }
}

