/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.abdera.writer;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.Locale;
import javax.activation.DataHandler;
import javax.xml.namespace.QName;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.i18n.rfc4646.Lang;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Text;
import org.apache.abdera.util.NamedItem;

public interface StreamWriter
extends NamedItem,
Appendable,
Closeable {
    public StreamWriter flush();

    public StreamWriter setAutoclose(boolean var1);

    public StreamWriter setAutoflush(boolean var1);

    public StreamWriter setWriter(Writer var1);

    public StreamWriter setOutputStream(OutputStream var1);

    public StreamWriter setOutputStream(OutputStream var1, String var2);

    public StreamWriter setChannel(WritableByteChannel var1);

    public StreamWriter setChannel(WritableByteChannel var1, String var2);

    public StreamWriter startDocument(String var1, String var2);

    public StreamWriter startDocument(String var1);

    public StreamWriter startDocument();

    public StreamWriter endDocument();

    public StreamWriter startFeed();

    public StreamWriter endFeed();

    public StreamWriter startEntry();

    public StreamWriter endEntry();

    public StreamWriter writeId(String var1);

    public StreamWriter writeIcon(String var1);

    public StreamWriter writeLogo(String var1);

    public StreamWriter writeIRIElement(QName var1, String var2);

    public StreamWriter writeIRIElement(String var1, String var2, String var3, String var4);

    public StreamWriter writeIRIElement(String var1, String var2, String var3);

    public StreamWriter writeIRIElement(String var1, String var2);

    public StreamWriter writeId(IRI var1);

    public StreamWriter writeIcon(IRI var1);

    public StreamWriter writeLogo(IRI var1);

    public StreamWriter writeIRIElement(QName var1, IRI var2);

    public StreamWriter writeIRIElement(String var1, String var2, String var3, IRI var4);

    public StreamWriter writeIRIElement(String var1, String var2, IRI var3);

    public StreamWriter writeIRIElement(String var1, IRI var2);

    public StreamWriter writeId();

    public StreamWriter writeUpdated(Date var1);

    public StreamWriter writePublished(Date var1);

    public StreamWriter writeEdited(Date var1);

    public StreamWriter writeDate(QName var1, Date var2);

    public StreamWriter writeDate(String var1, String var2, String var3, Date var4);

    public StreamWriter writeDate(String var1, String var2, Date var3);

    public StreamWriter writeDate(String var1, Date var2);

    public StreamWriter writeUpdated(String var1);

    public StreamWriter writePublished(String var1);

    public StreamWriter writeEdited(String var1);

    public StreamWriter writeDate(QName var1, String var2);

    public StreamWriter writeDate(String var1, String var2, String var3, String var4);

    public StreamWriter writeDate(String var1, String var2, String var3);

    public StreamWriter writeDate(String var1, String var2);

    public StreamWriter endPerson();

    public StreamWriter endLink();

    public StreamWriter writeLink(String var1);

    public StreamWriter writeLink(String var1, String var2);

    public StreamWriter writeLink(String var1, String var2, String var3);

    public StreamWriter writeLink(String var1, String var2, String var3, String var4, String var5, long var6);

    public StreamWriter startLink(String var1);

    public StreamWriter startLink(String var1, String var2);

    public StreamWriter startLink(String var1, String var2, String var3);

    public StreamWriter startLink(String var1, String var2, String var3, String var4, String var5, long var6);

    public StreamWriter endCategory();

    public StreamWriter writeCategory(String var1);

    public StreamWriter writeCategory(String var1, String var2);

    public StreamWriter writeCategory(String var1, String var2, String var3);

    public StreamWriter startCategory(String var1);

    public StreamWriter startCategory(String var1, String var2);

    public StreamWriter startCategory(String var1, String var2, String var3);

    public StreamWriter startSource();

    public StreamWriter endSource();

    public StreamWriter writeText(QName var1, Text.Type var2, String var3);

    public StreamWriter writeText(String var1, Text.Type var2, String var3);

    public StreamWriter writeText(String var1, String var2, Text.Type var3, String var4);

    public StreamWriter writeText(String var1, String var2, String var3, Text.Type var4, String var5);

    public StreamWriter startText(QName var1, Text.Type var2);

    public StreamWriter startText(String var1, Text.Type var2);

    public StreamWriter startText(String var1, String var2, Text.Type var3);

    public StreamWriter startText(String var1, String var2, String var3, Text.Type var4);

    public StreamWriter endContent();

    public StreamWriter writeContent(Content.Type var1, String var2);

    public StreamWriter writeContent(Content.Type var1, InputStream var2) throws IOException;

    public StreamWriter writeContent(Content.Type var1, DataHandler var2) throws IOException;

    public StreamWriter writeContent(String var1, String var2);

    public StreamWriter startContent(Content.Type var1);

    public StreamWriter startContent(String var1);

    public StreamWriter startContent(Content.Type var1, String var2);

    public StreamWriter startContent(String var1, String var2);

    public StreamWriter startElement(QName var1);

    public StreamWriter startElement(String var1);

    public StreamWriter startElement(String var1, String var2);

    public StreamWriter startElement(String var1, String var2, String var3);

    public StreamWriter writeElementText(String var1, Object ... var2);

    public StreamWriter writeElementText(String var1);

    public StreamWriter writeElementText(DataHandler var1) throws IOException;

    public StreamWriter writeElementText(InputStream var1) throws IOException;

    public StreamWriter writeElementText(Date var1);

    public StreamWriter writeElementText(int var1);

    public StreamWriter writeElementText(long var1);

    public StreamWriter writeElementText(double var1);

    public StreamWriter endElement();

    public StreamWriter writeTitle(String var1);

    public StreamWriter writeTitle(Text.Type var1, String var2);

    public StreamWriter writeSubtitle(String var1);

    public StreamWriter writeSubtitle(Text.Type var1, String var2);

    public StreamWriter writeSummary(String var1);

    public StreamWriter writeSummary(Text.Type var1, String var2);

    public StreamWriter writeRights(String var1);

    public StreamWriter writeRights(Text.Type var1, String var2);

    public StreamWriter writePerson(QName var1, String var2, String var3, String var4);

    public StreamWriter writePerson(String var1, String var2, String var3, String var4);

    public StreamWriter writePerson(String var1, String var2, String var3, String var4, String var5);

    public StreamWriter writePerson(String var1, String var2, String var3, String var4, String var5, String var6);

    public StreamWriter startPerson(QName var1);

    public StreamWriter startPerson(String var1);

    public StreamWriter startPerson(String var1, String var2);

    public StreamWriter startPerson(String var1, String var2, String var3);

    public StreamWriter writePersonName(String var1);

    public StreamWriter writePersonEmail(String var1);

    public StreamWriter writePersonUri(String var1);

    public StreamWriter writeAuthor(String var1, String var2, String var3);

    public StreamWriter writeAuthor(String var1);

    public StreamWriter startAuthor();

    public StreamWriter endAuthor();

    public StreamWriter writeContributor(String var1, String var2, String var3);

    public StreamWriter writeContributor(String var1);

    public StreamWriter startContributor();

    public StreamWriter endContributor();

    public StreamWriter writeGenerator(String var1, String var2, String var3);

    public StreamWriter startGenerator(String var1, String var2);

    public StreamWriter endGenerator();

    public StreamWriter writeComment(String var1);

    public StreamWriter writePI(String var1);

    public StreamWriter writePI(String var1, String var2);

    public StreamWriter startService();

    public StreamWriter endService();

    public StreamWriter startWorkspace();

    public StreamWriter endWorkspace();

    public StreamWriter startCollection(String var1);

    public StreamWriter endCollection();

    public StreamWriter writeAccepts(String ... var1);

    public StreamWriter writeAcceptsEntry();

    public StreamWriter writeAcceptsNothing();

    public StreamWriter startCategories();

    public StreamWriter startCategories(boolean var1);

    public StreamWriter startCategories(boolean var1, String var2);

    public StreamWriter endCategories();

    public StreamWriter startControl();

    public StreamWriter endControl();

    public StreamWriter writeDraft(boolean var1);

    public StreamWriter writeAttribute(QName var1, String var2);

    public StreamWriter writeAttribute(String var1, String var2);

    public StreamWriter writeAttribute(String var1, String var2, String var3);

    public StreamWriter writeAttribute(String var1, String var2, String var3, String var4);

    public StreamWriter writeAttribute(QName var1, Date var2);

    public StreamWriter writeAttribute(String var1, Date var2);

    public StreamWriter writeAttribute(String var1, String var2, Date var3);

    public StreamWriter writeAttribute(String var1, String var2, String var3, Date var4);

    public StreamWriter writeAttribute(QName var1, int var2);

    public StreamWriter writeAttribute(String var1, int var2);

    public StreamWriter writeAttribute(String var1, String var2, int var3);

    public StreamWriter writeAttribute(String var1, String var2, String var3, int var4);

    public StreamWriter writeAttribute(QName var1, long var2);

    public StreamWriter writeAttribute(String var1, long var2);

    public StreamWriter writeAttribute(String var1, String var2, long var3);

    public StreamWriter writeAttribute(String var1, String var2, String var3, long var4);

    public StreamWriter writeAttribute(QName var1, double var2);

    public StreamWriter writeAttribute(String var1, double var2);

    public StreamWriter writeAttribute(String var1, String var2, double var3);

    public StreamWriter writeAttribute(String var1, String var2, String var3, double var4);

    public StreamWriter indent();

    public StreamWriter setAutoIndent(boolean var1);

    public StreamWriter writeBase(String var1);

    public StreamWriter writeBase(IRI var1);

    public StreamWriter writeLanguage(String var1);

    public StreamWriter writeLanguage(Lang var1);

    public StreamWriter writeLanguage(Locale var1);

    public StreamWriter setPrefix(String var1, String var2);

    public StreamWriter writeNamespace(String var1, String var2);
}

