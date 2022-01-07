package no.nav.k9.dev.jakarta;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.eclipse.transformer.Transformer;
import org.eclipse.transformer.AppOption;
import org.eclipse.transformer.cli.JakartaTransformerCLI;
import org.eclipse.transformer.Transformer.ResultCode;
import org.eclipse.transformer.jakarta.JakartaTransform;
import org.slf4j.LoggerFactory;

/**
 * Transform a given code base to jakarta with provided default transformation rules
 *
 * (adapted from JakartaTransformer in eclipse-transformer.cli project)
 */
public class App {
    
    public static void main(String[] args) throws Exception {
        var cli = new JakartaTransformerCLI(System.out, System.err, args);
        cli.setOptionDefaults(getLoader(), JakartaTransform.getOptionDefaults());
        
        var rc = JakartaTransformerCLI.runWith(cli);
        System.exit(rc.equals(Transformer.ResultCode.SUCCESS_RC) ? 0 : 1);
    }

    public static Set<String> LOCALS = Set.of("jakarta-renames.properties", "jakarta-direct.properties", "jakarta-text-master.properties");

    public static Function<String, URL> getLoader(){
        return (key) -> {
             if(LOCALS.contains(key)){
                 return App.class.getResource(key);
             } else {
                 return JakartaTransform.getRuleLoader().apply(key);
             }
        };
    }
}
