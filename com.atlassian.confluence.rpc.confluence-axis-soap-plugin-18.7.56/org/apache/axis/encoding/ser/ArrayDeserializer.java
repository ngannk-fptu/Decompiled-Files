/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.encoding.ser;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.xml.namespace.QName;
import org.apache.axis.Constants;
import org.apache.axis.components.logger.LogFactory;
import org.apache.axis.encoding.DeserializationContext;
import org.apache.axis.encoding.Deserializer;
import org.apache.axis.encoding.DeserializerImpl;
import org.apache.axis.encoding.DeserializerTarget;
import org.apache.axis.message.SOAPHandler;
import org.apache.axis.soap.SOAPConstants;
import org.apache.axis.utils.ClassUtils;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ArrayDeserializer
extends DeserializerImpl {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$encoding$ser$ArrayDeserializer == null ? (class$org$apache$axis$encoding$ser$ArrayDeserializer = ArrayDeserializer.class$("org.apache.axis.encoding.ser.ArrayDeserializer")) : class$org$apache$axis$encoding$ser$ArrayDeserializer).getName());
    public QName arrayType = null;
    public int curIndex = 0;
    QName defaultItemType;
    int length;
    Class arrayClass = null;
    ArrayList mDimLength = null;
    ArrayList mDimFactor = null;
    SOAPConstants soapConstants = SOAPConstants.SOAP11_CONSTANTS;
    static /* synthetic */ Class class$org$apache$axis$encoding$ser$ArrayDeserializer;

    public void onStartElement(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        Class destClass;
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ArrayDeserializer::startElement()");
        }
        this.soapConstants = context.getSOAPConstants();
        QName typeQName = context.getTypeFromAttributes(namespace, localName, attributes);
        if (typeQName == null) {
            typeQName = this.getDefaultType();
        }
        if (typeQName != null && Constants.equals(Constants.SOAP_ARRAY, typeQName)) {
            typeQName = null;
        }
        QName arrayTypeValue = context.getQNameFromString(Constants.getValue(attributes, Constants.URIS_SOAP_ENC, this.soapConstants.getAttrItemType()));
        String dimString = null;
        QName innerQName = null;
        String innerDimString = "";
        if (arrayTypeValue != null) {
            if (this.soapConstants != SOAPConstants.SOAP12_CONSTANTS) {
                String arrayTypeValueNamespaceURI = arrayTypeValue.getNamespaceURI();
                String arrayTypeValueLocalPart = arrayTypeValue.getLocalPart();
                int leftBracketIndex = arrayTypeValueLocalPart.lastIndexOf(91);
                int rightBracketIndex = arrayTypeValueLocalPart.lastIndexOf(93);
                if (leftBracketIndex == -1 || rightBracketIndex == -1 || rightBracketIndex < leftBracketIndex) {
                    throw new IllegalArgumentException(Messages.getMessage("badArrayType00", "" + arrayTypeValue));
                }
                dimString = arrayTypeValueLocalPart.substring(leftBracketIndex + 1, rightBracketIndex);
                if ((arrayTypeValueLocalPart = arrayTypeValueLocalPart.substring(0, leftBracketIndex)).endsWith("]")) {
                    this.defaultItemType = Constants.SOAP_ARRAY;
                    int bracket = arrayTypeValueLocalPart.indexOf("[");
                    innerQName = new QName(arrayTypeValueNamespaceURI, arrayTypeValueLocalPart.substring(0, bracket));
                    innerDimString = arrayTypeValueLocalPart.substring(bracket);
                } else {
                    this.defaultItemType = new QName(arrayTypeValueNamespaceURI, arrayTypeValueLocalPart);
                }
            } else {
                String arraySizeValue = attributes.getValue(this.soapConstants.getEncodingURI(), "arraySize");
                int leftStarIndex = arraySizeValue.lastIndexOf(42);
                if (leftStarIndex != -1) {
                    if (leftStarIndex != 0 || arraySizeValue.length() != 1) {
                        if (leftStarIndex == arraySizeValue.length() - 1) {
                            throw new IllegalArgumentException(Messages.getMessage("badArraySize00", "" + arraySizeValue));
                        }
                        dimString = arraySizeValue.substring(leftStarIndex + 2);
                        innerQName = arrayTypeValue;
                        innerDimString = arraySizeValue.substring(0, leftStarIndex + 1);
                    }
                } else {
                    dimString = arraySizeValue;
                }
                this.defaultItemType = innerDimString == null || innerDimString.length() == 0 ? arrayTypeValue : Constants.SOAP_ARRAY12;
            }
        }
        if (!(this.defaultItemType != null || typeQName != null || (destClass = context.getDestinationClass()) != null && destClass.isArray())) {
            this.defaultItemType = Constants.XSD_ANYTYPE;
        }
        this.arrayClass = null;
        if (typeQName != null) {
            this.arrayClass = context.getTypeMapping().getClassForQName(typeQName);
        }
        if (typeQName == null || this.arrayClass == null) {
            Class arrayItemClass = null;
            QName compQName = this.defaultItemType;
            String dims = "[]";
            if (innerQName != null) {
                compQName = innerQName;
                if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    int offset = 0;
                    while ((offset = innerDimString.indexOf(42, offset)) != -1) {
                        dims = dims + "[]";
                        ++offset;
                    }
                } else {
                    dims = dims + innerDimString;
                }
            }
            if ((arrayItemClass = context.getTypeMapping().getClassForQName(compQName)) != null) {
                try {
                    String loadableArrayClassName = JavaUtils.getLoadableClassName(JavaUtils.getTextClassName(arrayItemClass.getName()) + dims);
                    this.arrayClass = ClassUtils.forName(loadableArrayClassName, true, arrayItemClass.getClassLoader());
                }
                catch (Exception e) {
                    throw new SAXException(Messages.getMessage("noComponent00", "" + this.defaultItemType));
                }
            }
        }
        if (this.arrayClass == null) {
            this.arrayClass = context.getDestinationClass();
        }
        if (this.arrayClass == null) {
            throw new SAXException(Messages.getMessage("noComponent00", "" + this.defaultItemType));
        }
        if (dimString == null || dimString.length() == 0) {
            this.value = new ArrayListExtension(this.arrayClass);
        } else {
            try {
                StringTokenizer tokenizer = this.soapConstants == SOAPConstants.SOAP12_CONSTANTS ? new StringTokenizer(dimString) : new StringTokenizer(dimString, "[],");
                this.length = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    this.mDimLength = new ArrayList();
                    this.mDimLength.add(new Integer(this.length));
                    while (tokenizer.hasMoreTokens()) {
                        this.mDimLength.add(new Integer(Integer.parseInt(tokenizer.nextToken())));
                    }
                }
                ArrayListExtension list = new ArrayListExtension(this.arrayClass, this.length);
                this.value = list;
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(Messages.getMessage("badInteger00", dimString));
            }
        }
        String offset = Constants.getValue(attributes, Constants.URIS_SOAP_ENC, "offset");
        if (offset != null) {
            if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                throw new SAXException(Messages.getMessage("noSparseArray"));
            }
            int leftBracketIndex = offset.lastIndexOf(91);
            int rightBracketIndex = offset.lastIndexOf(93);
            if (leftBracketIndex == -1 || rightBracketIndex == -1 || rightBracketIndex < leftBracketIndex) {
                throw new SAXException(Messages.getMessage("badOffset00", offset));
            }
            this.curIndex = this.convertToIndex(offset.substring(leftBracketIndex + 1, rightBracketIndex), "badOffset00");
        }
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ArrayDeserializer::startElement()");
        }
    }

    public SOAPHandler onStartChild(String namespace, String localName, String prefix, Attributes attributes, DeserializationContext context) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)"Enter: ArrayDeserializer.onStartChild()");
        }
        if (attributes != null) {
            String pos = Constants.getValue(attributes, Constants.URIS_SOAP_ENC, "position");
            if (pos != null) {
                if (this.soapConstants == SOAPConstants.SOAP12_CONSTANTS) {
                    throw new SAXException(Messages.getMessage("noSparseArray"));
                }
                int leftBracketIndex = pos.lastIndexOf(91);
                int rightBracketIndex = pos.lastIndexOf(93);
                if (leftBracketIndex == -1 || rightBracketIndex == -1 || rightBracketIndex < leftBracketIndex) {
                    throw new SAXException(Messages.getMessage("badPosition00", pos));
                }
                this.curIndex = this.convertToIndex(pos.substring(leftBracketIndex + 1, rightBracketIndex), "badPosition00");
            }
            if (context.isNil(attributes)) {
                this.setChildValue(null, new Integer(this.curIndex++));
                return null;
            }
        }
        QName itemType = context.getTypeFromAttributes(namespace, localName, attributes);
        Deserializer dSer = null;
        if (itemType != null && context.getCurElement().getHref() == null) {
            dSer = context.getDeserializerForType(itemType);
        }
        if (dSer == null) {
            QName defaultType = this.defaultItemType;
            Class<?> javaType = null;
            if (this.arrayClass != null && this.arrayClass.isArray() && defaultType == null) {
                javaType = this.arrayClass.getComponentType();
                defaultType = context.getTypeMapping().getTypeQName(javaType);
            }
            if (itemType == null && dSer == null && defaultType != null && SchemaUtils.isSimpleSchemaType(defaultType)) {
                dSer = context.getDeserializer(javaType, defaultType);
            }
            if (dSer == null) {
                dSer = new DeserializerImpl();
                if (itemType == null) {
                    dSer.setDefaultType(defaultType);
                }
            }
        }
        dSer.registerValueTarget(new DeserializerTarget(this, new Integer(this.curIndex)));
        this.addChildDeserializer(dSer);
        ++this.curIndex;
        context.setDestinationClass(this.arrayClass.getComponentType());
        if (log.isDebugEnabled()) {
            log.debug((Object)"Exit: ArrayDeserializer.onStartChild()");
        }
        return (SOAPHandler)((Object)dSer);
    }

    public void onEndChild(String namespace, String localName, DeserializationContext context) throws SAXException {
        context.setDestinationClass(this.arrayClass);
    }

    public void characters(char[] chars, int i, int i1) throws SAXException {
        int idx = i;
        while (i < i1) {
            if (!Character.isWhitespace(chars[idx])) {
                throw new SAXException(Messages.getMessage("charsInArray"));
            }
            ++i;
        }
    }

    public void setChildValue(Object value, Object hint) throws SAXException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("Enter: ArrayDeserializer::setValue(" + value + ", " + hint + ")"));
        }
        ArrayList list = (ArrayList)this.value;
        int offset = (Integer)hint;
        if (this.mDimLength == null) {
            while (list.size() <= offset) {
                list.add(null);
            }
            list.set(offset, value);
        } else {
            ArrayList mDimIndex = this.toMultiIndex(offset);
            for (int i = 0; i < this.mDimLength.size(); ++i) {
                int length = (Integer)this.mDimLength.get(i);
                int index = (Integer)mDimIndex.get(i);
                while (list.size() < length) {
                    list.add(null);
                }
                if (i < this.mDimLength.size() - 1) {
                    if (list.get(index) == null) {
                        list.set(index, new ArrayList());
                    }
                    list = (ArrayList)list.get(index);
                    continue;
                }
                list.set(index, value);
            }
        }
    }

    public void valueComplete() throws SAXException {
        if (this.componentsReady()) {
            try {
                if (this.arrayClass != null) {
                    this.value = JavaUtils.convert(this.value, this.arrayClass);
                }
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        super.valueComplete();
    }

    private int convertToIndex(String text, String exceptKey) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(text, "[],");
        int index = 0;
        try {
            if (this.mDimLength == null) {
                index = Integer.parseInt(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    throw new SAXException(Messages.getMessage(exceptKey, text));
                }
            } else {
                int dim = -1;
                ArrayList<Integer> work = new ArrayList<Integer>();
                while (tokenizer.hasMoreTokens()) {
                    if (++dim >= this.mDimLength.size()) {
                        throw new SAXException(Messages.getMessage(exceptKey, text));
                    }
                    int workIndex = Integer.parseInt(tokenizer.nextToken());
                    if (workIndex < 0 || workIndex >= (Integer)this.mDimLength.get(dim)) {
                        throw new SAXException(Messages.getMessage(exceptKey, text));
                    }
                    work.add(new Integer(workIndex));
                }
                index = this.toSingleIndex(work);
            }
        }
        catch (SAXException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SAXException(Messages.getMessage(exceptKey, text));
        }
        return index;
    }

    private ArrayList toMultiIndex(int single) {
        if (this.mDimLength == null) {
            return null;
        }
        if (this.mDimFactor == null) {
            this.mDimFactor = new ArrayList();
            for (int i = 0; i < this.mDimLength.size(); ++i) {
                int factor = 1;
                for (int j = i + 1; j < this.mDimLength.size(); ++j) {
                    factor *= ((Integer)this.mDimLength.get(j)).intValue();
                }
                this.mDimFactor.add(new Integer(factor));
            }
        }
        ArrayList<Integer> rc = new ArrayList<Integer>();
        for (int i = 0; i < this.mDimLength.size(); ++i) {
            int factor = (Integer)this.mDimFactor.get(i);
            rc.add(new Integer(single / factor));
            single %= factor;
        }
        return rc;
    }

    private int toSingleIndex(ArrayList indexArray) {
        if (this.mDimLength == null || indexArray == null) {
            return -1;
        }
        if (this.mDimFactor == null) {
            this.mDimFactor = new ArrayList();
            for (int i = 0; i < this.mDimLength.size(); ++i) {
                int factor = 1;
                for (int j = i + 1; j < this.mDimLength.size(); ++j) {
                    factor *= ((Integer)this.mDimLength.get(j)).intValue();
                }
                this.mDimFactor.add(new Integer(factor));
            }
        }
        int single = 0;
        for (int i = 0; i < indexArray.size(); ++i) {
            single += (Integer)this.mDimFactor.get(i) * (Integer)indexArray.get(i);
        }
        return single;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public class ArrayListExtension
    extends ArrayList
    implements JavaUtils.ConvertCache {
        private HashMap table = null;
        private Class arrayClass = null;

        ArrayListExtension(Class arrayClass) {
            this.arrayClass = arrayClass;
            if (arrayClass == null || arrayClass.isInterface() || Modifier.isAbstract(arrayClass.getModifiers())) {
                arrayClass = null;
            }
        }

        ArrayListExtension(Class arrayClass, int length) {
            super(length > 50000 ? 50000 : length);
            this.arrayClass = arrayClass;
            if (arrayClass == null || arrayClass.isInterface() || Modifier.isAbstract(arrayClass.getModifiers())) {
                arrayClass = null;
            }
        }

        public void setConvertedValue(Class cls, Object value) {
            if (this.table == null) {
                this.table = new HashMap();
            }
            this.table.put(cls, value);
        }

        public Object getConvertedValue(Class cls) {
            if (this.table == null) {
                return null;
            }
            return this.table.get(cls);
        }

        public Class getDestClass() {
            return this.arrayClass;
        }
    }
}

