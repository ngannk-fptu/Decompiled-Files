/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import org.outerj.daisy.diff.DaisyDiff;
import org.outerj.daisy.diff.HtmlCleaner;
import org.outerj.daisy.diff.XslFilter;
import org.outerj.daisy.diff.html.HTMLDiffer;
import org.outerj.daisy.diff.html.HtmlSaxDiffOutput;
import org.outerj.daisy.diff.html.TextNodeComparator;
import org.outerj.daisy.diff.html.dom.DomTreeBuilder;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class Main {
    static boolean quietMode = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) throws URISyntaxException {
        block52: {
            if (args.length < 2) {
                Main.help();
            }
            boolean htmlDiff = true;
            boolean htmlOut = true;
            String outputFileName = "daisydiff.htm";
            String[] css = new String[]{};
            InputStream oldStream = null;
            InputStream newStream = null;
            try {
                TransformerHandler postProcess;
                for (int i = 2; i < args.length; ++i) {
                    String[] split = args[i].split("=");
                    if (split[0].equalsIgnoreCase("--file")) {
                        outputFileName = split[1];
                        continue;
                    }
                    if (split[0].equalsIgnoreCase("--type")) {
                        if (!split[1].equalsIgnoreCase("tag")) continue;
                        htmlDiff = false;
                        continue;
                    }
                    if (split[0].equalsIgnoreCase("--css")) {
                        css = split[1].split(";");
                        continue;
                    }
                    if (split[0].equalsIgnoreCase("--output")) {
                        if (!split[1].equalsIgnoreCase("xml")) continue;
                        htmlOut = false;
                        continue;
                    }
                    if (split[0].equals("--q")) {
                        quietMode = true;
                        continue;
                    }
                    Main.help();
                }
                File outputFile = new File(outputFileName);
                try {
                    outputFile.createNewFile();
                }
                catch (IOException e) {
                    System.err.println("Filepath " + outputFileName + " is malformed, or some of its folders don't exist, or you don't have write access.");
                    try {
                        if (oldStream != null) {
                            oldStream.close();
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    try {
                        if (newStream != null) {
                            newStream.close();
                        }
                    }
                    catch (IOException iOException) {
                        // empty catch block
                    }
                    return;
                }
                if (!quietMode) {
                    System.out.println("Daisy Diff https://github.com/DaisyDiff/DaisyDiff");
                    System.out.println("Comparing documents: " + args[0] + " and " + args[1]);
                    System.out.println("Diff type: " + (htmlDiff ? "html" : "tag"));
                    System.out.println("Writing " + (htmlOut ? "html" : "xml") + " output to " + outputFileName);
                }
                if (css.length > 0) {
                    if (!quietMode) {
                        System.out.println("Adding external css files:");
                    }
                    for (String cssLink : css) {
                        System.out.println("  " + cssLink);
                    }
                }
                if (!quietMode) {
                    System.out.println("");
                    System.out.print(".");
                }
                SAXTransformerFactory tf = (SAXTransformerFactory)TransformerFactory.newInstance();
                TransformerHandler result = tf.newTransformerHandler();
                result.setResult(new StreamResult(outputFile));
                oldStream = args[0].startsWith("http://") ? new URI(args[0]).toURL().openStream() : new FileInputStream(args[0]);
                newStream = args[1].startsWith("http://") ? new URI(args[1]).toURL().openStream() : new FileInputStream(args[1]);
                XslFilter filter = new XslFilter();
                if (htmlDiff) {
                    postProcess = htmlOut ? filter.xsl(result, "xslfilter/htmlheader.xsl") : result;
                    Locale locale = Locale.getDefault();
                    String prefix = "diff";
                    HtmlCleaner cleaner = new HtmlCleaner();
                    InputSource oldSource = new InputSource(oldStream);
                    InputSource newSource = new InputSource(newStream);
                    DomTreeBuilder oldHandler = new DomTreeBuilder();
                    cleaner.cleanAndParse(oldSource, oldHandler);
                    System.out.print(".");
                    TextNodeComparator leftComparator = new TextNodeComparator(oldHandler, locale);
                    DomTreeBuilder newHandler = new DomTreeBuilder();
                    cleaner.cleanAndParse(newSource, newHandler);
                    System.out.print(".");
                    TextNodeComparator rightComparator = new TextNodeComparator(newHandler, locale);
                    postProcess.startDocument();
                    postProcess.startElement("", "diffreport", "diffreport", new AttributesImpl());
                    Main.doCSS(css, postProcess);
                    postProcess.startElement("", "diff", "diff", new AttributesImpl());
                    HtmlSaxDiffOutput output = new HtmlSaxDiffOutput(postProcess, prefix);
                    HTMLDiffer differ = new HTMLDiffer(output);
                    differ.diff(leftComparator, rightComparator);
                    System.out.print(".");
                    postProcess.endElement("", "diff", "diff");
                    postProcess.endElement("", "diffreport", "diffreport");
                    postProcess.endDocument();
                    break block52;
                }
                postProcess = htmlOut ? filter.xsl(result, "xslfilter/tagheader.xsl") : result;
                postProcess.startDocument();
                postProcess.startElement("", "diffreport", "diffreport", new AttributesImpl());
                postProcess.startElement("", "diff", "diff", new AttributesImpl());
                System.out.print(".");
                InputStreamReader oldReader = null;
                BufferedReader oldBuffer = null;
                InputStreamReader newISReader = null;
                BufferedReader newBuffer = null;
                try {
                    oldReader = new InputStreamReader(oldStream);
                    oldBuffer = new BufferedReader(oldReader);
                    newISReader = new InputStreamReader(newStream);
                    newBuffer = new BufferedReader(newISReader);
                    DaisyDiff.diffTag(oldBuffer, newBuffer, (ContentHandler)postProcess);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                finally {
                    oldBuffer.close();
                    newBuffer.close();
                    oldReader.close();
                    newISReader.close();
                }
                System.out.print(".");
                postProcess.endElement("", "diff", "diff");
                postProcess.endElement("", "diffreport", "diffreport");
                postProcess.endDocument();
            }
            catch (Throwable e) {
                if (quietMode) {
                    System.out.println(e);
                } else {
                    e.printStackTrace();
                    if (e.getCause() != null) {
                        e.getCause().printStackTrace();
                    }
                    if (e instanceof SAXException) {
                        ((SAXException)e).getException().printStackTrace();
                    }
                    Main.help();
                }
            }
            finally {
                try {
                    if (oldStream != null) {
                        oldStream.close();
                    }
                }
                catch (IOException iOException) {}
                try {
                    if (newStream != null) {
                        newStream.close();
                    }
                }
                catch (IOException iOException) {}
            }
        }
        if (quietMode) {
            System.out.println();
        } else {
            System.out.println("done");
        }
    }

    private static void doCSS(String[] css, ContentHandler handler) throws SAXException {
        handler.startElement("", "css", "css", new AttributesImpl());
        for (String cssLink : css) {
            AttributesImpl attr = new AttributesImpl();
            attr.addAttribute("", "href", "href", "CDATA", cssLink);
            attr.addAttribute("", "type", "type", "CDATA", "text/css");
            attr.addAttribute("", "rel", "rel", "CDATA", "stylesheet");
            handler.startElement("", "link", "link", attr);
            handler.endElement("", "link", "link");
        }
        handler.endElement("", "css", "css");
    }

    private static void help() {
        System.out.println("==========================");
        System.out.println("DAISY DIFF HELP:");
        System.out.println("java -jar daisydiff.jar [oldHTML] [newHTML]");
        System.out.println("--file=[filename] - Write output to the specified file.");
        System.out.println("--type=[html/tag] - Use the html (default) diff algorithm or the tag diff.");
        System.out.println("--css=[cssfile1;cssfile2;cssfile3] - Add external CSS files.");
        System.out.println("--output=[html/xml] - Write html (default) or xml output.");
        System.out.println("--q  - Generate less console output.");
        System.out.println("");
        System.out.println("EXAMPLES: ");
        System.out.println("(1)");
        System.out.println("java -jar daisydiff.jar http://web.archive.org/web/20070107145418/http://news.bbc.co.uk/ http://web.archive.org/web/20070107182640/http://news.bbc.co.uk/ --css=http://web.archive.org/web/20070107145418/http://news.bbc.co.uk/nol/shared/css/news_r5.css");
        System.out.println("(2)");
        System.out.println("java -jar daisydiff.jar http://cocoondev.org/wiki/291-cd/version/15/part/SimpleDocumentContent/data http://cocoondev.org/wiki/291-cd/version/17/part/SimpleDocumentContent/data --css=http://cocoondev.org/resources/skins/daisysite/css/daisy.css --output=xml --file=daisysite.htm");
        System.out.println("==========================");
        System.exit(0);
    }
}

