/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.xni.parser.XMLComponentManager;

public class XML11DTDValidator
extends XMLDTDValidator {
    protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";

    @Override
    public void reset(XMLComponentManager xMLComponentManager) {
        XMLDTDValidator xMLDTDValidator = null;
        xMLDTDValidator = (XMLDTDValidator)xMLComponentManager.getProperty(DTD_VALIDATOR_PROPERTY);
        if (xMLDTDValidator != null && xMLDTDValidator != this) {
            this.fGrammarBucket = xMLDTDValidator.getGrammarBucket();
        }
        super.reset(xMLComponentManager);
    }

    @Override
    protected void init() {
        if (this.fValidation || this.fDynamicValidation) {
            super.init();
            try {
                this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV("XML11ID");
                this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREF");
                this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV("XML11IDREFS");
                this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKEN");
                this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV("XML11NMTOKENS");
            }
            catch (Exception exception) {
                exception.printStackTrace(System.err);
            }
        }
    }
}

