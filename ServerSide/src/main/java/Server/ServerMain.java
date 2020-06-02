package Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import CloneEntities.*;
import CommonElements.DataElements;
import CommonElements.Login;
import Hibernate.HibernateMain;
import Hibernate.Entities.*;
import OCSF.AbstractServer;
import OCSF.ConnectionToClient;

public class ServerMain extends AbstractServer {
	static int numberOfConnectedClients;
	private ServerOperations serverHandler = null;
	
	public ServerMain(int port) {
		super(port);
		serverHandler = new ServerOperations();
		numberOfConnectedClients = 0;
	}

	@Override
	protected synchronized void clientDisconnected(ConnectionToClient client) {
		System.out.println("Client disconnected from server");
		super.clientDisconnected(client);
		numberOfConnectedClients = this.getNumberOfClients() - 1;
		System.out.println("Number of connected client(s): " + numberOfConnectedClients + "\n");

		if (numberOfConnectedClients == 0) {
			System.out.print("Do you want to close the server? (Yes \\ No): ");

			try (Scanner input = new Scanner(System.in)) {
				String stringInput = input.nextLine().toLowerCase();
				if (stringInput.equals("yes")) {
					try {
						this.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					System.out.println("Server ready!");
			}
		}
	}

	@Override
	protected void clientConnected(ConnectionToClient client) {
		System.out.println("New client connected.");
		super.clientConnected(client);
		numberOfConnectedClients = this.getNumberOfClients();
		System.out.println("Number of connected client(s): " + numberOfConnectedClients + "\n");
	}

	@Override
	protected void serverClosed() {
		HibernateMain.closeSession();
		super.serverClosed();
	}

	/**
	 * The function gets new msg from client Parsing the opcode and data Handle the
	 * client request Send back results
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		DataElements de = null;
		try {
			de = (DataElements) msg;
			Object dataFromDB = null;
			System.out.println("Received message from client: opcode = " + de.getOpcodeFromClient());

			switch (de.getOpcodeFromClient()) {
			case GetAllStudies:
				dataFromDB = serverHandler.handleSendStudiesToUser();
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.SendAllStudies);
				de.setData(dataFromDB);
				break;
			case GetAllCoursesInStudy:
				dataFromDB = serverHandler.handleSendCoursesFromStudy((CloneStudy) de.getData());
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.SendAllCoursesInStudy);
				de.setData(dataFromDB);
				break;
			case GetAllQuestionInCourse:
				dataFromDB = serverHandler.handleSendQuestionsFromCourse((CloneCourse) de.getData());
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.SendAllQuestionInCourse);
				de.setData(dataFromDB);
				break;
			case GetAllQuestion:
				dataFromDB = serverHandler.handleSendAllQuestions();
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.SendAllQuestion);
				de.setData(dataFromDB);
				break;
			case UpdateQuestion:
				dataFromDB = serverHandler.handleUpdateQuestion((CloneQuestion) de.getData());
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.UpdateQuestionResult);
				de.setData(dataFromDB);
				break;
			case UserLogin:
				dataFromDB = serverHandler.handleLoginRequest((Login) de.getData());
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.UserLoggedIn);
				de.setData(dataFromDB);
				break;
			default:
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.Error);
				de.setData("handleMessageFromClient: Unknown Error");
			}
		} catch (Exception e) {
			e.printStackTrace();
			de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.Error);
			de.setData(e.getMessage());

		} finally {
			if (de.getData() == null) {
				de.setOpCodeFromServer(DataElements.ServerToClientOpcodes.Error);
				de.setData("handleMessageFromClient: Unknown Error");
			}

			System.out.println("Send result to user! opcode = " + de.getOpCodeFromServer() + "\n");
			sendToAllClients(de);
		}
	}

	/**
	 * Entry point to server
	 * 
	 * @param args - <port>
	 * @throws IOException
	 */
	public static void main(String[] args){
		if (args.length != 1) {
			System.out.println("Required argument: <port>");
			return;
		}
	
		ServerMain server = new ServerMain(Integer.parseInt(args[0]));
		boolean hibernateStatus = HibernateMain.initHibernate();
		if (hibernateStatus == false) {
			System.out.println("Error during Hibernate initialization");
			return;
		}

		try {
			server.listen();
			System.out.println("Server ready!\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}