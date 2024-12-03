/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.tools.shell;

import java.io.InputStream;
import java.nio.charset.Charset;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.ShellConsole;

@Deprecated
public class ShellLine {
    @Deprecated
    public static InputStream getStream(Scriptable scope) {
        ShellConsole console = ShellConsole.getConsole(scope, Charset.defaultCharset());
        return console != null ? console.getIn() : null;
    }
}

