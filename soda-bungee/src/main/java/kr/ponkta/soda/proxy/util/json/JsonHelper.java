package kr.ponkta.soda.proxy.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import com.mongodb.client.model.ReplaceOptions;
import kr.ponkta.soda.proxy.module.profile.Profile;
import kr.ponkta.soda.proxy.util.json.serialization.ProfileSerializer;

public class JsonHelper {

    public static Gson GSON = new GsonBuilder()
            // Register adapters
            .registerTypeAdapter(Profile.class, new ProfileSerializer())

            // Stuffs
            .setPrettyPrinting()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .serializeNulls()
            .serializeSpecialFloatingPointValues()

            // Create
            .create();

    public static ReplaceOptions REPLACE_OPTIONS = new ReplaceOptions().upsert(true);
}
