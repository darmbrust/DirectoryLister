package directoryLister;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * <pre>
 *  Copyright (c) 2010  Daniel Armbrust.  All Rights Reserved.
 *  
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  The license was included with the download.
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * </pre>
 * 
 * @author <A HREF="mailto:daniel.armbrust@gmail.com">Daniel Armbrust</A>
 */
public class DirectoryLister extends JFrame
{
	DirectoryLister			dl;
	JComboBox				file;
	JButton					process;
	JCheckBox				recurseDirectories;
	JCheckBox				listDirectories;
	JCheckBox				listFiles;
	JCheckBox				indent;
	JCheckBox				treeDirectory;
	JCheckBox				removeExtensions;
	JCheckBox				removeDriveLetters;
	JCheckBox				fullNames;
	JCheckBox				writeToFile;
	JCheckBox				writeToScreen;
	JButton					redisplay;
	JTextField				textToAppend;
	JTextField				textToPrefix;
	JCheckBox				appendToFiles;
	JCheckBox				prefixToFiles;
	JCheckBox				appendToDirectories;
	JCheckBox				prefixToDirectories;
	JTextArea				output;
	BufferedWriter			outputFile;
	JScrollPane				outputScroll;
	private File			baseDirectory;
	private ArrayList		currentListing;
	private JFileChooser	directoryChooser;
	private JFileChooser	fileChooser;
	private ScreenDisplayer	screenDisplayer;
	public boolean			continueProcessing	= true;
	public JProgressBar		progressBar;

	public DirectoryLister()
	{
		super();
		dl = this;
		dl.setSize(new Dimension(640, 480));
		directoryChooser = new JFileChooser();
		directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser = new JFileChooser();
		//Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = dl.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		dl.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		dl.setIconImage(new ImageIcon(this.getClass().getResource("images/icon.gif")).getImage());
		dl.setTitle("Directory and File Lister");
		dl.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
		JLabel processLabel = new JLabel("Directory to Process:");
		file = new JComboBox();
		file.setEditable(true);
		file.addItem("");
		file.addItem("Browse for a file...");
		file.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				DefaultComboBoxModel model = (DefaultComboBoxModel) file.getModel();
				if (model.getSize() - 1 == file.getSelectedIndex())
				{
					int returnVal = directoryChooser.showOpenDialog(dl);
					if (returnVal != 0)
					{
						return;
					}
					if (((String) model.getElementAt(0)).equals(""))
					{
						model.removeElementAt(0);
					}
					model.insertElementAt(directoryChooser.getSelectedFile().getAbsolutePath(), 0);
					file.setSelectedIndex(0);
				}
			}
		});
		Dimension buttonSize = new Dimension(90, 21);
		process = new JButton("Process");
		process.setPreferredSize(buttonSize);
		process.setMinimumSize(buttonSize);
		process.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (process.getText().equals("Process"))
				{
					baseDirectory = new File((String) file.getSelectedItem());
					if (baseDirectory.exists())
					{
						continueProcessing = true;
						outputFile = null;
						if (dl.writeToFile.isSelected())
						{
							int returnVal = fileChooser.showSaveDialog(dl);
							if (returnVal != 0)
								return;
							try
							{
								outputFile = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
							}
							catch (IOException error)
							{
								JOptionPane.showMessageDialog(dl, "There was an error opening the output file.",
																"File writing error", JOptionPane.ERROR_MESSAGE);
								return;
							}
						}
						FileProcessor fileProcessor = new FileProcessor(baseDirectory, dl, dl.recurseDirectories
								.isSelected());
						process.setText("Cancel");
						new Thread(fileProcessor).start();
					}
					else
					{
						JOptionPane.showMessageDialog(dl, "Cannot open the file " + file.getSelectedItem() + ".",
														"File opening error", JOptionPane.ERROR_MESSAGE);
					}
				}
				else
				{
					continueProcessing = false;
					process.setText("Process");
				}
			}
		});
		progressBar = new JProgressBar();
		progressBar.setMinimumSize(new Dimension(40, 21));
		progressBar.setStringPainted(true);
		progressBar.setString("Choose a directory to process");
		progressBar.setForeground(Color.BLUE);
		final JButton clear = new JButton("Clear");
		clear.setMinimumSize(buttonSize);
		clear.setPreferredSize(buttonSize);
		clear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dl.output.setText("");
			}
		});
		listDirectories = new JCheckBox("List Directories", true);
		removeDriveLetters = new JCheckBox("Remove Drive Letters", true);
		removeDriveLetters.setEnabled(false);
		listFiles = new JCheckBox("List Files", true);
		listFiles.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeExtensions.setEnabled(listFiles.isSelected());
			}
		});
		indent = new JCheckBox("Indent Output", false);
		indent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (indent.isSelected())
					treeDirectory.setSelected(false);
			}
		});
		treeDirectory = new JCheckBox("Tree Directory");
		treeDirectory.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (treeDirectory.isSelected())
					indent.setSelected(false);
			}
		});
		recurseDirectories = new JCheckBox("Recurse Directories", true);
		removeExtensions = new JCheckBox("Remove Extensions", true);
		fullNames = new JCheckBox("Long Names", false);
		fullNames.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				removeDriveLetters.setEnabled(fullNames.isSelected());
			}
		});
		writeToScreen = new JCheckBox("Write to Screen", true);
		writeToFile = new JCheckBox("Write to File");
		redisplay = new JButton("Redisplay");
		redisplay.setPreferredSize(buttonSize);
		redisplay.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dl.output.setText("");
				dl.showFiles(null);
			}
		});
		JLabel textToAppendLabel = new JLabel("Append");
		JLabel textToPrefixLabel = new JLabel("Prefix");
		Dimension textFieldSize = new Dimension(100, 21);
		textToAppend = new JTextField();
		textToAppend.setMinimumSize(textFieldSize);
		textToAppend.setPreferredSize(textFieldSize);
		textToPrefix = new JTextField();
		textToPrefix.setMinimumSize(textFieldSize);
		textToPrefix.setPreferredSize(textFieldSize);
		appendToFiles = new JCheckBox("to files");
		prefixToFiles = new JCheckBox("to files");
		appendToDirectories = new JCheckBox("to directories");
		prefixToDirectories = new JCheckBox("to directories");
		output = new JTextArea();
		outputScroll = new JScrollPane(output);
		JPanel bottomPanel = new JPanel(new GridBagLayout());
		JPanel rightPanel = new JPanel(new GridBagLayout());
		JPanel mainPanel = new JPanel(new GridBagLayout());
		dl.setContentPane(mainPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		rightPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		bottomPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		rightPanel.setMinimumSize(new Dimension(170, 300));
		rightPanel.setPreferredSize(new Dimension(170, 300));
		gbc.anchor = gbc.WEST;
		gbc.insets = new Insets(5, 5, 0, 5);
		add(mainPanel, processLabel, gbc, 0, 0, 1, 1);
		gbc.weightx = 100;
		gbc.fill = gbc.HORIZONTAL;
		add(mainPanel, file, gbc, 1, 0, 2, 1);
		gbc.weightx = 0;
		add(mainPanel, writeToScreen, gbc, 4, 0, 1, 1);
		gbc.fill = gbc.NONE;
		add(mainPanel, recurseDirectories, gbc, 0, 1, 1, 1);
		add(mainPanel, process, gbc, 1, 1, 1, 1);
		gbc.weightx = 100;
		gbc.fill = gbc.HORIZONTAL;
		add(mainPanel, progressBar, gbc, 2, 1, 1, 1);
		gbc.weightx = 0;
		add(mainPanel, writeToFile, gbc, 4, 1, 1, 1);
		gbc.weightx = 100;
		gbc.weighty = 100;
		gbc.fill = gbc.BOTH;
		gbc.insets = new Insets(5, 5, 0, 5);
		add(mainPanel, outputScroll, gbc, 0, 2, 4, 1);
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = gbc.NONE;
		gbc.anchor = gbc.NORTHWEST;
		gbc.insets = new Insets(0, 5, 0, 0);
		add(rightPanel, listFiles, gbc, 0, 0, 1, 1);
		gbc.insets = new Insets(0, 15, 0, 0);
		add(rightPanel, removeExtensions, gbc, 0, 1, 1, 1);
		gbc.insets = new Insets(0, 5, 0, 0);
		add(rightPanel, listDirectories, gbc, 0, 2, 1, 1);
		add(rightPanel, fullNames, gbc, 0, 3, 1, 1);
		gbc.insets = new Insets(0, 15, 0, 0);
		add(rightPanel, removeDriveLetters, gbc, 0, 4, 1, 1);
		gbc.insets = new Insets(0, 5, 0, 0);
		add(rightPanel, indent, gbc, 0, 5, 1, 1);
		gbc.insets = new Insets(0, 5, 0, 0);
		gbc.weighty = 100;
		add(rightPanel, treeDirectory, gbc, 0, 6, 1, 1);
		gbc.weighty = 0;
		gbc.insets = new Insets(0, 5, 5, 0);
		add(rightPanel, clear, gbc, 0, 7, 1, 1);
		add(rightPanel, redisplay, gbc, 0, 8, 1, 1);
		gbc.insets = new Insets(5, 0, 0, 5);
		gbc.weightx = 0;
		gbc.weighty = 100;
		gbc.fill = gbc.VERTICAL;
		add(mainPanel, rightPanel, gbc, 4, 2, 1, 1);
		gbc.anchor = gbc.WEST;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = gbc.NONE;
		gbc.insets = new Insets(0, 5, 0, 0);
		add(bottomPanel, textToPrefixLabel, gbc, 0, 0, 1, 1);
		add(bottomPanel, textToAppendLabel, gbc, 0, 1, 1, 1);
		add(bottomPanel, textToPrefix, gbc, 1, 0, 1, 1);
		add(bottomPanel, textToAppend, gbc, 1, 1, 1, 1);
		add(bottomPanel, prefixToDirectories, gbc, 2, 0, 1, 1);
		add(bottomPanel, appendToDirectories, gbc, 2, 1, 1, 1);
		add(bottomPanel, prefixToFiles, gbc, 3, 0, 1, 1);
		gbc.weightx = 100;
		add(bottomPanel, appendToFiles, gbc, 3, 1, 1, 1);
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = gbc.HORIZONTAL;
		add(mainPanel, bottomPanel, gbc, 0, 3, 6, 1);
		MenuBar menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");
		MenuItem fileMenuSave = new MenuItem("Save...");
		fileMenuSave.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int returnVal = fileChooser.showSaveDialog(dl);
				if (returnVal != 0)
				{
					return;
				}
				Component glassPane = null;
				try
				{
					glassPane = dl.getGlassPane();
					glassPane.setVisible(true);
					glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					outputFile = new BufferedWriter(new FileWriter(fileChooser.getSelectedFile()));
					outputFile.write(output.getText());
					outputFile.close();
					outputFile = null;
					glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					glassPane.setVisible(false);
				}
				catch (IOException error)
				{
					glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					glassPane.setVisible(false);
					JOptionPane.showMessageDialog(dl,
													"There was an error saving your file./n  The file was not saved.",
													"File writing error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		MenuItem fileMenuExit = new MenuItem("Exit");
		fileMenuExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		Menu editMenu = new Menu("Edit");
		MenuItem editMenuSortAsc = new MenuItem("Sort A->Z");
		editMenuSortAsc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dl.sortScreen(true);
			}
		});
		MenuItem editMenuSortDesc = new MenuItem("Sort Z->A");
		editMenuSortDesc.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dl.sortScreen(false);
			}
		});
		MenuItem findReplace = new MenuItem("Find/Replace");
		findReplace.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				dl.output.requestFocus();
				new FindReplace(dl, true, dl.output);
			}
		});
		Menu helpMenu = new Menu("Help");
		MenuItem helpMenuAbout = new MenuItem("About...");
		helpMenuAbout.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(dl, "Directory Lister Version 1.1\n"
						+ "Copyright 2004, Daniel Armbrust All Rights Reserved\n"
						+ "This code is licensed under the Apache License - v 2.0. \n"
						+ "A full copy of the license has been included with the distribution.\n"
						+ "E-Mail me at daniel.armbrust@gmail.com.\n"
						+ "or visit http://armbrust.webhop.net/", "Directory Lister 1.1", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		fileMenu.add(fileMenuSave);
		fileMenu.add(fileMenuExit);
		editMenu.add(editMenuSortAsc);
		editMenu.add(editMenuSortDesc);
		editMenu.add(findReplace);
		helpMenu.add(helpMenuAbout);
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(helpMenu);
		dl.setMenuBar(menuBar);
		screenDisplayer = new ScreenDisplayer(this);
		dl.setVisible(true);
	}

	public void showFiles(ArrayList newListing)
	{
		if (newListing != null)
		{
			currentListing = newListing;
		}
		new Thread(screenDisplayer).start();
	}

	private void sortScreen(boolean asc)
	{
		String screen = output.getText();
		output.setText("");
		String[] temp = screen.split(System.getProperty("line.separator"));
		Arrays.sort(temp, new StringComparator());
		if (asc == true)
		{
			for (int i = 0; i < temp.length; i++)
			{
				output.append(temp[i] + System.getProperty("line.separator"));
			}
		}
		else
		{
			for (int i = temp.length - 1; i >= 0; i--)
			{
				output.append(temp[i] + System.getProperty("line.separator"));
			}
		}
		output.setCaretPosition(0);
	}

	private void add(Container container, Component c, GridBagConstraints gbc, int x, int y, int w, int h)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		container.add(c, gbc);
	}

	public ArrayList getCurrentListing()
	{
		return this.currentListing;
	}

	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new DirectoryLister();
	}
}