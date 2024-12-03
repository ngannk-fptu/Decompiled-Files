/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.dtd.DTDId;
import com.ctc.wstx.dtd.DTDSubset;
import com.ctc.wstx.dtd.DTDValidatorBase;
import com.ctc.wstx.dtd.FullDTDReader;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.ReaderCreator;
import com.ctc.wstx.sr.TypedStreamReader;
import com.ctc.wstx.util.URLUtil;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationProblem;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class ValidatingStreamReader
extends TypedStreamReader {
    static final String STAX_PROP_ENTITIES = "javax.xml.stream.entities";
    static final String STAX_PROP_NOTATIONS = "javax.xml.stream.notations";
    DTDValidationSchema mDTD = null;
    XMLValidator mAutoDtdValidator = null;
    boolean mDtdValidatorSet = false;
    protected ValidationProblemHandler mVldProbHandler = null;

    private ValidatingStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
    }

    public static ValidatingStreamReader createValidatingStreamReader(BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputBootstrapper bs, boolean forER) throws XMLStreamException {
        ValidatingStreamReader sr = new ValidatingStreamReader(bs, input, owner, cfg, ValidatingStreamReader.createElementStack(cfg), forER);
        return sr;
    }

    public Object getProperty(String name) {
        if (name.equals(STAX_PROP_ENTITIES)) {
            this.safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            List l = ((DTDSubset)this.mDTD).getGeneralEntityList();
            return new ArrayList(l);
        }
        if (name.equals(STAX_PROP_NOTATIONS)) {
            this.safeEnsureFinishToken();
            if (this.mDTD == null || !(this.mDTD instanceof DTDSubset)) {
                return null;
            }
            List l = ((DTDSubset)this.mDTD).getNotationList();
            return new ArrayList(l);
        }
        return super.getProperty(name);
    }

    public void setFeature(String name, Object value) {
        if (name.equals("org.codehaus.stax2.propDtdOverride")) {
            if (value != null && !(value instanceof DTDValidationSchema)) {
                throw new IllegalArgumentException("Value to set for feature " + name + " not of type DTDValidationSchema");
            }
            this.mConfig.setProperty("org.codehaus.stax2.propDtdOverride", (DTDValidationSchema)value);
        } else {
            super.setFeature(name, value);
        }
    }

    public Object getProcessedDTD() {
        return this.getProcessedDTDSchema();
    }

    public DTDValidationSchema getProcessedDTDSchema() {
        DTDValidationSchema dtd = this.mConfig.getDTDOverride();
        if (dtd == null) {
            dtd = this.mDTD;
        }
        return this.mDTD;
    }

    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.validateAgainst(schema);
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(schema);
    }

    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return this.mElementStack.stopValidatingAgainst(validator);
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        ValidationProblemHandler oldH = this.mVldProbHandler;
        this.mVldProbHandler = h;
        return oldH;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void finishDTD(boolean copyContents) throws XMLStreamException {
        if (!this.hasConfigFlags(16)) {
            super.finishDTD(copyContents);
            return;
        }
        char c = this.getNextChar(" in DOCTYPE declaration");
        DTDSubset intSubset = null;
        if (c == '[') {
            if (copyContents) {
                ((BranchingReaderSource)this.mInput).startBranch(this.mTextBuffer, this.mInputPtr, this.mNormalizeLFs);
            }
            try {
                intSubset = FullDTDReader.readInternalSubset(this, this.mInput, this.mConfig, this.hasConfigFlags(32), this.mDocXmlVersion);
            }
            finally {
                if (copyContents) {
                    ((BranchingReaderSource)this.mInput).endBranch(this.mInputPtr - 1);
                }
            }
            c = this.getNextCharAfterWS(" in internal DTD subset");
        }
        if (c != '>') {
            this.throwUnexpectedChar(c, "; expected '>' to finish DOCTYPE declaration.");
        }
        this.mDTD = this.mConfig.getDTDOverride();
        if (this.mDTD == null) {
            DTDSubset extSubset = null;
            if (this.mDtdPublicId != null || this.mDtdSystemId != null) {
                extSubset = this.findDtdExtSubset(this.mDtdPublicId, this.mDtdSystemId, intSubset);
            }
            this.mDTD = intSubset == null ? extSubset : (extSubset == null ? intSubset : intSubset.combineWithExternalSubset(this, extSubset));
        }
        if (this.mDTD == null) {
            this.mGeneralEntities = null;
        } else {
            if (this.mDTD instanceof DTDSubset) {
                this.mGeneralEntities = ((DTDSubset)this.mDTD).getGeneralEntityMap();
            } else {
                this._reportProblem(this.mConfig.getXMLReporter(), ErrorConsts.WT_DT_DECL, "Value to set for feature org.codehaus.stax2.propDtdOverride not a native Woodstox DTD implementation (but " + this.mDTD.getClass() + "): can not access full entity or notation information", null);
            }
            this.mAutoDtdValidator = this.mDTD.createValidator(this.mElementStack);
            this.mDtdValidatorSet = true;
            DTDValidatorBase nsDefs = null;
            if (this.mAutoDtdValidator instanceof DTDValidatorBase) {
                DTDValidatorBase dtdv = (DTDValidatorBase)this.mAutoDtdValidator;
                dtdv.setAttrValueNormalization(true);
                if (dtdv.hasNsDefaults()) {
                    nsDefs = dtdv;
                }
            }
            this.mElementStack.setAutomaticDTDValidator(this.mAutoDtdValidator, nsDefs);
        }
    }

    public void reportValidationProblem(XMLValidationProblem prob) throws XMLStreamException {
        if (this.mVldProbHandler != null) {
            this.mVldProbHandler.reportProblem(prob);
        } else {
            super.reportValidationProblem(prob);
        }
    }

    protected void initValidation() throws XMLStreamException {
        if (this.hasConfigFlags(32) && !this.mDtdValidatorSet) {
            this.reportProblem(null, ErrorConsts.WT_DT_DECL, ErrorConsts.W_MISSING_DTD, null, null);
        }
    }

    private DTDSubset findDtdExtSubset(String pubId, String sysId, DTDSubset intSubset) throws XMLStreamException {
        DTDSubset extSubset;
        DTDId dtdId;
        boolean cache = this.hasConfigFlags(65536);
        try {
            dtdId = this.constructDtdId(pubId, sysId);
        }
        catch (IOException ioe) {
            throw this.constructFromIOE(ioe);
        }
        if (cache && (extSubset = this.findCachedSubset(dtdId, intSubset)) != null) {
            return extSubset;
        }
        if (sysId == null) {
            this.throwParseError("Can not resolve DTD with public id \"{0}\"; missing system identifier", this.mDtdPublicId, null);
        }
        WstxInputSource src = null;
        try {
            int xmlVersion = this.mDocXmlVersion;
            if (xmlVersion == 0) {
                xmlVersion = 256;
            }
            src = DefaultInputResolver.resolveEntity(this.mInput, null, null, pubId, sysId, this.mConfig.getDtdResolver(), this.mConfig, xmlVersion);
        }
        catch (FileNotFoundException fex) {
            this.throwParseError("(was {0}) {1}", fex.getClass().getName(), fex.getMessage());
        }
        catch (IOException ioe) {
            this.throwFromIOE(ioe);
        }
        DTDSubset extSubset2 = FullDTDReader.readExternalSubset(src, this.mConfig, intSubset, this.hasConfigFlags(32), this.mDocXmlVersion);
        if (cache && extSubset2.isCachable()) {
            this.mOwner.addCachedDTD(dtdId, extSubset2);
        }
        return extSubset2;
    }

    private DTDSubset findCachedSubset(DTDId id, DTDSubset intSubset) throws XMLStreamException {
        DTDSubset extSubset = this.mOwner.findCachedDTD(id);
        if (extSubset != null && (intSubset == null || extSubset.isReusableWith(intSubset))) {
            return extSubset;
        }
        return null;
    }

    private URI resolveExtSubsetPath(String systemId) throws IOException {
        URL ctxt;
        URL uRL = ctxt = this.mInput == null ? null : this.mInput.getSource();
        if (ctxt == null) {
            return URLUtil.uriFromSystemId(systemId);
        }
        URL url = URLUtil.urlFromSystemId(systemId, ctxt);
        try {
            return new URI(url.toExternalForm());
        }
        catch (URISyntaxException e) {
            throw new IOException("Failed to construct URI for external subset, URL = " + url.toExternalForm() + ": " + e.getMessage());
        }
    }

    protected DTDId constructDtdId(String pubId, String sysId) throws IOException {
        boolean usePublicId;
        int significantFlags = this.mConfigFlags & 0x280021;
        URI sysRef = sysId == null || sysId.length() == 0 ? null : this.resolveExtSubsetPath(sysId);
        boolean bl = usePublicId = (this.mConfigFlags & 0x20000) != 0;
        if (usePublicId && pubId != null && pubId.length() > 0) {
            return DTDId.construct(pubId, sysRef, significantFlags, this.mXml11);
        }
        if (sysRef == null) {
            return null;
        }
        return DTDId.constructFromSystemId(sysRef, significantFlags, this.mXml11);
    }

    protected DTDId constructDtdId(URI sysId) throws IOException {
        int significantFlags = this.mConfigFlags & 0x80021;
        return DTDId.constructFromSystemId(sysId, significantFlags, this.mXml11);
    }

    protected void reportInvalidContent(int evtType) throws XMLStreamException {
        switch (this.mVldContent) {
            case 0: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_EMPTY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            case 1: 
            case 2: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_NON_MIXED, this.mElementStack.getTopElementDesc(), null);
                break;
            }
            case 3: 
            case 4: {
                this.reportValidationProblem(ErrorConsts.ERR_VLD_ANY, this.mElementStack.getTopElementDesc(), ErrorConsts.tokenTypeDesc(evtType));
                break;
            }
            default: {
                this.throwParseError("Internal error: trying to report invalid content for " + evtType);
            }
        }
    }
}

