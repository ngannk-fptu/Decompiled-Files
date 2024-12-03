/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.schemas.office.office;

import com.microsoft.schemas.vml.STExt;
import org.apache.poi.schemas.ooxml.system.ooxml.TypeSystemHolder;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STGuid;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STTrueFalse;

public interface CTSignatureLine
extends XmlObject {
    public static final DocumentFactory<CTSignatureLine> Factory = new DocumentFactory(TypeSystemHolder.typeSystem, "ctsignaturelineec85type");
    public static final SchemaType type = Factory.getType();

    public STExt.Enum getExt();

    public STExt xgetExt();

    public boolean isSetExt();

    public void setExt(STExt.Enum var1);

    public void xsetExt(STExt var1);

    public void unsetExt();

    public STTrueFalse.Enum getIssignatureline();

    public STTrueFalse xgetIssignatureline();

    public boolean isSetIssignatureline();

    public void setIssignatureline(STTrueFalse.Enum var1);

    public void xsetIssignatureline(STTrueFalse var1);

    public void unsetIssignatureline();

    public String getId();

    public STGuid xgetId();

    public boolean isSetId();

    public void setId(String var1);

    public void xsetId(STGuid var1);

    public void unsetId();

    public String getProvid();

    public STGuid xgetProvid();

    public boolean isSetProvid();

    public void setProvid(String var1);

    public void xsetProvid(STGuid var1);

    public void unsetProvid();

    public STTrueFalse.Enum getSigninginstructionsset();

    public STTrueFalse xgetSigninginstructionsset();

    public boolean isSetSigninginstructionsset();

    public void setSigninginstructionsset(STTrueFalse.Enum var1);

    public void xsetSigninginstructionsset(STTrueFalse var1);

    public void unsetSigninginstructionsset();

    public STTrueFalse.Enum getAllowcomments();

    public STTrueFalse xgetAllowcomments();

    public boolean isSetAllowcomments();

    public void setAllowcomments(STTrueFalse.Enum var1);

    public void xsetAllowcomments(STTrueFalse var1);

    public void unsetAllowcomments();

    public STTrueFalse.Enum getShowsigndate();

    public STTrueFalse xgetShowsigndate();

    public boolean isSetShowsigndate();

    public void setShowsigndate(STTrueFalse.Enum var1);

    public void xsetShowsigndate(STTrueFalse var1);

    public void unsetShowsigndate();

    public String getSuggestedsigner();

    public XmlString xgetSuggestedsigner();

    public boolean isSetSuggestedsigner();

    public void setSuggestedsigner(String var1);

    public void xsetSuggestedsigner(XmlString var1);

    public void unsetSuggestedsigner();

    public String getSuggestedsigner2();

    public XmlString xgetSuggestedsigner2();

    public boolean isSetSuggestedsigner2();

    public void setSuggestedsigner2(String var1);

    public void xsetSuggestedsigner2(XmlString var1);

    public void unsetSuggestedsigner2();

    public String getSuggestedsigneremail();

    public XmlString xgetSuggestedsigneremail();

    public boolean isSetSuggestedsigneremail();

    public void setSuggestedsigneremail(String var1);

    public void xsetSuggestedsigneremail(XmlString var1);

    public void unsetSuggestedsigneremail();

    public String getSigninginstructions();

    public XmlString xgetSigninginstructions();

    public boolean isSetSigninginstructions();

    public void setSigninginstructions(String var1);

    public void xsetSigninginstructions(XmlString var1);

    public void unsetSigninginstructions();

    public String getAddlxml();

    public XmlString xgetAddlxml();

    public boolean isSetAddlxml();

    public void setAddlxml(String var1);

    public void xsetAddlxml(XmlString var1);

    public void unsetAddlxml();

    public String getSigprovurl();

    public XmlString xgetSigprovurl();

    public boolean isSetSigprovurl();

    public void setSigprovurl(String var1);

    public void xsetSigprovurl(XmlString var1);

    public void unsetSigprovurl();
}

