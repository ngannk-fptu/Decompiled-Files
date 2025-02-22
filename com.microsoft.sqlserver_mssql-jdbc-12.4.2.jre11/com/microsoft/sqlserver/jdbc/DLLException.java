/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.text.MessageFormat;

class DLLException
extends Exception {
    private static final long serialVersionUID = -4498171382218222079L;
    private int category = -9;
    private int status = -9;
    private int state = -9;
    private int errCode = -1;
    private String param1 = "";
    private String param2 = "";
    private String param3 = "";

    DLLException(String message, int category, int status, int state) {
        super(message);
        this.category = category;
        this.status = status;
        this.state = state;
    }

    DLLException(String param1, String param2, String param3, int errCode) {
        this.errCode = errCode;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
    }

    int getCategory() {
        return this.category;
    }

    int getStatus() {
        return this.status;
    }

    int getState() {
        return this.state;
    }

    int getErrCode() {
        return this.errCode;
    }

    String getParam1() {
        return this.param1;
    }

    String getParam2() {
        return this.param2;
    }

    String getParam3() {
        return this.param3;
    }

    static void buildException(int errCode, String param1, String param2, String param3) throws SQLServerException {
        String errMessage = DLLException.getErrMessage(errCode);
        MessageFormat form = new MessageFormat(SQLServerException.getErrString(errMessage));
        String[] msgArgs = DLLException.buildMsgParams(errMessage, param1, param2, param3);
        throw new SQLServerException(null, form.format(msgArgs), null, 0, false);
    }

    private static String[] buildMsgParams(String errMessage, String parameter1, String parameter2, String parameter3) {
        String[] msgArgs = new String[3];
        if ("R_AECertLocBad".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter1;
            msgArgs[1] = parameter1 + "/" + parameter2 + "/" + parameter3;
        } else if ("R_AECertStoreBad".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter2;
            msgArgs[1] = parameter1 + "/" + parameter2 + "/" + parameter3;
        } else if ("R_AECertHashEmpty".equalsIgnoreCase(errMessage)) {
            msgArgs[0] = parameter1 + "/" + parameter2 + "/" + parameter3;
        } else {
            msgArgs[0] = parameter1;
            msgArgs[1] = parameter2;
            msgArgs[2] = parameter3;
        }
        return msgArgs;
    }

    private static String getErrMessage(int errCode) {
        String message;
        switch (errCode) {
            case 1: {
                message = "R_AEKeypathEmpty";
                break;
            }
            case 2: {
                message = "R_EncryptedCEKNull";
                break;
            }
            case 3: {
                message = "R_NullKeyEncryptionAlgorithm";
                break;
            }
            case 4: {
                message = "R_AEWinApiErr";
                break;
            }
            case 5: {
                message = "R_AECertpathBad";
                break;
            }
            case 6: {
                message = "R_AECertLocBad";
                break;
            }
            case 7: {
                message = "R_AECertStoreBad";
                break;
            }
            case 8: {
                message = "R_AECertHashEmpty";
                break;
            }
            case 9: {
                message = "R_AECertNotFound";
                break;
            }
            case 10: {
                message = "R_AEMaloc";
                break;
            }
            case 11: {
                message = "R_EmptyEncryptedCEK";
                break;
            }
            case 12: {
                message = "R_InvalidKeyEncryptionAlgorithm";
                break;
            }
            case 13: {
                message = "R_AEKeypathLong";
                break;
            }
            case 14: {
                message = "R_InvalidEcryptionAlgorithmVersion";
                break;
            }
            case 15: {
                message = "R_AEECEKLenBad";
                break;
            }
            case 16: {
                message = "R_AEECEKSigLenBad";
                break;
            }
            case 17: {
                message = "R_InvalidCertificateSignature";
                break;
            }
            default: {
                message = "R_AEWinApiErr";
            }
        }
        return message;
    }
}

