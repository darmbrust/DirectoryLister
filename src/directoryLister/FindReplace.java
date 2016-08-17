package directoryLister;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
public class FindReplace extends JDialog
{
	private FindReplace	fr;
	private boolean		displayReplace;
	private JTextArea	textArea;
	private int			currentFoundIndex	= 0;
	JCheckBox			wrap;
	JCheckBox			caseSensitive;
	JRadioButton		reverse;
	JLabel				status;

	public FindReplace(JFrame parent, boolean showReplace, JTextArea textArea)
	{
		super(parent);
		this.setModal(true);
		this.displayReplace = showReplace;
		this.textArea = textArea;
		if (displayReplace)
			this.setSize(350, 185);
		else
			this.setSize(350, 155);
		this.setTitle("Find/Replace Dialog");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		jbInit();
		this.fr = this;
		this.setVisible(true);
	}

	private void jbInit()
	{
		JLabel findLabel = new JLabel("Find");
		final JTextField find = new JTextField();
		JButton findButton = new JButton("Find");
		Dimension buttonSize = new Dimension(90, 25);
		findButton.setPreferredSize(buttonSize);
		findButton.setMinimumSize(buttonSize);
		findButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				status.setText("Ready");
				if ((!fr.wrap.isSelected() && currentFoundIndex == -1))
				{
					Toolkit.getDefaultToolkit().beep();
					status.setText("Search hit end");
					return;
				}
				textArea.requestFocus();
				StringBuffer text = new StringBuffer(textArea.getText());
				String findText = find.getText();
				if (!fr.caseSensitive.isSelected())
				{
					String temp = text.toString().toLowerCase();
					text.setLength(0);
					text.append(temp);
					findText = findText.toLowerCase();
				}
				if (fr.reverse.isSelected())
				{
					if (currentFoundIndex == -1)
					{
						currentFoundIndex = text.length();
					}
					currentFoundIndex = text.substring(0, currentFoundIndex)
							.lastIndexOf(new StringBuffer(findText).toString());

				}
				else
				{
					currentFoundIndex = text.indexOf(findText, currentFoundIndex);
				}
				if (currentFoundIndex == -1)
				{
					textArea.select(-1, -2);
					Toolkit.getDefaultToolkit().beep();
					status.setText("Search hit end");
					textArea.requestFocus();
					return;
				}

				textArea.select(currentFoundIndex, currentFoundIndex + find.getText().length());

				currentFoundIndex++;
			}
		});
		JLabel replaceLabel = new JLabel("Replace");
		final JTextField replace = new JTextField();
		JButton replaceButton = new JButton("Replace");
		replaceButton.setPreferredSize(buttonSize);
		replaceButton.setMinimumSize(buttonSize);
		replaceButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				status.setText("Ready");
				StringBuffer temp = new StringBuffer(textArea.getText());
				int start = textArea.getSelectionStart();
				int finish = textArea.getSelectionEnd();
				if (finish != 0)
				{
					temp.replace(start, finish, replace.getText());
					textArea.setText(temp.toString());
					textArea.select(start, start);
				}
				else
				{
					Toolkit.getDefaultToolkit().beep();
					status.setText("Nothing selected to replace");
				}
			}
		});
		JButton replaceAllButton = new JButton("Replace All");
		replaceAllButton.setPreferredSize(buttonSize);
		replaceAllButton.setMinimumSize(buttonSize);
		replaceAllButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				status.setText("Ready");
				StringBuffer temp = new StringBuffer(textArea.getText());
				String replacementText = replace.getText();
				String searchText = find.getText();
				if (!fr.caseSensitive.isSelected())
				{
					String foo = temp.toString().toLowerCase();
					temp.setLength(0);
					temp.append(foo);
					searchText = searchText.toLowerCase();
				}
				int index = temp.indexOf(searchText);
				while (index != -1)
				{
					temp.replace(index, index + searchText.length(), replacementText);
					index = temp.indexOf(searchText, index + replacementText.length());
				}
				textArea.setText(temp.toString());
			}
		});
		JPanel direction = new JPanel(new GridLayout(2, 2, 0, 0));
		direction.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Options"));
		ButtonGroup group = new ButtonGroup();
		JRadioButton forward = new JRadioButton("Down");
		forward.setSelected(true);
		reverse = new JRadioButton("Up");
		group.add(forward);
		group.add(reverse);
		wrap = new JCheckBox("Wrap");
		wrap.setSelected(true);
		caseSensitive = new JCheckBox("Case Sensitive");
		caseSensitive.setSelected(true);
		JButton cancelButton = new JButton("Done");
		cancelButton.setPreferredSize(buttonSize);
		cancelButton.setMinimumSize(buttonSize);
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fr.setVisible(false);
			}
		});
		status = new JLabel("Ready");
		JPanel main = new JPanel(new GridBagLayout());
		this.setContentPane(main);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		guiAdd(main, findLabel, gbc, 0, 0, 1, 1, 0.0, 0.0, gbc.NONE, gbc.WEST);
		guiAdd(main, find, gbc, 1, 0, 1, 1, 1.0, 0.0, gbc.HORIZONTAL, gbc.CENTER);
		guiAdd(main, findButton, gbc, 2, 0, 1, 1, 0.0, 0.0, gbc.NONE, gbc.EAST);
		if (displayReplace)
		{
			guiAdd(main, replaceLabel, gbc, 0, 1, 1, 1, 0.0, 0.0, gbc.NONE, gbc.WEST);
			guiAdd(main, replace, gbc, 1, 1, 1, 1, 1.0, 0.0, gbc.HORIZONTAL, gbc.CENTER);
			guiAdd(main, replaceButton, gbc, 2, 1, 1, 1, 0.0, 0.0, gbc.NONE, gbc.EAST);
			guiAdd(main, replaceAllButton, gbc, 2, 2, 1, 1, 0.0, 0.0, gbc.NONE, gbc.EAST);
		}
		direction.add(forward);
		direction.add(reverse);
		direction.add(wrap);
		direction.add(caseSensitive);
		gbc.insets = new Insets(0, 5, 5, 5);
		guiAdd(main, direction, gbc, 0, 2, 2, 2, 0.0, 0.0, gbc.HORIZONTAL, gbc.WEST);
		guiAdd(main, cancelButton, gbc, 2, 3, 1, 1, 0.0, 0.0, gbc.NONE, gbc.EAST);
		guiAdd(main, status, gbc, 0, 4, 3, 1, 0.0, 0.0, gbc.HORIZONTAL, gbc.WEST);
	}

	private void guiAdd(Container container, Component c, GridBagConstraints gbc, int x, int y, int w, int h,
			double weightx, double weighty, int fill, int anchor)
	{
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.anchor = anchor;
		gbc.fill = fill;
		container.add(c, gbc);
	}

	public static void main(String[] args) throws Exception
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		JFrame temp = new JFrame();
		temp.setSize(600, 600);
		JTextArea foo = new JTextArea("hello there.. ready for testing Hellow again something hello hi");
		temp.setContentPane(foo);
		temp.setVisible(true);
		new FindReplace(temp, true, foo);
	}
}