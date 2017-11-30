package application.gfx.comp.piece;

import java.util.ArrayList;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import application.ChessApplication;
import application.gfx.comp.Drawable;
import application.gfx.comp.actions.PossibleAttack;
import application.gfx.comp.actions.PossibleMovement;

public abstract class Piece implements Drawable{
	public Image im;
	protected ImageView view;
	
	public SimpleDoubleProperty width;
	public SimpleDoubleProperty height;
	public SimpleIntegerProperty xpos,ypos;
	
	public String type;
	public PieceColor thecol;
	protected ChessApplication ref;
	//Xrwmata
	public static final String COLOR_BLACK="black";
	public static final String COLOR_WHITE="white";
	//Eidos
	public static final String PIECE_PAWN = "pawn";
	public static final String PIECE_BISHOP = "bishop";
	public static final String PIECE_QUEEN = "queen";
	public static final String PIECE_KING="king";
	public static final String PIECE_TOWER="tower";
	public static final String PIECE_KNIGHT="knight";
	
	public Piece(ChessApplication abax,PieceColor col,String thetype){
		ref=abax;
		type=thetype;
		thecol=col;
		width  = new SimpleDoubleProperty();
		height = new SimpleDoubleProperty();
		xpos   = new SimpleIntegerProperty(-1);
		ypos   = new SimpleIntegerProperty(-1);
		width.bind(abax.canvas.widthProperty());
		height.bind(abax.canvas.heightProperty());
		im = new Image(getClass().getResourceAsStream("images/"+col.name()+"_"+type+".png"));
	}
	public enum PieceColor{
		white,black
	}
	public enum PieceType{
		pawn,bishop,queen,king,tower,knight
	}
	public void setPosition(int x,int y){
		if(xpos.get()>=0&&ypos.get()>=0){
			ref.gamecontext[xpos.get()][ypos.get()]=null;
		}
		xpos.set(x);
		ypos.set(y);
		ref.gamecontext[x][y]=this;
	}
	public PieceColor getColor(){
		return thecol;
	}
	public static void testMovement(int dx,int dy,Piece pic,ArrayList<Drawable> results){
		if(pic.xpos.get()+dx>=0&&pic.xpos.get()+dx<8&&pic.ypos.get()+dy>=0&&pic.ypos.get()+dy<8){
			if(pic.ref.gamecontext[pic.xpos.get()+dx][pic.ypos.get()+dy]==null){
				results.add(new PossibleMovement(pic.ref,pic.xpos.get()+dx,pic.ypos.get()+dy,pic));
			} else{
				if(!pic.ref.gamecontext[pic.xpos.get()+dx][pic.ypos.get()+dy].thecol.equals(pic.thecol)){
					results.add(new PossibleAttack(pic.ref,pic.xpos.get()+dx,pic.ypos.get()+dy,pic));
				}
			}
		}
	}
	@Override
	public void draw(GraphicsContext cx) {
		double step1 = width.get()/8;
		double step2 = height.get()/8;
		cx.drawImage(im, xpos.get()*step1/*+(step1/2)-(im.getWidth()/2step1)*/, ypos.get()*step2/*+(step2/2)-(im.getHeight()/2)*/,step1,step2);
	}
	public void delete(){
		//TODO
		ref.gamecontext[xpos.get()][ypos.get()]=null;
		ref.objectlist.remove(this);
	}
	public Image toTransparent(){
		return null;
	}
	public abstract ArrayList<Drawable> getValidMoveLocations();
}



