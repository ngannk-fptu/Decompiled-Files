/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.saxon.Configuration
 *  net.sf.saxon.dom.DOMNodeWrapper
 *  net.sf.saxon.dom.DocumentWrapper
 *  net.sf.saxon.dom.NodeOverNodeInfo
 *  net.sf.saxon.lib.ConversionRules
 *  net.sf.saxon.ma.map.HashTrieMap
 *  net.sf.saxon.om.GroundedValue
 *  net.sf.saxon.om.Item
 *  net.sf.saxon.om.NodeInfo
 *  net.sf.saxon.om.StructuredQName
 *  net.sf.saxon.query.DynamicQueryContext
 *  net.sf.saxon.query.StaticQueryContext
 *  net.sf.saxon.query.XQueryExpression
 *  net.sf.saxon.str.StringView
 *  net.sf.saxon.str.UnicodeString
 *  net.sf.saxon.trans.XPathException
 *  net.sf.saxon.type.BuiltInAtomicType
 *  net.sf.saxon.value.AnyURIValue
 *  net.sf.saxon.value.AtomicValue
 *  net.sf.saxon.value.BigDecimalValue
 *  net.sf.saxon.value.BigIntegerValue
 *  net.sf.saxon.value.BooleanValue
 *  net.sf.saxon.value.DateTimeValue
 *  net.sf.saxon.value.DateValue
 *  net.sf.saxon.value.DoubleValue
 *  net.sf.saxon.value.DurationValue
 *  net.sf.saxon.value.FloatValue
 *  net.sf.saxon.value.GDayValue
 *  net.sf.saxon.value.GMonthDayValue
 *  net.sf.saxon.value.GMonthValue
 *  net.sf.saxon.value.GYearMonthValue
 *  net.sf.saxon.value.GYearValue
 *  net.sf.saxon.value.HexBinaryValue
 *  net.sf.saxon.value.Int64Value
 *  net.sf.saxon.value.ObjectValue
 *  net.sf.saxon.value.QNameValue
 *  net.sf.saxon.value.SaxonDuration
 *  net.sf.saxon.value.SaxonXMLGregorianCalendar
 *  net.sf.saxon.value.StringValue
 *  net.sf.saxon.value.TimeValue
 */
package org.apache.xmlbeans.impl.xpath.saxon;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathException;
import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeOverNodeInfo;
import net.sf.saxon.lib.ConversionRules;
import net.sf.saxon.ma.map.HashTrieMap;
import net.sf.saxon.om.GroundedValue;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.query.DynamicQueryContext;
import net.sf.saxon.query.StaticQueryContext;
import net.sf.saxon.query.XQueryExpression;
import net.sf.saxon.str.StringView;
import net.sf.saxon.str.UnicodeString;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.value.AnyURIValue;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.BigDecimalValue;
import net.sf.saxon.value.BigIntegerValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.DurationValue;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.GDayValue;
import net.sf.saxon.value.GMonthDayValue;
import net.sf.saxon.value.GMonthValue;
import net.sf.saxon.value.GYearMonthValue;
import net.sf.saxon.value.GYearValue;
import net.sf.saxon.value.HexBinaryValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.ObjectValue;
import net.sf.saxon.value.QNameValue;
import net.sf.saxon.value.SaxonDuration;
import net.sf.saxon.value.SaxonXMLGregorianCalendar;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.TimeValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlDate;
import org.apache.xmlbeans.XmlDecimal;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlLong;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlRuntimeException;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.XmlTokenSource;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.Cursor;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.xpath.XQuery;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class SaxonXQuery
implements XQuery {
    private static final Logger LOG = LogManager.getLogger(SaxonXQuery.class);
    private final XQueryExpression xquery;
    private final String contextVar;
    private final Configuration config;
    private Cur _cur;
    private long _version;
    private XmlOptions _options;

    public SaxonXQuery(String query, String contextVar, Integer boundary, XmlOptions xmlOptions) {
        assert (!contextVar.startsWith(".") && !contextVar.startsWith(".."));
        this._options = xmlOptions;
        this.config = new Configuration();
        StaticQueryContext sc = this.config.newStaticQueryContext();
        Map<String, String> nsMap = xmlOptions.getLoadAdditionalNamespaces();
        if (nsMap != null) {
            nsMap.forEach((arg_0, arg_1) -> ((StaticQueryContext)sc).declareNamespace(arg_0, arg_1));
        }
        this.contextVar = contextVar;
        try {
            this.xquery = sc.compileQuery(query.substring(0, boundary) + " declare variable $" + contextVar + " external;" + query.substring(boundary));
        }
        catch (TransformerException e) {
            throw new XmlRuntimeException(e);
        }
    }

    @Override
    public XmlObject[] objectExecute(Cur c, XmlOptions options) {
        this._version = c.getLocale().version();
        this._cur = c.weakCur(this);
        this._options = options;
        Map<String, Object> bindings = XmlOptions.maskNull(this._options).getXqueryVariables();
        List<Object> resultsList = this.execQuery(this._cur.getDom(), bindings);
        XmlObject[] result = new XmlObject[resultsList.size()];
        for (int i = 0; i < resultsList.size(); ++i) {
            Cur res;
            Locale l = Locale.getLocale(this._cur.getLocale().getSchemaTypeLoader(), this._options);
            l.enter();
            Object node = resultsList.get(i);
            try {
                if (!(node instanceof Node)) {
                    res = l.load("<xml-fragment/>").tempCur();
                    res.setValue(node.toString());
                    SchemaType type = this.getType(node);
                    Locale.autoTypeDocument(res, type, null);
                    result[i] = res.getObject();
                } else {
                    res = this.loadNode(l, (Node)node);
                }
                result[i] = res.getObject();
            }
            catch (XmlException e) {
                throw new RuntimeException(e);
            }
            finally {
                l.exit();
            }
            res.release();
        }
        this.release();
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlCursor cursorExecute(Cur c, XmlOptions options) {
        this._version = c.getLocale().version();
        this._cur = c.weakCur(this);
        this._options = options;
        Map<String, Object> bindings = XmlOptions.maskNull(this._options).getXqueryVariables();
        List<Object> resultsList = this.execQuery(this._cur.getDom(), bindings);
        Locale locale = Locale.getLocale(this._cur.getLocale().getSchemaTypeLoader(), this._options);
        locale.enter();
        Cur.CurLoadContext _context = new Cur.CurLoadContext(locale, this._options);
        Cursor resultCur = null;
        try {
            for (int i = 0; i < resultsList.size(); ++i) {
                this.loadNodeHelper(locale, (Node)resultsList.get(i), _context);
            }
            Cur c2 = ((Locale.LoadContext)_context).finish();
            Locale.associateSourceName(c, this._options);
            Locale.autoTypeDocument(c, null, this._options);
            resultCur = new Cursor(c2);
        }
        catch (XmlException e) {
            LOG.atInfo().withThrowable(e).log("Can't autotype document");
        }
        finally {
            locale.exit();
        }
        this.release();
        return resultCur;
    }

    public List<Object> execQuery(Object node, Map<String, Object> variableBindings) {
        try {
            Node contextNode = (Node)node;
            Document dom = contextNode.getNodeType() == 9 ? (Document)contextNode : contextNode.getOwnerDocument();
            DocumentWrapper docWrapper = new DocumentWrapper((Node)dom, null, this.config);
            DOMNodeWrapper root = docWrapper.wrap(contextNode);
            DynamicQueryContext dc = new DynamicQueryContext(this.config);
            dc.setContextItem((Item)root);
            dc.setParameter(new StructuredQName("", null, this.contextVar), (GroundedValue)root);
            if (variableBindings != null) {
                for (Map.Entry<String, Object> me : variableBindings.entrySet()) {
                    StructuredQName key = new StructuredQName("", null, me.getKey());
                    Object value = me.getValue();
                    if (value instanceof XmlTokenSource) {
                        Node paramObject = ((XmlTokenSource)value).getDomNode();
                        dc.setParameter(key, (GroundedValue)docWrapper.wrap(paramObject));
                        continue;
                    }
                    try {
                        dc.setParameter(key, (GroundedValue)SaxonXQuery.objectToItem(value, this.config));
                    }
                    catch (XPathException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            List saxonNodes = this.xquery.evaluate(dc);
            ListIterator<NodeOverNodeInfo> it = saxonNodes.listIterator();
            while (it.hasNext()) {
                Object o = it.next();
                if (!(o instanceof NodeInfo)) continue;
                NodeOverNodeInfo n = NodeOverNodeInfo.wrap((NodeInfo)((NodeInfo)o));
                it.set(n);
            }
            return saxonNodes;
        }
        catch (TransformerException e) {
            throw new RuntimeException("Error binding " + this.contextVar, e);
        }
    }

    private static Item objectToItem(Object value, Configuration config) throws XPathException, net.sf.saxon.trans.XPathException {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean) {
            return BooleanValue.get((boolean)((Boolean)value));
        }
        if (value instanceof byte[]) {
            return new HexBinaryValue((byte[])value);
        }
        if (value instanceof Byte) {
            return new Int64Value((long)((Byte)value).byteValue(), BuiltInAtomicType.BYTE, false);
        }
        if (value instanceof Float) {
            return new FloatValue(((Float)value).floatValue());
        }
        if (value instanceof Double) {
            return new DoubleValue(((Double)value).doubleValue());
        }
        if (value instanceof Integer) {
            return new Int64Value((long)((Integer)value).intValue(), BuiltInAtomicType.INT, false);
        }
        if (value instanceof Long) {
            return new Int64Value(((Long)value).longValue(), BuiltInAtomicType.LONG, false);
        }
        if (value instanceof Short) {
            return new Int64Value((long)((Short)value).shortValue(), BuiltInAtomicType.SHORT, false);
        }
        if (value instanceof String) {
            return new StringValue((String)value);
        }
        if (value instanceof BigDecimal) {
            return new BigDecimalValue((BigDecimal)value);
        }
        if (value instanceof BigInteger) {
            return new BigIntegerValue((BigInteger)value);
        }
        if (value instanceof SaxonDuration) {
            return ((SaxonDuration)value).getDurationValue();
        }
        if (value instanceof Duration) {
            Duration dv = (Duration)value;
            return new DurationValue(dv.getSign() >= 0, dv.getYears(), dv.getMonths(), dv.getDays(), dv.getHours(), dv.getMinutes(), (long)dv.getSeconds(), 0);
        }
        if (value instanceof SaxonXMLGregorianCalendar) {
            return ((SaxonXMLGregorianCalendar)value).toCalendarValue();
        }
        if (value instanceof XMLGregorianCalendar) {
            XMLGregorianCalendar g = (XMLGregorianCalendar)value;
            QName gtype = g.getXMLSchemaType();
            if (gtype.equals(DatatypeConstants.DATETIME)) {
                return DateTimeValue.makeDateTimeValue((UnicodeString)StringView.tidy((String)value.toString()), (ConversionRules)config.getConversionRules()).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.DATE)) {
                return DateValue.makeDateValue((UnicodeString)StringView.tidy((String)value.toString()), (ConversionRules)config.getConversionRules()).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.TIME)) {
                return TimeValue.makeTimeValue((UnicodeString)StringView.tidy((String)value.toString())).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.GYEAR)) {
                return GYearValue.makeGYearValue((UnicodeString)StringView.tidy((String)value.toString()), (ConversionRules)config.getConversionRules()).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.GYEARMONTH)) {
                return GYearMonthValue.makeGYearMonthValue((UnicodeString)StringView.tidy((String)value.toString()), (ConversionRules)config.getConversionRules()).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.GMONTH)) {
                String val = value.toString();
                if (val.endsWith("--")) {
                    val = val.substring(0, val.length() - 2);
                }
                return GMonthValue.makeGMonthValue((UnicodeString)StringView.tidy((String)val)).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.GMONTHDAY)) {
                return GMonthDayValue.makeGMonthDayValue((UnicodeString)StringView.tidy((String)value.toString())).asAtomic();
            }
            if (gtype.equals(DatatypeConstants.GDAY)) {
                return GDayValue.makeGDayValue((UnicodeString)StringView.tidy((String)value.toString())).asAtomic();
            }
            throw new AssertionError((Object)"Unknown Gregorian date type");
        }
        if (value instanceof QName) {
            QName q = (QName)value;
            return new QNameValue(q.getPrefix(), q.getNamespaceURI(), q.getLocalPart());
        }
        if (value instanceof URI) {
            return new AnyURIValue(value.toString());
        }
        if (value instanceof Map) {
            HashTrieMap htm = new HashTrieMap();
            for (Map.Entry me : ((Map)value).entrySet()) {
                htm.initialPut((AtomicValue)SaxonXQuery.objectToItem(me.getKey(), config), (GroundedValue)SaxonXQuery.objectToItem(me.getValue(), config));
            }
            return htm;
        }
        return new ObjectValue(value);
    }

    private SchemaType getType(Object node) {
        SchemaType type = node instanceof Integer ? XmlInteger.type : (node instanceof Double ? XmlDouble.type : (node instanceof Long ? XmlLong.type : (node instanceof Float ? XmlFloat.type : (node instanceof BigDecimal ? XmlDecimal.type : (node instanceof Boolean ? XmlBoolean.type : (node instanceof String ? XmlString.type : (node instanceof Date ? XmlDate.type : XmlAnySimpleType.type)))))));
        return type;
    }

    public void release() {
        if (this._cur != null) {
            this._cur.release();
            this._cur = null;
        }
    }

    private Cur loadNode(Locale locale, Node node) {
        Cur.CurLoadContext context = new Cur.CurLoadContext(locale, this._options);
        try {
            this.loadNodeHelper(locale, node, context);
            Cur c = ((Locale.LoadContext)context).finish();
            Locale.associateSourceName(c, this._options);
            Locale.autoTypeDocument(c, null, this._options);
            return c;
        }
        catch (Exception e) {
            throw new XmlRuntimeException(e.getMessage(), e);
        }
    }

    private void loadNodeHelper(Locale locale, Node node, Locale.LoadContext context) {
        if (node.getNodeType() == 2) {
            QName attName = new QName(node.getNamespaceURI(), node.getLocalName(), node.getPrefix());
            context.attr(attName, node.getNodeValue());
        } else {
            locale.loadNode(node, context);
        }
    }
}

