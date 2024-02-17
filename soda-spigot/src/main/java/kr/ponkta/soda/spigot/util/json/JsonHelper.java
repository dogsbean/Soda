package kr.ponkta.soda.spigot.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mongodb.client.model.ReplaceOptions;
import kr.ponkta.soda.spigot.module.profile.Profile;
import kr.ponkta.soda.spigot.module.profile.punishment.Punishment;
import kr.ponkta.soda.spigot.util.json.serialization.ProfileSerializer;
import kr.ponkta.soda.spigot.util.json.serialization.PunishmentSerializer;

public class JsonHelper {

    public static Gson GSON = new GsonBuilder()
            // Register adapters
            .registerTypeAdapter(Profile.class, new ProfileSerializer())
            .registerTypeAdapter(Punishment.class, new PunishmentSerializer())

            // Stuffs
            .setPrettyPrinting()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()

            // Create
            .create();

    public static ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);
}
