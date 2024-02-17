package kr.ponkta.soda.spigot.module.profile.punishment.evidence;

import kr.ponkta.soda.spigot.util.time.DurationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data @AllArgsConstructor
public class PunishmentEvidence {
    private final String link;
    private final UUID addedBy;
    private final long addedAt;
}
