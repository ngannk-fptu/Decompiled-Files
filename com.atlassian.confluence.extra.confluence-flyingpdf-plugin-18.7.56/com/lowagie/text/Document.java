/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.ChapterAutoNumber;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Header;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.LargeElement;
import com.lowagie.text.Meta;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.VersionBean;
import com.lowagie.text.error_messages.MessageLocalization;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Document
implements AutoCloseable,
DocListener {
    private static final String OPENPDF = "OpenPDF";
    private static final String RELEASE = VersionBean.VERSION.getImplementationVersion();
    private static final String OPENPDF_VERSION = "OpenPDF " + RELEASE;
    public static boolean compress = true;
    public static boolean plainRandomAccess = false;
    public static float wmfFontCorrection = 0.86f;
    private List<DocListener> listeners = new ArrayList<DocListener>();
    protected boolean open;
    protected boolean close;
    protected Rectangle pageSize;
    protected float marginLeft = 0.0f;
    protected float marginRight = 0.0f;
    protected float marginTop = 0.0f;
    protected float marginBottom = 0.0f;
    protected boolean marginMirroring = false;
    protected boolean marginMirroringTopBottom = false;
    protected String javaScript_onLoad = null;
    protected String javaScript_onUnLoad = null;
    protected String htmlStyleClass = null;
    protected int pageN = 0;
    protected HeaderFooter header = null;
    protected HeaderFooter footer = null;
    protected int chapternumber = 0;

    public Document() {
        this(PageSize.A4);
    }

    public Document(Rectangle pageSize) {
        this(pageSize, 36.0f, 36.0f, 36.0f, 36.0f);
    }

    public Document(Rectangle pageSize, float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.pageSize = pageSize;
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
    }

    public void addDocListener(DocListener listener) {
        this.listeners.add(listener);
    }

    public void removeDocListener(DocListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public boolean add(Element element) throws DocumentException {
        LargeElement e;
        if (this.close) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.has.been.closed.you.can.t.add.any.elements"));
        }
        if (!this.open && element.isContent()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("the.document.is.not.open.yet.you.can.only.add.meta.information"));
        }
        boolean success = false;
        if (element instanceof ChapterAutoNumber) {
            this.chapternumber = ((ChapterAutoNumber)element).setAutomaticNumber(this.chapternumber);
        }
        for (DocListener listener : this.listeners) {
            success |= listener.add(element);
        }
        if (element instanceof LargeElement && !(e = (LargeElement)element).isComplete()) {
            e.flushContent();
        }
        return success;
    }

    @Override
    public void open() {
        if (!this.close) {
            this.open = true;
        }
        for (DocListener listener : this.listeners) {
            listener.setPageSize(this.pageSize);
            listener.setMargins(this.marginLeft, this.marginRight, this.marginTop, this.marginBottom);
            listener.open();
        }
    }

    @Override
    public boolean setPageSize(Rectangle pageSize) {
        this.pageSize = pageSize;
        for (DocListener listener : this.listeners) {
            listener.setPageSize(pageSize);
        }
        return true;
    }

    @Override
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        for (DocListener listener : this.listeners) {
            listener.setMargins(marginLeft, marginRight, marginTop, marginBottom);
        }
        return true;
    }

    @Override
    public boolean newPage() {
        if (!this.open || this.close) {
            return false;
        }
        for (DocListener listener : this.listeners) {
            listener.newPage();
        }
        return true;
    }

    @Override
    public void setHeader(HeaderFooter header) {
        this.header = header;
        for (DocListener listener : this.listeners) {
            listener.setHeader(header);
        }
    }

    @Override
    public void resetHeader() {
        this.header = null;
        for (DocListener listener : this.listeners) {
            listener.resetHeader();
        }
    }

    @Override
    public void setFooter(HeaderFooter footer) {
        this.footer = footer;
        for (DocListener listener : this.listeners) {
            listener.setFooter(footer);
        }
    }

    @Override
    public void resetFooter() {
        this.footer = null;
        for (DocListener listener : this.listeners) {
            listener.resetFooter();
        }
    }

    @Override
    public void resetPageCount() {
        this.pageN = 0;
        for (DocListener listener : this.listeners) {
            listener.resetPageCount();
        }
    }

    @Override
    public void setPageCount(int pageN) {
        this.pageN = pageN;
        for (DocListener listener : this.listeners) {
            listener.setPageCount(pageN);
        }
    }

    public int getPageNumber() {
        return this.pageN;
    }

    @Override
    public void close() {
        if (!this.close) {
            this.open = false;
            this.close = true;
        }
        for (DocListener listener : this.listeners) {
            listener.close();
        }
    }

    public boolean addHeader(String name, String content) {
        try {
            return this.add(new Header(name, content));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addTitle(String title) {
        try {
            return this.add(new Meta(1, title));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addSubject(String subject) {
        try {
            return this.add(new Meta(2, subject));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addKeywords(String keywords) {
        try {
            return this.add(new Meta(3, keywords));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addAuthor(String author) {
        try {
            return this.add(new Meta(4, author));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addCreator(String creator) {
        try {
            return this.add(new Meta(7, creator));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public boolean addProducer() {
        return this.addProducer(Document.getVersion());
    }

    public boolean addProducer(String producer) {
        return this.add(new Meta(5, producer));
    }

    public boolean addCreationDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
            return this.add(new Meta(6, sdf.format(new Date())));
        }
        catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    public float leftMargin() {
        return this.marginLeft;
    }

    public float rightMargin() {
        return this.marginRight;
    }

    public float topMargin() {
        return this.marginTop;
    }

    public float bottomMargin() {
        return this.marginBottom;
    }

    public float left() {
        return this.pageSize.getLeft(this.marginLeft);
    }

    public float right() {
        return this.pageSize.getRight(this.marginRight);
    }

    public float top() {
        return this.pageSize.getTop(this.marginTop);
    }

    public float bottom() {
        return this.pageSize.getBottom(this.marginBottom);
    }

    public float left(float margin) {
        return this.pageSize.getLeft(this.marginLeft + margin);
    }

    public float right(float margin) {
        return this.pageSize.getRight(this.marginRight + margin);
    }

    public float top(float margin) {
        return this.pageSize.getTop(this.marginTop + margin);
    }

    public float bottom(float margin) {
        return this.pageSize.getBottom(this.marginBottom + margin);
    }

    public Rectangle getPageSize() {
        return this.pageSize;
    }

    public boolean isOpen() {
        return this.open;
    }

    public static String getProduct() {
        return OPENPDF;
    }

    public static String getRelease() {
        return RELEASE;
    }

    public static String getVersion() {
        return OPENPDF_VERSION;
    }

    public void setJavaScript_onLoad(String code) {
        this.javaScript_onLoad = code;
    }

    public String getJavaScript_onLoad() {
        return this.javaScript_onLoad;
    }

    public void setJavaScript_onUnLoad(String code) {
        this.javaScript_onUnLoad = code;
    }

    public String getJavaScript_onUnLoad() {
        return this.javaScript_onUnLoad;
    }

    public void setHtmlStyleClass(String htmlStyleClass) {
        this.htmlStyleClass = htmlStyleClass;
    }

    public String getHtmlStyleClass() {
        return this.htmlStyleClass;
    }

    @Override
    public boolean setMarginMirroring(boolean marginMirroring) {
        this.marginMirroring = marginMirroring;
        for (DocListener listener : this.listeners) {
            listener.setMarginMirroring(marginMirroring);
        }
        return true;
    }

    @Override
    public boolean setMarginMirroringTopBottom(boolean marginMirroringTopBottom) {
        this.marginMirroringTopBottom = marginMirroringTopBottom;
        for (DocListener listener : this.listeners) {
            listener.setMarginMirroringTopBottom(marginMirroringTopBottom);
        }
        return true;
    }

    public boolean isMarginMirroring() {
        return this.marginMirroring;
    }
}

