/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug;

import groovyjarjarantlr.collections.impl.BitSet;
import groovyjarjarantlr.debug.ListenerBase;
import groovyjarjarantlr.debug.MessageEvent;
import groovyjarjarantlr.debug.MessageListener;
import groovyjarjarantlr.debug.NewLineEvent;
import groovyjarjarantlr.debug.NewLineListener;
import groovyjarjarantlr.debug.ParserController;
import groovyjarjarantlr.debug.ParserListener;
import groovyjarjarantlr.debug.ParserMatchEvent;
import groovyjarjarantlr.debug.ParserMatchListener;
import groovyjarjarantlr.debug.ParserTokenEvent;
import groovyjarjarantlr.debug.ParserTokenListener;
import groovyjarjarantlr.debug.SemanticPredicateEvent;
import groovyjarjarantlr.debug.SemanticPredicateListener;
import groovyjarjarantlr.debug.SyntacticPredicateEvent;
import groovyjarjarantlr.debug.SyntacticPredicateListener;
import groovyjarjarantlr.debug.TraceEvent;
import groovyjarjarantlr.debug.TraceListener;
import java.util.Hashtable;
import java.util.Vector;

public class ParserEventSupport {
    private Object source;
    private Hashtable doneListeners;
    private Vector matchListeners;
    private Vector messageListeners;
    private Vector tokenListeners;
    private Vector traceListeners;
    private Vector semPredListeners;
    private Vector synPredListeners;
    private Vector newLineListeners;
    private ParserMatchEvent matchEvent;
    private MessageEvent messageEvent;
    private ParserTokenEvent tokenEvent;
    private SemanticPredicateEvent semPredEvent;
    private SyntacticPredicateEvent synPredEvent;
    private TraceEvent traceEvent;
    private NewLineEvent newLineEvent;
    private ParserController controller;
    protected static final int CONSUME = 0;
    protected static final int ENTER_RULE = 1;
    protected static final int EXIT_RULE = 2;
    protected static final int LA = 3;
    protected static final int MATCH = 4;
    protected static final int MATCH_NOT = 5;
    protected static final int MISMATCH = 6;
    protected static final int MISMATCH_NOT = 7;
    protected static final int REPORT_ERROR = 8;
    protected static final int REPORT_WARNING = 9;
    protected static final int SEMPRED = 10;
    protected static final int SYNPRED_FAILED = 11;
    protected static final int SYNPRED_STARTED = 12;
    protected static final int SYNPRED_SUCCEEDED = 13;
    protected static final int NEW_LINE = 14;
    protected static final int DONE_PARSING = 15;
    private int ruleDepth = 0;

    public ParserEventSupport(Object object) {
        this.matchEvent = new ParserMatchEvent(object);
        this.messageEvent = new MessageEvent(object);
        this.tokenEvent = new ParserTokenEvent(object);
        this.traceEvent = new TraceEvent(object);
        this.semPredEvent = new SemanticPredicateEvent(object);
        this.synPredEvent = new SyntacticPredicateEvent(object);
        this.newLineEvent = new NewLineEvent(object);
        this.source = object;
    }

    public void addDoneListener(ListenerBase listenerBase) {
        Integer n;
        if (this.doneListeners == null) {
            this.doneListeners = new Hashtable();
        }
        int n2 = (n = (Integer)this.doneListeners.get(listenerBase)) != null ? n + 1 : 1;
        this.doneListeners.put(listenerBase, new Integer(n2));
    }

    public void addMessageListener(MessageListener messageListener) {
        if (this.messageListeners == null) {
            this.messageListeners = new Vector();
        }
        this.messageListeners.addElement(messageListener);
        this.addDoneListener(messageListener);
    }

    public void addNewLineListener(NewLineListener newLineListener) {
        if (this.newLineListeners == null) {
            this.newLineListeners = new Vector();
        }
        this.newLineListeners.addElement(newLineListener);
        this.addDoneListener(newLineListener);
    }

    public void addParserListener(ParserListener parserListener) {
        if (parserListener instanceof ParserController) {
            ((ParserController)parserListener).setParserEventSupport(this);
            this.controller = (ParserController)parserListener;
        }
        this.addParserMatchListener(parserListener);
        this.addParserTokenListener(parserListener);
        this.addMessageListener(parserListener);
        this.addTraceListener(parserListener);
        this.addSemanticPredicateListener(parserListener);
        this.addSyntacticPredicateListener(parserListener);
    }

    public void addParserMatchListener(ParserMatchListener parserMatchListener) {
        if (this.matchListeners == null) {
            this.matchListeners = new Vector();
        }
        this.matchListeners.addElement(parserMatchListener);
        this.addDoneListener(parserMatchListener);
    }

    public void addParserTokenListener(ParserTokenListener parserTokenListener) {
        if (this.tokenListeners == null) {
            this.tokenListeners = new Vector();
        }
        this.tokenListeners.addElement(parserTokenListener);
        this.addDoneListener(parserTokenListener);
    }

    public void addSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        if (this.semPredListeners == null) {
            this.semPredListeners = new Vector();
        }
        this.semPredListeners.addElement(semanticPredicateListener);
        this.addDoneListener(semanticPredicateListener);
    }

    public void addSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        if (this.synPredListeners == null) {
            this.synPredListeners = new Vector();
        }
        this.synPredListeners.addElement(syntacticPredicateListener);
        this.addDoneListener(syntacticPredicateListener);
    }

    public void addTraceListener(TraceListener traceListener) {
        if (this.traceListeners == null) {
            this.traceListeners = new Vector();
        }
        this.traceListeners.addElement(traceListener);
        this.addDoneListener(traceListener);
    }

    public void fireConsume(int n) {
        this.tokenEvent.setValues(ParserTokenEvent.CONSUME, 1, n);
        this.fireEvents(0, this.tokenListeners);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void fireDoneParsing() {
        this.traceEvent.setValues(TraceEvent.DONE_PARSING, 0, 0, 0);
        Hashtable hashtable = null;
        ListenerBase listenerBase = null;
        Object object = this;
        synchronized (object) {
            if (this.doneListeners == null) {
                return;
            }
            hashtable = (Hashtable)this.doneListeners.clone();
        }
        if (hashtable != null) {
            object = hashtable.keys();
            while (object.hasMoreElements()) {
                listenerBase = (ListenerBase)object.nextElement();
                this.fireEvent(15, listenerBase);
            }
        }
        if (this.controller != null) {
            this.controller.checkBreak();
        }
    }

    public void fireEnterRule(int n, int n2, int n3) {
        ++this.ruleDepth;
        this.traceEvent.setValues(TraceEvent.ENTER, n, n2, n3);
        this.fireEvents(1, this.traceListeners);
    }

    public void fireEvent(int n, ListenerBase listenerBase) {
        switch (n) {
            case 0: {
                ((ParserTokenListener)listenerBase).parserConsume(this.tokenEvent);
                break;
            }
            case 3: {
                ((ParserTokenListener)listenerBase).parserLA(this.tokenEvent);
                break;
            }
            case 1: {
                ((TraceListener)listenerBase).enterRule(this.traceEvent);
                break;
            }
            case 2: {
                ((TraceListener)listenerBase).exitRule(this.traceEvent);
                break;
            }
            case 4: {
                ((ParserMatchListener)listenerBase).parserMatch(this.matchEvent);
                break;
            }
            case 5: {
                ((ParserMatchListener)listenerBase).parserMatchNot(this.matchEvent);
                break;
            }
            case 6: {
                ((ParserMatchListener)listenerBase).parserMismatch(this.matchEvent);
                break;
            }
            case 7: {
                ((ParserMatchListener)listenerBase).parserMismatchNot(this.matchEvent);
                break;
            }
            case 10: {
                ((SemanticPredicateListener)listenerBase).semanticPredicateEvaluated(this.semPredEvent);
                break;
            }
            case 12: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateStarted(this.synPredEvent);
                break;
            }
            case 11: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateFailed(this.synPredEvent);
                break;
            }
            case 13: {
                ((SyntacticPredicateListener)listenerBase).syntacticPredicateSucceeded(this.synPredEvent);
                break;
            }
            case 8: {
                ((MessageListener)listenerBase).reportError(this.messageEvent);
                break;
            }
            case 9: {
                ((MessageListener)listenerBase).reportWarning(this.messageEvent);
                break;
            }
            case 15: {
                listenerBase.doneParsing(this.traceEvent);
                break;
            }
            case 14: {
                ((NewLineListener)listenerBase).hitNewLine(this.newLineEvent);
                break;
            }
            default: {
                throw new IllegalArgumentException("bad type " + n + " for fireEvent()");
            }
        }
    }

    public void fireEvents(int n, Vector vector) {
        ListenerBase listenerBase = null;
        if (vector != null) {
            for (int i = 0; i < vector.size(); ++i) {
                listenerBase = (ListenerBase)vector.elementAt(i);
                this.fireEvent(n, listenerBase);
            }
        }
        if (this.controller != null) {
            this.controller.checkBreak();
        }
    }

    public void fireExitRule(int n, int n2, int n3) {
        this.traceEvent.setValues(TraceEvent.EXIT, n, n2, n3);
        this.fireEvents(2, this.traceListeners);
        --this.ruleDepth;
        if (this.ruleDepth == 0) {
            this.fireDoneParsing();
        }
    }

    public void fireLA(int n, int n2) {
        this.tokenEvent.setValues(ParserTokenEvent.LA, n, n2);
        this.fireEvents(3, this.tokenListeners);
    }

    public void fireMatch(char c, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(c), null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatch(char c, BitSet bitSet, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_BITSET, c, bitSet, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatch(char c, String string, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_RANGE, c, string, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatch(int n, BitSet bitSet, String string, int n2) {
        this.matchEvent.setValues(ParserMatchEvent.BITSET, n, bitSet, string, n2, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatch(int n, String string, int n2) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(n), string, n2, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatch(String string, int n) {
        this.matchEvent.setValues(ParserMatchEvent.STRING, 0, string, null, n, false, true);
        this.fireEvents(4, this.matchListeners);
    }

    public void fireMatchNot(char c, char c2, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(c2), null, n, true, true);
        this.fireEvents(5, this.matchListeners);
    }

    public void fireMatchNot(int n, int n2, String string, int n3) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(n2), string, n3, true, true);
        this.fireEvents(5, this.matchListeners);
    }

    public void fireMismatch(char c, char c2, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(c2), null, n, false, false);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatch(char c, BitSet bitSet, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_BITSET, c, bitSet, null, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatch(char c, String string, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR_RANGE, c, string, null, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatch(int n, int n2, String string, int n3) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(n2), string, n3, false, false);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatch(int n, BitSet bitSet, String string, int n2) {
        this.matchEvent.setValues(ParserMatchEvent.BITSET, n, bitSet, string, n2, false, true);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatch(String string, String string2, int n) {
        this.matchEvent.setValues(ParserMatchEvent.STRING, 0, string2, string, n, false, true);
        this.fireEvents(6, this.matchListeners);
    }

    public void fireMismatchNot(char c, char c2, int n) {
        this.matchEvent.setValues(ParserMatchEvent.CHAR, c, new Character(c2), null, n, true, true);
        this.fireEvents(7, this.matchListeners);
    }

    public void fireMismatchNot(int n, int n2, String string, int n3) {
        this.matchEvent.setValues(ParserMatchEvent.TOKEN, n, new Integer(n2), string, n3, true, true);
        this.fireEvents(7, this.matchListeners);
    }

    public void fireNewLine(int n) {
        this.newLineEvent.setValues(n);
        this.fireEvents(14, this.newLineListeners);
    }

    public void fireReportError(Exception exception) {
        this.messageEvent.setValues(MessageEvent.ERROR, exception.toString());
        this.fireEvents(8, this.messageListeners);
    }

    public void fireReportError(String string) {
        this.messageEvent.setValues(MessageEvent.ERROR, string);
        this.fireEvents(8, this.messageListeners);
    }

    public void fireReportWarning(String string) {
        this.messageEvent.setValues(MessageEvent.WARNING, string);
        this.fireEvents(9, this.messageListeners);
    }

    public boolean fireSemanticPredicateEvaluated(int n, int n2, boolean bl, int n3) {
        this.semPredEvent.setValues(n, n2, bl, n3);
        this.fireEvents(10, this.semPredListeners);
        return bl;
    }

    public void fireSyntacticPredicateFailed(int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(11, this.synPredListeners);
    }

    public void fireSyntacticPredicateStarted(int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(12, this.synPredListeners);
    }

    public void fireSyntacticPredicateSucceeded(int n) {
        this.synPredEvent.setValues(0, n);
        this.fireEvents(13, this.synPredListeners);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void refresh(Vector vector) {
        Vector vector2;
        Vector vector3 = vector;
        synchronized (vector3) {
            vector2 = (Vector)vector.clone();
        }
        if (vector2 != null) {
            for (int i = 0; i < vector2.size(); ++i) {
                ((ListenerBase)vector2.elementAt(i)).refresh();
            }
        }
    }

    public void refreshListeners() {
        this.refresh(this.matchListeners);
        this.refresh(this.messageListeners);
        this.refresh(this.tokenListeners);
        this.refresh(this.traceListeners);
        this.refresh(this.semPredListeners);
        this.refresh(this.synPredListeners);
    }

    public void removeDoneListener(ListenerBase listenerBase) {
        if (this.doneListeners == null) {
            return;
        }
        Integer n = (Integer)this.doneListeners.get(listenerBase);
        int n2 = 0;
        if (n != null) {
            n2 = n - 1;
        }
        if (n2 == 0) {
            this.doneListeners.remove(listenerBase);
        } else {
            this.doneListeners.put(listenerBase, new Integer(n2));
        }
    }

    public void removeMessageListener(MessageListener messageListener) {
        if (this.messageListeners != null) {
            this.messageListeners.removeElement(messageListener);
        }
        this.removeDoneListener(messageListener);
    }

    public void removeNewLineListener(NewLineListener newLineListener) {
        if (this.newLineListeners != null) {
            this.newLineListeners.removeElement(newLineListener);
        }
        this.removeDoneListener(newLineListener);
    }

    public void removeParserListener(ParserListener parserListener) {
        this.removeParserMatchListener(parserListener);
        this.removeMessageListener(parserListener);
        this.removeParserTokenListener(parserListener);
        this.removeTraceListener(parserListener);
        this.removeSemanticPredicateListener(parserListener);
        this.removeSyntacticPredicateListener(parserListener);
    }

    public void removeParserMatchListener(ParserMatchListener parserMatchListener) {
        if (this.matchListeners != null) {
            this.matchListeners.removeElement(parserMatchListener);
        }
        this.removeDoneListener(parserMatchListener);
    }

    public void removeParserTokenListener(ParserTokenListener parserTokenListener) {
        if (this.tokenListeners != null) {
            this.tokenListeners.removeElement(parserTokenListener);
        }
        this.removeDoneListener(parserTokenListener);
    }

    public void removeSemanticPredicateListener(SemanticPredicateListener semanticPredicateListener) {
        if (this.semPredListeners != null) {
            this.semPredListeners.removeElement(semanticPredicateListener);
        }
        this.removeDoneListener(semanticPredicateListener);
    }

    public void removeSyntacticPredicateListener(SyntacticPredicateListener syntacticPredicateListener) {
        if (this.synPredListeners != null) {
            this.synPredListeners.removeElement(syntacticPredicateListener);
        }
        this.removeDoneListener(syntacticPredicateListener);
    }

    public void removeTraceListener(TraceListener traceListener) {
        if (this.traceListeners != null) {
            this.traceListeners.removeElement(traceListener);
        }
        this.removeDoneListener(traceListener);
    }
}

