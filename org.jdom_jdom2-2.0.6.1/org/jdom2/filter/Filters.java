/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.Content;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;
import org.jdom2.filter.AttributeFilter;
import org.jdom2.filter.ClassFilter;
import org.jdom2.filter.ElementFilter;
import org.jdom2.filter.Filter;
import org.jdom2.filter.PassThroughFilter;
import org.jdom2.filter.TextOnlyFilter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Filters {
    private static final Filter<Content> fcontent = new ClassFilter<Content>(Content.class);
    private static final Filter<Attribute> fattribute = new AttributeFilter();
    private static final Filter<Comment> fcomment = new ClassFilter<Comment>(Comment.class);
    private static final Filter<CDATA> fcdata = new ClassFilter<CDATA>(CDATA.class);
    private static final Filter<DocType> fdoctype = new ClassFilter<DocType>(DocType.class);
    private static final Filter<EntityRef> fentityref = new ClassFilter<EntityRef>(EntityRef.class);
    private static final Filter<ProcessingInstruction> fpi = new ClassFilter<ProcessingInstruction>(ProcessingInstruction.class);
    private static final Filter<Text> ftext = new ClassFilter<Text>(Text.class);
    private static final Filter<Text> ftextonly = new TextOnlyFilter();
    private static final Filter<Element> felement = new ClassFilter<Element>(Element.class);
    private static final Filter<Document> fdocument = new ClassFilter<Document>(Document.class);
    private static final Filter<Double> fdouble = new ClassFilter<Double>(Double.class);
    private static final Filter<Boolean> fboolean = new ClassFilter<Boolean>(Boolean.class);
    private static final Filter<String> fstring = new ClassFilter<String>(String.class);
    private static final Filter<Object> fpassthrough = new PassThroughFilter();

    private Filters() {
    }

    public static final Filter<Content> content() {
        return fcontent;
    }

    public static final Filter<Attribute> attribute() {
        return fattribute;
    }

    public static final Filter<Attribute> attribute(String name) {
        return new AttributeFilter(name);
    }

    public static final Filter<Attribute> attribute(String name, Namespace ns) {
        return new AttributeFilter(name, ns);
    }

    public static final Filter<Attribute> attribute(Namespace ns) {
        return new AttributeFilter(ns);
    }

    public static final Filter<Comment> comment() {
        return fcomment;
    }

    public static final Filter<CDATA> cdata() {
        return fcdata;
    }

    public static final Filter<DocType> doctype() {
        return fdoctype;
    }

    public static final Filter<EntityRef> entityref() {
        return fentityref;
    }

    public static final Filter<Element> element() {
        return felement;
    }

    public static final Filter<Document> document() {
        return fdocument;
    }

    public static final Filter<Element> element(String name) {
        return new ElementFilter(name, Namespace.NO_NAMESPACE);
    }

    public static final Filter<Element> element(String name, Namespace ns) {
        return new ElementFilter(name, ns);
    }

    public static final Filter<Element> element(Namespace ns) {
        return new ElementFilter(null, ns);
    }

    public static final Filter<ProcessingInstruction> processinginstruction() {
        return fpi;
    }

    public static final Filter<Text> text() {
        return ftext;
    }

    public static final Filter<Text> textOnly() {
        return ftextonly;
    }

    public static final Filter<Boolean> fboolean() {
        return fboolean;
    }

    public static final Filter<String> fstring() {
        return fstring;
    }

    public static final Filter<Double> fdouble() {
        return fdouble;
    }

    public static final <F> Filter<F> fclass(Class<F> clazz) {
        return new ClassFilter<F>(clazz);
    }

    public static final Filter<Object> fpassthrough() {
        return fpassthrough;
    }
}

