/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.DateUtils
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.protocols.xml.internal.marshall;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Stack;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
final class XmlWriter {
    private static final String PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String[] UNESCAPE_SEARCHES = new String[]{"&quot;", "&apos;", "&lt;", "&gt;", "&#x0D;", "&#x0A;", "&amp;"};
    private static final String[] UNESCAPE_REPLACEMENTS = new String[]{"\"", "'", "<", ">", "\r", "\n", "&"};
    private static final String[] ESCAPE_SEARCHES = new String[]{"&", "\"", "'", "<", ">", "\r", "\n"};
    private static final String[] ESCAPE_REPLACEMENTS = new String[]{"&amp;", "&quot;", "&apos;", "&lt;", "&gt;", "&#x0D;", "&#x0A;"};
    private final Writer writer;
    private final String xmlns;
    private Stack<String> elementStack = new Stack();
    private boolean rootElement = true;
    private boolean writtenProlog = false;

    XmlWriter(Writer w, String xmlns) {
        this.writer = w;
        this.xmlns = xmlns;
    }

    XmlWriter startElement(String element) {
        if (!this.writtenProlog) {
            this.writtenProlog = true;
            this.append(PROLOG);
        }
        this.append("<" + element);
        if (this.rootElement && this.xmlns != null) {
            this.append(" xmlns=\"" + this.xmlns + "\"");
            this.rootElement = false;
        }
        this.append(">");
        this.elementStack.push(element);
        return this;
    }

    XmlWriter startElement(String element, Map<String, String> attributes) {
        this.append("<" + element);
        for (Map.Entry<String, String> attribute : attributes.entrySet()) {
            this.append(" " + attribute.getKey() + "=\"" + attribute.getValue() + "\"");
        }
        this.append(">");
        this.elementStack.push(element);
        return this;
    }

    XmlWriter endElement() {
        String lastElement = this.elementStack.pop();
        this.append("</" + lastElement + ">");
        return this;
    }

    public XmlWriter value(String s) {
        this.append(this.escapeXmlEntities(s));
        return this;
    }

    public XmlWriter value(ByteBuffer b) {
        this.append(this.escapeXmlEntities(BinaryUtils.toBase64((byte[])BinaryUtils.copyBytesFrom((ByteBuffer)b))));
        return this;
    }

    public XmlWriter value(Date date) {
        this.append(this.escapeXmlEntities(DateUtils.formatIso8601Date((Instant)date.toInstant())));
        return this;
    }

    public XmlWriter value(Object obj) {
        this.append(this.escapeXmlEntities(obj.toString()));
        return this;
    }

    private void append(String s) {
        try {
            this.writer.append(s);
        }
        catch (IOException e) {
            throw SdkClientException.builder().message("Unable to write XML document").cause((Throwable)e).build();
        }
    }

    private String escapeXmlEntities(String s) {
        if (s.contains("&")) {
            s = StringUtils.replaceEach((String)s, (String[])UNESCAPE_SEARCHES, (String[])UNESCAPE_REPLACEMENTS);
        }
        return StringUtils.replaceEach((String)s, (String[])ESCAPE_SEARCHES, (String[])ESCAPE_REPLACEMENTS);
    }
}

