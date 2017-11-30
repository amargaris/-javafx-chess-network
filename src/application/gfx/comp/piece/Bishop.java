package application.gfx.comp.piece;

import java.util.ArrayList;

import application.ChessApplication;
import application.gfx.comp.Drawable;

public class Bishop extends Piece {
		
	    static final int[] dimx={1,1,-1,-1};
		static final int[] dimy={-1,1,1,-1};
		
	public Bishop(ChessApplication abax, PieceColor col) {
		super(abax, col, Piece.PIECE_BISHOP);
	}
	@Override
	public ArrayList<Drawable> getValidMoveLocations() {
		ArrayList<Drawable> results=new ArrayList<Drawable>();
		int x=xpos.get();
		int y=ypos.get();
		
		ArrayList<int[]> temp=new ArrayList<int[]>();
		for(int i=0;i<dimx.length;i++){
			int tempx=x+dimx[i];
			int tempy=y+dimy[i];
			while(tempx >= 0 && tempx <8 && tempy >=0 && tempy < 8){
				boolean flag=false;
				if(ref.gamecontext[tempx][tempy]==null){
				
				} else if(!ref.gamecontext[tempx][tempy].thecol.equals(thecol)){
					flag=true;
				} else{
					break;
				}
				temp.add(new int[]{tempx,tempy});
				tempx+=dimx[i];
				tempy+=dimy[i];
				if(flag){
					break;
				}
			}
		}
		for(int[] arr:temp){
			testMovement(arr[0]-x, arr[1]-y, this, results);
		}
		return results;
	}
	
}