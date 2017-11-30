package application.gfx.comp;


import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import application.ChessApplication;

public class ChessBoard implements Drawable{
	
	private static final double PAD_PERCENTAGE=0.0;
	private SimpleDoubleProperty width,height;
	private SimpleDoubleProperty xstart,ystart;
	
	public ChessBoard(ChessApplication abax){
		xstart = new SimpleDoubleProperty();
		ystart = new SimpleDoubleProperty();
		width  = new SimpleDoubleProperty();
		height = new SimpleDoubleProperty();
		xstart.bind(abax.canvas.widthProperty().multiply(PAD_PERCENTAGE));
		ystart.bind(abax.canvas.heightProperty().multiply(PAD_PERCENTAGE));
		width.bind(abax.canvas.widthProperty().multiply(1-2*PAD_PERCENTAGE));//.subtract(abax.canvas.widthProperty().multiply(1-2*PAD_PERCENTAGE)));
		height.bind(abax.canvas.heightProperty().multiply(1-2*PAD_PERCENTAGE));
	}

	@Override
	public void draw(GraphicsContext cx) {
		cx.clearRect(0, 0, cx.getCanvas().getWidth(), cx.getCanvas().getHeight());
		cx.setFill(Color.BLACK);
		double step1 = width.get()/8;
		double step2 = height.get()/8;
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				if(i%2==j%2){
					cx.setFill(Color.rgb(124, 117, 117));//Color.BLACK);
				}else{
					cx.setFill(Color.WHITE);
				}
				cx.fillRect(xstart.get()+i*step1,ystart.get()+j*step2,step1,step2);
			}
		}
		cx.setFill(Color.BLACK);
		cx.setFont(new Font("Times New Roman",20));
		for(int i=0;i<8;i++){
			cx.fillText(""+(char)((int)'a'+(int)i),xstart.get()-step1/2, ystart.get()+i*step2+step2/2);
		}
		for(int i=0;i<8;i++){
			cx.fillText(""+(i+1), xstart.get()+i*step1+step1/2-5, ystart.get()-5);
		}
		cx.setFill(Color.rgb(124, 117, 117));
		cx.strokeRect(xstart.get()+0,ystart.get()+0, width.get(), height.get());
	}
	public int[] getXY(MouseEvent event){
		double x=event.getX();
		double y=event.getY();
		double size1=width.get()/8;
		double size2=height.get()/8;
		int x_norm = (int)((x-xstart.get())/size1);
		int y_norm = (int)((y-ystart.get())/size2);
		if(x_norm>=0&&x_norm<=7&&y_norm>=0&&y_norm<=7){
			int[] ret ={x_norm,y_norm};
			return ret;
		}
		else{
			return null;
		}
	}
}