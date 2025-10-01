package com.easyfarming;

import net.runelite.client.ui.PluginPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EasyFarmingPanel extends PluginPanel
{
	private final EasyFarmingPlugin plugin;
	private final HerbRunManager herbRunManager;

    private JButton startHerbRunButton;
	private JLabel statusLabel;
	
	public EasyFarmingPanel(EasyFarmingPlugin plugin, HerbRunManager herbRunManager)
	{
		this.plugin = plugin;
		this.herbRunManager = herbRunManager;
		
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(40, 40, 40));
		setLayout(new BorderLayout());
		
		buildPanel();
	}
	
	private void buildPanel()
	{
		// Header
		JLabel titleLabel = new JLabel("Easy Farming");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
		add(titleLabel, BorderLayout.NORTH);
		
		// Content panel
        JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(new Color(40, 40, 40));
		
		// Status section
		JPanel statusPanel = createStatusPanel();
		contentPanel.add(statusPanel);
		
		// Control buttons
		JPanel buttonPanel = createButtonPanel();
		contentPanel.add(buttonPanel);
		
		add(contentPanel, BorderLayout.CENTER);
		
		// Initial update
		updatePanel();
	}
	
	private JPanel createStatusPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(50, 50, 50));
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
			new EmptyBorder(10, 10, 10, 10)
		));
		
		JLabel titleLabel = new JLabel("Status");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		panel.add(titleLabel);
		
		statusLabel = new JLabel("Ready to start herb run");
		statusLabel.setForeground(new Color(200, 200, 200));
		statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(statusLabel);
		
		return panel;
	}
	
	private JPanel createButtonPanel()
	{
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(new Color(50, 50, 50));
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
			new EmptyBorder(10, 10, 10, 10)
		));
		
		JLabel titleLabel = new JLabel("Controls");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		panel.add(titleLabel);
		
		// Button container
		JPanel buttonContainer = new JPanel();
		buttonContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonContainer.setBackground(new Color(50, 50, 50));
		
		startHerbRunButton = new JButton("Start Herb Run");
		startHerbRunButton.setBackground(new Color(0, 150, 0));
		startHerbRunButton.setForeground(Color.WHITE);
		startHerbRunButton.addActionListener(e -> {
            if (herbRunManager.isHerbRunActive())
            {
                // If run is active, stop it
                herbRunManager.stopHerbRun();
            }
            else
            {
                // If no run is active, start one
                herbRunManager.startHerbRun();
            }
            updatePanel();
        });
		
		// Add refresh button
		JButton refreshButton = new JButton("🔄 Refresh");
		refreshButton.setBackground(new Color(70, 70, 150));
		refreshButton.setForeground(Color.WHITE);
		refreshButton.addActionListener(e -> {
            // Force an inventory refresh
            plugin.forceInventoryUpdate();
        });
		
		buttonContainer.add(startHerbRunButton);
		buttonContainer.add(refreshButton);
		panel.add(buttonContainer);
		
		return panel;
	}
	

	
	public void updatePanel()
	{
		boolean isActive = herbRunManager.isHerbRunActive();
		
		// Update status
		if (isActive)
		{
			statusLabel.setText("Herb run in progress");
			statusLabel.setForeground(new Color(0, 150, 0));
			startHerbRunButton.setText("Abort Herb Run");
			startHerbRunButton.setBackground(new Color(200, 100, 0)); // Orange for abort
        }
		else
		{
			statusLabel.setText("Ready to start herb run");
			statusLabel.setForeground(new Color(200, 200, 200));
			startHerbRunButton.setText("Start Herb Run");
			startHerbRunButton.setBackground(new Color(0, 150, 0)); // Green for start
        }
        startHerbRunButton.setEnabled(true);
    }
	

	

}
