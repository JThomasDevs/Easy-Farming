package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;

public class InventoryTabChecker {
    // Lists for each tab state
    private static final List<Integer> INVENTORY = Arrays.asList(3);
    private static final List<Integer> SPELLBOOK = Arrays.asList(6);

    public enum TabState {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static TabState checkTab(Client client, int varbitIndex) {
        int varbitValue = client.getVarcIntValue(varbitIndex);
        if (INVENTORY.contains(varbitValue)) {
            return TabState.INVENTORY;
        } else if (SPELLBOOK.contains(varbitValue)) {
            return TabState.SPELLBOOK;
        } else {
            return TabState.REST;
        }
    }}