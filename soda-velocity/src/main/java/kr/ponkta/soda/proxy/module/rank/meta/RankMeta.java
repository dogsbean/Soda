package kr.ponkta.soda.proxy.module.rank.meta;

import lombok.Getter;

@Getter
public enum RankMeta {
    DEFAULT("Default Rank"),
    STAFF("Staff Rank"),
    SERVER("Bungee /server"),
    DONATOR("Donator Rank"),
    PREFIX("Additional Prefix"),
    VPN_BYPASS("VPN Bypass"),
    IP_BYPASS("IP Bypass"),
    HIDDEN("Hidden Rank");

    private String display;

    RankMeta(String display) {
        this.display = display;
    }

}
