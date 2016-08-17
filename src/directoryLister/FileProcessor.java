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
public class FileProcessor implements Runnable
{
	private ArrayList		files;
	private File			baseFile;
	private DirectoryLister	dl;
	private boolean			recurse;

	public FileProcessor(File baseFile, DirectoryLister dl, boolean recurse)
	{
		this.baseFile = baseFile;
		this.dl = dl;
		this.recurse = recurse;
	}

	public void run()
	{
		files = new ArrayList();
		ProcessDirectory(baseFile, 0);
		dl.showFiles(files);
		dl.process.setText("Process");
	}

	public void ProcessDirectory(File directory, int depth)
	{
		if (dl.continueProcessing == false)
			return;
		int progress = 0;
		dl.progressBar.setValue(progress);
		dl.progressBar.setString("Processing... " + directory.getName());
		File[] tempFile = directory.listFiles();
		dl.progressBar.setMaximum(tempFile.length * 2);
		if (tempFile == null)
			return; //keeps you from getting into trouble, in the recycle bin
					// and such
		if (dl.continueProcessing == false)
			return;
		//Sort the directories and the files into temp directories
		ArrayList tempFiles = new ArrayList();
		ArrayList tempDirectories = new ArrayList();
		for (int j = 0; j < tempFile.length; j++)
		{
			dl.progressBar.setValue(progress++);
			if (tempFile[j].isDirectory())
				tempDirectories.add(tempFile[j]);
			else
				tempFiles.add(tempFile[j]);
		}
		File[] tempFilesArray = (File[]) tempFiles.toArray(new File[tempFiles.size()]);
		File[] tempDirectoriesArray = (File[]) tempDirectories.toArray(new File[tempDirectories.size()]);
		Arrays.sort(tempFilesArray, new FileComparator());
		Arrays.sort(tempDirectoriesArray, new FileComparator());
		for (int i = 0; i < tempDirectoriesArray.length; i++)
		{
			if (dl.continueProcessing == false)
				break;
			dl.progressBar.setValue(progress++);
			files.add(new FileInfo(tempDirectoriesArray[i].getAbsolutePath(), tempDirectoriesArray[i].getName(), true,
					depth));
			if (recurse)
			{
				ProcessDirectory(tempDirectoriesArray[i], depth + 1);
			}
			dl.progressBar.setString("Processing... " + directory.getName());
			dl.progressBar.setMaximum(tempFile.length * 2);
		}
		for (int i = 0; i < tempFilesArray.length; i++)
		{
			if (dl.continueProcessing == false)
				break;
			dl.progressBar.setValue(progress++);
			files.add(new FileInfo(tempFilesArray[i].getAbsolutePath(), tempFilesArray[i].getName(), false, depth));
		}
	}
}