package no.nav.k9.dev.jakarta;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.transformer.AppOption;
import org.eclipse.transformer.Transformer;
import org.eclipse.transformer.cli.JakartaTransformerCLI;
import org.slf4j.LoggerFactory;

/**
 * Transform a given code base to jakarta with provided default transformation rules
 *
 * (adapted from JakartaTransformer in eclipse-transformer.cli project)
 */
public class App {

    public static void main(String[] args) throws Exception {
        var transformer = new Transformer(LoggerFactory.getLogger(App.class),
                new JakartaTransformerCLI(System.out, System.err, args));

        var rc = transformer.run();
        System.exit(rc.equals(Transformer.ResultCode.SUCCESS_RC) ? 0 : 1);
    }

    public static final String DEFAULT_RENAMES_REFERENCE = "jakarta-renames.properties";
    public static final String DEFAULT_VERSIONS_REFERENCE = "jakarta-versions.properties";
    public static final String DEFAULT_BUNDLES_REFERENCE = "jakarta-bundles.properties";
    public static final String DEFAULT_DIRECT_REFERENCE = "jakarta-direct.properties";
    public static final String DEFAULT_MASTER_TEXT_REFERENCE = "jakarta-text-master.properties";
    public static final String DEFAULT_PER_CLASS_CONSTANT_MASTER_REFERENCE = "jakarta-per-class-constant-master.properties";

    public static Map<AppOption, String> getOptionDefaults() {
        Map<AppOption, String> optionDefaults = new HashMap<>();

        optionDefaults.put(AppOption.RULES_RENAMES, DEFAULT_RENAMES_REFERENCE);
        optionDefaults.put(AppOption.RULES_VERSIONS, DEFAULT_VERSIONS_REFERENCE);
        optionDefaults.put(AppOption.RULES_BUNDLES, DEFAULT_BUNDLES_REFERENCE);
        optionDefaults.put(AppOption.RULES_DIRECT, DEFAULT_DIRECT_REFERENCE);
        optionDefaults.put(AppOption.RULES_MASTER_TEXT, DEFAULT_MASTER_TEXT_REFERENCE);
        optionDefaults.put(AppOption.RULES_PER_CLASS_CONSTANT, DEFAULT_PER_CLASS_CONSTANT_MASTER_REFERENCE);

        return optionDefaults;
    }
}
