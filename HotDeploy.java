package org.intalio.deploy.deployment.ws;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.commons.io.FileUtils;

public class HotDeploy {
	public static void main(String ar[]) throws IOException {
		HotDeploy obj = new HotDeploy();
		String archivedProject = ar[0];
		String deployDirPath = ar[1];
		obj.hotDeploy(archivedProject, deployDirPath);
	}

	public void hotDeploy(String archivedProject, String deployDirPath) {
		String assemblyName = extractAssemblyName(new File(archivedProject)
				.getName());
		int newVersionNumber = getNewVersionNumber(deployDirPath, assemblyName);
		String projectDir = (newVersionNumber == 0) ? assemblyName
				: assemblyName + "." + newVersionNumber;
		copyProject(projectDir, archivedProject, deployDirPath);
	}

	public int extractVersion(String fileName) {
		int version = 0;
		String[] result = fileName.split("\\.");
		if (result.length < 2)
			version = 1;
		else
			version = Integer.parseInt(result[1]);
		return version;
	}

	public void copyProject(String projectDir, String archivedProject,
			String deployDirPath) {
		File tempProjectFile = new File(System.getProperty("java.io.tmpdir")
				+ projectDir);
		try {
			if (tempProjectFile.exists()) {
				//System.out.println("Folder " + projectDir + " already exist, will delete it and create new one");
				FileUtils.deleteDirectory(tempProjectFile);
			}
			tempProjectFile.mkdir();
			ZipFile zipFile = new ZipFile(archivedProject);
			zipFile.extractAll(tempProjectFile.getPath());
			File destDir = new File(deployDirPath + "\\" + projectDir);
			FileUtils.copyDirectory(tempProjectFile, destDir);
			System.out.println("Deployed project " + destDir.getName());
		} catch (ZipException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String extractAssemblyName(String fileName) {
		String assymblyName;
		if (fileName.contains("-")) {
			assymblyName = fileName.substring(0, fileName.indexOf('-'));
		} else {
			assymblyName = fileName.substring(0, fileName.length() - 4);
		}
		return assymblyName;
	}

	public int getNewVersionNumber(String deployDirPath, String assemblyName) {
		File dir = new File(deployDirPath);
		final String projectName = assemblyName;
		int[] versions;
		String[] files = dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.contains(projectName) && (!name.endsWith("deployed"))
						&& (!name.endsWith("invalid")))
					return true;
				else
					return false;
			}
		});
		if (files.length == 0) {
			return 0;
		} else {
			versions = new int[files.length];
			for (int i = 0; i < files.length; i++) {
				//System.out.println("Found " + files[i]);
				versions[i] = extractVersion(files[i]);
			}
			Arrays.sort(versions);
			return versions[versions.length - 1] + 1;
		}
	}
}
