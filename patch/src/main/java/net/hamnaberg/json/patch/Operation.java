package net.hamnaberg.json.patch;

import net.hamnaberg.json.Json;
import net.hamnaberg.json.pointer.JsonPointer;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class Operation {
    enum Op {
        Add("add"),
        Remove("remove"),
        Replace("replace"),
        Move("move"),
        Copy("copy"),
        Test("test");

        public final String value;

        Op(String value) {
            this.value = value;
        }

        public static Op fromString(String name) {
            for (Op op : values()) {
                if (op.value.equals(name.trim())) {
                    return op;
                }
            }
            throw new IllegalArgumentException(String.format("'%s' is not a legal value for Op", name));
        }
    }

    public Op op;
    public Optional<JsonPointer> from;
    public JsonPointer path;
    public Optional<Json.JValue> value;

    public Operation(Op op, Optional<String> from, String path, Optional<Json.JValue> value) {
        this.op = op;
        this.from = from.map(JsonPointer::compile);
        this.path = JsonPointer.compile(path);
        this.value = value;
        if (EnumSet.of(Op.Add, Op.Replace, Op.Test).contains(op)) {
            if (!value.isPresent()) {
                throw new IllegalArgumentException(String.format("Op '%s' requires a value", op.value));
            }
        }
    }

    public static Operation fromJson(Json.JObject object) {
        return new Operation(
                Op.fromString(object.getAsStringOrEmpty("op")),
                object.getAsString("from"),
                object.getAsStringOrEmpty("path"),
                object.get("value")
        );
    }

    public Json.JObject toJson() {
        Map<String, Json.JValue> map = new LinkedHashMap<>();
        map.put("op", Json.jString(op.value));
        from.ifPresent(v -> map.put("from", Json.jString(v.toString())));
        map.put("path", Json.jString(path.toString()));
        value.ifPresent(v -> map.put("value", v));
        return Json.jObject(map);
    }
}
