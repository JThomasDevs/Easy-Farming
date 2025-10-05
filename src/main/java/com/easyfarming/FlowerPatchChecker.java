package com.easyfarming;

import net.runelite.api.Client;

import java.util.Arrays;
import java.util.List;

public class FlowerPatchChecker {
    // Add lists for each plant state
    private static final List<Integer> harvestable = Arrays.asList(32);
    private static final List<Integer> growing = Arrays.asList(28, 29, 30, 31);
    private static final List<Integer> dead = Arrays.asList(221,222,223,224);
    private static final List<Integer> weeds = Arrays.asList(0, 1, 2);


    public enum PlantState {
        HARVESTABLE,
        GROWING,
        DEAD,
        WEEDS,
        PLANT,
        UNKNOWN
    }

    public static PlantState checkFlowerPatch(Client client, int varbitIndex) {
        int varbitValue = client.getVarbitValue(varbitIndex);

        if (harvestable.contains(varbitValue)) {
            return PlantState.HARVESTABLE;
        } else if (growing.contains(varbitValue)) {
            return PlantState.GROWING;
        } else if (dead.contains(varbitValue)) {
            return PlantState.DEAD;
        }  else if (weeds.contains(varbitValue)) {
            return PlantState.WEEDS;
        } else if (varbitValue == 3) {
            return PlantState.PLANT;
        } else {
                return PlantState.UNKNOWN;
        }
    }
}