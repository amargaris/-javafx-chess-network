package application.gfx.comp.piece;

import java.util.ArrayList;

import application.ChessApplication;
import application.gfx.comp.Drawable;
import application.gfx.comp.actions.PossibleAttack;
import application.gfx.comp.actions.PossibleMovement;
import javafx.scene.canvas.GraphicsContext;

public class Pawn extends Piece {
	public int moved=0;
	public Pawn(ChessApplication abax, PieceColor col) {
		super(abax, col, Piece.PIECE_PAWN);
	}
	@Override
	public ArrayList<Drawable> getValidMoveLocations(){
		//TODO en passe kai elegxos gia egkyres theseis
		ArrayList<Drawable> results = new ArrayList<Drawable>();
		int dimensionality;
		int[] attackDim = new int[]{1,-1}; //y axis
		if(thecol==PieceColor.white){
			dimensionality = -1;//white goes <----
		}else{
			dimensionality = +1;
		}
		try{
			if(ref.gamecontext[xpos.get()+dimensionality][ypos.get()]==null){
				PossibleMovement tar = new PossibleMovement(ref,xpos.get()+dimensionality,ypos.get(),this);
				results.add(tar);
			}
		}catch(Exception e){
			
		}
		try{
			if(moved==0){
				if(ref.gamecontext[xpos.get()+2*dimensionality][ypos.get()]==null){
					PossibleMovement tar2 = new PossibleMovement(ref,xpos.get()+2*dimensionality,ypos.get(),this);
					results.add(tar2);
				}
			}
		}catch(Exception e){
			
		}
		for(int atdim:attackDim){
			try{
				if(ref.gamecontext[xpos.get()+dimensionality][ypos.get()+atdim]!=null){
					if(!ref.gamecontext[xpos.get()+dimensionality][ypos.get()+atdim].thecol.equals(thecol)){
						PossibleAttack at = new PossibleAttack(ref,xpos.get()+dimensionality,ypos.get()+atdim,this);
						results.add(at);
					}
				}
			}catch(Exception e){
				
			}
			try{
				int dimx = 0;
				int dimy = atdim;
				if(ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy]!=null){
					if(!ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy].thecol.equals(thecol)){
						if(ref.gamecontext[xpos.get()+dimensionality][ypos.get()+dimy]==null){
							PossibleMovement pm = new PossibleMovement(this.ref,this.xpos.get()+dimensionality,this.ypos.get()+dimy,this){
								/*@Override
								public void replace() {
									// TODO Auto-generated method stub
									super.replace();
								}*/
							};
							PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get(),this.ypos.get()+dimy,this){
								public boolean attack(){
									boolean flag = super.attack();
									pm.replace();
									return flag;
								}
								public void draw(GraphicsContext cx){
									super.draw(cx);
									pm.draw(cx);
								}
							};
							results.add(at);
						}
					}
				}
			}catch(Exception e){
			
			}
		}
			
			/*try{
				if(ref.gamecontext[xpos.get()+dimensionality][ypos.get()+1]!=null){
					if(!ref.gamecontext[xpos.get()+dimensionality][ypos.get()+1].thecol.equals(thecol)){
						
						PossibleAttack at = new PossibleAttack(ref,xpos.get()+dimensionality,ypos.get()+1,this);
						results.add(at);
					}
				}
			}catch(Exception e){
				
			}*/
			
			/*try{
				int dimx = 0;
				int dimy = +1;
				if(ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy]!=null){
					if(!ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy].thecol.equals(thecol)){
						if(ref.gamecontext[xpos.get()-1][ypos.get()+dimy]==null){
							PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get()-1,this.ypos.get()+dimy,this);
							results.add(at);
							PossibleMovement pm = new PossibleMovement(this.ref,this.xpos.get()-1,this.ypos.get()+dimy,this);
							PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get(),this.ypos.get()+dimy,this){
								public boolean attack(){
									boolean flag = super.attack();
									pm.replace();
									return flag;
								}
							};
							results.add(at);
						}
					}
				}
			}catch(Exception e){
			
			}*/
		/*}else if(thecol==PieceColor.black){
			try{
				if(ref.gamecontext[xpos.get()+1][ypos.get()+1]!=null){
					if(!ref.gamecontext[xpos.get()+1][ypos.get()+1].thecol.equals(thecol)){
						PossibleAttack at = new PossibleAttack(ref,xpos.get()+1,ypos.get()+1,this);
						results.add(at);
					}
				}
			}catch(Exception e){
				
			}
			try{
				if(ref.gamecontext[xpos.get()+1][ypos.get()-1]!=null){
					if(!ref.gamecontext[xpos.get()+1][ypos.get()-1].thecol.equals(thecol)){
						PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get()+1,this.ypos.get()-1,this);
						results.add(at);
					}
				}
			}catch(Exception e){
				
			}
			try{
				int dimx = 0;
				int dimy = -1;
				if(ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy]!=null){
					if(!ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy].thecol.equals(thecol)){
						if(ref.gamecontext[xpos.get()+1][ypos.get()+dimy]==null){
							PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get()+1,this.ypos.get()+dimy,this);
							results.add(at);
						}
					}
				}
			}catch(Exception e){
			
			}
			try{
				int dimx = 0;
				int dimy = +1;
				if(ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy]!=null){
					if(!ref.gamecontext[xpos.get()+dimx][ypos.get()+dimy].thecol.equals(thecol)){
						if(ref.gamecontext[xpos.get()+1][ypos.get()+dimy]==null){
							PossibleAttack at = new PossibleAttack(this.ref,this.xpos.get()+1,this.ypos.get()+dimy,this);
							results.add(at);
						}
					}
				}
			}catch(Exception e){
			
			}
			try{
				if(ref.gamecontext[xpos.get()+1][ypos.get()]==null){
					PossibleMovement tar = new PossibleMovement(this.ref,this.xpos.get()+1,this.ypos.get(),this);
					results.add(tar);
				}
			}catch(Exception e){
				
			}
			if(moved==0){
				try{
					if(ref.gamecontext[xpos.get()+2][ypos.get()]==null){
						PossibleMovement tar2 = new PossibleMovement(this.ref,this.xpos.get()+2,this.ypos.get(),this);
						results.add(tar2);
					}
				}catch(Exception e){
					
				}
				//firsttime=!firsttime;
			}

		}else{
			System.out.println(thecol);//"in else");
		}*/
		return results;
	}
	
}