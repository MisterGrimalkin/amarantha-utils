package net.amarantha.utils.properties;

import com.sun.org.apache.xerces.internal.impl.dv.xs.BooleanDV;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
        for (String arg : args) {
            if ("-help".equals(arg) || "-h".equals(arg)) {
                System.out.println(helpText);
                System.exit(0);
            }
            if (arg.length() > 1 && arg.charAt(0) == '-') {
                String[] pieces = arg.substring(1).split("=");
                commandLineArgs.put(pieces[0], pieces.length == 2 ? pieces[1] : "");
            } else {
                System.out.println("Bad Argument: " + arg);
            }
        }
    }

    static void printArgs() {
        commandLineArgs.forEach((k, v) -> System.out.println(k + (v.isEmpty() ? " SET" : " = " + v)));
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
        File propsFile = new File("config/" + filename + ".properties");

        if (propsFile.exists()) {
            try (FileInputStream in = new FileInputStream(propsFile)) {
                properties.load(in);
            } catch (IOException ignored) {
            }

        } else if (create) {
            try (FileWriter writer = new FileWriter(propsFile)) {
                writer.write("# Application Properties");
            } catch (IOException ignored) {
            }
        }

        propertySets.put(filename, properties);
        dirty.put(filename, false);

        return properties;
    }

    private Properties appProps;
    private Properties defProps;
    protected Map<String, Properties> propertySets = new HashMap<>();
    private Map<String, Boolean> dirty = new HashMap<>();

    ////////////////
    // Set & Save //
    ////////////////

    private Properties applicationProps() {
        return getProps("application");
    }

    private Properties defaultProps() {
        return getProps("default");
    }

    private Properties getProps(String filename) {
        Properties result = propertySets.get(filename);
        if (result == null) {
            result = loadProperties(filename, true);
        }
        return result;
    }

    public void setProperty(String propName, String value) {
        setProperty(applicationProps(), propName, value);
    }

    public void setProperty(Properties properties, String propName, String value) {
        if (PLACEHOLDER.equals(value)) {
            throw new IllegalArgumentException("That value is the placeholder, sorry!");
        }
        properties.setProperty(propName, value);
        saveProperties();
    }

    protected void saveProperties() {
        propertySets.forEach((filename,properties)->{
            try (FileOutputStream out = new FileOutputStream("config/" + filename + ".properties")) {
                properties.store(out, "Properties: " + filename);
            } catch (IOException ignored) {
            }
        });
    }

    ////////////////
    // Get String //
    ////////////////

    public String getString(String propName) throws PropertyNotFoundException {
        return getString(applicationProps(), propName);
    }

    public String getString(String propName, String defaultValue) {
        return getString(applicationProps(), propName, defaultValue);
    }

    public String getString(Properties properties, String propName) throws PropertyNotFoundException {
        String propStr = properties.getProperty(propName);
        if (propStr == null || PLACEHOLDER.equals(propStr)) {
            propStr = defaultProps().getProperty(propName);
            if (propStr == null) {
                properties.setProperty(propName, PLACEHOLDER);
                saveProperties();
                throw new PropertyNotFoundException("Property '" + propName + "' not found");
            } else {
                setProperty(properties, propName, propStr);
            }
        }
        return propStr;
    }

    public String getString(Properties properties, String propName, String defaultValue) {
        try {
            return getString(properties, propName);
        } catch (PropertyNotFoundException e) {
            setProperty(properties, propName, defaultValue);
            return defaultValue;
        }
    }

    /////////////////
    // Get Boolean //
    /////////////////

    public Boolean getBoolean(String propName) throws PropertyNotFoundException {
        return getBoolean(applicationProps(), propName);
    }

    public Boolean getBoolean(String propName, Boolean defaultValue) throws PropertyNotFoundException {
        return getBoolean(applicationProps(), propName, defaultValue);
    }

    public Boolean getBoolean(Properties properties, String propName) throws PropertyNotFoundException {
        return Boolean.parseBoolean(getString(properties, propName));
    }

    public Boolean getBoolean(Properties properties, String propName, Boolean defaultValue) {
        try {
            return getBoolean(properties, propName);
        } catch (PropertyNotFoundException e) {
            setProperty(propName, defaultValue.toString());
            saveProperties();
            return defaultValue;
        }
    }

    /////////////////
    // Get Integer //
    /////////////////

    public Integer getInt(String propName) throws PropertyNotFoundException {
        return getInt(applicationProps(), propName);
    }

    public Integer getInt(String propName, Integer defaultValue) {
        return getInt(applicationProps(), propName, defaultValue);
    }

    private Integer getInt(Properties properties, String propName) throws PropertyNotFoundException {
        try {
            return Integer.parseInt(getString(properties, propName));
        } catch (NumberFormatException e) {
            throw new PropertyNotFoundException("Property '" + propName + "' should be a number");
        }
    }

    private Integer getInt(Properties properties, String propName, Integer defaultValue) {
        try {
            return getInt(properties, propName);
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
        return getRGB(applicationProps(), propName);
    }

    public RGB getRGB(Properties properties, String propName) throws PropertyNotFoundException {
        String rgb = getString(properties, propName);
        try {
            return RGB.parse(rgb);
        } catch (NumberFormatException ignored) {
        }
        throw new PropertyNotFoundException("Property '" + propName + "' is not a valid RGB colour");
    }

    ///////////////
    // Get Class //
    ///////////////

    public <T> Class<T> getClass(String propName) throws PropertyNotFoundException {
        return getClass(applicationProps(), propName);
    }

    public <T> Class<T> getClass(String propName, String packageName) throws PropertyNotFoundException {
        return getClass(applicationProps(), propName, packageName);
    }

    public <T> Class<T> getClass(Properties properties, String propName) throws PropertyNotFoundException {
        return getClass(properties, propName, "");
    }

    @SuppressWarnings("unchecked")
    public <T> Class<T> getClass(Properties properties, String propName, String packageName) throws PropertyNotFoundException {
        String className = "";
        try {
            className = packageName + getString(properties, propName);
            return (Class<T>)Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new PropertyNotFoundException("Could not find class '" + className + "'");
        }
    }

    ////////////////
    // IP Address //
    ////////////////

    public String getIp() {
        if (ip == null) {
            StringBuilder output = new StringBuilder();
            Process p;
            try {
                p = Runtime.getRuntime().exec("sh scripts/ip.sh");
                p.waitFor();
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line = "";
                while ((line = reader.readLine()) != null) {
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

        Properties properties = applicationProps();

        Annotation group = object.getClass().getAnnotation(PropertyGroup.class);
        if ( group!=null ) {
            properties = getProps(((PropertyGroup)group).value());
        }

        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (Field f : object.getClass().getDeclaredFields()) {
            Annotation a = f.getAnnotation(Property.class);
            if (a != null) {
                String propName = ((Property) a).value();
                try {
                    f.setAccessible(true);
                    if (f.getType() == String.class) {
                        if (propName.equals("IP")) {
                            f.set(object, getIp());
                        } else {
                            f.set(object, getString(properties, propName));
                        }
                    } else if (f.getType() == int.class || f.getType() == Integer.class) {
                        f.set(object, getInt(properties, propName));
                    } else if (f.getType() == boolean.class || f.getType() == Boolean.class) {
                        f.set(object, getBoolean(properties, propName));
                    } else if (f.getType() == RGB.class) {
                        f.set(object, getRGB(properties, propName));
                    } else if (f.getType()==Class.class) {
                        f.set(object, getClass(properties, propName));
                    } else {
                        getString(properties, propName);
                    }
                    Object value = f.get(object);
                    if (value == null) {
                        throw new PropertyNotFoundException("Null value");
                    }
                    result.put(propName, value.toString());
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    try {
                        if ( f.get(object)!=null ) {
                            setProperty(properties, propName, f.get(object).toString());
                        } else {
                            sb.append(propName).append("\n");
                        }
                    } catch (IllegalAccessException e2) {
                        sb.append(propName).append("\n");
                    }
                }
            }
        }

        if (!sb.toString().isEmpty()) {
            throw new PropertyNotFoundException("The following properties could not be loaded from " + APP_FILENAME + ":\n" + sb.toString());
        }

        return result;
    }

    private static final String PLACEHOLDER = "*** Please Set This Value ***";

    private static final String APP_FILENAME = "application";
    private static final String DEF_FILENAME = "default";

}
