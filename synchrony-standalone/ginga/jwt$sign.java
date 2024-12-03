/*
 * Decompiled with CFR 0.152.
 */
package ginga;

import clojure.lang.AFunction;
import clojure.lang.RT;
import clojure.lang.Reflector;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import net.minidev.json.JSONObject;

public final class jwt$sign
extends AFunction {
    public static Object invokeStatic(Object data2, Object key2) {
        JWSHeader header2;
        Object[] objectArray = new Object[1];
        Object object = data2;
        data2 = null;
        objectArray[0] = object;
        Payload payload = new Payload((JSONObject)Reflector.invokeConstructor(RT.classForName("net.minidev.json.JSONObject"), objectArray));
        JWSHeader jWSHeader = header2 = new JWSHeader(JWSAlgorithm.HS256);
        header2 = null;
        Payload payload2 = payload;
        payload = null;
        JWSObject object2 = new JWSObject(jWSHeader, payload2);
        Object[] objectArray2 = new Object[1];
        Object object3 = key2;
        key2 = null;
        objectArray2[0] = Reflector.invokeNoArgInstanceMember(object3, "getBytes", false);
        Object signer = Reflector.invokeConstructor(RT.classForName("com.nimbusds.jose.crypto.MACSigner"), objectArray2);
        JWSObject jWSObject = object2;
        object2 = null;
        JWSObject G__20364 = jWSObject;
        Object object4 = signer;
        signer = null;
        G__20364.sign((JWSSigner)object4);
        JWSObject jWSObject2 = G__20364;
        G__20364 = null;
        return jWSObject2;
    }

    @Override
    public Object invoke(Object object, Object object2) {
        Object object3 = object;
        object = null;
        Object object4 = object2;
        object2 = null;
        return jwt$sign.invokeStatic(object3, object4);
    }
}

