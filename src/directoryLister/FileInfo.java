package directoryLister;

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
public class FileInfo
{
	public String	longName;
	public String	shortName;
	public boolean	isDirectory;
	public int		depth;

	public FileInfo(String longName, String shortName, boolean isDirectory, int depth)
	{
		this.longName = longName;
		this.shortName = shortName;
		this.isDirectory = isDirectory;
		this.depth = depth;
	}
}