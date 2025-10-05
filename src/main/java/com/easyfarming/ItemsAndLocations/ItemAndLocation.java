package com.easyfarming.ItemsAndLocations;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.ItemRequirement;
import com.easyfarming.Location;
import net.runelite.api.Client;
import net.runelite.api.ItemID;

import java.util.ArrayList;
import java.util.List;

public class ItemAndLocation
{
    protected EasyFarmingConfig config;

    protected Client client;

    protected EasyFarmingPlugin plugin;

    public List<Location> locations = new ArrayList<>();

    public ItemAndLocation()
    {
    }

    public ItemAndLocation(EasyFarmingConfig config, Client client, EasyFarmingPlugin plugin)
    {
        this.config = config;
        this.client = client;
        this.plugin = plugin;
    }

    public List<ItemRequirement> getHouseTeleportItemRequirements()
    {
        EasyFarmingConfig.OptionEnumHouseTele selectedOption = config.enumConfigHouseTele();

        List<ItemRequirement> itemRequirements = new ArrayList<>();

        switch (selectedOption) {
            case Law_air_earth_runes:
                itemRequirements.add(new ItemRequirement(
                    ItemID.AIR_RUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.EARTH_RUNE,
                    1
                ));

                itemRequirements.add(new ItemRequirement(
                    ItemID.LAW_RUNE,
                    1
                ));

                break;

            // case Law_dust_runes:
            //     itemRequirements.add(new ItemRequirement(
            //         ItemID.DUST_RUNE,
            //         1
            //     ));
            //
            //     itemRequirements.add(new ItemRequirement(
            //         ItemID.LAW_RUNE,
            //         1
            //     ));
            //
            //     break;

            case Teleport_To_House:
                itemRequirements.add(new ItemRequirement(
                    ItemID.TELEPORT_TO_HOUSE,
                    1
                ));

                break;

            case Construction_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.CONSTRUCT_CAPE,
                    1
                ));

                break;

            case Construction_cape_t:
                itemRequirements.add(new ItemRequirement(
                    ItemID.CONSTRUCT_CAPET,
                    1
                ));

                break;

            case Max_cape:
                itemRequirements.add(new ItemRequirement(
                    ItemID.MAX_CAPE,
                    1
                ));

                break;

            default:
                throw new IllegalStateException("Unexpected value: " + selectedOption);
        }

        return itemRequirements;
    }

    public Integer selectedCompostID()
    {
        EasyFarmingConfig.OptionEnumCompost selectedCompost = config.enumConfigCompost();

        switch (selectedCompost) {
            case Compost:
                return ItemID.COMPOST;

            case Supercompost:
                return ItemID.SUPERCOMPOST;

            case Ultracompost:
                return ItemID.ULTRACOMPOST;

            case Bottomless:
                return ItemID.BOTTOMLESS_COMPOST_BUCKET_22997;
        }

        return - 1;
    }

    public void setupLocations()
    {
        locations.clear();
    }
}
