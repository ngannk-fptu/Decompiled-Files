/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;
import org.aspectj.bridge.IMessageHolder;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.Message;
import org.aspectj.util.LangUtil;

public class MessageUtil {
    public static final IMessage ABORT_NOTHING_TO_RUN = new Message("aborting - nothing to run", IMessage.ABORT, null, null);
    public static final IMessage FAIL_INCOMPLETE = new Message("run not completed", IMessage.FAIL, null, null);
    public static final IMessage ABORT_NOMESSAGE = new Message("", IMessage.ABORT, null, null);
    public static final IMessage FAIL_NOMESSAGE = new Message("", IMessage.FAIL, null, null);
    public static final IMessage ERROR_NOMESSAGE = new Message("", IMessage.ERROR, null, null);
    public static final IMessage WARNING_NOMESSAGE = new Message("", IMessage.WARNING, null, null);
    public static final IMessageHandler PICK_ALL = new KindSelector(null);
    public static final IMessageHandler PICK_ABORT = new KindSelector(IMessage.ABORT);
    public static final IMessageHandler PICK_DEBUG = new KindSelector(IMessage.DEBUG);
    public static final IMessageHandler PICK_ERROR = new KindSelector(IMessage.ERROR);
    public static final IMessageHandler PICK_FAIL = new KindSelector(IMessage.FAIL);
    public static final IMessageHandler PICK_INFO = new KindSelector(IMessage.INFO);
    public static final IMessageHandler PICK_WARNING = new KindSelector(IMessage.WARNING);
    public static final IMessageHandler PICK_ABORT_PLUS = new KindSelector(IMessage.ABORT, true);
    public static final IMessageHandler PICK_DEBUG_PLUS = new KindSelector(IMessage.DEBUG, true);
    public static final IMessageHandler PICK_ERROR_PLUS = new KindSelector(IMessage.ERROR, true);
    public static final IMessageHandler PICK_FAIL_PLUS = new KindSelector(IMessage.FAIL, true);
    public static final IMessageHandler PICK_INFO_PLUS = new KindSelector(IMessage.INFO, true);
    public static final IMessageHandler PICK_WARNING_PLUS = new KindSelector(IMessage.WARNING, true);
    public static final IMessageRenderer MESSAGE_SCALED = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_SCALED";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            IMessage.Kind kind = message.getKind();
            int level = 3;
            level = kind == IMessage.ABORT || kind == IMessage.FAIL ? 1 : (kind == IMessage.ERROR || kind == IMessage.WARNING ? 2 : 3);
            String result = null;
            switch (level) {
                case 1: {
                    result = MESSAGE_TOSTRING.renderToString(message);
                    break;
                }
                case 2: {
                    result = MESSAGE_LINE.renderToString(message);
                    break;
                }
                case 3: {
                    result = MESSAGE_SHORT.renderToString(message);
                }
            }
            Throwable thrown = message.getThrown();
            if (null != thrown) {
                result = level == 3 ? result + "Thrown: \n" + LangUtil.renderExceptionShort(thrown) : result + "Thrown: \n" + LangUtil.renderException(thrown);
            }
            return result;
        }
    };
    public static final IMessageRenderer MESSAGE_LABEL = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_LABEL";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 5, 5, 32);
        }
    };
    public static final IMessageRenderer MESSAGE_LABEL_NOLOC = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_LABEL_NOLOC";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 10, 0, 32);
        }
    };
    public static final IMessageRenderer MESSAGE_LINE = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_LINE";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 8, 2, 74);
        }
    };
    public static final IMessageRenderer MESSAGE_LINE_FORCE_LOC = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_LINE_FORCE_LOC";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 2, 40, 74);
        }
    };
    public static final IMessageRenderer MESSAGE_ALL = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_ALL";
        }

        @Override
        public String renderToString(IMessage message) {
            return MessageUtil.renderMessage(message);
        }
    };
    public static final IMessageRenderer MESSAGE_MOST = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_MOST";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 1, 1, 10000);
        }
    };
    public static final IMessageRenderer MESSAGE_WIDELINE = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_WIDELINE";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return MessageUtil.renderMessageLine(message, 8, 2, 255);
        }
    };
    public static final IMessageRenderer MESSAGE_TOSTRING = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_TOSTRING";
        }

        @Override
        public String renderToString(IMessage message) {
            if (null == message) {
                return "((IMessage) null)";
            }
            return message.toString();
        }
    };
    public static final IMessageRenderer MESSAGE_SHORT = new IMessageRenderer(){

        public String toString() {
            return "MESSAGE_SHORT";
        }

        @Override
        public String renderToString(IMessage message) {
            return MessageUtil.toShortString(message);
        }
    };

    public static boolean abort(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.abort(message));
    }

    public static boolean abort(IMessageHandler handler, String message, Throwable t) {
        if (handler != null) {
            return handler.handleMessage(MessageUtil.abort(message, t));
        }
        return false;
    }

    public static boolean fail(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.fail(message));
    }

    public static boolean fail(IMessageHandler handler, String message, Throwable thrown) {
        return null != handler && handler.handleMessage(MessageUtil.fail(message, thrown));
    }

    public static boolean error(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.error(message));
    }

    public static boolean warn(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.warn(message));
    }

    public static boolean debug(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.debug(message));
    }

    public static boolean info(IMessageHandler handler, String message) {
        return null != handler && handler.handleMessage(MessageUtil.info(message));
    }

    public static IMessage abort(String message) {
        if (LangUtil.isEmpty(message)) {
            return ABORT_NOMESSAGE;
        }
        return new Message(message, IMessage.ABORT, null, null);
    }

    public static IMessage abort(String message, Throwable thrown) {
        if (!LangUtil.isEmpty(message)) {
            return new Message(message, IMessage.ABORT, thrown, null);
        }
        if (null == thrown) {
            return ABORT_NOMESSAGE;
        }
        return new Message(thrown.getMessage(), IMessage.ABORT, thrown, null);
    }

    public static IMessage fail(String message) {
        if (LangUtil.isEmpty(message)) {
            return FAIL_NOMESSAGE;
        }
        return new Message(message, IMessage.FAIL, null, ISourceLocation.EMPTY);
    }

    public static IMessage fail(String message, Throwable thrown) {
        if (LangUtil.isEmpty(message)) {
            if (null == thrown) {
                return FAIL_NOMESSAGE;
            }
            return new Message(thrown.getMessage(), IMessage.FAIL, thrown, null);
        }
        return new Message(message, IMessage.FAIL, thrown, null);
    }

    public static IMessage error(String message, ISourceLocation location) {
        if (LangUtil.isEmpty(message)) {
            return ERROR_NOMESSAGE;
        }
        return new Message(message, IMessage.ERROR, null, location);
    }

    public static IMessage warn(String message, ISourceLocation location) {
        if (LangUtil.isEmpty(message)) {
            return WARNING_NOMESSAGE;
        }
        return new Message(message, IMessage.WARNING, null, location);
    }

    public static IMessage error(String message) {
        if (LangUtil.isEmpty(message)) {
            return ERROR_NOMESSAGE;
        }
        return new Message(message, IMessage.ERROR, null, null);
    }

    public static IMessage warn(String message) {
        if (LangUtil.isEmpty(message)) {
            return WARNING_NOMESSAGE;
        }
        return new Message(message, IMessage.WARNING, null, null);
    }

    public static IMessage debug(String message) {
        return new Message(message, IMessage.DEBUG, null, null);
    }

    public static IMessage info(String message) {
        return new Message(message, IMessage.INFO, null, null);
    }

    public static void printMessageCounts(PrintStream out, IMessageHolder messageHolder) {
        if (null == out || null == messageHolder) {
            return;
        }
        MessageUtil.printMessageCounts(out, messageHolder, "");
    }

    public static void printMessageCounts(PrintStream out, IMessageHolder holder, String prefix) {
        out.println(prefix + "MessageHolder: " + MessageUtil.renderCounts(holder));
    }

    public static void print(PrintStream out, IMessageHolder messageHolder) {
        MessageUtil.print(out, messageHolder, null, null, null);
    }

    public static void print(PrintStream out, IMessageHolder holder, String prefix) {
        MessageUtil.print(out, holder, prefix, null, null);
    }

    public static void print(PrintStream out, IMessageHolder holder, String prefix, IMessageRenderer renderer) {
        MessageUtil.print(out, holder, prefix, renderer, null);
    }

    public static void print(PrintStream out, IMessageHolder holder, String prefix, IMessageRenderer renderer, IMessageHandler selector) {
        MessageUtil.print(out, holder, prefix, renderer, selector, true);
    }

    public static void print(PrintStream out, IMessageHolder holder, String prefix, IMessageRenderer renderer, IMessageHandler selector, boolean printSummary) {
        if (null == out || null == holder) {
            return;
        }
        if (null == renderer) {
            renderer = MESSAGE_ALL;
        }
        if (null == selector) {
            selector = PICK_ALL;
        }
        if (printSummary) {
            out.println(prefix + "MessageHolder: " + MessageUtil.renderCounts(holder));
        }
        for (IMessage.Kind kind : IMessage.KINDS) {
            if (selector.isIgnoring(kind)) continue;
            IMessage[] messages = holder.getMessages(kind, false);
            for (int i = 0; i < messages.length; ++i) {
                if (!selector.handleMessage(messages[i])) continue;
                String label = null == prefix ? "" : prefix + "[" + kind + " " + LangUtil.toSizedString(i, 3) + "]: ";
                out.println(label + renderer.renderToString(messages[i]));
            }
        }
    }

    public static String toShortString(IMessage message) {
        if (null == message) {
            return "null";
        }
        String m = message.getMessage();
        Throwable t = message.getThrown();
        return message.getKind() + (null == m ? "" : ": " + m) + (null == t ? "" : ": " + LangUtil.unqualifiedClassName(t));
    }

    public static int numMessages(List<IMessage> messages, IMessage.Kind kind, boolean orGreater) {
        if (LangUtil.isEmpty(messages)) {
            return 0;
        }
        IMessageHandler selector = MessageUtil.makeSelector(kind, orGreater, null);
        IMessage[] result = MessageUtil.visitMessages(messages, selector, true, false);
        return result.length;
    }

    public static IMessage[] getMessagesExcept(IMessageHolder holder, final IMessage.Kind kind, final boolean orGreater) {
        if (null == holder || null == kind) {
            return new IMessage[0];
        }
        IMessageHandler selector = new IMessageHandler(){

            @Override
            public boolean handleMessage(IMessage message) {
                IMessage.Kind test = message.getKind();
                return !(!orGreater ? kind == test : kind.isSameOrLessThan(test));
            }

            @Override
            public boolean isIgnoring(IMessage.Kind kind2) {
                return false;
            }

            @Override
            public void dontIgnore(IMessage.Kind kind2) {
            }

            @Override
            public void ignore(IMessage.Kind kind2) {
            }
        };
        return MessageUtil.visitMessages(holder, selector, true, false);
    }

    public static List<IMessage> getMessages(IMessageHolder holder, IMessage.Kind kind, boolean orGreater, String infix) {
        if (null == holder) {
            return Collections.emptyList();
        }
        if (null == kind && LangUtil.isEmpty(infix)) {
            return holder.getUnmodifiableListView();
        }
        IMessageHandler selector = MessageUtil.makeSelector(kind, orGreater, infix);
        Object[] messages = MessageUtil.visitMessages(holder, selector, true, false);
        if (LangUtil.isEmpty(messages)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(messages));
    }

    public static List<IMessage> getMessages(List<IMessage> messages, IMessage.Kind kind) {
        if (null == messages) {
            return Collections.emptyList();
        }
        if (null == kind) {
            return messages;
        }
        ArrayList<IMessage> result = new ArrayList<IMessage>();
        for (IMessage message : messages) {
            if (kind != message.getKind()) continue;
            result.add(message);
        }
        if (0 == result.size()) {
            return Collections.emptyList();
        }
        return result;
    }

    public static IMessage.Kind getKind(String kind) {
        if (null != kind) {
            kind = kind.toLowerCase();
            for (IMessage.Kind k : IMessage.KINDS) {
                if (!kind.equals(k.toString())) continue;
                return k;
            }
        }
        return null;
    }

    public static IMessage[] visitMessages(IMessageHolder holder, IMessageHandler visitor, boolean accumulate, boolean abortOnFail) {
        if (null == holder) {
            return IMessage.RA_IMessage;
        }
        return MessageUtil.visitMessages(holder.getUnmodifiableListView(), visitor, accumulate, abortOnFail);
    }

    public static IMessage[] visitMessages(IMessage[] messages, IMessageHandler visitor, boolean accumulate, boolean abortOnFail) {
        if (LangUtil.isEmpty(messages)) {
            return IMessage.RA_IMessage;
        }
        return MessageUtil.visitMessages(Arrays.asList(messages), visitor, accumulate, abortOnFail);
    }

    public static IMessage[] visitMessages(Collection<IMessage> messages, IMessageHandler visitor, boolean accumulate, boolean abortOnFail) {
        if (LangUtil.isEmpty(messages)) {
            return IMessage.RA_IMessage;
        }
        LangUtil.throwIaxIfNull(visitor, "visitor");
        ArrayList<IMessage> result = accumulate ? new ArrayList<IMessage>() : null;
        for (IMessage m : messages) {
            if (visitor.handleMessage(m)) {
                if (!accumulate) continue;
                result.add(m);
                continue;
            }
            if (!abortOnFail) continue;
            break;
        }
        if (!accumulate || 0 == result.size()) {
            return IMessage.RA_IMessage;
        }
        return result.toArray(IMessage.RA_IMessage);
    }

    public static IMessageHandler makeSelector(IMessage.Kind kind, boolean orGreater, String infix) {
        if (!orGreater && LangUtil.isEmpty(infix)) {
            if (kind == IMessage.ABORT) {
                return PICK_ABORT;
            }
            if (kind == IMessage.DEBUG) {
                return PICK_DEBUG;
            }
            if (kind == IMessage.DEBUG) {
                return PICK_DEBUG;
            }
            if (kind == IMessage.ERROR) {
                return PICK_ERROR;
            }
            if (kind == IMessage.FAIL) {
                return PICK_FAIL;
            }
            if (kind == IMessage.INFO) {
                return PICK_INFO;
            }
            if (kind == IMessage.WARNING) {
                return PICK_WARNING;
            }
        }
        return new KindSelector(kind, orGreater, infix);
    }

    public static String renderMessage(IMessage message) {
        return MessageUtil.renderMessage(message, true);
    }

    public static String renderMessage(IMessage message, boolean elide) {
        if (null == message) {
            return "((IMessage) null)";
        }
        ISourceLocation loc = message.getSourceLocation();
        String locString = null == loc ? "" : " at " + loc;
        String result = message.getKind() + locString + " " + message.getMessage();
        Throwable thrown = message.getThrown();
        if (thrown != null) {
            result = result + " -- " + LangUtil.renderExceptionShort(thrown);
            result = result + "\n" + LangUtil.renderException(thrown, elide);
        }
        if (message.getExtraSourceLocations().isEmpty()) {
            return result;
        }
        return MessageUtil.addExtraSourceLocations(message, result);
    }

    public static String addExtraSourceLocations(IMessage message, String baseMessage) {
        StringWriter buf = new StringWriter();
        PrintWriter writer = new PrintWriter(buf);
        writer.println(baseMessage);
        Iterator<ISourceLocation> iter = message.getExtraSourceLocations().iterator();
        while (iter.hasNext()) {
            ISourceLocation element = iter.next();
            if (element == null) continue;
            writer.print("\tsee also: " + element.toString());
            if (!iter.hasNext()) continue;
            writer.println();
        }
        try {
            buf.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return buf.getBuffer().toString();
    }

    public static String renderSourceLocation(ISourceLocation loc) {
        if (null == loc) {
            return "((ISourceLocation) null)";
        }
        StringBuffer sb = new StringBuffer();
        File sourceFile = loc.getSourceFile();
        if (sourceFile != ISourceLocation.NO_FILE) {
            sb.append(sourceFile.getPath());
            sb.append(":");
        }
        int line = loc.getLine();
        sb.append("" + line);
        int column = loc.getColumn();
        if (column != -2147483647) {
            sb.append(":" + column);
        }
        return sb.toString();
    }

    public static String renderMessageLine(IMessage message, int textScale, int locScale, int max) {
        String s;
        if (null == message) {
            return "((IMessage) null)";
        }
        if (max < 32) {
            max = 32;
        } else if (max > 10000) {
            max = 10000;
        }
        if (0 > textScale) {
            textScale = -textScale;
        }
        if (0 > locScale) {
            locScale = -locScale;
        }
        String text = message.getMessage();
        Throwable thrown = message.getThrown();
        ISourceLocation sl = message.getSourceLocation();
        IMessage.Kind kind = message.getKind();
        StringBuffer result = new StringBuffer();
        result.append(kind.toString());
        result.append(": ");
        if (null != thrown) {
            result.append(LangUtil.unqualifiedClassName(thrown) + " ");
            if (null == text || "".equals(text)) {
                text = thrown.getMessage();
            }
        }
        if (0 == textScale) {
            text = "";
        } else if (null != text && null != thrown && null != (s = thrown.getMessage()) && 0 < s.length()) {
            text = text + " - " + s;
        }
        String loc = "";
        if (0 != locScale && null != sl) {
            File f = sl.getSourceFile();
            if (f == ISourceLocation.NO_FILE) {
                f = null;
            }
            if (null != f) {
                loc = f.getName();
            }
            int line = sl.getLine();
            int col = sl.getColumn();
            int end = sl.getEndLine();
            if (0 != line || 0 != col || 0 != end) {
                loc = loc + ":" + line + (col == 0 ? "" : ":" + col);
                if (line != end) {
                    loc = loc + ":" + end;
                }
            }
            if (!LangUtil.isEmpty(loc)) {
                loc = "@[" + loc;
            }
        }
        float totalScale = locScale + textScale;
        float remainder = max - result.length() - 4;
        if (remainder > 0.0f && 0.0f < totalScale) {
            int textSize = (int)(remainder * (float)textScale / totalScale);
            int locSize = (int)(remainder * (float)locScale / totalScale);
            int extra = locSize - loc.length();
            if (0 < extra) {
                locSize = loc.length();
                textSize += extra;
            }
            if (0 < (extra = textSize - text.length())) {
                textSize = text.length();
                if (locSize < loc.length()) {
                    locSize += extra;
                }
            }
            if (locSize > loc.length()) {
                locSize = loc.length();
            }
            if (textSize > text.length()) {
                textSize = text.length();
            }
            if (0 < textSize) {
                result.append(text.substring(0, textSize));
            }
            if (0 < locSize) {
                if (0 < textSize) {
                    result.append(" ");
                }
                result.append(loc.substring(0, locSize) + "]");
            }
        }
        return result.toString();
    }

    public static String renderCounts(IMessageHolder holder) {
        if (0 == holder.numMessages(null, false)) {
            return "(0 messages)";
        }
        StringBuffer sb = new StringBuffer();
        for (IMessage.Kind kind : IMessage.KINDS) {
            int num = holder.numMessages(kind, false);
            if (0 >= num) continue;
            sb.append(" (" + num + " " + kind + ") ");
        }
        return sb.toString();
    }

    public static PrintStream handlerPrintStream(final IMessageHandler handler, final IMessage.Kind kind, final OutputStream overage, final String prefix) {
        LangUtil.throwIaxIfNull(handler, "handler");
        LangUtil.throwIaxIfNull(kind, "kind");
        class HandlerPrintStream
        extends PrintStream {
            HandlerPrintStream() {
                super(null == outputStream ? System.out : outputStream);
            }

            @Override
            public void println() {
                this.println("");
            }

            @Override
            public void println(Object o) {
                this.println(null == o ? "null" : o.toString());
            }

            @Override
            public void println(String input) {
                String textMessage = null == prefix ? input : prefix + input;
                Message m = new Message(textMessage, kind, null, null);
                handler.handleMessage(m);
            }
        }
        return new HandlerPrintStream();
    }

    private MessageUtil() {
    }

    public static boolean handleAll(IMessageHandler sink, IMessageHolder source, boolean fastFail) {
        return MessageUtil.handleAll(sink, source, null, true, fastFail);
    }

    public static boolean handleAll(IMessageHandler sink, IMessageHolder source, IMessage.Kind kind, boolean orGreater, boolean fastFail) {
        LangUtil.throwIaxIfNull(sink, "sink");
        LangUtil.throwIaxIfNull(source, "source");
        return MessageUtil.handleAll(sink, source.getMessages(kind, orGreater), fastFail);
    }

    public static boolean handleAllExcept(IMessageHandler sink, IMessageHolder source, IMessage.Kind kind, boolean orGreater, boolean fastFail) {
        LangUtil.throwIaxIfNull(sink, "sink");
        LangUtil.throwIaxIfNull(source, "source");
        if (null == kind) {
            return true;
        }
        IMessage[] messages = MessageUtil.getMessagesExcept(source, kind, orGreater);
        return MessageUtil.handleAll(sink, messages, fastFail);
    }

    public static boolean handleAll(IMessageHandler sink, IMessage[] sources, boolean fastFail) {
        LangUtil.throwIaxIfNull(sink, "sink");
        if (LangUtil.isEmpty(sources)) {
            return true;
        }
        boolean result = true;
        for (int i = 0; i < sources.length; ++i) {
            if (sink.handleMessage(sources[i])) continue;
            if (fastFail) {
                return false;
            }
            if (!result) continue;
            result = false;
        }
        return result;
    }

    public static interface IMessageRenderer {
        public String renderToString(IMessage var1);
    }

    private static class KindSelector
    implements IMessageHandler {
        final IMessage.Kind sought;
        final boolean floor;
        final String infix;

        KindSelector(IMessage.Kind sought) {
            this(sought, false);
        }

        KindSelector(IMessage.Kind sought, boolean floor) {
            this(sought, floor, null);
        }

        KindSelector(IMessage.Kind sought, boolean floor, String infix) {
            this.sought = sought;
            this.floor = floor;
            this.infix = LangUtil.isEmpty(infix) ? null : infix;
        }

        @Override
        public boolean handleMessage(IMessage message) {
            return null != message && !this.isIgnoring(message.getKind()) && this.textIn(message);
        }

        @Override
        public boolean isIgnoring(IMessage.Kind kind) {
            if (!this.floor) {
                return null != this.sought && this.sought != kind;
            }
            if (null == this.sought) {
                return false;
            }
            return 0 < IMessage.Kind.COMPARATOR.compare(this.sought, kind);
        }

        @Override
        public void dontIgnore(IMessage.Kind kind) {
        }

        private boolean textIn(IMessage message) {
            if (null == this.infix) {
                return true;
            }
            String text = message.getMessage();
            return text.indexOf(this.infix) != -1;
        }

        @Override
        public void ignore(IMessage.Kind kind) {
        }
    }
}

