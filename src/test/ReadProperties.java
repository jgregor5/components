package test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author julian
 */
public class ReadProperties {
    
    public static void main(String[] args) throws IOException {

        // both work: current and absolute
        InputStream is = ReadProperties.class.getResourceAsStream("my.properties");
        //InputStream is = ReadProperties.class.getResourceAsStream("/test/my.properties");
        Properties props = new Properties();
        props.load(is);
        System.out.println(props.getProperty("test.value"));
    }
}
