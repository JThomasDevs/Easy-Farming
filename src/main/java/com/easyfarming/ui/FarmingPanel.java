package com.easyfarming.ui;

import com.easyfarming.EasyFarmingConfig;
import com.easyfarming.EasyFarmingPlugin;
import com.easyfarming.core.*;
import com.easyfarming.runs.HerbRun;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

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
    
    public FarmingPanel(EasyFarmingPlugin plugin, EasyFarmingConfig config, 
                       FarmingRunState runState, HerbRun herbRun)
    {
        this.plugin = plugin;
        this.config = config;
        this.runState = runState;
        this.herbRun = herbRun;
        
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
        
        JLabel nameLabel = new JLabel(locationName);
        JLabel statusLabel = new JLabel(enabled ? "✓" : "✗");
        statusLabel.setForeground(enabled ? Color.GREEN : Color.RED);
        
        locationPanel.add(nameLabel, BorderLayout.CENTER);
        locationPanel.add(statusLabel, BorderLayout.EAST);
        
        locationsPanel.add(locationPanel);
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
