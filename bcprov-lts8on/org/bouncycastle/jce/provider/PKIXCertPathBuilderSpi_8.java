/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jce.provider;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathBuilderResult;
import java.security.cert.CertPathBuilderSpi;
import java.security.cert.CertPathParameters;
import java.security.cert.CertificateParsingException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.jcajce.PKIXCertStore;
import org.bouncycastle.jcajce.PKIXExtendedBuilderParameters;
import org.bouncycastle.jcajce.PKIXExtendedParameters;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jce.provider.AnnotatedException;
import org.bouncycastle.jce.provider.CertPathValidatorUtilities;
import org.bouncycastle.jce.provider.PKIXCertPathValidatorSpi_8;
import org.bouncycastle.jce.provider.ProvRevocationChecker;

public class PKIXCertPathBuilderSpi_8
extends CertPathBuilderSpi {
    private final JcaJceHelper helper = new BCJcaJceHelper();
    private final boolean isForCRLCheck;
    private Exception certPathException;

    public PKIXCertPathBuilderSpi_8() {
        this(false);
    }

    PKIXCertPathBuilderSpi_8(boolean isForCRLCheck) {
        this.isForCRLCheck = isForCRLCheck;
    }

    @Override
    public PKIXCertPathChecker engineGetRevocationChecker() {
        return new ProvRevocationChecker(this.helper);
    }

    @Override
    public CertPathBuilderResult engineBuild(CertPathParameters params) throws CertPathBuilderException, InvalidAlgorithmParameterException {
        PKIXExtendedBuilderParameters paramsPKIX;
        if (params instanceof PKIXBuilderParameters) {
            PKIXExtendedParameters.Builder paramsPKIXBldr = new PKIXExtendedParameters.Builder((PKIXBuilderParameters)params);
            PKIXExtendedBuilderParameters.Builder paramsBldrPKIXBldr = new PKIXExtendedBuilderParameters.Builder((PKIXBuilderParameters)params);
            paramsPKIX = paramsBldrPKIXBldr.build();
        } else if (params instanceof PKIXExtendedBuilderParameters) {
            paramsPKIX = (PKIXExtendedBuilderParameters)params;
        } else {
            throw new InvalidAlgorithmParameterException("Parameters must be an instance of " + PKIXBuilderParameters.class.getName() + " or " + PKIXExtendedBuilderParameters.class.getName() + ".");
        }
        ArrayList certPathList = new ArrayList();
        Collection targets = CertPathValidatorUtilities.findTargets(paramsPKIX);
        CertPathBuilderResult result = null;
        Iterator targetIter = targets.iterator();
        while (targetIter.hasNext() && result == null) {
            X509Certificate cert = (X509Certificate)targetIter.next();
            result = this.build(cert, paramsPKIX, certPathList);
        }
        if (result == null && this.certPathException != null) {
            if (this.certPathException instanceof AnnotatedException) {
                throw new CertPathBuilderException(this.certPathException.getMessage(), this.certPathException.getCause());
            }
            throw new CertPathBuilderException("Possible certificate chain could not be validated.", this.certPathException);
        }
        if (result == null && this.certPathException == null) {
            throw new CertPathBuilderException("Unable to find certificate chain.");
        }
        return result;
    }

    protected CertPathBuilderResult build(X509Certificate tbvCert, PKIXExtendedBuilderParameters pkixParams, List tbvPath) {
        if (tbvPath.contains(tbvCert)) {
            return null;
        }
        if (pkixParams.getExcludedCerts().contains(tbvCert)) {
            return null;
        }
        if (pkixParams.getMaxPathLength() != -1 && tbvPath.size() - 1 > pkixParams.getMaxPathLength()) {
            return null;
        }
        tbvPath.add(tbvCert);
        CertPathBuilderResult builderResult = null;
        try {
            PKIXCertPathValidatorSpi_8 validator;
            CertificateFactory cFact;
            try {
                cFact = new CertificateFactory();
                validator = new PKIXCertPathValidatorSpi_8(this.isForCRLCheck);
            }
            catch (Exception e) {
                throw new RuntimeException("Exception creating support classes.");
            }
            if (CertPathValidatorUtilities.isIssuerTrustAnchor(tbvCert, pkixParams.getBaseParameters().getTrustAnchors(), pkixParams.getBaseParameters().getSigProvider())) {
                CertPath certPath = null;
                PKIXCertPathValidatorResult result = null;
                try {
                    certPath = cFact.engineGenerateCertPath(tbvPath);
                }
                catch (Exception e) {
                    throw new AnnotatedException("Certification path could not be constructed from certificate list.", e);
                }
                try {
                    result = (PKIXCertPathValidatorResult)validator.engineValidate(certPath, pkixParams);
                }
                catch (Exception e) {
                    throw new AnnotatedException("Certification path could not be validated.", e);
                }
                return new PKIXCertPathBuilderResult(certPath, result.getTrustAnchor(), result.getPolicyTree(), result.getPublicKey());
            }
            ArrayList<PKIXCertStore> stores = new ArrayList<PKIXCertStore>();
            stores.addAll(pkixParams.getBaseParameters().getCertificateStores());
            try {
                stores.addAll(CertPathValidatorUtilities.getAdditionalStoresFromAltNames(tbvCert.getExtensionValue(Extension.issuerAlternativeName.getId()), pkixParams.getBaseParameters().getNamedCertificateStoreMap()));
            }
            catch (CertificateParsingException e) {
                throw new AnnotatedException("No additional X.509 stores can be added from certificate locations.", e);
            }
            HashSet issuers = new HashSet();
            try {
                issuers.addAll(CertPathValidatorUtilities.findIssuerCerts(tbvCert, pkixParams.getBaseParameters().getCertStores(), stores));
            }
            catch (AnnotatedException e) {
                throw new AnnotatedException("Cannot find issuer certificate for certificate in certification path.", e);
            }
            if (issuers.isEmpty()) {
                throw new AnnotatedException("No issuer certificate for certificate in certification path found.");
            }
            Iterator it = issuers.iterator();
            while (it.hasNext() && builderResult == null) {
                X509Certificate issuer = (X509Certificate)it.next();
                builderResult = this.build(issuer, pkixParams, tbvPath);
            }
        }
        catch (AnnotatedException e) {
            this.certPathException = e;
        }
        if (builderResult == null) {
            tbvPath.remove(tbvCert);
        }
        return builderResult;
    }
}

