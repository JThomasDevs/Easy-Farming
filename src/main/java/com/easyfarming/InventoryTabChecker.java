package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;

public class InventoryTabChecker {
    // Add lists for each plant state
    private static final List<Integer> inventory = Arrays.asList(3);
    private static final List<Integer> spellbook = Arrays.asList(6);


    public enum TabState {
        INVENTORY,
        SPELLBOOK,
        REST
    }

    public static TabState checkTab(Client client, int varbitIndex) {
        int varbitValue = client.getVarcIntValue(varbitIndex);
        if (inventory.contains(varbitValue)) {
            return TabState.INVENTORY;
        } else if (spellbook.contains(varbitValue)) {
            return TabState.SPELLBOOK;
        } else {
            return TabState.REST;
        }
    }
}