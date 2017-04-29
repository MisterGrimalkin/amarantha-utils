package net.amarantha.utils.properties;

import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import net.amarantha.utils.reflection.ReflectionUtils;

import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

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

        return properties;
    }

    protected Map<String, Properties> propertySets = new HashMap<>();

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
        String[] pieces = propName.split("/");
        if (pieces.length > 1) {
            properties = loadProperties(pieces[0], true);
            propName = pieces[1];
        }
        properties.setProperty(propName, value);
        saveProperties();
    }

    protected void saveProperties() {
        propertySets.forEach((filename, properties) -> {
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
        String[] pieces = propName.split("/");
        if (pieces.length > 1) {
            properties = loadProperties(pieces[0], true);
            propName = pieces[1];
        }
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
            return (Class<T>) Class.forName(className);
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
        return injectPropertiesOrExit(object, null);
    }

    public Map<String, String> injectPropertiesOrExit(Object object, final BiFunction<Class<?>, String, Object> customType) {
        try {
            return injectProperties(object, customType);
        } catch (PropertyNotFoundException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Map<String, String> injectProperties(Object object) throws PropertyNotFoundException {
        return injectProperties(object, null);
    }

    public Map<String, String> injectProperties(Object object, final BiFunction<Class<?>, String, Object> customType) throws PropertyNotFoundException {

        final Properties properties;
        Annotation group = object.getClass().getAnnotation(PropertyGroup.class);
        if (group != null) {
            properties = getProps(((PropertyGroup) group).value());
        } else {
            properties = applicationProps();
        }

        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        ReflectionUtils.iterateAnnotatedFields(object, Property.class, (f, a)-> {
            if (a != null) {
                String propName = a.value();
                try {
                    if (propName.equals("IP")) {
                        f.set(object, getIp());
                    } else {
                        String stringValue = getString(properties, propName);
                        ReflectionUtils.reflectiveSet(object, f, stringValue, customType);
                        Object value = f.get(object);
                        if (value == null) {
                            throw new PropertyNotFoundException("Null value");
                        }
                        result.put(propName, value.toString());
                    }
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    try {
                        if (f.get(object) != null) {
                            setProperty(properties, propName, f.get(object).toString());
                        } else {
                            sb.append(propName).append("\n");
                        }
                    } catch (IllegalAccessException e2) {
                        sb.append(propName).append("\n");
                    }
                }
            }
        });

        if (!sb.toString().isEmpty()) {
            throw new PropertyNotFoundException("The following properties could not be loaded from " + APP_FILENAME + ":\n" + sb.toString());
        }

        return result;
    }

    private static final String PLACEHOLDER = "*** Please Set This Value ***";

    private static final String APP_FILENAME = "application";
    private static final String DEF_FILENAME = "default";

}
