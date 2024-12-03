/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.exception.Nestable;

public class NestableDelegate
implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final transient String MUST_BE_THROWABLE = "The Nestable implementation passed to the NestableDelegate(Nestable) constructor must extend java.lang.Throwable";
    private Throwable nestable = null;
    public static boolean topDown = true;
    public static boolean trimStackFrames = true;
    public static boolean matchSubclasses = true;
    static /* synthetic */ Class class$org$apache$commons$lang$exception$Nestable;

    public NestableDelegate(Nestable nestable) {
        if (!(nestable instanceof Throwable)) {
            throw new IllegalArgumentException(MUST_BE_THROWABLE);
        }
        this.nestable = (Throwable)((Object)nestable);
    }

    public String getMessage(int index) {
        Throwable t;
        if ((class$org$apache$commons$lang$exception$Nestable == null ? (class$org$apache$commons$lang$exception$Nestable = NestableDelegate.class$("org.apache.commons.lang.exception.Nestable")) : class$org$apache$commons$lang$exception$Nestable).isInstance(t = this.getThrowable(index))) {
            return ((Nestable)((Object)t)).getMessage(0);
        }
        return t.getMessage();
    }

    public String getMessage(String baseMsg) {
        String causeMsg;
        Throwable nestedCause = ExceptionUtils.getCause(this.nestable);
        String string = causeMsg = nestedCause == null ? null : nestedCause.getMessage();
        if (nestedCause == null || causeMsg == null) {
            return baseMsg;
        }
        if (baseMsg == null) {
            return causeMsg;
        }
        return baseMsg + ": " + causeMsg;
    }

    public String[] getMessages() {
        Throwable[] throwables = this.getThrowables();
        String[] msgs = new String[throwables.length];
        for (int i = 0; i < throwables.length; ++i) {
            msgs[i] = (class$org$apache$commons$lang$exception$Nestable == null ? NestableDelegate.class$("org.apache.commons.lang.exception.Nestable") : class$org$apache$commons$lang$exception$Nestable).isInstance(throwables[i]) ? ((Nestable)((Object)throwables[i])).getMessage(0) : throwables[i].getMessage();
        }
        return msgs;
    }

    public Throwable getThrowable(int index) {
        if (index == 0) {
            return this.nestable;
        }
        Throwable[] throwables = this.getThrowables();
        return throwables[index];
    }

    public int getThrowableCount() {
        return ExceptionUtils.getThrowableCount(this.nestable);
    }

    public Throwable[] getThrowables() {
        return ExceptionUtils.getThrowables(this.nestable);
    }

    public int indexOfThrowable(Class type, int fromIndex) {
        if (type == null) {
            return -1;
        }
        if (fromIndex < 0) {
            throw new IndexOutOfBoundsException("The start index was out of bounds: " + fromIndex);
        }
        Throwable[] throwables = ExceptionUtils.getThrowables(this.nestable);
        if (fromIndex >= throwables.length) {
            throw new IndexOutOfBoundsException("The start index was out of bounds: " + fromIndex + " >= " + throwables.length);
        }
        if (matchSubclasses) {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (!type.isAssignableFrom(throwables[i].getClass())) continue;
                return i;
            }
        } else {
            for (int i = fromIndex; i < throwables.length; ++i) {
                if (!type.equals(throwables[i].getClass())) continue;
                return i;
            }
        }
        return -1;
    }

    public void printStackTrace() {
        this.printStackTrace(System.err);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintStream out) {
        PrintStream printStream = out;
        synchronized (printStream) {
            PrintWriter pw = new PrintWriter(out, false);
            this.printStackTrace(pw);
            pw.flush();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void printStackTrace(PrintWriter out) {
        Throwable throwable = this.nestable;
        if (ExceptionUtils.isThrowableNested()) {
            if (throwable instanceof Nestable) {
                ((Nestable)((Object)throwable)).printPartialStackTrace(out);
            } else {
                throwable.printStackTrace(out);
            }
            return;
        }
        ArrayList<String[]> stacks = new ArrayList<String[]>();
        while (throwable != null) {
            String[] st = this.getStackFrames(throwable);
            stacks.add(st);
            throwable = ExceptionUtils.getCause(throwable);
        }
        String separatorLine = "Caused by: ";
        if (!topDown) {
            separatorLine = "Rethrown as: ";
            Collections.reverse(stacks);
        }
        if (trimStackFrames) {
            this.trimStackFrames(stacks);
        }
        PrintWriter printWriter = out;
        synchronized (printWriter) {
            Iterator iter = stacks.iterator();
            while (iter.hasNext()) {
                String[] st = (String[])iter.next();
                int len = st.length;
                for (int i = 0; i < len; ++i) {
                    out.println(st[i]);
                }
                if (!iter.hasNext()) continue;
                out.print(separatorLine);
            }
        }
    }

    protected String[] getStackFrames(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        if (t instanceof Nestable) {
            ((Nestable)((Object)t)).printPartialStackTrace(pw);
        } else {
            t.printStackTrace(pw);
        }
        return ExceptionUtils.getStackFrames(sw.getBuffer().toString());
    }

    protected void trimStackFrames(List stacks) {
        int size = stacks.size();
        for (int i = size - 1; i > 0; --i) {
            String[] curr = (String[])stacks.get(i);
            String[] next = (String[])stacks.get(i - 1);
            ArrayList<String> currList = new ArrayList<String>(Arrays.asList(curr));
            ArrayList<String> nextList = new ArrayList<String>(Arrays.asList(next));
            ExceptionUtils.removeCommonFrames(currList, nextList);
            int trimmed = curr.length - currList.size();
            if (trimmed <= 0) continue;
            currList.add("\t... " + trimmed + " more");
            stacks.set(i, currList.toArray(new String[currList.size()]));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

