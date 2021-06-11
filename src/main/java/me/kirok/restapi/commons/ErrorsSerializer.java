package me.kirok.restapi.commons;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.validation.Errors;

@JsonComponent
public class ErrorsSerializer extends JsonSerializer<Errors> {

    @Override
    public void serialize(Errors errors, JsonGenerator gen, SerializerProvider serializers)
        throws IOException {
        gen.writeStartArray();

        errors.getFieldErrors().stream().forEach(error -> {
            try {

                gen.writeStartObject();
                gen.writeStringField("field", error.getField());
                gen.writeStringField("objectName", error.getObjectName());
                gen.writeStringField("code", error.getCode());
                gen.writeStringField("defaultMessage", error.getDefaultMessage());
                Object rejectedValue = error.getRejectedValue();
                if (rejectedValue != null) {
                    gen.writeStringField("rejectedValue", rejectedValue.toString());
                }

                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        errors.getGlobalErrors().forEach(error -> {
            try {

                gen.writeStartObject();
                gen.writeStringField("objectName", error.getObjectName());
                gen.writeStringField("code", error.getCode());
                gen.writeStringField("defaultMessage", error.getDefaultMessage());

                gen.writeEndObject();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        gen.writeEndArray();

    }
}
