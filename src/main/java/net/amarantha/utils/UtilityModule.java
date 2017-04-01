package net.amarantha.utils;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import net.amarantha.utils.file.FileService;
import net.amarantha.utils.file.FileServiceImpl;

public class UtilityModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(FileService.class).to(FileServiceImpl.class).in(Scopes.SINGLETON);
    }

}
