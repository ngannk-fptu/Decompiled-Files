/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser.external;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.parser.external.ExternalParser;
import org.apache.tika.parser.external.ExternalParsersConfigReaderMetKeys;
import org.apache.tika.utils.XMLReaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ExternalParsersConfigReader
implements ExternalParsersConfigReaderMetKeys {
    public static List<ExternalParser> read(InputStream stream) throws TikaException, IOException {
        try {
            DocumentBuilder builder = XMLReaderUtils.getDocumentBuilder();
            Document document = builder.parse(new InputSource(stream));
            return ExternalParsersConfigReader.read(document);
        }
        catch (SAXException e) {
            throw new TikaException("Invalid parser configuration", e);
        }
    }

    public static List<ExternalParser> read(Document document) throws TikaException, IOException {
        return ExternalParsersConfigReader.read(document.getDocumentElement());
    }

    public static List<ExternalParser> read(Element element) throws TikaException, IOException {
        ArrayList<ExternalParser> parsers = new ArrayList<ExternalParser>();
        if (element != null && element.getTagName().equals("external-parsers")) {
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); ++i) {
                ExternalParser p;
                Element child;
                Node node = nodes.item(i);
                if (node.getNodeType() != 1 || !(child = (Element)node).getTagName().equals("parser") || (p = ExternalParsersConfigReader.readParser(child)) == null) continue;
                parsers.add(p);
            }
        } else {
            throw new MimeTypeException("Not a <external-parsers/> configuration document: " + (element != null ? element.getTagName() : "n/a"));
        }
        return parsers;
    }

    private static ExternalParser readParser(Element parserDef) throws TikaException {
        ExternalParser parser = new ExternalParser();
        NodeList children = parserDef.getChildNodes();
        block12: for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 1) continue;
            Element child = (Element)node;
            switch (child.getTagName()) {
                case "check": {
                    boolean present = ExternalParsersConfigReader.readCheckTagAndCheck(child);
                    if (present) continue block12;
                    return null;
                }
                case "command": {
                    parser.setCommand(ExternalParsersConfigReader.getString(child));
                    continue block12;
                }
                case "mime-types": {
                    parser.setSupportedTypes(ExternalParsersConfigReader.readMimeTypes(child));
                    continue block12;
                }
                case "metadata": {
                    parser.setMetadataExtractionPatterns(ExternalParsersConfigReader.readMetadataPatterns(child));
                    continue block12;
                }
                default: {
                    throw new IllegalArgumentException("reaction not defined for " + child.getTagName());
                }
            }
        }
        return parser;
    }

    private static Set<MediaType> readMimeTypes(Element mimeTypes) {
        HashSet<MediaType> types = new HashSet<MediaType>();
        NodeList children = mimeTypes.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Element child;
            Node node = children.item(i);
            if (node.getNodeType() != 1 || !(child = (Element)node).getTagName().equals("mime-type")) continue;
            types.add(MediaType.parse(ExternalParsersConfigReader.getString(child)));
        }
        return types;
    }

    private static Map<Pattern, String> readMetadataPatterns(Element metadataDef) {
        HashMap<Pattern, String> metadata = new HashMap<Pattern, String>();
        NodeList children = metadataDef.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Element child;
            Node node = children.item(i);
            if (node.getNodeType() != 1 || !(child = (Element)node).getTagName().equals("match")) continue;
            String metadataKey = child.getAttribute("key");
            Pattern pattern = Pattern.compile(ExternalParsersConfigReader.getString(child));
            metadata.put(pattern, metadataKey);
        }
        return metadata;
    }

    private static boolean readCheckTagAndCheck(Element checkDef) {
        String command = null;
        ArrayList<Integer> errorVals = new ArrayList<Integer>();
        NodeList children = checkDef.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 1) continue;
            Element child = (Element)node;
            if (child.getTagName().equals("command")) {
                command = ExternalParsersConfigReader.getString(child);
            }
            if (!child.getTagName().equals("error-codes")) continue;
            String errs = ExternalParsersConfigReader.getString(child);
            StringTokenizer st = new StringTokenizer(errs, ",");
            while (st.hasMoreElements()) {
                try {
                    String s = st.nextToken();
                    errorVals.add(Integer.parseInt(s));
                }
                catch (NumberFormatException numberFormatException) {}
            }
        }
        if (command != null) {
            String[] theCommand = command.split(" ");
            int[] errVals = new int[errorVals.size()];
            for (int i = 0; i < errVals.length; ++i) {
                errVals[i] = (Integer)errorVals.get(i);
            }
            return ExternalParser.check(theCommand, errVals);
        }
        return true;
    }

    private static String getString(Element element) {
        StringBuffer s = new StringBuffer();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            Node node = children.item(i);
            if (node.getNodeType() != 3) continue;
            s.append(node.getNodeValue());
        }
        return s.toString();
    }
}

