/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import javax.xml.namespace.QName;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.util.RandomSingleton;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STAlgClass;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STAlgType;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STCryptProv;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STOnOff1;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSettings;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTZoom;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STDocProtect;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.SettingsDocument;

public class XWPFSettings
extends POIXMLDocumentPart {
    private CTSettings ctSettings;

    public XWPFSettings(PackagePart part) throws IOException {
        super(part);
    }

    public XWPFSettings() {
        this.ctSettings = CTSettings.Factory.newInstance();
    }

    @Override
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
        try (InputStream stream = this.getPackagePart().getInputStream();){
            this.readFrom(stream);
        }
    }

    public long getZoomPercent() {
        CTZoom zoom = !this.ctSettings.isSetZoom() ? this.ctSettings.addNewZoom() : this.ctSettings.getZoom();
        return zoom.getPercent() == null ? 100L : (long)(POIXMLUnits.parsePercent(zoom.xgetPercent()) / 1000);
    }

    public void setZoomPercent(long zoomPercent) {
        if (!this.ctSettings.isSetZoom()) {
            this.ctSettings.addNewZoom();
        }
        CTZoom zoom = this.ctSettings.getZoom();
        zoom.setPercent(BigInteger.valueOf(zoomPercent));
    }

    public boolean isEnforcedWith() {
        CTDocProtect ctDocProtect = this.ctSettings.getDocumentProtection();
        return ctDocProtect != null && POIXMLUnits.parseOnOff(ctDocProtect.xgetEnforcement());
    }

    public boolean isEnforcedWith(STDocProtect.Enum editValue) {
        CTDocProtect ctDocProtect = this.ctSettings.getDocumentProtection();
        return ctDocProtect != null && POIXMLUnits.parseOnOff(ctDocProtect.xgetEnforcement()) && ctDocProtect.getEdit().equals(editValue);
    }

    public void setEnforcementEditValue(STDocProtect.Enum editValue) {
        this.safeGetDocumentProtection().setEnforcement(STOnOff1.ON);
        this.safeGetDocumentProtection().setEdit(editValue);
    }

    public void setEnforcementEditValue(STDocProtect.Enum editValue, String password, HashAlgorithm hashAlgo) {
        this.safeGetDocumentProtection().setEnforcement(STOnOff1.ON);
        this.safeGetDocumentProtection().setEdit(editValue);
        if (password == null) {
            if (this.safeGetDocumentProtection().isSetCryptProviderType()) {
                this.safeGetDocumentProtection().unsetCryptProviderType();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmClass()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmClass();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmType()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmType();
            }
            if (this.safeGetDocumentProtection().isSetCryptAlgorithmSid()) {
                this.safeGetDocumentProtection().unsetCryptAlgorithmSid();
            }
            if (this.safeGetDocumentProtection().isSetSalt()) {
                this.safeGetDocumentProtection().unsetSalt();
            }
            if (this.safeGetDocumentProtection().isSetCryptSpinCount()) {
                this.safeGetDocumentProtection().unsetCryptSpinCount();
            }
            if (this.safeGetDocumentProtection().isSetHash()) {
                this.safeGetDocumentProtection().unsetHash();
            }
        } else {
            int sid;
            STCryptProv.Enum providerType;
            if (hashAlgo == null) {
                hashAlgo = HashAlgorithm.sha1;
            }
            switch (hashAlgo) {
                case md2: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 1;
                    break;
                }
                case md4: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 2;
                    break;
                }
                case md5: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 3;
                    break;
                }
                case sha1: {
                    providerType = STCryptProv.RSA_FULL;
                    sid = 4;
                    break;
                }
                case sha256: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 12;
                    break;
                }
                case sha384: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 13;
                    break;
                }
                case sha512: {
                    providerType = STCryptProv.RSA_AES;
                    sid = 14;
                    break;
                }
                default: {
                    throw new EncryptedDocumentException("Hash algorithm '" + (Object)((Object)hashAlgo) + "' is not supported for document write protection.");
                }
            }
            byte[] salt = RandomSingleton.getInstance().generateSeed(16);
            int spinCount = 100000;
            String legacyHash = CryptoFunctions.xorHashPasswordReversed(password);
            byte[] hash = CryptoFunctions.hashPassword(legacyHash, hashAlgo, salt, spinCount, false);
            this.safeGetDocumentProtection().setSalt(salt);
            this.safeGetDocumentProtection().setHash(hash);
            this.safeGetDocumentProtection().setCryptSpinCount(BigInteger.valueOf(spinCount));
            this.safeGetDocumentProtection().setCryptAlgorithmType(STAlgType.TYPE_ANY);
            this.safeGetDocumentProtection().setCryptAlgorithmClass(STAlgClass.HASH);
            this.safeGetDocumentProtection().setCryptProviderType(providerType);
            this.safeGetDocumentProtection().setCryptAlgorithmSid(BigInteger.valueOf(sid));
        }
    }

    public boolean validateProtectionPassword(String password) {
        HashAlgorithm hashAlgo;
        BigInteger sid = this.safeGetDocumentProtection().getCryptAlgorithmSid();
        byte[] hash = this.safeGetDocumentProtection().getHash();
        byte[] salt = this.safeGetDocumentProtection().getSalt();
        BigInteger spinCount = this.safeGetDocumentProtection().getCryptSpinCount();
        if (sid == null || hash == null || salt == null || spinCount == null) {
            return false;
        }
        switch (sid.intValue()) {
            case 1: {
                hashAlgo = HashAlgorithm.md2;
                break;
            }
            case 2: {
                hashAlgo = HashAlgorithm.md4;
                break;
            }
            case 3: {
                hashAlgo = HashAlgorithm.md5;
                break;
            }
            case 4: {
                hashAlgo = HashAlgorithm.sha1;
                break;
            }
            case 12: {
                hashAlgo = HashAlgorithm.sha256;
                break;
            }
            case 13: {
                hashAlgo = HashAlgorithm.sha384;
                break;
            }
            case 14: {
                hashAlgo = HashAlgorithm.sha512;
                break;
            }
            default: {
                return false;
            }
        }
        String legacyHash = CryptoFunctions.xorHashPasswordReversed(password);
        byte[] hash2 = CryptoFunctions.hashPassword(legacyHash, hashAlgo, salt, spinCount.intValue(), false);
        return Arrays.equals(hash, hash2);
    }

    public void removeEnforcement() {
        this.safeGetDocumentProtection().setEnforcement(STOnOff1.OFF);
    }

    public void setUpdateFields() {
        CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(STOnOff1.ON);
        this.ctSettings.setUpdateFields(onOff);
    }

    boolean isUpdateFields() {
        return this.ctSettings.isSetUpdateFields() && POIXMLUnits.parseOnOff(this.ctSettings.getUpdateFields().xgetVal());
    }

    public boolean isTrackRevisions() {
        return this.ctSettings.isSetTrackRevisions();
    }

    public void setTrackRevisions(boolean enable) {
        if (enable) {
            if (!this.ctSettings.isSetTrackRevisions()) {
                this.ctSettings.addNewTrackRevisions();
            }
        } else if (this.ctSettings.isSetTrackRevisions()) {
            this.ctSettings.unsetTrackRevisions();
        }
    }

    @Override
    protected void commit() throws IOException {
        if (this.ctSettings == null) {
            throw new IllegalStateException("Unable to write out settings that were never read in!");
        }
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTSettings.type.getName().getNamespaceURI(), "settings"));
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.ctSettings.save(out, xmlOptions);
        }
    }

    private CTDocProtect safeGetDocumentProtection() {
        CTDocProtect documentProtection = this.ctSettings.getDocumentProtection();
        if (documentProtection == null) {
            documentProtection = CTDocProtect.Factory.newInstance();
            this.ctSettings.setDocumentProtection(documentProtection);
        }
        return this.ctSettings.getDocumentProtection();
    }

    private void readFrom(InputStream inputStream) {
        try {
            this.ctSettings = ((SettingsDocument)SettingsDocument.Factory.parse(inputStream, POIXMLTypeLoader.DEFAULT_XML_OPTIONS)).getSettings();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to read data from input-stream", e);
        }
    }

    public boolean getEvenAndOddHeadings() {
        return this.ctSettings.isSetEvenAndOddHeaders();
    }

    public void setEvenAndOddHeadings(boolean enable) {
        CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(enable ? STOnOff1.ON : STOnOff1.OFF);
        this.ctSettings.setEvenAndOddHeaders(onOff);
    }

    public boolean getMirrorMargins() {
        return this.ctSettings.isSetMirrorMargins();
    }

    public void setMirrorMargins(boolean enable) {
        CTOnOff onOff = CTOnOff.Factory.newInstance();
        onOff.setVal(enable ? STOnOff1.ON : STOnOff1.OFF);
        this.ctSettings.setMirrorMargins(onOff);
    }
}

