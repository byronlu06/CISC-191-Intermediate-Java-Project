
/**
 * Lead Author(s):
 * 
 * @author Byron Lu; 5550223691
 *         <<Add additional lead authors here>>
 *
 *         References:
 *         Morelli, R., & Walde, R. (2016).
 *         Java, Java, Java: Object-Oriented Problem Solving
 *         https://open.umn.edu/opentextbooks/textbooks/java-java-java-object-oriented-problem-solving
 *
 *         <<Add more references here>>
 *
 *         Version: 2025-11-07
 */

/**
 * Purpose: The reponsibility of HeatRiskGUI is to provide a graphical user
 * interface for users to see results
 * It lets the user load a csv file, choose a year and month, and view the daily
 * results in a table
 * HeatRiskGUI is the main GUi that uses DataSet, HeatRiskAnalyzer, HeatSummary,
 * and ReportWriter to run the analysis and show the results
 * 
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HeatRiskGUI extends JFrame
{

	// Back-end objects (set after CSV load)
	private DataSet dataSet;
	private HeatRiskAnalyzer analyzer;

	// Left side controls
	private JTextField yearField = new JTextField(8);
	private JTextField monthField = new JTextField(8);
	private JTextField thresholdField = new JTextField(8);
	private JButton applyButton = new JButton("Apply / Compute");

	// Right side: table + info + buttons
	private DefaultTableModel tableModel;
	private JTable table;
	private JLabel summaryLabel = new JLabel("No data loaded yet.");
	private JButton loadCsvButton = new JButton("Load CSV");
	private JButton exportButton = new JButton("Export");

	public HeatRiskGUI()
	{
		super("Heat Risk Analyzer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(900, 600);
		setLocationRelativeTo(null);

		thresholdField.setText("95");

		setLayout(new BorderLayout());
		add(buildFiltersPanel(), BorderLayout.WEST);
		add(buildResultsPanel(), BorderLayout.CENTER);

		hookUpActions();
	}

	// Left panel: filters

	private JPanel buildFiltersPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createTitledBorder("Filters / Controls"));
		panel.setPreferredSize(new Dimension(280, 0));
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.anchor = GridBagConstraints.NORTHWEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		gbc.gridx = 0;
		gbc.gridy = 0;

		addLabeledField(panel, gbc, "Year:", yearField);
		addLabeledField(panel, gbc, "Month:", monthField);
		addLabeledField(panel, gbc, "Threshold (HI):", thresholdField);

		gbc.gridx = 0;
		gbc.gridwidth = 2;
		panel.add(applyButton, gbc);

		gbc.gridy++;
		gbc.weighty = 1;
		panel.add(Box.createVerticalGlue(), gbc);

		return panel;
	}

	private void addLabeledField(JPanel panel, GridBagConstraints gbc,
			String labelText, JComponent field)
	{
		gbc.gridx = 0;
		panel.add(new JLabel(labelText), gbc);
		gbc.gridx = 1;
		panel.add(field, gbc);
		gbc.gridy++;
	}

	// Right panel: results

	private JPanel buildResultsPanel()
	{
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(
				BorderFactory.createTitledBorder("Results Table / Summary"));

		String[] columns = { "Date", "Tmax", "RH", "HI", "Heat Day?" };
		tableModel = new DefaultTableModel(columns, 0)
		{
			@Override
			public boolean isCellEditable(int row, int column)
			{
				return false; // read-only table
			}
		};
		table = new JTable(tableModel);
		JScrollPane scrollPane = new JScrollPane(table);

		JPanel top = new JPanel(new BorderLayout());
		top.add(summaryLabel, BorderLayout.WEST);

		JPanel bottomButtons = new JPanel();
		bottomButtons.add(loadCsvButton);
		bottomButtons.add(exportButton);

		panel.add(top, BorderLayout.NORTH);
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.add(bottomButtons, BorderLayout.SOUTH);

		return panel;
	}

	// Button actions

	private void hookUpActions()
	{
		loadCsvButton.addActionListener(this::onLoadCsv);
		applyButton.addActionListener(this::onApply);
		exportButton.addActionListener(this::onExport);
	}

	private void onLoadCsv(ActionEvent e)
	{
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showOpenDialog(this);
		if (result != JFileChooser.APPROVE_OPTION)
		{
			return;
		}

		Path path = chooser.getSelectedFile().toPath();
		try
		{
			if (!Files.isRegularFile(path))
			{
				JOptionPane.showMessageDialog(this, "Not a file: " + path);
				return;
			}
			dataSet = DataSet.loadFromCsv(path);
			analyzer = new HeatRiskAnalyzer(dataSet);

			summaryLabel.setText(
					"Loaded " + dataSet.all().size() + " records (skipped: "
							+ dataSet.getSkippedRowCount() + ")");
			tableModel.setRowCount(0); // clear table
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(this,
					"Error reading CSV: " + ex.getMessage(), "Load Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void onApply(ActionEvent e)
	{
		if (dataSet == null || analyzer == null)
		{
			JOptionPane.showMessageDialog(this, "Please load a CSV first.");
	        return;
		}

		String yText = yearField.getText().trim();
	    String mText = monthField.getText().trim();
	    String tText = thresholdField.getText().trim();
	    
	    if (yText.isEmpty() || mText.isEmpty() || tText.isEmpty()) {
	        JOptionPane.showMessageDialog(this,
	                "Please fill in Year, Month, and Threshold.");
	        return;
	    }
		
	    try {
	        int year = Integer.parseInt(yText);
	        int month = Integer.parseInt(mText);
	        double threshold = Double.parseDouble(tText);

	        if (month < 1 || month > 12) {
	            JOptionPane.showMessageDialog(this,
	                    "Month must be between 1 and 12.");
	            return;
	        }

			HeatSummary summary = analyzer.summarizeFor(year, month, threshold);

			tableModel.setRowCount(0);
	        for (String[] row : summary.tableRows) {
	            tableModel.addRow(row);
	        }

	        summaryLabel.setText(String.format(
	                "Year %d, Month %d, Threshold %.1f | Days: %d, Heat days: %d, "
	                        + "Avg HI: %.1f, Max HI: %.1f, Longest streak: %d "
	                        + "| Skipped rows in file: %d",
	                year, month, threshold,
	                summary.totalDays, summary.heatDayCount,
	                summary.averageHeatIndex, summary.maxHeatIndex,
	                summary.longestStreak,
	                dataSet.getSkippedRowCount()));

		}
	    catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this, "Year, Month, and Threshold must be valid numbers.");
	    }
	}

	private void onExport(ActionEvent e)
	{
		if (dataSet == null || analyzer == null)
		{
			JOptionPane.showMessageDialog(this, "Nothing to export yet.");
			return;
		}
		
		String yText = yearField.getText().trim();
	    String mText = monthField.getText().trim();
	    String tText = thresholdField.getText().trim();
	    
	    if (yText.isEmpty() || mText.isEmpty() || tText.isEmpty()) {
	        JOptionPane.showMessageDialog(this,
	                "Please fill in Year, Month, and Threshold before exporting.");
	        return;
	    }

	    try {
	        int year = Integer.parseInt(yText);
	        int month = Integer.parseInt(mText);
	        double threshold = Double.parseDouble(tText);

	        if (month < 1 || month > 12) {
	            JOptionPane.showMessageDialog(this,
	                    "Month must be between 1 and 12.");
	            return;
	        }

	        boolean usePercentile = false;
	        HeatSummary summary =
	                analyzer.summarizeFor(year, month, threshold);

	        JFileChooser chooser = new JFileChooser();
	        chooser.setSelectedFile(new java.io.File("summary.txt"));
	        int result = chooser.showSaveDialog(this);
	        if (result != JFileChooser.APPROVE_OPTION) return;

	        Path path = chooser.getSelectedFile().toPath();
	        new ReportWriter().write(path, summary, year, month, threshold);

	        JOptionPane.showMessageDialog(this,
	                "Exported to: " + path.toAbsolutePath());

	    } catch (NumberFormatException ex) {
	        JOptionPane.showMessageDialog(this,
	                "Year, Month, and Threshold must be valid numbers.");
	    } catch (Exception ex) {
	        JOptionPane.showMessageDialog(this,
	                "Error exporting: " + ex.getMessage(),
	                "Export Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(() -> new HeatRiskGUI().setVisible(true));
	}
}
