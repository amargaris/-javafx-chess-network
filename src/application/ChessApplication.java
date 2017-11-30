package application;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import application.bean.ChessPreferences;
import application.gfx.comp.ChessBoard;
import application.gfx.comp.Drawable;
import application.gfx.comp.actions.PossibleAttack;
import application.gfx.comp.actions.PossibleMovement;
import application.gfx.comp.actions.Selection;
import application.gfx.comp.piece.Piece;
import application.gfx.comp.piece.PieceFactory;
import application.gfx.comp.piece.Piece.PieceColor;
import application.network.TCPDemon;
import application.network.bean.ChessMessage;
import application.network.bean.ChessMessage.MessageType;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ChessApplication extends Application implements EventHandler<Event>{
	
	//test
	//Game graphics
	public List<Drawable> objectlist=new ArrayList<Drawable>();
	public List<Drawable> displaylist=new ArrayList<Drawable>();
	public ChessBoard chessboard;
	public Canvas canvas;
	//Status
	public Text text;
	//Chat
	public ListView<String> list;
	public ObservableList<String> items;
	
	private Stage primary;
	
	//private String ipAddress;
	//private int port;
	
	public boolean playerturn;
	//String playername;
	
	public Piece[][] gamecontext = new Piece[8][8];
	
	private Piece selectedpiece;
	
	private Thread ai_thread;
	private AnimationTimer repaint_timer;
	private TCPDemon demon;
	private ChessMessage tempMessage;
	
	private PieceColor computerColor=PieceColor.white;
	private MenuItem startGame,hostGame,joinGame;
	private int gameMode=-1;
	
	private static final int GAME_AI=1;
	private static final int GAME_HOST=2;
	private static final int GAME_JOIN=3;
	
	private static final String PREFERENCE_FILE = "preferences.json";
	
	private ChessPreferences preferences;
	
	//Audio
	AudioClip ALERT_AUDIOCLIP = new AudioClip(getClass().getResource("audio/alert.wav").toString());
	
	public static final String[][] chess = {{"black@tower","black@knight","black@bishop","black@king","black@queen","black@bishop","black@knight","black@tower"},
											{"black@pawn","black@pawn","black@pawn","black@pawn","black@pawn","black@pawn","black@pawn","black@pawn"},
											{null,null,null,null,null,null,null,null},
											{null,null,null,null,null,null,null,null},
											{null,null,null,null,null,null,null,null},
											{null,null,null,null,null,null,null,null},
											{"white@pawn","white@pawn","white@pawn","white@pawn","white@pawn","white@pawn","white@pawn","white@pawn"},
											{"white@tower","white@knight","white@bishop","white@queen","white@king","white@bishop","white@knight","white@tower"}
	};
    @Override
    public void start(Stage primaryStage) {
    	primary = primaryStage;
    	if(!new File(PREFERENCE_FILE).exists()){
    		preferences = new ChessPreferences("Player #"+new Random().nextInt(Integer.MAX_VALUE), 400, 400 ,10000);
    		try(FileOutputStream faos = new FileOutputStream(new File(PREFERENCE_FILE))){
    			byte[] forWrite = new GsonBuilder().setPrettyPrinting().create().toJson(preferences).getBytes("utf-8");
    			faos.write(forWrite);
    		}catch(Exception e){
    			System.out.println("Could not create preference file! Reason:"+e.toString());
    			e.printStackTrace();
    		}
    	} else {
    		try {
				preferences = new Gson().fromJson(
									new String(Files.readAllBytes(
													Paths.get(PREFERENCE_FILE)),
															"utf-8"), ChessPreferences.class);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
    	}
        
        primaryStage.setTitle("JavaFX Chess Application - Welcome "+preferences.getPlayerName());
        primaryStage.getIcons().add(new Image(Piece.class.getResourceAsStream("images/black_queen.png")));
        
        BorderPane root = new BorderPane();
        Pane pane = new Pane();
        primaryStage.setWidth(preferences.getWindowWidth());
        primaryStage.setHeight(preferences.getWindowHeight());
        canvas = new Canvas(1,1);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());
        pane.getChildren().add(canvas);
        
        HBox box2 = new HBox();
        box2.setPrefHeight(25);
        VBox box3 = new VBox();
        VBox box4 = new VBox();
        box3.setPrefWidth(25);
        box4.setPrefWidth(25);
        
        MenuBar menuBar = new MenuBar();
        menuBar.setPrefHeight(25);
        Menu menuFile = new Menu("Game");
        startGame = new MenuItem("Start with AI");
        startGame.addEventHandler(Event.ANY,this);
        hostGame = new MenuItem("Host Game");
        hostGame.addEventHandler(Event.ANY,this);
        joinGame = new MenuItem("Join Game");
        joinGame.addEventHandler(Event.ANY, this);
        //TODO load game, train NN, objective function , viasmos
        menuFile.getItems().addAll(startGame,hostGame,joinGame);
        menuBar.getMenus().add(menuFile);
        
        ListView<String> list = new ListView<String>();
        items = FXCollections.observableArrayList();
        list.setItems(items);

        items.addListener(new ListChangeListener<String>(){
			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends String> arg0) {
				if(items.size() != 0)
					ALERT_AUDIOCLIP.play();
				if(items.size() > 10){
					items.remove(0);
				}
			}	
        });
        
        root.setCenter(pane);
        root.setTop(menuBar);
        VBox statusBar = new VBox();
        statusBar.setStyle("-fx-background-color: gainsboro");
        text = new Text("This is a Chess JavaFX Application by Aristotelis Margaris \u00A9 2014");
        statusBar.getChildren().add(text);
        root.setBottom(statusBar);
        root.setRight(box3);
        root.setLeft(box4);
        //
        Scene scene = new Scene(root, 400, 400);
        scene.setFill(Color.OLDLACE);
        primaryStage.setScene(scene);
        primaryStage.addEventHandler(Event.ANY, this);
        primaryStage.show();
        initThreads();
    }
    public void initThreads(){
    	 repaint_timer = new AnimationTimer() {
             @Override
             public void handle(long l) {
             	try{
 	            	GraphicsContext cx=canvas.getGraphicsContext2D();
 	            	chessboard.draw(cx);
 	            	for(Drawable dr:displaylist){
 	            		dr.draw(cx);
 	            	}
 	            	for(Drawable dr:objectlist){
 	            		dr.draw(cx);
 	            	}
             	}catch(Exception e){
             		
             	}
             }
  
         };
         repaint_timer.start();
         ai_thread = new Thread(()->{
     		if(gameMode==ChessApplication.GAME_AI){
        		makeRandomMotion(computerColor);
        		playerturn=true;
     		}
     		else if(gameMode==ChessApplication.GAME_HOST||gameMode==ChessApplication.GAME_JOIN){
     			if(demon!=null){
     				demon.enqueue(tempMessage);
     			}else{
     				text.setText("connection not established");
     			}
     		}
         });

    }
    public void cleanUp(){
    	if(demon!=null){
    		try{
    			demon.finalize();
    		}catch(Exception e){
    			//e.printStackTrace();
    			text.setText(e.toString());
    		}
    	}
    	objectlist.clear();
    	for(int i=0;i<gamecontext.length;i++){
    		for(int j=0;j<gamecontext[i].length;j++){
    			gamecontext[i][j]=null;
    		}
    	}
    }
    /**
     * Method that takes as parameter the AI's selected color,
     * generates all possible motion/attacks and choses in random
     * one of them to perform
     * 
     * @param thecol The AI's color
     */
    public void makeRandomMotion(PieceColor thecol){
    	ArrayList<Drawable> possible_locations = new ArrayList<Drawable>();
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(gamecontext[i][j]!=null){
					if(gamecontext[i][j].getColor()==thecol){
    					possible_locations.addAll(gamecontext[i][j].getValidMoveLocations());
    				}
				}
			}
		}
		int a = new Random().nextInt(possible_locations.size());
		Drawable tar=possible_locations.get(a);
		if(tar.getClass().equals(PossibleAttack.class)){
			PossibleAttack loktar = (PossibleAttack)tar;
			loktar.attack();
		}
		else if(tar.getClass().equals(PossibleMovement.class)){
			PossibleMovement movtar = (PossibleMovement)tar;
			movtar.replace();
		}
    }
    public void printContext(){
    	for(int i=0;i<gamecontext.length;i++){
    		System.out.println(Arrays.toString(gamecontext[i]));
    	}
    }
    public static String getHour(){
    	Calendar calendar = new GregorianCalendar();
	    int hour = calendar.get(Calendar.HOUR);
	    int minute = calendar.get(Calendar.MINUTE);
	    int second = calendar.get(Calendar.SECOND);
	    return String.format("[%d:%d:%d]", hour,minute,second);
    }
    public void initializeChessBoard(String[][] chess){
    	for(int i=0;i<8;i++){
    		for(int j=0;j<8;j++){
    			if(chess[i][j]==null){
    				continue;
    			}
    			String[] val =chess[i][j].split("@");
    			Piece pawn = PieceFactory.getPiece(val[1], PieceColor.valueOf(val[0]), this);
    			pawn.setPosition(i, j);
    			objectlist.add(pawn);
    		}
    	}	
    }
    public void digest(ChessMessage mes1){
    	final ChessMessage mes=mes1;
    	if(mes.type == MessageType.motion){
	    	new Thread(()->{
	    	    	Piece p=gamecontext[mes.a_1][mes.b_1];
	    	    	boolean flag=false;
	    			if(gamecontext[mes.a_2][mes.b_2]!=null){
	    				if(gamecontext[mes.a_2][mes.b_2].type.equals(Piece.PIECE_KING)){
	    					flag=true;	
	    				}
	    				gamecontext[mes.a_2][mes.b_2].delete();
	    			}
	    			gamecontext[mes.a_2][mes.b_2]=p;
	    			gamecontext[mes.a_1][mes.b_1]=null;
	    			p.xpos.set(mes.a_2);
	    			p.ypos.set(mes.b_2);
	    			text.setText(mes.toString());
	    			playerturn = true;
	    			if(flag){
	    				text.setText(p.thecol.name()+" won");
	    				playerturn=false;
	    				cleanUp();
	    				chessboard = new ChessBoard(ChessApplication.this);
	    				initializeChessBoard(ChessApplication.chess);
	    			} else {
	    				Platform.runLater(()-> {
	    					items.add("");
	    					items.remove(items.size()-1);
	    				});
	    			}
	    		}
	    	).start();
    	}else if(mes.type == MessageType.chat){
    		Platform.runLater(()-> items.add(ChessApplication.getHour()+mes.message));
    	}
    	
    }

    @Override
	public void handle(Event arg0) {
		if(arg0.getSource().equals(canvas)){
			if(arg0.getEventType()==MouseEvent.MOUSE_CLICKED){
				MouseEvent event=(MouseEvent)arg0;
				if(!playerturn){
					return;
				}
				if(chessboard==null){
					return;
				}
				int[] s=chessboard.getXY(event);
				if(s==null){
					return;
				}
				int x_norm = s[0];
				int y_norm = s[1];
				if(event.getButton().equals(MouseButton.PRIMARY)){
					displaylist.clear();
					if(gamecontext[x_norm][y_norm]!=null){
						if(computerColor!=gamecontext[x_norm][y_norm].getColor()){
							displaylist.add(new Selection(gamecontext[x_norm][y_norm]));
							List<Drawable> dr=gamecontext[x_norm][y_norm].getValidMoveLocations();
							if(dr!=null){
								displaylist.addAll(dr);
							}
							selectedpiece=gamecontext[x_norm][y_norm];
						}
					}
				}else{
					if(selectedpiece!=null){
						List<Drawable> dr=selectedpiece.getValidMoveLocations();
						if(dr==null){
							selectedpiece=null;
							displaylist.clear();
							return;
						}
						boolean flag=false;
						boolean flag2=true;
						for(Drawable tar:dr){
							if(tar instanceof PossibleAttack){
								PossibleAttack loktar = (PossibleAttack)tar;
								if(loktar.isMatch(x_norm, y_norm)){
									tempMessage= new ChessMessage(loktar.actor.xpos.get(),
																  loktar.actor.ypos.get(),
																  				   x_norm,
																  				   y_norm);
									flag=loktar.attack();
									flag2=false;
									break;
								}
							}else if(tar instanceof PossibleMovement){
								PossibleMovement movtar = (PossibleMovement)tar;
								if(movtar.isMatch(x_norm, y_norm)){
									tempMessage= new ChessMessage(movtar.actor.xpos.get(),
																  movtar.actor.ypos.get(),
																  				   x_norm,
																  				   y_norm);
									movtar.replace();
									flag2=false;
									break;
								}
							}
							
						}
						
						selectedpiece=null;
						displaylist.clear();
						if(flag2){
							return;
						}
						playerturn=false;
						try{
							ai_thread.run();
						}catch(Exception e){
							//e.printStackTrace();
							text.setText(e.toString());
						}
						if(flag){
							text.setText("You Won!");
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
								
							}
							cleanUp();
							chessboard = new ChessBoard(this);
							initializeChessBoard(ChessApplication.chess);
						}
					}
				}
			}	
		}else if(arg0.getSource().equals(startGame)){
			gameMode=GAME_AI;
			cleanUp();
	        chessboard = new ChessBoard(this);
	        initializeChessBoard(chess);
	        playerturn=true;
		}else if(arg0.getSource().equals(joinGame)){
			gameMode=GAME_JOIN;
			cleanUp();
	        chessboard = new ChessBoard(this);
	        initializeChessBoard(chess);
	        
	        TextInputDialog dialog = new TextInputDialog("Enter Remote Address and port (Format ->  address:port ) ");
	        dialog.setTitle("Network Preferences");
	        dialog.setHeaderText("Format ip:port");
	        dialog.setContentText("Address and port");

	        // Traditional way to get the response value.
	        Optional<String> result = dialog.showAndWait();
	        if (!result.isPresent())return;

	        // The Java 8 way to get the response value (with lambda expression).
	        String port = result.orElse(null);
	        String[] parts = port.split(":");
	        int targetPort = -1;
	        String ip = null;
	        try{
	        	targetPort = Integer.parseInt(parts[1]);
	        	ip		   = parts[0]; 
	        }catch(Exception e){
	        	e.printStackTrace();
	        	return;
	        }
	        String targetHost=ip;
	        int targetPortA = targetPort;
	        new Thread(()->{
		        try{
		        	demon = new TCPDemon(ChessApplication.this,
		        			 		 InetAddress.getByName(targetHost),
		        			 		 						targetPortA,
		        			 		 						TCPDemon.CLIENT);
		        }catch(Exception e){
		        	text.setText("Connection refused");
		        	return;
		        }
        	}).start();
	        playerturn=false;
	        computerColor=PieceColor.black;
		}else if(arg0.getSource().equals(hostGame)){
				gameMode=GAME_HOST;
				cleanUp();
		        chessboard = new ChessBoard(this);
		        playerturn=false;
		        initializeChessBoard(chess);
		        new Thread(()->{
	        		try{
	        			demon = new TCPDemon(ChessApplication.this,
	        					  InetAddress.getByName("0.0.0.0"),
	        					  	   preferences.getListenPort(),
	        					  	   				TCPDemon.HOST);
	        			demon.start();
	        		}catch(Exception e){
	        			text.setText(e.toString());
	        		}
	        	}).start();
		}else if(arg0.getSource().equals(primary)){
			if(arg0.getEventType().equals(javafx.stage.WindowEvent.WINDOW_CLOSE_REQUEST)){
				System.exit(0);
			}
		}
		
	}
    public static void main(String[] args) {
    	launch(args);
    }
    
}