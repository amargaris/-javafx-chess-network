package application.gfx.comp.actions;

import application.ChessApplication;
import application.gfx.comp.Drawable;
import application.gfx.comp.piece.Piece;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PossibleAttack implements Drawable{
	private SimpleDoubleProperty width,height;
	private SimpleIntegerProperty x,y;
	private ChessApplication ref;
	public Piece actor;
	
	public PossibleAttack(ChessApplication chess,int x1,int y1,Piece theactor){
		ref=chess;
		x= new SimpleIntegerProperty();
		y= new SimpleIntegerProperty();
		width = new SimpleDoubleProperty();
		width.bind(chess.canvas.widthProperty());
		height = new SimpleDoubleProperty();
		height.bind(chess.canvas.heightProperty());
		x.set(x1);
		y.set(y1);
		actor=theactor;
	}
	public boolean isMatch(int xtar,int ytar){
		return xtar==x.get()&&ytar==y.get();
	}
	public boolean attack(){
		boolean flag=false;
		if(ref.gamecontext[x.get()][y.get()].type.equals(Piece.PIECE_KING)){
			flag=true;
		}
		ref.gamecontext[x.get()][y.get()].delete();
		ref.gamecontext[x.get()][y.get()]=actor;
		ref.gamecontext[actor.xpos.get()][actor.ypos.get()]=null;
		actor.xpos.set(x.get());
		actor.ypos.set(y.get());
		
		return flag;
	}
	@Override
	public void draw(GraphicsContext cx) {
		cx.setFill(Color.RED);
		double step1 = width.get()/8;
		double step2 = height.get()/8;
		cx.fillOval(x.get()*step1,y.get()*step2, step1, step2);
		cx.setStroke(Color.ROSYBROWN);
		cx.strokeOval(x.get()*step1, y.get()*step2, step1, step2);
		cx.setStroke(Color.BLACK);
	}

}
