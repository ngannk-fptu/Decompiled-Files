/*
 * Decompiled with CFR 0.152.
 */
package javax.activation;

import java.awt.datatransfer.DataFlavor;
import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;

public class ActivationDataFlavor
extends DataFlavor {
    private String mimeType = null;
    private MimeType mimeObject = null;
    private String humanPresentableName = null;
    private Class representationClass = null;

    public ActivationDataFlavor(Class representationClass, String mimeType, String humanPresentableName) {
        super(mimeType, humanPresentableName);
        this.mimeType = mimeType;
        this.humanPresentableName = humanPresentableName;
        this.representationClass = representationClass;
    }

    public ActivationDataFlavor(Class representationClass, String humanPresentableName) {
        super(representationClass, humanPresentableName);
        this.mimeType = super.getMimeType();
        this.representationClass = representationClass;
        this.humanPresentableName = humanPresentableName;
    }

    public ActivationDataFlavor(String mimeType, String humanPresentableName) {
        super(mimeType, humanPresentableName);
        this.mimeType = mimeType;
        try {
            this.representationClass = Class.forName("java.io.InputStream");
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        this.humanPresentableName = humanPresentableName;
    }

    @Override
    public String getMimeType() {
        return this.mimeType;
    }

    public Class getRepresentationClass() {
        return this.representationClass;
    }

    @Override
    public String getHumanPresentableName() {
        return this.humanPresentableName;
    }

    @Override
    public void setHumanPresentableName(String humanPresentableName) {
        this.humanPresentableName = humanPresentableName;
    }

    @Override
    public boolean equals(DataFlavor dataFlavor) {
        return this.isMimeTypeEqual(dataFlavor) && dataFlavor.getRepresentationClass() == this.representationClass;
    }

    @Override
    public boolean isMimeTypeEqual(String mimeType) {
        MimeType mt = null;
        try {
            if (this.mimeObject == null) {
                this.mimeObject = new MimeType(this.mimeType);
            }
            mt = new MimeType(mimeType);
        }
        catch (MimeTypeParseException e) {
            return this.mimeType.equalsIgnoreCase(mimeType);
        }
        return this.mimeObject.match(mt);
    }

    @Override
    protected String normalizeMimeTypeParameter(String parameterName, String parameterValue) {
        return parameterValue;
    }

    @Override
    protected String normalizeMimeType(String mimeType) {
        return mimeType;
    }
}

