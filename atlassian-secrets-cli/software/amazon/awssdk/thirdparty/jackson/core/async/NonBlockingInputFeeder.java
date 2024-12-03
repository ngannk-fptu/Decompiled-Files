/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.async;

public interface NonBlockingInputFeeder {
    public boolean needMoreInput();

    public void endOfInput();
}

