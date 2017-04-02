package net.amarantha.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.utils.file.FileService;
import net.amarantha.utils.file.FileServiceImpl;
import net.amarantha.utils.file.FileServiceMock;
import net.amarantha.utils.http.HttpService;
import net.amarantha.utils.http.HttpServiceImpl;
import net.amarantha.utils.http.HttpServiceMock;
import net.amarantha.utils.midi.MidiService;
import net.amarantha.utils.midi.MidiServiceImpl;
import net.amarantha.utils.midi.MidiServiceMock;
import net.amarantha.utils.osc.OscService;
import net.amarantha.utils.osc.OscServiceImpl;
import net.amarantha.utils.osc.OscServiceMock;
import net.amarantha.utils.properties.PropertiesService;
import net.amarantha.utils.properties.PropertiesServiceMock;

public class UtilityModule extends AbstractModule {

    private final boolean live;

    public UtilityModule() {
        this(true);
    }

    public UtilityModule(boolean live) {
        this.live = live;
    }

    @Override
    protected void configure() {
        if (live) {
            bind(PropertiesService.class).in(Scopes.SINGLETON);
            bind(FileService.class).to(FileServiceImpl.class).in(Scopes.SINGLETON);
            bind(MidiService.class).to(MidiServiceImpl.class).in(Scopes.SINGLETON);
            bind(HttpService.class).to(HttpServiceImpl.class).in(Scopes.SINGLETON);
            bind(OscService.class).to(OscServiceImpl.class).in(Scopes.SINGLETON);
        } else {
            bind(PropertiesService.class).to(PropertiesServiceMock.class).in(Scopes.SINGLETON);
            bind(FileService.class).to(FileServiceMock.class).in(Scopes.SINGLETON);
            bind(MidiService.class).to(MidiServiceMock.class).in(Scopes.SINGLETON);
            bind(HttpService.class).to(HttpServiceMock.class).in(Scopes.SINGLETON);
            bind(OscService.class).to(OscServiceMock.class).in(Scopes.SINGLETON);
        }
    }

}
