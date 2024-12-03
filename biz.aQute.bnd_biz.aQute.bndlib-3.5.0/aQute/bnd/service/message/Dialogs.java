/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service.message;

import aQute.bnd.service.message.Progress;
import aQute.service.reporter.Reporter;
import java.util.regex.Pattern;
import org.osgi.util.promise.Promise;

public interface Dialogs {
    public Promise<Integer> message(String var1, String var2, String[] var3, int var4) throws Exception;

    public Promise<String> prompt(String var1, String var2, String var3, Pattern var4) throws Exception;

    public void errors(String var1, Reporter var2) throws Exception;

    public Progress createProgress(String var1) throws Exception;
}

