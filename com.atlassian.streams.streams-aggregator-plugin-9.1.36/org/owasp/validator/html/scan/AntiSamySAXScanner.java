/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serialize.HTMLSerializer
 *  org.apache.xml.serialize.OutputFormat
 */
package org.owasp.validator.html.scan;

import com.atlassian.xhtml.parsing.BlockIsolatingTagBalancer;
import com.atlassian.xhtml.parsing.SelfClosingTagPreservingHTMLTagBalancer;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.htmlunit.cyberneko.CustomizableHTMLTagBalancer;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentFilter;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.ScanException;
import org.owasp.validator.html.scan.AbstractAntiSamyScanner;
import org.owasp.validator.html.scan.CustomSAXParser;
import org.owasp.validator.html.scan.MagicSAXFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class AntiSamySAXScanner
extends AbstractAntiSamyScanner {
    private static final TransformerFactory sTransformerFactory = TransformerFactory.newInstance();

    public AntiSamySAXScanner(Policy policy) {
        super(policy);
    }

    @Override
    public CleanResults getResults() {
        return null;
    }

    @Override
    public CleanResults scan(String html) throws ScanException {
        return this.scan(html, this.policy);
    }

    public CleanResults scan(String html, Policy policy) throws ScanException {
        if (html == null) {
            throw new ScanException(new NullPointerException("Null html input"));
        }
        int maxInputSize = this.policy.getMaxInputSize();
        if (html.length() > maxInputSize) {
            this.addError("error.size.toolarge", new Object[]{html.length(), maxInputSize});
            throw new ScanException((String)this.errorMessages.get(0));
        }
        final StringWriter out = new StringWriter();
        StringReader reader = new StringReader(html);
        CleanResults results = this.scan(reader, out);
        final String tainted = html;
        Callable<String> cleanCallable = new Callable<String>(){

            @Override
            public String call() throws Exception {
                return AntiSamySAXScanner.this.trim(tainted, out.toString());
            }
        };
        return new CleanResults(results.getStartOfScan(), cleanCallable, null, results.getErrorMessages());
    }

    public CleanResults scan(Reader reader, Writer writer) throws ScanException {
        try {
            CachedItem cachedItem = new CachedItem(AntiSamySAXScanner.getNewTransformer(), AntiSamySAXScanner.getParser(), new MagicSAXFilter(messages), this.policy);
            CustomSAXParser parser = cachedItem.saxParser;
            cachedItem.magicSAXFilter.reset(this.policy);
            long startOfScan = System.currentTimeMillis();
            SAXSource source = new SAXSource(parser, new InputSource(reader));
            Transformer transformer = cachedItem.transformer;
            boolean formatOutput = this.policy.isFormatOutput();
            boolean useXhtml = this.policy.isUseXhtml();
            boolean omitXml = this.policy.isOmitXmlDeclaration();
            transformer.setOutputProperty("indent", formatOutput ? "yes" : "no");
            transformer.setOutputProperty("omit-xml-declaration", omitXml ? "yes" : "no");
            transformer.setOutputProperty("method", useXhtml ? "xml" : "html");
            OutputFormat format = this.getOutputFormat();
            HTMLSerializer serializer = this.getHTMLSerializer(writer, format);
            transformer.transform(source, new SAXResult((ContentHandler)serializer));
            this.errorMessages.clear();
            this.errorMessages.addAll(cachedItem.magicSAXFilter.getErrorMessages());
            return new CleanResults(startOfScan, (String)null, null, (List<String>)this.errorMessages);
        }
        catch (Exception e) {
            throw new ScanException(e);
        }
    }

    private static synchronized Transformer getNewTransformer() {
        try {
            return sTransformerFactory.newTransformer();
        }
        catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private static CustomSAXParser getParser() {
        try {
            CustomSAXParser parser = new CustomSAXParser();
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://cyberneko.org/html/features/document-fragment", true);
            parser.setFeature("http://cyberneko.org/html/features/scanner/cdata-sections", true);
            parser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
            parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
            return parser;
        }
        catch (SAXNotRecognizedException | SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    static class CachedItem {
        private final Transformer transformer;
        private final CustomSAXParser saxParser;
        private final MagicSAXFilter magicSAXFilter;

        CachedItem(Transformer transformer, CustomSAXParser saxParser, MagicSAXFilter magicSAXFilter, Policy policy) {
            this.transformer = transformer;
            this.saxParser = saxParser;
            this.magicSAXFilter = magicSAXFilter;
            Set<String> blocksToIsolate = policy.getBlocksToIsolate();
            CustomizableHTMLTagBalancer balancer = null;
            if (blocksToIsolate.isEmpty()) {
                balancer = new SelfClosingTagPreservingHTMLTagBalancer(saxParser.getHtmlConfiguration());
            } else {
                balancer = new BlockIsolatingTagBalancer(blocksToIsolate, saxParser.getHtmlConfiguration());
                try {
                    saxParser.setFeature("http://cyberneko.org/html/features/scanner/allow-selfclosing-tags", true);
                }
                catch (SAXNotRecognizedException e) {
                    throw new RuntimeException(e);
                }
                catch (SAXNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
            XMLDocumentFilter[] filters = new XMLDocumentFilter[]{balancer, magicSAXFilter};
            try {
                saxParser.setProperty("http://cyberneko.org/html/properties/filters", filters);
            }
            catch (SAXNotRecognizedException | SAXNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

