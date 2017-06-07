package net.amarantha.utils.properties;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import net.amarantha.utils.colour.RGB;
import net.amarantha.utils.properties.entity.Property;
import net.amarantha.utils.properties.entity.PropertyGroup;
import net.amarantha.utils.properties.entity.PropertyNotFoundException;
import net.amarantha.utils.reflection.ReflectionUtils;

import javax.inject.Singleton;
import java.io.*;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.amarantha.utils.shell.Utility.log;

@SuppressWarnings("WeakerAccess")
@Singleton
public class PropertiesService {

    public PropertiesService(String filename) {
        this.filename = filename;
        loadFromFile();
    }

    protected final String filename;
    protected Map<String, Map> propsMap;

    /////////////////
    // Load & Save //
    /////////////////

    protected void loadFromFile() {
        try (FileReader reader = new FileReader(filename)) {
            YamlReader yaml = new YamlReader(reader);
            //noinspection unchecked
            propsMap = (Map<String, Map>) yaml.read();
        } catch (FileNotFoundException e) {
            propsMap = new HashMap<>();
            propsMap.put(GENERAL, new HashMap());
            saveToFile();
        } catch (IOException e) {
            log("Error reading settings file '"+filename+"'.\n" + e.getMessage());
            System.exit(1);
        }
        if ( propsMap==null ) {
            propsMap = new HashMap<>();
        }
    }

    protected void saveToFile() {
        try (FileWriter writer = new FileWriter(filename)) {
            YamlWriter yaml = new YamlWriter(writer);
            yaml.write(propsMap);
            yaml.close();
        } catch (IOException e) {
            log("Error writing settings file '"+filename+"'.\n" + e.getMessage());
            System.exit(1);
        }
    }

    /////////
    // Set //
    /////////

    public void set(String key, Object value) {
        set(GENERAL, key, value);
    }

    @SuppressWarnings("unchecked")
    public void set(String groupName, String key, Object value) {
        if ( value==null ) {
            throw new IllegalArgumentException("Cannot set property '" + groupName + "/" + key + "' to null.");
        }
        Object outputObject;
        if ( Class.class.isAssignableFrom(value.getClass()) ) {
            outputObject = ((Class<?>) value).getName();
        } else if ( Collection.class.isAssignableFrom(value.getClass()) ) {
            outputObject = new ArrayList<>();
            ((Collection)value).forEach((e) -> ((List)outputObject).add(e.toString()));
        } else {
            outputObject = value.toString();
        }
        Map group = propsMap.get(groupName);
        if ( group==null ) {
            group = new HashMap<String, String>();
            propsMap.put(groupName, group);
        }
        //noinspection unchecked
        group.put(key, outputObject);
        saveToFile();
    }

    ///////////
    // Query //
    ///////////

    public boolean isSet(String key) {
        return isSet(GENERAL, key);
    }

    public boolean isSet(String groupName, String key) {
        Map group = propsMap.get(groupName);
        return group != null && group.containsKey(key);
    }

    /////////
    // Get //
    /////////

    @SuppressWarnings("unchecked")
    public <S, T> T get(String groupName, String key, Function<S, T> parser) throws PropertyNotFoundException {
        Map group = propsMap.get(groupName);
        if ( group==null ) {
            throw new PropertyNotFoundException(groupName, key, "Property group '" + groupName + "' not found.");
        }
        Object obj = group.get(key);
        if ( obj==null ) {
            throw new PropertyNotFoundException(groupName, key);
        }
        return parser.apply((S)obj);
    }

    public <T> T getOrDefault(String groupName, String key, T def, Function<String, T> parser) {
        try {
            return get(groupName, key, parser);
        } catch (PropertyNotFoundException e) {
            set(groupName, key, def);
            return def;
        }
    }

    public <T> List<T> getList(String groupName, String key, Function<String, T> innerParser) throws PropertyNotFoundException {
        return get(groupName, key, (Function<List<String>, List<T>>) strings -> {
            List<T> result = new ArrayList<>();
            strings.forEach((str)->result.add(innerParser.apply(str)));
            return result;
        });
    }

    ////////////////
    // Get String //
    ////////////////

    public String getString(String key) throws PropertyNotFoundException {
        return getString(GENERAL, key);
    }

    public String getString(String groupName, String key) throws PropertyNotFoundException {
        return get(groupName, key, String::toString);
    }

    public String getStringOrDefault(String key, String def) {
        return getStringOrDefault(GENERAL, key, def);
    }

    public String getStringOrDefault(String groupName, String key, String def) {
        return getOrDefault(groupName, key, def, String::toString);
    }

    public List<String> getStringList(String key) throws PropertyNotFoundException {
        return getStringList(GENERAL, key);
    }

    public List<String> getStringList(String groupName, String key) throws PropertyNotFoundException {
        return getList(groupName, key, String::toString);
    }

    /////////////////
    // Get Boolean //
    /////////////////

    public Boolean getBoolean(String key) throws PropertyNotFoundException {
        return getBoolean(GENERAL, key);
    }

    public Boolean getBoolean(String groupName, String key) throws PropertyNotFoundException {
        return get(groupName, key, Boolean::parseBoolean);
    }

    public Boolean getBooleanOrDefault(String key, Boolean def) {
        return getBooleanOrDefault(GENERAL, key, def);
    }

    public Boolean getBooleanOrDefault(String groupName, String key, Boolean def) {
        return getOrDefault(groupName, key, def, Boolean::parseBoolean);
    }

    /////////////////
    // Get Integer //
    /////////////////

    public Integer getInt(String key) throws PropertyNotFoundException {
        return getInt(GENERAL, key);
    }

    public Integer getInt(String groupName, String key) throws PropertyNotFoundException {
        return get(groupName, key, (Function<String,Integer>)Integer::parseInt);
    }

    public Integer getIntOrDefault(String key, Integer def) {
        return getIntOrDefault(GENERAL, key, def);
    }

    public Integer getIntOrDefault(String groupName, String key, Integer def) {
        return getOrDefault(groupName, key, def, Integer::parseInt);
    }

    ////////////////
    // Get Double //
    ////////////////

    public Double getDouble(String key) throws PropertyNotFoundException {
        return getDouble(GENERAL, key);
    }

    public Double getDouble(String groupName, String key) throws PropertyNotFoundException {
        return get(groupName, key, Double::parseDouble);
    }

    public Double getDoubleOrDefault(String key, Double def) {
        return getDoubleOrDefault(GENERAL, key, def);
    }

    public Double getDoubleOrDefault(String groupName, String key, Double def) {
        return getOrDefault(groupName, key, def, Double::parseDouble);
    }

    /////////////
    // Get RGB //
    /////////////

    public RGB getRgb(String key) throws PropertyNotFoundException {
        return getRgb(GENERAL, key);
    }

    public RGB getRgb(String groupName, String key) throws PropertyNotFoundException {
        return get(groupName, key, RGB::parse);
    }

    public RGB getRgbOrDefault(String key, RGB def) {
        return getRgbOrDefault(GENERAL, key, def);
    }

    public RGB getRgbOrDefault(String groupName, String key, RGB def) {
        return getOrDefault(groupName, key, def, RGB::parse);
    }

    public List<RGB> getRgbList(String key) throws PropertyNotFoundException {
        return getRgbList(GENERAL, key);
    }

    public List<RGB> getRgbList(String groupName, String key) throws PropertyNotFoundException {
        return getList(groupName, key, RGB::parse);
    }

    ///////////////
    // Get Class //
    ///////////////

    public <T> Class<T> getClass(String key) throws PropertyNotFoundException {
        return getClass(GENERAL, key);
    }

    public <T> Class<T> getClass(String groupName, String key) throws PropertyNotFoundException {
        String className = getString(groupName, key);
        try {
            //noinspection unchecked
            return (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new PropertyNotFoundException(groupName, key, "Class '" + className + "' not found");
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
            log(e.getMessage());
            System.exit(1);
        }
        return null;
    }

    public Map<String, String> injectProperties(Object object) throws PropertyNotFoundException {
        return injectProperties(object, null);
    }

    public Map<String, String> injectProperties(Object object, final BiFunction<Class<?>, String, Object> customType) throws PropertyNotFoundException {

        Annotation group = object.getClass().getAnnotation(PropertyGroup.class);
        String groupName = group==null ? GENERAL : ((PropertyGroup)group).value();

        Map<String, String> result = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        ReflectionUtils.iterateAnnotatedFields(object, Property.class, (f, a)-> {
            if (a != null) {
                String propKey = a.value();
                try {
                    if (propKey.equals("IP")) {
                        f.set(object, getIp());
                    } else {
                        String stringValue = getString(groupName, propKey);
                        ReflectionUtils.reflectiveSet(object, f, stringValue, customType);
                        Object value = f.get(object);
                        if (value == null) {
                            throw new PropertyNotFoundException(groupName, propKey, "Null value");
                        }
                        result.put(propKey, value.toString());
                    }
                } catch (IllegalAccessException | PropertyNotFoundException e) {
                    try {
                        if (f.get(object) != null) {
                            set(groupName, propKey, f.get(object));
                        } else {
                            sb.append(propKey).append("\n");
                        }
                    } catch (IllegalAccessException e2) {
                        sb.append(propKey).append("\n");
                    }
                }
            }
        });

        if (!sb.toString().isEmpty()) {
            throw new PropertyNotFoundException(null, null, "The following properties could not be loaded:\n" + sb.toString());
        }

        return result;
    }

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
                log(helpText);
                System.exit(0);
            }
            if (arg.length() > 1 && arg.charAt(0) == '-') {
                String[] pieces = arg.substring(1).split("=");
                commandLineArgs.put(pieces[0], pieces.length == 2 ? pieces[1] : "");
            } else {
                log("Bad Argument: " + arg);
            }
        }
    }

    public static boolean isArgumentPresent(String argName) {
        return commandLineArgs.containsKey(argName);
    }

    public static String getArgumentValue(String argName) {
        return commandLineArgs.get(argName);
    }

    static void printArgs() {
        commandLineArgs.forEach((k, v) -> log(k + (v.isEmpty() ? " SET" : " = " + v)));
    }

    private static final String GENERAL = "General";

}
