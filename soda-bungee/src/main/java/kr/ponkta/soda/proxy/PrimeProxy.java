package kr.ponkta.soda.proxy;

import com.google.common.io.ByteStreams;
import kr.ponkta.soda.proxy.module.ModuleHandler;
import kr.ponkta.soda.proxy.module.bungee.BungeeHandler;
import kr.ponkta.soda.proxy.module.bungee.listener.BungeeListener;
import kr.ponkta.soda.proxy.module.command.ServerCommand;
import kr.ponkta.soda.proxy.module.database.mongo.MongoModule;
import kr.ponkta.soda.proxy.module.database.redis.JedisModule;
import kr.ponkta.soda.proxy.module.profile.ProfileHandler;
import kr.ponkta.soda.proxy.module.profile.listener.ProfileListener;
import kr.ponkta.soda.proxy.module.rank.RankHandler;
import kr.ponkta.soda.proxy.module.server.ServerHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.Arrays;

@Getter
public class PrimeProxy extends Plugin {

    @Getter private static PrimeProxy instance;

    private ModuleHandler moduleHandler;
    private Configuration config;

    @Override
    public void onEnable() {
        instance = this;

        loadHelpers();
        loadHandlers();
        registerCommands();
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
    }

    private void loadHandlers() {
        this.moduleHandler = new ModuleHandler();

        Arrays.asList(
                new MongoModule(),
                new JedisModule(),
                new RankHandler(),
                new ProfileHandler(),
                new ServerHandler(),
                new BungeeHandler()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }

    @SneakyThrows
    private void loadHelpers() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml");
                     OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to create configuration file", e);
            }
        }

        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
    }

    private void registerCommands() {
        getProxy().getPluginManager().registerListener(this, new ProfileListener(this));
        getProxy().getPluginManager().registerCommand(this, new ServerCommand(this));
        getProxy().getPluginManager().registerListener(this, new BungeeListener());
    }
}
