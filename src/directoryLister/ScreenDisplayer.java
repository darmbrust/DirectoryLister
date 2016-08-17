package directoryLister;

import java.io.*;
import java.util.*;

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
public class ScreenDisplayer implements Runnable
{
	private DirectoryLister	dl;

	public ScreenDisplayer(DirectoryLister dl)
	{
		this.dl = dl;
	}

	public void run()
	{
		display(dl.getCurrentListing());
	}

	private void display(ArrayList currentListing)
	{
		int numberOfFiles = 0;
		int numberOfDirectories = 0;
		dl.progressBar.setString("Saving/Displaying results...");
		if (currentListing == null)
			return;
		dl.progressBar.setValue(0);
		dl.progressBar.setMaximum(currentListing.size() - 1);
		String current = "";
		String temp;
		for (int i = 0; i < currentListing.size(); i++)
		{
			dl.progressBar.setValue(i);
			FileInfo currentFile = (FileInfo) currentListing.get(i);
			current = "";
			if (currentFile.isDirectory)
			{
				numberOfDirectories++;
				if (dl.listDirectories.isSelected())
				{
					if (dl.indent.isSelected())
						current += spaceMaker(currentFile.depth, "  ");
					else if (dl.treeDirectory.isSelected())
					{
						if (currentFile.depth != 0)
							current += spaceMaker(currentFile.depth - 1, "|   ") + "|__ ";
					}
					if (dl.prefixToDirectories.isSelected())
						current += dl.textToPrefix.getText();
					if (dl.fullNames.isSelected())
					{
						temp = currentFile.longName;
						if (dl.removeDriveLetters.isSelected())
							current += temp.substring(temp.indexOf("\\") + 1);
						else
							current += temp;
					}
					else
						current += currentFile.shortName;
					if (dl.appendToDirectories.isSelected())
						current += dl.textToAppend.getText();
					if (dl.writeToScreen.isSelected())
					{
						dl.output.append(current + System.getProperty("line.separator"));
					}
					if (dl.outputFile != null)
					{
						try
						{
							dl.outputFile.write(current + System.getProperty("line.separator"));
						}
						catch (java.io.IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else
			//Not a directory section
			{
				numberOfFiles++;
				if (dl.listFiles.isSelected())
				{
					if (dl.indent.isSelected())
						current += spaceMaker(currentFile.depth, "  ");
					else if (dl.treeDirectory.isSelected())
					{
						if (currentFile.depth != 0)
							current += spaceMaker(currentFile.depth - 1, "|   ") + "|__ ";
					}
					if (dl.prefixToFiles.isSelected())
						current += dl.textToPrefix.getText();
					if (dl.fullNames.isSelected())
					{
						temp = currentFile.longName;
						if (dl.removeExtensions.isSelected())
						{
							int extensionPosition = temp.lastIndexOf(".");
							if (extensionPosition != -1)
								temp = temp.substring(0, extensionPosition);
						}
						if (dl.removeDriveLetters.isSelected())
							current += temp.substring(temp.indexOf("\\") + 1);
						else
							current += temp;
					}
					else
					{
						temp = currentFile.shortName;
						if (dl.removeExtensions.isSelected())
						{
							int extensionPosition = temp.lastIndexOf(".");
							if (extensionPosition != -1)
								temp = temp.substring(0, extensionPosition);
						}
						current += temp;
					}
					if (dl.appendToFiles.isSelected())
						current += dl.textToAppend.getText();
					if (dl.writeToScreen.isSelected())
					{
						dl.output.append(current + System.getProperty("line.separator"));
					}
					if (dl.outputFile != null)
					{
						try
						{
							dl.outputFile.write(current + System.getProperty("line.separator"));
						}
						catch (java.io.IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}
		if (dl.outputFile != null)
		{
			try
			{
				dl.outputFile.flush();
			}
			catch (IOException error)
			{
			}
		}
		dl.progressBar.setString("Done - " + numberOfFiles + " files in " + numberOfDirectories + " directories.");
	}

	private String spaceMaker(int depth, String spaceCharacter)
	{
		String temp = "";
		for (; depth != 0; depth--)
		{
			temp += spaceCharacter;
		}
		return temp;
	}
}