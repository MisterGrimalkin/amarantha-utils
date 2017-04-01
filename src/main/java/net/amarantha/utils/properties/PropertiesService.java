package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;

import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Singleton
public class PropertiesService {

    ////////////////////////////
    // Command Line Arguments //
    ////////////////////////////

    private static final Map<String, String> commandLineArgs = new HashMap<>();

    public static void processArgs(String[] args) {
        processArgs(args, "No help is available.");
    }

    public static void processArgs(String[] args, String helpText) {
        for ( String arg : args ) {
            if ( "-help".equals(arg) || "-h".equals(arg) ) {
                System.out.println(helpText);
                System.exit(0);
            }
            if ( arg.length()>1 && arg.charAt(0)=='-' ) {
                String[] pieces = arg.substring(1).split("=");
                commandLineArgs.put(pieces[0], pieces.length==2 ? pieces[1] : "");
            } else {
                System.out.println("Bad Argument: " + arg);
            }
        }
    }

    static void printArgs() {
        commandLineArgs.forEach((k, v)-> System.out.println(k+(v.isEmpty() ? " SET" : " = "+v)));
    }

    public boolean isArgumentPresent(String argName) {
        return commandLineArgs.containsKey(argName);
    }

    public String getArgumentValue(String argName) {
        return commandLineArgs.get(argName);
    }

    //////////////////////
    // Properties Files //
    //////////////////////

    public PropertiesService() {
        defProps = loadProperties(DEF_FILENAME, false);
        appProps = loadProperties(APP_FILENAME, true);
    }

    protected Properties loadProperties(String filename, boolean create) {

        Properties properties = new Properties();
        File propsFile = new File(filename);

        if ( propsFile.exists()) {
            try (FileInputStream in = new FileInputStream(propsFile)) {
                properties.load(in);
            } catch ( IOException ignored ) {}

        } else if ( create ) {
            try (FileWriter writer = new FileWriter(propsFile)) {
                writer.write("# Application Properties");
            } catch ( IOException ignored ) {}
        }

        return properties;
    }

    private Properties appProps;
    private Properties defProps;

    ////////////////
    // Set & Save //
    ////////////////

    public void setProperty(String propName, String value) {
        if ( PLACEHOLDER.equals(value) ) {
            throw new IllegalArgumentException("That value is the placeholder, sorry!");
        }
        appProps.setProperty(propName, value);
        saveProperties();
    }

    protected void saveProperties() {
        try (FileOutputStream out = new FileOutputStream(APP_FILENAME)) {
            appProps.store(out, "Application Properties");
        } catch (IOException ignored) {}
    }

    ////////////////
    // Get String //
    ////////////////

    public String getString(String propName) throws PropertyNotFoundException {
        String propStr = appProps.getProperty(propName);
        if ( propStr==null || PLACEHOLDER.equals(propStr) ) {
            propStr = defProps.getProperty(propName);
            if ( propStr==null ) {
                appProps.setProperty(propName, PLACEHOLDER);
                saveProperties();
                throw new PropertyNotFoundException("Property '" + propName + "' not found in " + APP_FILENAME);
            } else {
                setProperty(propName, propStr);
            }
        }
        return propStr;
    }

    public String getString(String propName, String defaultValue) {
        try {
            return getString(propName);
        } catch (PropertyNotFoundException e) {
            setProperty(propName, defaultValue);
            return defaultValue;
        }
    }

    /////////////////
    // Get Integer //
    /////////////////

    public Integer getInt(String propName) throws PropertyNotFoundException {
        try {
            return Integer.parseInt(getString(propName));
        } catch ( NumberFormatException e ) {
            throw new PropertyNotFoundException("Property '" + propName + "' in " + APP_FILENAME + " should be a number");
        }
    }

    public Integer getInt(String propName, Integer defaultValue) {
        try {
            return getInt(propName);
        } catch (PropertyNotFoundException e) {
            setProperty(propName, defaultValue.toString());
            saveProperties();
            return defaultValue;
        }
    }

    /////////////
    // Get RGB //
    /////////////

    public RGB getRGB(String propName) throws PropertyNotFoundException {
        String rgb = getString(propName);
        try {
            return RGB.parse(rgb);
        } catch ( NumberFormatException ignored ) {}
        throw new PropertyNotFoundException("Property '" + propName + "' is not a valid RGB colour");
    }

    ///////////////
    // Get Class //
    ///////////////

    public Class<?> getClass(String propName) throws PropertyNotFoundException {
        return getClass(propName, "");
    }

    public Class<?> getClass(String propName, String packageName) throws PropertyNotFoundException {
        String className = "";
        try {
            className = packageName + getString(propName);
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new PropertyNotFoundException("Could not find class '" + className + "'");
        }
    }

    ////////////////
    // IP Address //
    ////////////////

    public String getIp() {
        if ( ip==null ) {
            StringBuilder output = new StringBuilder();
            Process p;
            try {
                p = Runtime.getRuntime().exec("sh scripts/ip.sh");
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = reader.readLine())!= null) {
                    output.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            ip = output.toString().trim();
        }
        return ip;
    }

    private String ip;

    ////////////////////////
    // Property Injection //
    ////////////////////////

    public Map<String, String> injectPropertiesOrExit(Object object) {
        try {
            return injectProperties(object);
        } catch (PropertyNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Map<String, String> injectProperties(Object object) throws PropertyNotFoundException {

        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (Field f : object.getClass().getDeclaredFields() ) {
            Annotation a = f.getAnnotation(Property.class);
            if ( a!=null ) {
                String propName = ((Property)a).value();
                try {
                    f.setAccessible(true);
                    if ( f.getType()==String.class ) {
                        if ( propName.equals("IP") ) {
                            f.set(object, getIp());
                        } else {
                            f.set(object, getString(propName));
                        }
                    } else if (f.getType() == int.class || f.getType() == Integer.class) {
                        f.set(object, getInt(propName));
                    } else if (f.getType() == RGB.class ) {
                        f.set(object, getRGB(propName));
                    } else {
                        getString(propName);
                    }
                    Object value = f.get(object);
                    if ( value==null ) {
                        throw new PropertyNotFoundException("Null value");
                    }
                    result.put(propName, value.toString());
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    sb.append(propName).append("\n");
                }
            }
        }

        if ( !sb.toString().isEmpty() ) {
            throw new PropertyNotFoundException("The following properties could not be loaded from " + APP_FILENAME + ":\n" + sb.toString());
        }

        return result;
    }

    private static final String PLACEHOLDER = "*** Please Set This Value ***";

    private static final String APP_FILENAME = "application.properties";
    private static final String DEF_FILENAME = "default.properties";

}
