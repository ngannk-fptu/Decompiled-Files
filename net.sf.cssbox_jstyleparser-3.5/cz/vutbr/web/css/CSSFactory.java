/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CSSException;
import cz.vutbr.web.css.ElementMatcher;
import cz.vutbr.web.css.MatchCondition;
import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.MediaSpec;
import cz.vutbr.web.css.MediaSpecAll;
import cz.vutbr.web.css.NetworkProcessor;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.RuleFactory;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.SupportedCSS;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.csskit.DeclarationTransformer;
import cz.vutbr.web.csskit.DefaultNetworkProcessor;
import cz.vutbr.web.csskit.MatchConditionImpl;
import cz.vutbr.web.csskit.antlr4.CSSParserFactory;
import cz.vutbr.web.domassign.Analyzer;
import cz.vutbr.web.domassign.DeclarationTransformerImpl;
import cz.vutbr.web.domassign.StyleMap;
import cz.vutbr.web.domassign.Traversal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import org.fit.net.DataURLHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public final class CSSFactory {
    private static Logger log = LoggerFactory.getLogger(CSSFactory.class);
    private static final String DEFAULT_TERM_FACTORY = "cz.vutbr.web.csskit.TermFactoryImpl";
    private static final String DEFAULT_SUPPORTED_CSS = "cz.vutbr.web.domassign.SupportedCSS3";
    private static final String DEFAULT_RULE_FACTORY = "cz.vutbr.web.csskit.RuleFactoryImpl";
    private static final String DEFAULT_DECLARATION_TRANSFORMER = "cz.vutbr.web.domassign.DeclarationTransformerImpl";
    private static final String DEFAULT_NODE_DATA_IMPL = "cz.vutbr.web.domassign.SingleMapNodeData";
    private static final String DEFAULT_ELEMENT_MATCHER = "cz.vutbr.web.csskit.ElementMatcherSafeStd";
    private static CSSParserFactory pf;
    private static TermFactory tf;
    private static SupportedCSS css;
    private static RuleFactory rf;
    private static DeclarationTransformer dt;
    private static ElementMatcher matcher;
    private static Class<? extends NodeData> ndImpl;
    private static MatchCondition dcond;
    private static boolean implyPixelLengths;
    private static MediaSpec autoImportMedia;
    private static NetworkProcessor networkProcessor;

    public static final void setImplyPixelLength(boolean b) {
        implyPixelLengths = b;
    }

    public static final boolean getImplyPixelLength() {
        return implyPixelLengths;
    }

    public static MediaSpec getAutoImportMedia() {
        if (autoImportMedia == null) {
            autoImportMedia = new MediaSpecAll();
        }
        return autoImportMedia;
    }

    public static void setAutoImportMedia(MediaSpec autoImportMedia) {
        CSSFactory.autoImportMedia = autoImportMedia;
    }

    public static NetworkProcessor getNetworkProcessor() {
        if (networkProcessor == null) {
            networkProcessor = new DefaultNetworkProcessor();
        }
        return networkProcessor;
    }

    public static void setNetworkProcessor(NetworkProcessor networkProcessor) {
        CSSFactory.networkProcessor = networkProcessor;
    }

    public static final void registerCSSParserFactory(CSSParserFactory factory) {
        pf = factory;
    }

    private static final CSSParserFactory getCSSParserFactory() {
        if (pf == null) {
            pf = CSSParserFactory.getInstance();
        }
        return pf;
    }

    public static final void registerTermFactory(TermFactory newFactory) {
        tf = newFactory;
    }

    public static final TermFactory getTermFactory() {
        if (tf == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_TERM_FACTORY);
                Method m = clazz.getMethod("getInstance", new Class[0]);
                CSSFactory.registerTermFactory((TermFactory)m.invoke(null, new Object[0]));
                log.debug("Retrived {} as default TermFactory implementation.", (Object)DEFAULT_TERM_FACTORY);
            }
            catch (Exception e) {
                log.error("Unable to get TermFactory from default", (Throwable)e);
                throw new RuntimeException("No TermFactory implementation registered!");
            }
        }
        return tf;
    }

    public static final void registerSupportedCSS(SupportedCSS newCSS) {
        css = newCSS;
    }

    public static final SupportedCSS getSupportedCSS() {
        if (css == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_SUPPORTED_CSS);
                Method m = clazz.getMethod("getInstance", new Class[0]);
                CSSFactory.registerSupportedCSS((SupportedCSS)m.invoke(null, new Object[0]));
                log.debug("Retrived {} as default SupportedCSS implementation.", (Object)DEFAULT_SUPPORTED_CSS);
            }
            catch (Exception e) {
                log.error("Unable to get SupportedCSS from default", (Throwable)e);
                throw new RuntimeException("No SupportedCSS implementation registered!");
            }
        }
        return css;
    }

    public static final void registerRuleFactory(RuleFactory newRuleFactory) {
        rf = newRuleFactory;
    }

    public static final RuleFactory getRuleFactory() {
        if (rf == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_RULE_FACTORY);
                Method m = clazz.getMethod("getInstance", new Class[0]);
                CSSFactory.registerRuleFactory((RuleFactory)m.invoke(null, new Object[0]));
                log.debug("Retrived {} as default RuleFactory implementation.", (Object)DEFAULT_RULE_FACTORY);
            }
            catch (Exception e) {
                log.error("Unable to get RuleFactory from default", (Throwable)e);
                throw new RuntimeException("No RuleFactory implementation registered!");
            }
        }
        return rf;
    }

    public static final void registerDeclarationTransformer(DeclarationTransformer newDeclarationTransformer) {
        dt = newDeclarationTransformer;
    }

    public static final DeclarationTransformer getDeclarationTransformer() {
        if (dt == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_DECLARATION_TRANSFORMER);
                Method m = clazz.getMethod("getInstance", new Class[0]);
                CSSFactory.registerDeclarationTransformer((DeclarationTransformerImpl)m.invoke(null, new Object[0]));
                log.debug("Retrived {} as default DeclarationTransformer implementation.", (Object)DEFAULT_DECLARATION_TRANSFORMER);
            }
            catch (Exception e) {
                log.error("Unable to get DeclarationTransformer from default", (Throwable)e);
                throw new RuntimeException("No DeclarationTransformer implementation registered!");
            }
        }
        return dt;
    }

    public static final void registerElementMatcher(ElementMatcher newElementMatcher) {
        matcher = newElementMatcher;
    }

    public static final ElementMatcher getElementMatcher() {
        if (matcher == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_ELEMENT_MATCHER);
                CSSFactory.registerElementMatcher((ElementMatcher)clazz.newInstance());
                log.debug("Retrived {} as default ElementMatcher implementation.", (Object)DEFAULT_ELEMENT_MATCHER);
            }
            catch (Exception e) {
                log.error("Unable to get ElementMatcher from default", (Throwable)e);
                throw new RuntimeException("No ElementMatcher implementation registered!", e);
            }
        }
        return matcher;
    }

    public static final void registerDefaultMatchCondition(MatchCondition newMatchCondition) {
        dcond = newMatchCondition;
    }

    public static final MatchCondition getDefaultMatchCondition() {
        if (dcond == null) {
            dcond = new MatchConditionImpl();
        }
        return dcond;
    }

    public static final void registerNodeDataInstance(Class<? extends NodeData> clazz) {
        try {
            NodeData test = clazz.newInstance();
            ndImpl = clazz;
        }
        catch (InstantiationException e) {
            throw new RuntimeException("NodeData implemenation (" + clazz.getName() + ") doesn't provide sole constructor", e);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("NodeData implementation (" + clazz.getName() + ") is not accesible", e);
        }
    }

    public static final NodeData createNodeData() {
        if (ndImpl == null) {
            try {
                Class<?> clazz = Class.forName(DEFAULT_NODE_DATA_IMPL);
                CSSFactory.registerNodeDataInstance(clazz);
                log.debug("Registered {} as default NodeData instance.", (Object)DEFAULT_NODE_DATA_IMPL);
            }
            catch (Exception clazz) {
                // empty catch block
            }
        }
        try {
            return ndImpl.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException("No NodeData implementation registered");
        }
    }

    public static final StyleSheet parse(URL url, String encoding) throws CSSException, IOException {
        return CSSFactory.getCSSParserFactory().parse(url, CSSFactory.getNetworkProcessor(), encoding, CSSParserFactory.SourceType.URL, url);
    }

    public static final StyleSheet parse(URL url, NetworkProcessor network, String encoding) throws CSSException, IOException {
        return CSSFactory.getCSSParserFactory().parse(url, network, encoding, CSSParserFactory.SourceType.URL, url);
    }

    public static final StyleSheet parse(String fileName, String encoding) throws CSSException, IOException {
        try {
            File f = new File(fileName);
            URL url = f.toURI().toURL();
            return CSSFactory.parse(url, encoding);
        }
        catch (MalformedURLException e) {
            String message = "Unable to construct URL from fileName: " + fileName;
            log.error(message);
            throw new FileNotFoundException(message);
        }
    }

    @Deprecated
    public static final StyleSheet parse(String css) throws IOException, CSSException {
        URL base = new URL("file:///base/url/is/not/specified");
        return CSSFactory.getCSSParserFactory().parse(css, CSSFactory.getNetworkProcessor(), null, CSSParserFactory.SourceType.EMBEDDED, base);
    }

    public static final StyleSheet parseString(String css, URL base) throws IOException, CSSException {
        return CSSFactory.parseString(css, base, CSSFactory.getNetworkProcessor());
    }

    public static final StyleSheet parseString(String css, URL base, NetworkProcessor network) throws IOException, CSSException {
        URL baseurl = base;
        if (baseurl == null) {
            baseurl = new URL("file:///base/url/is/not/specified");
        }
        return CSSFactory.getCSSParserFactory().parse(css, network, null, CSSParserFactory.SourceType.EMBEDDED, baseurl);
    }

    public static final StyleSheet getUsedStyles(Document doc, String encoding, URL base, MediaSpec media, NetworkProcessor network) {
        SourceData pair = new SourceData(base, network, media);
        CSSAssignTraversal traversal = new CSSAssignTraversal(doc, encoding, pair, 1);
        StyleSheet style = (StyleSheet)CSSFactory.getRuleFactory().createStyleSheet().unlock();
        traversal.listTraversal(style);
        return style;
    }

    public static final StyleSheet getUsedStyles(Document doc, String encoding, URL base, MediaSpec media) {
        return CSSFactory.getUsedStyles(doc, encoding, base, media, CSSFactory.getNetworkProcessor());
    }

    public static final StyleSheet getUsedStyles(Document doc, String encoding, URL base, String media) {
        return CSSFactory.getUsedStyles(doc, encoding, base, new MediaSpec(media), CSSFactory.getNetworkProcessor());
    }

    @Deprecated
    public static final StyleSheet getUsedStyles(Document doc, URL base, String media) {
        return CSSFactory.getUsedStyles(doc, null, base, media);
    }

    public static final StyleMap assignDOM(Document doc, String encoding, URL base, MediaSpec media, boolean useInheritance) {
        return CSSFactory.assignDOM(doc, encoding, base, media, useInheritance, null);
    }

    public static final StyleMap assignDOM(Document doc, String encoding, URL base, String media, boolean useInheritance) {
        return CSSFactory.assignDOM(doc, encoding, base, new MediaSpec(media), useInheritance);
    }

    public static final StyleMap assignDOM(Document doc, String encoding, URL base, MediaSpec media, boolean useInheritance, MatchCondition matchCond) {
        return CSSFactory.assignDOM(doc, encoding, CSSFactory.getNetworkProcessor(), base, media, useInheritance, matchCond);
    }

    public static final StyleMap assignDOM(Document doc, String encoding, NetworkProcessor network, URL base, MediaSpec media, boolean useInheritance, MatchCondition matchCond) {
        SourceData pair = new SourceData(base, network, media);
        CSSAssignTraversal traversal = new CSSAssignTraversal(doc, encoding, pair, 1);
        StyleSheet style = (StyleSheet)CSSFactory.getRuleFactory().createStyleSheet().unlock();
        traversal.listTraversal(style);
        Analyzer analyzer = new Analyzer(style);
        if (matchCond != null) {
            analyzer.registerMatchCondition(matchCond);
        }
        return analyzer.evaluateDOM(doc, media, useInheritance);
    }

    public static final StyleMap assignDOM(Document doc, String encoding, URL base, String media, boolean useInheritance, MatchCondition matchCond) {
        return CSSFactory.assignDOM(doc, encoding, base, new MediaSpec(media), useInheritance, matchCond);
    }

    @Deprecated
    public static final StyleMap assignDOM(Document doc, URL base, String media, boolean useInheritance) {
        return CSSFactory.assignDOM(doc, null, base, media, useInheritance);
    }

    static /* synthetic */ CSSParserFactory access$000() {
        return CSSFactory.getCSSParserFactory();
    }

    static {
        implyPixelLengths = false;
        autoImportMedia = null;
        networkProcessor = null;
    }

    private static final class SourceData {
        public URL base;
        public NetworkProcessor network;
        public MediaSpec media;

        public SourceData(URL base, NetworkProcessor network, MediaSpec media) {
            this.base = base;
            this.network = network;
            this.media = media;
        }
    }

    private static final class CSSAssignTraversal
    extends Traversal<StyleSheet> {
        private static CSSParserFactory pf = CSSFactory.access$000();
        private String encoding;
        private final ElementMatcher matcher;

        public CSSAssignTraversal(Document doc, String encoding, Object source, int whatToShow) {
            super(doc, source, whatToShow);
            this.encoding = encoding;
            this.matcher = CSSFactory.getElementMatcher();
        }

        @Override
        protected void processNode(StyleSheet result, Node current, Object source) {
            URL base = ((SourceData)source).base;
            MediaSpec media = ((SourceData)source).media;
            NetworkProcessor network = ((SourceData)source).network;
            Element elem = (Element)current;
            try {
                if (this.isEmbeddedStyleSheet(elem, media)) {
                    result = pf.append(CSSAssignTraversal.extractElementText(elem), network, null, CSSParserFactory.SourceType.EMBEDDED, result, base);
                    log.debug("Matched embedded CSS style");
                } else if (this.isLinkedStyleSheet(elem, media)) {
                    URL uri = DataURLHandler.createURL(base, this.matcher.getAttribute(elem, "href"));
                    result = pf.append(uri, network, this.encoding, CSSParserFactory.SourceType.URL, result, uri);
                    log.debug("Matched linked CSS style");
                } else {
                    if (elem.getAttribute("style") != null && elem.getAttribute("style").length() > 0) {
                        result = pf.append(elem.getAttribute("style"), network, null, CSSParserFactory.SourceType.INLINE, elem, true, result, base);
                        log.debug("Matched inline CSS style");
                    }
                    if (elem.getAttribute("XDefaultStyle") != null && elem.getAttribute("XDefaultStyle").length() > 0) {
                        result = pf.append(elem.getAttribute("XDefaultStyle"), network, null, CSSParserFactory.SourceType.INLINE, elem, false, result, base);
                        log.debug("Matched default CSS style");
                    }
                }
            }
            catch (CSSException ce) {
                log.error("THROWN:", (Throwable)ce);
            }
            catch (IOException ioe) {
                log.error("THROWN:", (Throwable)ioe);
            }
        }

        private boolean isEmbeddedStyleSheet(Element e, MediaSpec media) {
            return "style".equalsIgnoreCase(e.getNodeName()) && CSSAssignTraversal.isAllowedMedia(e, media);
        }

        private boolean isLinkedStyleSheet(Element e, MediaSpec media) {
            return e.getNodeName().equalsIgnoreCase("link") && this.matcher.getAttribute(e, "rel").toLowerCase().contains("stylesheet") && (this.matcher.getAttribute(e, "type").isEmpty() || "text/css".equalsIgnoreCase(this.matcher.getAttribute(e, "type"))) && CSSAssignTraversal.isAllowedMedia(e, media);
        }

        private static String extractElementText(Element e) {
            Node text = e.getFirstChild();
            if (text != null && text.getNodeType() == 3) {
                return ((Text)text).getData();
            }
            return "";
        }

        private static boolean isAllowedMedia(Element e, MediaSpec media) {
            String attr = e.getAttribute("media");
            if (attr != null && attr.length() > 0) {
                if ((attr = attr.trim()).length() > 0) {
                    List<MediaQuery> ql = pf.parseMediaQuery(attr);
                    if (ql != null) {
                        for (MediaQuery q : ql) {
                            if (!media.matches(q)) continue;
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
                return media.matchesEmpty();
            }
            return media.matchesEmpty();
        }
    }
}

