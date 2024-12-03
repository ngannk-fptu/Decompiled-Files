/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.text.StringEscapeUtils
 */
package org.apache.commons.configuration2.plist;

import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration2.BaseHierarchicalConfiguration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.MapConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorAware;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.apache.commons.configuration2.tree.InMemoryNodeModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLPropertyListConfiguration
extends BaseHierarchicalConfiguration
implements FileBasedConfiguration,
FileLocatorAware {
    private static final int INDENT_SIZE = 4;
    private static final String DATA_ENCODING = "UTF-8";
    private FileLocator locator;

    public XMLPropertyListConfiguration() {
    }

    public XMLPropertyListConfiguration(HierarchicalConfiguration<ImmutableNode> configuration) {
        super(configuration);
    }

    XMLPropertyListConfiguration(ImmutableNode root) {
        super(new InMemoryNodeModel(root));
    }

    private void setPropertyDirect(String key, Object value) {
        this.setDetailEvents(false);
        try {
            this.clearProperty(key);
            this.addPropertyDirect(key, value);
        }
        finally {
            this.setDetailEvents(true);
        }
    }

    @Override
    protected void setPropertyInternal(String key, Object value) {
        if (value instanceof byte[] || value instanceof List) {
            this.setPropertyDirect(key, value);
        } else if (value instanceof Object[]) {
            this.setPropertyDirect(key, Arrays.asList((Object[])value));
        } else {
            super.setPropertyInternal(key, value);
        }
    }

    @Override
    protected void addPropertyInternal(String key, Object value) {
        if (value instanceof byte[] || value instanceof List) {
            this.addPropertyDirect(key, value);
        } else if (value instanceof Object[]) {
            this.addPropertyDirect(key, Arrays.asList((Object[])value));
        } else {
            super.addPropertyInternal(key, value);
        }
    }

    @Override
    public void initFileLocator(FileLocator locator) {
        this.locator = locator;
    }

    @Override
    public void read(Reader in) throws ConfigurationException {
        EntityResolver resolver = (publicId, systemId) -> new InputSource(this.getClass().getClassLoader().getResourceAsStream("PropertyList-1.0.dtd"));
        XMLPropertyListHandler handler = new XMLPropertyListHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(true);
            SAXParser parser = factory.newSAXParser();
            parser.getXMLReader().setEntityResolver(resolver);
            parser.getXMLReader().setContentHandler(handler);
            parser.getXMLReader().parse(new InputSource(in));
            this.getNodeModel().mergeRoot(handler.getResultBuilder().createNode(), null, null, null, this);
        }
        catch (Exception e) {
            throw new ConfigurationException("Unable to parse the configuration file", e);
        }
    }

    @Override
    public void write(Writer out) throws ConfigurationException {
        if (this.locator == null) {
            throw new ConfigurationException("Save operation not properly initialized! Do not call write(Writer) directly, but use a FileHandler to save a configuration.");
        }
        PrintWriter writer = new PrintWriter(out);
        if (this.locator.getEncoding() != null) {
            writer.println("<?xml version=\"1.0\" encoding=\"" + this.locator.getEncoding() + "\"?>");
        } else {
            writer.println("<?xml version=\"1.0\"?>");
        }
        writer.println("<!DOCTYPE plist SYSTEM \"file://localhost/System/Library/DTDs/PropertyList.dtd\">");
        writer.println("<plist version=\"1.0\">");
        this.printNode(writer, 1, this.getNodeModel().getNodeHandler().getRootNode());
        writer.println("</plist>");
        writer.flush();
    }

    private void printNode(PrintWriter out, int indentLevel, ImmutableNode node) {
        List<ImmutableNode> children;
        String padding = StringUtils.repeat((String)" ", (int)(indentLevel * 4));
        if (node.getNodeName() != null) {
            out.println(padding + "<key>" + StringEscapeUtils.escapeXml10((String)node.getNodeName()) + "</key>");
        }
        if (!(children = node.getChildren()).isEmpty()) {
            out.println(padding + "<dict>");
            Iterator<ImmutableNode> it = children.iterator();
            while (it.hasNext()) {
                ImmutableNode child = it.next();
                this.printNode(out, indentLevel + 1, child);
                if (!it.hasNext()) continue;
                out.println();
            }
            out.println(padding + "</dict>");
        } else if (node.getValue() == null) {
            out.println(padding + "<dict/>");
        } else {
            Object value = node.getValue();
            this.printValue(out, indentLevel, value);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void printValue(PrintWriter out, int indentLevel, Object value) {
        String padding = StringUtils.repeat((String)" ", (int)(indentLevel * 4));
        if (value instanceof Date) {
            DateFormat dateFormat = PListNodeBuilder.FORMAT;
            synchronized (dateFormat) {
                out.println(padding + "<date>" + PListNodeBuilder.FORMAT.format((Date)value) + "</date>");
            }
        }
        if (value instanceof Calendar) {
            this.printValue(out, indentLevel, ((Calendar)value).getTime());
        } else if (value instanceof Number) {
            if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
                out.println(padding + "<real>" + value.toString() + "</real>");
            } else {
                out.println(padding + "<integer>" + value.toString() + "</integer>");
            }
        } else if (value instanceof Boolean) {
            if (((Boolean)value).booleanValue()) {
                out.println(padding + "<true/>");
            } else {
                out.println(padding + "<false/>");
            }
        } else if (value instanceof List) {
            out.println(padding + "<array>");
            ((List)value).forEach(o -> this.printValue(out, indentLevel + 1, o));
            out.println(padding + "</array>");
        } else if (value instanceof HierarchicalConfiguration) {
            HierarchicalConfiguration config = (HierarchicalConfiguration)value;
            this.printNode(out, indentLevel, (ImmutableNode)config.getNodeModel().getNodeHandler().getRootNode());
        } else if (value instanceof ImmutableConfiguration) {
            out.println(padding + "<dict>");
            ImmutableConfiguration config = (ImmutableConfiguration)value;
            Iterator<String> it = config.getKeys();
            while (it.hasNext()) {
                String key = it.next();
                ImmutableNode node = new ImmutableNode.Builder().name(key).value(config.getProperty(key)).create();
                this.printNode(out, indentLevel + 1, node);
                if (!it.hasNext()) continue;
                out.println();
            }
            out.println(padding + "</dict>");
        } else if (value instanceof Map) {
            Map<String, Object> map = XMLPropertyListConfiguration.transformMap((Map)value);
            this.printValue(out, indentLevel, new MapConfiguration(map));
        } else if (value instanceof byte[]) {
            String base64;
            try {
                base64 = new String(Base64.encodeBase64((byte[])((byte[])value)), DATA_ENCODING);
            }
            catch (UnsupportedEncodingException e) {
                throw new AssertionError((Object)e);
            }
            out.println(padding + "<data>" + StringEscapeUtils.escapeXml10((String)base64) + "</data>");
        } else if (value != null) {
            out.println(padding + "<string>" + StringEscapeUtils.escapeXml10((String)String.valueOf(value)) + "</string>");
        } else {
            out.println(padding + "<string/>");
        }
    }

    private static Map<String, Object> transformMap(Map<?, ?> src) {
        HashMap<String, Object> dest = new HashMap<String, Object>();
        for (Map.Entry<?, ?> e : src.entrySet()) {
            if (!(e.getKey() instanceof String)) continue;
            dest.put((String)e.getKey(), e.getValue());
        }
        return dest;
    }

    private static class ArrayNodeBuilder
    extends PListNodeBuilder {
        private final List<Object> list = new ArrayList<Object>();

        private ArrayNodeBuilder() {
        }

        @Override
        public void addValue(Object value) {
            this.list.add(value);
        }

        @Override
        protected Object getNodeValue() {
            return this.list;
        }
    }

    private static class PListNodeBuilder {
        private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        private static final DateFormat GNUSTEP_FORMAT;
        private final Collection<PListNodeBuilder> childBuilders = new LinkedList<PListNodeBuilder>();
        private String name;
        private Object value;

        private PListNodeBuilder() {
        }

        public void addValue(Object v) {
            if (this.value == null) {
                this.value = v;
            } else if (this.value instanceof Collection) {
                Collection collection = (Collection)this.value;
                collection.add(v);
            } else {
                ArrayList<Object> list = new ArrayList<Object>();
                list.add(this.value);
                list.add(v);
                this.value = list;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void addDateValue(String value) {
            block9: {
                try {
                    if (value.indexOf(32) != -1) {
                        DateFormat dateFormat = GNUSTEP_FORMAT;
                        synchronized (dateFormat) {
                            this.addValue(GNUSTEP_FORMAT.parse(value));
                            break block9;
                        }
                    }
                    DateFormat dateFormat = FORMAT;
                    synchronized (dateFormat) {
                        this.addValue(FORMAT.parse(value));
                    }
                }
                catch (ParseException e) {
                    throw new IllegalArgumentException(String.format("'%s' cannot be parsed to a date!", value), e);
                }
            }
        }

        public void addDataValue(String value) {
            try {
                this.addValue(Base64.decodeBase64((byte[])value.getBytes(XMLPropertyListConfiguration.DATA_ENCODING)));
            }
            catch (UnsupportedEncodingException e) {
                throw new AssertionError((Object)e);
            }
        }

        public void addIntegerValue(String value) {
            this.addValue(new BigInteger(value));
        }

        public void addRealValue(String value) {
            this.addValue(new BigDecimal(value));
        }

        public void addTrueValue() {
            this.addValue(Boolean.TRUE);
        }

        public void addFalseValue() {
            this.addValue(Boolean.FALSE);
        }

        public void addList(ArrayNodeBuilder node) {
            this.addValue(node.getNodeValue());
        }

        public void setName(String nodeName) {
            this.name = nodeName;
        }

        public void addChild(PListNodeBuilder child) {
            this.childBuilders.add(child);
        }

        public ImmutableNode createNode() {
            ImmutableNode.Builder nodeBuilder = new ImmutableNode.Builder(this.childBuilders.size());
            this.childBuilders.forEach(child -> nodeBuilder.addChild(child.createNode()));
            return nodeBuilder.name(this.name).value(this.getNodeValue()).create();
        }

        protected Object getNodeValue() {
            return this.value;
        }

        static {
            FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
            GNUSTEP_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
        }
    }

    private class XMLPropertyListHandler
    extends DefaultHandler {
        private final StringBuilder buffer = new StringBuilder();
        private final List<PListNodeBuilder> stack = new ArrayList<PListNodeBuilder>();
        private final PListNodeBuilder resultBuilder = new PListNodeBuilder();

        public XMLPropertyListHandler() {
            this.push(this.resultBuilder);
        }

        public PListNodeBuilder getResultBuilder() {
            return this.resultBuilder;
        }

        private PListNodeBuilder peek() {
            if (!this.stack.isEmpty()) {
                return this.stack.get(this.stack.size() - 1);
            }
            return null;
        }

        private PListNodeBuilder peekNE() {
            PListNodeBuilder result = this.peek();
            if (result == null) {
                throw new ConfigurationRuntimeException("Access to empty stack!");
            }
            return result;
        }

        private PListNodeBuilder pop() {
            if (!this.stack.isEmpty()) {
                return this.stack.remove(this.stack.size() - 1);
            }
            return null;
        }

        private void push(PListNodeBuilder node) {
            this.stack.add(node);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if ("array".equals(qName)) {
                this.push(new ArrayNodeBuilder());
            } else if ("dict".equals(qName) && this.peek() instanceof ArrayNodeBuilder) {
                this.push(new PListNodeBuilder());
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("key".equals(qName)) {
                PListNodeBuilder node = new PListNodeBuilder();
                node.setName(this.buffer.toString());
                this.peekNE().addChild(node);
                this.push(node);
            } else if ("dict".equals(qName)) {
                PListNodeBuilder builder = this.pop();
                assert (builder != null) : "Stack was empty!";
                if (this.peek() instanceof ArrayNodeBuilder) {
                    XMLPropertyListConfiguration config = new XMLPropertyListConfiguration(builder.createNode());
                    ArrayNodeBuilder node = (ArrayNodeBuilder)this.peekNE();
                    node.addValue(config);
                }
            } else {
                if ("string".equals(qName)) {
                    this.peekNE().addValue(this.buffer.toString());
                } else if ("integer".equals(qName)) {
                    this.peekNE().addIntegerValue(this.buffer.toString());
                } else if ("real".equals(qName)) {
                    this.peekNE().addRealValue(this.buffer.toString());
                } else if ("true".equals(qName)) {
                    this.peekNE().addTrueValue();
                } else if ("false".equals(qName)) {
                    this.peekNE().addFalseValue();
                } else if ("data".equals(qName)) {
                    this.peekNE().addDataValue(this.buffer.toString());
                } else if ("date".equals(qName)) {
                    try {
                        this.peekNE().addDateValue(this.buffer.toString());
                    }
                    catch (IllegalArgumentException iex) {
                        XMLPropertyListConfiguration.this.getLogger().warn("Ignoring invalid date property " + this.buffer);
                    }
                } else if ("array".equals(qName)) {
                    ArrayNodeBuilder array = (ArrayNodeBuilder)this.pop();
                    this.peekNE().addList(array);
                }
                if (!(this.peek() instanceof ArrayNodeBuilder)) {
                    this.pop();
                }
            }
            this.buffer.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            this.buffer.append(ch, start, length);
        }
    }
}

