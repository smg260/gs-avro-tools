//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ir.fq.common.schema.avro.generated;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.specific.AvroGenerated;

@AvroGenerated
public enum MessageType {
    PIXEL_HIT,
    AD_COLLISION,
    AD_CLICK,
    VIEWABILITY,
    MOUSEOVER,
    ANDROID_HIT,
    SCORING_REQUEST_AND_RESPONSE,
    WEB_RTC,
    ENRICHED,
    SCORED;

    public static final Schema SCHEMA$ = (new Parser()).parse("{\"type\":\"enum\",\"name\":\"MessageType\",\"namespace\":\"ir.fq.common.schema.avro.generated\",\"symbols\":[\"PIXEL_HIT\",\"AD_COLLISION\",\"AD_CLICK\",\"VIEWABILITY\",\"MOUSEOVER\",\"ANDROID_HIT\",\"SCORING_REQUEST_AND_RESPONSE\",\"WEB_RTC\",\"ENRICHED\",\"SCORED\"]}");

    private MessageType() {
    }

    public static Schema getClassSchema() {
        return SCHEMA$;
    }
}
