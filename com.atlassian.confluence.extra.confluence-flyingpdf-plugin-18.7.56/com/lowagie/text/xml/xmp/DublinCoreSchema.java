/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.xml.xmp;

import com.lowagie.text.xml.xmp.XmpArray;
import com.lowagie.text.xml.xmp.XmpSchema;
import java.util.Arrays;

public class DublinCoreSchema
extends XmpSchema {
    private static final long serialVersionUID = -4551741356374797330L;
    public static final String DEFAULT_XPATH_ID = "dc";
    public static final String DEFAULT_XPATH_URI = "http://purl.org/dc/elements/1.1/";
    public static final String CONTRIBUTOR = "dc:contributor";
    public static final String COVERAGE = "dc:coverage";
    public static final String CREATOR = "dc:creator";
    public static final String DATE = "dc:date";
    public static final String DESCRIPTION = "dc:description";
    public static final String FORMAT = "dc:format";
    public static final String IDENTIFIER = "dc:identifier";
    public static final String LANGUAGE = "dc:language";
    public static final String PUBLISHER = "dc:publisher";
    public static final String RELATION = "dc:relation";
    public static final String RIGHTS = "dc:rights";
    public static final String SOURCE = "dc:source";
    public static final String SUBJECT = "dc:subject";
    public static final String TITLE = "dc:title";
    public static final String TYPE = "dc:type";

    public DublinCoreSchema() {
        super("xmlns:dc=\"http://purl.org/dc/elements/1.1/\"");
        this.setProperty(FORMAT, "application/pdf");
    }

    public void addTitle(String title) {
        XmpArray array = new XmpArray("rdf:Alt");
        array.add(title);
        this.setProperty(TITLE, array);
    }

    public void addDescription(String desc) {
        XmpArray array = new XmpArray("rdf:Alt");
        array.add(desc);
        this.setProperty(DESCRIPTION, array);
    }

    public void addSubject(String subject) {
        XmpArray array = new XmpArray("rdf:Bag");
        array.add(subject);
        this.setProperty(SUBJECT, array);
    }

    public void addSubject(String[] subject) {
        XmpArray array = new XmpArray("rdf:Bag");
        array.addAll(Arrays.asList(subject));
        this.setProperty(SUBJECT, array);
    }

    public void addAuthor(String author) {
        XmpArray array = new XmpArray("rdf:Seq");
        array.add(author);
        this.setProperty(CREATOR, array);
    }

    public void addAuthor(String[] author) {
        XmpArray array = new XmpArray("rdf:Seq");
        array.addAll(Arrays.asList(author));
        this.setProperty(CREATOR, array);
    }

    public void addPublisher(String publisher) {
        XmpArray array = new XmpArray("rdf:Seq");
        array.add(publisher);
        this.setProperty(PUBLISHER, array);
    }

    public void addPublisher(String[] publisher) {
        XmpArray array = new XmpArray("rdf:Seq");
        array.addAll(Arrays.asList(publisher));
        this.setProperty(PUBLISHER, array);
    }
}

