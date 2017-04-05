package net.amarantha.utils.service;

import com.google.inject.Inject;
import net.amarantha.utils.properties.PropertiesService;

import static net.amarantha.utils.shell.Utility.log;

public abstract class AbstractService {

    @Inject protected PropertiesService props;

    public final String name;

    public AbstractService(String name) {
        this.name = name;
    }

    public void setup() {
        props.injectPropertiesOrExit(this);
    }

    public final void start() {
        log("Starting " + name + "...");
        onStart();
    }

    public final void stop() {
        log("Stopping " + name + "...");
        onStop();
    }

    protected abstract void onStart();

    protected abstract void onStop();

}
