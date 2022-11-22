package unit.test.demo.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class ResourceParseUtil {
    public static final String BASE_JSON_PATH = "data/json/";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> T parseObject(String relativePath, Class<T> cls) throws IOException {
        try (InputStream in = getResourcesInputStream(relativePath)) {
            return OBJECT_MAPPER.readValue(in, cls);
        }
    }

    public static <T> T parseCollection(String relativePath, TypeReference<T> ref) throws IOException {
        try (InputStream in = getResourcesInputStream(relativePath)) {
            return OBJECT_MAPPER.readValue(in, ref);
        }
    }

    public static InputStream getResourcesInputStream(String path) {
        ClassLoader clsLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = clsLoader.getResourceAsStream(path);
        if (in == null) {
            log.error("error load resources from path {},loader {}", path, clsLoader.getName());
            throw new IllegalArgumentException(String.format("can not found resource path %s", path));
        }
        return in;
    }
}
