package no.nav.k9.dev.jakarta;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

public class AppReverseTest {

    private static final String TEST_PROPERTIES = "transformer-beans-xml.properties";

    @SuppressWarnings("resource")
    private static Properties loadProps(URL originalUrl) throws IOException {
        var props = new Properties();
        props.load(originalUrl.openStream());
        return props;
    }

    @Test
    public void test_property_keys() throws Exception {
        var props= loadProps(App.getLoader(false).apply(TEST_PROPERTIES));
        Assert.assertEquals("https:", props.getProperty("http:"));
    }

    @Test
    public void test_reverse_property_keys() throws Exception {
        var props = loadProps(App.getLoader(true).apply(TEST_PROPERTIES));
        Assert.assertEquals("http:", props.getProperty("https:"));
    }
}
