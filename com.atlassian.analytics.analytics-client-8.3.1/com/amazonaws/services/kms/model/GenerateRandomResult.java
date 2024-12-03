/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.kms.model;

import com.amazonaws.AmazonWebServiceResult;
import com.amazonaws.ResponseMetadata;
import java.io.Serializable;
import java.nio.ByteBuffer;

public class GenerateRandomResult
extends AmazonWebServiceResult<ResponseMetadata>
implements Serializable,
Cloneable {
    private ByteBuffer plaintext;

    public void setPlaintext(ByteBuffer plaintext) {
        this.plaintext = plaintext;
    }

    public ByteBuffer getPlaintext() {
        return this.plaintext;
    }

    public GenerateRandomResult withPlaintext(ByteBuffer plaintext) {
        this.setPlaintext(plaintext);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getPlaintext() != null) {
            sb.append("Plaintext: ").append("***Sensitive Data Redacted***");
        }
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GenerateRandomResult)) {
            return false;
        }
        GenerateRandomResult other = (GenerateRandomResult)obj;
        if (other.getPlaintext() == null ^ this.getPlaintext() == null) {
            return false;
        }
        return other.getPlaintext() == null || other.getPlaintext().equals(this.getPlaintext());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getPlaintext() == null ? 0 : this.getPlaintext().hashCode());
        return hashCode;
    }

    public GenerateRandomResult clone() {
        try {
            return (GenerateRandomResult)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

