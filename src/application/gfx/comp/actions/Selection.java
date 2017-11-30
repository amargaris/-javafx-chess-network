package application.gfx.comp.actions;

import application.gfx.comp.Drawable;
import application.gfx.comp.piece.Piece;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
public class Selection implements Drawable{
	
	
	private SimpleDoubleProperty x,y;
	private SimpleDoubleProperty width,height;
	
	public Selection(Piece pis){
		x= new SimpleDoubleProperty();
		y= new SimpleDoubleProperty();
		width = new SimpleDoubleProperty();
		width.bind(pis.width);
		height = new SimpleDoubleProperty();
		height.bind(pis.height);
		x.bind(pis.xpos);
		y.bind(pis.ypos);
	}
	@Override
	public void draw(GraphicsContext cx) {
		cx.setFill(Color.GREENYELLOW);
		double step1 = width.get()/8;
		double step2 = height.get()/8;
		cx.fillOval(x.get()*step1,y.get()*step2, step1, step2);
		cx.setStroke(Color.GREEN);
		cx.strokeOval(x.get()*step1, y.get()*step2, step1, step2);
		cx.setStroke(Color.BLACK);
	}
	
}