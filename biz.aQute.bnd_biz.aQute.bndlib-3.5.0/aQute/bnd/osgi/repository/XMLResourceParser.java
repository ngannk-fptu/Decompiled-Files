/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi.repository;

import aQute.bnd.header.Attrs;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResourceBuilder;
import aQute.lib.strings.Strings;
import aQute.libg.gzip.GZipUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.osgi.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLResourceParser
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(XMLResourceParser.class);
    static final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
    private static final String NS_URI = "http://www.osgi.org/xmlns/repository/v1.0.0";
    private static final String TAG_REPOSITORY = "repository";
    private static final String TAG_REFERRAL = "referral";
    private static final String TAG_RESOURCE = "resource";
    private static final String TAG_CAPABILITY = "capability";
    private static final String TAG_REQUIREMENT = "requirement";
    private static final String TAG_ATTRIBUTE = "attribute";
    private static final String TAG_DIRECTIVE = "directive";
    private static final String ATTR_REFERRAL_URL = "url";
    private static final String ATTR_REFERRAL_DEPTH = "depth";
    private static final String ATTR_NAMESPACE = "namespace";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_VALUE = "value";
    private static final String ATTR_TYPE = "type";
    private final List<Resource> resources = new ArrayList<Resource>();
    private final XMLStreamReader reader;
    private final Set<URI> traversed;
    private final String what;
    private final URI url;
    private int depth;

    public static List<Resource> getResources(URI uri) throws Exception {
        try (XMLResourceParser parser = new XMLResourceParser(uri);){
            List<Resource> list = parser.parse();
            return list;
        }
    }

    public XMLResourceParser(URI url) throws Exception {
        this(url.toURL().openStream(), url.toString(), url);
    }

    public XMLResourceParser(InputStream in, String what, URI uri) throws Exception {
        this(in, what, 100, new HashSet<URI>(), uri);
    }

    public void setDepth(int n) {
        this.depth = n;
    }

    public XMLResourceParser(InputStream in, String what, int depth, Set<URI> traversed, URI url) throws Exception {
        this.what = what;
        this.depth = depth;
        this.traversed = traversed;
        this.url = url;
        in = GZipUtils.detectCompression(in);
        this.addClose(in);
        this.reader = inputFactory.createXMLStreamReader(in);
    }

    public XMLResourceParser(File location) throws Exception {
        this(location.toURI());
    }

    @Override
    public void close() throws IOException {
        try {
            this.reader.close();
        }
        catch (XMLStreamException e) {
            throw new IOException(e);
        }
        finally {
            super.close();
        }
    }

    List<Resource> getResources() {
        if (!this.isOk()) {
            return null;
        }
        return this.resources;
    }

    public List<Resource> parse() throws Exception {
        if (!this.check(this.reader.hasNext(), "No content found", new Object[0])) {
            return null;
        }
        this.next();
        if (!this.check(this.reader.isStartElement(), "Expected a start element at the root, is %s", this.reader.getEventType())) {
            return null;
        }
        String name = this.reader.getLocalName();
        if (!this.check(TAG_REPOSITORY.equals(name), "Invalid tag name of top element, expected %s, got %s", TAG_REPOSITORY, name)) {
            return null;
        }
        String nsUri = this.reader.getNamespaceURI();
        if (nsUri != null) {
            this.check(NS_URI.equals(nsUri), "Incorrect namespace. Expected %s, got %s", NS_URI, nsUri);
        }
        this.next();
        while (this.reader.isStartElement()) {
            String localName = this.reader.getLocalName();
            if (localName.equals(TAG_REFERRAL)) {
                this.parseReferral();
                continue;
            }
            if (localName.equals(TAG_RESOURCE)) {
                this.parseResource(this.resources);
                continue;
            }
            this.check(false, "Unexpected element %s", localName);
            this.next();
        }
        this.check(this.reader.isEndElement() && this.reader.getLocalName().equals(TAG_REPOSITORY), "Expected to be at the end but are on %s", this.reader.getLocalName());
        return this.getResources();
    }

    public void next() throws XMLStreamException {
        this.report();
        this.reader.nextTag();
    }

    private void report() {
        int type = this.reader.getEventType();
        switch (type) {
            case 1: 
            case 2: 
            case 7: {
                break;
            }
            default: {
                logger.debug("** unknown element, event type {}", (Object)type);
            }
        }
    }

    private void parseReferral() throws Exception {
        if (--this.depth < 0) {
            this.error("Too deep, traversed %s", this.traversed);
        } else {
            String depthString = this.reader.getAttributeValue(NS_URI, ATTR_REFERRAL_DEPTH);
            String urlString = this.reader.getAttributeValue(NS_URI, ATTR_REFERRAL_URL);
            if (this.check(urlString != null, "Expected URL in referral", new Object[0])) {
                URI url = this.url.resolve(urlString);
                this.traversed.add(url);
                int depth = 100;
                if (depthString != null) {
                    depth = Integer.parseInt(depthString);
                }
                InputStream in = url.toURL().openStream();
                try (XMLResourceParser referralParser = new XMLResourceParser(in, urlString, depth, this.traversed, url);){
                    referralParser.parse();
                    this.resources.addAll(referralParser.resources);
                }
            }
        }
        this.tagEnd(TAG_REFERRAL);
    }

    private void tagEnd(String tag) throws XMLStreamException {
        if (!this.check(this.reader.isEndElement(), "Expected end element, got %s for %s (%s)", this.reader.getEventType(), tag, this.reader.getLocalName())) {
            logger.debug("oops, invalid end {}", (Object)tag);
        }
        this.next();
    }

    private void parseResource(List<Resource> resources) throws Exception {
        ResourceBuilder resourceBuilder = new ResourceBuilder();
        this.next();
        while (this.reader.isStartElement()) {
            this.parseCapabilityOrRequirement(resourceBuilder);
        }
        Resource resource = resourceBuilder.build();
        resources.add(resource);
        this.tagEnd(TAG_RESOURCE);
    }

    private void parseCapabilityOrRequirement(ResourceBuilder resourceBuilder) throws Exception {
        String name = this.reader.getLocalName();
        this.check(TAG_REQUIREMENT.equals(name) || TAG_CAPABILITY.equals(name), "Expected <%s> or <%s> tag, got <%s>", TAG_REQUIREMENT, TAG_CAPABILITY, name);
        String namespace = this.reader.getAttributeValue(null, ATTR_NAMESPACE);
        CapReqBuilder capReqBuilder = new CapReqBuilder(namespace);
        this.next();
        while (this.reader.isStartElement()) {
            this.parseAttributesOrDirectives(capReqBuilder);
        }
        if (TAG_REQUIREMENT.equals(name)) {
            resourceBuilder.addRequirement(capReqBuilder);
        } else {
            resourceBuilder.addCapability(capReqBuilder);
        }
        this.tagEnd(name);
    }

    private void parseAttributesOrDirectives(CapReqBuilder capReqBuilder) throws Exception {
        String name;
        switch (name = this.reader.getLocalName()) {
            case "attribute": {
                this.parseAttribute(capReqBuilder);
                break;
            }
            case "directive": {
                this.parseDirective(capReqBuilder);
                break;
            }
            default: {
                this.check(false, "Invalid tag, expected either <%s> or <%s>, got <%s>", TAG_ATTRIBUTE, TAG_DIRECTIVE);
            }
        }
        this.next();
        this.tagEnd(name);
    }

    private boolean check(boolean check, String format, Object ... args) {
        if (check) {
            return true;
        }
        String message = Strings.format(format, args);
        this.error("%s: %s", this.what, message);
        return false;
    }

    private void parseAttribute(CapReqBuilder capReqBuilder) throws Exception {
        String attributeName = this.reader.getAttributeValue(null, ATTR_NAME);
        String attributeValue = this.reader.getAttributeValue(null, ATTR_VALUE);
        String attributeType = this.reader.getAttributeValue(null, ATTR_TYPE);
        if (this.isContent(capReqBuilder) && attributeName.equals(ATTR_REFERRAL_URL)) {
            attributeValue = this.url.resolve(attributeValue).toString();
        }
        Object value = Attrs.convert(attributeType, attributeValue);
        capReqBuilder.addAttribute(attributeName, value);
    }

    private boolean isContent(CapReqBuilder capReqBuilder) {
        return "osgi.content".equals(capReqBuilder.getNamespace());
    }

    private void parseDirective(CapReqBuilder capReqBuilder) throws XMLStreamException {
        String attributeName = this.reader.getAttributeValue(null, ATTR_NAME);
        String attributeValue = this.reader.getAttributeValue(null, ATTR_VALUE);
        String attributeType = this.reader.getAttributeValue(null, ATTR_TYPE);
        this.check(attributeType == null, "Expected a directive to have no type: %s:%s=%s", attributeName, attributeType, attributeValue);
        capReqBuilder.addDirective(attributeName, attributeValue);
    }

    static {
        inputFactory.setProperty("javax.xml.stream.isNamespaceAware", true);
        inputFactory.setProperty("javax.xml.stream.isValidating", false);
        inputFactory.setProperty("javax.xml.stream.supportDTD", false);
    }
}

