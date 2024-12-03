/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.microsoft.schemas.office.office.CTCallout
 *  com.microsoft.schemas.office.office.CTClipPath
 *  com.microsoft.schemas.office.office.CTEquationXml
 *  com.microsoft.schemas.office.office.CTExtrusion
 *  com.microsoft.schemas.office.office.CTInk
 *  com.microsoft.schemas.office.office.CTSkew
 *  com.microsoft.schemas.office.office.STDiagramLayout
 *  com.microsoft.schemas.office.powerpoint.CTEmpty
 *  com.microsoft.schemas.office.powerpoint.CTRel
 *  com.microsoft.schemas.office.word.CTBorder
 */
package com.microsoft.schemas.vml.impl;

import com.microsoft.schemas.office.excel.CTClientData;
import com.microsoft.schemas.office.office.CTCallout;
import com.microsoft.schemas.office.office.CTClipPath;
import com.microsoft.schemas.office.office.CTEquationXml;
import com.microsoft.schemas.office.office.CTExtrusion;
import com.microsoft.schemas.office.office.CTInk;
import com.microsoft.schemas.office.office.CTLock;
import com.microsoft.schemas.office.office.CTSignatureLine;
import com.microsoft.schemas.office.office.CTSkew;
import com.microsoft.schemas.office.office.STBWMode;
import com.microsoft.schemas.office.office.STConnectorType;
import com.microsoft.schemas.office.office.STDiagramLayout;
import com.microsoft.schemas.office.office.STHrAlign;
import com.microsoft.schemas.office.office.STInsetMode;
import com.microsoft.schemas.office.powerpoint.CTEmpty;
import com.microsoft.schemas.office.powerpoint.CTRel;
import com.microsoft.schemas.office.word.CTAnchorLock;
import com.microsoft.schemas.office.word.CTBorder;
import com.microsoft.schemas.office.word.CTWrap;
import com.microsoft.schemas.vml.CTFill;
import com.microsoft.schemas.vml.CTFormulas;
import com.microsoft.schemas.vml.CTHandles;
import com.microsoft.schemas.vml.CTImageData;
import com.microsoft.schemas.vml.CTPath;
import com.microsoft.schemas.vml.CTShadow;
import com.microsoft.schemas.vml.CTShape;
import com.microsoft.schemas.vml.CTStroke;
import com.microsoft.schemas.vml.CTTextPath;
import com.microsoft.schemas.vml.CTTextbox;
import java.math.BigInteger;
import java.util.List;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.XmlBase64Binary;
import org.apache.xmlbeans.XmlFloat;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.values.JavaListXmlObject;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STColorType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalseBlank;

public class CTShapeImpl
extends XmlComplexContentImpl
implements CTShape {
    private static final long serialVersionUID = 1L;
    private static final QName[] PROPERTY_QNAME = new QName[]{new QName("urn:schemas-microsoft-com:vml", "path"), new QName("urn:schemas-microsoft-com:vml", "formulas"), new QName("urn:schemas-microsoft-com:vml", "handles"), new QName("urn:schemas-microsoft-com:vml", "fill"), new QName("urn:schemas-microsoft-com:vml", "stroke"), new QName("urn:schemas-microsoft-com:vml", "shadow"), new QName("urn:schemas-microsoft-com:vml", "textbox"), new QName("urn:schemas-microsoft-com:vml", "textpath"), new QName("urn:schemas-microsoft-com:vml", "imagedata"), new QName("urn:schemas-microsoft-com:office:office", "skew"), new QName("urn:schemas-microsoft-com:office:office", "extrusion"), new QName("urn:schemas-microsoft-com:office:office", "callout"), new QName("urn:schemas-microsoft-com:office:office", "lock"), new QName("urn:schemas-microsoft-com:office:office", "clippath"), new QName("urn:schemas-microsoft-com:office:office", "signatureline"), new QName("urn:schemas-microsoft-com:office:word", "wrap"), new QName("urn:schemas-microsoft-com:office:word", "anchorlock"), new QName("urn:schemas-microsoft-com:office:word", "bordertop"), new QName("urn:schemas-microsoft-com:office:word", "borderbottom"), new QName("urn:schemas-microsoft-com:office:word", "borderleft"), new QName("urn:schemas-microsoft-com:office:word", "borderright"), new QName("urn:schemas-microsoft-com:office:excel", "ClientData"), new QName("urn:schemas-microsoft-com:office:powerpoint", "textdata"), new QName("urn:schemas-microsoft-com:office:office", "ink"), new QName("urn:schemas-microsoft-com:office:powerpoint", "iscomment"), new QName("urn:schemas-microsoft-com:office:office", "equationxml"), new QName("", "id"), new QName("", "style"), new QName("", "href"), new QName("", "target"), new QName("", "class"), new QName("", "title"), new QName("", "alt"), new QName("", "coordsize"), new QName("", "coordorigin"), new QName("", "wrapcoords"), new QName("", "print"), new QName("urn:schemas-microsoft-com:office:office", "spid"), new QName("urn:schemas-microsoft-com:office:office", "oned"), new QName("urn:schemas-microsoft-com:office:office", "regroupid"), new QName("urn:schemas-microsoft-com:office:office", "doubleclicknotify"), new QName("urn:schemas-microsoft-com:office:office", "button"), new QName("urn:schemas-microsoft-com:office:office", "userhidden"), new QName("urn:schemas-microsoft-com:office:office", "bullet"), new QName("urn:schemas-microsoft-com:office:office", "hr"), new QName("urn:schemas-microsoft-com:office:office", "hrstd"), new QName("urn:schemas-microsoft-com:office:office", "hrnoshade"), new QName("urn:schemas-microsoft-com:office:office", "hrpct"), new QName("urn:schemas-microsoft-com:office:office", "hralign"), new QName("urn:schemas-microsoft-com:office:office", "allowincell"), new QName("urn:schemas-microsoft-com:office:office", "allowoverlap"), new QName("urn:schemas-microsoft-com:office:office", "userdrawn"), new QName("urn:schemas-microsoft-com:office:office", "bordertopcolor"), new QName("urn:schemas-microsoft-com:office:office", "borderleftcolor"), new QName("urn:schemas-microsoft-com:office:office", "borderbottomcolor"), new QName("urn:schemas-microsoft-com:office:office", "borderrightcolor"), new QName("urn:schemas-microsoft-com:office:office", "dgmlayout"), new QName("urn:schemas-microsoft-com:office:office", "dgmnodekind"), new QName("urn:schemas-microsoft-com:office:office", "dgmlayoutmru"), new QName("urn:schemas-microsoft-com:office:office", "insetmode"), new QName("", "chromakey"), new QName("", "filled"), new QName("", "fillcolor"), new QName("", "opacity"), new QName("", "stroked"), new QName("", "strokecolor"), new QName("", "strokeweight"), new QName("", "insetpen"), new QName("urn:schemas-microsoft-com:office:office", "spt"), new QName("urn:schemas-microsoft-com:office:office", "connectortype"), new QName("urn:schemas-microsoft-com:office:office", "bwmode"), new QName("urn:schemas-microsoft-com:office:office", "bwpure"), new QName("urn:schemas-microsoft-com:office:office", "bwnormal"), new QName("urn:schemas-microsoft-com:office:office", "forcedash"), new QName("urn:schemas-microsoft-com:office:office", "oleicon"), new QName("urn:schemas-microsoft-com:office:office", "ole"), new QName("urn:schemas-microsoft-com:office:office", "preferrelative"), new QName("urn:schemas-microsoft-com:office:office", "cliptowrap"), new QName("urn:schemas-microsoft-com:office:office", "clip"), new QName("", "type"), new QName("", "adj"), new QName("", "path"), new QName("urn:schemas-microsoft-com:office:office", "gfxdata"), new QName("", "equationxml")};

    public CTShapeImpl(SchemaType sType) {
        super(sType);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTPath> getPathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTPath>(this::getPathArray, this::setPathArray, this::insertNewPath, this::removePath, this::sizeOfPathArray);
        }
    }

    @Override
    public CTPath[] getPathArray() {
        return (CTPath[])this.getXmlObjectArray(PROPERTY_QNAME[0], new CTPath[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPath getPathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPath target = null;
            target = (CTPath)((Object)this.get_store().find_element_user(PROPERTY_QNAME[0], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfPathArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[0]);
        }
    }

    @Override
    public void setPathArray(CTPath[] pathArray) {
        this.check_orphaned();
        this.arraySetterHelper(pathArray, PROPERTY_QNAME[0]);
    }

    @Override
    public void setPathArray(int i, CTPath path) {
        this.generatedSetterHelperImpl(path, PROPERTY_QNAME[0], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPath insertNewPath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPath target = null;
            target = (CTPath)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[0], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTPath addNewPath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTPath target = null;
            target = (CTPath)((Object)this.get_store().add_element_user(PROPERTY_QNAME[0]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removePath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[0], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFormulas> getFormulasList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFormulas>(this::getFormulasArray, this::setFormulasArray, this::insertNewFormulas, this::removeFormulas, this::sizeOfFormulasArray);
        }
    }

    @Override
    public CTFormulas[] getFormulasArray() {
        return (CTFormulas[])this.getXmlObjectArray(PROPERTY_QNAME[1], new CTFormulas[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFormulas getFormulasArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFormulas target = null;
            target = (CTFormulas)((Object)this.get_store().find_element_user(PROPERTY_QNAME[1], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfFormulasArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[1]);
        }
    }

    @Override
    public void setFormulasArray(CTFormulas[] formulasArray) {
        this.check_orphaned();
        this.arraySetterHelper(formulasArray, PROPERTY_QNAME[1]);
    }

    @Override
    public void setFormulasArray(int i, CTFormulas formulas) {
        this.generatedSetterHelperImpl(formulas, PROPERTY_QNAME[1], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFormulas insertNewFormulas(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFormulas target = null;
            target = (CTFormulas)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[1], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFormulas addNewFormulas() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFormulas target = null;
            target = (CTFormulas)((Object)this.get_store().add_element_user(PROPERTY_QNAME[1]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFormulas(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[1], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTHandles> getHandlesList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTHandles>(this::getHandlesArray, this::setHandlesArray, this::insertNewHandles, this::removeHandles, this::sizeOfHandlesArray);
        }
    }

    @Override
    public CTHandles[] getHandlesArray() {
        return (CTHandles[])this.getXmlObjectArray(PROPERTY_QNAME[2], new CTHandles[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHandles getHandlesArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHandles target = null;
            target = (CTHandles)((Object)this.get_store().find_element_user(PROPERTY_QNAME[2], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfHandlesArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[2]);
        }
    }

    @Override
    public void setHandlesArray(CTHandles[] handlesArray) {
        this.check_orphaned();
        this.arraySetterHelper(handlesArray, PROPERTY_QNAME[2]);
    }

    @Override
    public void setHandlesArray(int i, CTHandles handles) {
        this.generatedSetterHelperImpl(handles, PROPERTY_QNAME[2], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHandles insertNewHandles(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHandles target = null;
            target = (CTHandles)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[2], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTHandles addNewHandles() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTHandles target = null;
            target = (CTHandles)((Object)this.get_store().add_element_user(PROPERTY_QNAME[2]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeHandles(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[2], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTFill> getFillList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTFill>(this::getFillArray, this::setFillArray, this::insertNewFill, this::removeFill, this::sizeOfFillArray);
        }
    }

    @Override
    public CTFill[] getFillArray() {
        return (CTFill[])this.getXmlObjectArray(PROPERTY_QNAME[3], new CTFill[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFill getFillArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFill target = null;
            target = (CTFill)((Object)this.get_store().find_element_user(PROPERTY_QNAME[3], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfFillArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[3]);
        }
    }

    @Override
    public void setFillArray(CTFill[] fillArray) {
        this.check_orphaned();
        this.arraySetterHelper(fillArray, PROPERTY_QNAME[3]);
    }

    @Override
    public void setFillArray(int i, CTFill fill) {
        this.generatedSetterHelperImpl(fill, PROPERTY_QNAME[3], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFill insertNewFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFill target = null;
            target = (CTFill)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[3], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTFill addNewFill() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTFill target = null;
            target = (CTFill)((Object)this.get_store().add_element_user(PROPERTY_QNAME[3]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeFill(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[3], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTStroke> getStrokeList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTStroke>(this::getStrokeArray, this::setStrokeArray, this::insertNewStroke, this::removeStroke, this::sizeOfStrokeArray);
        }
    }

    @Override
    public CTStroke[] getStrokeArray() {
        return (CTStroke[])this.getXmlObjectArray(PROPERTY_QNAME[4], new CTStroke[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStroke getStrokeArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStroke target = null;
            target = (CTStroke)((Object)this.get_store().find_element_user(PROPERTY_QNAME[4], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfStrokeArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[4]);
        }
    }

    @Override
    public void setStrokeArray(CTStroke[] strokeArray) {
        this.check_orphaned();
        this.arraySetterHelper(strokeArray, PROPERTY_QNAME[4]);
    }

    @Override
    public void setStrokeArray(int i, CTStroke stroke) {
        this.generatedSetterHelperImpl(stroke, PROPERTY_QNAME[4], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStroke insertNewStroke(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStroke target = null;
            target = (CTStroke)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[4], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTStroke addNewStroke() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTStroke target = null;
            target = (CTStroke)((Object)this.get_store().add_element_user(PROPERTY_QNAME[4]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeStroke(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[4], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTShadow> getShadowList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTShadow>(this::getShadowArray, this::setShadowArray, this::insertNewShadow, this::removeShadow, this::sizeOfShadowArray);
        }
    }

    @Override
    public CTShadow[] getShadowArray() {
        return (CTShadow[])this.getXmlObjectArray(PROPERTY_QNAME[5], new CTShadow[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShadow getShadowArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShadow target = null;
            target = (CTShadow)((Object)this.get_store().find_element_user(PROPERTY_QNAME[5], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfShadowArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[5]);
        }
    }

    @Override
    public void setShadowArray(CTShadow[] shadowArray) {
        this.check_orphaned();
        this.arraySetterHelper(shadowArray, PROPERTY_QNAME[5]);
    }

    @Override
    public void setShadowArray(int i, CTShadow shadow) {
        this.generatedSetterHelperImpl(shadow, PROPERTY_QNAME[5], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShadow insertNewShadow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShadow target = null;
            target = (CTShadow)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[5], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTShadow addNewShadow() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTShadow target = null;
            target = (CTShadow)((Object)this.get_store().add_element_user(PROPERTY_QNAME[5]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeShadow(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[5], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTextbox> getTextboxList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextbox>(this::getTextboxArray, this::setTextboxArray, this::insertNewTextbox, this::removeTextbox, this::sizeOfTextboxArray);
        }
    }

    @Override
    public CTTextbox[] getTextboxArray() {
        return (CTTextbox[])this.getXmlObjectArray(PROPERTY_QNAME[6], new CTTextbox[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextbox getTextboxArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextbox target = null;
            target = (CTTextbox)((Object)this.get_store().find_element_user(PROPERTY_QNAME[6], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTextboxArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[6]);
        }
    }

    @Override
    public void setTextboxArray(CTTextbox[] textboxArray) {
        this.check_orphaned();
        this.arraySetterHelper(textboxArray, PROPERTY_QNAME[6]);
    }

    @Override
    public void setTextboxArray(int i, CTTextbox textbox) {
        this.generatedSetterHelperImpl(textbox, PROPERTY_QNAME[6], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextbox insertNewTextbox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextbox target = null;
            target = (CTTextbox)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[6], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextbox addNewTextbox() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextbox target = null;
            target = (CTTextbox)((Object)this.get_store().add_element_user(PROPERTY_QNAME[6]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextbox(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[6], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTTextPath> getTextpathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTTextPath>(this::getTextpathArray, this::setTextpathArray, this::insertNewTextpath, this::removeTextpath, this::sizeOfTextpathArray);
        }
    }

    @Override
    public CTTextPath[] getTextpathArray() {
        return (CTTextPath[])this.getXmlObjectArray(PROPERTY_QNAME[7], new CTTextPath[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextPath getTextpathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextPath target = null;
            target = (CTTextPath)((Object)this.get_store().find_element_user(PROPERTY_QNAME[7], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTextpathArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[7]);
        }
    }

    @Override
    public void setTextpathArray(CTTextPath[] textpathArray) {
        this.check_orphaned();
        this.arraySetterHelper(textpathArray, PROPERTY_QNAME[7]);
    }

    @Override
    public void setTextpathArray(int i, CTTextPath textpath) {
        this.generatedSetterHelperImpl(textpath, PROPERTY_QNAME[7], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextPath insertNewTextpath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextPath target = null;
            target = (CTTextPath)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[7], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTTextPath addNewTextpath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTTextPath target = null;
            target = (CTTextPath)((Object)this.get_store().add_element_user(PROPERTY_QNAME[7]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextpath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[7], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTImageData> getImagedataList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTImageData>(this::getImagedataArray, this::setImagedataArray, this::insertNewImagedata, this::removeImagedata, this::sizeOfImagedataArray);
        }
    }

    @Override
    public CTImageData[] getImagedataArray() {
        return (CTImageData[])this.getXmlObjectArray(PROPERTY_QNAME[8], new CTImageData[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTImageData getImagedataArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTImageData target = null;
            target = (CTImageData)((Object)this.get_store().find_element_user(PROPERTY_QNAME[8], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfImagedataArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[8]);
        }
    }

    @Override
    public void setImagedataArray(CTImageData[] imagedataArray) {
        this.check_orphaned();
        this.arraySetterHelper(imagedataArray, PROPERTY_QNAME[8]);
    }

    @Override
    public void setImagedataArray(int i, CTImageData imagedata) {
        this.generatedSetterHelperImpl(imagedata, PROPERTY_QNAME[8], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTImageData insertNewImagedata(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTImageData target = null;
            target = (CTImageData)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[8], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTImageData addNewImagedata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTImageData target = null;
            target = (CTImageData)((Object)this.get_store().add_element_user(PROPERTY_QNAME[8]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeImagedata(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[8], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSkew> getSkewList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSkew>(this::getSkewArray, this::setSkewArray, this::insertNewSkew, this::removeSkew, this::sizeOfSkewArray);
        }
    }

    @Override
    public CTSkew[] getSkewArray() {
        return (CTSkew[])this.getXmlObjectArray(PROPERTY_QNAME[9], (XmlObject[])new CTSkew[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSkew getSkewArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSkew target = null;
            target = (CTSkew)this.get_store().find_element_user(PROPERTY_QNAME[9], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSkewArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[9]);
        }
    }

    @Override
    public void setSkewArray(CTSkew[] skewArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])skewArray, PROPERTY_QNAME[9]);
    }

    @Override
    public void setSkewArray(int i, CTSkew skew) {
        this.generatedSetterHelperImpl((XmlObject)skew, PROPERTY_QNAME[9], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSkew insertNewSkew(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSkew target = null;
            target = (CTSkew)this.get_store().insert_element_user(PROPERTY_QNAME[9], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSkew addNewSkew() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSkew target = null;
            target = (CTSkew)this.get_store().add_element_user(PROPERTY_QNAME[9]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSkew(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[9], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTExtrusion> getExtrusionList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTExtrusion>(this::getExtrusionArray, this::setExtrusionArray, this::insertNewExtrusion, this::removeExtrusion, this::sizeOfExtrusionArray);
        }
    }

    @Override
    public CTExtrusion[] getExtrusionArray() {
        return (CTExtrusion[])this.getXmlObjectArray(PROPERTY_QNAME[10], (XmlObject[])new CTExtrusion[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtrusion getExtrusionArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtrusion target = null;
            target = (CTExtrusion)this.get_store().find_element_user(PROPERTY_QNAME[10], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfExtrusionArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[10]);
        }
    }

    @Override
    public void setExtrusionArray(CTExtrusion[] extrusionArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])extrusionArray, PROPERTY_QNAME[10]);
    }

    @Override
    public void setExtrusionArray(int i, CTExtrusion extrusion) {
        this.generatedSetterHelperImpl((XmlObject)extrusion, PROPERTY_QNAME[10], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtrusion insertNewExtrusion(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtrusion target = null;
            target = (CTExtrusion)this.get_store().insert_element_user(PROPERTY_QNAME[10], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTExtrusion addNewExtrusion() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTExtrusion target = null;
            target = (CTExtrusion)this.get_store().add_element_user(PROPERTY_QNAME[10]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeExtrusion(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[10], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTCallout> getCalloutList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTCallout>(this::getCalloutArray, this::setCalloutArray, this::insertNewCallout, this::removeCallout, this::sizeOfCalloutArray);
        }
    }

    @Override
    public CTCallout[] getCalloutArray() {
        return (CTCallout[])this.getXmlObjectArray(PROPERTY_QNAME[11], (XmlObject[])new CTCallout[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCallout getCalloutArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCallout target = null;
            target = (CTCallout)this.get_store().find_element_user(PROPERTY_QNAME[11], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfCalloutArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[11]);
        }
    }

    @Override
    public void setCalloutArray(CTCallout[] calloutArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])calloutArray, PROPERTY_QNAME[11]);
    }

    @Override
    public void setCalloutArray(int i, CTCallout callout) {
        this.generatedSetterHelperImpl((XmlObject)callout, PROPERTY_QNAME[11], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCallout insertNewCallout(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCallout target = null;
            target = (CTCallout)this.get_store().insert_element_user(PROPERTY_QNAME[11], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTCallout addNewCallout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTCallout target = null;
            target = (CTCallout)this.get_store().add_element_user(PROPERTY_QNAME[11]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeCallout(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[11], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTLock> getLockList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTLock>(this::getLockArray, this::setLockArray, this::insertNewLock, this::removeLock, this::sizeOfLockArray);
        }
    }

    @Override
    public CTLock[] getLockArray() {
        return (CTLock[])this.getXmlObjectArray(PROPERTY_QNAME[12], new CTLock[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLock getLockArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLock target = null;
            target = (CTLock)((Object)this.get_store().find_element_user(PROPERTY_QNAME[12], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfLockArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[12]);
        }
    }

    @Override
    public void setLockArray(CTLock[] lockArray) {
        this.check_orphaned();
        this.arraySetterHelper(lockArray, PROPERTY_QNAME[12]);
    }

    @Override
    public void setLockArray(int i, CTLock lock) {
        this.generatedSetterHelperImpl(lock, PROPERTY_QNAME[12], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLock insertNewLock(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLock target = null;
            target = (CTLock)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[12], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTLock addNewLock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTLock target = null;
            target = (CTLock)((Object)this.get_store().add_element_user(PROPERTY_QNAME[12]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeLock(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[12], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTClipPath> getClippathList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTClipPath>(this::getClippathArray, this::setClippathArray, this::insertNewClippath, this::removeClippath, this::sizeOfClippathArray);
        }
    }

    @Override
    public CTClipPath[] getClippathArray() {
        return (CTClipPath[])this.getXmlObjectArray(PROPERTY_QNAME[13], (XmlObject[])new CTClipPath[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClipPath getClippathArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClipPath target = null;
            target = (CTClipPath)this.get_store().find_element_user(PROPERTY_QNAME[13], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfClippathArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[13]);
        }
    }

    @Override
    public void setClippathArray(CTClipPath[] clippathArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])clippathArray, PROPERTY_QNAME[13]);
    }

    @Override
    public void setClippathArray(int i, CTClipPath clippath) {
        this.generatedSetterHelperImpl((XmlObject)clippath, PROPERTY_QNAME[13], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClipPath insertNewClippath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClipPath target = null;
            target = (CTClipPath)this.get_store().insert_element_user(PROPERTY_QNAME[13], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClipPath addNewClippath() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClipPath target = null;
            target = (CTClipPath)this.get_store().add_element_user(PROPERTY_QNAME[13]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeClippath(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[13], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTSignatureLine> getSignaturelineList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTSignatureLine>(this::getSignaturelineArray, this::setSignaturelineArray, this::insertNewSignatureline, this::removeSignatureline, this::sizeOfSignaturelineArray);
        }
    }

    @Override
    public CTSignatureLine[] getSignaturelineArray() {
        return (CTSignatureLine[])this.getXmlObjectArray(PROPERTY_QNAME[14], new CTSignatureLine[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignatureLine getSignaturelineArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignatureLine target = null;
            target = (CTSignatureLine)((Object)this.get_store().find_element_user(PROPERTY_QNAME[14], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfSignaturelineArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[14]);
        }
    }

    @Override
    public void setSignaturelineArray(CTSignatureLine[] signaturelineArray) {
        this.check_orphaned();
        this.arraySetterHelper(signaturelineArray, PROPERTY_QNAME[14]);
    }

    @Override
    public void setSignaturelineArray(int i, CTSignatureLine signatureline) {
        this.generatedSetterHelperImpl(signatureline, PROPERTY_QNAME[14], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignatureLine insertNewSignatureline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignatureLine target = null;
            target = (CTSignatureLine)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[14], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTSignatureLine addNewSignatureline() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTSignatureLine target = null;
            target = (CTSignatureLine)((Object)this.get_store().add_element_user(PROPERTY_QNAME[14]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeSignatureline(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[14], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTWrap> getWrapList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTWrap>(this::getWrapArray, this::setWrapArray, this::insertNewWrap, this::removeWrap, this::sizeOfWrapArray);
        }
    }

    @Override
    public CTWrap[] getWrapArray() {
        return (CTWrap[])this.getXmlObjectArray(PROPERTY_QNAME[15], new CTWrap[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWrap getWrapArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWrap target = null;
            target = (CTWrap)((Object)this.get_store().find_element_user(PROPERTY_QNAME[15], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfWrapArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[15]);
        }
    }

    @Override
    public void setWrapArray(CTWrap[] wrapArray) {
        this.check_orphaned();
        this.arraySetterHelper(wrapArray, PROPERTY_QNAME[15]);
    }

    @Override
    public void setWrapArray(int i, CTWrap wrap) {
        this.generatedSetterHelperImpl(wrap, PROPERTY_QNAME[15], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWrap insertNewWrap(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWrap target = null;
            target = (CTWrap)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[15], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTWrap addNewWrap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTWrap target = null;
            target = (CTWrap)((Object)this.get_store().add_element_user(PROPERTY_QNAME[15]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeWrap(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[15], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTAnchorLock> getAnchorlockList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTAnchorLock>(this::getAnchorlockArray, this::setAnchorlockArray, this::insertNewAnchorlock, this::removeAnchorlock, this::sizeOfAnchorlockArray);
        }
    }

    @Override
    public CTAnchorLock[] getAnchorlockArray() {
        return (CTAnchorLock[])this.getXmlObjectArray(PROPERTY_QNAME[16], new CTAnchorLock[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchorLock getAnchorlockArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchorLock target = null;
            target = (CTAnchorLock)((Object)this.get_store().find_element_user(PROPERTY_QNAME[16], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfAnchorlockArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[16]);
        }
    }

    @Override
    public void setAnchorlockArray(CTAnchorLock[] anchorlockArray) {
        this.check_orphaned();
        this.arraySetterHelper(anchorlockArray, PROPERTY_QNAME[16]);
    }

    @Override
    public void setAnchorlockArray(int i, CTAnchorLock anchorlock) {
        this.generatedSetterHelperImpl(anchorlock, PROPERTY_QNAME[16], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchorLock insertNewAnchorlock(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchorLock target = null;
            target = (CTAnchorLock)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[16], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTAnchorLock addNewAnchorlock() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTAnchorLock target = null;
            target = (CTAnchorLock)((Object)this.get_store().add_element_user(PROPERTY_QNAME[16]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAnchorlock(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[16], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBorder> getBordertopList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorder>(this::getBordertopArray, this::setBordertopArray, this::insertNewBordertop, this::removeBordertop, this::sizeOfBordertopArray);
        }
    }

    @Override
    public CTBorder[] getBordertopArray() {
        return (CTBorder[])this.getXmlObjectArray(PROPERTY_QNAME[17], (XmlObject[])new CTBorder[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder getBordertopArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().find_element_user(PROPERTY_QNAME[17], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBordertopArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[17]);
        }
    }

    @Override
    public void setBordertopArray(CTBorder[] bordertopArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])bordertopArray, PROPERTY_QNAME[17]);
    }

    @Override
    public void setBordertopArray(int i, CTBorder bordertop) {
        this.generatedSetterHelperImpl((XmlObject)bordertop, PROPERTY_QNAME[17], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder insertNewBordertop(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().insert_element_user(PROPERTY_QNAME[17], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder addNewBordertop() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().add_element_user(PROPERTY_QNAME[17]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBordertop(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[17], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBorder> getBorderbottomList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorder>(this::getBorderbottomArray, this::setBorderbottomArray, this::insertNewBorderbottom, this::removeBorderbottom, this::sizeOfBorderbottomArray);
        }
    }

    @Override
    public CTBorder[] getBorderbottomArray() {
        return (CTBorder[])this.getXmlObjectArray(PROPERTY_QNAME[18], (XmlObject[])new CTBorder[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder getBorderbottomArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().find_element_user(PROPERTY_QNAME[18], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBorderbottomArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[18]);
        }
    }

    @Override
    public void setBorderbottomArray(CTBorder[] borderbottomArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])borderbottomArray, PROPERTY_QNAME[18]);
    }

    @Override
    public void setBorderbottomArray(int i, CTBorder borderbottom) {
        this.generatedSetterHelperImpl((XmlObject)borderbottom, PROPERTY_QNAME[18], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder insertNewBorderbottom(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().insert_element_user(PROPERTY_QNAME[18], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder addNewBorderbottom() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().add_element_user(PROPERTY_QNAME[18]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBorderbottom(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[18], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBorder> getBorderleftList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorder>(this::getBorderleftArray, this::setBorderleftArray, this::insertNewBorderleft, this::removeBorderleft, this::sizeOfBorderleftArray);
        }
    }

    @Override
    public CTBorder[] getBorderleftArray() {
        return (CTBorder[])this.getXmlObjectArray(PROPERTY_QNAME[19], (XmlObject[])new CTBorder[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder getBorderleftArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().find_element_user(PROPERTY_QNAME[19], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBorderleftArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[19]);
        }
    }

    @Override
    public void setBorderleftArray(CTBorder[] borderleftArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])borderleftArray, PROPERTY_QNAME[19]);
    }

    @Override
    public void setBorderleftArray(int i, CTBorder borderleft) {
        this.generatedSetterHelperImpl((XmlObject)borderleft, PROPERTY_QNAME[19], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder insertNewBorderleft(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().insert_element_user(PROPERTY_QNAME[19], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder addNewBorderleft() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().add_element_user(PROPERTY_QNAME[19]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBorderleft(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[19], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTBorder> getBorderrightList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTBorder>(this::getBorderrightArray, this::setBorderrightArray, this::insertNewBorderright, this::removeBorderright, this::sizeOfBorderrightArray);
        }
    }

    @Override
    public CTBorder[] getBorderrightArray() {
        return (CTBorder[])this.getXmlObjectArray(PROPERTY_QNAME[20], (XmlObject[])new CTBorder[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder getBorderrightArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().find_element_user(PROPERTY_QNAME[20], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfBorderrightArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[20]);
        }
    }

    @Override
    public void setBorderrightArray(CTBorder[] borderrightArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])borderrightArray, PROPERTY_QNAME[20]);
    }

    @Override
    public void setBorderrightArray(int i, CTBorder borderright) {
        this.generatedSetterHelperImpl((XmlObject)borderright, PROPERTY_QNAME[20], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder insertNewBorderright(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().insert_element_user(PROPERTY_QNAME[20], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTBorder addNewBorderright() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTBorder target = null;
            target = (CTBorder)this.get_store().add_element_user(PROPERTY_QNAME[20]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeBorderright(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[20], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTClientData> getClientDataList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTClientData>(this::getClientDataArray, this::setClientDataArray, this::insertNewClientData, this::removeClientData, this::sizeOfClientDataArray);
        }
    }

    @Override
    public CTClientData[] getClientDataArray() {
        return (CTClientData[])this.getXmlObjectArray(PROPERTY_QNAME[21], new CTClientData[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClientData getClientDataArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClientData target = null;
            target = (CTClientData)((Object)this.get_store().find_element_user(PROPERTY_QNAME[21], i));
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfClientDataArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[21]);
        }
    }

    @Override
    public void setClientDataArray(CTClientData[] clientDataArray) {
        this.check_orphaned();
        this.arraySetterHelper(clientDataArray, PROPERTY_QNAME[21]);
    }

    @Override
    public void setClientDataArray(int i, CTClientData clientData) {
        this.generatedSetterHelperImpl(clientData, PROPERTY_QNAME[21], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClientData insertNewClientData(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClientData target = null;
            target = (CTClientData)((Object)this.get_store().insert_element_user(PROPERTY_QNAME[21], i));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTClientData addNewClientData() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTClientData target = null;
            target = (CTClientData)((Object)this.get_store().add_element_user(PROPERTY_QNAME[21]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeClientData(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[21], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTRel> getTextdataList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTRel>(this::getTextdataArray, this::setTextdataArray, this::insertNewTextdata, this::removeTextdata, this::sizeOfTextdataArray);
        }
    }

    @Override
    public CTRel[] getTextdataArray() {
        return (CTRel[])this.getXmlObjectArray(PROPERTY_QNAME[22], (XmlObject[])new CTRel[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel getTextdataArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)this.get_store().find_element_user(PROPERTY_QNAME[22], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfTextdataArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[22]);
        }
    }

    @Override
    public void setTextdataArray(CTRel[] textdataArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])textdataArray, PROPERTY_QNAME[22]);
    }

    @Override
    public void setTextdataArray(int i, CTRel textdata) {
        this.generatedSetterHelperImpl((XmlObject)textdata, PROPERTY_QNAME[22], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel insertNewTextdata(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)this.get_store().insert_element_user(PROPERTY_QNAME[22], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTRel addNewTextdata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTRel target = null;
            target = (CTRel)this.get_store().add_element_user(PROPERTY_QNAME[22]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeTextdata(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[22], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTInk> getInkList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTInk>(this::getInkArray, this::setInkArray, this::insertNewInk, this::removeInk, this::sizeOfInkArray);
        }
    }

    @Override
    public CTInk[] getInkArray() {
        return (CTInk[])this.getXmlObjectArray(PROPERTY_QNAME[23], (XmlObject[])new CTInk[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInk getInkArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInk target = null;
            target = (CTInk)this.get_store().find_element_user(PROPERTY_QNAME[23], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfInkArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[23]);
        }
    }

    @Override
    public void setInkArray(CTInk[] inkArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])inkArray, PROPERTY_QNAME[23]);
    }

    @Override
    public void setInkArray(int i, CTInk ink) {
        this.generatedSetterHelperImpl((XmlObject)ink, PROPERTY_QNAME[23], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInk insertNewInk(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInk target = null;
            target = (CTInk)this.get_store().insert_element_user(PROPERTY_QNAME[23], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTInk addNewInk() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTInk target = null;
            target = (CTInk)this.get_store().add_element_user(PROPERTY_QNAME[23]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeInk(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[23], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEmpty> getIscommentList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEmpty>(this::getIscommentArray, this::setIscommentArray, this::insertNewIscomment, this::removeIscomment, this::sizeOfIscommentArray);
        }
    }

    @Override
    public CTEmpty[] getIscommentArray() {
        return (CTEmpty[])this.getXmlObjectArray(PROPERTY_QNAME[24], (XmlObject[])new CTEmpty[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty getIscommentArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)this.get_store().find_element_user(PROPERTY_QNAME[24], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfIscommentArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[24]);
        }
    }

    @Override
    public void setIscommentArray(CTEmpty[] iscommentArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])iscommentArray, PROPERTY_QNAME[24]);
    }

    @Override
    public void setIscommentArray(int i, CTEmpty iscomment) {
        this.generatedSetterHelperImpl((XmlObject)iscomment, PROPERTY_QNAME[24], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty insertNewIscomment(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)this.get_store().insert_element_user(PROPERTY_QNAME[24], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEmpty addNewIscomment() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEmpty target = null;
            target = (CTEmpty)this.get_store().add_element_user(PROPERTY_QNAME[24]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeIscomment(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[24], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List<CTEquationXml> getEquationxmlList() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return new JavaListXmlObject<CTEquationXml>(this::getEquationxmlArray, this::setEquationxmlArray, this::insertNewEquationxml, this::removeEquationxml, this::sizeOfEquationxmlArray);
        }
    }

    @Override
    public CTEquationXml[] getEquationxmlArray() {
        return (CTEquationXml[])this.getXmlObjectArray(PROPERTY_QNAME[25], (XmlObject[])new CTEquationXml[0]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEquationXml getEquationxmlArray(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEquationXml target = null;
            target = (CTEquationXml)this.get_store().find_element_user(PROPERTY_QNAME[25], i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int sizeOfEquationxmlArray() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().count_elements(PROPERTY_QNAME[25]);
        }
    }

    @Override
    public void setEquationxmlArray(CTEquationXml[] equationxmlArray) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])equationxmlArray, PROPERTY_QNAME[25]);
    }

    @Override
    public void setEquationxmlArray(int i, CTEquationXml equationxml) {
        this.generatedSetterHelperImpl((XmlObject)equationxml, PROPERTY_QNAME[25], i, (short)2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEquationXml insertNewEquationxml(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEquationXml target = null;
            target = (CTEquationXml)this.get_store().insert_element_user(PROPERTY_QNAME[25], i);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public CTEquationXml addNewEquationxml() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            CTEquationXml target = null;
            target = (CTEquationXml)this.get_store().add_element_user(PROPERTY_QNAME[25]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeEquationxml(int i) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_element(PROPERTY_QNAME[25], i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[26]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setId(String id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[26]));
            }
            target.setStringValue(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetId(XmlString id) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[26]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[26]));
            }
            target.set(id);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetId() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[26]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[27]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStyle(String style) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[27]));
            }
            target.setStringValue(style);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetStyle(XmlString style) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[27]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[27]));
            }
            target.set(style);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStyle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[27]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getHref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetHref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[28]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHref(String href) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.setStringValue(href);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHref(XmlString href) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[28]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[28]));
            }
            target.set(href);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHref() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[28]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getTarget() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetTarget() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTarget() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[29]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTarget(String targetValue) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[29]));
            }
            target.setStringValue(targetValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTarget(XmlString targetValue) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[29]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[29]));
            }
            target.set(targetValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTarget() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[29]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getClass1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetClass1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetClass1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[30]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setClass1(String class1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[30]));
            }
            target.setStringValue(class1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetClass1(XmlString class1) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[30]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[30]));
            }
            target.set(class1);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetClass1() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[30]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[31]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTitle(String title) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[31]));
            }
            target.setStringValue(title);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetTitle(XmlString title) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[31]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[31]));
            }
            target.set(title);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetTitle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[31]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getAlt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[32]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetAlt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[32]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAlt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[32]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAlt(String alt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[32]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[32]));
            }
            target.setStringValue(alt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAlt(XmlString alt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[32]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[32]));
            }
            target.set(alt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAlt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[32]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getCoordsize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[33]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetCoordsize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[33]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCoordsize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[33]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCoordsize(String coordsize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[33]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[33]));
            }
            target.setStringValue(coordsize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCoordsize(XmlString coordsize) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[33]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[33]));
            }
            target.set(coordsize);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCoordsize() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[33]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getCoordorigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[34]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetCoordorigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[34]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCoordorigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[34]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCoordorigin(String coordorigin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[34]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[34]));
            }
            target.setStringValue(coordorigin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCoordorigin(XmlString coordorigin) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[34]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[34]));
            }
            target.set(coordorigin);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCoordorigin() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[34]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getWrapcoords() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[35]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetWrapcoords() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[35]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetWrapcoords() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[35]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setWrapcoords(String wrapcoords) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[35]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[35]));
            }
            target.setStringValue(wrapcoords);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetWrapcoords(XmlString wrapcoords) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[35]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[35]));
            }
            target.set(wrapcoords);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetWrapcoords() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[35]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getPrint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[36]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetPrint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[36]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPrint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[36]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPrint(STTrueFalse.Enum print) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[36]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[36]));
            }
            target.setEnumValue(print);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPrint(STTrueFalse print) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[36]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[36]));
            }
            target.set(print);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPrint() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[36]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[37]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[37]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[37]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSpid(String spid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[37]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[37]));
            }
            target.setStringValue(spid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSpid(XmlString spid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[37]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[37]));
            }
            target.set(spid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[37]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getOned() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[38]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetOned() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[38]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOned() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[38]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOned(STTrueFalse.Enum oned) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[38]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[38]));
            }
            target.setEnumValue(oned);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOned(STTrueFalse oned) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[38]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[38]));
            }
            target.set(oned);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOned() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[38]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getRegroupid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[39]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetRegroupid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[39]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetRegroupid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[39]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setRegroupid(BigInteger regroupid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[39]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[39]));
            }
            target.setBigIntegerValue(regroupid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetRegroupid(XmlInteger regroupid) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[39]));
            if (target == null) {
                target = (XmlInteger)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[39]));
            }
            target.set(regroupid);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetRegroupid() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[39]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getDoubleclicknotify() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[40]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetDoubleclicknotify() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[40]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDoubleclicknotify() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[40]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDoubleclicknotify(STTrueFalse.Enum doubleclicknotify) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[40]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[40]));
            }
            target.setEnumValue(doubleclicknotify);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDoubleclicknotify(STTrueFalse doubleclicknotify) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[40]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[40]));
            }
            target.set(doubleclicknotify);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDoubleclicknotify() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[40]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getButton() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[41]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetButton() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[41]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetButton() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[41]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setButton(STTrueFalse.Enum button) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[41]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[41]));
            }
            target.setEnumValue(button);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetButton(STTrueFalse button) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[41]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[41]));
            }
            target.set(button);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetButton() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[41]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getUserhidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[42]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetUserhidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[42]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetUserhidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[42]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setUserhidden(STTrueFalse.Enum userhidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[42]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[42]));
            }
            target.setEnumValue(userhidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetUserhidden(STTrueFalse userhidden) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[42]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[42]));
            }
            target.set(userhidden);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetUserhidden() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[42]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getBullet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[43]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetBullet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[43]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBullet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[43]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBullet(STTrueFalse.Enum bullet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[43]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[43]));
            }
            target.setEnumValue(bullet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBullet(STTrueFalse bullet) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[43]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[43]));
            }
            target.set(bullet);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBullet() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[43]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getHr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[44]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetHr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[44]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[44]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHr(STTrueFalse.Enum hr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[44]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[44]));
            }
            target.setEnumValue(hr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHr(STTrueFalse hr) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[44]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[44]));
            }
            target.set(hr);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHr() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[44]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getHrstd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[45]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetHrstd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[45]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHrstd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[45]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHrstd(STTrueFalse.Enum hrstd) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[45]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[45]));
            }
            target.setEnumValue(hrstd);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHrstd(STTrueFalse hrstd) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[45]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[45]));
            }
            target.set(hrstd);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHrstd() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[45]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getHrnoshade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[46]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetHrnoshade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[46]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHrnoshade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[46]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHrnoshade(STTrueFalse.Enum hrnoshade) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[46]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[46]));
            }
            target.setEnumValue(hrnoshade);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHrnoshade(STTrueFalse hrnoshade) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[46]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[46]));
            }
            target.set(hrnoshade);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHrnoshade() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[46]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float getHrpct() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[47]));
            return target == null ? 0.0f : target.getFloatValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlFloat xgetHrpct() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlFloat target = null;
            target = (XmlFloat)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[47]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHrpct() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[47]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHrpct(float hrpct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[47]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[47]));
            }
            target.setFloatValue(hrpct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHrpct(XmlFloat hrpct) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlFloat target = null;
            target = (XmlFloat)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[47]));
            if (target == null) {
                target = (XmlFloat)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[47]));
            }
            target.set(hrpct);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHrpct() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[47]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STHrAlign.Enum getHralign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[48]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[48]));
            }
            return target == null ? null : (STHrAlign.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STHrAlign xgetHralign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHrAlign target = null;
            target = (STHrAlign)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[48]));
            if (target == null) {
                target = (STHrAlign)this.get_default_attribute_value(PROPERTY_QNAME[48]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetHralign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[48]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setHralign(STHrAlign.Enum hralign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[48]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[48]));
            }
            target.setEnumValue(hralign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetHralign(STHrAlign hralign) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STHrAlign target = null;
            target = (STHrAlign)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[48]));
            if (target == null) {
                target = (STHrAlign)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[48]));
            }
            target.set(hralign);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetHralign() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[48]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getAllowincell() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[49]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetAllowincell() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[49]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAllowincell() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[49]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAllowincell(STTrueFalse.Enum allowincell) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[49]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[49]));
            }
            target.setEnumValue(allowincell);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAllowincell(STTrueFalse allowincell) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[49]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[49]));
            }
            target.set(allowincell);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAllowincell() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[49]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getAllowoverlap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[50]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetAllowoverlap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[50]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAllowoverlap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[50]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAllowoverlap(STTrueFalse.Enum allowoverlap) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[50]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[50]));
            }
            target.setEnumValue(allowoverlap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAllowoverlap(STTrueFalse allowoverlap) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[50]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[50]));
            }
            target.set(allowoverlap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAllowoverlap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[50]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getUserdrawn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[51]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetUserdrawn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[51]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetUserdrawn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[51]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setUserdrawn(STTrueFalse.Enum userdrawn) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[51]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[51]));
            }
            target.setEnumValue(userdrawn);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetUserdrawn(STTrueFalse userdrawn) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[51]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[51]));
            }
            target.set(userdrawn);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetUserdrawn() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[51]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getBordertopcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[52]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetBordertopcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[52]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBordertopcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[52]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBordertopcolor(String bordertopcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[52]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[52]));
            }
            target.setStringValue(bordertopcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBordertopcolor(XmlString bordertopcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[52]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[52]));
            }
            target.set(bordertopcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBordertopcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[52]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getBorderleftcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[53]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetBorderleftcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[53]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBorderleftcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[53]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBorderleftcolor(String borderleftcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[53]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[53]));
            }
            target.setStringValue(borderleftcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBorderleftcolor(XmlString borderleftcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[53]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[53]));
            }
            target.set(borderleftcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBorderleftcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[53]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getBorderbottomcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[54]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetBorderbottomcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[54]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBorderbottomcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[54]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBorderbottomcolor(String borderbottomcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[54]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[54]));
            }
            target.setStringValue(borderbottomcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBorderbottomcolor(XmlString borderbottomcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[54]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[54]));
            }
            target.set(borderbottomcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBorderbottomcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[54]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getBorderrightcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[55]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetBorderrightcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[55]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBorderrightcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[55]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBorderrightcolor(String borderrightcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[55]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[55]));
            }
            target.setStringValue(borderrightcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBorderrightcolor(XmlString borderrightcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[55]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[55]));
            }
            target.set(borderrightcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBorderrightcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[55]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getDgmlayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[56]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDiagramLayout xgetDgmlayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDiagramLayout target = null;
            target = (STDiagramLayout)this.get_store().find_attribute_user(PROPERTY_QNAME[56]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDgmlayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[56]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDgmlayout(BigInteger dgmlayout) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[56]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[56]));
            }
            target.setBigIntegerValue(dgmlayout);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDgmlayout(STDiagramLayout dgmlayout) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDiagramLayout target = null;
            target = (STDiagramLayout)this.get_store().find_attribute_user(PROPERTY_QNAME[56]);
            if (target == null) {
                target = (STDiagramLayout)this.get_store().add_attribute_user(PROPERTY_QNAME[56]);
            }
            target.set((XmlObject)dgmlayout);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDgmlayout() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[56]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getDgmnodekind() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[57]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlInteger xgetDgmnodekind() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[57]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDgmnodekind() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[57]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDgmnodekind(BigInteger dgmnodekind) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[57]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[57]));
            }
            target.setBigIntegerValue(dgmnodekind);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDgmnodekind(XmlInteger dgmnodekind) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlInteger target = null;
            target = (XmlInteger)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[57]));
            if (target == null) {
                target = (XmlInteger)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[57]));
            }
            target.set(dgmnodekind);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDgmnodekind() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[57]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public BigInteger getDgmlayoutmru() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[58]));
            return target == null ? null : target.getBigIntegerValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STDiagramLayout xgetDgmlayoutmru() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDiagramLayout target = null;
            target = (STDiagramLayout)this.get_store().find_attribute_user(PROPERTY_QNAME[58]);
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetDgmlayoutmru() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[58]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setDgmlayoutmru(BigInteger dgmlayoutmru) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[58]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[58]));
            }
            target.setBigIntegerValue(dgmlayoutmru);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetDgmlayoutmru(STDiagramLayout dgmlayoutmru) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STDiagramLayout target = null;
            target = (STDiagramLayout)this.get_store().find_attribute_user(PROPERTY_QNAME[58]);
            if (target == null) {
                target = (STDiagramLayout)this.get_store().add_attribute_user(PROPERTY_QNAME[58]);
            }
            target.set((XmlObject)dgmlayoutmru);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetDgmlayoutmru() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[58]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STInsetMode.Enum getInsetmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[59]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[59]));
            }
            return target == null ? null : (STInsetMode.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STInsetMode xgetInsetmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STInsetMode target = null;
            target = (STInsetMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[59]));
            if (target == null) {
                target = (STInsetMode)this.get_default_attribute_value(PROPERTY_QNAME[59]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInsetmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[59]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setInsetmode(STInsetMode.Enum insetmode) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[59]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[59]));
            }
            target.setEnumValue(insetmode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetInsetmode(STInsetMode insetmode) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STInsetMode target = null;
            target = (STInsetMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[59]));
            if (target == null) {
                target = (STInsetMode)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[59]));
            }
            target.set(insetmode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInsetmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[59]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getChromakey() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[60]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorType xgetChromakey() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[60]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetChromakey() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[60]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setChromakey(String chromakey) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[60]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[60]));
            }
            target.setStringValue(chromakey);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetChromakey(STColorType chromakey) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[60]));
            if (target == null) {
                target = (STColorType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[60]));
            }
            target.set(chromakey);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetChromakey() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[60]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getFilled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[61]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetFilled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[61]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFilled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[61]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFilled(STTrueFalse.Enum filled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[61]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[61]));
            }
            target.setEnumValue(filled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFilled(STTrueFalse filled) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[61]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[61]));
            }
            target.set(filled);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFilled() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[61]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getFillcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[62]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorType xgetFillcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[62]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetFillcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[62]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setFillcolor(String fillcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[62]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[62]));
            }
            target.setStringValue(fillcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetFillcolor(STColorType fillcolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[62]));
            if (target == null) {
                target = (STColorType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[62]));
            }
            target.set(fillcolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetFillcolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[62]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getOpacity() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[63]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetOpacity() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[63]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOpacity() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[63]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOpacity(String opacity) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[63]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[63]));
            }
            target.setStringValue(opacity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOpacity(XmlString opacity) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[63]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[63]));
            }
            target.set(opacity);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOpacity() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[63]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getStroked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[64]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetStroked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[64]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStroked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[64]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStroked(STTrueFalse.Enum stroked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[64]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[64]));
            }
            target.setEnumValue(stroked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetStroked(STTrueFalse stroked) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[64]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[64]));
            }
            target.set(stroked);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStroked() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[64]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getStrokecolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[65]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STColorType xgetStrokecolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[65]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStrokecolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[65]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStrokecolor(String strokecolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[65]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[65]));
            }
            target.setStringValue(strokecolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetStrokecolor(STColorType strokecolor) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STColorType target = null;
            target = (STColorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[65]));
            if (target == null) {
                target = (STColorType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[65]));
            }
            target.set(strokecolor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStrokecolor() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[65]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getStrokeweight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[66]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetStrokeweight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[66]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetStrokeweight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[66]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setStrokeweight(String strokeweight) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[66]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[66]));
            }
            target.setStringValue(strokeweight);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetStrokeweight(XmlString strokeweight) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[66]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[66]));
            }
            target.set(strokeweight);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetStrokeweight() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[66]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getInsetpen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetInsetpen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetInsetpen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[67]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setInsetpen(STTrueFalse.Enum insetpen) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[67]));
            }
            target.setEnumValue(insetpen);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetInsetpen(STTrueFalse insetpen) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[67]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[67]));
            }
            target.set(insetpen);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetInsetpen() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[67]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public float getSpt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[68]));
            return target == null ? 0.0f : target.getFloatValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlFloat xgetSpt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlFloat target = null;
            target = (XmlFloat)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[68]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetSpt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[68]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setSpt(float spt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[68]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[68]));
            }
            target.setFloatValue(spt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetSpt(XmlFloat spt) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlFloat target = null;
            target = (XmlFloat)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[68]));
            if (target == null) {
                target = (XmlFloat)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[68]));
            }
            target.set(spt);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetSpt() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[68]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STConnectorType.Enum getConnectortype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[69]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_default_attribute_value(PROPERTY_QNAME[69]));
            }
            return target == null ? null : (STConnectorType.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STConnectorType xgetConnectortype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STConnectorType target = null;
            target = (STConnectorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[69]));
            if (target == null) {
                target = (STConnectorType)this.get_default_attribute_value(PROPERTY_QNAME[69]);
            }
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetConnectortype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[69]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setConnectortype(STConnectorType.Enum connectortype) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[69]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[69]));
            }
            target.setEnumValue(connectortype);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetConnectortype(STConnectorType connectortype) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STConnectorType target = null;
            target = (STConnectorType)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[69]));
            if (target == null) {
                target = (STConnectorType)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[69]));
            }
            target.set(connectortype);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetConnectortype() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[69]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode.Enum getBwmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[70]));
            return target == null ? null : (STBWMode.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode xgetBwmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[70]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBwmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[70]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBwmode(STBWMode.Enum bwmode) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[70]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[70]));
            }
            target.setEnumValue(bwmode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBwmode(STBWMode bwmode) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[70]));
            if (target == null) {
                target = (STBWMode)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[70]));
            }
            target.set(bwmode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBwmode() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[70]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode.Enum getBwpure() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[71]));
            return target == null ? null : (STBWMode.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode xgetBwpure() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[71]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBwpure() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[71]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBwpure(STBWMode.Enum bwpure) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[71]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[71]));
            }
            target.setEnumValue(bwpure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBwpure(STBWMode bwpure) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[71]));
            if (target == null) {
                target = (STBWMode)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[71]));
            }
            target.set(bwpure);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBwpure() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[71]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode.Enum getBwnormal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[72]));
            return target == null ? null : (STBWMode.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STBWMode xgetBwnormal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[72]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetBwnormal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[72]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setBwnormal(STBWMode.Enum bwnormal) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[72]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[72]));
            }
            target.setEnumValue(bwnormal);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetBwnormal(STBWMode bwnormal) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STBWMode target = null;
            target = (STBWMode)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[72]));
            if (target == null) {
                target = (STBWMode)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[72]));
            }
            target.set(bwnormal);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetBwnormal() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[72]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getForcedash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[73]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetForcedash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[73]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetForcedash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[73]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setForcedash(STTrueFalse.Enum forcedash) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[73]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[73]));
            }
            target.setEnumValue(forcedash);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetForcedash(STTrueFalse forcedash) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[73]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[73]));
            }
            target.set(forcedash);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetForcedash() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[73]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getOleicon() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[74]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetOleicon() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[74]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOleicon() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[74]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOleicon(STTrueFalse.Enum oleicon) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[74]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[74]));
            }
            target.setEnumValue(oleicon);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOleicon(STTrueFalse oleicon) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[74]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[74]));
            }
            target.set(oleicon);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOleicon() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[74]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank.Enum getOle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[75]));
            return target == null ? null : (STTrueFalseBlank.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalseBlank xgetOle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[75]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetOle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[75]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setOle(STTrueFalseBlank.Enum ole) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[75]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[75]));
            }
            target.setEnumValue(ole);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetOle(STTrueFalseBlank ole) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalseBlank target = null;
            target = (STTrueFalseBlank)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[75]));
            if (target == null) {
                target = (STTrueFalseBlank)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[75]));
            }
            target.set(ole);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetOle() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[75]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getPreferrelative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[76]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetPreferrelative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[76]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPreferrelative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[76]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPreferrelative(STTrueFalse.Enum preferrelative) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[76]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[76]));
            }
            target.setEnumValue(preferrelative);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPreferrelative(STTrueFalse preferrelative) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[76]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[76]));
            }
            target.set(preferrelative);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPreferrelative() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[76]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getCliptowrap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[77]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetCliptowrap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[77]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetCliptowrap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[77]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setCliptowrap(STTrueFalse.Enum cliptowrap) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[77]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[77]));
            }
            target.setEnumValue(cliptowrap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetCliptowrap(STTrueFalse cliptowrap) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[77]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[77]));
            }
            target.set(cliptowrap);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetCliptowrap() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[77]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse.Enum getClip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[78]));
            return target == null ? null : (STTrueFalse.Enum)target.getEnumValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public STTrueFalse xgetClip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[78]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetClip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[78]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setClip(STTrueFalse.Enum clip) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[78]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[78]));
            }
            target.setEnumValue(clip);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetClip(STTrueFalse clip) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            STTrueFalse target = null;
            target = (STTrueFalse)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[78]));
            if (target == null) {
                target = (STTrueFalse)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[78]));
            }
            target.set(clip);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetClip() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[78]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[79]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[79]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[79]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setType(String type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[79]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[79]));
            }
            target.setStringValue(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetType(XmlString type) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[79]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[79]));
            }
            target.set(type);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetType() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[79]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getAdj() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[80]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetAdj() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[80]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetAdj() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[80]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setAdj(String adj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[80]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[80]));
            }
            target.setStringValue(adj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetAdj(XmlString adj) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[80]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[80]));
            }
            target.set(adj);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetAdj() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[80]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getPath2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[81]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetPath2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[81]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetPath2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[81]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setPath2(String path2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[81]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[81]));
            }
            target.setStringValue(path2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetPath2(XmlString path2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[81]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[81]));
            }
            target.set(path2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetPath2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[81]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public byte[] getGfxdata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[82]));
            return target == null ? null : target.getByteArrayValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlBase64Binary xgetGfxdata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBase64Binary target = null;
            target = (XmlBase64Binary)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[82]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetGfxdata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[82]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setGfxdata(byte[] gfxdata) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[82]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[82]));
            }
            target.setByteArrayValue(gfxdata);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetGfxdata(XmlBase64Binary gfxdata) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlBase64Binary target = null;
            target = (XmlBase64Binary)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[82]));
            if (target == null) {
                target = (XmlBase64Binary)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[82]));
            }
            target.set(gfxdata);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetGfxdata() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[82]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String getEquationxml2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[83]));
            return target == null ? null : target.getStringValue();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public XmlString xgetEquationxml2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[83]));
            return target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean isSetEquationxml2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(PROPERTY_QNAME[83]) != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setEquationxml2(String equationxml2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[83]));
            if (target == null) {
                target = (SimpleValue)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[83]));
            }
            target.setStringValue(equationxml2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void xsetEquationxml2(XmlString equationxml2) {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            XmlString target = null;
            target = (XmlString)((Object)this.get_store().find_attribute_user(PROPERTY_QNAME[83]));
            if (target == null) {
                target = (XmlString)((Object)this.get_store().add_attribute_user(PROPERTY_QNAME[83]));
            }
            target.set(equationxml2);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void unsetEquationxml2() {
        Object object = this.monitor();
        synchronized (object) {
            this.check_orphaned();
            this.get_store().remove_attribute(PROPERTY_QNAME[83]);
        }
    }
}

