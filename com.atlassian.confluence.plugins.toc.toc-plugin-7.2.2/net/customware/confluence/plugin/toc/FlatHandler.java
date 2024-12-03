/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.MacroExecutionException
 */
package net.customware.confluence.plugin.toc;

import com.atlassian.confluence.macro.MacroExecutionException;
import java.io.IOException;
import net.customware.confluence.plugin.toc.OutputHandler;
import net.customware.confluence.plugin.toc.SeparatorType;

public class FlatHandler
implements OutputHandler {
    private boolean firstItemWritten = false;
    private String pre;
    private String mid;
    private String post;

    public FlatHandler(String separatorName) throws MacroExecutionException {
        try {
            SeparatorType separatorType = SeparatorType.valueOfSeparator(separatorName);
            this.pre = separatorType.getPre();
            this.mid = separatorType.getMid();
            this.post = separatorType.getPost();
        }
        catch (Exception e) {
            this.pre = "";
            this.mid = separatorName;
            this.post = "";
        }
    }

    @Override
    public String appendStyle(Appendable out) {
        return null;
    }

    @Override
    public void appendIncLevel(Appendable out) {
    }

    @Override
    public void appendDecLevel(Appendable out) {
    }

    @Override
    public void appendPrefix(Appendable out) throws IOException {
        out.append(this.pre);
    }

    @Override
    public void appendPostfix(Appendable out) throws IOException {
        if (!this.firstItemWritten) {
            return;
        }
        out.append(this.post);
    }

    @Override
    public void appendSeparator(Appendable out) throws IOException {
        if (!this.firstItemWritten) {
            return;
        }
        out.append(this.mid);
    }

    @Override
    public void appendHeading(Appendable out, String string) throws IOException {
        out.append(string);
        this.firstItemWritten = true;
    }
}

