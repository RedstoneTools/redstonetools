package com.domain.redstonetools.config;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * An abstraction for 'raw' configuration formats.
 */
public interface RawFormat {

    @SuppressWarnings("unchecked")
    static RawFormat ofYaml(Yaml yaml) {
        return new RawFormat() {
            @Override
            public void save(Writer writer, Object object) throws IOException {
                yaml.dump(object, writer);
            }

            @Override
            public Object load(Reader reader) throws IOException {
                return yaml.load(reader);
            }
        };
    }

    void save(Writer writer,
              Object object) throws IOException;

    <T> T load(Reader reader) throws IOException;

}
