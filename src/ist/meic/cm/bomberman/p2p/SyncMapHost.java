package ist.meic.cm.bomberman.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import ist.meic.cm.bomberman.InGame;
import ist.meic.cm.bomberman.controller.MapController;
import ist.meic.cm.bomberman.controller.OperationCodes;
import ist.meic.cm.bomberman.multiplayerC.Message;
import ist.meic.cm.bomberman.p2p.manager.Client;
import ist.meic.cm.bomberman.status.BombermanStatus;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class SyncMapHost extends Service {
	private MPDMainGamePanel gamePanel;
	private boolean end;
	private OperationCodes option;
	private boolean running;

	@Override
	public IBinder onBind(Intent intent) {

		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		gamePanel = (MPDMainGamePanel) InGame.getGamePanel();
		end = intent.getBooleanExtra("end", false);
		option = (OperationCodes) intent.getSerializableExtra("option");
		running = true;

		ThreadRefresh td = new ThreadRefresh();
		td.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private class ThreadRefresh extends Thread {

		private ArrayList<Client> clients;
		private ObjectInputStream input;
		private ObjectOutputStream output;
		private Message toSend;
		private Message received;
		private ServerSocket mySocket;
		private static final long REFRESH = 500;

		@Override
		public void run() {
			super.run();

			clients = gamePanel.getClients();

			mySocket = gamePanel.getServerSocket();

			try {

				if (end) {
					toSend = new Message(Message.END);
					// sendToServer();
					gamePanel.endConnection();
					running = false;
				} else
					while (running) {
						for (Client current : clients) {

							input = current.getIn();
							output = current.getOut();
							received = (Message) input.readObject();

							if (received.getCode() == Message.REQUEST) {
								toSend = new Message(Message.SUCCESS,
										gamePanel.getMapController());
								sendToClient();
							}

						}

						sleep(REFRESH);
					}

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (OptionalDataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		private void sendToClient() throws IOException {
			synchronized (output) {
				output.writeObject(toSend);
				output.reset();
			}

		}
	}

}