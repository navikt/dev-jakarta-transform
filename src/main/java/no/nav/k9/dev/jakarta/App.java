package no.nav.k9.dev.jakarta;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.transformer.Transformer;
import org.eclipse.transformer.cli.JakartaTransformerCLI;
import org.eclipse.transformer.jakarta.JakartaTransform;

/**
 * Transform a given code base to jakarta with provided default transformation rules
 *
 * (adapted from JakartaTransformer in eclipse-transformer.cli project)
 */
public class App {

    private static final class ReverseLoader implements Function<String, URL> {
        private final Map<String, URL> cache = new LinkedHashMap<>();
        private final Function<String, URL> baseLoader;

        public ReverseLoader(Function<String, URL> baseLoader) {
            this.baseLoader = baseLoader;
        }

        @Override
        public URL apply(String key) {
            if (cache.containsKey(key)) {
                return cache.get(key);
            }

            if (TEXT_MASTER_PROPERTIES.equals(key)) {
                // no conversion, holds master list of filetypes to convert
                return App.class.getResource(key);
            } else {
                var regularUrl = baseLoader.apply(key);
                try (var stream = regularUrl.openStream()) {
                    var copyFrom = new Properties();
                    copyFrom.load(stream);
                    var copyTo = new Properties();
                    copyFrom.stringPropertyNames().stream().forEach(k -> {
                        copyTo.put(copyFrom.getProperty(k), k);
                    });

                    var f = File.createTempFile("reverse-" + key, ".properties");
                    try (var out = new FileOutputStream(f)) {
                        copyTo.store(out, "Reversed key-value");
                    }

                    var newUrl = f.toURI().toURL();
                    cache.put(key, newUrl);
                    return newUrl;
                } catch (IOException e) {
                    throw new IllegalStateException("key=" + key + ", url=" + regularUrl, e);
                }
            }
        }

    }

    private static final String TEXT_MASTER_PROPERTIES = "jakarta-text-master.properties";
    private static Set<String> LOCALS = Set.of("jakarta-renames.properties", "jakarta-direct.properties", TEXT_MASTER_PROPERTIES);

    public static void main(String[] args) throws Exception {

        final String reverseFlag = "--reverse";
        List<String> strArgs = args == null || args.length == 0 ? List.of() : Arrays.asList(args);
        final boolean reverse = strArgs.stream().filter(a -> reverseFlag.equals(a)).findAny().isPresent();
        List<String> strArgsClean = strArgs.stream().filter(a -> !reverseFlag.equals(a)).collect(Collectors.toList());

        var cli = new JakartaTransformerCLI(System.out, System.err, strArgsClean.toArray(new String[0]));
        cli.setOptionDefaults(getLoader(reverse), getOptionsDefault(reverse));

        var rc = JakartaTransformerCLI.runWith(cli);
        System.exit(rc.equals(Transformer.ResultCode.SUCCESS_RC) ? 0 : 1);
    }

    private static Map<String, String> getOptionsDefault(boolean reverse) {
        Map<String, String> optionDefaults = new LinkedHashMap<>(JakartaTransform.getOptionDefaults());
        if (reverse) {
            // non-compatible with reverse lookup
            optionDefaults.remove("bundles");
            optionDefaults.remove("versions");
            optionDefaults.remove("per-class-constant");
        }
        return Collections.unmodifiableMap(optionDefaults);
    }

    public static Function<String, URL> getLoader(boolean reverse) {

        if (reverse) {
            return new ReverseLoader(getLoader(false));
        } else {
            return (key) -> {
                if (LOCALS.contains(key) || key.startsWith("transformer-")) {
                    return App.class.getResource(key);
                } else {
                    return JakartaTransform.getRuleLoader().apply(key);
                }
            };
        }
    }
}
