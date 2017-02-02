package org.intalio.deploy.deployment.ws;

import java.io.File;
import java.rmi.RemoteException;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.axis2.AxisFault;
import org.intalio.deploy.deployment.ws.DeployServiceStub.DeployAssembly;
import org.intalio.deploy.deployment.ws.DeployServiceStub.DeployResult;

public class DeployClientAnt {
	public static void main(String ar[]) {
		DeployClientAnt client = new DeployClientAnt();
		try {
			String endpoint = ar[0];
			String user = ar[1];
			String archivedProject = ar[2];
			/*Scanner sc = new Scanner(System.in);
			System.out.println("Service endpoint is " + endpoint + "\n" +
					"Give new endpoint if would like to change or type No to skip");
			String answer = sc.next();
			if (!answer.equalsIgnoreCase("no")) {
				endpoint = answer;
			}
			System.out.println("Absolute path to archived project");
			archivedProject = sc.next();
			System.out.println("Username");
			user = sc.next();
			sc.close();*/
			File projectFile = new File(archivedProject);
			
			DeployResult result = client.doDeploy(endpoint, user, projectFile);
			if (result.getSuccess().equalsIgnoreCase("true")) {
				System.out.println("Deployed Project " + result.getAssemblyName() + " as Version " + result.getAssemblyVersion());
			}
		} catch (AxisFault fault) {
			fault.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public DeployResult doDeploy(String endpoint, String user, File archivedProject) throws AxisFault, RemoteException{
		DeployServiceStub stub = new DeployServiceStub(endpoint);
		DeployAssembly assembly = new DeployAssembly();
		String assemblyName = extractAssemblyName(archivedProject.getName());
		FileDataSource fileDataSource = new FileDataSource(archivedProject);
		DataHandler dataHandler = new DataHandler(fileDataSource);
		assembly.setAssemblyName(assemblyName);
		assembly.setActivate(true);
		assembly.setZip(dataHandler);
		assembly.setUser(user);
		return stub.deployAssembly(assembly);
	}
	
	private String extractAssemblyName(String fileName) {
		String assymblyName;
		if(fileName.contains("-")) {
			assymblyName = fileName.substring(0, fileName.indexOf('-'));
		}
		else {
			assymblyName = fileName.substring(0, fileName.length()-4);
		}
		return assymblyName;
	}
}
