package application.gfx.comp.actions;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import application.ChessApplication;
import application.gfx.comp.Drawable;
import application.gfx.comp.piece.Pawn;
import application.gfx.comp.piece.Piece;

public class PossibleMovement implements Drawable{
	
	private SimpleDoubleProperty width,height;
	private SimpleDoubleProperty x,y;
	
	public Piece actor;
	
	public PossibleMovement(ChessApplication chess,int x1,int y1,Piece theactor){
		x= new SimpleDoubleProperty();
		y= new SimpleDoubleProperty();
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
	public void replace(){
		actor.setPosition((int)x.get(), (int)y.get());
		if(actor.getClass().equals(Pawn.class)){
			Pawn pa=(Pawn)actor;
			pa.moved=+1;
		}
	}
	@Override
	public void draw(GraphicsContext cx) {
		cx.setFill(Color.BLUEVIOLET);
		double step1 = width.get()/8;
		double step2 = height.get()/8;
		cx.fillOval(x.get()*step1,y.get()*step2, step1, step2);
		cx.setStroke(Color.GREEN);
		cx.strokeOval(x.get()*step1, y.get()*step2, step1, step2);
		cx.setStroke(Color.BLACK);
		cx.drawImage(actor.im, x.get()*step1, y.get()*step2,step1,step2);
	}
	
}