/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.protocols.query.unmarshall.XmlElement
 */
package software.amazon.awssdk.protocols.xml.internal.unmarshall;

import java.util.Optional;
import java.util.function.Function;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.protocols.query.unmarshall.XmlElement;
import software.amazon.awssdk.protocols.xml.internal.unmarshall.AwsXmlUnmarshallingContext;

@SdkInternalApi
public class DecorateErrorFromResponseBodyUnmarshaller
implements Function<AwsXmlUnmarshallingContext, AwsXmlUnmarshallingContext> {
    private static final String ERROR_IN_SUCCESS_BODY_ELEMENT_NAME = "Error";
    private final Function<XmlElement, Optional<XmlElement>> errorRootLocationFunction;

    private DecorateErrorFromResponseBodyUnmarshaller(Function<XmlElement, Optional<XmlElement>> errorRootLocationFunction) {
        this.errorRootLocationFunction = errorRootLocationFunction;
    }

    public static DecorateErrorFromResponseBodyUnmarshaller of(Function<XmlElement, Optional<XmlElement>> errorRootFunction) {
        return new DecorateErrorFromResponseBodyUnmarshaller(errorRootFunction);
    }

    @Override
    public AwsXmlUnmarshallingContext apply(AwsXmlUnmarshallingContext context) {
        Optional<XmlElement> parsedRootXml = Optional.ofNullable(context.parsedRootXml());
        if (!context.sdkHttpFullResponse().isSuccessful()) {
            Optional<XmlElement> parsedErrorXml = parsedRootXml.flatMap(this.errorRootLocationFunction);
            return context.toBuilder().isResponseSuccess(false).parsedErrorXml(parsedErrorXml.orElse(null)).build();
        }
        Optional<XmlElement> parsedErrorXml = parsedRootXml.isPresent() ? DecorateErrorFromResponseBodyUnmarshaller.getErrorRootFromSuccessBody(context.parsedRootXml()) : Optional.empty();
        return parsedErrorXml.map(xmlElement -> context.toBuilder().isResponseSuccess(false).parsedErrorXml((XmlElement)xmlElement).build()).orElseGet(() -> context.toBuilder().isResponseSuccess(true).build());
    }

    private static Optional<XmlElement> getErrorRootFromSuccessBody(XmlElement document) {
        return ERROR_IN_SUCCESS_BODY_ELEMENT_NAME.equals(document.elementName()) ? Optional.of(document) : Optional.empty();
    }
}

