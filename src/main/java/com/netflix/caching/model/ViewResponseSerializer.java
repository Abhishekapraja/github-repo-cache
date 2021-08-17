package com.netflix.caching.model;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

public class ViewResponseSerializer extends StdSerializer<ViewResponse> {

    public ViewResponseSerializer() {
        this(null);
    }

    public ViewResponseSerializer(Class<ViewResponse> t) {
        super(t);
    }

    @Override
    public void serialize(ViewResponse value, JsonGenerator jsonGenerator, SerializerProvider provider)
            throws IOException {

        jsonGenerator.writeStartArray();
        jsonGenerator.writeString(value.getRepoName());

        if (value.getData() instanceof Instant) {
            jsonGenerator.writeString(((Instant) value.getData()).toString());
        } else {
            jsonGenerator.writeNumber((int) value.getData());
        }

        jsonGenerator.writeEndArray();
    }
}