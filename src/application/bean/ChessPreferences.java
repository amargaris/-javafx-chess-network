package application.bean;

public class ChessPreferences {
	
	private String playerName;
	private int windowWidth;
	private int windowHeight;
	private int listenPort;
	
	public ChessPreferences(String playerName, int windowWidth, int windowHeight,int listenPort) {
		super();
		this.playerName = playerName;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.listenPort = listenPort;
	}
	
	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public int getWindowWidth() {
		return windowWidth;
	}
	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
	}
	public int getWindowHeight() {
		return windowHeight;
	}
	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
	}
	
}
