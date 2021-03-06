package net.hamnaberg.json.nativeparser;

import net.hamnaberg.json.Json;
import net.hamnaberg.json.io.JsonSerializer;

import java.io.IOException;
import java.io.Writer;
import java.util.function.Supplier;

public class NativeJsonSerializer implements JsonSerializer {
    private final PrettyPrinter printer;

    public NativeJsonSerializer(PrettyPrinter printer) {
        this.printer = printer;
    }

    public static NativeJsonSerializer nospaces() {
        return new NativeJsonSerializer(PrettyPrinter.nospaces());
    }

    public static NativeJsonSerializer spaces2() {
        return new NativeJsonSerializer(PrettyPrinter.spaces2());
    }

    public static NativeJsonSerializer spaces4() {
        return new NativeJsonSerializer(PrettyPrinter.spaces4());
    }

    @Override
    public void write(Json.JValue value, Writer writer) {
        try (Writer w = writer) {
            w.write(printer.writeString(value));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
