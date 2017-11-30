package application.gfx.comp.piece;

import java.util.ArrayList;

import application.ChessApplication;
import application.gfx.comp.Drawable;

public class King extends Piece {
		static final int[] x={1,1,1,0,0,0,-1,-1,-1};
		static final int[] y={-1,0,1,-1,1,0,1,0,-1};
	public King(ChessApplication abax, PieceColor col) {
		super(abax, col,Piece.PIECE_KING);
	}

	@Override
	public ArrayList<Drawable> getValidMoveLocations() {
		// TODO Auto-generated method stub
		ArrayList<Drawable> results= new ArrayList<Drawable>();
		
		for(int i=0;i<x.length;i++){
			testMovement(x[i], y[i], this, results);
		}
		return results;
	}
	
	
}