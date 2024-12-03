/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.native2ascii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.function.UnaryOperator;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.optional.Native2Ascii;
import org.apache.tools.ant.taskdefs.optional.native2ascii.Native2AsciiAdapter;
import org.apache.tools.ant.util.Native2AsciiUtils;

public class BuiltinNative2Ascii
implements Native2AsciiAdapter {
    static final String IMPLEMENTATION_NAME = "builtin";

    /*
     * Enabled aggressive exception aggregation
     */
    @Override
    public final boolean convert(Native2Ascii args, File srcFile, File destFile) throws BuildException {
        boolean reverse = args.getReverse();
        String encoding = args.getEncoding();
        try (BufferedReader input = this.getReader(srcFile, encoding, reverse);){
            boolean bl;
            block14: {
                Writer output = this.getWriter(destFile, encoding, reverse);
                try {
                    this.translate(input, output, reverse ? Native2AsciiUtils::ascii2native : Native2AsciiUtils::native2ascii);
                    bl = true;
                    if (output == null) break block14;
                }
                catch (Throwable throwable) {
                    if (output != null) {
                        try {
                            output.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                output.close();
            }
            return bl;
        }
        catch (IOException ex) {
            throw new BuildException("Exception trying to translate data", ex);
        }
    }

    private BufferedReader getReader(File srcFile, String encoding, boolean reverse) throws IOException {
        if (reverse || encoding == null) {
            return new BufferedReader(new FileReader(srcFile));
        }
        return new BufferedReader(new InputStreamReader(Files.newInputStream(srcFile.toPath(), new OpenOption[0]), encoding));
    }

    private Writer getWriter(File destFile, String encoding, boolean reverse) throws IOException {
        if (!reverse) {
            encoding = "ASCII";
        }
        if (encoding == null) {
            return new BufferedWriter(new FileWriter(destFile));
        }
        return new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(destFile.toPath(), new OpenOption[0]), encoding));
    }

    private void translate(BufferedReader input, Writer output, UnaryOperator<String> translation) throws IOException {
        for (String line : () -> input.lines().map(translation).iterator()) {
            output.write(String.format("%s%n", line));
        }
    }
}

