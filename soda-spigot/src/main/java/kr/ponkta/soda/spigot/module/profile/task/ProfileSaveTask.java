package kr.ponkta.soda.spigot.module.profile.task;

import kr.ponkta.soda.spigot.module.profile.ProfileHandler;
import kr.ponkta.soda.spigot.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class ProfileSaveTask extends BukkitRunnable {

    private final ProfileHandler profileHandler;

    public ProfileSaveTask(ProfileHandler profileHandler) {
        this.profileHandler = profileHandler;
    }

    @Override
    public void run() {
        // TODO: make it so it doesnt make a new thread for each profile
        this.profileHandler.getProfiles().forEach(this.profileHandler::save);
    }
}