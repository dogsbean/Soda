package kr.ponkta.soda.spigot;

import com.elevatemc.elib.eLib;
import kr.ponkta.soda.spigot.module.ModuleHandler;
import kr.ponkta.soda.spigot.module.database.mongo.MongoModule;
import kr.ponkta.soda.spigot.module.database.redis.JedisModule;
import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.module.profile.target.ProfileTarget;
import kr.ponkta.soda.spigot.module.profile.target.ProfileTypeAdapter;
import kr.ponkta.soda.spigot.module.profile.listener.ProfileListener;
import kr.ponkta.soda.spigot.module.profile.punishment.listener.PunishmentListener;
import kr.ponkta.soda.spigot.module.profile.skin.SkinHandler;
import kr.ponkta.soda.spigot.module.rank.Rank;
import kr.ponkta.soda.spigot.module.rank.RankHandler;
import kr.ponkta.soda.spigot.module.rank.adapter.RankTypeAdapter;
import kr.ponkta.soda.spigot.module.server.ServerHandler;
import kr.ponkta.soda.spigot.module.server.filter.ChatFilterHandler;
import kr.ponkta.soda.spigot.module.server.filter.listener.ChatFilterListener;
import kr.ponkta.soda.spigot.module.tag.Tag;
import kr.ponkta.soda.spigot.module.tag.TagHandler;
import kr.ponkta.soda.spigot.module.tag.adapter.TagTypeAdapter;
import kr.ponkta.soda.spigot.module.webhook.listener.StaffGriefListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public class Soda extends JavaPlugin {

    private ModuleHandler moduleHandler;
    private static Soda instance;

    @Override
    public void onEnable() {
        instance = this;

        loadHandlers();
        loadHelpers();
        loadModules();
    }

    @Override
    public void onDisable() {
        this.moduleHandler.disableModules();

        instance = null;
    }

    private void loadHandlers() {
        this.moduleHandler = new ModuleHandler();

        Arrays.asList(
                new MongoModule(),
                new JedisModule(),
                new RankHandler(),
                new ProfileHandler(),
                new ServerHandler(),
                new ChatFilterHandler(),
                new TagHandler(),
                new SkinHandler()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }
    
    private void loadHelpers() {
        saveDefaultConfig();
        this.getConfig().options().copyDefaults(true);

        eLib.getInstance().getCommandHandler().registerAll(this);

        eLib.getInstance().getCommandHandler().registerParameterType(ProfileTarget.class, new ProfileTypeAdapter());
        eLib.getInstance().getCommandHandler().registerParameterType(Rank.class, new RankTypeAdapter(this.moduleHandler.getModule(RankHandler.class)));
        eLib.getInstance().getCommandHandler().registerParameterType(Tag.class, new TagTypeAdapter(this.moduleHandler.getModule(TagHandler.class)));
    }

    private void loadModules() {
        Arrays.asList(
                /*
                Listeners, Commands, Modules, etc.
                The module handler should be able to handle them all
                */
                new ProfileListener(),
                new PunishmentListener(),
                new StaffGriefListener(),
                new ChatFilterListener()
        ).forEach(module -> this.moduleHandler.registerModule(module));
    }

    public static Soda getInstance() {
        return instance;
    }
}
