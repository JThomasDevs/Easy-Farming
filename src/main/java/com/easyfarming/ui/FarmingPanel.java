package com.easyfarming.ui;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Main farming panel that displays in the RuneLite sidebar.
 * Provides controls for starting/stopping runs, viewing progress, and configuring options.
 */
public class FarmingPanel extends PluginPanel
{
    private final EasyFarmingPlugin plugin;
    private final EasyFarmingConfig config;
    private final FarmingRunState runState;
    private final HerbRun herbRun;
    private final ConfigManager configManager;
    
    // UI Components
    private final JPanel headerPanel = new JPanel();
    private final JPanel controlPanel = new JPanel();
    private final JPanel statusPanel = new JPanel();
    private final JPanel locationsPanel = new JPanel();
    
    private final JLabel titleLabel = new JLabel("Easy Farming");
    private final JButton startStopButton = new JButton("Start Run");
    private final JButton pauseResumeButton = new JButton("Pause");
    private final JLabel statusLabel = new JLabel("Idle");
    private final JLabel progressLabel = new JLabel("0/0 locations");
    
    private final JCheckBox herbRunCheckbox = new JCheckBox("Herb Run", true);
    private final JCheckBox treeRunCheckbox = new JCheckBox("Tree Run", false);
    private final JCheckBox fruitTreeCheckbox = new JCheckBox("Fruit Tree Run", false);
    private final JCheckBox allotmentCheckbox = new JCheckBox("Allotment Run", false);
    
    // Location status tracking
    private final Map<String, JLabel> locationStatusLabels = new HashMap<>();
    private final Map<String, JComboBox<String>> locationTeleportDropdowns = new HashMap<>();
    
    public FarmingPanel(EasyFarmingPlugin plugin, EasyFarmingConfig config, 
                       FarmingRunState runState, HerbRun herbRun, ConfigManager configManager)
    {
        this.plugin = plugin;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        this.configManager = configManager;
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        init();
    }
    
    private void init()
    {
        // Create header
        createHeader();
        
        // Create control panel
        createControlPanel();
        
        // Create status panel
        createStatusPanel();
        
        // Create locations panel
        createLocationsPanel();
        
        // Add all panels to main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(controlPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(locationsPanel);
        
        add(mainPanel, BorderLayout.NORTH);
    }
    
    private void createHeader()
    {
        headerPanel.setLayout(new BorderLayout());
        
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(titleLabel, BorderLayout.CENTER);
    }
    
    private void createControlPanel()
    {
        controlPanel.setLayout(new GridLayout(4, 1, 5, 5));
        controlPanel.setBorder(BorderFactory.createTitledBorder("Run Types"));
        
        // Add checkboxes for run types
        controlPanel.add(herbRunCheckbox);
        controlPanel.add(treeRunCheckbox);
        controlPanel.add(fruitTreeCheckbox);
        controlPanel.add(allotmentCheckbox);
        
        // Add listeners
        herbRunCheckbox.addItemListener(e -> updateRunTypes());
        treeRunCheckbox.addItemListener(e -> updateRunTypes());
        fruitTreeCheckbox.addItemListener(e -> updateRunTypes());
        allotmentCheckbox.addItemListener(e -> updateRunTypes());
    }
    
    private void createStatusPanel()
    {
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
        
        // Status label
        JPanel statusLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusLabelPanel.add(new JLabel("State: "));
        statusLabelPanel.add(statusLabel);
        statusPanel.add(statusLabelPanel);
        
        // Progress label
        JPanel progressLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressLabelPanel.add(new JLabel("Progress: "));
        progressLabelPanel.add(progressLabel);
        statusPanel.add(progressLabelPanel);
        
        // Control buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        buttonPanel.add(startStopButton);
        buttonPanel.add(pauseResumeButton);
        statusPanel.add(buttonPanel);
        
        // Add button listeners
        startStopButton.addActionListener(e -> toggleRun());
        pauseResumeButton.addActionListener(e -> togglePause());
        
        // Initial button state
        pauseResumeButton.setEnabled(false);
    }
    
    private void createLocationsPanel()
    {
        locationsPanel.setLayout(new BoxLayout(locationsPanel, BoxLayout.Y_AXIS));
        locationsPanel.setBorder(BorderFactory.createTitledBorder("Herb Locations"));
        
        // Add location entries
        addLocationEntry("Ardougne", config.ardougneHerb());
        addLocationEntry("Catherby", config.catherbyHerb());
        addLocationEntry("Falador", config.faladorHerb());
        addLocationEntry("Morytania", config.morytaniaHerb());
        addLocationEntry("Troll Stronghold", config.trollStrongholdHerb());
        addLocationEntry("Kourend", config.kourendHerb());
        addLocationEntry("Farming Guild", config.farmingGuildHerb());
        addLocationEntry("Harmony Island", config.harmonyHerb());
        addLocationEntry("Weiss", config.weissHerb());
    }
    
    private void addLocationEntry(String locationName, boolean enabled)
    {
        JPanel locationPanel = new JPanel(new BorderLayout());
        locationPanel.setBorder(new EmptyBorder(2, 5, 2, 5));
        
        // Create checkbox for location toggle
        JCheckBox locationCheckbox = new JCheckBox(locationName, enabled);
        locationCheckbox.addItemListener(e -> updateLocationSetting(locationName, e.getStateChange() == ItemEvent.SELECTED));
        
        // Create teleport dropdown
        JComboBox<String> teleportDropdown = createTeleportDropdown(locationName);
        
        // Create status label
        JLabel statusLabel = new JLabel("●");
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setToolTipText("Location status: Not started");
        
        // Store references for updates
        locationTeleportDropdowns.put(locationName, teleportDropdown);
        locationStatusLabels.put(locationName, statusLabel);
        
        // Add components to panel
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.add(locationCheckbox);
        leftPanel.add(Box.createHorizontalStrut(5));
        leftPanel.add(statusLabel);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightPanel.add(teleportDropdown);
        
        locationPanel.add(leftPanel, BorderLayout.WEST);
        locationPanel.add(rightPanel, BorderLayout.EAST);
        
        locationsPanel.add(locationPanel);
    }
    
    private JComboBox<String> createTeleportDropdown(String locationName)
    {
        JComboBox<String> dropdown = new JComboBox<>();
        dropdown.setPreferredSize(new Dimension(120, 25));
        
        // Add teleport options based on location
        switch (locationName.toLowerCase())
        {
            case "ardougne":
                dropdown.addItem("Ardougne Cloak");
                dropdown.addItem("Ardougne Teleport");
                dropdown.addItem("Ardougne Tele Tab");
                dropdown.addItem("Skills Necklace");
                dropdown.addItem("Combat Bracelet");
                dropdown.addItem("Quest Cape");
                dropdown.addItem("Fairy Ring BLR");
                break;
            case "catherby":
                dropdown.addItem("Catherby Tele Tab");
                dropdown.addItem("Camelot Teleport");
                dropdown.addItem("Camelot Tele Tab");
                break;
            case "falador":
                dropdown.addItem("Explorer's Ring");
                dropdown.addItem("Falador Teleport");
                dropdown.addItem("Falador Tele Tab");
                dropdown.addItem("Ring of Elements");
                dropdown.addItem("Spirit Tree");
                dropdown.addItem("Draynor Manor");
                dropdown.addItem("Amulet of Glory");
                dropdown.addItem("Skills Necklace");
                dropdown.addItem("Ring of Wealth");
                break;
            case "morytania":
                dropdown.addItem("Ectophial");
                dropdown.addItem("Burgh de Rott");
                break;
            case "troll stronghold":
                dropdown.addItem("Trollheim Teleport");
                dropdown.addItem("Stony Basalt");
                break;
            case "kourend":
                dropdown.addItem("Xeric's Talisman");
                dropdown.addItem("Mounted Xeric's");
                break;
            case "farming guild":
                dropdown.addItem("Skills Necklace");
                dropdown.addItem("Farming Cape");
                break;
            case "harmony island":
                dropdown.addItem("Harmony Tele Tab");
                break;
            case "weiss":
                dropdown.addItem("Icy Basalt");
                break;
        }
        
        // Add listener to update config
        dropdown.addActionListener(e -> updateTeleportSetting(locationName, (String) dropdown.getSelectedItem()));
        
        return dropdown;
    }
    
    private void updateLocationSetting(String locationName, boolean enabled)
    {
        // Update config based on location name
        String configKey = null;
        switch (locationName.toLowerCase())
        {
            case "ardougne":
                configKey = "ardougneHerb";
                break;
            case "catherby":
                configKey = "catherbyHerb";
                break;
            case "falador":
                configKey = "faladorHerb";
                break;
            case "morytania":
                configKey = "morytaniaHerb";
                break;
            case "troll stronghold":
                configKey = "trollStrongholdHerb";
                break;
            case "kourend":
                configKey = "kourendHerb";
                break;
            case "farming guild":
                configKey = "farmingGuildHerb";
                break;
            case "harmony island":
                configKey = "harmonyHerb";
                break;
            case "weiss":
                configKey = "weissHerb";
                break;
        }
        
        if (configKey != null)
        {
            configManager.setConfiguration("easyfarming", configKey, enabled);
        }
    }
    
    private void updateTeleportSetting(String locationName, String teleportMethod)
    {
        // Update config based on location name and teleport method
        String configKey = null;
        String configValue = null;
        
        switch (locationName.toLowerCase())
        {
            case "ardougne":
                configKey = "ardougneTeleport";
                switch (teleportMethod)
                {
                    case "Ardougne Cloak": configValue = "ARDOUGNE_CLOAK"; break;
                    case "Ardougne Teleport": configValue = "ARDOUGNE_TELEPORT"; break;
                    case "Ardougne Tele Tab": configValue = "ARDOUGNE_TELE_TAB"; break;
                    case "Skills Necklace": configValue = "JEWL_NECKLACE_OF_SKILLS_1_FISHING"; break;
                    case "Combat Bracelet": configValue = "COMBAT_BRACELET_RANGING"; break;
                    case "Quest Cape": configValue = "QUEST_POINT_CAPE"; break;
                    case "Fairy Ring BLR": configValue = "FAIRY_RING_BLR"; break;
                }
                break;
            case "catherby":
                configKey = "catherbyTeleport";
                switch (teleportMethod)
                {
                    case "Catherby Tele Tab": configValue = "CATHERBY_TELE_TAB"; break;
                    case "Camelot Teleport": configValue = "CAMELOT_TELEPORT"; break;
                    case "Camelot Tele Tab": configValue = "CAMELOT_TELE_TAB"; break;
                }
                break;
            case "falador":
                configKey = "faladorTeleport";
                switch (teleportMethod)
                {
                    case "Explorer's Ring": configValue = "EXPLORERS_RING"; break;
                    case "Falador Teleport": configValue = "FALADOR_TELEPORT"; break;
                    case "Falador Tele Tab": configValue = "FALADOR_TELE_TAB"; break;
                    case "Ring of Elements": configValue = "RING_OF_ELEMENTS_AIR"; break;
                    case "Spirit Tree": configValue = "SPIRIT_TREE_PORT_SARIM"; break;
                    case "Draynor Manor": configValue = "DRAYNOR_MANOR_TELEPORT"; break;
                    case "Amulet of Glory": configValue = "AMULET_OF_GLORY_DRAYNOR"; break;
                    case "Skills Necklace": configValue = "JEWL_NECKLACE_OF_SKILLS_1_MINING"; break;
                    case "Ring of Wealth": configValue = "RING_OF_WEALTH_FALADOR"; break;
                }
                break;
            case "morytania":
                configKey = "morytaniaTeleport";
                switch (teleportMethod)
                {
                    case "Ectophial": configValue = "ECTOPHIAL"; break;
                    case "Burgh de Rott": configValue = "BURGH_DE_ROTT_TELEPORT"; break;
                }
                break;
            case "troll stronghold":
                configKey = "trollStrongholdTeleport";
                switch (teleportMethod)
                {
                    case "Trollheim Teleport": configValue = "TROLLHEIM_TELEPORT"; break;
                    case "Stony Basalt": configValue = "STRONGHOLD_TELEPORT_BASALT"; break;
                }
                break;
            case "kourend":
                configKey = "kourendTeleport";
                switch (teleportMethod)
                {
                    case "Xeric's Talisman": configValue = "XERICS_TALISMAN"; break;
                    case "Mounted Xeric's": configValue = "MOUNTED_XERICS"; break;
                }
                break;
            case "farming guild":
                configKey = "farmingGuildTeleport";
                switch (teleportMethod)
                {
                    case "Skills Necklace": configValue = "JEWL_NECKLACE_OF_SKILLS_1"; break;
                    case "Farming Cape": configValue = "SKILLCAPE_FARMING"; break;
                }
                break;
            case "harmony island":
                configKey = "harmonyTeleport";
                switch (teleportMethod)
                {
                    case "Harmony Tele Tab": configValue = "HARMONY_TELE_TAB"; break;
                }
                break;
            case "weiss":
                configKey = "weissTeleport";
                switch (teleportMethod)
                {
                    case "Icy Basalt": configValue = "WEISS_TELEPORT_BASALT"; break;
                }
                break;
        }
        
        if (configKey != null && configValue != null)
        {
            configManager.setConfiguration("easyfarming", configKey, configValue);
        }
    }
    
    /**
     * Update the status display for a specific location
     */
    public void updateLocationStatus(String locationName, LocationStatus status)
    {
        JLabel statusLabel = locationStatusLabels.get(locationName);
        if (statusLabel != null)
        {
            switch (status)
            {
                case NOT_STARTED:
                    statusLabel.setText("●");
                    statusLabel.setForeground(Color.GRAY);
                    statusLabel.setToolTipText("Location status: Not started");
                    break;
                case IN_PROGRESS:
                    statusLabel.setText("●");
                    statusLabel.setForeground(Color.YELLOW);
                    statusLabel.setToolTipText("Location status: In progress");
                    break;
                case COMPLETED:
                    statusLabel.setText("●");
                    statusLabel.setForeground(Color.GREEN);
                    statusLabel.setToolTipText("Location status: Completed");
                    break;
                case SKIPPED:
                    statusLabel.setText("●");
                    statusLabel.setForeground(Color.ORANGE);
                    statusLabel.setToolTipText("Location status: Skipped");
                    break;
                case ERROR:
                    statusLabel.setText("●");
                    statusLabel.setForeground(Color.RED);
                    statusLabel.setToolTipText("Location status: Error");
                    break;
            }
        }
    }
    
    /**
     * Update the overall status display
     */
    public void updateOverallStatus()
    {
        if (runState.isRunActive())
        {
            FarmingState currentState = runState.getCurrentState();
            statusLabel.setText(currentState.getDisplayName());
            
            // Update progress
            int totalLocations = getEnabledLocationCount();
            int completedLocations = getCompletedLocationCount();
            progressLabel.setText(completedLocations + "/" + totalLocations + " locations");
            
            // Update button states
            startStopButton.setText("Stop Run");
            pauseResumeButton.setEnabled(true);
            pauseResumeButton.setText(runState.getCurrentState() == FarmingState.PAUSED ? "Resume" : "Pause");
        }
        else
        {
            statusLabel.setText("Idle");
            progressLabel.setText("0/0 locations");
            startStopButton.setText("Start Run");
            pauseResumeButton.setEnabled(false);
            pauseResumeButton.setText("Pause");
        }
    }
    
    /**
     * Get count of enabled locations
     */
    private int getEnabledLocationCount()
    {
        int count = 0;
        if (config.ardougneHerb()) count++;
        if (config.catherbyHerb()) count++;
        if (config.faladorHerb()) count++;
        if (config.morytaniaHerb()) count++;
        if (config.trollStrongholdHerb()) count++;
        if (config.kourendHerb()) count++;
        if (config.farmingGuildHerb()) count++;
        if (config.harmonyHerb()) count++;
        if (config.weissHerb()) count++;
        return count;
    }
    
    /**
     * Get count of completed locations (placeholder - would need integration with run state)
     */
    private int getCompletedLocationCount()
    {
        // This would need to be integrated with the actual farming run state
        // For now, return 0 as a placeholder
        return 0;
    }
    
    /**
     * Enum for location status
     */
    public enum LocationStatus
    {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        SKIPPED,
        ERROR
    }
    
    private void toggleRun()
    {
        if (runState.isRunActive())
        {
            // Stop the run
            runState.stopRun();
            startStopButton.setText("Start Run");
            pauseResumeButton.setEnabled(false);
            updateStatus();
        }
        else
        {
            // Start the run
            runState.startRun(
                herbRunCheckbox.isSelected(),
                treeRunCheckbox.isSelected(),
                fruitTreeCheckbox.isSelected(),
                allotmentCheckbox.isSelected()
            );
            startStopButton.setText("Stop Run");
            pauseResumeButton.setEnabled(true);
            pauseResumeButton.setText("Pause");
            updateStatus();
        }
    }
    
    private void togglePause()
    {
        if (runState.getCurrentState() == FarmingState.PAUSED)
        {
            runState.resumeRun();
            pauseResumeButton.setText("Pause");
        }
        else
        {
            runState.pauseRun();
            pauseResumeButton.setText("Resume");
        }
        updateStatus();
    }
    
    private void updateRunTypes()
    {
        // If a run is active, restart it with new settings
        if (runState.isRunActive())
        {
            runState.stopRun();
            runState.startRun(
                herbRunCheckbox.isSelected(),
                treeRunCheckbox.isSelected(),
                fruitTreeCheckbox.isSelected(),
                allotmentCheckbox.isSelected()
            );
            updateStatus();
        }
    }
    
    /**
     * Update the status display
     */
    public void updateStatus()
    {
        SwingUtilities.invokeLater(() -> {
            // Update state label
            FarmingState currentState = runState.getCurrentState();
            statusLabel.setText(currentState.getDisplayName());
            statusLabel.setForeground(getStateColor(currentState));
            
            // Update progress label
            int total = runState.getEnabledLocations().size();
            int completed = runState.getCompletedLocations().size();
            progressLabel.setText(completed + "/" + total + " locations");
            
            // Update button states
            if (runState.isRunActive())
            {
                startStopButton.setText("Stop Run");
                pauseResumeButton.setEnabled(true);
                
                if (currentState == FarmingState.PAUSED)
                {
                    pauseResumeButton.setText("Resume");
                }
                else
                {
                    pauseResumeButton.setText("Pause");
                }
            }
            else
            {
                startStopButton.setText("Start Run");
                pauseResumeButton.setEnabled(false);
            }
        });
    }
    
    /**
     * Get color for a farming state
     */
    private Color getStateColor(FarmingState state)
    {
        switch (state)
        {
            case IDLE:
                return Color.GRAY;
            case GATHERING_ITEMS:
                return Color.ORANGE;
            case READY_TO_TELEPORT:
                return Color.GREEN;
            case TELEPORTING:
                return Color.CYAN;
            case NAVIGATING:
                return Color.YELLOW;
            case AT_PATCH:
                return Color.WHITE;
            case HARVESTING:
            case PLANTING:
            case TREATING_DISEASE:
            case REMOVING_DEAD:
            case COMPOSTING:
            case WATERING:
                return Color.MAGENTA;
            case MOVING_TO_NEXT:
                return Color.BLUE;
            case RUN_COMPLETE:
                return Color.GREEN;
            case PAUSED:
                return Color.ORANGE;
            case ERROR:
                return Color.RED;
            default:
                return Color.GRAY;
        }
    }
    
    /**
     * Show an error message in the panel
     */
    public void showError(String message)
    {
        removeAll();
        
        PluginErrorPanel errorPanel = new PluginErrorPanel();
        errorPanel.setContent("Easy Farming Error", message);
        
        add(errorPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
    
    /**
     * Clear any error and restore normal panel
     */
    public void clearError()
    {
        removeAll();
        init();
        revalidate();
        repaint();
    }
}
