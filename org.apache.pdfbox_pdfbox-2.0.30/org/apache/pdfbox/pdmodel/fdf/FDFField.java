/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFIconFit;
import org.apache.pdfbox.pdmodel.fdf.FDFNamedPageReference;
import org.apache.pdfbox.pdmodel.fdf.FDFOptionElement;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.util.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FDFField
implements COSObjectable {
    private COSDictionary field;

    public FDFField() {
        this.field = new COSDictionary();
    }

    public FDFField(COSDictionary f) {
        this.field = f;
    }

    public FDFField(Element fieldXML) throws IOException {
        this();
        this.setPartialFieldName(fieldXML.getAttribute("name"));
        NodeList nodeList = fieldXML.getChildNodes();
        ArrayList<FDFField> kids = new ArrayList<FDFField>();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (!(node instanceof Element)) continue;
            Element child = (Element)node;
            if (child.getTagName().equals("value")) {
                this.setValue(XMLUtil.getNodeValue(child));
                continue;
            }
            if (child.getTagName().equals("value-richtext")) {
                this.setRichText(new COSString(XMLUtil.getNodeValue(child)));
                continue;
            }
            if (!child.getTagName().equals("field")) continue;
            kids.add(new FDFField(child));
        }
        if (kids.size() > 0) {
            this.setKids(kids);
        }
    }

    public void writeXML(Writer output) throws IOException {
        List<FDFField> kids;
        output.write("<field name=\"");
        output.write(this.getPartialFieldName());
        output.write("\">\n");
        Object value = this.getValue();
        if (value instanceof String) {
            output.write("<value>");
            output.write(this.escapeXML((String)value));
            output.write("</value>\n");
        } else if (value instanceof List) {
            List items = (List)value;
            for (String item : items) {
                output.write("<value>");
                output.write(this.escapeXML(item));
                output.write("</value>\n");
            }
        }
        String rt = this.getRichText();
        if (rt != null) {
            output.write("<value-richtext>");
            output.write(this.escapeXML(rt));
            output.write("</value-richtext>\n");
        }
        if ((kids = this.getKids()) != null) {
            for (FDFField kid : kids) {
                kid.writeXML(output);
            }
        }
        output.write("</field>\n");
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.field;
    }

    public List<FDFField> getKids() {
        COSArray kids = (COSArray)this.field.getDictionaryObject(COSName.KIDS);
        COSArrayList retval = null;
        if (kids != null) {
            ArrayList<FDFField> actuals = new ArrayList<FDFField>(kids.size());
            for (int i = 0; i < kids.size(); ++i) {
                actuals.add(new FDFField((COSDictionary)kids.getObject(i)));
            }
            retval = new COSArrayList(actuals, kids);
        }
        return retval;
    }

    public void setKids(List<FDFField> kids) {
        this.field.setItem(COSName.KIDS, (COSBase)COSArrayList.converterToCOSArray(kids));
    }

    public String getPartialFieldName() {
        return this.field.getString(COSName.T);
    }

    public void setPartialFieldName(String partial) {
        this.field.setString(COSName.T, partial);
    }

    public Object getValue() throws IOException {
        COSBase value = this.field.getDictionaryObject(COSName.V);
        if (value instanceof COSName) {
            return ((COSName)value).getName();
        }
        if (value instanceof COSArray) {
            return COSArrayList.convertCOSStringCOSArrayToList((COSArray)value);
        }
        if (value instanceof COSString) {
            return ((COSString)value).getString();
        }
        if (value instanceof COSStream) {
            return ((COSStream)value).toTextString();
        }
        if (value != null) {
            throw new IOException("Error:Unknown type for field import" + value);
        }
        return null;
    }

    public COSBase getCOSValue() throws IOException {
        COSBase value = this.field.getDictionaryObject(COSName.V);
        if (value instanceof COSName) {
            return value;
        }
        if (value instanceof COSArray) {
            return value;
        }
        if (value instanceof COSString || value instanceof COSStream) {
            return value;
        }
        if (value != null) {
            throw new IOException("Error:Unknown type for field import" + value);
        }
        return null;
    }

    public void setValue(Object value) throws IOException {
        COSBase cos = null;
        if (value instanceof List) {
            cos = COSArrayList.convertStringListToCOSStringCOSArray((List)value);
        } else if (value instanceof String) {
            cos = new COSString((String)value);
        } else if (value instanceof COSObjectable) {
            cos = ((COSObjectable)value).getCOSObject();
        } else if (value != null) {
            throw new IOException("Error:Unknown type for field import" + value);
        }
        this.field.setItem(COSName.V, cos);
    }

    public void setValue(COSBase value) {
        this.field.setItem(COSName.V, value);
    }

    public Integer getFieldFlags() {
        Integer retval = null;
        COSNumber ff = (COSNumber)this.field.getDictionaryObject(COSName.FF);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    public void setFieldFlags(Integer ff) {
        COSInteger value = null;
        if (ff != null) {
            value = COSInteger.get(ff.intValue());
        }
        this.field.setItem(COSName.FF, (COSBase)value);
    }

    public void setFieldFlags(int ff) {
        this.field.setInt(COSName.FF, ff);
    }

    public Integer getSetFieldFlags() {
        Integer retval = null;
        COSNumber ff = (COSNumber)this.field.getDictionaryObject(COSName.SET_FF);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    public void setSetFieldFlags(Integer ff) {
        COSInteger value = null;
        if (ff != null) {
            value = COSInteger.get(ff.intValue());
        }
        this.field.setItem(COSName.SET_FF, (COSBase)value);
    }

    public void setSetFieldFlags(int ff) {
        this.field.setInt(COSName.SET_FF, ff);
    }

    public Integer getClearFieldFlags() {
        Integer retval = null;
        COSNumber ff = (COSNumber)this.field.getDictionaryObject(COSName.CLR_FF);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    public void setClearFieldFlags(Integer ff) {
        COSInteger value = null;
        if (ff != null) {
            value = COSInteger.get(ff.intValue());
        }
        this.field.setItem(COSName.CLR_FF, (COSBase)value);
    }

    public void setClearFieldFlags(int ff) {
        this.field.setInt(COSName.CLR_FF, ff);
    }

    public Integer getWidgetFieldFlags() {
        Integer retval = null;
        COSNumber f = (COSNumber)this.field.getDictionaryObject("F");
        if (f != null) {
            retval = f.intValue();
        }
        return retval;
    }

    public void setWidgetFieldFlags(Integer f) {
        COSInteger value = null;
        if (f != null) {
            value = COSInteger.get(f.intValue());
        }
        this.field.setItem(COSName.F, (COSBase)value);
    }

    public void setWidgetFieldFlags(int f) {
        this.field.setInt(COSName.F, f);
    }

    public Integer getSetWidgetFieldFlags() {
        Integer retval = null;
        COSNumber ff = (COSNumber)this.field.getDictionaryObject(COSName.SET_F);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    public void setSetWidgetFieldFlags(Integer ff) {
        COSInteger value = null;
        if (ff != null) {
            value = COSInteger.get(ff.intValue());
        }
        this.field.setItem(COSName.SET_F, (COSBase)value);
    }

    public void setSetWidgetFieldFlags(int ff) {
        this.field.setInt(COSName.SET_F, ff);
    }

    public Integer getClearWidgetFieldFlags() {
        Integer retval = null;
        COSNumber ff = (COSNumber)this.field.getDictionaryObject(COSName.CLR_F);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    public void setClearWidgetFieldFlags(Integer ff) {
        COSInteger value = null;
        if (ff != null) {
            value = COSInteger.get(ff.intValue());
        }
        this.field.setItem(COSName.CLR_F, (COSBase)value);
    }

    public void setClearWidgetFieldFlags(int ff) {
        this.field.setInt(COSName.CLR_F, ff);
    }

    public PDAppearanceDictionary getAppearanceDictionary() {
        PDAppearanceDictionary retval = null;
        COSDictionary dict = (COSDictionary)this.field.getDictionaryObject(COSName.AP);
        if (dict != null) {
            retval = new PDAppearanceDictionary(dict);
        }
        return retval;
    }

    public void setAppearanceDictionary(PDAppearanceDictionary ap) {
        this.field.setItem(COSName.AP, (COSObjectable)ap);
    }

    public FDFNamedPageReference getAppearanceStreamReference() {
        FDFNamedPageReference retval = null;
        COSDictionary ref = (COSDictionary)this.field.getDictionaryObject(COSName.AP_REF);
        if (ref != null) {
            retval = new FDFNamedPageReference(ref);
        }
        return retval;
    }

    public void setAppearanceStreamReference(FDFNamedPageReference ref) {
        this.field.setItem(COSName.AP_REF, (COSObjectable)ref);
    }

    public FDFIconFit getIconFit() {
        FDFIconFit retval = null;
        COSDictionary dic = (COSDictionary)this.field.getDictionaryObject(COSName.IF);
        if (dic != null) {
            retval = new FDFIconFit(dic);
        }
        return retval;
    }

    public void setIconFit(FDFIconFit fit) {
        this.field.setItem(COSName.IF, (COSObjectable)fit);
    }

    public List<Object> getOptions() {
        COSArrayList retval = null;
        COSArray array = (COSArray)this.field.getDictionaryObject(COSName.OPT);
        if (array != null) {
            ArrayList<Object> objects = new ArrayList<Object>(array.size());
            for (int i = 0; i < array.size(); ++i) {
                COSBase next = array.getObject(i);
                if (next instanceof COSString) {
                    objects.add(((COSString)next).getString());
                    continue;
                }
                COSArray value = (COSArray)next;
                objects.add(new FDFOptionElement(value));
            }
            retval = new COSArrayList(objects, array);
        }
        return retval;
    }

    public void setOptions(List<Object> options) {
        COSArray value = COSArrayList.converterToCOSArray(options);
        this.field.setItem(COSName.OPT, (COSBase)value);
    }

    public PDAction getAction() {
        return PDActionFactory.createAction((COSDictionary)this.field.getDictionaryObject(COSName.A));
    }

    public void setAction(PDAction a) {
        this.field.setItem(COSName.A, (COSObjectable)a);
    }

    public PDAdditionalActions getAdditionalActions() {
        PDAdditionalActions retval = null;
        COSDictionary dict = (COSDictionary)this.field.getDictionaryObject(COSName.AA);
        if (dict != null) {
            retval = new PDAdditionalActions(dict);
        }
        return retval;
    }

    public void setAdditionalActions(PDAdditionalActions aa) {
        this.field.setItem(COSName.AA, (COSObjectable)aa);
    }

    public String getRichText() {
        COSBase rv = this.field.getDictionaryObject(COSName.RV);
        if (rv == null) {
            return null;
        }
        if (rv instanceof COSString) {
            return ((COSString)rv).getString();
        }
        return ((COSStream)rv).toTextString();
    }

    public void setRichText(COSString rv) {
        this.field.setItem(COSName.RV, (COSBase)rv);
    }

    public void setRichText(COSStream rv) {
        this.field.setItem(COSName.RV, (COSBase)rv);
    }

    private String escapeXML(String input) {
        StringBuilder escapedXML = new StringBuilder();
        block7: for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            switch (c) {
                case '<': {
                    escapedXML.append("&lt;");
                    continue block7;
                }
                case '>': {
                    escapedXML.append("&gt;");
                    continue block7;
                }
                case '\"': {
                    escapedXML.append("&quot;");
                    continue block7;
                }
                case '&': {
                    escapedXML.append("&amp;");
                    continue block7;
                }
                case '\'': {
                    escapedXML.append("&apos;");
                    continue block7;
                }
                default: {
                    if (c > '~') {
                        escapedXML.append("&#").append((int)c).append(';');
                        continue block7;
                    }
                    escapedXML.append(c);
                }
            }
        }
        return escapedXML.toString();
    }
}

