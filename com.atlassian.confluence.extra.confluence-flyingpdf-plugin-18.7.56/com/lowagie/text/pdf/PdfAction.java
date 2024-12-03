/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBoolean;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfEncodings;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfLayer;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfRendition;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.collection.PdfTargetDictionary;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class PdfAction
extends PdfDictionary {
    public static final int FIRSTPAGE = 1;
    public static final int PREVPAGE = 2;
    public static final int NEXTPAGE = 3;
    public static final int LASTPAGE = 4;
    public static final int PRINTDIALOG = 5;
    public static final int SUBMIT_EXCLUDE = 1;
    public static final int SUBMIT_INCLUDE_NO_VALUE_FIELDS = 2;
    public static final int SUBMIT_HTML_FORMAT = 4;
    public static final int SUBMIT_HTML_GET = 8;
    public static final int SUBMIT_COORDINATES = 16;
    public static final int SUBMIT_XFDF = 32;
    public static final int SUBMIT_INCLUDE_APPEND_SAVES = 64;
    public static final int SUBMIT_INCLUDE_ANNOTATIONS = 128;
    public static final int SUBMIT_PDF = 256;
    public static final int SUBMIT_CANONICAL_FORMAT = 512;
    public static final int SUBMIT_EXCL_NON_USER_ANNOTS = 1024;
    public static final int SUBMIT_EXCL_F_KEY = 2048;
    public static final int SUBMIT_EMBED_FORM = 8196;
    public static final int RESET_EXCLUDE = 1;

    public PdfAction() {
    }

    public PdfAction(URL url) {
        this(url.toExternalForm());
    }

    public PdfAction(URL url, boolean isMap) {
        this(url.toExternalForm(), isMap);
    }

    public PdfAction(String url) {
        this(url, false);
    }

    public PdfAction(String url, boolean isMap) {
        this.put(PdfName.S, PdfName.URI);
        this.put(PdfName.URI, new PdfString(url));
        if (isMap) {
            this.put(PdfName.ISMAP, PdfBoolean.PDFTRUE);
        }
    }

    PdfAction(PdfIndirectReference destination) {
        this.put(PdfName.S, PdfName.GOTO);
        this.put(PdfName.D, destination);
    }

    public PdfAction(String filename, String name) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(filename));
        this.put(PdfName.D, new PdfString(name));
    }

    public PdfAction(String filename, int page) {
        this.put(PdfName.S, PdfName.GOTOR);
        this.put(PdfName.F, new PdfString(filename));
        this.put(PdfName.D, new PdfLiteral("[" + (page - 1) + " /FitH 10000]"));
    }

    public PdfAction(int named) {
        this.put(PdfName.S, PdfName.NAMED);
        switch (named) {
            case 1: {
                this.put(PdfName.N, PdfName.FIRSTPAGE);
                break;
            }
            case 4: {
                this.put(PdfName.N, PdfName.LASTPAGE);
                break;
            }
            case 3: {
                this.put(PdfName.N, PdfName.NEXTPAGE);
                break;
            }
            case 2: {
                this.put(PdfName.N, PdfName.PREVPAGE);
                break;
            }
            case 5: {
                this.put(PdfName.S, PdfName.JAVASCRIPT);
                this.put(PdfName.JS, new PdfString("this.print(true);\r"));
                break;
            }
            default: {
                throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.named.action"));
            }
        }
    }

    public PdfAction(String application, String parameters, String operation, String defaultDir) {
        this.put(PdfName.S, PdfName.LAUNCH);
        if (parameters == null && operation == null && defaultDir == null) {
            this.put(PdfName.F, new PdfString(application));
        } else {
            PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.F, new PdfString(application));
            if (parameters != null) {
                dic.put(PdfName.P, new PdfString(parameters));
            }
            if (operation != null) {
                dic.put(PdfName.O, new PdfString(operation));
            }
            if (defaultDir != null) {
                dic.put(PdfName.D, new PdfString(defaultDir));
            }
            this.put(PdfName.WIN, dic);
        }
    }

    public static PdfAction createLaunch(String application, String parameters, String operation, String defaultDir) {
        return new PdfAction(application, parameters, operation, defaultDir);
    }

    public static PdfAction rendition(String file, PdfFileSpecification fs, String mimeType, PdfIndirectReference ref) throws IOException {
        PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.RENDITION);
        js.put(PdfName.R, new PdfRendition(file, fs, mimeType));
        js.put(new PdfName("OP"), new PdfNumber(0));
        js.put(new PdfName("AN"), ref);
        return js;
    }

    public static PdfAction javaScript(String code, PdfWriter writer, boolean unicode) {
        PdfAction js = new PdfAction();
        js.put(PdfName.S, PdfName.JAVASCRIPT);
        if (unicode && code.length() < 50) {
            js.put(PdfName.JS, new PdfString(code, "UnicodeBig"));
        } else if (!unicode && code.length() < 100) {
            js.put(PdfName.JS, new PdfString(code));
        } else {
            try {
                byte[] b = PdfEncodings.convertToBytes(code, unicode ? "UnicodeBig" : "PDF");
                PdfStream stream = new PdfStream(b);
                stream.flateCompress(writer.getCompressionLevel());
                js.put(PdfName.JS, writer.addToBody(stream).getIndirectReference());
            }
            catch (Exception e) {
                js.put(PdfName.JS, new PdfString(code));
            }
        }
        return js;
    }

    public static PdfAction javaScript(String code, PdfWriter writer) {
        return PdfAction.javaScript(code, writer, false);
    }

    static PdfAction createHide(PdfObject obj, boolean hide) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.HIDE);
        action.put(PdfName.T, obj);
        if (!hide) {
            action.put(PdfName.H, PdfBoolean.PDFFALSE);
        }
        return action;
    }

    public static PdfAction createHide(PdfAnnotation annot, boolean hide) {
        return PdfAction.createHide(annot.getIndirectReference(), hide);
    }

    public static PdfAction createHide(String name, boolean hide) {
        return PdfAction.createHide(new PdfString(name), hide);
    }

    static PdfArray buildArray(Object[] names) {
        PdfArray array = new PdfArray();
        for (Object obj : names) {
            if (obj instanceof String) {
                array.add(new PdfString((String)obj));
                continue;
            }
            if (obj instanceof PdfAnnotation) {
                array.add(((PdfAnnotation)obj).getIndirectReference());
                continue;
            }
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.array.must.contain.string.or.pdfannotation"));
        }
        return array;
    }

    public static PdfAction createHide(Object[] names, boolean hide) {
        return PdfAction.createHide(PdfAction.buildArray(names), hide);
    }

    public static PdfAction createSubmitForm(String file, Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SUBMITFORM);
        PdfDictionary dic = new PdfDictionary();
        dic.put(PdfName.F, new PdfString(file));
        dic.put(PdfName.FS, PdfName.URL);
        action.put(PdfName.F, dic);
        if (names != null) {
            action.put(PdfName.FIELDS, PdfAction.buildArray(names));
        }
        action.put(PdfName.FLAGS, new PdfNumber(flags));
        return action;
    }

    public static PdfAction createResetForm(Object[] names, int flags) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.RESETFORM);
        if (names != null) {
            action.put(PdfName.FIELDS, PdfAction.buildArray(names));
        }
        action.put(PdfName.FLAGS, new PdfNumber(flags));
        return action;
    }

    public static PdfAction createImportData(String file) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.IMPORTDATA);
        action.put(PdfName.F, new PdfString(file));
        return action;
    }

    public void next(PdfAction na) {
        PdfObject nextAction = this.get(PdfName.NEXT);
        if (nextAction == null) {
            this.put(PdfName.NEXT, na);
        } else if (nextAction.isDictionary()) {
            PdfArray array = new PdfArray(nextAction);
            array.add(na);
            this.put(PdfName.NEXT, array);
        } else {
            ((PdfArray)nextAction).add(na);
        }
    }

    public static PdfAction gotoLocalPage(int page, PdfDestination dest, PdfWriter writer) {
        PdfIndirectReference ref = writer.getPageReference(page);
        dest.addPage(ref);
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTO);
        action.put(PdfName.D, dest);
        return action;
    }

    public static PdfAction gotoLocalPage(String dest, boolean isName) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTO);
        if (isName) {
            action.put(PdfName.D, new PdfName(dest));
        } else {
            action.put(PdfName.D, new PdfString(dest, null));
        }
        return action;
    }

    public static PdfAction gotoRemotePage(String filename, String dest, boolean isName, boolean newWindow) {
        PdfAction action = new PdfAction();
        action.put(PdfName.F, new PdfString(filename));
        action.put(PdfName.S, PdfName.GOTOR);
        if (isName) {
            action.put(PdfName.D, new PdfName(dest));
        } else {
            action.put(PdfName.D, new PdfString(dest, null));
        }
        if (newWindow) {
            action.put(PdfName.NEWWINDOW, PdfBoolean.PDFTRUE);
        }
        return action;
    }

    public static PdfAction gotoEmbedded(String filename, PdfTargetDictionary target, String dest, boolean isName, boolean newWindow) {
        if (isName) {
            return PdfAction.gotoEmbedded(filename, target, new PdfName(dest), newWindow);
        }
        return PdfAction.gotoEmbedded(filename, target, new PdfString(dest, null), newWindow);
    }

    public static PdfAction gotoEmbedded(String filename, PdfTargetDictionary target, PdfObject dest, boolean newWindow) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.GOTOE);
        action.put(PdfName.T, target);
        action.put(PdfName.D, dest);
        action.put(PdfName.NEWWINDOW, new PdfBoolean(newWindow));
        if (filename != null) {
            action.put(PdfName.F, new PdfString(filename));
        }
        return action;
    }

    public static PdfAction setOCGstate(List<Object> state, boolean preserveRB) {
        PdfAction action = new PdfAction();
        action.put(PdfName.S, PdfName.SETOCGSTATE);
        PdfArray a = new PdfArray();
        for (Object o : state) {
            if (o == null) continue;
            if (o instanceof PdfIndirectReference) {
                a.add((PdfIndirectReference)o);
                continue;
            }
            if (o instanceof PdfLayer) {
                a.add(((PdfLayer)o).getRef());
                continue;
            }
            if (o instanceof PdfName) {
                a.add((PdfName)o);
                continue;
            }
            if (o instanceof String) {
                PdfName name = null;
                String s = (String)o;
                if (s.equalsIgnoreCase("on")) {
                    name = PdfName.ON;
                } else if (s.equalsIgnoreCase("off")) {
                    name = PdfName.OFF;
                } else if (s.equalsIgnoreCase("toggle")) {
                    name = PdfName.TOGGLE;
                } else {
                    throw new IllegalArgumentException(MessageLocalization.getComposedMessage("a.string.1.was.passed.in.state.only.on.off.and.toggle.are.allowed", s));
                }
                a.add(name);
                continue;
            }
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.type.was.passed.in.state.1", o.getClass().getName()));
        }
        action.put(PdfName.STATE, a);
        if (!preserveRB) {
            action.put(PdfName.PRESERVERB, PdfBoolean.PDFFALSE);
        }
        return action;
    }
}

